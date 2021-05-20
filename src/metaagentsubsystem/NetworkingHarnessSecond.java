package metaagentsubsystem;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import javax.swing.JFrame;

/**
 * Test Harness Class for External Network Tests
 * @author Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class NetworkingHarnessSecond 
{

    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws InterruptedException, IOException 
    {
        Router r2 = Router.createNew("r2");
        
        Portal p3 = new Portal("p3", r2);
        
        UserAgent ben = new UserAgent("Ben", p3);
        UserAgent jacob = new UserAgent("Jacob", p3);
        
        Portal p4 = new Portal("p4", r2);
        
        UserAgent teddy = new UserAgent("Teddy", p4);
        System.out.println(InetAddress.getLocalHost().getHostAddress().trim());
        r2.advertiseConnection(1232);
        r2.newConnection(InetAddress.getLocalHost().getHostAddress().trim(), 1234);
        Thread.sleep(5000);
        
        //ben.sendMsg(new MsgData("Ben", "Carter", "Hi Carter! I am Ben!"));
        UserAgent jerry = new UserAgent("Jerry", p3);
        Thread.sleep(1000);
        System.out.println("");
        System.out.println("");
        
        System.out.println("");
        System.out.println("r2");
        for(Map.Entry<String, MetaAgent> set : r2.getRoutingTable().entrySet())
        {
            System.out.println(set.getKey() + " " + set.getValue().getHandle());
        }
        System.out.println("");
        System.out.println("p3");
        for(Map.Entry<String, MetaAgent> set : p3.getRoutingTable().entrySet())
        {
            System.out.println(set.getKey() + " " + set.getValue().getHandle());
        }
        System.out.println("");
        System.out.println("p4");
        for(Map.Entry<String, MetaAgent> set : p4.getRoutingTable().entrySet())
        {
            System.out.println(set.getKey() + " " + set.getValue().getHandle());
        }
        System.out.println("");
        
  
    }
    
}
