package metaagentsubsystem;

import java.io.Serializable;

/**
 * Enumeration class created to identify message types.
 * @author Group B, Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */

public enum MessageType implements Serializable{

    USER,
    ADD,
    REMOVE,
    HANDLE,
    LIST,
    RETURN
}
