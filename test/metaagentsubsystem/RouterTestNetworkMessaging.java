package metaagentsubsystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Group B, Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class RouterTestNetworkMessaging 
{
        private ByteArrayOutputStream sysOut;
        private Router r1, r2;
        private Portal p1, p2, p3, p4, p5, p6;
        private UserAgent u1, u2, u3, u4, u5, u6, u7, u8, u9, u10;
        static int sleepTime;
        private TreeMap<String, MetaAgent> router1Expected, router2Expected,
                portal1Expected,portal2Expected,portal3Expected,portal4Expected,
                portal5Expected,portal6Expected;
        
    
    /**
     * Method used to call the test
     */
    public RouterTestNetworkMessaging() {
    }
    
    /**
     * Initialise variables for all tests
     * @throws java.lang.NoSuchFieldException
     * @throws java.io.IOException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.InterruptedException
     */  
    @BeforeClass
    public static void setUpClass() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InterruptedException, IOException 
    {
        sleepTime = 2000;
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
        sysOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(sysOut));
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
    }
    
    /**
     * Test of msgHandler method, of class Router.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testMsgHandler() throws IOException, InterruptedException
    {
        advertise(8013);
        String expected = "r1: test message Recipient: u6"+ 
                System.getProperty("line.separator");
        MsgData message = new MsgData("r1","u6","test message");
        r1.msgHandler(message);
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 32));  
    }
    
    /**
     * Test of msgHandler (null) method, of class Router.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test (expected = NullPointerException.class)
    public void testMsgHandlerNullMessage() throws IOException, InterruptedException
    {
        advertise(8014);
        r1.msgHandler(null);
    }
    
    /**
     * Test of msgHandler (null) method, of class Router.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test (expected = NullPointerException.class)
    public void testMsgHandlerNullVariables() throws IOException, InterruptedException
    {
        advertise(8015);
        String unambiguous = null;
        MsgData message = new MsgData(null,null,unambiguous);
        r1.msgHandler(message);
    }   
    
    /**
     * Test of msgHandler (null) method, of class Router.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testAddAgentMsgHandler() throws InterruptedException, IOException 
    {
        advertise(8016);
        p5 = new Portal("p5",r1);
        u9 = new UserAgent("u9",p5);   
        String expected = "r2: test message Recipient: u9"+ 
                System.getProperty("line.separator");
        MsgData message = new MsgData("r2","u9","test message");
        Thread.sleep(sleepTime);
        r2.msgHandler(message);
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 32));      
    }
    
    /**
     * Test of sendMessage (null) method, of class UserAgent.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testSendNullBodyMessageFromUser() throws InterruptedException, IOException 
    {
        advertise(8018);
        String expected = "u1: null Recipient: u8"+ 
                System.getProperty("line.separator");
        u1.sendMessage("u8", null);
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 24));
    }  
    
    /**
     * Test of sendMessage (null) method, of class UserAgent.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testSendMessageUserToUserNoDelay() throws InterruptedException, IOException 
    { 
        advertise(8020);
        String expected = "u1: test message Recipient: u8" + 
                System.getProperty("line.separator");
        u1.sendMessage("u8","test message");
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 32));       
    }    
    
    /**
     * Test of sendMessage method, of class UserAgent.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testSendMessageUserToUserWithDelay() throws InterruptedException, IOException 
    { 
        advertise(8032);
        String expected = "u1: test message Recipient: u8" + 
                System.getProperty("line.separator");
        Thread.sleep(sleepTime);
        u1.sendMessage("u8","test message");
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 32));       
    }       
    
    /**
     * Test of sendMsg (null) method, of class UserAgent.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testSendMsg_NullMsgData() throws Exception 
    {
        advertise(8022);
        String expected = "Empty message"+ 
                System.getProperty("line.separator");
        u1.sendMsg(null);
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 15));        
    } 
    
    /**
     * Test of sendMessage method, of class UserAgent.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testSendMessageCurrentUserToNewUserWithDelay() throws InterruptedException, IOException 
    { 
        advertise(8025);
        p5 = new Portal("p5",r1);
        u9 = new UserAgent("u9",p5);  
        String expected = "u8: test message Recipient: u9" + 
                System.getProperty("line.separator");
        Thread.sleep(sleepTime);
        u8.sendMessage("u9","test message");
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 32));       
    } 

    /**
     * Test of sendMessage method, of class UserAgent.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testSendMessageCurrentUserToNewUserNoDelay() throws InterruptedException, IOException 
    { 
        advertise(8023);
        p5 = new Portal("p5",r1);
        u9 = new UserAgent("u9",p5);  
        String expected = "u8: test message Recipient: u9" + 
                System.getProperty("line.separator");
        u8.sendMessage("u9","test message");
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 32));       
    } 
    
    /**
     * Test of sendMessage method, of class UserAgent.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testSendMessageNewUserToCurrentUserNoDelay() throws InterruptedException, IOException 
    { 
        advertise(8024);
        p5 = new Portal("p5",r1);
        u9 = new UserAgent("u9",p5);   
        String expected = "u9: test message Recipient: u8" + 
                System.getProperty("line.separator");
        u9.sendMessage("u8","test message");
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 32));       
    } 
    
    /**
     * Test of sendMessage method, of class UserAgent.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testSendMessageNewUserToCurrentUserWithDelay() throws InterruptedException, IOException 
    { 
        advertise(8027);
        p5 = new Portal("p5",r1);
        u9 = new UserAgent("u9",p5);   
        String expected = "u9: test message Recipient: u8" + 
                System.getProperty("line.separator");
        Thread.sleep(sleepTime);
        u9.sendMessage("u8","test message");
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 32));       
    } 
    
    /**
     * Test of sendMessage method, of class UserAgent.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testSendMessageTwoNewUsersOnNewPortals() throws InterruptedException, IOException 
    { 
        advertise(8026);
        p5 = new Portal("p5",r1);
        u9 = new UserAgent("u9",p5); 
        p6 = new Portal("p6",r2);
        u10 = new UserAgent("u10",p6);
        String expected = "u9: test message Recipient: u10" + 
                System.getProperty("line.separator");
        Thread.sleep(sleepTime);
        u9.sendMessage("u10","test message");
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 33));       
    }   
        
    /**
     * Test of sendMessage method, of class UserAgent.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testSendToUnregisteredUserFromUser() throws InterruptedException, IOException
    {  
        advertise(8033);
        String expected = "Recipient not found in p1" + 
                System.getProperty("line.separator");
        u1.sendMessage("nonexistant", "are you there?");
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 27));        
    }   
    
    /**
     * Test of msgHandler method, of class Router.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testSendToUnregisteredUserFromRouter() throws InterruptedException, IOException
    {  
        advertise(8034);
        String expected = "Recipient not found in r1" + 
                System.getProperty("line.separator");
        r1.msgHandler(new MsgData("r1","nonexistant", "are you there?"));
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 27));        
    }
}