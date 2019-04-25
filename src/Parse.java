import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class created to handle the parsing of input files and writing output
 * to necessary output files
 * @author Joey Rodgers jdr14
 * @author Jovany Cabrera jovanyc4
 * @version 1.0.0
 */
public abstract class Parse
{
    private String fileName;
    
    /**
     * Constructor which sets the fileName private field
     * @param fileName (name of existing file passed in as a string)
     */
    public Parse(String fileName)
    {
        this.fileName = fileName;
    }
    
    /**
     * Accessor method
     * @return the current set fileName as a string
     */
    public String getFileName()
    {
        return this.fileName;
    }
    
    /**
     * Mutator method for the purposes of changing the file name if needed 
     * for the duration of the object life
     * @param fileName (name of existing file passed in as a string)
     */
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
    
    /**
     * Simple checker function to determine if a given line is empty or not
     * @param line (the current line passed in as a String)
     * @return true if line is empty and false if not
     */
    public boolean lineIsEmpty(String line)
    {
        return (line.trim().length() <= 0);
    }
    
    /**
     * Separate the line into an array based on whitespace
     * @param line (the current line passed in as a String)
     * @return the line separated by whitespace as an array
     */
    public List<String> lineAsList(String line)
    {
        return new ArrayList<String>(Arrays.asList(line.split("\\s+")));
    }
    
    /**
     * Simple function to eliminate all preceding whitespace in a line
     * @param line 
     * @return line (the trimmed line without the preceding whitespace 
     * is returned)
     */
    public String eliminatePrecedingWhitespace(String line)
    {
        while (line.charAt(0) == ' ')
        {
            line = line.substring(1);
        }
        return line;
    }
    
    /**
     * @param listString list of strings
     * @param out is a PrintWriter for printing out
     */
    public void outputToFile(PrintWriter out, List<String> listString) 
    {
        for (int i = 0; i < listString.size(); i++) 
        {
            if (listString.get(i) != null) 
            {
                System.out.println(listString.get(i));
            }
        }
    }
}