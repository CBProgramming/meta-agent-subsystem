package metaagentsubsystem;

/**
 * Interface which enforces the usage of the method inside, used for Portal agent and user additions and deletions
 * @author Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public interface UserMonitor extends AgentMonitor
{
    /**
     * Empty method needed for when implemented
     */
    public abstract void removal();
}
