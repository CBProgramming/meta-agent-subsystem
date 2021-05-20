package metaagentsubsystem;

import java.time.LocalDateTime;

/**
 * Interface which enforces the usage of the methods inside used for Portal agent additions and deletions
 * @author Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public interface RouterMonitor extends AgentMonitor
{
    /**
     * Empty Method needed for when router monitor is implemented
     * @param portalHandle name of portal
     * @param agentName agent name
     * @param type agent type
     * @param time time of addition
     */
    public abstract void agentAdded(String portalHandle, String agentName, MetaAgentType type, LocalDateTime time);
    
    /**
     * Empty Method needed for when portal monitor is implemented
     * @param portalHandle name of portal
     * @param agentName agent name
     * @param type agent type
     * @param time time of addition 
     */
    public abstract void agentRemoved(String portalHandle, String agentName,  MetaAgentType type, LocalDateTime time);
    
    /**
     * Empty Method needed for when portal monitor is implemented
     * @param portalHandle name of the portal
     * @param connectionHandle name given to a connection
     * @param time time of connection
     */
    public abstract void newConnection(String portalHandle, String connectionHandle, LocalDateTime time);
}
