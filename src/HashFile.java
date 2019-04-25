import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Creates the hash (binary) file to store the sequences of different lengths
 * Also manages the hash table as an array of pointers to each sequence
 * stored on the hash file (on disk)
 * @author Joey Rodgers jdr14
 * @author Jovany Cabrera jovanyc4
 * @version 1.0.0
 */
public class HashFile {

	/**
	 * Hash file object
	 */
	private RandomAccessFile hashFile;
	
	/**
	 * Default constructor
	 * @param hashFileName of type string
	 */
	public HashFile(String hashFileName) 
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

	/**
	 * 
	 * @param sequence of type string
	 * @throws IOException
	 */
	public void fileInsert(String sequence) throws IOException
	{
		// get current hash file location
		long currentPointer = hashFile.getFilePointer();
		
		// write string to hash file as bytes
		hashFile.write(sequence.getBytes());
	}
	
	/**
	 * 
	 * @param seqPosition
	 * @throws IOException
	 */
	public void fileRemove(long seqPosition) throws IOException
	{
		// move file pointer to position of sequence to be deleted
		hashFile.seek(seqPosition);
		
		// delete sequence
		
	}
}
