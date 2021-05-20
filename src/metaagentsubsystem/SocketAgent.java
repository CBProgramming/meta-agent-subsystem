/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metaagentsubsystem;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.TreeMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import static metaagentsubsystem.MetaAgent.poolSize;

/**
 * Class SocketAgent is used for connecting to routers and sending messages to them
 * @author Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class SocketAgent extends MetaAgent{
    
    //Instance Variables
    private Connection connection; 
    private Router router;
    private String remoteIp;
    private int remotePort;
    private Object lock = new Object();
    private boolean returning;
    
    /**
     * Constructor for a SocketAgent which will be used for setting up connections
     * @param handle name 
     * @param router router assigned too the socket
     * @param remoteIp IP Address assigned to the socket 
     * @param remotePort is the port of assigned 
     */
    public SocketAgent(String handle, Router router, String remoteIp, int remotePort) 
    {
        super(handle);
        type = MetaAgentType.SOCKET;
        this.router = router;
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
    }

    /**
     * Constructor for a SocketAgent for setting up a connection on a returning socket
     * @param handle name of the socket
     * @param router router assigned too the socket
     * @param remoteIp IP Address assigned to the socket
     * @param remotePort is the port of assigned 
     * @param returning boolean to show if returning socket is successful
     */
    public SocketAgent(String handle, Router router, String remoteIp, int remotePort, boolean returning) 
    {
        super(handle);
        type = MetaAgentType.SOCKET;
        this.router = router;
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        this.returning = returning;
    }
    
    /**
     * This method establishes a new connection between routers.
     * It uses a three way handshake.
     */
    public void newConnection() 
    {
        final ExecutorService pool = Executors.newFixedThreadPool(poolSize);
        pool.execute(new Runnable()
        {
            public void run() {
                InetAddress bindAddress;
                try
                {
                    bindAddress = InetAddress.getByName(remoteIp);
                    Socket socket = new Socket(bindAddress, remotePort);
                    Connection newConnection = new Connection("connection", socket);
                    MsgData helloMessage = null;
                    if(returning)
                    {
                        helloMessage = new MsgData(router.getHandle(), MessageType.RETURN);
                    }
                    else
                    {
                        helloMessage = new MsgData(router.getHandle(), MessageType.HANDLE);
                    } 
                    helloMessage.setSender(router.getIpAdress());
                    helloMessage.setId(router.getPort());
                    helloMessage.setAgentsList(router.getPortalHandles());
                    newConnection.sendMessage(helloMessage);
                    int timeout = 0; //TO-DO: implement seconds timer instead
                    while(!newConnection.hasMessageInQueue())
                    {
                        timeout++;
                        if(timeout > 10000000)
                        {
                            System.out.println("Timed out");
                            socket.close();
                            return;
                        }
                    }
                    MsgData sysMsg = newConnection.receiveMessage();
                    System.out.println(sysMsg.getHandle());
                    if(sysMsg.getType() == MessageType.LIST)
                    {
                        System.out.println("MESSAGE RECEIVED BACK in " + getHandle());
                        newConnection.setHandle(sysMsg.getHandle());
                        SocketAgent.this.setHandle(sysMsg.getHandle());
                        if(connection == null)
                        {
                            router.getRoutingTable().put(getHandle(), SocketAgent.this);
                            for(String agentHandle : sysMsg.getAgentsList())
                            {                        
                                router.put(new MsgData(agentHandle, getHandle(), MessageType.ADD));
                                //router.msgHandler(new MsgData(agentHandle, handle, MessageType.ADD));
                            }
                            connection = newConnection;
                            System.out.println(connection.getHandle());
                        }
                        else
                        {
                            System.out.println("Already Connected!");
                        }
                    }
                    else
                    {
                        System.out.println("Incorrect message received, dropping...");
                    }
                }
                catch(UnknownHostException e)
                {
                    
                }
                catch(IOException io)
                {
                    
                } 
                catch (ClassNotFoundException ex) 
                {
                    
                } catch (InterruptedException ex) {
                    Logger.getLogger(SocketAgent.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
                    
        }
        );
    }
    
    /**
     * Method message handles the message by sending it over the network.
     * @param md is the message data
     * @throws InterruptedException throws Interrupt exception in event of an error
     * @throws IOException throws exception 
     */
    @Override
    protected void msgHandler(MsgData md) throws InterruptedException, IOException
    {
        System.out.println("sending to " + md.getRecipient() + " and am currently at socket " + getHandle());
        connection.sendMessage(md);
    }
}
