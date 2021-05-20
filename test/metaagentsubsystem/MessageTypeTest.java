package metaagentsubsystem;

import static metaagentsubsystem.MessageType.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Group B, Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class MessageTypeTest {
    
    /**
     * Method used to call the test
     */
    public MessageTypeTest() {
    }
    
    /**
     * Test of values method, of class MessageType.
     */
    @Test
    public void testValues() 
    {
        MessageType[] expResult = {USER,ADD,REMOVE,HANDLE,LIST,RETURN};
        MessageType[] result = MessageType.values();
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of valueOf method, of class MessageType.
     */
    @Test
    public void testValueOf() 
    {
        assertEquals(USER, MessageType.valueOf("USER"));
        assertEquals(ADD, MessageType.valueOf("ADD"));
        assertEquals(REMOVE, MessageType.valueOf("REMOVE"));
        assertEquals(HANDLE, MessageType.valueOf("HANDLE"));
        assertEquals(LIST, MessageType.valueOf("LIST"));
        assertEquals(RETURN, MessageType.valueOf("RETURN"));
    }
    
}
