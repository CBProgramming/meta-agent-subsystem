package metaagentsubsystem;

import java.io.IOException;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MetaAgent Class extends LinkBlockingQueue which consists of objects of type MsgData.
 * @author Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class MetaAgent extends LinkedBlockingQueue<MsgData> 
{
    /**
     * Class instance variables
     */
    public static int sessionCount; 
    protected String handle; 
    protected final ExecutorService pool;
    /**
     * Size of the executor service pool
     */
    public static int poolSize = 50;
    /**
     * Size of the LinkedBlockingQueue
     */
    public static int queueSize = Integer.MAX_VALUE;
    protected MetaAgentType type;

    /**
     * This method is used to get the type of the MetaAgent
     * @return the MetaAgentType of the object
     */
    public MetaAgentType getType() {
        return type;
    }
    
    /**
     * This Constructor calls the superclass, then creates an Executor Service with a fixed thread size.
     */
    public MetaAgent()
    {
        super(queueSize);
        pool = Executors.newFixedThreadPool(poolSize);
    }
    
    /**
     * This Constructor calls the superclass, the creates a Executor Service with a fixed thread size
     * @param handle is the handle of the agent
     */
    public MetaAgent(String handle)
    {
        super(queueSize);
        this.handle = handle;
        pool = Executors.newFixedThreadPool(poolSize);
        startHandleThread();
    }
    
    /**
     * This method is used for handling an user message
     * @param md is the MsgData to be handled
     * @throws InterruptedException - error handler
     * @throws IOException - error handler
     */
    protected void msgHandler(MsgData md) throws InterruptedException, IOException
    {
        System.out.println(md.toString());
    }
    
    /**
     * Method used for removing connections, used in UserAgents
     */
    public void removeConnections()
    {
        return;
    }
    
    /**
     * Method created to set the name of the MetaAgent
     * @param handle is the name 
     */
    public void setHandle(String handle) {
        this.handle = handle;
    }
    
    /**
     * This method handles the linked blocking queue, any errors are picked up an dealt with.
     * It continuously takes messages from the queue and passes them to the message handler
     */
    private void startHandleThread()
    {
        pool.execute(new Runnable()
        {
            public void run()
            {
                try 
                {
                    while(true)
                    {
                        MsgData msg = MetaAgent.this.take();
                        if(msg != null)
                        {
                            msgHandler(msg);
                        }
                        else
                        {
                            System.out.println("Null object in the queue of " + handle + ", aborting...");
                        }
                    }
                } catch (InterruptedException ex) {
                    System.out.println("MetaAgent " + handle + " has been interrupted");
                    return;
                } catch (IOException ex) {
                    System.out.println("IOException in " + handle);
                }
            }
        });
    }
    
    /**
     * Method handles the termination of a thread
     * @throws InterruptedException when the thread is interrupted
     */
    public void terminateThread() throws InterruptedException
    {
        pool.shutdown();
    }
    
    /**
     * Method returns the name of the MetaAgent
     * @return the name
     */
    public String getHandle() 
    {
        return handle;
    }

    /**
     * Method is created to allow child objects display their routing tables
     * @return null as this is not used in the super class
     */
    public TreeMap<String, MetaAgent> getRoutingTable()
    {
        System.out.println("Agent does not have a routing table");
        return null;
    }
}
