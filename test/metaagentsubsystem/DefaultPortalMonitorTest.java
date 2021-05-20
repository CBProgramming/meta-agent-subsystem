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
 * Class to test the default monitor
 * @author Group B, Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class DefaultPortalMonitorTest 
{
    private static FileManager fm;
    private static String fileName;
    private static String separator;
    private static DefaultPortalMonitor pm0;
    private static LocalDateTime time;
    
    /**
     * Method used to call the test
     */
    public DefaultPortalMonitorTest() {
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
        pm0 = new DefaultPortalMonitor(fm,fm);
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
     * Test of agentAdded method, of class DefaultPortalMonitor.
     */
    @Test
    public void testAgentAdded() 
    {
        pm0.agentAdded("testHandle", "testAgent", MetaAgentType.PORTAL, time);
        String expected = "PORTAL added to: " + separator + "testHandle\tName: " +
                separator + "testAgent\tTime Added: " + separator + time;
        assertEquals(expected,readFile(fileName));
    }

    /**
     * Test of agentRemoved method, of class DefaultPortalMonitor.
     */
    @Test
    public void testAgentRemoved() 
    {
        pm0.agentRemoved("testHandle", "testAgent", MetaAgentType.PORTAL, time);
        String expected = "PORTAL removed by: " + separator + "testHandle\tName: " +
                separator + "testAgent\tTime Removed: " + separator + time;
        assertEquals(expected,readFile(fileName));
    }

    /**
     * Test of messageRecieved method, of class DefaultPortalMonitor.
     */
    @Test
    public void testMessageRecieved() 
    {
        pm0.messageRecieved("testPortal", "testSender", "testRecipient", "testMessage", time);
        String expected = "Message received from: " + separator + "testSender\tRecipient: " +
                separator + "testRecipient\tDelivered by: " + separator + "testPortal\tTime received: " + 
                separator + time + "\tMessage body: " + separator + "testMessage";
        assertEquals(expected,readFile(fileName));
    }

    /**
     * Test of messageSent method, of class DefaultPortalMonitor.
     */
    @Test
    public void testMessageSent() 
    {
        pm0.messageSent("testPortal", "testSender", "testRecipient", "testMessage", time);
        String expected = "Message sent to: " + separator + "testRecipient\tSender: " +
                separator + "testSender\tPassed to: " + separator + "testPortal\tTime sent: " + 
                separator + time + "\tMessage body: " + separator + "testMessage";
        assertEquals(expected,readFile(fileName));
    }
    
    /**
     * Test one of each portal monitor methods.
     */
    @Test
    public void multiplePortalMonitorMethods()
    {
        pm0.agentAdded("testHandle", "testAgent", MetaAgentType.PORTAL, time);
        pm0.agentRemoved("testHandle", "testAgent", MetaAgentType.PORTAL, time);
        pm0.messageRecieved("testPortal", "testSender", "testRecipient", "testMessage", time);
        pm0.messageSent("testPortal", "testSender", "testRecipient", "testMessage", time);
        String expected = "PORTAL added to: " + separator + "testHandle\tName: " +
                separator + "testAgent\tTime Added: " + separator + time +      
                "PORTAL removed by: " + separator + "testHandle\tName: " +
                separator + "testAgent\tTime Removed: " + separator + time + 
                "Message received from: " + separator + "testSender\tRecipient: " +
                separator + "testRecipient\tDelivered by: " + separator + "testPortal\tTime received: " + 
                separator + time + "\tMessage body: " + separator + "testMessage" + 
                "Message sent to: " + separator + "testRecipient\tSender: " +
                separator + "testSender\tPassed to: " + separator + "testPortal\tTime sent: " + 
                separator + time + "\tMessage body: " + separator + "testMessage";
        assertEquals(expected,readFile(fileName));
    }

    /**
     * Test of agentAdded (null) method, of class DefaultPortalMonitor.
     */
    @Test
    public void testAgentAddedNullExceptType() 
    {
        pm0.agentAdded(null, null, MetaAgentType.PORTAL, null);
        String expected = "PORTAL added to: " + separator + "null\tName: " +
                separator + "null\tTime Added: " + separator + "null";
        assertEquals(expected,readFile(fileName));
    }
    
    /**
     * Test of agentAdded (null) method, of class DefaultPortalMonitor.
     */
    @Test (expected = NullPointerException.class)
    public void testAgentAddedNullType() 
    {
        pm0.agentAdded(null, null, null, null);
    }

    /**
     * Test of agentRemoved (null) method, of class DefaultPortalMonitor.
     */
    @Test
    public void testAgentRemovedNullExceptType() 
    {
        pm0.agentRemoved(null, null, MetaAgentType.PORTAL, null);
        String expected = "PORTAL removed by: " + separator + "null\tName: " +
                separator + "null\tTime Removed: " + separator + "null";
        assertEquals(expected,readFile(fileName));
    }
    
        /**
     * Test of agentRemoved (null)  method, of class DefaultPortalMonitor.
     */
    @Test (expected = NullPointerException.class)
    public void testAgentRemovedNullType() 
    {
        pm0.agentRemoved(null, null, null, null);
    }

    /**
     * Test of messageRecieved (null) method, of class DefaultPortalMonitor.
     */
    @Test
    public void testMessageRecievedNull() 
    {
        pm0.messageRecieved(null, null, null, null, null);
        String expected = "Message received from: " + separator + "null\tRecipient: " +
                separator + "null\tDelivered by: " + separator + "null\tTime received: " + 
                separator + "null\tMessage body: " + separator + "null";
        assertEquals(expected,readFile(fileName));
    }

    /**
     * Test of messageSent (null) method, of class DefaultPortalMonitor.
     */
    @Test
    public void testMessageSentNull() 
    {
        pm0.messageSent(null, null, null, null, null);
        String expected = "Message sent to: " + separator + "null\tSender: " +
                separator + "null\tPassed to: " + separator + "null\tTime sent: " + 
                separator + "null\tMessage body: " + separator + "null";
        assertEquals(expected,readFile(fileName));
    }
}
