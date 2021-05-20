package metaagentsubsystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Group B, Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class ThreeRoutersTestGeneralMethods 
{
    private ByteArrayOutputStream sysOut;
    private Router r1, r2, r3;
    private Portal p1, p2, p3, p4, p5, p6, p7;
    private UserAgent u1, u2, u3, u4, u5, u6, u7, u8, u9, u10, u11;
    static int sleepTime;
    private TreeMap<String, MetaAgent> router1Expected, router2Expected, router3Expected,
            portal1Expected,portal2Expected,portal3Expected,portal4Expected,
            portal5Expected,portal6Expected,portal7Expected;
    
    /**
     * Method used to call the test
     */
    public ThreeRoutersTestGeneralMethods() {
    }
    
    /**
     * Initialise variables for all tests
     * @throws java.lang.NoSuchFieldException
     * @throws java.lang.IllegalAccessException
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */  
    @BeforeClass
    public static void setUpClass() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InterruptedException, IOException 
    {
        sleepTime = 3000;
    }
    
    /**
     * Setup basic network for each test
     * @throws java.lang.NoSuchFieldException
     * @throws java.lang.InterruptedException
     * @throws java.lang.IllegalAccessException
     * @throws java.io.IOException
     */      
    @Before
    public void setUp() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InterruptedException, IOException
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
     * Setup basic network
     * @throws java.lang.NoSuchFieldException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */   
    public void basicNetworkSetup() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InterruptedException, IOException
    {
        r1 = Router.createNew("r1");
        p1 = new Portal("p1",r1);
        u1 = new UserAgent("u1", p1);
        u2 = new UserAgent("u2", p1);
        p2 = new Portal("p2",r1);
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
        resetRouter();
        r3 = Router.createNew("r3");
        p5 = new Portal("p5",r3);
        u9 = new UserAgent("u9", p5);        
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
        r2.newConnection(InetAddress.getLocalHost().getHostAddress().trim(), port);
        Thread.sleep(sleepTime);
        r3.advertiseConnection(port + 2000);
        r3.newConnection(InetAddress.getLocalHost().getHostAddress().trim(), port);        
        Thread.sleep(sleepTime);
    }
    
    /**
     * Test triple router setup successfully overrides singleton
     */
    @Test
    public void checkTripleRouterSetup()
    {
        assertFalse(r1 == r2);
        assertFalse(r1 == r3);
        assertFalse(r3 == r2);
    }
    
    /**
     * Test of getIpAddress method, of class Router.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testGetIpAddress() throws IOException, InterruptedException 
    {
        advertise(8028);
        String expected = InetAddress.getLocalHost().getHostAddress().trim();
        assertEquals(expected,r1.getIpAdress());
        assertEquals(expected,r2.getIpAdress());
        assertEquals(expected,r3.getIpAdress());
    }
    
    /**
     * Test of getPort method, of class Router.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testGetPort() throws IOException, InterruptedException
    {
        advertise(8029);
        assertEquals(8029,r1.getPort());
        assertEquals(9029,r2.getPort());
        assertEquals(10029,r3.getPort());
    }  
    
    /**
     * Test of getPortalHandles method, of class Router.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testGetPortalHandles() throws IOException, InterruptedException
    {
        advertise(8030);
        List<String> expected = new ArrayList<>();
        expected.add("p1");
        expected.add("p2");
        expected.add("r1");
        expected.add("u1");
        expected.add("u2");
        expected.add("u3");
        expected.add("u4");
        expected.add("p3");
        expected.add("p4");
        expected.add("r2");
        expected.add("u5");
        expected.add("u6");
        expected.add("u7");
        expected.add("u8");
        expected.add("r3");
        expected.add("p5");
        expected.add("u9");
        List<String> actual = r1.getPortalHandles();      
        assertThat(expected.size(),is(actual.size()));
        assertEquals(expected.contains("p1"),actual.contains("p1"));
        assertEquals(expected.contains("p2"),actual.contains("p2"));
        assertEquals(expected.contains("r1"),actual.contains("r1"));
        assertEquals(expected.contains("u1"),actual.contains("u1"));
        assertEquals(expected.contains("u2"),actual.contains("u2"));
        assertEquals(expected.contains("u3"),actual.contains("u3"));
        assertEquals(expected.contains("u4"),actual.contains("u4"));
        assertEquals(expected.contains("p3"),actual.contains("p3"));
        assertEquals(expected.contains("p4"),actual.contains("p4"));
        assertEquals(expected.contains("r2"),actual.contains("r2"));
        assertEquals(expected.contains("u5"),actual.contains("u5"));
        assertEquals(expected.contains("u6"),actual.contains("u6"));
        assertEquals(expected.contains("u7"),actual.contains("u7"));
        assertEquals(expected.contains("u8"),actual.contains("u8"));
        assertEquals(expected.contains("u8"),actual.contains("u9"));
        assertEquals(expected.contains("u8"),actual.contains("p5"));
        assertEquals(expected.contains("u8"),actual.contains("r3"));
    }  
    
    /**
     * Test of getInstance method, of class Router.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testGetInstance() throws IOException, InterruptedException
    {  
        advertise(8031);
        assertFalse(r1.equals(Router.getInstance()));
        assertFalse(r2.equals(Router.getInstance()));
        assertTrue(r3.equals(Router.getInstance()));
    }  
    
    /**
     * Test of addAgent method, of class Router.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testAddPortalAgentAlreadyAdded() throws InterruptedException, IOException
    {  
        sysOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(sysOut));
        advertise(8035);
        Thread.sleep(sleepTime);
        String expected = "Agent already exists" + 
                System.getProperty("line.separator");
        Thread.sleep(sleepTime);
        r1.addAgent(p1);
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 22));        
    }  
    
    /**
     * Test of addAgent method, of class Router.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testAddUserAgentAlreadyAdded() throws InterruptedException, IOException
    {  
        sysOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(sysOut));
        advertise(8036);
        String expected = "Agent already exists" + 
                System.getProperty("line.separator");
        r1.addAgent(u1);
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 22));        
    }   

    /**
     * Test of addAgent method, of class Router.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testAddUserAgentToRouter() throws InterruptedException, IOException
    {  
        sysOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(sysOut));
        advertise(8037);
        p6 = new Portal("p6",r1);
        Thread.sleep(sleepTime);
        u11 = new UserAgent("u11",p6);
        String expected = "Only portals and sockets are allowed to add on a router" + 
                System.getProperty("line.separator");
        Thread.sleep(sleepTime);
        p6.removeAgent("u11");
        Thread.sleep(sleepTime);
        r1.addAgent(u11);
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 57));        
    }  
    
    /**
     * Test of removeAgent (null) method, of class UserAgent.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test (expected = NullPointerException.class)
    public void testRemoveNullAgent() throws InterruptedException, IOException 
    {
        advertise(8021);
        r1.removeAgent(null);
    }  
}