package metaagentsubsystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Map;
import static metaagentsubsystem.MessageType.USER;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Class used to test the User Agent
 * @author Group B, Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class UserAgentTest 
{
    private ByteArrayOutputStream sysOut;
    private static UserAgent u1;
    private static MsgData message;
    private Router r1;
    
    /**
     * Method used to call the test
     */
    public UserAgentTest() {
    }
    
    /**
     * Initialise variables for all tests
     */  
    @BeforeClass
    public static void setUpClass() 
    {
        u1 = new UserAgent("u1",null);
        message = new MsgData("u1","u2","u1","test message",USER);
    }
    
    /**
     * Setup basic network for each test
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
        sysOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(sysOut));
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
     * Test of msgHandler method, of class UserAgent.
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    @Test
    public void testMsgHandler() throws InterruptedException, IOException
    {
        String expected = "u2: test message Recipient: u1" + 
                System.getProperty("line.separator");
        u1.msgHandler(message);
        assertEquals(expected,sysOut.toString());
    }  

    /**
     * Test of msgHandler (null) method, of class UserAgent.
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     */
    @Test
    public void testMsgHandlerNull() throws InterruptedException, IOException
    {
        String expected = "Error. Null message received." + 
                System.getProperty("line.separator");
        u1.msgHandler(null);
        assertEquals(expected,sysOut.toString());
    }  
    
    /**
     * Test of receiveMsg method, of class UserAgent.
     */
    @Test
    public void testReceiveMsg()
    {
        String expected = "u2: test message Recipient: u1" + 
                System.getProperty("line.separator");
        u1.receiveMsg(message);
        assertEquals(expected,sysOut.toString());
    }  

    /**
     * Test of receiveMsg method, of class UserAgent.
     */
    @Test
    public void testReceiveMsgNull()
    {
        String expected = "Error. Null message received." + 
                System.getProperty("line.separator");
        u1.receiveMsg(null);
        assertEquals(expected,sysOut.toString());
    } 
    
    /**
     * Test of getRoutingTable method, of class UserAgent.
     */
    @Test
    public void testgetRoutingTable()
    {
        assertEquals(null,u1.getRoutingTable());
    }  

    /**
     * Test of getPortal method, of class UserAgent.
     */
    @Test
    public void testGetPortal() {
        Portal expected = null;
        assertEquals(expected,u1.getPortal()); 
        r1 = Router.createNew("r1");
        Portal p1 = new Portal("p1",r1);
        expected = p1;
        UserAgent u2 = new UserAgent ("u2",p1);
        assertEquals(expected,u2.getPortal()); 
    }    
    
    /**
     * Test creating two users with same handle
     */
    @Test
    public void testTwoUsersSameHandle() 
    {
        String expected = "Agent already exists" + 
                System.getProperty("line.separator");
        r1 = Router.createNew("r1");
        Portal p1 = new Portal("p1",r1);
        UserAgent u1 = new UserAgent("handle",p1);
        UserAgent u2 = new UserAgent("handle",p1);
        assertEquals(expected,sysOut.toString());
        Portal p2 = new Portal("p2",r1);
        p2.addAgent(u2);
        expected = "Agent already exists" + System.getProperty("line.separator") + 
                "Agent already exists" + System.getProperty("line.separator");
        assertEquals(expected,sysOut.toString());
    }      
}
