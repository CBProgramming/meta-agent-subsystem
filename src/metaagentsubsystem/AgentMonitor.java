package metaagentsubsystem;

import java.time.LocalDateTime;

/**
 * Interface AgentMonitor contains two methods which take in information and ultimately force usage when implemented.
 * @author Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public interface AgentMonitor 
{
    /**
     * Method messageRecieved receives the information of Messages being received on the agent when implemented
     * @param portalHandle - name of the portal
     * @param sender - name of the sender
     * @param recipient - name of the recipient
     * @param body - the text sent in a message 
     * @param time - the time the message was received
     */
    public abstract void messageRecieved(String portalHandle, String sender, String recipient, String body, LocalDateTime time); //Session ID
    
    /**
     * Method messageSent receives the information of Messages being sent and passes this information to the file manager
     * @param portalHandle - name of the portal
     * @param sender - name of the sender
     * @param recipient - name of the recipient
     * @param body - the text sent in a message 
     * @param time - the time the message was received 
     */
    public abstract void messageSent(String portalHandle, String sender, String recipient, String body, LocalDateTime time); //Session ID
}
