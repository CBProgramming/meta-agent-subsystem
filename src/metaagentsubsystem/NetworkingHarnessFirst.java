package metaagentsubsystem;

import java.io.IOException;
import java.util.Map;
import javax.swing.JFrame;

/**
 * Test Harness Class for Internal Network Tests
 * @author Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class NetworkingHarnessFirst 
{

    /**
     * Main Method to allow testing of the Networking 
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws InterruptedException, IOException 
    {
   
        Router r1 = Router.createNew("r1");
        Portal p1 = new Portal("p1", r1);
        
        UserAgent carter = new UserAgent("Carter", p1);
        UserAgent chris = new UserAgent("Chris", p1);

        Portal p2 = new Portal("p2", r1);
        
        UserAgent boris = new UserAgent("Boris", p2);
        
        r1.advertiseConnection(1234);
        Thread.sleep(10000);
        
        chris.sendMsg(new MsgData("Chris", "Jerry", "Hi Jerry! I am Chris!"));
        Thread.sleep(1000);
        System.out.println("");
        System.out.println("");
        
        System.out.println("");
        System.out.println("p1");
        for(Map.Entry<String, MetaAgent> set : p1.getRoutingTable().entrySet())
        {
            System.out.println(set.getKey() + " " + set.getValue().getHandle());
        }
        System.out.println("");
        System.out.println("p2");
        for(Map.Entry<String, MetaAgent> set : p2.getRoutingTable().entrySet())
        {
            System.out.println(set.getKey() + " " + set.getValue().getHandle());
        }
        System.out.println("");
        System.out.println("r1");
        for(Map.Entry<String, MetaAgent> set : r1.getRoutingTable().entrySet())
        {
            System.out.println(set.getKey() + " " + set.getValue().getHandle());
        }
        System.out.println("");
        

    }
    
}
