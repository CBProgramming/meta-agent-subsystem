package metaagentsubsystem;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class portal allows a object of portal to be created and this is a necessary component in the metaAgent system
 * @author Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class Portal extends MetaAgent
{
    //Instance Variables
    private PortalMonitor monitor;   
    private TreeMap<String, MetaAgent> routingTable;
    private ReentrantLock lock = new ReentrantLock();
    
    /**
     * Constructor for the portal 
     * @param name needed to name the portal
     * @param router is the object of router associated with the portal object
     * Action is also taken to add the name to the portal routing table
     * Then the portal to the object of router
     */
    public Portal(String name, Router router)
    {
        super(name);
        type = MetaAgentType.PORTAL;
        routingTable = new TreeMap<>();
        routingTable.put(getHandle(), this);
        router.addAgent(this);
    }
    
    /**
     * Class msgHandler is needed to handle the user/system messages in the system.
     * Messages are handled differently based on the type
     * @param md is the message data and all associated data
     * @throws InterruptedException in event of an error
     * @throws IOException in the event of an error
     */
    @Override
    protected void msgHandler(MsgData md) throws InterruptedException, IOException
    {
        lock.lock();
        if(md == null)
        {
            System.out.println("Null message");
        }
        else if(md.getType() == null)
        {
            System.out.println("Invalid type");
        }
        else if(md.getType() == MessageType.USER && md.getRecipient() != null & !md.getRecipient().equals(getHandle()))
        {
            if(!routingTable.containsKey(md.getRecipient()))
            {
                System.out.println("Recipient not found in " + getHandle());
                return;
            }
            try 
            {
                System.out.println("sending to " + md.getRecipient() + " and am currently at " + getHandle());
                routingTable.get(md.getRecipient()).put(md); 
            } 
            catch (InterruptedException ex) 
            {
                System.out.println("Adding" + md.getId() + "to queue was interrupted.");
            }
        }
        else if(md.getType() == MessageType.ADD && !routingTable.containsKey(md.getSender()))
        {
            ConcurrentHashMap<String, MetaAgent> hm = new ConcurrentHashMap(routingTable);
            routingTable.put(md.getSender(), routingTable.get(md.getRecipient()));
            for(Map.Entry mapElement : hm.entrySet())
            {
                String key = (String)mapElement.getKey();
                MetaAgent value = (MetaAgent)mapElement.getValue();
                if(key == value.getHandle() && key != md.getRecipient() && value.getType() != MetaAgentType.USER)
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
            if(md.getSender().equals(getHandle()))
            {         
                removeAllUsers();
            }
            else if(routingTable.get(md.getSender()).getHandle().equals(md.getSender()) 
                    && routingTable.get(md.getSender()).getType().equals(MetaAgentType.USER))
            {
                routingTable.get(md.getSender()).removeConnections();
            }
            ConcurrentHashMap<String, MetaAgent> hm = new ConcurrentHashMap(routingTable);
            for(Map.Entry mapElement : hm.entrySet())
            {
                String key = (String)mapElement.getKey();
                MetaAgent value = (MetaAgent)mapElement.getValue();
                if(key == value.getHandle() && key != md.getRecipient() && value.getType() != MetaAgentType.USER)
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
     * Adds an agent to the system.
     * If the agent already exists in the routing table the method is aborted.
     * @param agent is needed to be able to add to the portal routing table
     * @return true if the agent add has been successfully initiated
     * @return false if the agent add has been unsuccessful
     */
    public boolean addAgent(MetaAgent agent)
    {
        lock.lock();
        if(routingTable.containsKey(agent.getHandle()))
        {
            System.out.println("Agent already exists");
            lock.unlock();
            return false;
        }
        if (agent.getType() == type)
        {
            System.out.println("Cannot add a portal to a portal");
            lock.unlock();
            return false;
        }
        routingTable.put(agent.getHandle(), agent);
        if (monitor != null)
        {
            monitor.agentAdded(getHandle(), agent.getHandle(),agent.type,LocalDateTime.now());
        }
        ConcurrentHashMap<String, MetaAgent> copyMap = new ConcurrentHashMap(routingTable);
        for(Map.Entry mapElement : copyMap.entrySet())
        {
            String key = (String)mapElement.getKey();
            MetaAgent value = (MetaAgent)mapElement.getValue();
            if(key == value.getHandle() && value.getType() == MetaAgentType.ROUTER)
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
     * The method is used to remove an agent from the system.
     * This method checks to see if the agent exists before adding to the portal routing table.
     * The method also leads into the removal of the message from portal to the router 
     * @param handle is needed to be identify the agent to be removed
     */
    public void removeAgent(String handle)
    {
        lock.lock();
        if(routingTable.containsKey(handle))
        {
            if(routingTable.get(handle).getType() == MetaAgentType.ROUTER && routingTable.get(handle).getHandle().equals(handle))
            {
                System.out.println("Router removal is disallowed...");
                lock.unlock();
                return;
            }
            if(routingTable.get(handle).getHandle().equals(handle) && routingTable.get(handle).getType() == MetaAgentType.USER)
            {
                routingTable.get(handle).removeConnections();
            }
            if(this.getHandle().equals(handle))
            {
                removeAllUsers();
            }
            Map<String, MetaAgent> copyMap = new ConcurrentHashMap<>(routingTable);
            MetaAgentType type = getTypeFromTable(handle);
            routingTable.remove(handle);
            if (monitor != null)
            {
                monitor.agentRemoved(this.getHandle(), handle, type, LocalDateTime.now());
            }
            for(Map.Entry mapElement : copyMap.entrySet())
            {
                String key = (String)mapElement.getKey();
                MetaAgent value = (MetaAgent)mapElement.getValue();
                if(key == value.getHandle() && value.getType() == MetaAgentType.ROUTER)
                {
                    try {
                        value.put(new MsgData(handle, this.getHandle(), MessageType.REMOVE));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Portal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        lock.unlock();
    }
    
    /**
     * This method allows removal for all users from the portal
     * Searches the routing table and removes users until empty
     */
    public void removeAllUsers()
    {
        Map<String, MetaAgent> copyMap = new ConcurrentHashMap<>(routingTable);
        for(Map.Entry mapElement : copyMap.entrySet())
        {
            String key = (String)mapElement.getKey();
            MetaAgent value = (MetaAgent)mapElement.getValue();
            if(key == value.getHandle() && value.getType() == MetaAgentType.USER)
            {
                try {
                    this.put(new MsgData(key, getHandle(), MessageType.REMOVE));
                    if (monitor != null)
                    {
                        monitor.agentRemoved(this.getHandle(), value.getHandle(), value.getType(), LocalDateTime.now());
                    }
                    value.removeConnections();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Portal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
  
    /**
     * The methods is needed to allow access to the routing table
     * @return the routing table
     */
    public TreeMap<String, MetaAgent> getRoutingTable() 
    {
        return routingTable;
    }
    
    /**
     * Class setMonitor is used to set the local variable monitor needed for the object
     * @param pm is a portal monitor
     */
    public void setMonitor(PortalMonitor pm)
    {
        monitor = pm;
    }

    /**
     * Method needed to return the objects monitor variable 
     * @return the monitor
     */
    public PortalMonitor getMonitor() 
    {
        return monitor;
    }
    
    /**
     * Method which returns the type of MetaAgent from the routing table
     * @param handle name of the metaAgent
     * @return the type or null if applicable
     */
    //needed for system logging
    public MetaAgentType getTypeFromTable(String handle)
    {
        try
        {
        if (routingTable.get(handle).getType() == MetaAgentType.ROUTER)
        {
            if(routingTable.get(handle).getRoutingTable().get(handle).getType() == MetaAgentType.ROUTER)
            {
                return MetaAgentType.ROUTER;                
            }
            else if(routingTable.get(handle).getRoutingTable().get(handle).getType() == MetaAgentType.PORTAL)
            {
                if(routingTable.get(handle).getRoutingTable().get(handle).getRoutingTable().get(handle).getType() == MetaAgentType.PORTAL)
                {
                    return MetaAgentType.PORTAL;
                }
                else
                {
                    return MetaAgentType.USER;
                }
            }
            else
            {
                return MetaAgentType.SOCKET;  
            }
        } 
        else if (routingTable.get(handle).getType() == MetaAgentType.USER)
        {
            return MetaAgentType.USER;  
        } 
        else
        {
            return null;  
        }
        }
        catch(Exception e)
        {
            return null;            
        }
    }
    
    /**
     * Returns a list with all the agent handles
     * @return list of users
     */
    public ArrayList<String> getAllUsers()
    {
        ArrayList<String> list = new ArrayList<>();
        Map<String, MetaAgent> copyMap = new ConcurrentHashMap<>(routingTable);
        for(Map.Entry mapElement : copyMap.entrySet())
        {
            String key = (String)mapElement.getKey();
            MetaAgent value = (MetaAgent)mapElement.getValue();
            if(key == value.getHandle() && value.getType() == MetaAgentType.USER)
            {
                list.add(key);
            }
        }
        return list;
    }
}
