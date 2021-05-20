package metaagentsubsystem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.Map;

/**
 * Test Harness Class for testing Monitor Network
 * @author Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class MonitorNetworkTestHarness 
{
  
        /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.NoSuchFieldException
     */
    public static void main(String[] args) throws InterruptedException, IOException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException
    {
        resetFile("system_log.txt");
        resetFile("user_log.txt");
        int sleepTime = 500;
        FileManager sysFm = new FileManager("system_log.txt");
        FileManager userFm = new FileManager("user_log.txt");
        DefaultPortalMonitor p1mon = new DefaultPortalMonitor(sysFm,userFm);
        DefaultRouterMonitor r1mon = new DefaultRouterMonitor(sysFm,userFm);
        DefaultPortalMonitor p2mon = new DefaultPortalMonitor(sysFm,userFm);
        
        resetRouter();
        Router r1 = Router.createNew("r1");
        r1.setMonitor(r1mon);
        Portal p1 = new Portal("p1",r1);
        p1.setMonitor(p1mon);
        UserAgent u1 = new UserAgent("u1", p1);
        //UserAgent u2 = new UserAgent("u2", p1);
        Portal p2 = new Portal("p2",r1);
        p2.setMonitor(p2mon);
        UserAgent u3 = new UserAgent("u3", p2);
        UserAgent u4 = new UserAgent("u4", p2);
        resetRouter();
        Router r2 = Router.createNew("r2");
        Portal p3 = new Portal("p3",r2);
        UserAgent u5 = new UserAgent("u5", p3);
        UserAgent u6 = new UserAgent("u6", p3);
        Portal p4 = new Portal("p4",r2);
        
        UserAgent u8 = new UserAgent("u8", p4);
        Thread.sleep(sleepTime);
        
        r1.advertiseConnection(8000);
        r2.advertiseConnection(9000);
        r2.newConnection(InetAddress.getLocalHost().getHostAddress().trim(), 8000);
        Thread.sleep(sleepTime);
        
        
        u1.sendMessage("u8", "Hi u8");
        Thread.sleep(sleepTime);
        u6.sendMessage("u3", "Hi u3");
        
        UserAgent u7 = new UserAgent("u7", p4);
        UserAgent u2 = new UserAgent("u2", p1);
        Thread.sleep(1000);
        r1.removeAgent("u1");
        Thread.sleep(1000);
        r2.removeAgent("u8");

    }
    
    /**
     * Method used for reseting the router variables
     * @throws java.lang.NoSuchFieldException
     * @throws java.lang.IllegalAccessException
     */
    public static void resetRouter() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        Field instance = Router.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null); 
    }
    
    /**
     * Method used for testing in the harness
     * @param fileName name of of the file
     */
    public static void resetFile(String fileName) 
    {
        File file = new File(fileName);
        try 
        {
            final BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
            bw.write("");
            bw.close();
        } 
        catch (IOException e) 
        {
            System.err.println("Something went wrong restting the file");
        }
    }
}
