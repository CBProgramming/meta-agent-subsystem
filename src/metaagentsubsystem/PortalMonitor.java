package metaagentsubsystem;

import java.time.LocalDateTime;

/**
 * Interface which enforces the usage of the methods inside used for user agent additions and deletions
 * @author Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public interface PortalMonitor extends AgentMonitor
{
    /**
     * Empty Method needed for when portal monitor is implemented
     * @param portalHandle - name
     * @param agentName - agent name
     * @param type agent type
     * @param time time added
     */
    public abstract void agentAdded(String portalHandle, String agentName,  MetaAgentType type, LocalDateTime time);
    
    /**
     * Empty Method needed for when portal monitor is implemented
     * @param portalHandle - name
     * @param agentName - agent name
     * @param type agent type
     * @param time time removed
     */
    public abstract void agentRemoved(String portalHandle, String agentName,  MetaAgentType type, LocalDateTime time);
}
