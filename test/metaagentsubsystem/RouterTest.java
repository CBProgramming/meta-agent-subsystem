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
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Group B, Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class RouterTest 
{
        private ByteArrayOutputStream sysOut;
        private Router r1;
        private Portal p1, p2;
        private UserAgent u1, u2, u3, u4;
        static int sleepTime;
        private TreeMap<String, MetaAgent> routerExpected;
        private TreeMap<String, MetaAgent> portal1Expected;
        private TreeMap<String, MetaAgent> portal2Expected;
        private static String separator;
        private static String userFile;
        private static String systemFile;
        private static FileManager userManager, systemManager;
        private static PortalMonitor pm;
        private static RouterMonitor rm;
    
     /**
     * Method used to call the test
     */
    public RouterTest() {
    }
    
    /**
     * Initialise variables for all tests
     */  
    @BeforeClass
    public static void setUpClass() 
    {
        sleepTime = 500;
        separator = "¶»¤";
        userFile = "userTestFile.txt";
        systemFile = "systemTestFile.txt";
        userManager = new FileManager(userFile);
        systemManager = new FileManager(systemFile);
        pm = new DefaultPortalMonitor(systemManager,userManager);
        rm = new DefaultRouterMonitor(systemManager,userManager);
    }
    
    /**
     * Setup basic network for each test
     * @throws java.lang.NoSuchFieldException
     * @throws java.lang.InterruptedException
     * @throws java.lang.IllegalAccessException
     */      
    @Before
    public void setUp() throws NoSuchFieldException, IllegalArgumentException, IllegalArgumentException, IllegalAccessException, InterruptedException
    {
        try
        {
            resetRouter();
        }
        catch (IllegalAccessException e)
        {
            System.out.println("Reset router failed");
        }
        basicNetworkSetup();
        expectedHashMapSetup();
        sysOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(sysOut));
    }

    /**
     * Override singleton router for testing purposes
     * @throws java.lang.NoSuchFieldException
     * @throws java.lang.IllegalAccessException
     */   
    public void resetRouter() throws NoSuchFieldException, IllegalAccessException
    {
        Field instance = Router.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null); 
    }
    
    /**
     * Setup basic network
     * @throws java.lang.NoSuchFieldException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.InterruptedException
     */     
    public void basicNetworkSetup() throws NoSuchFieldException, IllegalAccessException, InterruptedException
    {
        r1 = Router.createNew("r1");
        p1 = new Portal("p1",r1);
        u1 = new UserAgent("u1", p1);
        u2 = new UserAgent("u2", p1);
        p2 = new Portal("p2",r1);
        u3 = new UserAgent("u3", p2);
        u4 = new UserAgent("u4", p2);
        Thread.sleep(sleepTime);
    }   

    /**
     * Setup expected tree maps
     */  
    public void expectedHashMapSetup()
    {
        routerExpected = new TreeMap<>();
        routerExpected.put("r1", r1);         
        routerExpected.put("p1", p1);
        routerExpected.put("p2", p2);        
        routerExpected.put("u1", p1);
        routerExpected.put("u2", p1);
        routerExpected.put("u3", p2);
        routerExpected.put("u4", p2);
        portal1Expected = new TreeMap<>();
        portal1Expected.put("r1", r1);         
        portal1Expected.put("p1", p1);        
        portal1Expected.put("p2", r1);        
        portal1Expected.put("u1", u1);
        portal1Expected.put("u2", u2);
        portal1Expected.put("u3", r1);
        portal1Expected.put("u4", r1);
        portal2Expected = new TreeMap<>();
        portal2Expected.put("r1", r1);          
        portal2Expected.put("p1", r1);        
        portal2Expected.put("p2", p2);        
        portal2Expected.put("u1", r1);
        portal2Expected.put("u2", r1);
        portal2Expected.put("u3", u3);
        portal2Expected.put("u4", u4);
    } 

    /**
     * Remove key from expected hash maps
     * @param key key to be removed
     */  
    public void removeKeyFromExpected(String key)
    {
        routerExpected.remove(key);
        portal1Expected.remove(key);
        portal2Expected.remove(key);
    }
    
    /**
     * Reset monitor log file
     * @param fileName file to be reset
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
     * Read monitor log file
     * @param fileName file to be read
     * @return string of file contents
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
     * Test of adding new portal to router and new user to portal
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testAddAgentHashMapCheckViaRouter() throws InterruptedException
    {
        Portal p3 = new Portal("p3",r1);
        UserAgent u5 = new UserAgent("u5",p3);
        Thread.sleep(sleepTime);
        //additional expected outcomes
        routerExpected.put("p3", p3);
        routerExpected.put("u5", p3);
        portal1Expected.put("p3", r1);
        portal1Expected.put("u5", r1);          
        portal2Expected.put("p3", r1);
        portal2Expected.put("u5", r1);         
        TreeMap<String, MetaAgent> portal3Expected = new TreeMap<>();
        portal3Expected.put("u1", r1);
        portal3Expected.put("u2", r1);
        portal3Expected.put("p1", r1);
        portal3Expected.put("u3", r1);
        portal3Expected.put("u4", r1);
        portal3Expected.put("p2", r1);
        portal3Expected.put("r1", r1);        
        portal3Expected.put("p3", p3);
        portal3Expected.put("u5", u5); 
        //check r1 routing table
        assertThat(r1.getRoutingTable(), is(routerExpected));
        assertThat(r1.getRoutingTable().size(), is(routerExpected.size()));          
        assertEquals(routerExpected.containsKey("u5"),r1.getRoutingTable().containsKey("u5"));          
        assertEquals(routerExpected.containsKey("p3"),r1.getRoutingTable().containsKey("p3"));     
        assertEquals(routerExpected.containsKey("u1"),r1.getRoutingTable().containsKey("u1"));
        assertEquals(routerExpected.containsKey("u2"),r1.getRoutingTable().containsKey("u2"));
        assertEquals(routerExpected.containsKey("p1"),r1.getRoutingTable().containsKey("p1"));
        assertEquals(routerExpected.containsKey("u3"),r1.getRoutingTable().containsKey("u3"));
        assertEquals(routerExpected.containsKey("u4"),r1.getRoutingTable().containsKey("u4"));
        assertEquals(routerExpected.containsKey("p2"),r1.getRoutingTable().containsKey("p2"));
        assertEquals(routerExpected.containsKey("r1"),r1.getRoutingTable().containsKey("r1"));
        assertEquals(routerExpected.get("u1"),r1.getRoutingTable().get("u1"));
        assertEquals(routerExpected.get("u2"),r1.getRoutingTable().get("u2"));
        assertEquals(routerExpected.get("p1"),r1.getRoutingTable().get("p1"));
        assertEquals(routerExpected.get("u3"),r1.getRoutingTable().get("u3"));
        assertEquals(routerExpected.get("u4"),r1.getRoutingTable().get("u4"));
        assertEquals(routerExpected.get("p2"),r1.getRoutingTable().get("p2"));
        assertEquals(routerExpected.get("r1"),r1.getRoutingTable().get("r1"));
        assertEquals(routerExpected.get("p3"),r1.getRoutingTable().get("p3"));
        assertEquals(routerExpected.get("u5"),r1.getRoutingTable().get("u5"));
        //check p1 routing table
        assertThat(p1.getRoutingTable(), is(portal1Expected));
        assertThat(p1.getRoutingTable().size(), is(portal1Expected.size()));          
        assertEquals(portal1Expected.containsKey("u5"),p1.getRoutingTable().containsKey("u5"));          
        assertEquals(portal1Expected.containsKey("p3"),p1.getRoutingTable().containsKey("p3"));     
        assertEquals(portal1Expected.containsKey("u1"),p1.getRoutingTable().containsKey("u1"));
        assertEquals(portal1Expected.containsKey("u2"),p1.getRoutingTable().containsKey("u2"));
        assertEquals(portal1Expected.containsKey("p1"),p1.getRoutingTable().containsKey("p1"));
        assertEquals(portal1Expected.containsKey("u3"),p1.getRoutingTable().containsKey("u3"));
        assertEquals(portal1Expected.containsKey("u4"),p1.getRoutingTable().containsKey("u4"));
        assertEquals(portal1Expected.containsKey("p2"),p1.getRoutingTable().containsKey("p2"));
        assertEquals(portal1Expected.containsKey("r1"),p1.getRoutingTable().containsKey("r1"));
        assertEquals(portal1Expected.get("u1"),p1.getRoutingTable().get("u1"));
        assertEquals(portal1Expected.get("u2"),p1.getRoutingTable().get("u2"));
        assertEquals(portal1Expected.get("p1"),p1.getRoutingTable().get("p1"));
        assertEquals(portal1Expected.get("u3"),p1.getRoutingTable().get("u3"));
        assertEquals(portal1Expected.get("u4"),p1.getRoutingTable().get("u4"));
        assertEquals(portal1Expected.get("p2"),p1.getRoutingTable().get("p2"));
        assertEquals(portal1Expected.get("r1"),p1.getRoutingTable().get("r1"));
        assertEquals(portal1Expected.get("p3"),p1.getRoutingTable().get("p3"));
        assertEquals(portal1Expected.get("u5"),p1.getRoutingTable().get("u5"));
        //check p2 routing table
        assertThat(p2.getRoutingTable(), is(portal2Expected));
        assertThat(p2.getRoutingTable().size(), is(portal2Expected.size()));          
        assertEquals(portal2Expected.containsKey("u5"),p2.getRoutingTable().containsKey("u5"));          
        assertEquals(portal2Expected.containsKey("p3"),p2.getRoutingTable().containsKey("p3"));     
        assertEquals(portal2Expected.containsKey("u1"),p2.getRoutingTable().containsKey("u1"));
        assertEquals(portal2Expected.containsKey("u2"),p2.getRoutingTable().containsKey("u2"));
        assertEquals(portal2Expected.containsKey("p1"),p2.getRoutingTable().containsKey("p1"));
        assertEquals(portal2Expected.containsKey("u3"),p2.getRoutingTable().containsKey("u3"));
        assertEquals(portal2Expected.containsKey("u4"),p2.getRoutingTable().containsKey("u4"));
        assertEquals(portal2Expected.containsKey("p2"),p2.getRoutingTable().containsKey("p2"));
        assertEquals(portal2Expected.containsKey("r1"),p2.getRoutingTable().containsKey("r1"));
        assertEquals(portal2Expected.get("u1"),p2.getRoutingTable().get("u1"));
        assertEquals(portal2Expected.get("u2"),p2.getRoutingTable().get("u2"));
        assertEquals(portal2Expected.get("p1"),p2.getRoutingTable().get("p1"));
        assertEquals(portal2Expected.get("u3"),p2.getRoutingTable().get("u3"));
        assertEquals(portal2Expected.get("u4"),p2.getRoutingTable().get("u4"));
        assertEquals(portal2Expected.get("p2"),p2.getRoutingTable().get("p2"));
        assertEquals(portal2Expected.get("r1"),p2.getRoutingTable().get("r1"));
        assertEquals(portal2Expected.get("p3"),p2.getRoutingTable().get("p3"));
        assertEquals(portal2Expected.get("u5"),p2.getRoutingTable().get("u5"));  
        //check p3 routing table
        assertThat(p3.getRoutingTable(), is(portal3Expected));
        assertThat(p3.getRoutingTable().size(), is(portal3Expected.size()));          
        assertEquals(portal3Expected.containsKey("u5"),p3.getRoutingTable().containsKey("u5"));          
        assertEquals(portal3Expected.containsKey("p3"),p3.getRoutingTable().containsKey("p3"));     
        assertEquals(portal3Expected.containsKey("u1"),p3.getRoutingTable().containsKey("u1"));
        assertEquals(portal3Expected.containsKey("u2"),p3.getRoutingTable().containsKey("u2"));
        assertEquals(portal3Expected.containsKey("p1"),p3.getRoutingTable().containsKey("p1"));
        assertEquals(portal3Expected.containsKey("u3"),p3.getRoutingTable().containsKey("u3"));
        assertEquals(portal3Expected.containsKey("u4"),p3.getRoutingTable().containsKey("u4"));
        assertEquals(portal3Expected.containsKey("p2"),p3.getRoutingTable().containsKey("p2"));
        assertEquals(portal3Expected.containsKey("r1"),p3.getRoutingTable().containsKey("r1"));
        assertEquals(portal3Expected.get("u1"),p3.getRoutingTable().get("u1"));
        assertEquals(portal3Expected.get("u2"),p3.getRoutingTable().get("u2"));
        assertEquals(portal3Expected.get("p1"),p3.getRoutingTable().get("p1"));
        assertEquals(portal3Expected.get("u3"),p3.getRoutingTable().get("u3"));
        assertEquals(portal3Expected.get("u4"),p3.getRoutingTable().get("u4"));
        assertEquals(portal3Expected.get("p2"),p3.getRoutingTable().get("p2"));
        assertEquals(portal3Expected.get("r1"),p3.getRoutingTable().get("r1"));
        assertEquals(portal3Expected.get("p3"),p3.getRoutingTable().get("p3"));
        assertEquals(portal3Expected.get("u5"),p3.getRoutingTable().get("u5")); 
    }
    
     /**
     * Test of adding multiple new portals and users
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testAddTwoAgentsAndTwoPortalsHashMapCheckViaRouter() throws InterruptedException
    {
        Portal p3 = new Portal("p3",r1);
        UserAgent u5 = new UserAgent("u5",p3);
        Portal p4 = new Portal("p4",r1);
        UserAgent u6 = new UserAgent("u6",p4);
        Thread.sleep(sleepTime);
        //additional expected outcomes
        routerExpected.put("p3", p3);
        routerExpected.put("u5", p3);
        portal1Expected.put("p3", r1);
        portal1Expected.put("u5", r1);          
        portal2Expected.put("p3", r1);
        portal2Expected.put("u5", r1); 
        routerExpected.put("p4", p4);
        routerExpected.put("u6", p4);
        portal1Expected.put("p4", r1);
        portal1Expected.put("u6", r1);          
        portal2Expected.put("p4", r1);
        portal2Expected.put("u6", r1);  
        
        TreeMap<String, MetaAgent> portal3Expected = new TreeMap<>();
        portal3Expected.put("u1", r1);
        portal3Expected.put("u2", r1);
        portal3Expected.put("p1", r1);
        portal3Expected.put("u3", r1);
        portal3Expected.put("u4", r1);
        portal3Expected.put("p2", r1);
        portal3Expected.put("r1", r1);        
        portal3Expected.put("p3", p3);
        portal3Expected.put("u5", u5); 
        portal3Expected.put("p4", r1); 
        portal3Expected.put("u6", r1); 
        
        TreeMap<String, MetaAgent> portal4Expected = new TreeMap<>();
        portal4Expected.put("u1", r1);
        portal4Expected.put("u2", r1);
        portal4Expected.put("p1", r1);
        portal4Expected.put("u3", r1);
        portal4Expected.put("u4", r1);
        portal4Expected.put("p2", r1);
        portal4Expected.put("r1", r1);        
        portal4Expected.put("p3", r1);
        portal4Expected.put("u5", r1); 
        portal4Expected.put("p4", p4);
        portal4Expected.put("u6", u6); 

        //check r1 routing table
        assertThat(r1.getRoutingTable(), is(routerExpected));
        assertThat(r1.getRoutingTable().size(), is(routerExpected.size()));          
        assertEquals(routerExpected.containsKey("u5"),r1.getRoutingTable().containsKey("u5"));          
        assertEquals(routerExpected.containsKey("p3"),r1.getRoutingTable().containsKey("p3"));     
        assertEquals(routerExpected.containsKey("u1"),r1.getRoutingTable().containsKey("u1"));
        assertEquals(routerExpected.containsKey("u2"),r1.getRoutingTable().containsKey("u2"));
        assertEquals(routerExpected.containsKey("p1"),r1.getRoutingTable().containsKey("p1"));
        assertEquals(routerExpected.containsKey("u3"),r1.getRoutingTable().containsKey("u3"));
        assertEquals(routerExpected.containsKey("u4"),r1.getRoutingTable().containsKey("u4"));
        assertEquals(routerExpected.containsKey("p2"),r1.getRoutingTable().containsKey("p2"));
        assertEquals(routerExpected.containsKey("r1"),r1.getRoutingTable().containsKey("r1"));
        assertEquals(routerExpected.get("u1"),r1.getRoutingTable().get("u1"));
        assertEquals(routerExpected.get("u2"),r1.getRoutingTable().get("u2"));
        assertEquals(routerExpected.get("p1"),r1.getRoutingTable().get("p1"));
        assertEquals(routerExpected.get("u3"),r1.getRoutingTable().get("u3"));
        assertEquals(routerExpected.get("u4"),r1.getRoutingTable().get("u4"));
        assertEquals(routerExpected.get("p2"),r1.getRoutingTable().get("p2"));
        assertEquals(routerExpected.get("r1"),r1.getRoutingTable().get("r1"));
        assertEquals(routerExpected.get("p3"),r1.getRoutingTable().get("p3"));
        assertEquals(routerExpected.get("u5"),r1.getRoutingTable().get("u5"));
        //check p1 routing table
        assertThat(p1.getRoutingTable(), is(portal1Expected));
        assertThat(p1.getRoutingTable().size(), is(portal1Expected.size()));          
        assertEquals(portal1Expected.containsKey("u5"),p1.getRoutingTable().containsKey("u5"));          
        assertEquals(portal1Expected.containsKey("p3"),p1.getRoutingTable().containsKey("p3"));     
        assertEquals(portal1Expected.containsKey("u1"),p1.getRoutingTable().containsKey("u1"));
        assertEquals(portal1Expected.containsKey("u2"),p1.getRoutingTable().containsKey("u2"));
        assertEquals(portal1Expected.containsKey("p1"),p1.getRoutingTable().containsKey("p1"));
        assertEquals(portal1Expected.containsKey("u3"),p1.getRoutingTable().containsKey("u3"));
        assertEquals(portal1Expected.containsKey("u4"),p1.getRoutingTable().containsKey("u4"));
        assertEquals(portal1Expected.containsKey("p2"),p1.getRoutingTable().containsKey("p2"));
        assertEquals(portal1Expected.containsKey("r1"),p1.getRoutingTable().containsKey("r1"));
        assertEquals(portal1Expected.get("u1"),p1.getRoutingTable().get("u1"));
        assertEquals(portal1Expected.get("u2"),p1.getRoutingTable().get("u2"));
        assertEquals(portal1Expected.get("p1"),p1.getRoutingTable().get("p1"));
        assertEquals(portal1Expected.get("u3"),p1.getRoutingTable().get("u3"));
        assertEquals(portal1Expected.get("u4"),p1.getRoutingTable().get("u4"));
        assertEquals(portal1Expected.get("p2"),p1.getRoutingTable().get("p2"));
        assertEquals(portal1Expected.get("r1"),p1.getRoutingTable().get("r1"));
        assertEquals(portal1Expected.get("p3"),p1.getRoutingTable().get("p3"));
        assertEquals(portal1Expected.get("u5"),p1.getRoutingTable().get("u5"));
        //check p2 routing table
        assertThat(p2.getRoutingTable(), is(portal2Expected));
        assertThat(p2.getRoutingTable().size(), is(portal2Expected.size()));          
        assertEquals(portal2Expected.containsKey("u5"),p2.getRoutingTable().containsKey("u5"));          
        assertEquals(portal2Expected.containsKey("p3"),p2.getRoutingTable().containsKey("p3"));     
        assertEquals(portal2Expected.containsKey("u1"),p2.getRoutingTable().containsKey("u1"));
        assertEquals(portal2Expected.containsKey("u2"),p2.getRoutingTable().containsKey("u2"));
        assertEquals(portal2Expected.containsKey("p1"),p2.getRoutingTable().containsKey("p1"));
        assertEquals(portal2Expected.containsKey("u3"),p2.getRoutingTable().containsKey("u3"));
        assertEquals(portal2Expected.containsKey("u4"),p2.getRoutingTable().containsKey("u4"));
        assertEquals(portal2Expected.containsKey("p2"),p2.getRoutingTable().containsKey("p2"));
        assertEquals(portal2Expected.containsKey("r1"),p2.getRoutingTable().containsKey("r1"));
        assertEquals(portal2Expected.get("u1"),p2.getRoutingTable().get("u1"));
        assertEquals(portal2Expected.get("u2"),p2.getRoutingTable().get("u2"));
        assertEquals(portal2Expected.get("p1"),p2.getRoutingTable().get("p1"));
        assertEquals(portal2Expected.get("u3"),p2.getRoutingTable().get("u3"));
        assertEquals(portal2Expected.get("u4"),p2.getRoutingTable().get("u4"));
        assertEquals(portal2Expected.get("p2"),p2.getRoutingTable().get("p2"));
        assertEquals(portal2Expected.get("r1"),p2.getRoutingTable().get("r1"));
        assertEquals(portal2Expected.get("p3"),p2.getRoutingTable().get("p3"));
        assertEquals(portal2Expected.get("u5"),p2.getRoutingTable().get("u5"));  
        //check p3 routing table
        assertThat(p3.getRoutingTable(), is(portal3Expected));
        assertThat(p3.getRoutingTable().size(), is(portal3Expected.size()));          
        assertEquals(portal3Expected.containsKey("u5"),p3.getRoutingTable().containsKey("u5"));          
        assertEquals(portal3Expected.containsKey("p3"),p3.getRoutingTable().containsKey("p3"));     
        assertEquals(portal3Expected.containsKey("u1"),p3.getRoutingTable().containsKey("u1"));
        assertEquals(portal3Expected.containsKey("u2"),p3.getRoutingTable().containsKey("u2"));
        assertEquals(portal3Expected.containsKey("p1"),p3.getRoutingTable().containsKey("p1"));
        assertEquals(portal3Expected.containsKey("u3"),p3.getRoutingTable().containsKey("u3"));
        assertEquals(portal3Expected.containsKey("u4"),p3.getRoutingTable().containsKey("u4"));
        assertEquals(portal3Expected.containsKey("p2"),p3.getRoutingTable().containsKey("p2"));
        assertEquals(portal3Expected.containsKey("r1"),p3.getRoutingTable().containsKey("r1"));
        assertEquals(portal3Expected.get("u1"),p3.getRoutingTable().get("u1"));
        assertEquals(portal3Expected.get("u2"),p3.getRoutingTable().get("u2"));
        assertEquals(portal3Expected.get("p1"),p3.getRoutingTable().get("p1"));
        assertEquals(portal3Expected.get("u3"),p3.getRoutingTable().get("u3"));
        assertEquals(portal3Expected.get("u4"),p3.getRoutingTable().get("u4"));
        assertEquals(portal3Expected.get("p2"),p3.getRoutingTable().get("p2"));
        assertEquals(portal3Expected.get("r1"),p3.getRoutingTable().get("r1"));
        assertEquals(portal3Expected.get("p3"),p3.getRoutingTable().get("p3"));
        assertEquals(portal3Expected.get("u5"),p3.getRoutingTable().get("u5")); 
        //check p3 routing table
        assertThat(p4.getRoutingTable(), is(portal4Expected));
        assertThat(p4.getRoutingTable().size(), is(portal4Expected.size()));          
        assertEquals(portal4Expected.containsKey("u5"),p4.getRoutingTable().containsKey("u5"));          
        assertEquals(portal4Expected.containsKey("p3"),p4.getRoutingTable().containsKey("p3"));     
        assertEquals(portal4Expected.containsKey("u1"),p4.getRoutingTable().containsKey("u1"));
        assertEquals(portal4Expected.containsKey("u2"),p4.getRoutingTable().containsKey("u2"));
        assertEquals(portal4Expected.containsKey("p1"),p4.getRoutingTable().containsKey("p1"));
        assertEquals(portal4Expected.containsKey("u3"),p4.getRoutingTable().containsKey("u3"));
        assertEquals(portal4Expected.containsKey("u4"),p4.getRoutingTable().containsKey("u4"));
        assertEquals(portal4Expected.containsKey("p2"),p4.getRoutingTable().containsKey("p2"));
        assertEquals(portal4Expected.containsKey("r1"),p4.getRoutingTable().containsKey("r1"));
        assertEquals(portal4Expected.get("u1"),p4.getRoutingTable().get("u1"));
        assertEquals(portal4Expected.get("u2"),p4.getRoutingTable().get("u2"));
        assertEquals(portal4Expected.get("p1"),p4.getRoutingTable().get("p1"));
        assertEquals(portal4Expected.get("u3"),p4.getRoutingTable().get("u3"));
        assertEquals(portal4Expected.get("u4"),p4.getRoutingTable().get("u4"));
        assertEquals(portal4Expected.get("p2"),p4.getRoutingTable().get("p2"));
        assertEquals(portal4Expected.get("r1"),p4.getRoutingTable().get("r1"));
        assertEquals(portal4Expected.get("p3"),p4.getRoutingTable().get("p3"));
        assertEquals(portal4Expected.get("u5"),p4.getRoutingTable().get("u5")); 
    }

    /**
     * Test of adding new portal to router and new user to portal
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testAddAgentHashMapCheckDifferentRegistrationOrder() throws InterruptedException 
    {
        Portal p3 = new Portal("p3",r1);
        UserAgent u5 = new UserAgent("u5",null);  
        p3.addAgent(u5);
        Thread.sleep(sleepTime);
        //additional expected outcomes
        portal1Expected.put("p3", r1);
        portal1Expected.put("u5", r1);
        portal2Expected.put("p3", r1);
        portal2Expected.put("u5", r1);
        routerExpected.put("p3", p3);
        routerExpected.put("u5", p3);
        TreeMap<String, MetaAgent> portal3Expected = new TreeMap<>();
        portal3Expected.put("p3", p3);
        portal3Expected.put("u5", u5); 
        portal3Expected.put("r1", r1); 
        portal3Expected.put("p1", r1); 
        portal3Expected.put("p2", r1); 
        portal3Expected.put("u1", r1); 
        portal3Expected.put("u2", r1); 
        portal3Expected.put("u3", r1); 
        portal3Expected.put("u4", r1); 
        
        //check r1 routing table
        assertThat(r1.getRoutingTable(), is(routerExpected));
        assertThat(r1.getRoutingTable().size(), is(routerExpected.size()));          
        assertEquals(routerExpected.containsKey("u5"),r1.getRoutingTable().containsKey("u5"));          
        assertEquals(routerExpected.containsKey("p3"),r1.getRoutingTable().containsKey("p3"));     
        assertEquals(routerExpected.containsKey("u1"),r1.getRoutingTable().containsKey("u1"));
        assertEquals(routerExpected.containsKey("u2"),r1.getRoutingTable().containsKey("u2"));
        assertEquals(routerExpected.containsKey("p1"),r1.getRoutingTable().containsKey("p1"));
        assertEquals(routerExpected.containsKey("u3"),r1.getRoutingTable().containsKey("u3"));
        assertEquals(routerExpected.containsKey("u4"),r1.getRoutingTable().containsKey("u4"));
        assertEquals(routerExpected.containsKey("p2"),r1.getRoutingTable().containsKey("p2"));
        assertEquals(routerExpected.containsKey("r1"),r1.getRoutingTable().containsKey("r1"));
        assertEquals(routerExpected.get("u1"),r1.getRoutingTable().get("u1"));
        assertEquals(routerExpected.get("u2"),r1.getRoutingTable().get("u2"));
        assertEquals(routerExpected.get("p1"),r1.getRoutingTable().get("p1"));
        assertEquals(routerExpected.get("u3"),r1.getRoutingTable().get("u3"));
        assertEquals(routerExpected.get("u4"),r1.getRoutingTable().get("u4"));
        assertEquals(routerExpected.get("p2"),r1.getRoutingTable().get("p2"));
        assertEquals(routerExpected.get("r1"),r1.getRoutingTable().get("r1"));
        assertEquals(routerExpected.get("p3"),r1.getRoutingTable().get("p3"));
        assertEquals(routerExpected.get("u5"),r1.getRoutingTable().get("u5"));
        //check p1 routing table
        assertThat(p1.getRoutingTable(), is(portal1Expected));
        assertThat(p1.getRoutingTable().size(), is(portal1Expected.size()));          
        assertEquals(portal1Expected.containsKey("u5"),p1.getRoutingTable().containsKey("u5"));          
        assertEquals(portal1Expected.containsKey("p3"),p1.getRoutingTable().containsKey("p3"));     
        assertEquals(portal1Expected.containsKey("u1"),p1.getRoutingTable().containsKey("u1"));
        assertEquals(portal1Expected.containsKey("u2"),p1.getRoutingTable().containsKey("u2"));
        assertEquals(portal1Expected.containsKey("p1"),p1.getRoutingTable().containsKey("p1"));
        assertEquals(portal1Expected.containsKey("u3"),p1.getRoutingTable().containsKey("u3"));
        assertEquals(portal1Expected.containsKey("u4"),p1.getRoutingTable().containsKey("u4"));
        assertEquals(portal1Expected.containsKey("p2"),p1.getRoutingTable().containsKey("p2"));
        assertEquals(portal1Expected.containsKey("r1"),p1.getRoutingTable().containsKey("r1"));
        assertEquals(portal1Expected.get("u1"),p1.getRoutingTable().get("u1"));
        assertEquals(portal1Expected.get("u2"),p1.getRoutingTable().get("u2"));
        assertEquals(portal1Expected.get("p1"),p1.getRoutingTable().get("p1"));
        assertEquals(portal1Expected.get("u3"),p1.getRoutingTable().get("u3"));
        assertEquals(portal1Expected.get("u4"),p1.getRoutingTable().get("u4"));
        assertEquals(portal1Expected.get("p2"),p1.getRoutingTable().get("p2"));
        assertEquals(portal1Expected.get("r1"),p1.getRoutingTable().get("r1"));
        assertEquals(portal1Expected.get("p3"),p1.getRoutingTable().get("p3"));
        assertEquals(portal1Expected.get("u5"),p1.getRoutingTable().get("u5"));
        //check p2 routing table
        assertThat(p2.getRoutingTable(), is(portal2Expected));
        assertThat(p2.getRoutingTable().size(), is(portal2Expected.size()));          
        assertEquals(portal2Expected.containsKey("u5"),p2.getRoutingTable().containsKey("u5"));          
        assertEquals(portal2Expected.containsKey("p3"),p2.getRoutingTable().containsKey("p3"));     
        assertEquals(portal2Expected.containsKey("u1"),p2.getRoutingTable().containsKey("u1"));
        assertEquals(portal2Expected.containsKey("u2"),p2.getRoutingTable().containsKey("u2"));
        assertEquals(portal2Expected.containsKey("p1"),p2.getRoutingTable().containsKey("p1"));
        assertEquals(portal2Expected.containsKey("u3"),p2.getRoutingTable().containsKey("u3"));
        assertEquals(portal2Expected.containsKey("u4"),p2.getRoutingTable().containsKey("u4"));
        assertEquals(portal2Expected.containsKey("p2"),p2.getRoutingTable().containsKey("p2"));
        assertEquals(portal2Expected.containsKey("r1"),p2.getRoutingTable().containsKey("r1"));
        assertEquals(portal2Expected.get("u1"),p2.getRoutingTable().get("u1"));
        assertEquals(portal2Expected.get("u2"),p2.getRoutingTable().get("u2"));
        assertEquals(portal2Expected.get("p1"),p2.getRoutingTable().get("p1"));
        assertEquals(portal2Expected.get("u3"),p2.getRoutingTable().get("u3"));
        assertEquals(portal2Expected.get("u4"),p2.getRoutingTable().get("u4"));
        assertEquals(portal2Expected.get("p2"),p2.getRoutingTable().get("p2"));
        assertEquals(portal2Expected.get("r1"),p2.getRoutingTable().get("r1"));
        assertEquals(portal2Expected.get("p3"),p2.getRoutingTable().get("p3"));
        assertEquals(portal2Expected.get("u5"),p2.getRoutingTable().get("u5"));  
        //check p3 routing table
        assertThat(p3.getRoutingTable(), is(portal3Expected));
        assertThat(p3.getRoutingTable().size(), is(portal3Expected.size()));          
        assertEquals(portal3Expected.containsKey("u5"),p3.getRoutingTable().containsKey("u5"));          
        assertEquals(portal3Expected.containsKey("p3"),p3.getRoutingTable().containsKey("p3"));     
        assertEquals(portal3Expected.containsKey("u1"),p3.getRoutingTable().containsKey("u1"));
        assertEquals(portal3Expected.containsKey("u2"),p3.getRoutingTable().containsKey("u2"));
        assertEquals(portal3Expected.containsKey("p1"),p3.getRoutingTable().containsKey("p1"));
        assertEquals(portal3Expected.containsKey("u3"),p3.getRoutingTable().containsKey("u3"));
        assertEquals(portal3Expected.containsKey("u4"),p3.getRoutingTable().containsKey("u4"));
        assertEquals(portal3Expected.containsKey("p2"),p3.getRoutingTable().containsKey("p2"));
        assertEquals(portal3Expected.containsKey("r1"),p3.getRoutingTable().containsKey("r1"));
        assertEquals(portal3Expected.get("u1"),p3.getRoutingTable().get("u1"));
        assertEquals(portal3Expected.get("u2"),p3.getRoutingTable().get("u2"));
        assertEquals(portal3Expected.get("p1"),p3.getRoutingTable().get("p1"));
        assertEquals(portal3Expected.get("u3"),p3.getRoutingTable().get("u3"));
        assertEquals(portal3Expected.get("u4"),p3.getRoutingTable().get("u4"));
        assertEquals(portal3Expected.get("p2"),p3.getRoutingTable().get("p2"));
        assertEquals(portal3Expected.get("r1"),p3.getRoutingTable().get("r1"));
        assertEquals(portal3Expected.get("p3"),p3.getRoutingTable().get("p3"));
        assertEquals(portal3Expected.get("u5"),p3.getRoutingTable().get("u5")); 
    }    
    
 
    /**
     * Test of removeAgent method, of class Router.
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testRemoveUserHashMapCheckViaRouter() throws InterruptedException 
    {
        r1.removeAgent("u4");            
        removeKeyFromExpected("u4");
        Thread.sleep(sleepTime);
        //check r1 routing table
        assertThat(r1.getRoutingTable(), is(routerExpected));
        assertThat(r1.getRoutingTable().size(), is(routerExpected.size()));      
        assertEquals(routerExpected.containsKey("r1"),r1.getRoutingTable().containsKey("r1"));
        assertEquals(routerExpected.containsKey("p1"),r1.getRoutingTable().containsKey("p1"));
        assertEquals(routerExpected.containsKey("p2"),r1.getRoutingTable().containsKey("p2"));             
        assertEquals(routerExpected.containsKey("u1"),r1.getRoutingTable().containsKey("u1"));
        assertEquals(routerExpected.containsKey("u2"),r1.getRoutingTable().containsKey("u2"));
        assertEquals(routerExpected.containsKey("u3"),r1.getRoutingTable().containsKey("u3"));
        assertEquals(routerExpected.get("r1"),r1.getRoutingTable().get("r1"));
        assertEquals(routerExpected.get("p1"),r1.getRoutingTable().get("p1"));        
        assertEquals(routerExpected.get("p2"),r1.getRoutingTable().get("p2"));               
        assertEquals(routerExpected.get("u1"),r1.getRoutingTable().get("u1"));
        assertEquals(routerExpected.get("u2"),r1.getRoutingTable().get("u2"));
        assertEquals(routerExpected.get("u3"),r1.getRoutingTable().get("u3"));
        assertEquals(routerExpected.get("u4"),r1.getRoutingTable().get("u4")); 
        //check p1 routing table
        assertThat(p1.getRoutingTable(), is(portal1Expected));
        assertThat(p1.getRoutingTable().size(), is(portal1Expected.size()));      
        assertEquals(portal1Expected.containsKey("r1"),p1.getRoutingTable().containsKey("r1"));
        assertEquals(portal1Expected.containsKey("p1"),p1.getRoutingTable().containsKey("p1"));
        assertEquals(portal1Expected.containsKey("p2"),p1.getRoutingTable().containsKey("p2"));             
        assertEquals(portal1Expected.containsKey("u1"),p1.getRoutingTable().containsKey("u1"));
        assertEquals(portal1Expected.containsKey("u2"),p1.getRoutingTable().containsKey("u2"));
        assertEquals(portal1Expected.containsKey("u3"),p1.getRoutingTable().containsKey("u3"));
        assertEquals(portal1Expected.get("r1"),p1.getRoutingTable().get("r1"));
        assertEquals(portal1Expected.get("p1"),p1.getRoutingTable().get("p1"));        
        assertEquals(portal1Expected.get("p2"),p1.getRoutingTable().get("p2"));               
        assertEquals(portal1Expected.get("u1"),p1.getRoutingTable().get("u1"));
        assertEquals(portal1Expected.get("u2"),p1.getRoutingTable().get("u2"));
        assertEquals(portal1Expected.get("u3"),p1.getRoutingTable().get("u3"));
        assertEquals(portal1Expected.get("u4"),p1.getRoutingTable().get("u4"));
        //check p2 routing table
        assertThat(p2.getRoutingTable(), is(portal2Expected));
        assertThat(p2.getRoutingTable().size(), is(portal2Expected.size()));      
        assertEquals(portal2Expected.containsKey("r1"),p2.getRoutingTable().containsKey("r1"));
        assertEquals(portal2Expected.containsKey("p1"),p2.getRoutingTable().containsKey("p1"));
        assertEquals(portal2Expected.containsKey("p2"),p2.getRoutingTable().containsKey("p2"));             
        assertEquals(portal2Expected.containsKey("u1"),p2.getRoutingTable().containsKey("u1"));
        assertEquals(portal2Expected.containsKey("u2"),p2.getRoutingTable().containsKey("u2"));
        assertEquals(portal2Expected.containsKey("u3"),p2.getRoutingTable().containsKey("u3"));
        assertEquals(portal2Expected.get("r1"),p2.getRoutingTable().get("r1"));
        assertEquals(portal2Expected.get("p1"),p2.getRoutingTable().get("p1"));        
        assertEquals(portal2Expected.get("p2"),p2.getRoutingTable().get("p2"));               
        assertEquals(portal2Expected.get("u1"),p2.getRoutingTable().get("u1"));
        assertEquals(portal2Expected.get("u2"),p2.getRoutingTable().get("u2"));
        assertEquals(portal2Expected.get("u3"),p2.getRoutingTable().get("u3"));
        assertEquals(portal2Expected.get("u4"),p2.getRoutingTable().get("u4"));     
    }   
    
    /**
     * Test of removeAgent method, of class Portal.
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testRemoveUserHashMapCheckViaPortal() throws InterruptedException 
    {
        p1.removeAgent("u4");            
        removeKeyFromExpected("u4");
        Thread.sleep(sleepTime);
        //check r1 routing table
        assertThat(r1.getRoutingTable(), is(routerExpected));
        assertThat(r1.getRoutingTable().size(), is(routerExpected.size()));      
        assertEquals(routerExpected.containsKey("r1"),r1.getRoutingTable().containsKey("r1"));
        assertEquals(routerExpected.containsKey("p1"),r1.getRoutingTable().containsKey("p1"));
        assertEquals(routerExpected.containsKey("p2"),r1.getRoutingTable().containsKey("p2"));             
        assertEquals(routerExpected.containsKey("u1"),r1.getRoutingTable().containsKey("u1"));
        assertEquals(routerExpected.containsKey("u2"),r1.getRoutingTable().containsKey("u2"));
        assertEquals(routerExpected.containsKey("u3"),r1.getRoutingTable().containsKey("u3"));
        assertEquals(routerExpected.containsKey("u4"),r1.getRoutingTable().containsKey("u4"));
        assertEquals(routerExpected.get("r1"),r1.getRoutingTable().get("r1"));
        assertEquals(routerExpected.get("p1"),r1.getRoutingTable().get("p1"));        
        assertEquals(routerExpected.get("p2"),r1.getRoutingTable().get("p2"));               
        assertEquals(routerExpected.get("u1"),r1.getRoutingTable().get("u1"));
        assertEquals(routerExpected.get("u2"),r1.getRoutingTable().get("u2"));
        assertEquals(routerExpected.get("u3"),r1.getRoutingTable().get("u3"));
        assertEquals(routerExpected.get("u4"),r1.getRoutingTable().get("u4")); 
        //check p1 routing table
        assertThat(p1.getRoutingTable(), is(portal1Expected));
        assertThat(p1.getRoutingTable().size(), is(portal1Expected.size()));      
        assertEquals(portal1Expected.containsKey("r1"),p1.getRoutingTable().containsKey("r1"));
        assertEquals(portal1Expected.containsKey("p1"),p1.getRoutingTable().containsKey("p1"));
        assertEquals(portal1Expected.containsKey("p2"),p1.getRoutingTable().containsKey("p2"));             
        assertEquals(portal1Expected.containsKey("u1"),p1.getRoutingTable().containsKey("u1"));
        assertEquals(portal1Expected.containsKey("u2"),p1.getRoutingTable().containsKey("u2"));
        assertEquals(portal1Expected.containsKey("u3"),p1.getRoutingTable().containsKey("u3"));
        assertEquals(portal1Expected.containsKey("u4"),p1.getRoutingTable().containsKey("u4"));
        assertEquals(portal1Expected.get("r1"),p1.getRoutingTable().get("r1"));
        assertEquals(portal1Expected.get("p1"),p1.getRoutingTable().get("p1"));        
        assertEquals(portal1Expected.get("p2"),p1.getRoutingTable().get("p2"));               
        assertEquals(portal1Expected.get("u1"),p1.getRoutingTable().get("u1"));
        assertEquals(portal1Expected.get("u2"),p1.getRoutingTable().get("u2"));
        assertEquals(portal1Expected.get("u3"),p1.getRoutingTable().get("u3"));
        assertEquals(portal1Expected.get("u4"),p1.getRoutingTable().get("u4"));
        //check p2 routing table
        assertThat(p2.getRoutingTable(), is(portal2Expected));
        assertThat(p2.getRoutingTable().size(), is(portal2Expected.size()));      
        assertEquals(portal2Expected.containsKey("r1"),p2.getRoutingTable().containsKey("r1"));
        assertEquals(portal2Expected.containsKey("p1"),p2.getRoutingTable().containsKey("p1"));
        assertEquals(portal2Expected.containsKey("p2"),p2.getRoutingTable().containsKey("p2"));             
        assertEquals(portal2Expected.containsKey("u1"),p2.getRoutingTable().containsKey("u1"));
        assertEquals(portal2Expected.containsKey("u2"),p2.getRoutingTable().containsKey("u2"));
        assertEquals(portal2Expected.containsKey("u3"),p2.getRoutingTable().containsKey("u3"));
        assertEquals(portal2Expected.containsKey("u4"),p2.getRoutingTable().containsKey("u4"));
        assertEquals(portal2Expected.get("r1"),p2.getRoutingTable().get("r1"));
        assertEquals(portal2Expected.get("p1"),p2.getRoutingTable().get("p1"));        
        assertEquals(portal2Expected.get("p2"),p2.getRoutingTable().get("p2"));               
        assertEquals(portal2Expected.get("u1"),p2.getRoutingTable().get("u1"));
        assertEquals(portal2Expected.get("u2"),p2.getRoutingTable().get("u2"));
        assertEquals(portal2Expected.get("u3"),p2.getRoutingTable().get("u3"));
        assertEquals(portal2Expected.get("u4"),p2.getRoutingTable().get("u4"));     
    }  
    
    /**
     * Test of removeAgent method, of class Router.
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testRemovePortalHashMapCheck() throws InterruptedException 
    {
        System.setOut(System.out);
        r1.removeAgent("p2"); 
        removeKeyFromExpected("p2");
        removeKeyFromExpected("u3");
        removeKeyFromExpected("u4");
        Thread.sleep(sleepTime + 5000);
        //check r1 routing table
        printRoutingTable(r1);
        printRoutingTable(p1);
        printRoutingTable(p2);
        assertThat(r1.getRoutingTable(), is(routerExpected));
        assertThat(r1.getRoutingTable().size(), is(routerExpected.size()));      
        assertEquals(routerExpected.containsKey("r1"),r1.getRoutingTable().containsKey("r1"));
        assertEquals(routerExpected.containsKey("p1"),r1.getRoutingTable().containsKey("p1"));
        assertEquals(routerExpected.containsKey("p2"),r1.getRoutingTable().containsKey("p2"));             
        assertEquals(routerExpected.containsKey("u1"),r1.getRoutingTable().containsKey("u1"));
        assertEquals(routerExpected.containsKey("u2"),r1.getRoutingTable().containsKey("u2"));
        assertEquals(routerExpected.containsKey("u3"),r1.getRoutingTable().containsKey("u3"));
        assertEquals(routerExpected.containsKey("u4"),r1.getRoutingTable().containsKey("u4"));
        assertEquals(routerExpected.get("r1"),r1.getRoutingTable().get("r1"));
        assertEquals(routerExpected.get("p1"),r1.getRoutingTable().get("p1"));        
        assertEquals(routerExpected.get("p2"),r1.getRoutingTable().get("p2"));               
        assertEquals(routerExpected.get("u1"),r1.getRoutingTable().get("u1"));
        assertEquals(routerExpected.get("u2"),r1.getRoutingTable().get("u2"));
        assertEquals(routerExpected.get("u3"),r1.getRoutingTable().get("u3"));
        assertEquals(routerExpected.get("u4"),r1.getRoutingTable().get("u4"));
        //check p1 routing table
        assertThat(p1.getRoutingTable(), is(portal1Expected));
        assertThat(p1.getRoutingTable().size(), is(portal1Expected.size()));      
        assertEquals(portal1Expected.containsKey("r1"),p1.getRoutingTable().containsKey("r1"));
        assertEquals(portal1Expected.containsKey("p1"),p1.getRoutingTable().containsKey("p1"));
        assertEquals(portal1Expected.containsKey("p2"),p1.getRoutingTable().containsKey("p2"));             
        assertEquals(portal1Expected.containsKey("u1"),p1.getRoutingTable().containsKey("u1"));
        assertEquals(portal1Expected.containsKey("u2"),p1.getRoutingTable().containsKey("u2"));
        assertEquals(portal1Expected.containsKey("u3"),p1.getRoutingTable().containsKey("u3"));
        assertEquals(portal1Expected.get("r1"),p1.getRoutingTable().get("r1"));
        assertEquals(portal1Expected.get("p1"),p1.getRoutingTable().get("p1"));        
        assertEquals(portal1Expected.get("p2"),p1.getRoutingTable().get("p2"));               
        assertEquals(portal1Expected.get("u1"),p1.getRoutingTable().get("u1"));
        assertEquals(portal1Expected.get("u2"),p1.getRoutingTable().get("u2"));
        assertEquals(portal1Expected.get("u3"),p1.getRoutingTable().get("u3"));
        assertEquals(portal1Expected.get("u4"),p1.getRoutingTable().get("u4"));       
        //check p2 routing table
        assertThat(p2.getRoutingTable(), is(portal2Expected));
        assertThat(p2.getRoutingTable().size(), is(portal2Expected.size()));      
        assertEquals(portal2Expected.containsKey("r1"),p2.getRoutingTable().containsKey("r1"));
        assertEquals(portal2Expected.containsKey("p1"),p2.getRoutingTable().containsKey("p1"));
        assertEquals(portal2Expected.containsKey("p2"),p2.getRoutingTable().containsKey("p2"));             
        assertEquals(portal2Expected.containsKey("u1"),p2.getRoutingTable().containsKey("u1"));
        assertEquals(portal2Expected.containsKey("u2"),p2.getRoutingTable().containsKey("u2"));
        assertEquals(portal2Expected.containsKey("u3"),p2.getRoutingTable().containsKey("u3"));
        assertEquals(portal2Expected.get("r1"),p2.getRoutingTable().get("r1"));
        assertEquals(portal2Expected.get("p1"),p2.getRoutingTable().get("p1"));        
        assertEquals(portal2Expected.get("p2"),p2.getRoutingTable().get("p2"));               
        assertEquals(portal2Expected.get("u1"),p2.getRoutingTable().get("u1"));
        assertEquals(portal2Expected.get("u2"),p2.getRoutingTable().get("u2"));
        assertEquals(portal2Expected.get("u3"),p2.getRoutingTable().get("u3"));
        assertEquals(portal2Expected.get("u4"),p2.getRoutingTable().get("u4"));       
        assertFalse(u1.getPortal() == null);
        assertFalse(u2.getPortal() == null);
        assertEquals(null,u3.getPortal());
        assertEquals(null,u4.getPortal()); 
    }       

    /**
     * Test of msgHandler method, of class Router.
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    @Test
    public void testMsgHandler() throws InterruptedException, IOException
    {
        String expected = "r1: test message Recipient: u2"+ 
                System.getProperty("line.separator");
        MsgData message = new MsgData("r1","u2","test message");
        r1.msgHandler(message);
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 32));  
    }
    
    /**
     * Test of msgHandler (null) method, of class Router.
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    @Test (expected = NullPointerException.class)
    public void testMsgHandlerNullMessage() throws InterruptedException, IOException
    {
        r1.msgHandler(null);
    }
    
    /**
     * Test of msgHandler (null) method, of class Router.
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    @Test (expected = NullPointerException.class)
    public void testMsgHandlerNullVariables() throws InterruptedException, IOException
    {
        String expected = "Null message"+ 
                System.getProperty("line.separator");
        String unambiguous = null;
        MsgData message = new MsgData(null,null,unambiguous);
        r1.msgHandler(message);
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 32));  
    }    
    
    /**
     * Test of msgHandler method, of class Router.
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    @Test
    public void testAddAgentMsgHandler() throws InterruptedException, IOException 
    {
        Portal p4 = new Portal("p4",r1);
        UserAgent u6 = new UserAgent("u6",p4);   
        String expected = "r1: test message Recipient: u6"+ 
                System.getProperty("line.separator");
        MsgData message = new MsgData("r1","u6","test message");
        r1.msgHandler(message);
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 32));       
    }
    
    /**
     * Test of addAgent (null) method, of class Router.
     */
    @Test (expected = NullPointerException.class)
    public void testAddNullAgent()
    {
        r1.addAgent(null);
        //check r1 routing table
        assertThat(r1.getRoutingTable(), is(routerExpected));
        assertThat(r1.getRoutingTable().size(), is(routerExpected.size()));      
        assertEquals(routerExpected.containsKey("r1"),r1.getRoutingTable().containsKey("r1"));
        assertEquals(routerExpected.containsKey("p1"),r1.getRoutingTable().containsKey("p1"));
        assertEquals(routerExpected.containsKey("p2"),r1.getRoutingTable().containsKey("p2"));             
        assertEquals(routerExpected.containsKey("u1"),r1.getRoutingTable().containsKey("u1"));
        assertEquals(routerExpected.containsKey("u2"),r1.getRoutingTable().containsKey("u2"));
        assertEquals(routerExpected.containsKey("u3"),r1.getRoutingTable().containsKey("u3"));
        assertEquals(routerExpected.containsKey("u4"),r1.getRoutingTable().containsKey("u4"));
        assertEquals(routerExpected.get("r1"),r1.getRoutingTable().get("r1"));
        assertEquals(routerExpected.get("p1"),r1.getRoutingTable().get("p1"));        
        assertEquals(routerExpected.get("p2"),r1.getRoutingTable().get("p2"));               
        assertEquals(routerExpected.get("u1"),r1.getRoutingTable().get("u1"));
        assertEquals(routerExpected.get("u2"),r1.getRoutingTable().get("u2"));
        assertEquals(routerExpected.get("u3"),r1.getRoutingTable().get("u3"));
        assertEquals(routerExpected.get("u4"),r1.getRoutingTable().get("u4"));
        //check p1 routing table
        assertThat(p1.getRoutingTable(), is(portal1Expected));
        assertThat(p1.getRoutingTable().size(), is(portal1Expected.size()));      
        assertEquals(portal1Expected.containsKey("r1"),p1.getRoutingTable().containsKey("r1"));
        assertEquals(portal1Expected.containsKey("p1"),p1.getRoutingTable().containsKey("p1"));
        assertEquals(portal1Expected.containsKey("p2"),p1.getRoutingTable().containsKey("p2"));             
        assertEquals(portal1Expected.containsKey("u1"),p1.getRoutingTable().containsKey("u1"));
        assertEquals(portal1Expected.containsKey("u2"),p1.getRoutingTable().containsKey("u2"));
        assertEquals(portal1Expected.containsKey("u3"),p1.getRoutingTable().containsKey("u3"));
        assertEquals(portal1Expected.get("r1"),p1.getRoutingTable().get("r1"));
        assertEquals(portal1Expected.get("p1"),p1.getRoutingTable().get("p1"));        
        assertEquals(portal1Expected.get("p2"),p1.getRoutingTable().get("p2"));               
        assertEquals(portal1Expected.get("u1"),p1.getRoutingTable().get("u1"));
        assertEquals(portal1Expected.get("u2"),p1.getRoutingTable().get("u2"));
        assertEquals(portal1Expected.get("u3"),p1.getRoutingTable().get("u3"));
        assertEquals(portal1Expected.get("u4"),p1.getRoutingTable().get("u4"));       
        //check p2 routing table
        assertThat(p2.getRoutingTable(), is(portal2Expected));
        assertThat(p2.getRoutingTable().size(), is(portal2Expected.size()));      
        assertEquals(portal2Expected.containsKey("r1"),p2.getRoutingTable().containsKey("r1"));
        assertEquals(portal2Expected.containsKey("p1"),p2.getRoutingTable().containsKey("p1"));
        assertEquals(portal2Expected.containsKey("p2"),p2.getRoutingTable().containsKey("p2"));             
        assertEquals(portal2Expected.containsKey("u1"),p2.getRoutingTable().containsKey("u1"));
        assertEquals(portal2Expected.containsKey("u2"),p2.getRoutingTable().containsKey("u2"));
        assertEquals(portal2Expected.containsKey("u3"),p2.getRoutingTable().containsKey("u3"));
        assertEquals(portal2Expected.get("r1"),p2.getRoutingTable().get("r1"));
        assertEquals(portal2Expected.get("p1"),p2.getRoutingTable().get("p1"));        
        assertEquals(portal2Expected.get("p2"),p2.getRoutingTable().get("p2"));               
        assertEquals(portal2Expected.get("u1"),p2.getRoutingTable().get("u1"));
        assertEquals(portal2Expected.get("u2"),p2.getRoutingTable().get("u2"));
        assertEquals(portal2Expected.get("u3"),p2.getRoutingTable().get("u3"));
        assertEquals(portal2Expected.get("u4"),p2.getRoutingTable().get("u4"));   
    } 
    
    /**
     * Test of sendMessage (null) method, of class UserAgent.
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testSendNullBodyMessageFromUser() throws InterruptedException
    {
        String expected = "u1: null Recipient: u4"+ 
                System.getProperty("line.separator");
        u1.sendMessage("u4", null);
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 24));
    }   
    
    /**
     * Test of sendMessage (null) method, of class UserAgent.
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testSendNullRecipientMessageFromUser() throws InterruptedException 
    {
        String expected = "";
        u1.sendMessage(null, "test");
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString());
    }    
    
    /**
     * Test of sendMessage method, of class UserAgent.
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testSendMessageUserToUser() throws InterruptedException
    { 
        String expected = "u1: test message Recipient: u4" + 
                System.getProperty("line.separator");
        u1.sendMessage("u4","test message");
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 32));       
    }    
    
    /**
     * Test of removeAgent (null) method, of class Router.
     */
    @Test (expected = NullPointerException.class)
    public void testRemoveNullAgent()
    {
        r1.removeAgent(null);
    }  
    
    /**
     * Test of sendMsg (null) method, of class UserAgent.
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testSendMsg_NullMsgData() throws InterruptedException
    {
        String expected = "Empty message"+ 
                System.getProperty("line.separator");
        u1.sendMsg(null);
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString());        
    } 

    /**
     * Test of sendMessage method, of class UserAgent.
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testSendMessageCurrentUserToNewUser() throws InterruptedException
    { 
        Portal p4 = new Portal("p4",r1);
        UserAgent u6 = new UserAgent("u6",p4);  
        String expected = "u1: test message Recipient: u6" + 
                System.getProperty("line.separator");
        u1.sendMessage("u6","test message");
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 32));       
    } 

    /**
     * Test of sendMessage method, of class UserAgent.
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testSendMessageNewUserToCurrentUser() throws InterruptedException
    { 
        Portal p4 = new Portal("p4",r1);
        UserAgent u6 = new UserAgent("u6",p4);  
        String expected = "u6: test message Recipient: u1" + 
                System.getProperty("line.separator");
        u6.sendMessage("u1","test message");
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 32));       
    } 

    /**
     * Test of sendMessage method, of class UserAgent.
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testSendMessageTwoNewUsersOnNewPortals() throws InterruptedException
    { 
        Portal p4 = new Portal("p4",r1);
        UserAgent u6 = new UserAgent("u6",p4);  
        Portal p5 = new Portal("p5",r1);
        UserAgent u7 = new UserAgent("u7",p5);
        String expected = "u6: test message Recipient: u7" + 
                System.getProperty("line.separator");
        u6.sendMessage("u7","test message");
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 32));       
    }     
    
    /**
     * Test of getRoutingTable method, of class Router.
     */
    @Test
    public void testGetRoutingTable()
    {
        assertThat(r1.getRoutingTable(), is(routerExpected));
        assertThat(r1.getRoutingTable().size(), is(routerExpected.size()));
        assertEquals(routerExpected.containsKey("u1"),r1.getRoutingTable().containsKey("u1"));
        assertEquals(routerExpected.containsKey("u2"),r1.getRoutingTable().containsKey("u2"));
        assertEquals(routerExpected.containsKey("p1"),r1.getRoutingTable().containsKey("p1"));
        assertEquals(routerExpected.containsKey("u3"),r1.getRoutingTable().containsKey("u3"));
        assertEquals(routerExpected.containsKey("u4"),r1.getRoutingTable().containsKey("u4"));
        assertEquals(routerExpected.containsKey("p2"),r1.getRoutingTable().containsKey("p2"));
        assertEquals(routerExpected.containsKey("r1"),r1.getRoutingTable().containsKey("r1"));
        assertEquals(routerExpected.get("u1"),r1.getRoutingTable().get("u1"));
        assertEquals(routerExpected.get("u2"),r1.getRoutingTable().get("u2"));
        assertEquals(routerExpected.get("p1"),r1.getRoutingTable().get("p1"));
        assertEquals(routerExpected.get("u3"),r1.getRoutingTable().get("u3"));
        assertEquals(routerExpected.get("u4"),r1.getRoutingTable().get("u4"));
        assertEquals(routerExpected.get("p2"),r1.getRoutingTable().get("p2"));
        assertEquals(routerExpected.get("r1"),r1.getRoutingTable().get("r1"));
        
        assertThat(p1.getRoutingTable(), is(portal1Expected));
        assertThat(p1.getRoutingTable().size(), is(portal1Expected.size()));
        assertEquals(portal1Expected.containsKey("u1"),p1.getRoutingTable().containsKey("u1"));
        assertEquals(portal1Expected.containsKey("u2"),p1.getRoutingTable().containsKey("u2"));
        assertEquals(portal1Expected.containsKey("p1"),p1.getRoutingTable().containsKey("p1"));
        assertEquals(portal1Expected.containsKey("u3"),p1.getRoutingTable().containsKey("u3"));
        assertEquals(portal1Expected.containsKey("u4"),p1.getRoutingTable().containsKey("u4"));
        assertEquals(portal1Expected.containsKey("p2"),p1.getRoutingTable().containsKey("p2"));
        assertEquals(portal1Expected.containsKey("r1"),p1.getRoutingTable().containsKey("r1"));
        assertEquals(portal1Expected.get("u1"),p1.getRoutingTable().get("u1"));
        assertEquals(portal1Expected.get("u2"),p1.getRoutingTable().get("u2"));
        assertEquals(portal1Expected.get("p1"),p1.getRoutingTable().get("p1"));
        assertEquals(portal1Expected.get("u3"),p1.getRoutingTable().get("u3"));
        assertEquals(portal1Expected.get("u4"),p1.getRoutingTable().get("u4"));
        assertEquals(portal1Expected.get("p2"),p1.getRoutingTable().get("p2"));
        assertEquals(portal1Expected.get("r1"),p1.getRoutingTable().get("r1"));  
        
        assertThat(p2.getRoutingTable(), is(portal2Expected));
        assertThat(p2.getRoutingTable().size(), is(portal2Expected.size()));
        assertEquals(portal2Expected.containsKey("u1"),p2.getRoutingTable().containsKey("u1"));
        assertEquals(portal2Expected.containsKey("u2"),p2.getRoutingTable().containsKey("u2"));
        assertEquals(portal2Expected.containsKey("p1"),p2.getRoutingTable().containsKey("p1"));
        assertEquals(portal2Expected.containsKey("u3"),p2.getRoutingTable().containsKey("u3"));
        assertEquals(portal2Expected.containsKey("u4"),p2.getRoutingTable().containsKey("u4"));
        assertEquals(portal2Expected.containsKey("p2"),p2.getRoutingTable().containsKey("p2"));
        assertEquals(portal2Expected.containsKey("r1"),p2.getRoutingTable().containsKey("r1"));
        assertEquals(portal2Expected.get("u1"),p2.getRoutingTable().get("u1"));
        assertEquals(portal2Expected.get("u2"),p2.getRoutingTable().get("u2"));
        assertEquals(portal2Expected.get("p1"),p2.getRoutingTable().get("p1"));
        assertEquals(portal2Expected.get("u3"),p2.getRoutingTable().get("u3"));
        assertEquals(portal2Expected.get("u4"),p2.getRoutingTable().get("u4"));
        assertEquals(portal2Expected.get("p2"),p2.getRoutingTable().get("p2"));
        assertEquals(portal2Expected.get("r1"),p2.getRoutingTable().get("r1"));        
    }
    
    /**
     * Test of getIpAddress method, of class Router.
     * @throws java.io.IOException
     */
    @Test
    public void testGetIpAddress() throws IOException
    {
        assertEquals(null,r1.getIpAdress());
        String expected = InetAddress.getLocalHost().getHostAddress().trim();
        r1.advertiseConnection(8354);
        assertEquals(expected,r1.getIpAdress());
    }
    
    /**
     * Test of getPort method, of class Router.
     * @throws java.io.IOException
     */
    @Test
    public void testGetPort() throws IOException
    {
        assertEquals(0,r1.getPort());        
        r1.advertiseConnection(8001);
        assertEquals(8001,r1.getPort());
    }   
    
    /**
     * Test of getPortalHandles method, of class Router.
     */
    @Test
    public void testGetPortalHandles()
    {
        List<String> expected = new ArrayList<>();
        expected.add("p1");
        expected.add("p2");
        expected.add("r1");
        expected.add("u1");
        expected.add("u2");
        expected.add("u3");
        expected.add("u4");
        List<String> actual = r1.getPortalHandles();      
        assertThat(expected.size(),is(actual.size()));
        assertEquals(expected.contains("p1"),actual.contains("p1"));
        assertEquals(expected.contains("p2"),actual.contains("p2"));
        assertEquals(expected.contains("r1"),actual.contains("r1"));
        assertEquals(expected.contains("u1"),actual.contains("u1"));
        assertEquals(expected.contains("u2"),actual.contains("u2"));
        assertEquals(expected.contains("u3"),actual.contains("u3"));
        assertEquals(expected.contains("u4"),actual.contains("u4"));
    }  
    
    /**
     * Test of getInstance method, of class Router.
     */
    @Test
    public void testGetInstance()
    {  
        assertEquals(r1,Router.getInstance());
    }  
    
    /**
     * Test of CreateNew method, of class Router.
     * @throws java.lang.NoSuchFieldException
     * @throws java.lang.IllegalAccessException
     */
    @Test
    public void testCreateNewSecondRouter() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {  
        resetRouter();
        Router first = Router.createNew("first");
        Router second = Router.createNew("second");
        assertEquals(first,Router.getInstance());
        assertEquals(null,second);
    }   
    
    /**
     * Test of sendMessage method, of class UserAgent
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testSendToUnregisteredUserFromUser() throws InterruptedException
    {  
        String expected = "Recipient not found in p1" + 
                System.getProperty("line.separator");
        u1.sendMessage("nonexistant", "are you there?");
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 27));        
    }       
    
    /**
     * Test of msgHandler method, of class Router system.
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    @Test
    public void testSendToUnregisteredUserFromPRouter() throws InterruptedException, IOException
    {  
        String expected = "Recipient not found in r1" + 
                System.getProperty("line.separator");
        r1.msgHandler(new MsgData("r1","nonexistant", "are you there?"));
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 27));        
    }
    
    /**
     * Test of addAgent method, of class Router system.
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testAddPortalAgentAlreadyAdded() throws InterruptedException
    {  
        String expected = "Agent already exists" + 
                System.getProperty("line.separator");
        r1.addAgent(p1);
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString());        
    }    
    
    /**
     * Test of addAgent method, of class Router system.
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testAddUserAgentAlreadyAdded() throws InterruptedException
    {  
        String expected = "Agent already exists" + 
                System.getProperty("line.separator");
        r1.addAgent(u1);
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString());        
    }   

    /**
     * Test of addAgent method, of class Router system.
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testAddUserAgentToRouter() throws InterruptedException
    {  
        Portal p5 = new Portal("p5",r1);
        UserAgent u5 = new UserAgent("u5",null);
        String expected = "Only portals and sockets are allowed to add on a router" + 
                System.getProperty("line.separator");
        r1.addAgent(u5);
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString());        
    }
    
    /**
     * Test of removeAllUsers method of class Router
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testPortalRemoveAllUsers() throws InterruptedException
    {  
        p1.removeAllUsers();
        removeKeyFromExpected("u1");
        removeKeyFromExpected("u2");
        Thread.sleep(sleepTime);
        //check r1 routing table
        assertThat(r1.getRoutingTable(), is(routerExpected));
        assertThat(r1.getRoutingTable().size(), is(routerExpected.size()));          
        assertEquals(routerExpected.containsKey("u5"),r1.getRoutingTable().containsKey("u5"));          
        assertEquals(routerExpected.containsKey("p3"),r1.getRoutingTable().containsKey("p3"));     
        assertEquals(routerExpected.containsKey("u1"),r1.getRoutingTable().containsKey("u1"));
        assertEquals(routerExpected.containsKey("u2"),r1.getRoutingTable().containsKey("u2"));
        assertEquals(routerExpected.containsKey("p1"),r1.getRoutingTable().containsKey("p1"));
        assertEquals(routerExpected.containsKey("u3"),r1.getRoutingTable().containsKey("u3"));
        assertEquals(routerExpected.containsKey("u4"),r1.getRoutingTable().containsKey("u4"));
        assertEquals(routerExpected.containsKey("p2"),r1.getRoutingTable().containsKey("p2"));
        assertEquals(routerExpected.containsKey("r1"),r1.getRoutingTable().containsKey("r1"));
        assertEquals(routerExpected.get("u1"),r1.getRoutingTable().get("u1"));
        assertEquals(routerExpected.get("u2"),r1.getRoutingTable().get("u2"));
        assertEquals(routerExpected.get("p1"),r1.getRoutingTable().get("p1"));
        assertEquals(routerExpected.get("u3"),r1.getRoutingTable().get("u3"));
        assertEquals(routerExpected.get("u4"),r1.getRoutingTable().get("u4"));
        assertEquals(routerExpected.get("p2"),r1.getRoutingTable().get("p2"));
        assertEquals(routerExpected.get("r1"),r1.getRoutingTable().get("r1"));
        assertEquals(routerExpected.get("p3"),r1.getRoutingTable().get("p3"));
        assertEquals(routerExpected.get("u5"),r1.getRoutingTable().get("u5"));
        //check p1 routing table
        assertThat(p1.getRoutingTable(), is(portal1Expected));
        assertThat(p1.getRoutingTable().size(), is(portal1Expected.size()));          
        assertEquals(portal1Expected.containsKey("u5"),p1.getRoutingTable().containsKey("u5"));          
        assertEquals(portal1Expected.containsKey("p3"),p1.getRoutingTable().containsKey("p3"));     
        assertEquals(portal1Expected.containsKey("u1"),p1.getRoutingTable().containsKey("u1"));
        assertEquals(portal1Expected.containsKey("u2"),p1.getRoutingTable().containsKey("u2"));
        assertEquals(portal1Expected.containsKey("p1"),p1.getRoutingTable().containsKey("p1"));
        assertEquals(portal1Expected.containsKey("u3"),p1.getRoutingTable().containsKey("u3"));
        assertEquals(portal1Expected.containsKey("u4"),p1.getRoutingTable().containsKey("u4"));
        assertEquals(portal1Expected.containsKey("p2"),p1.getRoutingTable().containsKey("p2"));
        assertEquals(portal1Expected.containsKey("r1"),p1.getRoutingTable().containsKey("r1"));
        assertEquals(portal1Expected.get("u1"),p1.getRoutingTable().get("u1"));
        assertEquals(portal1Expected.get("u2"),p1.getRoutingTable().get("u2"));
        assertEquals(portal1Expected.get("p1"),p1.getRoutingTable().get("p1"));
        assertEquals(portal1Expected.get("u3"),p1.getRoutingTable().get("u3"));
        assertEquals(portal1Expected.get("u4"),p1.getRoutingTable().get("u4"));
        assertEquals(portal1Expected.get("p2"),p1.getRoutingTable().get("p2"));
        assertEquals(portal1Expected.get("r1"),p1.getRoutingTable().get("r1"));
        assertEquals(portal1Expected.get("p3"),p1.getRoutingTable().get("p3"));
        assertEquals(portal1Expected.get("u5"),p1.getRoutingTable().get("u5"));
        //check p2 routing table
        assertThat(p2.getRoutingTable(), is(portal2Expected));
        assertThat(p2.getRoutingTable().size(), is(portal2Expected.size()));          
        assertEquals(portal2Expected.containsKey("u5"),p2.getRoutingTable().containsKey("u5"));          
        assertEquals(portal2Expected.containsKey("p3"),p2.getRoutingTable().containsKey("p3"));     
        assertEquals(portal2Expected.containsKey("u1"),p2.getRoutingTable().containsKey("u1"));
        assertEquals(portal2Expected.containsKey("u2"),p2.getRoutingTable().containsKey("u2"));
        assertEquals(portal2Expected.containsKey("p1"),p2.getRoutingTable().containsKey("p1"));
        assertEquals(portal2Expected.containsKey("u3"),p2.getRoutingTable().containsKey("u3"));
        assertEquals(portal2Expected.containsKey("u4"),p2.getRoutingTable().containsKey("u4"));
        assertEquals(portal2Expected.containsKey("p2"),p2.getRoutingTable().containsKey("p2"));
        assertEquals(portal2Expected.containsKey("r1"),p2.getRoutingTable().containsKey("r1"));
        assertEquals(portal2Expected.get("u1"),p2.getRoutingTable().get("u1"));
        assertEquals(portal2Expected.get("u2"),p2.getRoutingTable().get("u2"));
        assertEquals(portal2Expected.get("p1"),p2.getRoutingTable().get("p1"));
        assertEquals(portal2Expected.get("u3"),p2.getRoutingTable().get("u3"));
        assertEquals(portal2Expected.get("u4"),p2.getRoutingTable().get("u4"));
        assertEquals(portal2Expected.get("p2"),p2.getRoutingTable().get("p2"));
        assertEquals(portal2Expected.get("r1"),p2.getRoutingTable().get("r1"));
        assertEquals(portal2Expected.get("p3"),p2.getRoutingTable().get("p3"));
        assertEquals(portal2Expected.get("u5"),p2.getRoutingTable().get("u5"));  
    }    
    
    /**
     * Test of Portal Constructor with identical handle
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testNewPortalSameHandle() throws InterruptedException
    {
        Portal p0 = new Portal("p1",r1);
        String expected = "Agent already exists" + 
                System.getProperty("line.separator");
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString());   
        //check r1 routing table
        assertThat(r1.getRoutingTable(), is(routerExpected));
        assertThat(r1.getRoutingTable().size(), is(routerExpected.size()));          
        assertEquals(routerExpected.containsKey("u5"),r1.getRoutingTable().containsKey("u5"));          
        assertEquals(routerExpected.containsKey("p3"),r1.getRoutingTable().containsKey("p3"));     
        assertEquals(routerExpected.containsKey("u1"),r1.getRoutingTable().containsKey("u1"));
        assertEquals(routerExpected.containsKey("u2"),r1.getRoutingTable().containsKey("u2"));
        assertEquals(routerExpected.containsKey("p1"),r1.getRoutingTable().containsKey("p1"));
        assertEquals(routerExpected.containsKey("u3"),r1.getRoutingTable().containsKey("u3"));
        assertEquals(routerExpected.containsKey("u4"),r1.getRoutingTable().containsKey("u4"));
        assertEquals(routerExpected.containsKey("p2"),r1.getRoutingTable().containsKey("p2"));
        assertEquals(routerExpected.containsKey("r1"),r1.getRoutingTable().containsKey("r1"));
        assertEquals(routerExpected.containsKey("p0"),r1.getRoutingTable().containsKey("p0"));
        assertEquals(routerExpected.get("u1"),r1.getRoutingTable().get("u1"));
        assertEquals(routerExpected.get("u2"),r1.getRoutingTable().get("u2"));
        assertEquals(routerExpected.get("p1"),r1.getRoutingTable().get("p1"));
        assertEquals(routerExpected.get("u3"),r1.getRoutingTable().get("u3"));
        assertEquals(routerExpected.get("u4"),r1.getRoutingTable().get("u4"));
        assertEquals(routerExpected.get("p2"),r1.getRoutingTable().get("p2"));
        assertEquals(routerExpected.get("r1"),r1.getRoutingTable().get("r1"));
        assertEquals(routerExpected.get("p3"),r1.getRoutingTable().get("p3"));
        assertEquals(routerExpected.get("u5"),r1.getRoutingTable().get("u5"));
        assertEquals(routerExpected.get("p0"),r1.getRoutingTable().get("p0"));
        //check p1 routing table
        assertThat(p1.getRoutingTable(), is(portal1Expected));
        assertThat(p1.getRoutingTable().size(), is(portal1Expected.size()));          
        assertEquals(portal1Expected.containsKey("u5"),p1.getRoutingTable().containsKey("u5"));          
        assertEquals(portal1Expected.containsKey("p3"),p1.getRoutingTable().containsKey("p3"));     
        assertEquals(portal1Expected.containsKey("u1"),p1.getRoutingTable().containsKey("u1"));
        assertEquals(portal1Expected.containsKey("u2"),p1.getRoutingTable().containsKey("u2"));
        assertEquals(portal1Expected.containsKey("p1"),p1.getRoutingTable().containsKey("p1"));
        assertEquals(portal1Expected.containsKey("u3"),p1.getRoutingTable().containsKey("u3"));
        assertEquals(portal1Expected.containsKey("u4"),p1.getRoutingTable().containsKey("u4"));
        assertEquals(portal1Expected.containsKey("p2"),p1.getRoutingTable().containsKey("p2"));
        assertEquals(portal1Expected.containsKey("r1"),p1.getRoutingTable().containsKey("r1"));
        assertEquals(portal1Expected.containsKey("p0"),p1.getRoutingTable().containsKey("p0"));
        assertEquals(portal1Expected.get("u1"),p1.getRoutingTable().get("u1"));
        assertEquals(portal1Expected.get("u2"),p1.getRoutingTable().get("u2"));
        assertEquals(portal1Expected.get("p1"),p1.getRoutingTable().get("p1"));
        assertEquals(portal1Expected.get("u3"),p1.getRoutingTable().get("u3"));
        assertEquals(portal1Expected.get("u4"),p1.getRoutingTable().get("u4"));
        assertEquals(portal1Expected.get("p2"),p1.getRoutingTable().get("p2"));
        assertEquals(portal1Expected.get("r1"),p1.getRoutingTable().get("r1"));
        assertEquals(portal1Expected.get("p3"),p1.getRoutingTable().get("p3"));
        assertEquals(portal1Expected.get("u5"),p1.getRoutingTable().get("u5"));
        assertEquals(portal1Expected.get("p0"),p1.getRoutingTable().get("p0"));
        //check p2 routing table
        assertThat(p2.getRoutingTable(), is(portal2Expected));
        assertThat(p2.getRoutingTable().size(), is(portal2Expected.size()));          
        assertEquals(portal2Expected.containsKey("u5"),p2.getRoutingTable().containsKey("u5"));          
        assertEquals(portal2Expected.containsKey("p3"),p2.getRoutingTable().containsKey("p3"));     
        assertEquals(portal2Expected.containsKey("u1"),p2.getRoutingTable().containsKey("u1"));
        assertEquals(portal2Expected.containsKey("u2"),p2.getRoutingTable().containsKey("u2"));
        assertEquals(portal2Expected.containsKey("p1"),p2.getRoutingTable().containsKey("p1"));
        assertEquals(portal2Expected.containsKey("u3"),p2.getRoutingTable().containsKey("u3"));
        assertEquals(portal2Expected.containsKey("u4"),p2.getRoutingTable().containsKey("u4"));
        assertEquals(portal2Expected.containsKey("p2"),p2.getRoutingTable().containsKey("p2"));
        assertEquals(portal2Expected.containsKey("r1"),p2.getRoutingTable().containsKey("r1"));
        assertEquals(portal2Expected.containsKey("p0"),p2.getRoutingTable().containsKey("p0"));
        assertEquals(portal2Expected.get("u1"),p2.getRoutingTable().get("u1"));
        assertEquals(portal2Expected.get("u2"),p2.getRoutingTable().get("u2"));
        assertEquals(portal2Expected.get("p1"),p2.getRoutingTable().get("p1"));
        assertEquals(portal2Expected.get("u3"),p2.getRoutingTable().get("u3"));
        assertEquals(portal2Expected.get("u4"),p2.getRoutingTable().get("u4"));
        assertEquals(portal2Expected.get("p2"),p2.getRoutingTable().get("p2"));
        assertEquals(portal2Expected.get("r1"),p2.getRoutingTable().get("r1"));
        assertEquals(portal2Expected.get("p3"),p2.getRoutingTable().get("p3"));
        assertEquals(portal2Expected.get("u5"),p2.getRoutingTable().get("u5"));  
        assertEquals(portal2Expected.get("p0"),p2.getRoutingTable().get("p0"));  
        TreeMap<String, MetaAgent> portal0Expected = new TreeMap<>();
        portal0Expected.put("p1", p0);  
        assertThat(p0.getRoutingTable(), is(portal0Expected));
        assertThat(p0.getRoutingTable().size(), is(portal0Expected.size()));          
        assertEquals(portal0Expected.containsKey("u5"),p0.getRoutingTable().containsKey("u5"));          
        assertEquals(portal0Expected.containsKey("p3"),p0.getRoutingTable().containsKey("p3"));     
        assertEquals(portal0Expected.containsKey("u1"),p0.getRoutingTable().containsKey("u1"));
        assertEquals(portal0Expected.containsKey("u2"),p0.getRoutingTable().containsKey("u2"));
        assertEquals(portal0Expected.containsKey("p1"),p0.getRoutingTable().containsKey("p1"));
        assertEquals(portal0Expected.containsKey("u3"),p0.getRoutingTable().containsKey("u3"));
        assertEquals(portal0Expected.containsKey("u4"),p0.getRoutingTable().containsKey("u4"));
        assertEquals(portal0Expected.containsKey("p2"),p0.getRoutingTable().containsKey("p2"));
        assertEquals(portal0Expected.containsKey("r1"),p0.getRoutingTable().containsKey("r1")); 
        assertEquals(portal0Expected.containsKey("p0"),p0.getRoutingTable().containsKey("p0"));         
        assertEquals(portal0Expected.get("u1"),p0.getRoutingTable().get("u1"));
        assertEquals(portal0Expected.get("u2"),p0.getRoutingTable().get("u2"));
        assertEquals(portal0Expected.get("p1"),p0.getRoutingTable().get("p1"));
        assertEquals(portal0Expected.get("u3"),p0.getRoutingTable().get("u3"));
        assertEquals(portal0Expected.get("u4"),p0.getRoutingTable().get("u4"));
        assertEquals(portal0Expected.get("p2"),p0.getRoutingTable().get("p2"));
        assertEquals(portal0Expected.get("r1"),p0.getRoutingTable().get("r1"));
        assertEquals(portal0Expected.get("p3"),p0.getRoutingTable().get("p3"));
        assertEquals(portal0Expected.get("u5"),p0.getRoutingTable().get("u5"));  
        assertEquals(portal0Expected.get("p0"),p0.getRoutingTable().get("p0"));
    }
    
    /**
     * Test of UserAgent Constructor with identical handle
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testNewUserSameHandle() throws InterruptedException
    {
        Portal p0 = new Portal("p0",r1);
        UserAgent u0 = new UserAgent("u1",p0);
        Thread.sleep(sleepTime);   
        //check p0
        TreeMap<String, MetaAgent> portal0Expected = new TreeMap<>();
        portal0Expected.put("p0", p0);  
        portal0Expected.put("p1", r1);  
        portal0Expected.put("p2", r1);  
        portal0Expected.put("u1", r1);  
        portal0Expected.put("u2", r1);  
        portal0Expected.put("u3", r1);  
        portal0Expected.put("u4", r1);
        portal0Expected.put("r1", r1);
        routerExpected.put("p0", p0);
        portal1Expected.put("p0", r1);
        portal2Expected.put("p0", r1);
        assertThat(p0.getRoutingTable(), is(portal0Expected));
        assertThat(p0.getRoutingTable().size(), is(portal0Expected.size()));          
        assertEquals(portal0Expected.containsKey("u5"),p0.getRoutingTable().containsKey("u5"));          
        assertEquals(portal0Expected.containsKey("p3"),p0.getRoutingTable().containsKey("p3"));     
        assertEquals(portal0Expected.containsKey("u1"),p0.getRoutingTable().containsKey("u1"));
        assertEquals(portal0Expected.containsKey("u2"),p0.getRoutingTable().containsKey("u2"));
        assertEquals(portal0Expected.containsKey("p1"),p0.getRoutingTable().containsKey("p1"));
        assertEquals(portal0Expected.containsKey("u3"),p0.getRoutingTable().containsKey("u3"));
        assertEquals(portal0Expected.containsKey("u4"),p0.getRoutingTable().containsKey("u4"));
        assertEquals(portal0Expected.containsKey("p2"),p0.getRoutingTable().containsKey("p2"));
        assertEquals(portal0Expected.containsKey("r1"),p0.getRoutingTable().containsKey("r1")); 
        assertEquals(portal0Expected.containsKey("p0"),p0.getRoutingTable().containsKey("p0"));         
        assertEquals(portal0Expected.get("u1"),p0.getRoutingTable().get("u1"));
        assertEquals(portal0Expected.get("u2"),p0.getRoutingTable().get("u2"));
        assertEquals(portal0Expected.get("p1"),p0.getRoutingTable().get("p1"));
        assertEquals(portal0Expected.get("u3"),p0.getRoutingTable().get("u3"));
        assertEquals(portal0Expected.get("u4"),p0.getRoutingTable().get("u4"));
        assertEquals(portal0Expected.get("p2"),p0.getRoutingTable().get("p2"));
        assertEquals(portal0Expected.get("r1"),p0.getRoutingTable().get("r1"));
        assertEquals(portal0Expected.get("p3"),p0.getRoutingTable().get("p3"));
        assertEquals(portal0Expected.get("u5"),p0.getRoutingTable().get("u5"));  
        assertEquals(portal0Expected.get("p0"),p0.getRoutingTable().get("p0"));
        //check r1 routing table
        assertThat(r1.getRoutingTable(), is(routerExpected));
        assertThat(r1.getRoutingTable().size(), is(routerExpected.size()));          
        assertEquals(routerExpected.containsKey("u5"),r1.getRoutingTable().containsKey("u5"));          
        assertEquals(routerExpected.containsKey("p3"),r1.getRoutingTable().containsKey("p3"));     
        assertEquals(routerExpected.containsKey("u1"),r1.getRoutingTable().containsKey("u1"));
        assertEquals(routerExpected.containsKey("u2"),r1.getRoutingTable().containsKey("u2"));
        assertEquals(routerExpected.containsKey("p1"),r1.getRoutingTable().containsKey("p1"));
        assertEquals(routerExpected.containsKey("u3"),r1.getRoutingTable().containsKey("u3"));
        assertEquals(routerExpected.containsKey("u4"),r1.getRoutingTable().containsKey("u4"));
        assertEquals(routerExpected.containsKey("p2"),r1.getRoutingTable().containsKey("p2"));
        assertEquals(routerExpected.containsKey("r1"),r1.getRoutingTable().containsKey("r1"));
        assertEquals(routerExpected.containsKey("p0"),r1.getRoutingTable().containsKey("p0"));
        assertEquals(routerExpected.get("u1"),r1.getRoutingTable().get("u1"));
        assertEquals(routerExpected.get("u2"),r1.getRoutingTable().get("u2"));
        assertEquals(routerExpected.get("p1"),r1.getRoutingTable().get("p1"));
        assertEquals(routerExpected.get("u3"),r1.getRoutingTable().get("u3"));
        assertEquals(routerExpected.get("u4"),r1.getRoutingTable().get("u4"));
        assertEquals(routerExpected.get("p2"),r1.getRoutingTable().get("p2"));
        assertEquals(routerExpected.get("r1"),r1.getRoutingTable().get("r1"));
        assertEquals(routerExpected.get("p3"),r1.getRoutingTable().get("p3"));
        assertEquals(routerExpected.get("u5"),r1.getRoutingTable().get("u5"));
        assertEquals(routerExpected.get("p0"),r1.getRoutingTable().get("p0"));
        //check p1 routing table
        assertThat(p1.getRoutingTable(), is(portal1Expected));
        assertThat(p1.getRoutingTable().size(), is(portal1Expected.size()));          
        assertEquals(portal1Expected.containsKey("u5"),p1.getRoutingTable().containsKey("u5"));          
        assertEquals(portal1Expected.containsKey("p3"),p1.getRoutingTable().containsKey("p3"));     
        assertEquals(portal1Expected.containsKey("u1"),p1.getRoutingTable().containsKey("u1"));
        assertEquals(portal1Expected.containsKey("u2"),p1.getRoutingTable().containsKey("u2"));
        assertEquals(portal1Expected.containsKey("p1"),p1.getRoutingTable().containsKey("p1"));
        assertEquals(portal1Expected.containsKey("u3"),p1.getRoutingTable().containsKey("u3"));
        assertEquals(portal1Expected.containsKey("u4"),p1.getRoutingTable().containsKey("u4"));
        assertEquals(portal1Expected.containsKey("p2"),p1.getRoutingTable().containsKey("p2"));
        assertEquals(portal1Expected.containsKey("r1"),p1.getRoutingTable().containsKey("r1"));
        assertEquals(portal1Expected.containsKey("p0"),p1.getRoutingTable().containsKey("p0"));
        assertEquals(portal1Expected.get("u1"),p1.getRoutingTable().get("u1"));
        assertEquals(portal1Expected.get("u2"),p1.getRoutingTable().get("u2"));
        assertEquals(portal1Expected.get("p1"),p1.getRoutingTable().get("p1"));
        assertEquals(portal1Expected.get("u3"),p1.getRoutingTable().get("u3"));
        assertEquals(portal1Expected.get("u4"),p1.getRoutingTable().get("u4"));
        assertEquals(portal1Expected.get("p2"),p1.getRoutingTable().get("p2"));
        assertEquals(portal1Expected.get("r1"),p1.getRoutingTable().get("r1"));
        assertEquals(portal1Expected.get("p3"),p1.getRoutingTable().get("p3"));
        assertEquals(portal1Expected.get("u5"),p1.getRoutingTable().get("u5"));
        assertEquals(portal1Expected.get("p0"),p1.getRoutingTable().get("p0"));
        //check p2 routing table
        assertThat(p2.getRoutingTable(), is(portal2Expected));
        assertThat(p2.getRoutingTable().size(), is(portal2Expected.size()));          
        assertEquals(portal2Expected.containsKey("u5"),p2.getRoutingTable().containsKey("u5"));          
        assertEquals(portal2Expected.containsKey("p3"),p2.getRoutingTable().containsKey("p3"));     
        assertEquals(portal2Expected.containsKey("u1"),p2.getRoutingTable().containsKey("u1"));
        assertEquals(portal2Expected.containsKey("u2"),p2.getRoutingTable().containsKey("u2"));
        assertEquals(portal2Expected.containsKey("p1"),p2.getRoutingTable().containsKey("p1"));
        assertEquals(portal2Expected.containsKey("u3"),p2.getRoutingTable().containsKey("u3"));
        assertEquals(portal2Expected.containsKey("u4"),p2.getRoutingTable().containsKey("u4"));
        assertEquals(portal2Expected.containsKey("p2"),p2.getRoutingTable().containsKey("p2"));
        assertEquals(portal2Expected.containsKey("r1"),p2.getRoutingTable().containsKey("r1"));
        assertEquals(portal2Expected.containsKey("p0"),p2.getRoutingTable().containsKey("p0"));
        assertEquals(portal2Expected.get("u1"),p2.getRoutingTable().get("u1"));
        assertEquals(portal2Expected.get("u2"),p2.getRoutingTable().get("u2"));
        assertEquals(portal2Expected.get("p1"),p2.getRoutingTable().get("p1"));
        assertEquals(portal2Expected.get("u3"),p2.getRoutingTable().get("u3"));
        assertEquals(portal2Expected.get("u4"),p2.getRoutingTable().get("u4"));
        assertEquals(portal2Expected.get("p2"),p2.getRoutingTable().get("p2"));
        assertEquals(portal2Expected.get("r1"),p2.getRoutingTable().get("r1"));
        assertEquals(portal2Expected.get("p3"),p2.getRoutingTable().get("p3"));
        assertEquals(portal2Expected.get("u5"),p2.getRoutingTable().get("u5"));  
        assertEquals(portal2Expected.get("p0"),p2.getRoutingTable().get("p0"));  
    }
    
    /**
     * Test of monitor when local users send message
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testMonitorMultipleActions() throws InterruptedException 
    {
        resetFile(userFile);
        resetFile(systemFile);
        r1.setMonitor(rm); 
        Portal p3 = new Portal("p3",r1);
        p3.setMonitor(pm);
        UserAgent u7 = new UserAgent("u7",p3);
        Portal p4 = new Portal("p4",r1);
        p4.setMonitor(pm);
        UserAgent u8 = new UserAgent("u8",p4);
        UserAgent u9 = new UserAgent("u9",p4);
        assertEquals("",readFile(userFile));
        String sysExpected2 = "PORTAL added to: " + separator + "r1\tName: " +
                separator + "p3\tTime Added: " + separator; //exclude time 
        String sysExpected3 = "USER added to: " + separator + "p3\tName: " +
                separator + "u7\tTime Added: " + separator; //exclude time         
        String sysExpected4 = "PORTAL added to: " + separator + "r1\tName: " +
                separator + "p4\tTime Added: " + separator; //exclude time       
        String sysExpected5 = "USER added to: " + separator + "p4\tName: " +
                separator + "u8\tTime Added: " + separator; //exclude time
        String sysExpected6 = "USER added to: " + separator + "p4\tName: " +
                separator + "u9\tTime Added: " + separator; //exclude time
        String fileContents = readFile(systemFile);
        assertEquals(sysExpected2,fileContents.substring(0, 50));
        assertEquals(sysExpected3,fileContents.substring(73, 121));
        assertEquals(sysExpected4,fileContents.substring(144, 194));
        assertEquals(sysExpected5,fileContents.substring(217, 265));
        assertEquals(sysExpected6,fileContents.substring(288, 336));
    }
    
    /**
     * Test of monitor when local users send message
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testMonitorSendingMessagesSharedPortalMonitor() throws InterruptedException 
    {
        r1.setMonitor(rm); 
        p1.setMonitor(pm);
        p2.setMonitor(pm);
        resetFile(userFile);
        resetFile(systemFile);
        u1.sendMessage("u3", "testMessage");
        assertEquals("",readFile(systemFile));
        String sentExpected1 = "Message sent to: " + separator + "u3\tSender: " +
                separator + "u1\tPassed to: " + separator + "p1\tTime sent: " + 
                separator;
        String sentExpected2 = "\tMessage body: " + separator + "testMessage";
        String receivedExpected1 = "Message received from: " + separator + "u1\tRecipient: " +
                separator + "u3\tDelivered by: " + separator + "p2\tTime received: " + 
                separator;
        String receivedExpected2 = "\tMessage body: " + separator + "testMessage";
        String fileContents = readFile(userFile);
        assertEquals(sentExpected1,fileContents.substring(0, 68));
        assertEquals(sentExpected2,fileContents.substring(91, 120));
        assertEquals(receivedExpected1,fileContents.substring(120, 204));
        assertEquals(receivedExpected2,fileContents.substring(227, 256));
    }   
    
    /**
     * Test of monitor when local users send message
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testMonitorSendingMessagesDifferentPortalMonitor() throws InterruptedException 
    {
        r1.setMonitor(rm); 
        p1.setMonitor(pm);
        PortalMonitor pm2 = new DefaultPortalMonitor(systemManager,userManager);
        p2.setMonitor(pm2);
        Thread.sleep(sleepTime);
        resetFile(userFile);
        resetFile(systemFile);
        u1.sendMessage("u3", "testMessage");
        assertEquals("",readFile(systemFile));
        String sentExpected1 = "Message sent to: " + separator + "u3\tSender: " +
                separator + "u1\tPassed to: " + separator + "p1\tTime sent: " + 
                separator;
        String sentExpected2 = "\tMessage body: " + separator + "testMessage";
        String receivedExpected1 = "Message received from: " + separator + "u1\tRecipient: " +
                separator + "u3\tDelivered by: " + separator + "p2\tTime received: " + 
                separator;
        String fileContents = readFile(userFile);
        assertTrue(fileContents.contains(sentExpected1));
        assertTrue(fileContents.contains(sentExpected2));
        assertTrue(fileContents.contains(receivedExpected1));
    }   
    
    /**
     * Test of monitor when removing agent
     */
    @Test
    public void testMonitorRemoveUserAgent()
    {
        r1.setMonitor(rm);
        p1.setMonitor(pm);
        p2.setMonitor(pm);
        resetFile(userFile);
        resetFile(systemFile);
        r1.removeAgent("u1");
        String expected = "USER removed by: " + separator + "r1\tName: " +
                separator + "u1\tTime Removed: " + separator; //exclude time
        assertEquals(expected,readFile(systemFile).substring(0, 52));
        assertEquals("",readFile(userFile));
    }
    
    /**
     * Test of getMonitor
     */
    @Test
    public void testGetMonitor()
    {
        r1.setMonitor(rm);
        assertEquals(rm,r1.getMonitor());
    }
    
    /**
     * Test of removeAgent, removing router
     */
    @Test
    public void testRemoveRouter()
    {
        String expected = "Router removal is disallowed..." + 
                System.getProperty("line.separator");
        r1.removeAgent("r1");
        assertEquals(expected,sysOut.toString());
    }    
    
    /**
     * Test of getTypeFromTable
     */
    @Test
    public void testGetTypeFromTable()
    {
        assertEquals(MetaAgentType.ROUTER,r1.getTypeFromTable("r1"));
    } 
    
    /**
     * Test of removeAgent method, of class Portal.
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testRemovePortalHashMapCheckviaDifferentPortal() throws InterruptedException 
    {
        System.setOut(System.out);
        p1.removeAgent("p2"); 
        removeKeyFromExpected("p2");
        removeKeyFromExpected("u3");
        removeKeyFromExpected("u4");
        Thread.sleep(sleepTime);
        printRoutingTable(r1);
        printRoutingTable(p1);
        printRoutingTable(p2);
        //check r1 routing table
        assertThat(r1.getRoutingTable(), is(routerExpected));
        assertThat(r1.getRoutingTable().size(), is(routerExpected.size()));      
        assertEquals(routerExpected.containsKey("r1"),r1.getRoutingTable().containsKey("r1"));
        assertEquals(routerExpected.containsKey("p1"),r1.getRoutingTable().containsKey("p1"));
        assertEquals(routerExpected.containsKey("p2"),r1.getRoutingTable().containsKey("p2"));             
        assertEquals(routerExpected.containsKey("u1"),r1.getRoutingTable().containsKey("u1"));
        assertEquals(routerExpected.containsKey("u2"),r1.getRoutingTable().containsKey("u2"));
        assertEquals(routerExpected.containsKey("u3"),r1.getRoutingTable().containsKey("u3"));
        assertEquals(routerExpected.containsKey("u4"),r1.getRoutingTable().containsKey("u4"));
        assertEquals(routerExpected.get("r1"),r1.getRoutingTable().get("r1"));
        assertEquals(routerExpected.get("p1"),r1.getRoutingTable().get("p1"));        
        assertEquals(routerExpected.get("p2"),r1.getRoutingTable().get("p2"));               
        assertEquals(routerExpected.get("u1"),r1.getRoutingTable().get("u1"));
        assertEquals(routerExpected.get("u2"),r1.getRoutingTable().get("u2"));
        assertEquals(routerExpected.get("u3"),r1.getRoutingTable().get("u3"));
        assertEquals(routerExpected.get("u4"),r1.getRoutingTable().get("u4"));
        //check p1 routing table
        assertThat(p1.getRoutingTable(), is(portal1Expected));
        assertThat(p1.getRoutingTable().size(), is(portal1Expected.size()));      
        assertEquals(portal1Expected.containsKey("r1"),p1.getRoutingTable().containsKey("r1"));
        assertEquals(portal1Expected.containsKey("p1"),p1.getRoutingTable().containsKey("p1"));
        assertEquals(portal1Expected.containsKey("p2"),p1.getRoutingTable().containsKey("p2"));             
        assertEquals(portal1Expected.containsKey("u1"),p1.getRoutingTable().containsKey("u1"));
        assertEquals(portal1Expected.containsKey("u2"),p1.getRoutingTable().containsKey("u2"));
        assertEquals(portal1Expected.containsKey("u3"),p1.getRoutingTable().containsKey("u3"));
        assertEquals(portal1Expected.get("r1"),p1.getRoutingTable().get("r1"));
        assertEquals(portal1Expected.get("p1"),p1.getRoutingTable().get("p1"));        
        assertEquals(portal1Expected.get("p2"),p1.getRoutingTable().get("p2"));               
        assertEquals(portal1Expected.get("u1"),p1.getRoutingTable().get("u1"));
        assertEquals(portal1Expected.get("u2"),p1.getRoutingTable().get("u2"));
        assertEquals(portal1Expected.get("u3"),p1.getRoutingTable().get("u3"));
        assertEquals(portal1Expected.get("u4"),p1.getRoutingTable().get("u4"));       
        //check p2 routing table
        assertThat(p2.getRoutingTable(), is(portal2Expected));
        assertThat(p2.getRoutingTable().size(), is(portal2Expected.size()));      
        assertEquals(portal2Expected.containsKey("r1"),p2.getRoutingTable().containsKey("r1"));
        assertEquals(portal2Expected.containsKey("p1"),p2.getRoutingTable().containsKey("p1"));
        assertEquals(portal2Expected.containsKey("p2"),p2.getRoutingTable().containsKey("p2"));             
        assertEquals(portal2Expected.containsKey("u1"),p2.getRoutingTable().containsKey("u1"));
        assertEquals(portal2Expected.containsKey("u2"),p2.getRoutingTable().containsKey("u2"));
        assertEquals(portal2Expected.containsKey("u3"),p2.getRoutingTable().containsKey("u3"));
        assertEquals(portal2Expected.get("r1"),p2.getRoutingTable().get("r1"));
        assertEquals(portal2Expected.get("p1"),p2.getRoutingTable().get("p1"));        
        assertEquals(portal2Expected.get("p2"),p2.getRoutingTable().get("p2"));               
        assertEquals(portal2Expected.get("u1"),p2.getRoutingTable().get("u1"));
        assertEquals(portal2Expected.get("u2"),p2.getRoutingTable().get("u2"));
        assertEquals(portal2Expected.get("u3"),p2.getRoutingTable().get("u3"));
        assertEquals(portal2Expected.get("u4"),p2.getRoutingTable().get("u4"));  
        assertEquals(null,u3.getPortal());
        assertEquals(null,u4.getPortal());
        assertFalse(u1.getPortal() == null);
        assertFalse(u2.getPortal() == null);
    }      
    
    /**
     * Test of removeAgent method, of class Router.
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testRemovePortalHashMapCheckviaSamePortal() throws InterruptedException 
    {
        System.setOut(System.out);
        p2.removeAgent("p2"); 
        removeKeyFromExpected("p2");
        removeKeyFromExpected("u3");
        removeKeyFromExpected("u4");
        Thread.sleep(sleepTime);
        printRoutingTable(r1);
        printRoutingTable(p1);
        printRoutingTable(p2);
        //check r1 routing table
        assertThat(r1.getRoutingTable(), is(routerExpected));
        assertThat(r1.getRoutingTable().size(), is(routerExpected.size()));      
        assertEquals(routerExpected.containsKey("r1"),r1.getRoutingTable().containsKey("r1"));
        assertEquals(routerExpected.containsKey("p1"),r1.getRoutingTable().containsKey("p1"));
        assertEquals(routerExpected.containsKey("p2"),r1.getRoutingTable().containsKey("p2"));             
        assertEquals(routerExpected.containsKey("u1"),r1.getRoutingTable().containsKey("u1"));
        assertEquals(routerExpected.containsKey("u2"),r1.getRoutingTable().containsKey("u2"));
        assertEquals(routerExpected.containsKey("u3"),r1.getRoutingTable().containsKey("u3"));
        assertEquals(routerExpected.containsKey("u4"),r1.getRoutingTable().containsKey("u4"));
        assertEquals(routerExpected.get("r1"),r1.getRoutingTable().get("r1"));
        assertEquals(routerExpected.get("p1"),r1.getRoutingTable().get("p1"));        
        assertEquals(routerExpected.get("p2"),r1.getRoutingTable().get("p2"));               
        assertEquals(routerExpected.get("u1"),r1.getRoutingTable().get("u1"));
        assertEquals(routerExpected.get("u2"),r1.getRoutingTable().get("u2"));
        assertEquals(routerExpected.get("u3"),r1.getRoutingTable().get("u3"));
        assertEquals(routerExpected.get("u4"),r1.getRoutingTable().get("u4"));
        //check p1 routing table
        assertThat(p1.getRoutingTable(), is(portal1Expected));
        assertThat(p1.getRoutingTable().size(), is(portal1Expected.size()));      
        assertEquals(portal1Expected.containsKey("r1"),p1.getRoutingTable().containsKey("r1"));
        assertEquals(portal1Expected.containsKey("p1"),p1.getRoutingTable().containsKey("p1"));
        assertEquals(portal1Expected.containsKey("p2"),p1.getRoutingTable().containsKey("p2"));             
        assertEquals(portal1Expected.containsKey("u1"),p1.getRoutingTable().containsKey("u1"));
        assertEquals(portal1Expected.containsKey("u2"),p1.getRoutingTable().containsKey("u2"));
        assertEquals(portal1Expected.containsKey("u3"),p1.getRoutingTable().containsKey("u3"));
        assertEquals(portal1Expected.get("r1"),p1.getRoutingTable().get("r1"));
        assertEquals(portal1Expected.get("p1"),p1.getRoutingTable().get("p1"));        
        assertEquals(portal1Expected.get("p2"),p1.getRoutingTable().get("p2"));               
        assertEquals(portal1Expected.get("u1"),p1.getRoutingTable().get("u1"));
        assertEquals(portal1Expected.get("u2"),p1.getRoutingTable().get("u2"));
        assertEquals(portal1Expected.get("u3"),p1.getRoutingTable().get("u3"));
        assertEquals(portal1Expected.get("u4"),p1.getRoutingTable().get("u4"));       
        //check p2 routing table
        assertThat(p2.getRoutingTable(), is(portal2Expected));
        assertThat(p2.getRoutingTable().size(), is(portal2Expected.size()));      
        assertEquals(portal2Expected.containsKey("r1"),p2.getRoutingTable().containsKey("r1"));
        assertEquals(portal2Expected.containsKey("p1"),p2.getRoutingTable().containsKey("p1"));
        assertEquals(portal2Expected.containsKey("p2"),p2.getRoutingTable().containsKey("p2"));             
        assertEquals(portal2Expected.containsKey("u1"),p2.getRoutingTable().containsKey("u1"));
        assertEquals(portal2Expected.containsKey("u2"),p2.getRoutingTable().containsKey("u2"));
        assertEquals(portal2Expected.containsKey("u3"),p2.getRoutingTable().containsKey("u3"));
        assertEquals(portal2Expected.get("r1"),p2.getRoutingTable().get("r1"));
        assertEquals(portal2Expected.get("p1"),p2.getRoutingTable().get("p1"));        
        assertEquals(portal2Expected.get("p2"),p2.getRoutingTable().get("p2"));               
        assertEquals(portal2Expected.get("u1"),p2.getRoutingTable().get("u1"));
        assertEquals(portal2Expected.get("u2"),p2.getRoutingTable().get("u2"));
        assertEquals(portal2Expected.get("u3"),p2.getRoutingTable().get("u3"));
        assertEquals(portal2Expected.get("u4"),p2.getRoutingTable().get("u4")); 
        assertEquals(null,u3.getPortal());
        assertEquals(null,u4.getPortal());
        assertFalse(u1.getPortal() == null);
        assertFalse(u2.getPortal() == null);
    }   
}
