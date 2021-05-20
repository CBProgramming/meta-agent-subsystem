package metaagentsubsystem;

import java.time.LocalDateTime;

/**
 * Class that creates the necessary methods to allow the system to function, however +
 * these are left empty as they can be adapted as seen fit by the developer
 * @author Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class DefaultUserMonitor implements UserMonitor 
{
    /**
     * Method messageRecieved receives the information of Messages being received
     * @param portalHandle - name of the portal
     * @param sender - name of the sender
     * @param recipient - name of the recipient
     * @param body - the text sent in a message 
     * @param time - the time the message was received
     */
    @Override
    public void messageRecieved(String portalHandle, String sender, String recipient, String body, LocalDateTime time) 
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Method messageSent receives the information of Messages being sent
     * @param portalHandle - name of the portal
     * @param sender - name of the sender
     * @param recipient - name of the recipient
     * @param body - the text sent in a message 
     * @param time - the time the message was received 
     */
    @Override
    public void messageSent(String portalHandle, String sender, String recipient, String body, LocalDateTime time) 
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Method removal receives the information of Messages being received
     */
    @Override
    public void removal() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
