package metaagentsubsystem;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class UserAgent extends metaAgent and also has implementation of MsgListener
 * @author Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class UserAgent extends MetaAgent implements MsgListener
{
    //Instance Variables
    private Portal portal;   
    private UserMonitor monitor; 
    /**
     * Message timeout before sending messages to make sure the users have time to be added to the system.
     */
    public static int messageTimeout = 100;
    
    /**
     * Constructor for UserAgent.
     * @param handle name 
     * @param portal portal the agent is assigned too
     */
    public UserAgent(String handle, Portal portal)
    {
        super(handle);
        type = MetaAgentType.USER;
        this.portal = portal;
        if (portal != null)
        portal.addAgent(this);
    }
    
    /**
     * The method is used to handle a message received.
     * For now it displays it to the console.
     * @param md is the message data
     */
    @Override
    public void receiveMsg(MsgData md)
    {
        if (md != null)
        {
            System.out.println(md.getSender() + ": " + md.getBody() + " Recipient: " + getHandle());  
            
            if(monitor != null)
            {
                monitor.messageRecieved(portal.getHandle(), getHandle(), md.getRecipient(), md.getBody(), LocalDateTime.now());
            }
        }
        else
        {
            System.out.println("Error. Null message received.");
        }
    }

    /**
     * Handles the incoming message, calling the receiveMsg method.
     * @param md
     * @throws InterruptedException
     * @throws IOException 
     */
    @Override
    protected void msgHandler(MsgData md) throws InterruptedException, IOException 
    {
        receiveMsg(md);
        if (portal != null && portal.getMonitor() != null)
        {
            portal.getMonitor().messageRecieved(portal.getHandle(), md.getSender(), getHandle(), md.getBody(),LocalDateTime.now());
        }        
    }

    /**
     * The send message method is used to initialise the sending of a user message
     * @param recipient holds the recipient
     * @param body the body of the message
     */
    public void sendMessage(String recipient, String body)
    {
        sendMsg(new MsgData(getHandle(), recipient, body));
    }
    
    /**
     * This method sends the user message to the portal's blocking queue.
     * @param msg is the message data
     */
    @Override
    public void sendMsg(MsgData msg) {
        try {
            if(msg == null)
            {
                System.out.println("Empty message");
                return;
            }
            if(portal == null)
            {
                System.out.println("Recipient not found");
                return;
            }
            long timeout = System.currentTimeMillis() + messageTimeout;
            while(System.currentTimeMillis() < timeout)
            {
                
            }
            
            portal.put(msg);
            
            if (portal != null && portal.getMonitor() != null)
            {
                portal.getMonitor().messageSent(portal.getHandle(), getHandle(), msg.getRecipient(), msg.getBody(),LocalDateTime.now());
            }
            
            if(monitor != null)
            {
                monitor.messageSent(portal.getHandle(), getHandle(), msg.getRecipient(), msg.getBody(), LocalDateTime.now());
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(UserAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method returns the portal connected
     * @return portal object
     */
    public Portal getPortal() {
        return portal;
    }
    
    /**
     * This method is used to remove the connections to the portal.
     */
    @Override
    public void removeConnections()
    {
        if(monitor != null)
            {
                monitor.removal();
            }
        
        portal = null;
    }
    
    /**
     * This Method sets the user monitor variable 
     * @param md is the userMonitor object
     */
    public void setMonitor(UserMonitor md)
    {
        monitor = md;
    }

}
