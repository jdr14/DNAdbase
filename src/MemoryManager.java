import java.io.*;

/**
 * 
 * @author jovany cabrera jovanyc4
 * @author joey destin rodgers jdr14
 * @version 1.3.4
 *
 */
public class MemoryManager 
{
	/**
	 * Hash file object
	 */
	private RandomAccessFile hashFile;
    
	/**
	 * Default constructor
	 * @param hashFileName of type string
	 */
	public MemoryManager(String hashFileName) 
	{
		try
		{
			// File is the stream used to create the hash (binary) file...
			hashFile = new RandomAccessFile(new File(hashFileName), "rw");
		}
		catch (FileNotFoundException e)
		{
			System.err.println("Error with creating the hash file " 
		        + e.getMessage());
		}
	}
	    
}
