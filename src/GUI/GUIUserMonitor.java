package GUI;

import java.time.LocalDateTime;
import metaagentsubsystem.DefaultUserMonitor;

/**
 * This class is used to extract the data from the metaagentsubsystem
 * @author Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class GUIUserMonitor extends DefaultUserMonitor
{
    //Instance Variable
    MsgWindow mw;
    
    /**
     * Method takes a GUI for message window and applies it to this object
     * @param mw message window 
     */
    public GUIUserMonitor(MsgWindow mw)
    {
        this.mw = mw;
    }
    
    /**
     * Method is used to display the message activity and information on received message
     * @param portalHandle name of the portal 
     * @param sender sender information
     * @param recipient recipient information
     * @param body message
     * @param time time of message 
     */
    @Override
    public void messageRecieved(String portalHandle, String sender, String recipient, String body, LocalDateTime time) 
    {
        mw.newMsg(time.toString(), sender, recipient, body);
        System.out.println("Carter Test Point f");
    }

    /**
     * Method is used to display the message activity and information on sent message
     * @param portalHandle name of the portal 
     * @param sender sender information
     * @param recipient recipient information
     * @param body message
     * @param time time of message 
     */
    @Override
    public void messageSent(String portalHandle, String sender, String recipient, String body, LocalDateTime time) 
    {
        mw.newMsg(time.toString(), sender, recipient, body);
        System.out.println("Carter Test Point c");
    }
    
    /**
     * Method to remove the message windows when user is removed
     */
    @Override
    public void removal()
    {
        mw.terminate();
    }
    
    /**
     * Does nothing
     */
    public void addAgent()
    {
        
    }
}
