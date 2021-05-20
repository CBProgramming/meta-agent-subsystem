package metaagentsubsystem;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static metaagentsubsystem.MessageType.*;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Group B, Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class MsgDataTest 
{
    private static MsgData message;
    private static List <String> expectedAgentList;
    private static Date date;
    private static Timestamp timestamp;
    
    /**
     * Method used to call the test
     */
    public MsgDataTest() 
    {
    }
    
    /**
     * Initialise variables before tests run
     */ 
    @BeforeClass
    public static void setUpClass() 
    {
        date = new Date();
        timestamp = new Timestamp(date.getTime());
        message = new MsgData("u1","u2","u3","test message",USER);
        expectedAgentList = new ArrayList<>();
        expectedAgentList.add("u1");
        expectedAgentList.add("u2");
    }
    
    /**
     * Test of getId method, of class MsgData.
     */
    @Test
    public void testGetId() 
    {
        int expectedId = MsgData.msgCount;
        assertEquals(expectedId,message.getId());
    }
    
    /**
     * Test of getId method, of class MsgData.
     */
    @Test
    public void testGetIdSecondInstantiation() 
    {
        MsgData secondInstantiation = new MsgData("u1","u2","u3","test message",USER);
        int expectedId = MsgData.msgCount;
        assertEquals(expectedId,secondInstantiation.getId());
    }    
    
    /**
     * Test of getAgentsList method, of class MsgData.
     */
    @Test
    public void testGetAgentsList() 
    {
        MsgData testMessage = new MsgData(expectedAgentList);
        assertEquals(expectedAgentList,testMessage.getAgentsList());
    }

    /**
     * Test of setAgentsList method, of class MsgData.
     */
    @Test
    public void testSetAgentsList() 
    {
        message.setAgentsList(expectedAgentList);
        assertEquals(expectedAgentList,message.getAgentsList());
    }
    
    /**
     * Test of setAgentsList method, of class MsgData.
     */
    @Test
    public void testSetAgentsListNull() 
    {
        message.setAgentsList(null);
        assertEquals(null,message.getAgentsList());
    }    

    /**
     * Test of getBody method, of class MsgData.
     */
    @Test
    public void testGetBody() 
    {
        message.setBody("test message");
        String testBody = "test message";
        assertEquals(testBody,message.getBody());
    }

    /**
     * Test of setBody method, of class MsgData.
     */
    @Test
    public void testSetBody() 
    {
        String newMessage = "new message";
        message.setBody(newMessage);
        assertEquals(newMessage,message.getBody());
    }
    
     /**
     * Test of setBody (null) method, of class MsgData.
     */
    @Test
    public void testSetBodyNull() 
    {
        message.setBody(null);
        assertEquals(null,message.getBody());
    }   

    /**
     * Test of getHandle method, of class MsgData.
     */
    @Test
    public void testGetHandle() 
    {
        String testBody = "u1";
        assertEquals(testBody,message.getHandle());       
    }

    /**
     * Test of getTimestamp method, of class MsgData.
     * May occasionally fail due to second crossover
     */
    @Test
    public void testGetTimestamp() 
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        String testTimestamp = sdf.format(timestamp); 
        assertEquals(testTimestamp,message.getTimestamp());
    }

    /**
     * Test of getType method, of class MsgData.
     */
    @Test
    public void testGetType() 
    {
        assertEquals(USER,message.getType());
    }

    /**
     * Test of getSender method, of class MsgData.
     */
    @Test
    public void testGetSender() 
    {
        MsgData testMessage = new MsgData("u1","u2","u3","test message",USER);
        String testSender = "u2";
        assertEquals(testSender,testMessage.getSender());  
    }

    /**
     * Test of setSender method, of class MsgData.
     */
    @Test
    public void testSetSender() 
    {
        String newSender = "u4";
        message.setSender(newSender);
        assertEquals(newSender,message.getSender());  
    }
    
     /**
     * Test of setSender (null) method, of class MsgData.
     */
    @Test
    public void testSetSenderNull() 
    {
        message.setSender(null);
        assertEquals(null,message.getSender());  
    }   

    /**
     * Test of getRecipient method, of class MsgData.
     */
    @Test
    public void testGetRecipient() 
    {
        MsgData testMessage = new MsgData("u1","u2","u3","test message",USER);
        String testRecipient = "u3";
        assertEquals(testRecipient,testMessage.getRecipient()); 
    }

    /**
     * Test of setRecipient method, of class MsgData.
     */
    @Test
    public void testSetRecipient() 
    {
        String newRecipient = "u5";
        message.setRecipient(newRecipient);
        assertEquals(newRecipient,message.getRecipient());  
    }

    /**
     * Test of setRecipient (null) method, of class MsgData.
     */
    @Test
    public void testSetRecipientNull() 
    {
        message.setRecipient(null);
        assertEquals(null,message.getRecipient());  
    }
    
    /**
     * Test of setId method, of class MsgData.
     */
    @Test
    public void testSetId() 
    {
        int newId = 99;
        message.setId(99);
        assertEquals(newId,message.getId()); 
    }
    
    /**
     * Test of MsgData (String handle, MessageType type) constructor, of class 
     * MsgData.
     */
    @Test
    public void testAltMsgDataConstructor1() 
    {
        MsgData testMsgData = new MsgData("handle",USER);
        assertEquals("handle",testMsgData.getHandle());
        assertEquals(USER,testMsgData.getType());
        assertEquals(null,testMsgData.getSender());
        assertEquals(null,testMsgData.getRecipient());
        assertEquals(null,testMsgData.getBody());
    }  

    /**
     * Test of MsgData (String handle, MessageType type) constructor, of class 
     * MsgData.
     */
    @Test
    public void testAltMsgDataConstructor1Null() 
    {
        MsgData testMsgData = new MsgData(null,null);
        assertEquals(null,testMsgData.getHandle());
        assertEquals(null,testMsgData.getType());
        assertEquals(null,testMsgData.getSender());
        assertEquals(null,testMsgData.getRecipient());
        assertEquals(null,testMsgData.getBody());
    }  
    
    /**
     * Test of MsgData(String sender, String recipient, MessageType type) 
     * constructor, of class MsgData.
     */
    @Test
    public void testAltMsgDataConstructor2() 
    {
        MsgData testMsgData = new MsgData("sender","recipient",USER);
        assertEquals(null,testMsgData.getHandle());
        assertEquals(USER,testMsgData.getType());
        assertEquals("sender",testMsgData.getSender());
        assertEquals("recipient",testMsgData.getRecipient());
        assertEquals(null,testMsgData.getBody());
    }    
    
    /**
     * Test of MsgData(String sender, String recipient, MessageType type) 
     * constructor, of class MsgData.
     */
    @Test
    public void testAltMsgDataConstructor2Null() 
    {
        MessageType unambiguous = null;
        MsgData testMsgData = new MsgData(null,null,unambiguous);
        assertEquals(null,testMsgData.getHandle());
        assertEquals(null,testMsgData.getType());
        assertEquals(null,testMsgData.getSender());
        assertEquals(null,testMsgData.getRecipient());
        assertEquals(null,testMsgData.getBody());
    }      
    
    /**
     * Test of MsgData(String sender, String recipient, String body) 
     * constructor, of class MsgData.
     */
    @Test
    public void testAltMsgDataConstructor3() 
    {
        MsgData testMsgData = new MsgData("sender","recipient","body");
        assertEquals(null,testMsgData.getHandle());
        assertEquals(USER,testMsgData.getType());
        assertEquals("sender",testMsgData.getSender());
        assertEquals("recipient",testMsgData.getRecipient());
        assertEquals("body",testMsgData.getBody());
    }        
    
    /**
     * Test of MsgData(String sender, String recipient, String body) 
     * constructor, of class MsgData.
     */
    @Test
    public void testAltMsgDataConstructor3Null() 
    {
        String unambiguous = null;
        MsgData testMsgData = new MsgData(null,null,unambiguous);
        assertEquals(null,testMsgData.getHandle());
        assertEquals(USER,testMsgData.getType());
        assertEquals(null,testMsgData.getSender());
        assertEquals(null,testMsgData.getRecipient());
        assertEquals(null,testMsgData.getBody());
    }    
}
