package metaagentsubsystem;

import static metaagentsubsystem.MetaAgentType.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Group B, Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class MetaAgentTypeTest {
    
    /**
     * Method used to call the test
     */
    public MetaAgentTypeTest() {
    }
    
    /**
     * Test of values method, of class MetaAgentType.
     */
    @Test
    public void testValues() {
        MetaAgentType[] expResult = {USER,ROUTER,PORTAL,SOCKET};
        MetaAgentType[] result = MetaAgentType.values();
        assertArrayEquals(expResult, result);
    }
    
    /**
     * Test of valueOf method, of class MetaAgentType.
     */
    @Test
    public void testValueOf() 
    {
        assertEquals(USER, MetaAgentType.valueOf("USER"));
        assertEquals(ROUTER, MetaAgentType.valueOf("ROUTER"));
        assertEquals(PORTAL, MetaAgentType.valueOf("PORTAL"));
        assertEquals(SOCKET, MetaAgentType.valueOf("SOCKET"));
    }
}
