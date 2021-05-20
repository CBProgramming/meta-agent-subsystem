package metaagentsubsystem;

import java.time.LocalDateTime;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class Default Router Monitor created to allow monitoring of the systems Routers
 * @author Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class DefaultRouterMonitor implements RouterMonitor
{
    /**
     * Instance Variables
     */
    private FileManager sysFm;
    private FileManager userFm;
    private ReentrantLock lock;
    private String separator;
    
    /**
     * Constructor allows the object to be instantiated
     * @param sysFm Takes a FileMananager object used for system info
     * @param userFm Takes in a FileManager object used for user info 
     */
    public DefaultRouterMonitor(FileManager sysFm,FileManager userFm)
    {
        this.sysFm = sysFm;
        this.userFm = userFm;
        lock = new ReentrantLock();
        separator = "¶»¤";
    }
    
    /**
     * Method agentAdded receives the information of Agents being added and passes this information to the file manager
     * @param portalHandle - name of the portal
     * @param agentName - name of the agent
     * @param type - metaAgent type
     * @param time - time the agent was added 
     */
    @Override
    public void agentAdded( String portalHandle, String agentName,  MetaAgentType type, LocalDateTime time)
    {  
       lock.lock();
       String handle = type.toString();
       if(handle == "ROUTER")
       {
           handle = "AGENT";
       }
       sysFm.writeMsg(handle + " added to: " + separator + 
               portalHandle + "\tName: " + separator + agentName + 
               "\tTime Added: " + separator + time);
       lock.unlock();         
    }
    
    /**
     * Method agentRemoved receives the information of Agents being removed and passes this information to the file manager
     * @param portalHandle - name of the portal
     * @param agentName - name of the agent
     * @param type - metaAgent type
     * @param time - time the agent was removed
     */
    @Override
    public void agentRemoved(String portalHandle, String agentName, MetaAgentType type, LocalDateTime time)
    {  
       lock.lock();
       String handle = type.toString();
       if(handle == "ROUTER")
       {
           handle = "AGENT";
       }    
       sysFm.writeMsg(handle + " removed by: " + separator + 
               portalHandle + "\tName: " + separator + agentName + 
               "\tTime Removed: " + separator + time);
       lock.unlock();        
    }
    
     /**
     * Method messageRecieved receives the information of Messages being received and passes this information to the file manager
     * @param portalHandle - name of the portal
     * @param sender - name of the sender
     * @param recipient - name of the recipient
     * @param body - the text sent in a message 
     * @param time - the time the message was received
     */
    @Override
    public void messageRecieved(String portalHandle, String sender, String recipient,String body, LocalDateTime time) //Session ID //done
    {  
       lock.lock();
       userFm.writeMsg("Message forwarded from router to external user: " + separator + recipient + 
               "\tSender: " + separator + sender +  "\tTime sent: " + separator + 
               time + "\tMessage body: " + separator + body);
       lock.unlock();   
    }

    /**
     * Method messageSent receives the information of Messages being sent and passes this information to the file manager
     * @param portalHandle - name of the portal
     * @param sender - name of the sender
     * @param recipient - name of the recipient
     * @param body - the text sent in a message 
     * @param time - the time the message was received
     */
    @Override
    public void messageSent(String portalHandle, String recipient, String sender, String body, LocalDateTime time) //Session ID //done
    {
       lock.lock();
       userFm.writeMsg("Message received in local router from external user: " + separator + 
               recipient + "\tRecipient: " + separator + sender + "\tTime received: " + separator + 
               time + "\tMessage body: " + separator + body);
       lock.unlock();  
    }
    
    /**
     * Method newConnection receives the information of the connection being made and passes this information to the file manager
     * @param portalHandle - name of the portal
     * @param connectionHandle - name of the connection
     * @param time - the time of the new connection
     */
    @Override
    public void newConnection(String portalHandle, String connectionHandle, LocalDateTime time)
    {  
       lock.lock();
       sysFm.writeMsg("New connection between: " + separator + 
               portalHandle + "\tand: " + separator + connectionHandle + 
               "\tTime: " + separator + time);
       lock.unlock();          
    }
}
