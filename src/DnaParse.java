import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Extends abstract class Parse and provides parsing functionality 
 * specific to the tasks outlined by the DNAdbase project (project 4)
 * @author Joey Rodgers: jdr14
 * @author Jovany Cabrera: jovanyc4
 * @version 1.0.0
 */
public class DnaParse extends Parse
{
	
    /**
     * Constructor which calls parent (Parse) constructor internally
     * @param fileName (file name of a file that exists passed in as a string)
     */
	public DnaParse(String commandFileName, 
			String hashFileArg, long hashTableSize, String memoryFileName) 
	{
		super(commandFileName);
		MemoryManager memManager = new MemoryManager();
	}

	public Boolean parseMain()
	{
		File commandFile = new File(this.getFileName());
		
        //List<Pair<String, String>> parsedList = new ArrayList<>();
        
        // Try/Catch block to account for case if file is not found
        try
        {
            Scanner inFileStream = new Scanner(commandFile);
            
            // Iterate through the entire file
            while (inFileStream.hasNextLine())
            {
                // currentLine is defined to be the next line of the file
            	// with the leading and trailing whitespace eliminated
                String currentLine = inFileStream.nextLine().trim();
                if (!lineIsEmpty(currentLine))
                {
                    List<String> listedLine = lineAsList(currentLine);
                    
                    // Case insert
                    if (listedLine.size() == 3)
                    {
                        if (listedLine.get(0).equalsIgnoreCase("insert"))
                        {
                        	String seqId = listedLine.get(1);
                        	
                        	// Project 4 instructions specified not to worry 
                        	// about accounting for syntax errors, so it is 
                        	// safe to get the next line which is the sequence
                        	long sequenceLength = 
                        			Long.parseLong(listedLine.get(2));
                            List<String> nextLineAsList = 
                            		lineAsList(inFileStream.nextLine().trim());
                        	String sequence = nextLineAsList.get(0); 
                        	
                        	// Offload work to helper method
                        	handleInsert(seqId, sequence, sequenceLength);
                        }
                    }
                    // Case remove
                    else if (listedLine.size() == 2)
                    {
                        parsedList.add(new 
                                Pair<String, String>(listedLine.get(0), ""));
                    }
                }
            }
            
            // Close the file stream
            inFileStream.close();
        }
        catch (FileNotFoundException err)
        {
            // Print a custom error along with the stack trace
            System.out.println("ERR" + this.getFileName() + " not found");
            err.printStackTrace();
        }
        return true;  // successful run of parse
	}  // End Parse main
	
	/**
	 * Handles the passing of 
	 * @param seqId
	 * @param seq
	 * @param seqLength
	 */
    private void handleInsert(String seqId, String seq, long seqLength)
    {
    	
    }
}
