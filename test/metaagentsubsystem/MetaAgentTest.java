package metaagentsubsystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import static metaagentsubsystem.Router.createNew;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Group B, Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class MetaAgentTest 
{
    private ByteArrayOutputStream sysOut;
    private static Router r1;
    private static Portal p1;
    private static UserAgent u1;
    private static UserAgent u2;
    private static MetaAgent ma;
    
    /**
     * Method used to call the test
     */
    public MetaAgentTest() 
    {
    }
    
    /**
     * Initialise MetaAgent for tests
     */
    @BeforeClass
    public static void setUpClass() 
    {
        ma = new MetaAgent();
    }
    
    /**
     * Setup basic network before each test
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
        r1 = createNew("r1");
        p1 = new Portal("p1",r1);
        u1 = new UserAgent("u1",p1);
        u2 = new UserAgent("u2",p1);
        sysOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(sysOut));
    }

    /**
     * Overrides router singleton for testing purposes
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
     * Test of sendMsg method, of class UserAgent.
     * @throws java.lang.Exception
     */
    @Test
    public void testSendMsg_String_String() throws Exception 
    {
        String expected = "u1: test message Recipient: u2"+ 
                System.getProperty("line.separator");
        u1.sendMessage("u2", "test message");
        Thread.sleep(1000);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 32));
    }
    
    /**
     * Test of sendMsg method, of class UserAgent.
     * @throws java.lang.Exception
     */
    @Test
    public void testSendMsg_String_StringNull() throws Exception 
    {
        String expected = "";
        u1.sendMessage(null, null);
        Thread.sleep(1000);
        assertEquals(expected,sysOut.toString());
    }

    /**
     * Test of sendMsg method, of class UserAgent.
     * @throws java.lang.Exception
     */
    @Test
    public void testSendMsg_MsgData() throws Exception 
    {
        String expected = "u1: test message Recipient: u2" + 
                System.getProperty("line.separator");
        MsgData message = new MsgData("u1","u2","test message");
        u1.sendMsg(message);
        Thread.sleep(1000);
        assertEquals(expected,sysOut.toString().substring(sysOut.toString().length() - 32)); 
    }

    /**
     * Test of getHandle method, of various MetaAgents.
     */
    @Test
    public void testGetHandle() 
    {
        String expected = "p1";
        assertEquals(expected,p1.getHandle()); 
        expected = "u2";
        assertEquals(expected,u2.getHandle()); 
    }

    /**
     * Test of msgHandler method, of class MetaAgent.
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    @Test
    public void testMsgHandler() throws InterruptedException, IOException 
    {
        MsgData md = new MsgData("testSender","testRecipient","testMessage");
        String expected = md.toString() + 
                System.getProperty("line.separator");
        ma.msgHandler(md);
        assertEquals(expected,sysOut.toString()); 
    }
    
    /**
     * Test of removeConnections method, of class MetaAgent.
     */
    @Test
    public void testRemoveConnections()
    {
        ma.removeConnections();
    }   
    
    /**
     * Test of terminateThread method, of class MetaAgent.
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testTerminateThread() throws InterruptedException
    {
        ma.terminateThread();
    }    
    
    /**
     * Test of getRoutingTable method, of class MetaAgent.
     */
    @Test
    public void testGetRoutingTable()
    {
        String expected = "Agent does not have a routing table" + 
                System.getProperty("line.separator");
        assertEquals(null,ma.getRoutingTable());
        assertEquals(expected,sysOut.toString()); 
    }     
}
