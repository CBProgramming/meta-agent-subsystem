package metaagentsubsystem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Group B, Carter Ridgeway (v8265314), Chris Burrell (t7145969), Cristian Tudor (v8002382), James Carney (m2023967)
 */
public class FileManagerTest 
{
    private FileManager fm;
    private String fileName;
    
    /**
     * Method used to call the test
     */
    public FileManagerTest() {
    }
    
    /**
     * Initialise variables before tests run
     */   
    @Before
    public void setUp() 
    {
        fileName = "testFile.txt";
        resetFile(fileName);
        fm = new FileManager(fileName);
    }
       
    /**
     * Resets log files used by file managers
     * @param fileName name of file
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
    
    /**
     * Reads file and returns string contents
     * @param fileName name of file
     * @return file contents as string
     */   
    public static String readFile(String fileName) 
    {
        File file = new File(fileName);
        String fileString = "";
        try 
        {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) 
            {
                fileString += scanner.nextLine();
            }
        } catch (FileNotFoundException e) 
        {
            System.err.println("Error attempting to read file");
        }
        return fileString;
    }

    /**
     * Test of writeMsg method, of class FileManager.
     */
    @Test
    public void testWriteMsg() 
    {
        String expected = "test file write";
        fm.writeMsg("test file write");
        assertEquals(expected,readFile(fileName));
    }

    /**
     * Test of writeMsg method, of class FileManager.
     */
    @Test
    public void testWriteMsgTwice() 
    {
        String expected = "test file write" + "test file write";
        fm.writeMsg("test file write");
        fm.writeMsg("test file write");
        assertEquals(expected,readFile(fileName));
    }  
    
    /**
     * Test of writeMsg method, of class FileManager.
     */
    @Test
    public void testWriteMsgNull() 
    {
        String expected = "null";
        fm.writeMsg(null);
        assertEquals(expected,readFile(fileName));
    }      
}
