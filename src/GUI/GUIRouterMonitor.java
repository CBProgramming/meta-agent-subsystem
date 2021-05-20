package GUI;

import java.time.LocalDateTime;
import metaagentsubsystem.DefaultRouterMonitor;
import metaagentsubsystem.FileManager;
import metaagentsubsystem.MetaAgentType;
import metaagentsubsystem.RouterMonitor;

/**
 * The GUI Router Monitor
 * @author Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class GUIRouterMonitor extends DefaultRouterMonitor
{
    //Instance Variables
    TableManager tm;
    
    /**
     * The Method is used to gather the information from a Router Monitor and display this information in a table
     * @param sysFm system file manager 
     * @param userFm user data file manager
     * @param tm table manager object 
     */
    public GUIRouterMonitor(FileManager sysFm, FileManager userFm, TableManager tm)
    {
        super(sysFm, userFm);
        this.tm = tm;
    }

    /**
     * This Method is used to display information when an agent is added
     * @param portalHandle the name of the portal
     * @param agentName agent name 
     * @param type type of meta agent
     * @param time time of the message 
     */
    @Override
    public void agentAdded(String portalHandle, String agentName,  MetaAgentType type, LocalDateTime time) 
    {
        super.agentAdded(portalHandle, agentName, type, time);
        tm.newLine("[Agent Added](" + type.toString() + ") " + agentName);
    }

    /**
     * This Method is used to display information when an agent is removed
     * @param portalHandle the name of the portal
     * @param agentName agent name 
     * @param type type of meta agent
     * @param time time of the message 
     */
    @Override
    public void agentRemoved(String portalHandle, String agentName, MetaAgentType type, LocalDateTime time) 
    {
        super.agentRemoved(portalHandle, agentName, type, time);
        tm.newLine("[Agent Removed](" + type.toString() + ") " + agentName);
    }

    /**
     * Method is used to display the message activity and information on received information
     * @param portalHandle name of the portal 
     * @param sender sender information
     * @param recipient recipient information
     * @param body message
     * @param time time of message 
     */
    @Override
    public void messageRecieved(String portalHandle, String sender, String recipient, String body, LocalDateTime time) 
    {
        super.messageRecieved(portalHandle, sender, recipient, body, time);
        tm.newLine("[Message Received](" + recipient + " from " + sender + ") " + body, time.toString());
        tm.newRow(time.toString(), -1, -1, sender, recipient, body);
    }

    /**
     * c
     */
    @Override
    public void messageSent(String portalHandle, String sender, String recipient, String body, LocalDateTime time) 
    {
        super.messageSent(portalHandle, sender, recipient, body, time);
        tm.newLine("[Message Sent](" + sender + " to " + recipient + ") " + body, time.toString());
        //tm.newRow(time.toString(), -1, -1, sender, recipient, body);
    }
    
    /**
     * This method enables the router to router connection information to be displayed
     * @param portalHandle name of the portal
     * @param connectionHandle name of the connection
     * @param time time of connection
     */
    @Override
    public void newConnection(String portalHandle, String connectionHandle, LocalDateTime time)
    {  
       tm.newRouter(connectionHandle);
       super.newConnection(portalHandle, connectionHandle, time);
    }
    
}