package GUI;

/**
 * Class of Table Manager allows creation of table objects
 * @author Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class TableManager 
{
    //Instance variable
    RouterController gui;
    
    /**
     * Constructor for Table Manager 
     * @param rc Router Controller object
     */
    public TableManager(RouterController rc)
    {
        gui = rc;
    }
    
    /**
     * Method to create a new row
     * @param time time of the update
     * @param session number
     * @param id identification
     * @param from name
     * @param to name 
     * @param msg message data 
     */
    public void newRow(String time, int session, int id, String from, String to, String msg)
    {
        gui.updateTable(time, session, id, from, to, msg);
    }
    
    /**
     * Method to create a new line
     * @param line line information
     */
    public void newLine(String line)
    {
        gui.updateTab(line);
    }
    
    /**
     * Method to create a new line
     * @param line line information
     * @param time time added
     */
    public void newLine(String line, String time)
    {
        gui.updateTab(line);
    }
    
    /**
     * Method for display when a new router is added 
     * @param handle name of the router
     */
    public void newRouter(String handle)
    {
        gui.addConnection(handle);
    }
}