package metaagentsubsystem;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.JFrame;
import java.util.TreeMap;

/**
 * Test Harness created to carry out stress testing on the system
 * @author Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class StressTest 
{

    /**
     * Main Method for stress tesing harness
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.NoSuchFieldException
     */
    public static void main(String[] args) throws InterruptedException, IOException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException
    {
        int totalPortals = 100;
        int agentsPerPortal = 5;
        int totalMessages = 100;
        int agentsToRemove = 100;
        int portalsToRemove = 10;
        Router r0 = Router.createNew("r0");
        int portalCount = 0;
        int userCount = 0;
        List<Portal> portalList = new ArrayList<>();
        List<UserAgent> agentList = new ArrayList<>();
        List<String> deletedAgents = new ArrayList<>();
        List<String> deletedPortals = new ArrayList<>();
        
        for(int i = 0; i < totalPortals; i++)
        {
            Portal newPortal = new Portal("p" + portalCount++, r0);
            portalList.add(newPortal);
            for(int j = 0; j < agentsPerPortal; j++)
            {
                UserAgent newUser = new UserAgent("u" + userCount++, newPortal);
                agentList.add(newUser);
            }
        }
        Random r = new Random();
        
        for(int i = 0; i < totalMessages; i++)
        {
            int randSender = r.nextInt(userCount);
            int randReceiver = r.nextInt(userCount);
            agentList.get(randSender).sendMessage("u" + randReceiver, "HI THERE, u" + randReceiver + ", THIS IS u" + randSender + "!");
        }
        
        for(int i = 0; i < totalMessages; i++)
        {
            agentList.get(i).sendMessage("u" + (totalMessages - i), "HI THERE, u" + (totalMessages - i) + ", THIS IS u" + i + "!");
        }
        Thread.sleep(10000);
        for(int i = 0; i < agentsToRemove; i++)
        {
            int randAgent = r.nextInt(userCount);
            deletedAgents.add("u" + randAgent);
            r0.removeAgent("u" + randAgent);
            agentList.remove(randAgent);
            userCount--;
        }
        Thread.sleep(10000);
        for(int i = 0; i < portalsToRemove; i++)
        {
            int randPortal = r.nextInt(portalCount);
            deletedPortals.add("p" + randPortal);
            /*
            for(String user : portalList.get(randPortal).getAllUsers())
            {
                agentList.remove(Integer.parseInt(user.substring(1)));
                userCount--;
                deletedAgents.add(user);
            }
            */
            r0.removeAgent("p" + randPortal);
            portalList.remove(randPortal);
            portalCount--;
        }
        Thread.sleep(10000);
        System.out.println("");
        System.out.println("Router routing table:");
        for(Map.Entry<String, MetaAgent> set : r0.getRoutingTable().entrySet())
        {
            if(deletedAgents.contains(set.getKey()) || deletedPortals.contains(set.getKey()))
                System.out.println("AGENT " + set.getKey() + " SHOULD BE DELETED");
            System.out.println(set.getKey() + " " + set.getValue().getHandle());
        }

    }
    
}
