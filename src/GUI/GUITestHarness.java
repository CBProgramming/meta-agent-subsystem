package GUI;

import GUI.MsgWindow;
import GUI.RouterController;
import java.io.IOException;
import javax.swing.JFrame;
import metaagentsubsystem.Portal;
import metaagentsubsystem.UserAgent;

/**
 * GUI Test Harness
 * @author Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class GUITestHarness {

    /**
     * Main class to run the test harness
     * @param args the command line arguments
     * @throws InterruptedException needed for waiting thread
     * @throws IOException needed for error messages
     */
    public static void main(String[] args) throws InterruptedException, IOException 
    {
        
        JFrame routerControllerFrame = new JFrame("Router Controller");

        RouterController routerControllerPanel = new RouterController();
        
        
        routerControllerFrame.add(routerControllerPanel);
        
        routerControllerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        viewerFrame.setSize(1000, 1000);
        routerControllerFrame.pack();
        routerControllerFrame.setVisible(true);
        routerControllerFrame.setResizable(false);
    }
    
}
