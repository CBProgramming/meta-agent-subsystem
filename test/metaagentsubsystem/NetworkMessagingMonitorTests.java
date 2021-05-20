package metaagentsubsystem;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import static metaagentsubsystem.NetworkMessagingMonitorTests.sleepTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test for the Network Messaging Monitor
 * @author Group B, Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class NetworkMessagingMonitorTests 
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
    public NetworkMessagingMonitorTests() {
    }
 
    /**
     * Initialise separator and sleepTime for all tests
     * @throws java.lang.NoSuchFieldException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */       
    @BeforeClass
    public static void setUpClass() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InterruptedException, IOException 
    {
        separator = "¶»¤";
        sleepTime = 2000;
    }
    
    /**
     * Initialise monitors and basic network before each test
     * @throws java.lang.NoSuchFieldException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */      
    @Before
    public void setUp() throws NoSuchFieldException, IllegalArgumentException, IllegalArgumentException, IllegalAccessException, InterruptedException, IOException
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
     * Overrides singleton router for testing with two routers
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
     * Resets log files used by file managers
     */   
    public void resetFiles()
    {
        resetFile("system_log.txt");
        resetFile("user_log.txt");
    }
    
    /**
     * Sets up a basic network of MetaAgents
     * @throws java.lang.NoSuchFieldException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */   
    public void basicNetworkSetup() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InterruptedException, IOException
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
     * Outputs routing table for MetaAgent
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
     * Connects two routers
     * @param port port number of first router
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
     * Reset monitor log files
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
     * Reads file and returns contents as string
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
     * Ensure router singleton has been overridden
     */       
    @Test
    public void checkDoubleRouterSetup()
    {
        assertFalse(r1 == r2);
    }   
    
    /**
     * Test of msgHandler method, of class Router with logging
     * @throws java.lang.Exception
     */
    @Test
    public void testMsgHandler() throws Exception 
    {
        advertise(8013);
        resetFiles();
        MsgData message = new MsgData("r1","u5","test message");
        r1.msgHandler(message);
        Thread.sleep(sleepTime);
        
        fileContents = readFile("system_log.txt");
        assertEquals("", fileContents);
        fileContents = readFile("user_log.txt");
        expected = "Message forwarded from router to external user: " + separator + "u5\tSender: " + separator + "r1\tTime sent: " + separator;
        assertTrue(fileContents.contains(expected));
        expected = "Message body: " + separator + "test message";
        assertTrue(fileContents.contains(expected));
    }
    
    /**
     * Test of msgHandler (null) method, of class Router with logging
     * @throws java.lang.Exception
     */
    @Test (expected = NullPointerException.class)
    public void testMsgHandlerNullMessage() throws Exception 
    {
        advertise(8014);
        resetFiles();
        r1.msgHandler(null);
        fileContents = readFile("system_log.txt");
        assertEquals("", fileContents);
        fileContents = readFile("user_log.txt");
        assertEquals("", fileContents);
    }
    
    /**
     * Test of msgHandler (null) method, of class Router with logging
     * @throws java.lang.Exception
     */
    @Test (expected = NullPointerException.class)
    public void testMsgHandlerNullVariables() throws Exception 
    {
        advertise(8015);
        resetFiles();
        String unambiguous = null;
        MsgData message = new MsgData(null,null,unambiguous);
        r1.msgHandler(message);
        fileContents = readFile("system_log.txt");
        assertEquals("", fileContents);
        fileContents = readFile("user_log.txt");
        assertEquals("", fileContents);
    }   
    
    /**
     * Test of msgHandler method, of class Router with logging
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    @Test
    public void testAddAgentMsgHandler() throws InterruptedException, IOException 
    {
        advertise(8016);
        resetFiles();
        p5 = new Portal("p5",r1);
        p5.setMonitor(p1mon);
        Thread.sleep(sleepTime);
        u9 = new UserAgent("u9",p5); 
        MsgData message = new MsgData("r2","u9","test message");
        Thread.sleep(sleepTime);
        r2.msgHandler(message);
        Thread.sleep(sleepTime);
        fileContents = readFile("system_log.txt");
        expected = "PORTAL added to: " + separator + "r1\tName: " + separator + "p5\tTime Added: " + separator;
        assertTrue(fileContents.contains(expected));        
        expected = "USER added to: " + separator + "p5\tName: " + separator + "u9\tTime Added: " + separator;
        assertTrue(fileContents.contains(expected));

        fileContents = readFile("user_log.txt");
        expected = "Message received in local router from external user: " + separator + "r2\tRecipient: " + separator + "u9\tTime received: " + separator;
        assertTrue(fileContents.contains(expected));
        expected = "Message body: " + separator + "test message";
        assertTrue(fileContents.contains(expected));
        expected = "Message received from: " + separator + "r2\tRecipient: " + separator + "u9\tDelivered by: " + separator + "p5\tTime received: " + separator;
        assertTrue(fileContents.contains(expected));
        expected = "Message body: " + separator + "test message";
        assertTrue(fileContents.contains(expected));
    }
    
    /**
     * Test of addAgent (null) method, of class Router with logging
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    @Test (expected = NullPointerException.class)
    public void testAddNullAgent() throws InterruptedException, IOException 
    {
        advertise(8017);
        resetFiles();
        r1.addAgent(null);
        Thread.sleep(sleepTime);
        fileContents = readFile("system_log.txt");
        assertEquals("", fileContents);
        fileContents = readFile("user_log.txt");
        assertEquals("", fileContents);
    } 
   
    /**
     * Test of sendMessage (null) method, of class UserAgent with logging
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    @Test
    public void testSendNullBodyMessageFromUser() throws InterruptedException, IOException 
    {
        advertise(8018);
        resetFiles();
        u1.sendMessage("u8", null);
        Thread.sleep(sleepTime);
        fileContents = readFile("system_log.txt");
        assertEquals("", fileContents);
        fileContents = readFile("user_log.txt");
        expected = "Message sent to: " + separator + "u8\tSender: " + separator + "u1\tPassed to: " + separator + "p1\tTime sent: " + separator;
        assertTrue(fileContents.contains(expected));     
        expected = "Message body: " + separator + "null";
        assertTrue(fileContents.contains(expected));
        expected = "Message forwarded from router to external user: " + separator + "u8	Sender: " + separator + "u1	Time sent: " + separator;
        assertTrue(fileContents.contains(expected));
        expected = "Message body: " + separator + "null";
        assertTrue(fileContents.contains(expected));
    }  
     
    /**
     * Test of sendMessage method, of class User, with logging
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    @Test
    public void testSendMessageUserToUser() throws InterruptedException, IOException 
    { 
        advertise(8032);
        resetFiles();
        Thread.sleep(sleepTime);
        u1.sendMessage("u8","test message");
        Thread.sleep(sleepTime);
        fileContents = readFile("system_log.txt");
        assertEquals("", fileContents);
        fileContents = readFile("user_log.txt");
        //assertEquals("", fileContents);
        expected = "Message sent to: " + separator + "u8\tSender: " + separator + "u1\tPassed to: " + separator + "p1\tTime sent: " + separator;
        assertTrue(fileContents.contains(expected));     
        expected = "Message body: " + separator + "test message";
        assertTrue(fileContents.contains(expected));
        expected = "Message forwarded from router to external user: " + separator + "u8	Sender: " + separator + "u1\tTime sent: " + separator;
        assertTrue(fileContents.contains(expected));
        expected = "Message body: " + separator + "test message";
        assertTrue(fileContents.contains(expected));     
    }       
    
    /**
     * Test of removeAgent (null) method, of class Router, with logging
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */    
    @Test (expected = NullPointerException.class)
    public void testRemoveNullAgent() throws InterruptedException, IOException 
    {
        advertise(8021);
        resetFiles();
        r1.removeAgent(null);
        fileContents = readFile("system_log.txt");
        assertEquals("", fileContents);
        fileContents = readFile("user_log.txt");
        assertEquals("", fileContents);
    }  
    
    /**
     * Test of sendMsg (null) method, of class UserAgent, with logging
     * @throws java.lang.Exception
     */
    @Test
    public void testSendMsg_NullMsgData() throws Exception 
    {
        advertise(8022);
        resetFiles();
        u1.sendMsg(null);
        Thread.sleep(sleepTime);
        fileContents = readFile("system_log.txt");
        assertEquals("", fileContents);
        fileContents = readFile("user_log.txt");
        assertEquals("", fileContents); 
    } 
    
    /**
     * Test of sendMessage method, of class UserAgent, with logging
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    @Test
    public void testSendMessageCurrentUserToNewUser() throws InterruptedException, IOException 
    { 
        advertise(8025);
        resetFiles();
        p5 = new Portal("p5",r1);
        p5.setMonitor(p1mon);
        u9 = new UserAgent("u9",p5);  
        Thread.sleep(sleepTime);
        u8.sendMessage("u9","test message");
        Thread.sleep(sleepTime);
        fileContents = readFile("system_log.txt");
        expected = "PORTAL added to: " + separator + "r1\tName: " + separator + "p5\tTime Added: " + separator;
        assertTrue(fileContents.contains(expected));        
        expected = "USER added to: " + separator + "p5\tName: " + separator + "u9\tTime Added: " + separator;
        assertTrue(fileContents.contains(expected));

        fileContents = readFile("user_log.txt");
        expected = "Message received in local router from external user: " + separator + "u8\tRecipient: " + separator + "u9\tTime received: " + separator;
        assertTrue(fileContents.contains(expected));
        expected = "Message body: " + separator + "test message";
        assertTrue(fileContents.contains(expected));
        expected = "Message received from: " + separator + "u8\tRecipient: " + separator + "u9\tDelivered by: " + separator + "p5\tTime received: " + separator;
        assertTrue(fileContents.contains(expected));
        expected = "Message body: " + separator + "test message";
        assertTrue(fileContents.contains(expected));       
    } 

    
    /**
     * Test of sendMessage method, of class UserAgent, with logging
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    @Test
    public void testSendMessageNewUserToCurrentUser() throws InterruptedException, IOException 
    { 
        advertise(8024);
        resetFiles();
        p5 = new Portal("p5",r1);
        p5.setMonitor(p1mon);
        u9 = new UserAgent("u9",p5);   
        u9.sendMessage("u8","test message");
        Thread.sleep(sleepTime);
        fileContents = readFile("system_log.txt");
        expected = "PORTAL added to: " + separator + "r1\tName: " + separator + "p5\tTime Added: " + separator;
        assertTrue(fileContents.contains(expected));        
        expected = "USER added to: " + separator + "p5\tName: " + separator + "u9\tTime Added: " + separator;
        assertTrue(fileContents.contains(expected));
        fileContents = readFile("user_log.txt");
        expected = "Message sent to: " + separator + "u8\tSender: " + separator + "u9\tPassed to: " + separator + "p5\tTime sent: " + separator;
        assertTrue(fileContents.contains(expected));
        expected = "Message body: " + separator + "test message";
        assertTrue(fileContents.contains(expected));         
        expected = "Message forwarded from router to external user: " + separator + "u8\tSender: " + separator + "u9\tTime sent: " + separator;
        assertTrue(fileContents.contains(expected));
        expected = "Message body: " + separator + "test message";
        assertTrue(fileContents.contains(expected)); 
    } 
    
    /**
     * Test of sendMessage method of class UserAGent, with logging
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    @Test
    public void testSendMessageTwoNewUsersOnNewPortals() throws InterruptedException, IOException 
    { 
        advertise(8026);
        resetFiles();
        p5 = new Portal("p5",r1);
        p5.setMonitor(p1mon);
        u9 = new UserAgent("u9",p5); 
        p6 = new Portal("p6",r2);
        u10 = new UserAgent("u10",p6);
        Thread.sleep(sleepTime);
        u9.sendMessage("u10","test message");
        Thread.sleep(sleepTime);
        
        fileContents = readFile("system_log.txt");
        expected = "PORTAL added to: " + separator + "r1\tName: " + separator + "p5\tTime Added: " + separator;
        assertTrue(fileContents.contains(expected)); 
        expected = "USER added to: " + separator + "p5\tName: " + separator + "u9\tTime Added: " + separator;
        assertTrue(fileContents.contains(expected)); 
        expected = "AGENT added to: " + separator + "r2\tName: " + separator + "p6\tTime Added: " + separator;
        assertTrue(fileContents.contains(expected)); 
        expected = "AGENT added to: " + separator + "r2\tName: " + separator + "u10\tTime Added: " + separator;
        assertTrue(fileContents.contains(expected)); 
        fileContents = readFile("user_log.txt");        
        expected = "Message sent to: " + separator + "u10\tSender: " + separator + "u9\tPassed to: " + separator + "p5\tTime sent: " + separator;
        assertTrue(fileContents.contains(expected)); 
        expected = "Message body: " + separator + "test message";
        assertTrue(fileContents.contains(expected)); 
        expected = "Message forwarded from router to external user: " + separator + "u10\tSender: " + separator + "u9\tTime sent: " + separator;
        assertTrue(fileContents.contains(expected)); 
        expected = "Message body: " + separator + "test message";
        assertTrue(fileContents.contains(expected)); 
    }   
        
    /**
     * Test of sendMessage method, of class UserAgent, with logging
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    @Test
    public void testSendToUnregisteredUserFromUser() throws InterruptedException, IOException
    {  
        advertise(8033);
        resetFiles();
        u1.sendMessage("nonexistant", "are you there?");
        Thread.sleep(sleepTime);
        fileContents = readFile("system_log.txt");
        assertEquals("", fileContents);
        fileContents = readFile("user_log.txt");
        expected = "Message sent to: " + separator + "nonexistant\tSender: " + separator + "u1\tPassed to: " + separator + "p1\tTime sent: " + separator;
        assertTrue(fileContents.contains(expected)); 
        expected = "Message body: " + separator + "are you there?"; 
        assertTrue(fileContents.contains(expected));  
    }   
    
    /**
     * Test of msgHandler method, of class Router, with logging
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    @Test
    public void testSendToUnregisteredUserFromRouter() throws InterruptedException, IOException
    {  
        advertise(8034);
        resetFiles();
        r1.msgHandler(new MsgData("r1","nonexistant", "are you there?"));
        Thread.sleep(sleepTime);
        fileContents = readFile("system_log.txt");
        assertEquals("", fileContents);
        fileContents = readFile("user_log.txt");
        assertEquals("", fileContents);        
    }
}