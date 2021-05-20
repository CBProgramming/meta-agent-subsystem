/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metaagentsubsystem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Test Harness Class for testing Monitor Network
 * @author Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class MonitorTestHarness 
{
        /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     * @throws java.io.IOException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.NoSuchFieldException
     */
    public static void main(String[] args) throws InterruptedException, IOException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException
    {
        resetFile("system_log.txt");
        resetFile("user_log.txt");
        FileManager sysFm = new FileManager("system_log.txt");
        FileManager userFm = new FileManager("user_log.txt");
        DefaultPortalMonitor p1mon = new DefaultPortalMonitor(sysFm,userFm);
        DefaultRouterMonitor r1mon = new DefaultRouterMonitor(sysFm,userFm);
        DefaultPortalMonitor p2mon = new DefaultPortalMonitor(sysFm,userFm);
        
        Router r1 = Router.createNew("r1");
        r1.setMonitor(r1mon);
        Portal p1 = new Portal("p1", r1);
        p1.setMonitor(p1mon);
        UserAgent carter = new UserAgent("Carter", p1);     
        UserAgent chris = new UserAgent("Chris", p1);

        carter.sendMessage("Chris", "test1");
        chris.sendMessage("Carter", "Hi Carter");
        carter.sendMessage("Chris", "Hi Chris!");
        
        Portal p2 = new Portal("p2", r1);

        p2.setMonitor(p2mon);
        UserAgent james = new UserAgent("James",p2);
        james.sendMessage("Carter", "Hi Carter");
//        p2.removeAgent("r1");
        
        
        
        r1.removeAgent("p1");
        Thread.sleep(2000);
        carter.sendMessage("James", "test1");
    }
    
    /**
     * Method used to reset the file for testing
     * @param fileName the name of the file
     */
    public static void resetFile(String fileName) 
    {
        File file = new File(fileName);
        try 
        {
            final BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
            bw.write("");
            bw.close();
        } 
        catch (IOException e) 
        {
            System.err.println("Something went wrong restting the file");
        }
    }
}
