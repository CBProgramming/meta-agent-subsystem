package metaagentsubsystem;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import javax.swing.JFrame;
import java.util.TreeMap;

/**
 * Test Harness Class 
 * @author Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class MetaAgentSubsystem 
{

    /**
     * Main Method to run the test harness
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.NoSuchFieldException
     */
    public static void main(String[] args) throws InterruptedException, IOException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException
    {
   
        Router r1;
        Portal p1, p2;
        UserAgent u1, u2, u3, u4;
        
        r1 = Router.createNew("r1");
        p1 = new Portal("p1",r1);
        u1 = new UserAgent("u1", p1);
        u2 = new UserAgent("u2", p1);
        p2 = new Portal("p2",r1);
        u3 = new UserAgent("u3", p2);
        u4 = new UserAgent("u4", p2);
        u1.sendMessage("u3", "heya");
        Thread.sleep(5000);
        p2.removeAgent("p1");
        Thread.sleep(2000);
        System.out.println("");
        System.out.println(r1.getHandle());
        for(Map.Entry<String, MetaAgent> set : r1.getRoutingTable().entrySet())
        {
            System.out.println(set.getKey() + " " + set.getValue().getHandle());
        }
        System.out.println("");
        System.out.println(p1.getHandle());
        for(Map.Entry<String, MetaAgent> set : p1.getRoutingTable().entrySet())
        {
            System.out.println(set.getKey() + " " + set.getValue().getHandle());
        }
        System.out.println("");
        System.out.println(p2.getHandle());
        for(Map.Entry<String, MetaAgent> set : p2.getRoutingTable().entrySet())
        {
            System.out.println(set.getKey() + " " + set.getValue().getHandle());
        }
   
    }
    
}
