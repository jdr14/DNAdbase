import java.io.*;

public class MemoryManager 
{
	/**
	 * Hash file object
	 */
	private RandomAccessFile hashFile;

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
