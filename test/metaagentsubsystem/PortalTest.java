package metaagentsubsystem;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Scanner;
import java.util.TreeMap;
import static metaagentsubsystem.MetaAgentType.*;
import static metaagentsubsystem.Router.createNew;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Group B, Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class PortalTest 
{
    private ByteArrayOutputStream sysOut;
    private static Router r1;
    private static Portal p1;
    private static UserAgent u1, u2;
    int sleepTime;
    private static String separator;
    private static String userFile;
    private static String systemFile;
    private static FileManager userManager;
    private static FileManager systemManager;
    private static PortalMonitor pm;
    private static RouterMonitor rm;
    
    /**
     * Method used to call the test
     */
    public PortalTest() 
    {
        
    }
    
    /**
     * Initialise variables for all tests
     */  
    @BeforeClass
    public static void setUpClass() 
    {
        separator = "¶»¤";
        userFile = "userTestFile.txt";
        systemFile = "systemTestFile.txt";
        userManager = new FileManager(userFile);
        systemManager = new FileManager(systemFile);
        pm = new DefaultPortalMonitor(systemManager,userManager);
        rm = new DefaultRouterMonitor(systemManager,userManager);
    }
    
    /**
     * Setup basic network
     * @throws java.lang.NoSuchFieldException
     */     
    @Before
    public void setUp() throws NoSuchFieldException 
    {
        try
        {
            resetRouter();
        }
        catch (IllegalAccessException e)
        {
            System.out.println("Reset router failed");
        }
        sleepTime = 100;
        r1 = createNew("r1");
        p1 = new Portal("p1",r1);
        u1 = new UserAgent("u1",p1);
        u2 = new UserAgent("u2",p1);
        sysOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(sysOut));
    }
    
    /**
     * Override singleton router for testing purposes
     * @throws java.lang.NoSuchFieldException
     * @throws java.lang.IllegalAccessException
     */    
    public void resetRouter() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        Field instance = Router.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null); 
    }
    
    /**
     * Reset monitor log file
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
     * Read monitor log file and return as string
     * @param fileName name of file
     * @return file name
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
     * Test of msgHandler method, of class Portal.
     * @throws java.lang.Exception
     */
    @Test
    public void testMsgHandler() throws Exception 
    {
        String expected = "u1: test message Recipient: u2"+ 
                System.getProperty("line.separator");
        MsgData message = new MsgData("u1","u2","test message");
        p1.msgHandler(message);
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 32));  
    }

    /**
     * Test of msgHandler (null) method, of class Portal.
     * @throws java.lang.Exception
     */
    @Test
    public void testMsgHandlerNull() throws Exception 
    {
        String expected = "Null message" + 
                System.getProperty("line.separator");
        MsgData message = null;
        p1.msgHandler(message);
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString());  
    }   
    
    /**
     * Test of msgHandler (null) method, of class Portal.
     * @throws java.lang.Exception
     */
    @Test (expected = NullPointerException.class)
    public void testMsgHandlerNullVariables() throws Exception 
    {
        String unambiguous = null;
        MsgData message = new MsgData(null,null,unambiguous);
        p1.msgHandler(message);
        Thread.sleep(sleepTime);
    }    
    
    /**
     * Test of new UserAgent (null portal) constructor, of class UserAgent.
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testNewAgentNoPortal() throws InterruptedException 
    {
        TreeMap<String, MetaAgent> expected = new TreeMap<>();
        expected.put("u1", u1);
        expected.put("u2", u2);
        expected.put("p1", p1);
        expected.put("r1",r1);
        UserAgent u3 = new UserAgent("u3",null);
        assertThat(p1.getRoutingTable(), is(expected));
        assertThat(p1.getRoutingTable().size(), is(expected.size()));
        assertEquals(expected.containsKey("u1"),p1.getRoutingTable().containsKey("u1"));
        assertEquals(expected.containsKey("u2"),p1.getRoutingTable().containsKey("u2"));
        assertEquals(expected.containsKey("p1"),p1.getRoutingTable().containsKey("p1"));
        assertEquals(expected.get("u1"),p1.getRoutingTable().get("u1"));
        assertEquals(expected.get("u2"),p1.getRoutingTable().get("u2"));
        assertEquals(expected.get("p1"),p1.getRoutingTable().get("p1"));
    }
    
    /**
     * Test of new UserAgent (null handle) constructor, of class UserAgent.
     * @throws java.lang.InterruptedException
     */
    @Test (expected = NullPointerException.class)
    public void testNewNullNamedAgent() throws InterruptedException 
    {
        TreeMap<String, MetaAgent> expected = new TreeMap<>();
        expected.put("u1", u1);
        expected.put("u2", u2);
        expected.put("p1", p1);
        UserAgent u3 = new UserAgent(null,p1);
        assertThat(p1.getRoutingTable(), is(expected));
        assertThat(p1.getRoutingTable().size(), is(expected.size()));
        assertEquals(expected.containsKey("u1"),p1.getRoutingTable().containsKey("u1"));
        assertEquals(expected.containsKey("u2"),p1.getRoutingTable().containsKey("u2"));
        assertEquals(expected.containsKey("p1"),p1.getRoutingTable().containsKey("p1"));
        assertEquals(expected.get("u1"),p1.getRoutingTable().get("u1"));
        assertEquals(expected.get("u2"),p1.getRoutingTable().get("u2"));
        assertEquals(expected.get("p1"),p1.getRoutingTable().get("p1"));
    }
    
    /**
     * Test of addAgent method, of class Portal.
     * @throws java.lang.InterruptedException
     */    
    @Test
    public void testAddAgentOnePortal() throws InterruptedException 
    {
        UserAgent u3 = new UserAgent("u3",null);
        TreeMap<String, MetaAgent> expected = new TreeMap<>();
        expected.put("u1", u1);
        expected.put("u2", u2);
        expected.put("p1", p1);
        expected.put("u3", u3);
        expected.put("r1", r1);
        p1.addAgent(u3);
        assertThat(p1.getRoutingTable(), is(expected));
        assertThat(p1.getRoutingTable().size(), is(expected.size()));
        assertEquals(expected.containsKey("u1"),p1.getRoutingTable().containsKey("u1"));
        assertEquals(expected.containsKey("u2"),p1.getRoutingTable().containsKey("u2"));
        assertEquals(expected.containsKey("p1"),p1.getRoutingTable().containsKey("p1"));
        assertEquals(expected.containsKey("u3"),p1.getRoutingTable().containsKey("u3"));        
        assertEquals(expected.get("u1"),p1.getRoutingTable().get("u1"));
        assertEquals(expected.get("u2"),p1.getRoutingTable().get("u2"));
        assertEquals(expected.get("u3"),p1.getRoutingTable().get("u3"));
        assertEquals(expected.get("p1"),p1.getRoutingTable().get("p1"));
    } 

    /**
     * Test of addAgent (null)method, of class Portal.
     * @throws java.lang.InterruptedException
     */      
    @Test (expected = NullPointerException.class)
    public void testAddNullAgent() throws InterruptedException 
    {
        UserAgent u3 = null;
        TreeMap<String, MetaAgent> expected = new TreeMap<>();
        expected.put("u1", u1);
        expected.put("u2", u2);
        expected.put("p1", p1);
        expected.put("u3", u3);   
        p1.addAgent(u3);
        assertThat(p1.getRoutingTable(), is(expected));
        assertThat(p1.getRoutingTable().size(), is(expected.size()));
        assertEquals(expected.containsKey("u1"),p1.getRoutingTable().containsKey("u1"));
        assertEquals(expected.containsKey("u2"),p1.getRoutingTable().containsKey("u2"));
        assertEquals(expected.containsKey("p1"),p1.getRoutingTable().containsKey("p1"));
        assertEquals(expected.containsKey("u3"),p1.getRoutingTable().containsKey("u3"));        
        assertEquals(expected.get("u1"),p1.getRoutingTable().get("u1"));
        assertEquals(expected.get("u2"),p1.getRoutingTable().get("u2"));
        assertEquals(expected.get("u3"),p1.getRoutingTable().get("u3"));
        assertEquals(expected.get("p1"),p1.getRoutingTable().get("p1"));
    } 
    
    /**
     * Test of new UserAgent constructor then sendMessage method, of class UserAgent.
     * @throws java.lang.InterruptedException
     */      
    @Test
    public void testAddAgentOnePortalThenSendMsgToCurrentUser() throws InterruptedException 
    {
        String expected = "u3: test message Recipient: u1"+ 
                System.getProperty("line.separator");
        UserAgent u3 = new UserAgent("u3",p1);
        u3.sendMessage("u1", "test message");
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 32));
    } 
    
    /**
     * Test of sendMessage (null body) method, of class UserAgent.
     * @throws java.lang.InterruptedException
     */       
    @Test
    public void testSendNullBodyMessageFromUser() throws InterruptedException 
    {
        String expected = "u1: null Recipient: u2"+ 
                System.getProperty("line.separator");
        u1.sendMessage("u2", null);
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 24));
    } 

    /**
     * Test of sendMessage (null recipient) method, of class UserAgent.
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
     * Test of new UserAgent constructor then sendMessage method, of class UserAgent.
     * @throws java.lang.InterruptedException
     */        
    @Test
    public void testAddAgentOnePortalThenSendMsgToNewUser() throws InterruptedException 
    {
        String expected = "u1: test message Recipient: u3"+ 
                System.getProperty("line.separator");
        UserAgent u3 = new UserAgent("u3",p1);
        u1.sendMessage("u3", "test message");
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 32));
    } 

    /**
     * Test of new UserAgent constructor then sendMessage method, of class UserAgent.
     * @throws java.lang.InterruptedException
     */        
    @Test
    public void testAddTwoAgentsOnePortalThenSendMsgBetween() throws InterruptedException 
    {
        String expected = "u4: test message Recipient: u3"+ 
                System.getProperty("line.separator");
        UserAgent u3 = new UserAgent("u3",p1);
        UserAgent u4 = new UserAgent("u4",p1);
        u4.sendMessage("u3", "test message");
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 32));
    } 
    
    /**
     * Test of addAgent (duplicate handle) method, of class Portal.
     */ 
    @Test
    public void testAddAgentWithSameName()
    {
        TreeMap<String, MetaAgent> expected = new TreeMap<>();
        expected.put("u1", u1);
        expected.put("u2", u2);
        expected.put("p1", p1);  
        expected.put("r1", r1);
        UserAgent u3 = new UserAgent("u1",null);
        p1.addAgent(u3);
        assertThat(p1.getRoutingTable(), is(expected));
        assertThat(p1.getRoutingTable().size(), is(expected.size()));
        assertEquals(expected.containsKey("u1"),p1.getRoutingTable().containsKey("u1"));
        assertEquals(expected.containsKey("u2"),p1.getRoutingTable().containsKey("u2"));
        assertEquals(expected.containsKey("p1"),p1.getRoutingTable().containsKey("p1"));
        assertEquals(expected.containsKey("u3"),p1.getRoutingTable().containsKey("u3"));        
        assertEquals(expected.get("u1"),p1.getRoutingTable().get("u1"));
        assertEquals(expected.get("u2"),p1.getRoutingTable().get("u2"));
        assertEquals(expected.get("u3"),p1.getRoutingTable().get("u3"));
        assertEquals(expected.get("p1"),p1.getRoutingTable().get("p1"));    
    }
    
    /**
     * Test of removeAgent method, of class Portal.
     * @throws java.lang.InterruptedException
     */ 
    @Test
    public void testRemoveAgentOnePortalRemoveUser() throws InterruptedException 
    {
        TreeMap<String, MetaAgent> expected = new TreeMap<>();
        expected.put("u2", u2);
        expected.put("p1", p1);
        expected.put("r1", r1);
        p1.removeAgent("u1");
        assertThat(p1.getRoutingTable(), is(expected));
        assertThat(p1.getRoutingTable().size(), is(expected.size()));
        assertEquals(expected.containsKey("u2"),p1.getRoutingTable().containsKey("u2"));
        assertEquals(expected.containsKey("p1"),p1.getRoutingTable().containsKey("p1"));       
        assertFalse(p1.getRoutingTable().containsKey("u1"));
        assertEquals(expected.get("u2"),p1.getRoutingTable().get("u2"));
        assertEquals(expected.get("p1"),p1.getRoutingTable().get("p1"));
        assertEquals(null,p1.getRoutingTable().get("u1"));
    }  
    
    /**
     * Test of removeAgent method, of class Portal, then sendMessage method to removed UserAgent.
     * @throws java.lang.InterruptedException
     */ 
    @Test
    public void testRemoveAgentAttemptToSendMessageToRemoved() throws InterruptedException 
    {
        String expected = "Recipient not found in p1" + 
                System.getProperty("line.separator");
        p1.removeAgent("u1");
        u2.sendMessage("u1", "test");
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 27));
    } 
    
    /**
     * Test of removeAgent method, of class Portal, then sendMessage method from removed UserAgent.
     * @throws java.lang.InterruptedException
     */ 
    @Test
    public void testRemoveAgentAttemptToSendMessageFromRemoved() throws InterruptedException 
    {
        String expected = "Recipient not found" + 
                System.getProperty("line.separator");
        p1.removeAgent("u1");       
        u1.sendMessage("u2", "test");
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString());
    }
    
    /**
     * Test of removeAgent method, of class Portal, then sendMessage method between removed UserAgents
     * @throws java.lang.InterruptedException
     */     
    @Test
    public void testRemoveBothAgentsAttemptToSendMessage() throws InterruptedException 
    {
        String expected = "Recipient not found" + 
                System.getProperty("line.separator") +
                "Recipient not found" + System.getProperty("line.separator");
        p1.removeAgent("u1"); 
        p1.removeAgent("u2"); 
        u1.sendMessage("u2", "test");
        u2.sendMessage("u1", "test");
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString());
    }   
    
    /**
     * Test of removeAgent (null) method, of class Portal
     */  
    @Test (expected = NullPointerException.class)
    public void testRemoveNullAgent()
    {
        TreeMap<String, MetaAgent> expected = new TreeMap<>();
        expected.put("u1", u1);
        expected.put("u2", u2);
        expected.put("p1", p1);  
        p1.removeAgent(null);
        assertThat(p1.getRoutingTable(), is(expected));
        assertThat(p1.getRoutingTable().size(), is(expected.size()));
        assertEquals(expected.containsKey("u1"),p1.getRoutingTable().containsKey("u1"));
        assertEquals(expected.containsKey("u2"),p1.getRoutingTable().containsKey("u2"));
        assertEquals(expected.containsKey("p1"),p1.getRoutingTable().containsKey("p1"));       
        assertEquals(expected.get("u1"),p1.getRoutingTable().get("u1"));
        assertEquals(expected.get("u2"),p1.getRoutingTable().get("u2"));
        assertEquals(expected.get("p1"),p1.getRoutingTable().get("p1"));
    }  
    
    /**
     * Test of Portal constructor, of class Portal
     * @throws java.lang.InterruptedException
     */  
    @Test
    public void testAddSecondPortalNoUsers() throws InterruptedException 
    {
        Portal p2 = new Portal("p2",r1);
        Thread.sleep(sleepTime);
        TreeMap<String, MetaAgent> p1Expected = new TreeMap<>();
        p1Expected.put("u1", u1);
        p1Expected.put("u2", u2);
        p1Expected.put("p1", p1);
        p1Expected.put("r1", r1);
        p1Expected.put("p2", r1);
        Thread.sleep(sleepTime);
        assertThat(p1.getRoutingTable(),is(p1Expected));
        assertThat(p1.getRoutingTable().size(),is(p1Expected.size()));
        assertEquals(p1Expected.containsKey("u1"),p1.getRoutingTable().containsKey("u1"));
        assertEquals(p1Expected.containsKey("u2"),p1.getRoutingTable().containsKey("u2"));
        assertEquals(p1Expected.containsKey("p1"),p1.getRoutingTable().containsKey("p1")); 
        assertEquals(p1Expected.containsKey("p2"),p1.getRoutingTable().containsKey("p2"));
        assertEquals(p1Expected.containsKey("r1"),p1.getRoutingTable().containsKey("r1"));         
        assertEquals(p1Expected.get("u1"),p1.getRoutingTable().get("u1"));
        assertEquals(p1Expected.get("u2"),p1.getRoutingTable().get("u2"));
        assertEquals(p1Expected.get("p2"),p1.getRoutingTable().get("p2"));
        assertEquals(p1Expected.get("p1"),p1.getRoutingTable().get("p1"));
        assertEquals(p1Expected.get("r1"),p1.getRoutingTable().get("r1"));
        
        TreeMap<String, MetaAgent> p2Expected = new TreeMap<>();
        p2Expected.put("p2", p2);   
        p2Expected.put("u1", r1);
        p2Expected.put("u2", r1);
        p2Expected.put("p1", r1);
        p2Expected.put("r1", r1);        
        assertThat(p2.getRoutingTable(),is(p2Expected));
        assertThat(p2.getRoutingTable().size(),is(p2Expected.size()));
        assertEquals(p2Expected.containsKey("u1"),p2.getRoutingTable().containsKey("u1"));
        assertEquals(p2Expected.containsKey("u2"),p2.getRoutingTable().containsKey("u2"));
        assertEquals(p2Expected.containsKey("p1"),p2.getRoutingTable().containsKey("p1")); 
        assertEquals(p2Expected.containsKey("p2"),p2.getRoutingTable().containsKey("p2"));
        assertEquals(p2Expected.containsKey("r1"),p2.getRoutingTable().containsKey("r1"));         
        assertEquals(p2Expected.get("u1"),p2.getRoutingTable().get("u1"));
        assertEquals(p2Expected.get("u2"),p2.getRoutingTable().get("u2"));
        assertEquals(p2Expected.get("p2"),p2.getRoutingTable().get("p2"));
        assertEquals(p2Expected.get("p1"),p2.getRoutingTable().get("p1"));
        assertEquals(p2Expected.get("r1"),p2.getRoutingTable().get("r1"));     
    }

    /**
     * Test of Portal constructor, of class Portal, then UserAgent Constructor adding to new portal
     * @throws java.lang.InterruptedException
     */  
    @Test
    public void testAddSecondPortalWithTwoUsers() throws InterruptedException 
    {
        Portal p2 = new Portal("p2",r1);
        UserAgent u3 = new UserAgent("u3",p2);
        UserAgent u4 = new UserAgent("u4",p2);
        Thread.sleep(sleepTime);
        TreeMap<String, MetaAgent> p1Expected = new TreeMap<>();
        p1Expected.put("u1", u1);
        p1Expected.put("u2", u2);
        p1Expected.put("p1", p1);
        p1Expected.put("r1", r1);
        p1Expected.put("p2", r1);
        p1Expected.put("u3", r1);
        p1Expected.put("u4", r1);
        
        assertThat(p1.getRoutingTable(),is(p1Expected));
        assertThat(p1.getRoutingTable().size(),is(p1Expected.size()));
        assertEquals(p1Expected.containsKey("u1"),p1.getRoutingTable().containsKey("u1"));
        assertEquals(p1Expected.containsKey("u2"),p1.getRoutingTable().containsKey("u2"));
        assertEquals(p1Expected.containsKey("p1"),p1.getRoutingTable().containsKey("p1"));  
        assertEquals(p1Expected.containsKey("u3"),p1.getRoutingTable().containsKey("u3"));
        assertEquals(p1Expected.containsKey("u4"),p1.getRoutingTable().containsKey("u4"));
        assertEquals(p1Expected.containsKey("p2"),p1.getRoutingTable().containsKey("p2"));  
        assertEquals(p1Expected.containsKey("r1"),p1.getRoutingTable().containsKey("r1"));
        assertEquals(p1Expected.get("u1"),p1.getRoutingTable().get("u1"));
        assertEquals(p1Expected.get("u2"),p1.getRoutingTable().get("u2"));
        assertEquals(p1Expected.get("p2"),p1.getRoutingTable().get("p2")); 
        assertEquals(p1Expected.get("r1"),p1.getRoutingTable().get("r1")); 
        assertEquals(p1Expected.get("p1"),p1.getRoutingTable().get("p1")); 
        assertEquals(p1Expected.get("u3"),p1.getRoutingTable().get("u3")); 
        assertEquals(p1Expected.get("u4"),p1.getRoutingTable().get("u4")); 
        
        TreeMap<String, MetaAgent> p2Expected = new TreeMap<>();
        p2Expected.put("p2", p2);
        p2Expected.put("u3", u3);
        p2Expected.put("u4", u4); 
        p2Expected.put("u1", r1);
        p2Expected.put("u2", r1);
        p2Expected.put("p1", r1);
        p2Expected.put("r1", r1);
        
        assertThat(p2.getRoutingTable(),is(p2Expected));
        assertThat(p2.getRoutingTable().size(),is(p2Expected.size()));
        assertEquals(p2Expected.containsKey("u1"),p2.getRoutingTable().containsKey("u1"));
        assertEquals(p2Expected.containsKey("u2"),p2.getRoutingTable().containsKey("u2"));
        assertEquals(p2Expected.containsKey("p1"),p2.getRoutingTable().containsKey("p1"));  
        assertEquals(p2Expected.containsKey("u3"),p2.getRoutingTable().containsKey("u3"));
        assertEquals(p2Expected.containsKey("u4"),p2.getRoutingTable().containsKey("u4"));
        assertEquals(p2Expected.containsKey("p2"),p2.getRoutingTable().containsKey("p2"));  
        assertEquals(p2Expected.containsKey("r1"),p2.getRoutingTable().containsKey("r1"));
        assertEquals(p2Expected.get("u1"),p2.getRoutingTable().get("u1"));
        assertEquals(p2Expected.get("u2"),p2.getRoutingTable().get("u2"));
        assertEquals(p2Expected.get("p2"),p2.getRoutingTable().get("p2")); 
        assertEquals(p2Expected.get("r1"),p2.getRoutingTable().get("r1")); 
        assertEquals(p2Expected.get("p1"),p2.getRoutingTable().get("p1")); 
        assertEquals(p2Expected.get("u3"),p2.getRoutingTable().get("u3")); 
        assertEquals(p2Expected.get("u4"),p2.getRoutingTable().get("u4")); 
    }
    
    /**
     * Test of Portal constructor, then addAgent method, of class Portal
     * @throws java.lang.InterruptedException
     */   
    @Test
    public void testAddSecondPortalThenAddUsersToEachPortal() throws InterruptedException 
    {
        Portal p2 = new Portal("p2",r1); 
        UserAgent u3 = new UserAgent("u3",null);
        UserAgent u4 = new UserAgent("u4",null);
        p2.addAgent(u3);
        p2.addAgent(u4);
        Thread.sleep(sleepTime);
        TreeMap<String, MetaAgent> p1Expected = new TreeMap<>();
        p1Expected.put("u1", u1);
        p1Expected.put("u2", u2);
        p1Expected.put("p1", p1);
        p1Expected.put("p2", r1);
        p1Expected.put("u3", r1);
        p1Expected.put("u4", r1);
        p1Expected.put("r1", r1);
        TreeMap<String, MetaAgent> p2Expected = new TreeMap<>();
        p2Expected.put("p2", p2); 
        p2Expected.put("u3", u3);
        p2Expected.put("u4", u4);
        p2Expected.put("u1", r1);
        p2Expected.put("u2", r1);
        p2Expected.put("p1", r1);
        p2Expected.put("r1", r1);
 
        assertThat(p1.getRoutingTable(),is(p1Expected));
        assertThat(p1.getRoutingTable().size(),is(p1Expected.size()));
        assertEquals(p1Expected.containsKey("u1"),p1.getRoutingTable().containsKey("u1"));
        assertEquals(p1Expected.containsKey("u2"),p1.getRoutingTable().containsKey("u2"));
        assertEquals(p1Expected.containsKey("p1"),p1.getRoutingTable().containsKey("p1"));
        assertEquals(p1Expected.containsKey("p2"),p1.getRoutingTable().containsKey("p2"));  
        assertEquals(p1Expected.containsKey("u3"),p1.getRoutingTable().containsKey("u3"));
        assertEquals(p1Expected.containsKey("u4"),p1.getRoutingTable().containsKey("u4"));
        assertEquals(p1Expected.get("u1"),p1.getRoutingTable().get("u1"));
        assertEquals(p1Expected.get("u2"),p1.getRoutingTable().get("u2"));
        assertEquals(p1Expected.get("p2"),p1.getRoutingTable().get("p2"));
        assertEquals(p1Expected.get("p1"),p1.getRoutingTable().get("p1")); 
        assertEquals(p1Expected.get("u3"),p1.getRoutingTable().get("u3"));
        assertEquals(p1Expected.get("u4"),p1.getRoutingTable().get("u4"));        
          
        assertThat(p2.getRoutingTable(),is(p2Expected));
        assertThat(p2.getRoutingTable().size(),is(p2Expected.size()));
        assertEquals(p2Expected.containsKey("u1"),p2.getRoutingTable().containsKey("u1"));
        assertEquals(p2Expected.containsKey("u2"),p2.getRoutingTable().containsKey("u2"));
        assertEquals(p2Expected.containsKey("p1"),p2.getRoutingTable().containsKey("p1"));
        assertEquals(p2Expected.containsKey("p2"),p2.getRoutingTable().containsKey("p2")); 
        assertEquals(p2Expected.containsKey("u3"),p2.getRoutingTable().containsKey("u3"));
        assertEquals(p2Expected.containsKey("u4"),p2.getRoutingTable().containsKey("u4"));        
        assertEquals(p2Expected.get("u1"),p2.getRoutingTable().get("u1"));
        assertEquals(p2Expected.get("u2"),p2.getRoutingTable().get("u2"));
        assertEquals(p2Expected.get("p2"),p2.getRoutingTable().get("p2"));
        assertEquals(p2Expected.get("p1"),p2.getRoutingTable().get("p1")); 
        assertEquals(p2Expected.get("u3"),p2.getRoutingTable().get("u3"));
        assertEquals(p2Expected.get("u4"),p2.getRoutingTable().get("u4"));
    }    

    /**
     * Test of sendMsg method, of class UserAgent.
     * @throws java.lang.Exception
     */
    @Test
    public void testSendMsg_String_String() throws Exception 
    {
        String expected = "u1: test message Recipient: u2"+ 
                System.getProperty("line.separator");
        u1.sendMessage("u2", "test message");
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 32));
    }

    /**
     * Test of sendMsg method, of class UserAGent.
     * @throws java.lang.Exception
     */
    @Test
    public void testSendMsg_MsgData() throws Exception 
    {
        String expected = "u1: test message Recipient: u2"+ 
                System.getProperty("line.separator");
        MsgData message = new MsgData("u1","u2","test message");
        u1.sendMsg(message);
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 32));        
    }
    
    /**
     * Test of sendMsg (null) method, of class UserAgent.
     * @throws java.lang.Exception
     */
    @Test
    public void testSendMsg_NullMsgData() throws Exception 
    {
        String expected = "Empty message"+ 
                System.getProperty("line.separator");
        u1.sendMsg(null);
        Thread.sleep(sleepTime);
        assertEquals(expected,sysOut.toString());        
    }    
    
    /**
     * Test of getRoutingTable method, of class Portal.
     */
    @Test
    public void testGetRoutingTable() 
    {
        TreeMap<String, MetaAgent> expected = new TreeMap<>();
        expected.put("u1", u1);
        expected.put("u2", u2);
        expected.put("p1", p1);
        expected.put("r1", r1);
        assertThat(p1.getRoutingTable(), is(expected));
        assertThat(p1.getRoutingTable().size(), is(expected.size()));
        assertEquals(expected.containsKey("u1"),p1.getRoutingTable().containsKey("u1"));
        assertEquals(expected.containsKey("u2"),p1.getRoutingTable().containsKey("u2"));
        assertEquals(expected.containsKey("p1"),p1.getRoutingTable().containsKey("p1"));
        assertEquals(expected.get("u1"),p1.getRoutingTable().get("u1"));
        assertEquals(expected.get("u2"),p1.getRoutingTable().get("u2"));
        assertEquals(expected.get("p1"),p1.getRoutingTable().get("p1"));
    }
    
    /**
     * Test of values method, of class MetaAgentType.
     */
    @Test
    public void testGetType() 
    {
        MetaAgentType portal = p1.getType();
        MetaAgentType expected = PORTAL;
        assertEquals(expected, portal);
        expected = p1.getRoutingTable().get("p1").getType();
        assertEquals(expected, portal);
        MetaAgentType user = u1.getType();
        expected = USER;
        assertEquals(expected, user);
    }
    
    /**
     * Test of removeAllUsers method, of class Portal
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testRemoveAllUsers() throws InterruptedException 
    {
        p1.removeAllUsers();
        Thread.sleep(sleepTime);
        TreeMap<String, MetaAgent> expected = new TreeMap<>(); 
        expected.put("p1", p1);
        expected.put("r1", r1);
        assertThat(p1.getRoutingTable(), is(expected));
        assertThat(p1.getRoutingTable().size(), is(expected.size()));
        assertEquals(expected.containsKey("u1"),p1.getRoutingTable().containsKey("u1"));
        assertEquals(expected.containsKey("u2"),p1.getRoutingTable().containsKey("u2"));
        assertEquals(expected.containsKey("p1"),p1.getRoutingTable().containsKey("p1"));
        assertEquals(expected.get("u1"),p1.getRoutingTable().get("u1"));
        assertEquals(expected.get("u2"),p1.getRoutingTable().get("u2"));
        assertEquals(expected.get("p1"),p1.getRoutingTable().get("p1"));
    }
    
    /**
     * Test of portal monitor when adding agent
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testMonitorAddAgent() throws InterruptedException 
    {
        r1.setMonitor(rm);
        resetFile(userFile);
        resetFile(systemFile);
        p1.setMonitor(pm);
        UserAgent newAgent = new UserAgent("newAgent",p1);
        String expected = "USER added to: " + separator + "p1\tName: " +
                separator + "newAgent\tTime Added: " + separator; //exclude time
        assertEquals(expected,readFile(systemFile).substring(0, 54));
        assertEquals("",readFile(userFile));
    }   
    
    /**
     * Test of portal monitor when removing agent
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testMonitorRemoveUserAgent() throws InterruptedException 
    {
        r1.setMonitor(rm);
        resetFile(userFile);
        resetFile(systemFile);
        p1.setMonitor(pm);
        p1.removeAgent("u1");
        String expected = "USER removed by: " + separator + "p1\tName: " +
                separator + "u1\tTime Removed: " + separator; //exclude time
        assertEquals(expected,readFile(systemFile).substring(0, 52));
        assertEquals("",readFile(userFile));
    }
    
    /**
     * Test of portal monitor when local users send message
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testMonitorUserMessage() throws InterruptedException 
    {
        r1.setMonitor(rm);
        p1.setMonitor(pm);
        resetFile(userFile);
        resetFile(systemFile);
        u1.sendMessage("u2", "testMessage");
        String sentExpected1 = "Message sent to: " + separator + "u2\tSender: " +
                separator + "u1\tPassed to: " + separator + "p1\tTime sent: " + 
                separator;
        String sentExpected2 = "\tMessage body: " + separator + "testMessage";
        String receivedExpected1 = "Message received from: " + separator + "u1\tRecipient: " +
                separator + "u2\tDelivered by: " + separator + "p1\tTime received: " + 
                separator;
        String receivedExpected2 = "\tMessage body: " + separator + "testMessage";
        String fileContents = readFile(userFile);
        assertTrue(fileContents.contains(sentExpected1));
        assertTrue(fileContents.contains(sentExpected2));
        assertTrue(fileContents.contains(receivedExpected1));
        assertEquals("",readFile(systemFile));
    }  
    
    /**
     * Test of portal monitor when local users send message
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testMonitorMultipleActions() throws InterruptedException 
    {
        r1.setMonitor(rm);       
        resetFile(userFile);
        resetFile(systemFile);
        p1.setMonitor(pm);
        p1.removeAgent("u2");
        UserAgent u3 = new UserAgent("u3",p1);
        u1.sendMessage("u3", "testMessage");
        String sentExpected1 = "Message sent to: " + separator + "u3\tSender: " +
                separator + "u1\tPassed to: " + separator + "p1\tTime sent: " + 
                separator;
        String sentExpected2 = "\tMessage body: " + separator + "testMessage";
        String receivedExpected1 = "Message received from: " + separator + "u1\tRecipient: " +
                separator + "u3\tDelivered by: " + separator + "p1\tTime received: " + 
                separator;
        String receivedExpected2 = "\tMessage body: " + separator + "testMessage";
        String fileContents = readFile(userFile);
        assertEquals(sentExpected1,fileContents.substring(0, 68));
        assertEquals(sentExpected2,fileContents.substring(91, 120));
        assertEquals(receivedExpected1,fileContents.substring(120, 204));
        assertEquals(receivedExpected2,fileContents.substring(227, 256));
        String sysExpected1 = "USER removed by: " + separator + "p1\tName: " +
                separator + "u2\tTime Removed: " + separator; //exclude time        
        String sysExpected2 = "USER added to: " + separator + "p1\tName: " +
                separator + "u3\tTime Added: " + separator; //exclude time
        fileContents = readFile(systemFile);
        assertEquals(sysExpected1,readFile(systemFile).substring(0, 52));  
        assertEquals(sysExpected2,readFile(systemFile).substring(75, 123));  
    }  

    /**
     * Test of msgHandler with null MsgData.type
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    @Test
    public void testMsgHandlerNullMsgDataType() throws InterruptedException, IOException 
    {
        MsgData md = new MsgData("handle", "sender", "recipient", "body", null);
        String expected = "Invalid type" + System.getProperty("line.separator");
        p1.msgHandler(md);
        assertEquals(expected,sysOut.toString());
    }    
    
    /**
     * Test of addAgent with agent adding same type as added
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    @Test
    public void testAddAgentSameTypes() throws InterruptedException, IOException 
    {
        Portal p2 = new Portal("p2",r1);
        assertEquals(false,p1.addAgent(p2));
    }    
    
    /**
     * Test of removeAgent, removing router
     */
    @Test
    public void testRemoveRouter()
    {
        String expected = "Router removal is disallowed..." + 
                System.getProperty("line.separator");
        p1.removeAgent("r1");
        assertEquals(expected,sysOut.toString());
    } 
    
    /**
     * Test of getTypeFromTable (Router)
     */
    @Test
    public void testGetTypeFromTableRouter()
    {
        assertEquals(MetaAgentType.ROUTER,p1.getTypeFromTable("r1"));
    }  
    
    /**
     * Test of getTypeFromTable, (empty string)
     */
    @Test
    public void testGetTypeFromTableEmptyString()
    {
        assertEquals(null,p1.getTypeFromTable(""));
    }      
    
    /**
     * Test of getTypeFromTable, (null)
     */
    @Test
    public void testGetTypeFromTableNull()
    {
        assertEquals(null,p1.getTypeFromTable(null));
    }         
}
