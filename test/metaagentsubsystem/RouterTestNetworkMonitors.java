package metaagentsubsystem;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import static metaagentsubsystem.RouterTestNetworkMonitors.sleepTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Group B, Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class RouterTestNetworkMonitors 
{
        private ByteArrayOutputStream sysOut;
        private Router r1, r2;
        private Portal p1, p2, p3, p4, p5, p6;
        private UserAgent u1, u2, u3, u4, u5, u6, u7, u8, u9, u10;
        static int sleepTime;
        TreeMap<String, MetaAgent> router1Expected, router2Expected,
                portal1Expected,portal2Expected,portal3Expected,portal4Expected,
                portal5Expected,portal6Expected;
        FileManager sysFm, userFm;
        DefaultPortalMonitor p1mon, p2mon;
        DefaultRouterMonitor r1mon;
        String fileContents, expected; 
        static String separator;
    
        /**
         * Method used to call the test
         */
    public RouterTestNetworkMonitors() {
    }
    
    /**
     * Initialise variables for all tests
     */  
    @BeforeClass
    public static void setUpClass()
    {
        separator = "¶»¤";
        sleepTime = 2000;
    }
    
    /**
     * Setup basic network and monitors for each test
     * @throws java.lang.NoSuchFieldException
     * @throws java.lang.InterruptedException
     * @throws java.lang.IllegalAccessException
     */       
    @Before
    public void setUp() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InterruptedException
    {
        sysFm = new FileManager("system_log.txt");
        userFm = new FileManager("user_log.txt");
        p1mon = new DefaultPortalMonitor(sysFm,userFm);
        r1mon = new DefaultRouterMonitor(sysFm,userFm);
        p2mon = new DefaultPortalMonitor(sysFm,userFm);
        try
        {
            resetRouter();
        }
        catch (IllegalAccessException e)
        {
            System.out.println("Reset router failed");
        }
        basicNetworkSetup();   
    }

    /**
     * Override singleton router for testing purposes
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
     * Reset monitor log files
     */   
    public void resetFiles()
    {
        resetFile("system_log.txt");
        resetFile("user_log.txt");
    }
    
    /**
     * Setup basic network
     * @throws java.lang.NoSuchFieldException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.InterruptedException
     */   
    public void basicNetworkSetup() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InterruptedException
    {
        resetFile("system_log.txt");
        resetFile("user_log.txt");
        resetRouter();
        r1 = Router.createNew("r1");
        r1.setMonitor(r1mon);
        p1 = new Portal("p1",r1);
        p1.setMonitor(p1mon);
        u1 = new UserAgent("u1", p1);
        u2 = new UserAgent("u2", p1);
        p2 = new Portal("p2",r1);
        p2.setMonitor(p2mon);
        u3 = new UserAgent("u3", p2);
        u4 = new UserAgent("u4", p2);
        resetRouter();
        r2 = Router.createNew("r2");
        p3 = new Portal("p3",r2);
        u5 = new UserAgent("u5", p3);
        u6 = new UserAgent("u6", p3);
        p4 = new Portal("p4",r2);
        u7 = new UserAgent("u7", p4);
        u8 = new UserAgent("u8", p4);
        Thread.sleep(sleepTime);
    }   
    
    /**
     * Print routing table for MetaAgent
     * @param ma MetaAgent
     */  
    public void printRoutingTable(MetaAgent ma)
    {
        for(Map.Entry<String, MetaAgent> set : ma.getRoutingTable().entrySet())
        {
            System.out.println(set.getKey() + " " + set.getValue().handle);
        }        
    }
    
    /**
     * Connect two routers
     * @param port port of first router
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */  
    public void advertise(int port) throws IOException, InterruptedException
    {
        r1.advertiseConnection(port);
        r2.advertiseConnection(port + 1000);
        r1.newConnection(InetAddress.getLocalHost().getHostAddress().trim(), port + 1000);
        Thread.sleep(sleepTime);
    }

    /**
     * Reset file contents
     * @param fileName name of file
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
    
    /**
     * Read file contents
     * @param fileName name of file
     * @return string file contents
     */ 
    public static String readFile(String fileName) 
    {
        File file = new File(fileName);
        String fileString = "";
        try 
        {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) 
            {
                fileString += scanner.nextLine();
            }
        } catch (FileNotFoundException e) 
        {
            System.err.println("Error attempting to read file");
        }
        return fileString;
    }
    
    /**
     * Test double router setup successfully overrides singleton
     */
    @Test
    public void checkDoubleRouterSetup()
    {
        assertFalse(r1 == r2);
    }   

    /**
     * Check file logging for basic setup
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testBasicSetup() throws IOException, InterruptedException
    {
        advertise(8000);
        Thread.sleep(sleepTime);
        fileContents = readFile("user_log.txt");
        assertEquals("",fileContents);
        fileContents = readFile("system_log.txt");
        expected = "PORTAL added to: " + separator + "r1\tName: " + separator + 
                "p1\tTime Added: " + separator;
        assertTrue(fileContents.contains(expected));   
        expected = "PORTAL added to: " + separator + "r1\tName: " + separator + 
                "p2\tTime Added: " + separator;        
        assertTrue(fileContents.contains(expected));
        expected = "USER added to: " + separator + "p1\tName: " + separator + 
                "u1\tTime Added: " + separator;
        assertTrue(fileContents.contains(expected));   
        expected = "USER added to: " + separator + "p1\tName: " + separator + 
                "u2\tTime Added: " + separator;
        assertTrue(fileContents.contains(expected));         
        expected = "USER added to: " + separator + "p2\tName: " + separator + 
                "u3\tTime Added: " + separator;
        assertTrue(fileContents.contains(expected));   
        expected = "USER added to: " + separator + "p2\tName: " + separator + 
                "u4\tTime Added: " + separator;
        assertTrue(fileContents.contains(expected));         
    }
    
    /**
     * Check file logging when adding agents
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testAddLocalPortalAndUser() throws IllegalArgumentException, IOException, InterruptedException
    {
        advertise(8001);
        resetFiles();
        p5 = new Portal("p5",r1);
        p5.setMonitor(p1mon);
        Thread.sleep(sleepTime);
        u9 = new UserAgent("u9",p5);
        Thread.sleep(sleepTime);
        fileContents = readFile("user_log.txt");
        assertEquals("",fileContents);
        fileContents = readFile("system_log.txt");
        expected = "PORTAL added to: " + separator + "r1\tName: " + separator + 
                "p5\tTime Added: " + separator;
        assertTrue(fileContents.contains(expected));   
        expected = "USER added to: " + separator + "p5\tName: " + separator + 
                "u9\tTime Added: " + separator;
        assertTrue(fileContents.contains(expected));   
    }
    
    /**
     * Check file logging when adding agents
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testAddPortalAndAgentToBothRoutersHashMapCheck() throws IOException, InterruptedException 
    {
        advertise(8002);
        resetFiles();
        p5 = new Portal("p5",r1);
        p5.setMonitor(p1mon);
        u9 = new UserAgent("u9",p5);
        p6 = new Portal("p6",r2);
        u10 = new UserAgent("u10",p6);
        Thread.sleep(sleepTime);
        fileContents = readFile("user_log.txt");
        assertEquals("",fileContents);
        fileContents = readFile("system_log.txt");
        expected = "PORTAL added to: " + separator + "r1\tName: " + separator + 
                "p5\tTime Added: " + separator;
        assertTrue(fileContents.contains(expected));   
        expected = "USER added to: " + separator + "p5\tName: " + separator + 
                "u9\tTime Added: " + separator;
        assertTrue(fileContents.contains(expected));  
        expected = "AGENT added to: " + separator + "r2\tName: " + separator + 
                "p6\tTime Added: " + separator;
        assertTrue(fileContents.contains(expected)); 
        expected = "AGENT added to: " + separator + "r2\tName: " + separator + 
                "u10\tTime Added: " + separator;
        assertTrue(fileContents.contains(expected)); 
    }
   
    /**
     * Check file logging when removing agents
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testRemoveUserHashMapCheckViaRouter() throws InterruptedException, IOException 
    {
        advertise(8003);
        resetFiles();
        r1.removeAgent("u4");            
        Thread.sleep(sleepTime);
        fileContents = readFile("user_log.txt");
        assertEquals("",fileContents);
        fileContents = readFile("system_log.txt");
        expected = "USER removed by: " + separator + "r1\tName: " + separator + 
                "u4\tTime Removed: " + separator;
        assertTrue(fileContents.contains(expected));
    }   
    
    /**
     * Check file logging when removing agents
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testRemoveTwoUsersHashMapCheckViaDifferentRouters() throws InterruptedException, IOException 
    {
        advertise(8004);
        resetFiles();
        r1.removeAgent("u4");            
        r2.removeAgent("u8");
        Thread.sleep(sleepTime);
        fileContents = readFile("user_log.txt");
        assertEquals("",fileContents);
        fileContents = readFile("system_log.txt");
        expected = "USER removed by: " + separator + "r1\tName: " + separator + 
                "u4\tTime Removed: " + separator;
        assertTrue(fileContents.contains(expected));
        expected = "AGENT removed by: " + separator + "r2\tName: " + separator + 
                "u8\tTime Removed: " + separator;
        assertTrue(fileContents.contains(expected));        
    }
    
    /**
     * Check file logging when removing agents
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testRemoveUserHashMapCheckViaPortal() throws InterruptedException, IOException 
    {
        advertise(8006);
        resetFiles();
        p1.removeAgent("u4");            
        Thread.sleep(sleepTime);
        fileContents = readFile("user_log.txt");
        assertEquals("",fileContents);
        fileContents = readFile("system_log.txt");
        expected = "USER removed by: " + separator + "p1\tName: " + separator + 
                "u4\tTime Removed: " + separator;
        assertTrue(fileContents.contains(expected));
    }   
    
    /**
     * Check file logging when removing agents
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testRemoveTwoUsersHashMapCheckViaDifferentPortals() throws InterruptedException, IOException 
    {
        advertise(8007);
        resetFiles();
        p2.removeAgent("u4"); 
        p3.removeAgent("u8");
        Thread.sleep(sleepTime);
        fileContents = readFile("user_log.txt");
        assertEquals("",fileContents);
        fileContents = readFile("system_log.txt");
        
        expected = "USER removed by: " + separator + "p2\tName: " + separator + 
                "u4\tTime Removed: " + separator;
        assertTrue(fileContents.contains(expected));    
                expected = "AGENT removed by: " + separator + "r2\tName: " + separator + 
                "u8\tTime Removed: " + separator;
        assertTrue(fileContents.contains(expected));  
    }
    
    
    /**
     * Check file logging when removing agents
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testRemovePortalHashMapCheckViaRouter() throws InterruptedException, IOException 
    {
        advertise(8009);
        resetFiles();
        Thread.sleep(sleepTime +1000);
        r1.removeAgent("p2"); 
        Thread.sleep(sleepTime +1000);
        fileContents = readFile("user_log.txt");
        assertEquals("",fileContents);
        fileContents = readFile("system_log.txt");
        expected = "PORTAL removed by: " + separator + "r1\tName: " + separator + 
                "p2\tTime Removed: " + separator;
        assertTrue(fileContents.contains(expected)); 
        expected = "USER removed by: " + separator + "r1\tName: " + separator + 
                "u3\tTime Removed: " + separator;        
        assertTrue(fileContents.contains(expected)); 
        expected = "USER removed by: " + separator + "r1\tName: " + separator + 
                "u4\tTime Removed: " + separator;
        assertTrue(fileContents.contains(expected)); 
    }      
    
    /**
     * Check file logging when removing agents
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testRemovePortalOnDifferentNetworkHashMapCheckViaRouter() throws InterruptedException, IOException 
    {
        advertise(8010);
        resetFiles();
        r2.removeAgent("p2"); 
        Thread.sleep(sleepTime);
        fileContents = readFile("user_log.txt");
        assertEquals("",fileContents);
        fileContents = readFile("system_log.txt");
        expected = "AGENT removed by: " + separator + "r2\tName: " + separator + "p2\tTime Removed: " + separator;
        assertTrue(fileContents.contains(expected));
        expected = "USER removed by: " + separator + "p2\tName: " + separator + "u3\tTime Removed: " + separator;
        assertTrue(fileContents.contains(expected));       
        expected = "USER removed by: " + separator + "p2\tName: " + separator + "u4\tTime Removed: " + separator;     
        assertTrue(fileContents.contains(expected));
    }    
    
    /**
     * Check file logging when removing agents
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testRemovePortalHashMapCheckViaPortal() throws InterruptedException, IOException 
    {
        advertise(8011);
        resetFiles();
        p1.removeAgent("p2"); 
        Thread.sleep(sleepTime);
        fileContents = readFile("user_log.txt");
        assertEquals("",fileContents);
        fileContents = readFile("system_log.txt");
        //assertEquals("",fileContents);
        expected = "PORTAL removed by: " + separator + "p1\tName: " + separator 
                + "p2\tTime Removed: " + separator;
        assertTrue(fileContents.contains(expected)); 
        expected = "USER removed by: " + separator + "p2\tName: " + separator 
                + "u4\tTime Removed: " + separator;
        assertTrue(fileContents.contains(expected)); 
        expected = "USER removed by: " + separator + "p2\tName: " + separator 
                + "u3\tTime Removed: " + separator;
        assertTrue(fileContents.contains(expected));  
    }      
    
    /**
     * Check file logging when removing agents
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testRemovePortalOnDifferentNetworkHashMapCheckViaPortal() throws InterruptedException, IOException 
    {
        advertise(8012);
        resetFiles();
        p4.removeAgent("p2"); 
        Thread.sleep(sleepTime);
        fileContents = readFile("user_log.txt");
        assertEquals("",fileContents);
        fileContents = readFile("system_log.txt");
        expected = "AGENT removed by: " + separator + "r2\tName: " + separator + "p2\tTime Removed: " + separator;
        assertTrue(fileContents.contains(expected));
        expected = "USER removed by: " + separator + "p2\tName: " + separator + "u3\tTime Removed: " + separator;
        assertTrue(fileContents.contains(expected));       
        expected = "USER removed by: " + separator + "p2\tName: " + separator + "u4\tTime Removed: " + separator;     
        assertTrue(fileContents.contains(expected));
    }    

    /**
     * Check file logging when adding user to router (disallowed)
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testAddUserAgentToRouter() throws InterruptedException, IOException
    {  
        sysOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(sysOut));
        advertise(8037);
        p5 = new Portal("p5",r1);
        Thread.sleep(sleepTime);
        u9 = new UserAgent("u9",p5);
        Thread.sleep(sleepTime);
        p5.removeAgent("u9");
        Thread.sleep(sleepTime);
        resetFiles();
        r1.addAgent(u9);
        Thread.sleep(sleepTime);
        fileContents = readFile("user_log.txt");
        assertEquals("",fileContents);
        fileContents = readFile("system_log.txt");
        assertEquals("",fileContents);               
    }  
    
    /**
     * Check file logging when using removeAllAgents method of class portal
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testPortalRemoveAllUsers() throws InterruptedException, IOException
    {  
        advertise(8038);
        resetFiles();
        p2.removeAllUsers();
        Thread.sleep(sleepTime);
        fileContents = readFile("user_log.txt");
        assertEquals("",fileContents);
        fileContents = readFile("system_log.txt");
        expected = "USER removed by: " + separator + "p2\tName: " + separator 
                + "u4\tTime Removed: " + separator;
        assertTrue(fileContents.contains(expected)); 
        expected = "USER removed by: " + separator + "p2\tName: " + separator 
                + "u3\tTime Removed: " + separator;
        assertTrue(fileContents.contains(expected));         
    }        
}