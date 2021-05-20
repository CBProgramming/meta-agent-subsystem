package metaagentsubsystem;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import static metaagentsubsystem.MetaAgent.poolSize;

/**
 * Class Router allows for creation of Router object extending MetaAgent
 * @author Carter Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class Router extends MetaAgent
{   
    //Instance Variables
    private static Router instance = null;
    private TreeMap<String, MetaAgent> routingTable;
    private TreeMap<String, Connection> connections;
    ReentrantLock lock = new ReentrantLock();
    ServerSocket serverSocket;
    private int socketNumber;    
    private String ipAdress;
    private int port;    
    private RouterMonitor monitor;
    
    /**
     * Constructor for Router
     * @param handle name given to the Router
     */
    private Router(String handle)
    {
        super(handle);
        lock = new ReentrantLock();
        type = MetaAgentType.ROUTER;
        socketNumber = 0;
        routingTable = new TreeMap<>();
        routingTable.put(handle, this);
        connections = new TreeMap<>();
    }
    
    /**
     * The method will return the instance of the Router
     * @return the router
     */
    public static Router getInstance()
    {
        return instance;
    }
    
    /**
     * Method that creates a new instance of router 
     * @param handle name of the router
     * @return instance of the router
     */
    public static Router createNew(String handle)
    {
        if(instance != null)
        {
            return null;
        }
        instance = new Router(handle);
        return instance;
    }
    
    /**
     * Method is used to get the routing table from the router object
     * @return the routing table
     */
    public TreeMap<String, MetaAgent> getRoutingTable() {
        return routingTable;
    }
    
    /**
     * Advertise on the given port
     * @param port is the port on which the router operates
     * @throws UnknownHostException throws error when needed
     * @throws IOException throws exception when needed
     */
    public void advertiseConnection(int port) throws UnknownHostException, IOException
    {
        if(serverSocket == null)
        {
            //InetAddress localHost = InetAddress.getLocalHost();
            ipAdress = InetAddress.getLocalHost().getHostAddress().trim();
            InetAddress bindAddress = InetAddress.getByName(ipAdress);
            this.port = port;
            serverSocket = new ServerSocket(port, 0, bindAddress);
            acceptThread();
        }
    }

    /**
     * This method gets the port
     * @return port number
     */
    public int getPort() {
        return port;
    }
    
    /**
     * Class msgHandler is needed to handle the user/system messages.
     * Messages can be handled in several ways depending on type
     * @param md is the message data and all associated data
     * @throws InterruptedException in event of an error
     * @throws IOException in the event of an exception
     */
    protected void msgHandler(MsgData md) throws InterruptedException, IOException
    {
        lock.lock();
        //System.out.println(md.toString());
        if(md.getType() == MessageType.USER)
        {
            System.out.println("sending to " + md.getRecipient() + " and am currently at " + getHandle());
            if(!routingTable.containsKey(md.getRecipient()))
            {
                System.out.println("Recipient not found in " + getHandle());
                return;
            }
            if(routingTable.get(md.getRecipient()).getType() == MetaAgentType.SOCKET && monitor != null)
            {
                monitor.messageRecieved(md.getHandle(), md.getSender(), md.getRecipient(), md.getBody(), LocalDateTime.now());
            }
            routingTable.get(md.getRecipient()).put(md);
        }
        else if(md.getType() == MessageType.ADD && !routingTable.containsKey(md.getSender()))
        {
            routingTable.put(md.getSender(), routingTable.get(md.getRecipient()));
     
            for(Map.Entry mapElement : routingTable.entrySet())
            {
                String key = (String)mapElement.getKey();
                MetaAgent value = (MetaAgent)mapElement.getValue();
                if(key == value.getHandle() && key != md.getRecipient())
                {
                    try {
                        value.put(new MsgData(md.getSender(), getHandle(), MessageType.ADD));
                    } catch (InterruptedException ex) {
                        System.out.println("Interrupted!");
                    }
                }
            }
        }
        else if(md.getType() == MessageType.REMOVE && routingTable.containsKey(md.getSender()))
        {
            for(Map.Entry mapElement : routingTable.entrySet())
            {
                String key = (String)mapElement.getKey();
                MetaAgent value = (MetaAgent)mapElement.getValue();
                if(key == value.getHandle() && key != md.getRecipient())
                {
                    try {
                        value.put(new MsgData(md.getSender(), getHandle(), MessageType.REMOVE));
                    } catch (InterruptedException ex) {
                        System.out.println("Interrupted!");
                    }
                }
            }
            routingTable.remove(md.getSender());
        }
        lock.unlock();
    }
    
    /**
     * Method newConnection is used to connect to an advertising router
     * @param remoteIp is the address needed for the connection 
     * @param remotePort is the port which the connection will be made with
     */
    public void newConnection(String remoteIp, int remotePort) 
    {
        lock.lock();
        SocketAgent newSocket = new SocketAgent("s" + socketNumber++, this, remoteIp, remotePort);
        newSocket.newConnection();
        lock.unlock();
    }
    
    /**
     * Method newReturnConnection is used to connect back to the router in response to the initial connection 
     * @param remoteIp is the address needed for the connection 
     * @param remotePort is the port which the connection will be made with
     * @param socketName name of the socket
     */
    private void newReturningConnection(String remoteIp, int remotePort, String socketName) 
    {
        lock.lock();
        SocketAgent newSocket = new SocketAgent(socketName, this, remoteIp, remotePort, true);
        newSocket.newConnection();
        lock.unlock();
    }
    
    
    /**
     * This method checks all the current connections for incoming messages and handles the messages.
     * Messages are handled according to their type
     */
    private void receiveThread()
    {
        pool.execute(() -> {
            while(true)
            {
                for (Map.Entry mapElement : connections.entrySet()) 
                {
                    Connection current = (Connection)mapElement.getValue();
                    try
                    {
                        if(current.hasMessageInQueue())
                        {
                            System.out.println("New message received over the network");
                            MsgData newMessage = current.receiveMessage();
                            if(newMessage.getType() == MessageType.USER)
                            {
                                Router.this.put(newMessage);
                                if(monitor != null)
                                {
                                    monitor.messageSent(newMessage.getHandle(), newMessage.getSender(), newMessage.getRecipient(), newMessage.getBody(), LocalDateTime.now());
                                }                                
                                                            }
                            else if(newMessage.getType() == MessageType.ADD)
                            {
                                Router.this.put(newMessage);
                                MetaAgentType type = getTypeFromTable(newMessage.getSender());
                                if(monitor != null && type != MetaAgentType.USER && type != MetaAgentType.PORTAL  && type != MetaAgentType.ROUTER)
                                {
                                    monitor.agentAdded(newMessage.getRecipient(), newMessage.getSender(), routingTable.get(getHandle()).getType(),LocalDateTime.now());
                                }      
                            }
                            else if(newMessage.getType() == MessageType.REMOVE)
                            {
                                Router.this.put(newMessage);
                                if(monitor != null && getTypeFromTable(newMessage.getSender()) == MetaAgentType.SOCKET || getTypeFromTable(newMessage.getSender()) == MetaAgentType.PORTAL)
                                {
                                    monitor.agentRemoved(newMessage.getRecipient(), newMessage.getSender(), routingTable.get(getHandle()).getType(),LocalDateTime.now());
                                }     
                            }
                        }
                    }
                    catch(IOException ex)
                    {
                        
                    }
                    catch(InterruptedException e)
                    {
                        
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(Router.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }
    
    /**
     * This method deals with the incoming connections.
     * A connection once established will send a response to the corresponding router.
     * A three way handshake is performed.
     */
    private void acceptThread()
    {
        pool.execute(() -> {
            //accept connections code
            while(true)
            {
                //send all of its nodes through a "system" message 
                try
                {
                    Socket newConnectionSocket = serverSocket.accept();
                    Connection newConnection = new Connection(newConnectionSocket);
                    MsgData sysMsg = newConnection.receiveMessage();
                    System.out.println("Connection request received");
                    if(sysMsg.getType() == MessageType.HANDLE)
                    {
                        String newConnectionHandle = sysMsg.getHandle(); 
                        if(newConnectionHandle != null)
                        {
                                if(!connections.containsKey(newConnectionHandle))
                                {
                                    boolean alreadyConnected = true;
                                    System.out.println(sysMsg.getAgentsList().get(0));
                                    for(String agentHandle : sysMsg.getAgentsList())
                                    {
                                        if(!routingTable.containsKey(agentHandle))
                                        {
                                            System.out.println("Not connected, connecting");
                                            alreadyConnected = false;
                                            break;
                                        }    
                                    }
                                    if(!alreadyConnected)
                                    {
                                        newConnection.setHandle(newConnectionHandle);
                                        connections.put(newConnectionHandle, newConnection);
                                        
                                        //MONITOR NEW CONNECTION receiving
                                        if (monitor != null)
                                        {
                                            monitor.newConnection(getHandle(), newConnectionHandle, LocalDateTime.now());
                                        }
                                        MsgData listMessage = new MsgData(getHandle(), MessageType.LIST);
                                        listMessage.setAgentsList(getPortalHandles());
                                        newConnection.sendMessage(listMessage);
                                        newReturningConnection(sysMsg.getSender(), sysMsg.getId(), sysMsg.getHandle());
                                        if(connections.size() == 1)
                                            receiveThread();
                                        continue;
                                    }
                                }
                                else
                                {
                                    System.out.println("Already connected");
                                }
                        }
                        else
                        {
                            System.out.println("Invalid handle, aborting");
                        }
                    }
                    else if(sysMsg.getType() == MessageType.RETURN)
                    {
                        newConnection.setHandle(sysMsg.getHandle());
                        connections.put(newConnection.getHandle(), newConnection);
                        if (monitor != null)
                        {
                            monitor.newConnection(getHandle(), newConnection.getHandle(), LocalDateTime.now());
                        }
                        
                        MsgData listMessage = new MsgData(getHandle(), MessageType.LIST);
                        listMessage.setAgentsList(getPortalHandles());
                        newConnection.sendMessage(listMessage);
                    }
                    else
                    {
                        System.out.println("Invalid request message, dropping connection");
                        return;
                    }
                }
                catch(IOException e)
                {
                    System.out.println("Timed out in accepting");
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Router.class.getName()).log(Level.SEVERE, null, ex);
                }
                if(connections.size() == 1)
                    receiveThread();
            }
        });
    }
    
    /**
     * This method returns a list of all the metaAgent handles in the routing table 
     * @return list of names
     */
    public synchronized List<String> getPortalHandles()
    {
        List<String> handles = new ArrayList<>();
        handles.addAll(routingTable.keySet());
        return handles;
    }
    
    /**
     * This method adds an agent to the system.
     * If the agent is already in the system, the add operation is aborted
     * @param agent is the object that will be added to the router
     * @return true if the agent addition is successful, false if not
     */
    public synchronized boolean addAgent(MetaAgent agent)
    {
        lock.lock();
        if(routingTable.containsKey(agent.getHandle()))
        {
            System.out.println("Agent already exists");
            lock.unlock();
            return false;
        }
        if(agent.getType() != MetaAgentType.PORTAL && agent.getType() != MetaAgentType.SOCKET)
        {
            System.out.println("Only portals and sockets are allowed to add on a router");
            lock.unlock();
            return false;
        }
        routingTable.put(agent.getHandle(), agent);
        if (monitor != null)
            {
                monitor.agentAdded(this.getHandle(), agent.getHandle(),agent.getType(),LocalDateTime.now());
            }
        agent.getRoutingTable().put(getHandle(), this);
        for(Map.Entry mapElement : routingTable.entrySet())
        {
            String key = (String)mapElement.getKey();
            MetaAgent value = (MetaAgent)mapElement.getValue();
            try {
                agent.put(new MsgData(key, getHandle(), MessageType.ADD));
            } catch (InterruptedException ex) {
                Logger.getLogger(Router.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(key == value.getHandle())
            {
                try {
                    value.put(new MsgData(agent.getHandle(), getHandle(), MessageType.ADD));
                } catch (InterruptedException ex) {
                    System.out.println("Interrupted!");
                }
            }
        }
        lock.unlock();
        return true;
    }
    
    /**
     * Remove agent allows an agent to be removed from the system.
     * If the user is not present in the system, the operation is aborted
     * @param handle the name of the agent to be removed
     */
    public void removeAgent(String handle)
    {
        if(routingTable.containsKey(handle))
        {
            if(routingTable.get(handle).getHandle() == handle && routingTable.get(handle).getType() == MetaAgentType.ROUTER)
            {
                System.out.println("Router removal is disallowed...");
                return;
            }
            if(routingTable.get(handle).getType() == MetaAgentType.PORTAL && routingTable.get(handle).getHandle() == handle)
            {
                Map<String, MetaAgent> copyMap = new ConcurrentHashMap<>(routingTable.get(handle).getRoutingTable());
                for(Map.Entry mapElement : copyMap.entrySet())
                {
                    String key = (String)mapElement.getKey();
                    MetaAgent value = (MetaAgent)mapElement.getValue();
                    if(key == value.getHandle() && value.getType() == MetaAgentType.USER)
                    {
                        try {
                            if (monitor != null)
                            {
                               monitor.agentRemoved(this.getHandle(), value.getHandle(),value.getType(),LocalDateTime.now());
                            }
                            routingTable.get(handle).put(new MsgData(key, routingTable.get(handle).getHandle(), MessageType.REMOVE));
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Router.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
            Map<String, MetaAgent> copyMap = new ConcurrentHashMap<>(routingTable);
            MetaAgentType handleType = getTypeFromTable(handle);
            routingTable.remove(handle);
            if (monitor != null)
            {
               monitor.agentRemoved(this.getHandle(), handle,handleType,LocalDateTime.now());
            }
            for(Map.Entry mapElement : copyMap.entrySet())
            {
                String key = (String)mapElement.getKey();
                MetaAgent value = (MetaAgent)mapElement.getValue();
                if(key == value.getHandle())
                {
                    try {
                        value.put(new MsgData(handle, this.getHandle(), MessageType.REMOVE));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Portal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    /**
     * Method to return the IP Address of the router
     * @return string IP Address
     */
    public String getIpAdress() 
    {
        return ipAdress;
    }
    
    /**
     * Method to set the monitor 
     * @param rm is the objects router monitor
     */
    public void setMonitor(RouterMonitor rm)
    {
        monitor = rm;
    }

    /**
     * Method returns the objects router monitor
     * @return the routers monitor
     */
    public RouterMonitor getMonitor() 
    {
        return monitor;
    }
    
    /**
     * This method enables retrieval of the agent type from the routing table
     * @param handle is the name of the agent
     * @return 
     */
    public MetaAgentType getTypeFromTable(String handle)
    {
        try
            {
            if (routingTable.get(handle).getType() == this.getType())
            {
                return MetaAgentType.ROUTER;
            }
            else if (routingTable.get(handle).getType() == MetaAgentType.SOCKET)
            {
                return MetaAgentType.SOCKET;
            }
            else if ((routingTable.get(handle).getRoutingTable().get(handle).getType() ==  routingTable.get(handle).getType()))
            {
                return MetaAgentType.PORTAL;
            }
            else
            {
                return MetaAgentType.USER;
            }            
        }
        catch(Exception e)
        {

        }
        return null;
    }
}
