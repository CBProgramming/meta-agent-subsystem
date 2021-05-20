package metaagentsubsystem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Group B, Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class DefaultRouterMonitorTest 
{
    private static FileManager fm;
    private static String fileName;
    private static String separator;
    private static DefaultRouterMonitor rm0;
    private static LocalDateTime time;
    
    /**
     * Method used to call the test
     */
    public DefaultRouterMonitorTest() {
    }
    
    /**
     * Initialise variables before tests run
     */   
    @BeforeClass
    public static void setUpClass() 
    {
        separator = "¶»¤";
        fileName = "testFile.txt";
        fm = new FileManager(fileName);
        rm0 = new DefaultRouterMonitor(fm,fm);
        time = LocalDateTime.now();
    }
    
    /**
     * reset file on completion of tests
     */  
    @AfterClass
    public static void tearDownClass() 
    {
        resetFile(fileName);
    }
    
    /**
     * reset file before each test
     */   
    @Before
    public void setUp() 
    {
        resetFile(fileName);
    }
    
    /**
     * Resets log files used by file managers
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
     * Reads file and returns string contents
     * @param fileName name of file
     * @return file contents as string
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
     * Test of agentAdded method, of class DefaultRouterMonitor.
     */
    @Test
    public void testAgentAdded() 
    {
        rm0.agentAdded("testHandle", "testAgent", MetaAgentType.PORTAL, time);
        String expected = "PORTAL added to: " + separator + "testHandle\tName: " +
                separator + "testAgent\tTime Added: " + separator + time;
        assertEquals(expected,readFile(fileName));
    }

    /**
     * Test of agentRemoved method, of class DefaultRouterMonitor.
     */
    @Test
    public void testAgentRemoved() 
    {
        rm0.agentRemoved("testHandle", "testAgent", MetaAgentType.PORTAL, time);
        String expected = "PORTAL removed by: " + separator + "testHandle\tName: " +
                separator + "testAgent\tTime Removed: " + separator + time;
        assertEquals(expected,readFile(fileName));
    }

    /**
     * Test of messageRecieved method, of class DefaultRouterMonitor.
     */
    @Test
    public void testMessageRecieved() 
    {
        rm0.messageRecieved("testPortal", "testSender", "testRecipient", "testMessage", time);
        String expected = "Message forwarded from router to external user: " + separator + "testRecipient\tSender: " +
                separator + "testSender\tTime sent: " + 
                separator + time + "\tMessage body: " + separator + "testMessage";
        assertEquals(expected,readFile(fileName));
    }

    /**
     * Test of messageSent method, of class DefaultRouterMonitor.
     */
    @Test
    public void testMessageSent() 
    {
        rm0.messageSent("testPortal", "testSender", "testRecipient", "testMessage", time);
        String expected = "Message received in local router from external user: " + separator + "testSender\tRecipient: " +
                separator + "testRecipient\tTime received: " + 
                separator + time + "\tMessage body: " + separator + "testMessage";
        assertEquals(expected,readFile(fileName));
    }
    
    /**
     * Test one of each router monitor methods.
     */
    @Test
    public void multiplePortalMonitorMethods()
    {
        rm0.agentAdded("testHandle", "testAgent", MetaAgentType.PORTAL, time);
        rm0.agentRemoved("testHandle", "testAgent", MetaAgentType.PORTAL, time);
        rm0.messageRecieved("testPortal", "testSender", "testRecipient", "testMessage", time);
        rm0.messageSent("testPortal", "testRecipient","testSender", "testMessage", time);
        String expected1 = "PORTAL added to: " + separator + "testHandle\tName: " +
                separator + "testAgent\tTime Added: " + separator + time;
         String expected2 = "PORTAL removed by: " + separator + "testHandle\tName: " +
                separator + "testAgent\tTime Removed: " + separator + time;       
        String expected3 = "Message forwarded from router to external user: " + separator + "testRecipient\tSender: " +
                separator + "testSender\tTime sent: " + 
                separator + time + "\tMessage body: " + separator + "testMessage";        
        String expected4 = "Message received in local router from external user: " + separator + "testRecipient\tRecipient: " +
                separator + "testSender\tTime received: " + 
                separator + time + "\tMessage body: " + separator + "testMessage";
        String fileContents = readFile(fileName);
        assertTrue(fileContents.contains(expected1));
        assertTrue(fileContents.contains(expected2));
        assertTrue(fileContents.contains(expected3));
        assertTrue(fileContents.contains(expected4));
    }

    /**
     * Test of agentAdded (null) method, of class DefaultRouterMonitor.
     */
    @Test
    public void testAgentAddedNullExceptType() 
    {
        rm0.agentAdded(null, null, MetaAgentType.PORTAL, null);
        String expected = "PORTAL added to: " + separator + "null\tName: " +
                separator + "null\tTime Added: " + separator + "null";
        assertEquals(expected,readFile(fileName));
    }
    
    /**
     * Test of agentAdded (null) method, of class DefaultRouterMonitor.
     */
    @Test (expected = NullPointerException.class)
    public void testAgentAddedNullType() 
    {
        rm0.agentAdded(null, null, null, null);
    }

    /**
     * Test of agentRemoved (null) method, of class DefaultRouterMonitor.
     */
    @Test
    public void testAgentRemovedNullExceptType() 
    {
        rm0.agentRemoved(null, null, MetaAgentType.PORTAL, null);
        String expected = "PORTAL removed by: " + separator + "null\tName: " +
                separator + "null\tTime Removed: " + separator + "null";
        assertEquals(expected,readFile(fileName));
    }
    
    /**
     * Test of agentRemoved (null) method, of class DefaultRouterMonitor.
     */
    @Test (expected = NullPointerException.class)
    public void testAgentRemovedNullType() 
    {
        rm0.agentRemoved(null, null, null, null);
        String expected = "PORTAL removed by: " + separator + "null\tName: " +
                separator + "null\tTime Added: " + separator + "null";
        assertEquals(expected,readFile(fileName));
    }

    /**
     * Test of messageRecieved (null) method, of class DefaultRouterMonitor.
     */
    @Test
    public void testMessageRecievedNull() 
    {
        rm0.messageRecieved(null, null, null, null, null);
        String expected = "Message forwarded from router to external user: " + separator + "null\tSender: " +
                separator + "null\tTime sent: " + 
                separator + "null\tMessage body: " + separator + "null";
        assertEquals(expected,readFile(fileName));
    }

    /**
     * Test of messageSent (null) method, of class DefaultRouterMonitor.
     */
    @Test
    public void testMessageSentNull() 
    {
        rm0.messageSent(null, null, null, null, null);
        String expected = "Message received in local router from external user: " + separator + "null\tRecipient: " +
                separator + "null\tTime received: " + 
                separator + "null\tMessage body: " + separator + "null";
        assertEquals(expected,readFile(fileName));
    }
    
    /**
     * Test of newConnection method, of class DefaultRouterMonitor.
     */
    @Test
    public void testnewConnection() 
    {
        rm0.newConnection("testPortalHandle", "testConnectionHandle", time);
        String expected = "New connection between: " + separator + 
                "testPortalHandle\tand: " + separator + 
                "testConnectionHandle\tTime: " + separator + time;
        assertEquals(expected,readFile(fileName));        
    }   
    
    /**
     * Test of newConnection (null) method, of class DefaultRouterMonitor.
     */
    @Test
    public void testnewConnectionNull() 
    {
        rm0.newConnection(null, null, time);
        String expected = "New connection between: " + separator + 
                "null\tand: " + separator + "null\tTime: " + separator + time;
        assertEquals(expected,readFile(fileName));        
    }  
}