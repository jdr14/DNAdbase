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
	public DnaParse(String commandFileName, String hashFileArg) 
	{
		super(commandFileName);
		MemoryManager mem
	}

	public Boolean parseMain()
	{
		File commandFile = new File(this.getFileName());
		
        List<Pair<String, String>> parsedList = new ArrayList<>();
        
        // Try/Catch block to account for case if file is not found
        try
        {
            Scanner inFileStream = new Scanner(parseFile);
            
            // Iterate through the entire file
            while (inFileStream.hasNextLine())
            {
                // currentLine is defined to be the next line of the file
            	// with the leading and trailing whitespace eliminated
                String currentLine = inFileStream.nextLine().trim();
                if (!lineIsEmpty(currentLine))
                {
                    List<String> listedLine = lineAsList(currentLine);
                    
                    if (listedLine.size() == 2)
                    {
                        parsedList.add(new 
                                Pair<String, String>(listedLine.get(0), 
                                listedLine.get(1)));
                    }
                    else if (listedLine.size() == 1)
                    {
                        parsedList.add(new 
                                Pair<String, String>(listedLine.get(0), ""));
                    }
                }
            }
            
            // Close the file stream
            inFileStream.close();
            
            return parsedList;
        }
        catch (FileNotFoundException err)
        {
            // Print a custom error along with the stack trace
            System.out.println("ERR" + this.getFileName() + " not found");
            err.printStackTrace();
        }
        return true;  // successful run of parse
	}
}
