package metaagentsubsystem;

/**
 * Interface which enforces the usage of the methods inside used for message retrieval
 * @author Group B, Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */

public interface MsgListener 
{
    /**
     * Empty Method needed for when implemented 
     * @param msg needs message data object
     */
    public void receiveMsg(MsgData msg);
    
    /**
     * Empty Method needed for when implemented 
     * @param msg needs message data object 
     */
    public void sendMsg(MsgData msg);
}
