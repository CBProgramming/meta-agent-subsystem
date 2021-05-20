package metaagentsubsystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;;
import java.net.InetAddress;
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
public class ThreeRoutersTestHashMaps
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
    public ThreeRoutersTestHashMaps() {
    }
    
    /**
     * Initialise variables for all tests
     */  
    @BeforeClass
    public static void setUpClass()
    {
        sleepTime = 1000;
    }
    
    /**
     * Setup basic network for each test
     * @throws java.lang.NoSuchFieldException
     * @throws java.lang.InterruptedException
     * @throws java.lang.IllegalAccessException
     * @throws java.io.IOException
     */   
    @Before
    public void setUp() throws NoSuchFieldException, IllegalArgumentException, IllegalArgumentException, IllegalAccessException, InterruptedException, IOException
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
     */     
    public void basicNetworkSetup() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InterruptedException
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
     * Setup expected tree maps
     */  
    public void expectedHashMapSetup()
    {
        //first network
        router1Expected = new TreeMap<>();
        router1Expected.put("r1", r1);         
        router1Expected.put("p1", p1);
        router1Expected.put("p2", p2);        
        router1Expected.put("u1", p1);
        router1Expected.put("u2", p1);
        router1Expected.put("u3", p2);
        router1Expected.put("u4", p2);
        router1Expected.put("r2", null); //Pointer to socket. Cannot access socket pointer in tests, can test value.handle and can also test it is of type socket
        router1Expected.put("p3", null);
        router1Expected.put("p4", null);        
        router1Expected.put("u5", null);
        router1Expected.put("u6", null);
        router1Expected.put("u7", null);
        router1Expected.put("u8", null);
        router1Expected.put("r3", null);
        router1Expected.put("p5", null);
        router1Expected.put("u9", null);        
        portal1Expected = new TreeMap<>();
        portal1Expected.put("r1", r1);         
        portal1Expected.put("p1", p1);        
        portal1Expected.put("p2", r1);        
        portal1Expected.put("u1", u1);
        portal1Expected.put("u2", u2);
        portal1Expected.put("u3", r1);
        portal1Expected.put("u4", r1);
        portal1Expected.put("p5", r1);
        portal1Expected.put("u9", r1); 
        //portal1Expected.put("r2", r1);         
        portal1Expected.put("p3", r1);
        portal1Expected.put("p4", r1);        
        portal1Expected.put("u5", r1);
        portal1Expected.put("u6", r1);
        portal1Expected.put("u7", r1);
        portal1Expected.put("u8", r1);
        portal2Expected = new TreeMap<>();
        portal2Expected.put("r1", r1);          
        portal2Expected.put("p1", r1);        
        portal2Expected.put("p2", p2);        
        portal2Expected.put("u1", r1);
        portal2Expected.put("u2", r1);
        portal2Expected.put("u3", u3);
        portal2Expected.put("u4", u4);
        //portal2Expected.put("r2", r1);         
        portal2Expected.put("p3", r1);
        portal2Expected.put("p4", r1);        
        portal2Expected.put("u5", r1);
        portal2Expected.put("u6", r1);
        portal2Expected.put("u7", r1);
        portal2Expected.put("u8", r1);
        portal2Expected.put("p5", r1);
        portal2Expected.put("u9", r1); 
        
        
        //second network
        router2Expected = new TreeMap<>();
        router2Expected.put("r1", null);         
        router2Expected.put("p1", null);
        router2Expected.put("p2", null);        
        router2Expected.put("u1", null);
        router2Expected.put("u2", null);
        router2Expected.put("u3", null);
        router2Expected.put("u4", null);
        router2Expected.put("p5", null);
        router2Expected.put("u9", null); 
        router2Expected.put("r2", r2);         
        router2Expected.put("p3", p3);
        router2Expected.put("p4", p4);        
        router2Expected.put("u5", p3);
        router2Expected.put("u6", p3);
        router2Expected.put("u7", p4);
        router2Expected.put("u8", p4);
        portal3Expected = new TreeMap<>();
        //portal3Expected.put("r1", r2);         
        portal3Expected.put("p1", r2);
        portal3Expected.put("p2", r2);        
        portal3Expected.put("u1", r2);
        portal3Expected.put("u2", r2);
        portal3Expected.put("u3", r2);
        portal3Expected.put("u4", r2);
        portal3Expected.put("r2", r2);         
        portal3Expected.put("p3", p3);        
        portal3Expected.put("p4", r2);        
        portal3Expected.put("u5", u5);
        portal3Expected.put("u6", u6);
        portal3Expected.put("u7", r2);
        portal3Expected.put("u8", r2);
        portal3Expected.put("p5", r2);
        portal3Expected.put("u9", r2);        
        portal4Expected = new TreeMap<>();
        //portal4Expected.put("r1", r2);         
        portal4Expected.put("p1", r2);
        portal4Expected.put("p2", r2);        
        portal4Expected.put("u1", r2);
        portal4Expected.put("u2", r2);
        portal4Expected.put("u3", r2);
        portal4Expected.put("u4", r2);
        portal4Expected.put("r2", r2);          
        portal4Expected.put("p3", r2);        
        portal4Expected.put("p4", p4);        
        portal4Expected.put("u5", r2);
        portal4Expected.put("u6", r2);
        portal4Expected.put("u7", u7);
        portal4Expected.put("u8", u8);
        portal4Expected.put("p5", r2);
        portal4Expected.put("u9", r2); 
        
        //third network
        router3Expected = new TreeMap<>();
        router3Expected.put("r1", null);         
        router3Expected.put("p1", null);
        router3Expected.put("p2", null);        
        router3Expected.put("u1", null);
        router3Expected.put("u2", null);
        router3Expected.put("u3", null);
        router3Expected.put("u4", null);
        router3Expected.put("p5", p5);
        router3Expected.put("u9", p5); 
        router3Expected.put("r2", null);  
        router3Expected.put("r3", r3); 
        router3Expected.put("p3", null);
        router3Expected.put("p4", null);        
        router3Expected.put("u5", null);
        router3Expected.put("u6", null);
        router3Expected.put("u7", null);
        router3Expected.put("u8", null);
        portal5Expected = new TreeMap<>();
        //portal3Expected.put("r1", r2);         
        portal5Expected.put("p1", r3);
        portal5Expected.put("p2", r3);        
        portal5Expected.put("u1", r3);
        portal5Expected.put("u2", r3);
        portal5Expected.put("u3", r3);
        portal5Expected.put("u4", r3);
        portal5Expected.put("r2", r3); 
        portal5Expected.put("r3", r3);          
        portal5Expected.put("p3", r3);        
        portal5Expected.put("p4", r3);        
        portal5Expected.put("u5", r3);
        portal5Expected.put("u6", r3);
        portal5Expected.put("u7", r3);
        portal5Expected.put("u8", r3);
        portal5Expected.put("p5", p5);
        portal5Expected.put("u9", u9);
    } 
    
    /**
     * Remove key from expected hash maps
     * @param key key to be removed
     */  
    public void removeKeyFromExpected(String key)
    {
        router1Expected.remove(key);
        portal1Expected.remove(key);
        portal2Expected.remove(key);
        router2Expected.remove(key);
        portal3Expected.remove(key);
        portal4Expected.remove(key);
        router3Expected.remove(key);
        portal5Expected.remove(key);
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
     * Connect three routers
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
     * Check routing tables are correct
     * @return true if all tests pass (to be asserted by other tests)
     */  
    public boolean checkRoutingTables()
    {
        try
        {
        //check r1
        assertTrue(r1.getRoutingTable().keySet().equals(router1Expected.keySet()));
        assertThat(r1.getRoutingTable().size(), is(router1Expected.size()));          
        assertEquals(router1Expected.get("r1"),r1.getRoutingTable().get("r1"));
        assertEquals(router1Expected.get("p1"),r1.getRoutingTable().get("p1"));
        assertEquals(router1Expected.get("p2"),r1.getRoutingTable().get("p2"));
        assertEquals(router1Expected.get("u1"),r1.getRoutingTable().get("u1"));
        assertEquals(router1Expected.get("u2"),r1.getRoutingTable().get("u2"));
        assertEquals(router1Expected.get("u3"),r1.getRoutingTable().get("u3"));
        assertEquals(router1Expected.get("u4"),r1.getRoutingTable().get("u4"));
        assertEquals(MetaAgentType.SOCKET,r1.getRoutingTable().get("r2").getType());
        assertEquals(MetaAgentType.SOCKET,r1.getRoutingTable().get("p3").getType());
        assertEquals(MetaAgentType.SOCKET,r1.getRoutingTable().get("p4").getType());
        assertEquals(MetaAgentType.SOCKET,r1.getRoutingTable().get("r3").getType());
        assertEquals(MetaAgentType.SOCKET,r1.getRoutingTable().get("u9").getType());
        assertEquals(MetaAgentType.SOCKET,r1.getRoutingTable().get("p5").getType());
        if (router1Expected.containsKey("p7"))
        {
            assertEquals(MetaAgentType.SOCKET,r1.getRoutingTable().get("p7").getType());
        }
        if (router1Expected.containsKey("p6"))
        {
            assertEquals(MetaAgentType.SOCKET,r1.getRoutingTable().get("p6").getType());
        }
        if (router1Expected.containsKey("u10"))
        {
            assertEquals(MetaAgentType.SOCKET,r1.getRoutingTable().get("u10").getType());
        }         
        if (router1Expected.containsKey("u11"))
        {
            assertEquals(MetaAgentType.SOCKET,r1.getRoutingTable().get("u11").getType());
        }  
        assertEquals(MetaAgentType.SOCKET,r1.getRoutingTable().get("u5").getType());
        assertEquals(MetaAgentType.SOCKET,r1.getRoutingTable().get("u6").getType());
        assertEquals(MetaAgentType.SOCKET,r1.getRoutingTable().get("u7").getType());
        if (router1Expected.containsKey("u8"))
        {
            assertEquals(MetaAgentType.SOCKET,r1.getRoutingTable().get("u8").getType());
        }

        System.out.println("routing table for r2");
        printRoutingTable(r2);        
        
        //check r2
        assertTrue(r2.getRoutingTable().keySet().equals(router2Expected.keySet()));
        assertThat(r2.getRoutingTable().size(), is(router2Expected.size()));          
        assertEquals(router2Expected.get("r2"),r2.getRoutingTable().get("r2"));
        assertEquals(router2Expected.get("p3"),r2.getRoutingTable().get("p3"));
        assertEquals(router2Expected.get("p4"),r2.getRoutingTable().get("p4"));
        assertEquals(router2Expected.get("u5"),r2.getRoutingTable().get("u5"));
        assertEquals(router2Expected.get("u6"),r2.getRoutingTable().get("u6"));
        assertEquals(router2Expected.get("u7"),r2.getRoutingTable().get("u7"));
        assertEquals(router2Expected.get("u8"),r2.getRoutingTable().get("u8"));
        assertEquals(router2Expected.get("u11"),r2.getRoutingTable().get("u11"));
        assertEquals(router2Expected.get("p7"),r2.getRoutingTable().get("p7"));
        assertEquals(MetaAgentType.SOCKET,r2.getRoutingTable().get("r1").getType());
        assertEquals(MetaAgentType.SOCKET,r2.getRoutingTable().get("p1").getType());
        //assertEquals(MetaAgentType.SOCKET,r2.getRoutingTable().get("r3").getType());
        assertEquals(MetaAgentType.SOCKET,r2.getRoutingTable().get("p5").getType());
        if (router2Expected.containsKey("u10"))
        {      
            assertEquals(MetaAgentType.SOCKET,r2.getRoutingTable().get("u10").getType());
        }           
        if (router2Expected.containsKey("p6"))
        {      
            assertEquals(MetaAgentType.SOCKET,r2.getRoutingTable().get("p6").getType());
        }       
        if (router2Expected.containsKey("p2"))
        {      
            assertEquals(MetaAgentType.SOCKET,r2.getRoutingTable().get("p2").getType());
        }
        assertEquals(MetaAgentType.SOCKET,r2.getRoutingTable().get("u1").getType());
        assertEquals(MetaAgentType.SOCKET,r2.getRoutingTable().get("u2").getType());
        if (router2Expected.containsKey("u3"))
        {                  
            assertEquals(MetaAgentType.SOCKET,r2.getRoutingTable().get("u3").getType());
        }
        if (router2Expected.containsKey("u4"))
        {       
            assertEquals(MetaAgentType.SOCKET,r2.getRoutingTable().get("u4").getType());
        }
        if (router2Expected.containsKey("u9"))
        {
           assertEquals(MetaAgentType.SOCKET,r2.getRoutingTable().get("u9").getType());
        }     

        System.out.println("routing table for r3");
        printRoutingTable(r3);  

        //check r3
        assertTrue(r3.getRoutingTable().keySet().equals(router3Expected.keySet()));
        assertThat(r3.getRoutingTable().size(), is(router3Expected.size()));          
        assertEquals(router3Expected.get("r3"),r3.getRoutingTable().get("r3"));
        assertEquals(router3Expected.get("p5"),r3.getRoutingTable().get("p5"));
        assertEquals(router3Expected.get("p6"),r3.getRoutingTable().get("p6"));
        assertEquals(router3Expected.get("u9"),r3.getRoutingTable().get("u9"));
        assertEquals(router3Expected.get("u10"),r3.getRoutingTable().get("u10"));
        assertEquals(MetaAgentType.SOCKET,r3.getRoutingTable().get("r2").getType());
        assertEquals(MetaAgentType.SOCKET,r3.getRoutingTable().get("p3").getType());
        assertEquals(MetaAgentType.SOCKET,r3.getRoutingTable().get("p4").getType());         
        assertEquals(MetaAgentType.SOCKET,r3.getRoutingTable().get("u5").getType());
        assertEquals(MetaAgentType.SOCKET,r3.getRoutingTable().get("u6").getType());
        assertEquals(MetaAgentType.SOCKET,r3.getRoutingTable().get("u7").getType());
        assertEquals(MetaAgentType.SOCKET,r3.getRoutingTable().get("r1").getType());
        assertEquals(MetaAgentType.SOCKET,r3.getRoutingTable().get("p1").getType());
        if (router3Expected.containsKey("u8"))
        {
            assertEquals(MetaAgentType.SOCKET,r3.getRoutingTable().get("u8").getType());
        }  
        if (router3Expected.containsKey("u11"))
        {
            assertEquals(MetaAgentType.SOCKET,r3.getRoutingTable().get("u11").getType());
        }  
        if (router3Expected.containsKey("p7"))
        {
            assertEquals(MetaAgentType.SOCKET,r3.getRoutingTable().get("p7").getType());
        }         
        if (router3Expected.containsKey("p2"))
        {      
            assertEquals(MetaAgentType.SOCKET,r3.getRoutingTable().get("p2").getType());
        }
        assertEquals(MetaAgentType.SOCKET,r3.getRoutingTable().get("u1").getType());
        assertEquals(MetaAgentType.SOCKET,r3.getRoutingTable().get("u2").getType());
        if (router3Expected.containsKey("u3"))
        {                  
            assertEquals(MetaAgentType.SOCKET,r3.getRoutingTable().get("u3").getType());
        }
        if (router3Expected.containsKey("u4"))
        {       
            assertEquals(MetaAgentType.SOCKET,r3.getRoutingTable().get("u4").getType());
        }
        
        //check p1
        assertTrue(p1.getRoutingTable().keySet().equals(portal1Expected.keySet()));
        assertThat(p1.getRoutingTable().size(), is(portal1Expected.size()));          
        assertEquals(portal1Expected.get("r1"),p1.getRoutingTable().get("r1"));
        assertEquals(portal1Expected.get("p1"),p1.getRoutingTable().get("p1"));
        assertEquals(portal1Expected.get("p2"),p1.getRoutingTable().get("p2"));
        assertEquals(portal1Expected.get("u1"),p1.getRoutingTable().get("u1"));
        assertEquals(portal1Expected.get("u2"),p1.getRoutingTable().get("u2"));
        assertEquals(portal1Expected.get("u3"),p1.getRoutingTable().get("u3"));
        assertEquals(portal1Expected.get("u4"),p1.getRoutingTable().get("u4"));
        assertEquals(portal1Expected.get("r2"),p1.getRoutingTable().get("r2"));
        assertEquals(portal1Expected.get("p3"),p1.getRoutingTable().get("p3"));
        assertEquals(portal1Expected.get("p4"),p1.getRoutingTable().get("p4"));
        assertEquals(portal1Expected.get("u5"),p1.getRoutingTable().get("u5"));
        assertEquals(portal1Expected.get("u6"),p1.getRoutingTable().get("u6"));
        assertEquals(portal1Expected.get("u7"),p1.getRoutingTable().get("u7"));
        assertEquals(portal1Expected.get("u8"),p1.getRoutingTable().get("u8"));
        assertEquals(portal1Expected.get("p5"),p1.getRoutingTable().get("p5"));
        assertEquals(portal1Expected.get("p6"),p1.getRoutingTable().get("p6"));
        assertEquals(portal1Expected.get("u9"),p1.getRoutingTable().get("u9"));
        assertEquals(portal1Expected.get("u10"),p1.getRoutingTable().get("u10"));
        assertEquals(portal1Expected.get("p7"),p1.getRoutingTable().get("p7"));
        assertEquals(portal1Expected.get("u11"),p1.getRoutingTable().get("u11"));

        //check p2
        assertTrue(p2.getRoutingTable().keySet().equals(portal2Expected.keySet()));
        assertThat(p2.getRoutingTable().size(), is(portal2Expected.size()));          
        assertEquals(portal2Expected.get("r1"),p2.getRoutingTable().get("r1"));
        assertEquals(portal2Expected.get("p1"),p2.getRoutingTable().get("p1"));
        assertEquals(portal2Expected.get("p2"),p2.getRoutingTable().get("p2"));
        assertEquals(portal2Expected.get("u1"),p2.getRoutingTable().get("u1"));
        assertEquals(portal2Expected.get("u2"),p2.getRoutingTable().get("u2"));
        assertEquals(portal2Expected.get("u3"),p2.getRoutingTable().get("u3"));
        assertEquals(portal2Expected.get("u4"),p2.getRoutingTable().get("u4"));
        assertEquals(portal2Expected.get("r2"),p2.getRoutingTable().get("r2"));
        assertEquals(portal2Expected.get("p3"),p2.getRoutingTable().get("p3"));
        assertEquals(portal2Expected.get("p4"),p2.getRoutingTable().get("p4"));
        assertEquals(portal2Expected.get("u5"),p2.getRoutingTable().get("u5"));
        assertEquals(portal2Expected.get("u6"),p2.getRoutingTable().get("u6"));
        assertEquals(portal2Expected.get("u7"),p2.getRoutingTable().get("u7"));
        assertEquals(portal2Expected.get("u8"),p2.getRoutingTable().get("u8"));
        assertEquals(portal2Expected.get("p5"),p2.getRoutingTable().get("p5"));
        assertEquals(portal2Expected.get("p6"),p2.getRoutingTable().get("p6"));
        assertEquals(portal2Expected.get("u9"),p2.getRoutingTable().get("u9"));
        assertEquals(portal2Expected.get("u10"),p2.getRoutingTable().get("u10"));
        assertEquals(portal2Expected.get("p7"),p2.getRoutingTable().get("p7"));
        assertEquals(portal2Expected.get("u11"),p2.getRoutingTable().get("u11"));

        //check p3
        assertTrue(p3.getRoutingTable().keySet().equals(portal3Expected.keySet()));
        assertThat(p3.getRoutingTable().size(), is(portal3Expected.size()));          
        assertEquals(portal3Expected.get("r1"),p3.getRoutingTable().get("r1"));
        assertEquals(portal3Expected.get("p1"),p3.getRoutingTable().get("p1"));
        assertEquals(portal3Expected.get("p2"),p3.getRoutingTable().get("p2"));
        assertEquals(portal3Expected.get("u1"),p3.getRoutingTable().get("u1"));
        assertEquals(portal3Expected.get("u2"),p3.getRoutingTable().get("u2"));
        assertEquals(portal3Expected.get("u3"),p3.getRoutingTable().get("u3"));
        assertEquals(portal3Expected.get("u4"),p3.getRoutingTable().get("u4"));
        assertEquals(portal3Expected.get("r2"),p3.getRoutingTable().get("r2"));
        assertEquals(portal3Expected.get("p3"),p3.getRoutingTable().get("p3"));
        assertEquals(portal3Expected.get("p4"),p3.getRoutingTable().get("p4"));
        assertEquals(portal3Expected.get("u5"),p3.getRoutingTable().get("u5"));
        assertEquals(portal3Expected.get("u6"),p3.getRoutingTable().get("u6"));
        assertEquals(portal3Expected.get("u7"),p3.getRoutingTable().get("u7"));
        assertEquals(portal3Expected.get("u8"),p3.getRoutingTable().get("u8"));
        assertEquals(portal3Expected.get("p5"),p3.getRoutingTable().get("p5"));
        assertEquals(portal3Expected.get("p6"),p3.getRoutingTable().get("p6"));
        assertEquals(portal3Expected.get("u9"),p3.getRoutingTable().get("u9"));
        assertEquals(portal3Expected.get("u10"),p3.getRoutingTable().get("u10"));
        assertEquals(portal3Expected.get("p7"),p3.getRoutingTable().get("p7"));
        assertEquals(portal3Expected.get("u11"),p3.getRoutingTable().get("u11"));

        System.out.println("routing table for p4");
        printRoutingTable(p4);        
        
        //check p4
        assertTrue(p4.getRoutingTable().keySet().equals(portal4Expected.keySet()));
        assertThat(p4.getRoutingTable().size(), is(portal4Expected.size()));          
        assertEquals(portal4Expected.get("r1"),p4.getRoutingTable().get("r1"));
        assertEquals(portal4Expected.get("p1"),p4.getRoutingTable().get("p1"));
        assertEquals(portal4Expected.get("p2"),p4.getRoutingTable().get("p2"));
        assertEquals(portal4Expected.get("u1"),p4.getRoutingTable().get("u1"));
        assertEquals(portal4Expected.get("u2"),p4.getRoutingTable().get("u2"));
        assertEquals(portal4Expected.get("u3"),p4.getRoutingTable().get("u3"));
        assertEquals(portal4Expected.get("u4"),p4.getRoutingTable().get("u4"));
        assertEquals(portal4Expected.get("r2"),p4.getRoutingTable().get("r2"));
        assertEquals(portal4Expected.get("p3"),p4.getRoutingTable().get("p3"));
        assertEquals(portal4Expected.get("p4"),p4.getRoutingTable().get("p4"));
        assertEquals(portal4Expected.get("u5"),p4.getRoutingTable().get("u5"));
        assertEquals(portal4Expected.get("u6"),p4.getRoutingTable().get("u6"));
        assertEquals(portal4Expected.get("u7"),p4.getRoutingTable().get("u7"));
        assertEquals(portal4Expected.get("u8"),p4.getRoutingTable().get("u8"));
        assertEquals(portal4Expected.get("p5"),p4.getRoutingTable().get("p5"));
        assertEquals(portal4Expected.get("p6"),p4.getRoutingTable().get("p6"));
        assertEquals(portal4Expected.get("u9"),p4.getRoutingTable().get("u9"));
        assertEquals(portal4Expected.get("u10"),p4.getRoutingTable().get("u10"));
        assertEquals(portal4Expected.get("p7"),p4.getRoutingTable().get("p7"));
        assertEquals(portal4Expected.get("u11"),p4.getRoutingTable().get("u11"));

        System.out.println("routing table for p5");
        printRoutingTable(p5);        
        
         //check p5
        //assertTrue(p5.getRoutingTable().keySet().equals(portal5Expected.keySet()));
        assertThat(p5.getRoutingTable().size(), is(portal5Expected.size()));          
        assertEquals(portal5Expected.get("r1"),p5.getRoutingTable().get("r1"));
        assertEquals(portal5Expected.get("p1"),p5.getRoutingTable().get("p1"));
        assertEquals(portal5Expected.get("p2"),p5.getRoutingTable().get("p2"));
        assertEquals(portal5Expected.get("u1"),p5.getRoutingTable().get("u1"));
        assertEquals(portal5Expected.get("u2"),p5.getRoutingTable().get("u2"));
        assertEquals(portal5Expected.get("u3"),p5.getRoutingTable().get("u3"));
        assertEquals(portal5Expected.get("u4"),p5.getRoutingTable().get("u4"));
        assertEquals(portal5Expected.get("r2"),p5.getRoutingTable().get("r2"));
        assertEquals(portal5Expected.get("p3"),p5.getRoutingTable().get("p3"));
        assertEquals(portal5Expected.get("p4"),p5.getRoutingTable().get("p4"));
        assertEquals(portal5Expected.get("u5"),p5.getRoutingTable().get("u5"));
        assertEquals(portal5Expected.get("u6"),p5.getRoutingTable().get("u6"));
        assertEquals(portal5Expected.get("u7"),p5.getRoutingTable().get("u7"));
        assertEquals(portal5Expected.get("u8"),p5.getRoutingTable().get("u8"));
        assertEquals(portal5Expected.get("p5"),p5.getRoutingTable().get("p5"));
        assertEquals(portal5Expected.get("p6"),p5.getRoutingTable().get("p6"));
        assertEquals(portal5Expected.get("u9"),p5.getRoutingTable().get("u9"));
        assertEquals(portal5Expected.get("u10"),p5.getRoutingTable().get("u10"));                  
        assertEquals(portal5Expected.get("p7"),p5.getRoutingTable().get("p7"));
        assertEquals(portal5Expected.get("u11"),p5.getRoutingTable().get("u11"));

        //check p6
        if (p6 != null)
        {
            System.out.println("routing table for p6");
            printRoutingTable(p6);  
            
            assertTrue(p6.getRoutingTable().keySet().equals(portal6Expected.keySet()));
            assertThat(p6.getRoutingTable().size(), is(portal6Expected.size()));          
            assertEquals(portal6Expected.get("r1"),p6.getRoutingTable().get("r1"));
            assertEquals(portal6Expected.get("p1"),p6.getRoutingTable().get("p1"));
            assertEquals(portal6Expected.get("p2"),p6.getRoutingTable().get("p2"));
            assertEquals(portal6Expected.get("u1"),p6.getRoutingTable().get("u1"));
            assertEquals(portal6Expected.get("u2"),p6.getRoutingTable().get("u2"));
            assertEquals(portal6Expected.get("u3"),p6.getRoutingTable().get("u3"));
            assertEquals(portal6Expected.get("u4"),p6.getRoutingTable().get("u4"));
            assertEquals(portal6Expected.get("r2"),p6.getRoutingTable().get("r2"));
            assertEquals(portal6Expected.get("p3"),p6.getRoutingTable().get("p3"));
            assertEquals(portal6Expected.get("p4"),p6.getRoutingTable().get("p4"));
            assertEquals(portal6Expected.get("u5"),p6.getRoutingTable().get("u5"));
            assertEquals(portal6Expected.get("u6"),p6.getRoutingTable().get("u6"));
            assertEquals(portal6Expected.get("u7"),p6.getRoutingTable().get("u7"));
            assertEquals(portal6Expected.get("u8"),p6.getRoutingTable().get("u8"));
            assertEquals(portal6Expected.get("p5"),p6.getRoutingTable().get("p5"));
            assertEquals(portal6Expected.get("p6"),p6.getRoutingTable().get("p6"));
            assertEquals(portal6Expected.get("u9"),p6.getRoutingTable().get("u9"));
            assertEquals(portal6Expected.get("u10"),p6.getRoutingTable().get("u10")); 
            assertEquals(portal6Expected.get("p7"),p6.getRoutingTable().get("p7"));
            assertEquals(portal6Expected.get("u11"),p6.getRoutingTable().get("u11"));
        }

        //check p7
        if (p7 != null)
        {
            System.out.println("routing table for p7");
            printRoutingTable(p7);  
            
            assertTrue(p7.getRoutingTable().keySet().equals(portal7Expected.keySet()));
            assertThat(p7.getRoutingTable().size(), is(portal7Expected.size()));          
            assertEquals(portal7Expected.get("r1"),p7.getRoutingTable().get("r1"));
            assertEquals(portal7Expected.get("p1"),p7.getRoutingTable().get("p1"));
            assertEquals(portal7Expected.get("p2"),p7.getRoutingTable().get("p2"));
            assertEquals(portal7Expected.get("u1"),p7.getRoutingTable().get("u1"));
            assertEquals(portal7Expected.get("u2"),p7.getRoutingTable().get("u2"));
            assertEquals(portal7Expected.get("u3"),p7.getRoutingTable().get("u3"));
            assertEquals(portal7Expected.get("u4"),p7.getRoutingTable().get("u4"));
            assertEquals(portal7Expected.get("r2"),p7.getRoutingTable().get("r2"));
            assertEquals(portal7Expected.get("p3"),p7.getRoutingTable().get("p3"));
            assertEquals(portal7Expected.get("p4"),p7.getRoutingTable().get("p4"));
            assertEquals(portal7Expected.get("u5"),p7.getRoutingTable().get("u5"));
            assertEquals(portal7Expected.get("u6"),p7.getRoutingTable().get("u6"));
            assertEquals(portal7Expected.get("u7"),p7.getRoutingTable().get("u7"));
            assertEquals(portal7Expected.get("u8"),p7.getRoutingTable().get("u8"));
            assertEquals(portal7Expected.get("p5"),p7.getRoutingTable().get("p5"));
            assertEquals(portal7Expected.get("p6"),p7.getRoutingTable().get("p6"));
            assertEquals(portal7Expected.get("u9"),p7.getRoutingTable().get("u9"));
            assertEquals(portal7Expected.get("u10"),p7.getRoutingTable().get("u10")); 
            assertEquals(portal7Expected.get("p7"),p7.getRoutingTable().get("p7"));
            assertEquals(portal7Expected.get("u11"),p7.getRoutingTable().get("u11"));
        }
            return true;
        }
        catch(AssertionError e)
        {
            return false;
        }
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
     * Check routing tables for basic setup
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testBasicSetup() throws IOException, InterruptedException
    {
        advertise(8000);
        Thread.sleep(sleepTime);
        assertTrue(checkRoutingTables());
    }
    
    /**
     * Check routing tables when adding agents
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testAddPortalAndUserHashMapCheck() throws IllegalArgumentException, IOException, InterruptedException
    {
        advertise(8001);
        p6 = new Portal("p6",r3);
        Thread.sleep(sleepTime);
        u10 = new UserAgent("u10",p6);
        Thread.sleep(sleepTime);
        //additional expected outcomes
        router1Expected.put("p6", null);
        router1Expected.put("u10", null);
        portal1Expected.put("p6", r1);
        portal1Expected.put("u10", r1);          
        portal2Expected.put("p6", r1);
        portal2Expected.put("u10", r1);
        router2Expected.put("p6", null);
        router2Expected.put("u10", null);
        portal3Expected.put("p6", r2);
        portal3Expected.put("u10", r2);          
        portal4Expected.put("p6", r2);
        portal4Expected.put("u10", r2); 
        portal5Expected.put("p6", r3);
        portal5Expected.put("u10", r3); 
        router3Expected.put("p6", p6);
        router3Expected.put("u10", p6);        
        portal6Expected = new TreeMap<>();
        portal6Expected.put("u1", r3);
        portal6Expected.put("u2", r3);
        portal6Expected.put("p1", r3);
        portal6Expected.put("u3", r3);
        portal6Expected.put("u4", r3);
        portal6Expected.put("p2", r3);
        portal6Expected.put("r1", r3);        
        portal6Expected.put("p5", r3);
        portal6Expected.put("u9", r3); 
        portal6Expected.put("u5", r3);
        portal6Expected.put("u6", r3);
        portal6Expected.put("p3", r3);
        portal6Expected.put("u7", r3);
        portal6Expected.put("u8", r3);
        portal6Expected.put("p4", r3);
        portal6Expected.put("r2", r3);     
        portal6Expected.put("r3", r3); 
        portal6Expected.put("p6", p6);
        portal6Expected.put("u10", u10); 
        Thread.sleep(sleepTime);    
        assertTrue(checkRoutingTables()); 
    }
    
    /**
     * Check routing tables when adding agents
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testAddPortalAndAgentToTwoRoutersHashMapCheck() throws IOException, InterruptedException 
    {
        advertise(8002);
        p6 = new Portal("p6",r3);
        Thread.sleep(sleepTime);
        u10 = new UserAgent("u10",p6);
        Thread.sleep(sleepTime);
        p7 = new Portal("p7",r2);
        Thread.sleep(sleepTime);
        u11 = new UserAgent("u11",p7);
        Thread.sleep(sleepTime);
        //additional expected outcomes
        router1Expected.put("p6", null);
        router1Expected.put("u10", null);
        portal1Expected.put("p6", r1);
        portal1Expected.put("u10", r1);          
        portal2Expected.put("p6", r1);
        portal2Expected.put("u10", r1);
        router2Expected.put("p6", null);
        router2Expected.put("u10", null);
        portal3Expected.put("p6", r2);
        portal3Expected.put("u10", r2);          
        portal4Expected.put("p6", r2);
        portal4Expected.put("u10", r2); 
        portal4Expected.put("p6", r2);
        portal4Expected.put("u10", r2); 
        portal5Expected.put("p6", r3);
        portal5Expected.put("u10", r3); 
        router3Expected.put("p6", p6);
        router3Expected.put("u10", p6);  
        router1Expected.put("p7", null);
        router1Expected.put("u11", null);
        portal1Expected.put("p7", r1);
        portal1Expected.put("u11", r1);          
        portal2Expected.put("p7", r1);
        portal2Expected.put("u11", r1);
        router2Expected.put("p7", p7);
        router2Expected.put("u11", p7);
        portal3Expected.put("p7", r2);
        portal3Expected.put("u11", r2);          
        portal4Expected.put("p7", r2);
        portal4Expected.put("u11", r2); 
        portal5Expected.put("p7", r3);
        portal5Expected.put("u11", r3); 
        router3Expected.put("p7", null);
        router3Expected.put("u11", null);  
        portal6Expected = new TreeMap<>();
        portal6Expected.put("u1", r3);
        portal6Expected.put("u2", r3);
        portal6Expected.put("p1", r3);
        portal6Expected.put("u3", r3);
        portal6Expected.put("u4", r3);
        portal6Expected.put("p2", r3);
        portal6Expected.put("r1", r3);        
        portal6Expected.put("p5", r3);
        portal6Expected.put("u9", r3); 
        portal6Expected.put("u5", r3);
        portal6Expected.put("u6", r3);
        portal6Expected.put("p3", r3);
        portal6Expected.put("u7", r3);
        portal6Expected.put("u8", r3);
        portal6Expected.put("p4", r3);
        portal6Expected.put("r2", r3);     
        portal6Expected.put("r3", r3); 
        portal6Expected.put("p6", p6);
        portal6Expected.put("u10", u10); 
        portal6Expected.put("p7", r3);
        portal6Expected.put("u11", r3);   
        portal7Expected = new TreeMap<>();
        portal7Expected.put("u1", r2);
        portal7Expected.put("u2", r2);
        portal7Expected.put("p1", r2);
        portal7Expected.put("u3", r2);
        portal7Expected.put("u4", r2);
        portal7Expected.put("p2", r2);
        portal7Expected.put("r1", r2);        
        portal7Expected.put("p5", r2);
        portal7Expected.put("u9", r2); 
        portal7Expected.put("u5", r2);
        portal7Expected.put("u6", r2);
        portal7Expected.put("p3", r2);
        portal7Expected.put("u7", r2);
        portal7Expected.put("u8", r2);
        portal7Expected.put("p4", r2);
        portal7Expected.put("r2", r2);     
        //portal7Expected.put("r3", r2); 
        portal7Expected.put("p6", r2);
        portal7Expected.put("u10", r2); 
        portal7Expected.put("p7", p7);
        portal7Expected.put("u11", u11); 
        Thread.sleep(sleepTime);
        assertTrue(checkRoutingTables());    
    }
    
    /**
     * Check routing tables when removing agents
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testRemoveUserHashMapCheckViaRouter() throws InterruptedException, IOException 
    {
        advertise(8003);
        r1.removeAgent("u4");            
        removeKeyFromExpected("u4");
        Thread.sleep(sleepTime);
        assertTrue(checkRoutingTables()); 
    }   
    
    /**
     * Check routing tables when removing agents
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testRemoveTwoUsersHashMapCheckViaDifferentRouters() throws InterruptedException, IOException 
    {
        advertise(8004);
        r1.removeAgent("u4");  
        Thread.sleep(sleepTime);
        removeKeyFromExpected("u4");
        r2.removeAgent("u8");
        removeKeyFromExpected("u8");
        Thread.sleep(sleepTime);
        assertTrue(checkRoutingTables()); 
    }
    
    /**
     * Check routing tables when removing agents
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testRemoveUserBelongingToOtherNetworkHashMapCheckViaRouter() throws InterruptedException, IOException 
    {
        advertise(8005);
        r2.removeAgent("u4");            
        removeKeyFromExpected("u4");
        Thread.sleep(sleepTime);
        assertTrue(checkRoutingTables()); 
    }    
    
    /**
     * Check routing tables when removing agents
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testRemoveUserHashMapCheckViaPortal() throws InterruptedException, IOException 
    {
        advertise(8006);
        p1.removeAgent("u4");            
        removeKeyFromExpected("u4");
        Thread.sleep(sleepTime);
        assertTrue(checkRoutingTables()); 
    }   
    
    /**
     * Check routing tables when removing agents
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testRemoveTwoUsersHashMapCheckViaDifferentPortals() throws InterruptedException, IOException 
    {
        advertise(8007);
        p2.removeAgent("u4");            
        removeKeyFromExpected("u4");
        p3.removeAgent("u8");
        removeKeyFromExpected("u8");
        Thread.sleep(sleepTime);
        assertTrue(checkRoutingTables()); 
    }
    
    /**
     * Check routing tables when removing agents
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testRemoveUserBelongingToOtherNetworkHashMapCheckViaPortal() throws InterruptedException, IOException 
    {
        advertise(8008);        
        p4.removeAgent("u4");            
        removeKeyFromExpected("u4");
        Thread.sleep(sleepTime + 5000);
        assertTrue(checkRoutingTables()); 
    }  
    
    /**
     * Check routing tables when removing agents
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testRemovePortalHashMapCheckViaRouter() throws InterruptedException, IOException 
    {
        advertise(8009);
        r1.removeAgent("p2"); 
        removeKeyFromExpected("p2");
        removeKeyFromExpected("u3");
        removeKeyFromExpected("u4");
        Thread.sleep(sleepTime);
        assertTrue(checkRoutingTables()); 
    }      
    
    /**
     * Check routing tables when removing agents
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testRemovePortalOnDifferentNetworkHashMapCheckViaRouter() throws InterruptedException, IOException 
    {
        advertise(8010);
        r2.removeAgent("p2"); 
        removeKeyFromExpected("p2");
        removeKeyFromExpected("u3");
        removeKeyFromExpected("u4");
        Thread.sleep(sleepTime + 5000);
        assertTrue(checkRoutingTables()); 
    }    
    
    /**
     * Check routing tables when removing agents
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testRemovePortalHashMapCheckViaPortal() throws InterruptedException, IOException 
    {
        advertise(8011);
        p1.removeAgent("p2"); 
        removeKeyFromExpected("p2");
        removeKeyFromExpected("u3");
        removeKeyFromExpected("u4");
        Thread.sleep(sleepTime);
        assertTrue(checkRoutingTables()); 
    }      
    
    /**
     * Check routing tables when removing agents
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testRemovePortalOnDifferentNetworkHashMapCheckViaPortal() throws InterruptedException, IOException 
    {
        advertise(8012);
        p4.removeAgent("p2"); 
        removeKeyFromExpected("p2");
        removeKeyFromExpected("u3");
        removeKeyFromExpected("u4");
        Thread.sleep(sleepTime);
        assertTrue(checkRoutingTables()); 
    }
    
    /**
     * Check routing tables when removing agents
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testPortalRemoveAllUsers() throws InterruptedException, IOException
    {  
        advertise(8038);
        p2.removeAllUsers();
        removeKeyFromExpected("u3");
        removeKeyFromExpected("u4");
        Thread.sleep(sleepTime);
        assertTrue(checkRoutingTables());
    }     
}