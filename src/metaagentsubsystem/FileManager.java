package metaagentsubsystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class FileManager extending LinkedBlockingQueue
 * @author Group B, Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class FileManager extends LinkedBlockingQueue<String>
{
    //Instance Variables 
    private PrintWriter out; 
    private ReentrantLock lock;
    
    /**
     * Constructor for the FileManager
     * @param fileName is passed in and checked then created if none exists
     */
    public FileManager(String fileName)
    {
        try {
            File f = new File(fileName);
            if(!f.exists())
            {
                f.createNewFile();
            }
           
            out = new PrintWriter(new FileWriter(fileName,true), true);
        } 
        catch (FileNotFoundException ex) 
        {
            System.out.println("File does not exist");
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        lock = new ReentrantLock();
    }
    
    /**
     * writeMsg used to output a message passed in
     * @param msg is passed in the method and displayed
     */
    public void writeMsg(String msg)
    {
        lock.lock();
        out.println(msg);
        lock.unlock();
    }
}
