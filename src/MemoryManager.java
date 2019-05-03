import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Class to keep track of the empty spaces in the binary (hash file)
 * @author jovany cabrera jovanyc4
 * @author joey destin rodgers jdr14
 * @version 1.3.4
 *
 */
public class MemoryManager implements Comparator<Pair<Long, Long>>
{

    /**
     * variable to track the size of the linked list
     */
    private long listSize;
    
    /**
     * File which the memory manager will be tracking
     */
    private RandomAccessFile memFile;
    
    
    private LinkedList<Pair<Long, Long>> freeBlocks;

    /**
     * Default constructor to be used for comparing the offsets later
     */
    public MemoryManager() {}
    
    /**
     * Constructor creates 
     * @param hashFileName
     */
	public MemoryManager(String memoryFileName) 
	{
        // Initialize the memory file for external disk storage
		try
		{
			// File is the stream used to create the hash (binary) file...
			memFile = new RandomAccessFile(new File(memoryFileName), "rw");
		}
		catch (FileNotFoundException e)
		{
			System.err.println("Error with creating the memory file " 
		        + e.getMessage());
		}
		
		// Initialize the linked list size as well as head, current, and tail
		listSize = 0;
		freeBlocks = new LinkedList<Pair<Long, Long>>();
	}
	
	/**
	 * 
	 * @return
	 */
	public LinkedList<Pair<Long, Long>> getList()
	{
		return freeBlocks;
	}
    
	/**
	 * 
	 * @param sequence of type string
	 * @throws IOException
	 */
	public void writeToFile(byte [] s) throws IOException
	{
		// write string to hash file as bytes
		memFile.write(s);
	}
	
	/**
	 * 
	 * @param seqPosition
	 * @throws IOException
	 */
	public void fileRemove(long seqPosition) throws IOException
	{
		// move file pointer to position of sequence to be deleted
		memFile.seek(seqPosition);
	}
    
	/**
	 * Insert "it" at current position
	 * @param seqId
	 * @param seq
	 * @return a pair of pairs to be placed in hashTable
	 */
    public Pair<Pair<Long, Long>, Pair<Long, Long>> 
        insert(String seqId, String seq)
    {
    	// pair to be returned at end of function
    	Pair<Pair<Long, Long>, Pair<Long, Long>> result =
    			new Pair<Pair<Long, Long>, Pair<Long, Long>>();
    	
    	// convert both seqId and seq into byte arrays in
    	// accordance with the project sheet
    	byte[] arrayofId = stringToByte(seqId);
    	
    	// case where seqId is 'AAAA' or 'AAAAAAAA'
    	if (arrayofId.length == 0)
    	{
    		arrayofId = new byte[seqId.length() / 4];
    	}
    	byte[] arrayofSeq = stringToByte(seq);
    	
    	// case where seq is 'AAAA' or 'AAAAAAAA'
    	if (arrayofSeq.length == 0)
    	{
    		arrayofSeq = new byte[seq.length() / 4];
    	}
    	
    	// variable used to save position of seqId in file
    	long posOfseqId = 0;
    	// variable used to save position of seq in file
    	long posOfSeq = 0;
    	
        // case where space was found in list for the seqID
    	// parameter of this function might be string
    	if (spaceInList(arrayofId))
    	{
    		// if space available, place in list and save position
    		try 
    		{
    			posOfseqId = emplaceInList(arrayofSeq);
    		}
    		catch (IOException e)
    		{
    			System.err.println(e);
    			e.printStackTrace();
    		}
    	}
    	else
    	{
    		// else put seqId at the end of file
    		try 
    		{
				posOfseqId = placeAtEndOfFile(arrayofId);
			} 
    		catch (IOException e) 
    		{
				e.printStackTrace();
			}
    	}
        
    	// creation of the first entry in the pair to be returned
    	// second parameter might have to be length of string
    	Pair<Long, Long> bigKey = 
    			new Pair<Long, Long>(posOfseqId, (long)seqId.length());
    	
    	// case where space was found in list for the seq
    	// parameter of this function might be string
    	if (spaceInList(arrayofSeq))
    	{
    		// if space available, place in list and save position
    		try 
    		{
    			posOfSeq = emplaceInList(arrayofSeq);
    		}
    		catch (IOException e)
    		{
    			System.err.println(e);
    			e.printStackTrace();
    		}
    	}
    	else
    	{
    		// else put seq at end of file
    		try 
    		{
				posOfSeq = placeAtEndOfFile(arrayofSeq);
			} 
    		catch (IOException e) 
    		{
				e.printStackTrace();
			}
    	}
    	// creation of the second entry in pair to be returned
    	// second parameter might have to be length of string
    	Pair<Long, Long> bigValue = 
    			new Pair<Long, Long>(posOfSeq, (long)seq.length());
    	
    	// set result to have the correct values calculated
    	result.setKey(bigKey);
    	result.setValue(bigValue);
    	
    	return result;
    }
    
    /**
     * Method for handling the conversion of string to a byte array as per the
     * project 4 specification
     * @param convertThis The name of the string to be converted into a 
     * byte array
     * @return Return byte array equivalent of the string passed in
     */
    public byte[] stringToByte(String convertThis)
    {
        // Create a new for the conversion
    	BitSet b1 = new BitSet(convertThis.length() * 2);
    	
    	// Check each char in the seqID and convert to corresponding bits
    	for (int i = 0; i < convertThis.length(); i++)
    	{
    		char currChar = convertThis.charAt(i);
    		if (currChar == 'A')  // 00
    		{
    			b1.clear(2 * i);
    			b1.clear((2 * i) + 1);
    		}
    		else if (currChar == 'C')  // 01
    		{
    		    b1.set((2 * i) + 1);
    		}
    		else if (currChar == 'G')  // 10
    		{
    		    b1.set(2 * i);
    		}
    		else if (currChar == 'T')  // 11
    		{
    		    b1.set(2 * i);
    		    b1.set((2 * i) + 1);
    		}
    	}
    	
    	// Bit set is now correct, so return the byte array.
    	// * Warning: returning as a byte array will zero fill to the closest
    	// multiple of four.  So if there are 7 characters in the string, a 
    	// byte array of size 2 (2 bits per char) will be returned with the 
    	// last 2 bits set as 0s.  This must be handled when converting byte
    	// array back into a string
    	if (b1.length() == 0)
    	{
    		if (convertThis.length() % 4 != 0)
    		{
    			b1.set((convertThis.length() * 2) + 1);	
    		}
    	}
    	return b1.toByteArray();
    }
    
    /**
     * 
     * @param convertThis The byte array needing to be converted back into
     * its corresponding string.  Warning, if the original string sequence
     * length is not a multiple of four, there will be 0 fill for between 
     * 2 to 6 bits long. 
     * @param strLength Needed to ignore the potential zero fill at the end
     * of the byte array passed in
     * @return the final conerted string built
     */
    public String byteToString(byte[] convertThis, int strLength)
    {
    	// Temp bitset to extract the data from the byte array passed in
    	BitSet b1 = BitSet.valueOf(convertThis);
    	
    	// Build the result as a string to be returned at method conclusion
    	String result = "";
    	for (int i = 0; i < strLength; i++)
    	{
    		if (i <= strLength)
    		{
    			if (b1.get(i * 2) == false && b1.get(i * 2 + 1) == false)
    			{
    			    result += 'A';  // 00 (A)
    			}
    			else if (b1.get(i * 2) == false && b1.get(i * 2 + 1) == true)
    			{
    			    result += 'C';  // 01 (C)
    			}
    			else if (b1.get(i * 2) == true && b1.get(i * 2 + 1) == false)
    			{
    			    result += 'G';  // 10 (G)
    			}
    			else if (b1.get(i * 2) == true && b1.get(i * 2 + 1) == true)
    			{
    			    result += 'T';  // 11 (T)
    			}
    		}
    	}
    	return result;  // Finally, return the complete string
    }
    
    /**
     * Function used to check if there is space in the
     * linked list for byte array passed in
     * @param insertThis
     * @return
     */
    private boolean spaceInList(byte[] insertThis)
    {
    	/*
    	 Node should have pointer to file offset position as well as 
    	 length of empty space that hole can fill
    	 
    	 if (insertThis.length <= 
    	 */
    	for (int i = 0; i < freeBlocks.size(); i++)
    	{
    		if (insertThis.length <= freeBlocks.get(i).getValue())
    		{
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * Function that places the seq/seqID into its position
     * @param insertThis
     * @return the position in the file of the seq/seqId
     */
    private long emplaceInList(byte[] insertThis) throws IOException
    {
    	for (int i = 0; i < freeBlocks.size(); i++)
    	{
    		if (insertThis.length == freeBlocks.get(i).getValue())
    		{
    		    long fileOffset = freeBlocks.get(i).getKey();
    		    long origFilePointer = memFile.getFilePointer(); 
    		    
    		    memFile.seek(fileOffset);
    		    writeToFile(insertThis);
    		    
    		    // return file pointer to original state
    		    memFile.seek(origFilePointer);  
    		    
    		    // Free space no longer exists
    		    freeBlocks.remove(i);
    		    
    		    return fileOffset;
    		}
    		
    		// Not the entire free block was taken up, decrement appropriately
    		if (insertThis.length < freeBlocks.get(i).getValue())
    		{
    		    long fileOffset = freeBlocks.get(i).getKey();
    		    long origFilePointer = memFile.getFilePointer(); 
    		    
    		    memFile.seek(fileOffset);
    		    writeToFile(insertThis);
    		    
    		    long newFileOffset = memFile.getFilePointer();
    		    
    		    // return file pointer to original state
    		    memFile.seek(origFilePointer);  
    		    
    		    // Create an updated free space node to replace the current 
    			Pair<Long, Long> updatedFreeSpaceNode = 
    					new Pair<Long, Long>(newFileOffset, 
    							freeBlocks.get(i).getValue() - 
    							insertThis.length);
    			freeBlocks.set(i, updatedFreeSpaceNode);
    		}
    	}
    	return -1;  // Error has occurred
    }
    
    /**
     * Function called for when no space in list found,
     * just put at end of file
     * @param insertThis
     * @return the position in the file of the seq/seqId
     * @throws IOException 
     */
    private long placeAtEndOfFile(byte[] insertThis) throws IOException
    {
    	// Get the pointer to file offset whenever its at the end of file
		long posInFile = memFile.getFilePointer();
		
		// Write the sequence ID to the file
		writeToFile(insertThis);
		
    	return posInFile;
    }
    
    /**
     * Overload the comparator class for custom Pair types.  Comparing the
     * offsets of the pair objects will aid in sorting the linked list
     */
    @Override
    public int compare(Pair<Long, Long> p1, Pair<Long, Long> p2)
    {
    	return (int)(p1.getKey() - p2.getKey());
    }

    /**
     * 
     * @param seqToRemove
     * Remove and return current element
     */
    public boolean remove (Pair<Pair<Long, Long>,
    		Pair<Long, Long>> hashEntry, int sLength) 
    {
    	
		// if correct, add entries to linked list of free spaces
		// make sure that this is correct but it makes sense
    	
    	long seqIdByteLength = seqToByteLength(sLength);
    	//System.out.println("SEQ BYTE LENGTH = " + seqByteLength);
    	
    	long seqIdOffset = hashEntry.getKey().getKey();
    	long seqOffset = hashEntry.getValue().getKey();
    	
    	// If the 2 free space nodes will be adjacent, combine them
    	if (seqIdOffset + seqIdByteLength == seqOffset)
    	{
    		// Get length of the combined string of seq Id + sequence
    	    long combinedStrLength = hashEntry.getKey().getValue() 
    	    		+ hashEntry.getValue().getValue();
    	    
    	    // Convert the combined string length to byte length
    	    /*
    	     *  TODO: Review that the string length equivalent of free space
    	     *  should be stored instead of the byte length??
    	     */
    	    // long combinedByteLength = seqToByteLength(combinedStrLength);
    		Pair<Long, Long> combinedNode = new Pair<Long, Long>(seqIdOffset,
    				combinedStrLength);
    		
    		// Add the combined free space node to the linked list
    		freeBlocks.add(combinedNode);
    		
    		listSize++;  // Increment list size accordingly
    	}
    	else
    	{
    		// Otherwise, add both free space nodes as 2 seperate entities
    		freeBlocks.add(hashEntry.getKey());
    		freeBlocks.add(hashEntry.getValue());
    		listSize += 2;
    	}
    	
    	// make sure the list stays sorted in ascending order
    	// Uses the overloaded comparator to compare the offsets
    	Collections.sort(freeBlocks, new MemoryManager());
    	
    	// Also ensure all adjacent free nodes are merged properly
    	mergeAdjacentFreeNodes();
    	
//    	freeBlocks.add(hashEntry.getKey());
//    	System.out.println("hash key offset = " + hashEntry.getKey().getKey());
//    	System.out.println("hash key length = " + hashEntry.getKey().getValue());
//    	curr.setNext(new Node(hashEntry.getKey()));
//    	curr = curr.next();
    	
//    	freeBlocks.add(hashEntry.getValue());
//    	System.out.println("hash value offset = " + hashEntry.getValue().getKey());
//    	System.out.println("hash value length = " + hashEntry.getValue().getValue());
//    	curr.setNext(new Node(hashEntry.getValue()));
//    	listSize += 2;
    	
    	// print out sequence
    	long seqPos1 = hashEntry.getKey().getKey();
    	String seqToRemove = getDataFromFile(seqPos1, sLength);
    	System.out.println("Sequence Removed " + seqToRemove + ":");
    	long seqPos = hashEntry.getValue().getKey();
    	long seqLength = hashEntry.getValue().getValue();
    	String seqFromFile = getDataFromFile(seqPos, (int)seqLength);
    	System.out.println(seqFromFile);
        return true;
    }
    
    /**
     * Iterates through the internal linked list and merges free space
     * nodes that are next to each other.  The idea is that two adjacent
     * nodes merge together to become a bigger free space node
     */
    private void mergeAdjacentFreeNodes()
    {
    	if (freeBlocks.size() <= 1)
    	{
    		// Merging not needed in the case of 0 or 1 node
    		return;
    	}
    	
    	//int initialListSize = freeBlocks.size();
    	
    	for (int i = 1; i < freeBlocks.size(); i++)
    	{
    		Pair<Long, Long> prevNode = freeBlocks.get(i - 1);
    		Pair<Long, Long> currNode = freeBlocks.get(i);
    		
    		// Check if the offset + byte length = the next offset
    		if (currNode.getKey() == prevNode.getKey() 
    				+ seqToByteLength(prevNode.getValue()))
    		{
    			// Merge is necessary
    			Pair<Long, Long> mergeNode = 
    					new Pair<Long, Long>(prevNode.getKey(), 
    							prevNode.getValue() + currNode.getValue());
    			freeBlocks.set(i - 1, mergeNode);
    			freeBlocks.remove(i);
    			i = 0;
    		}
    	}
    }
    
    /**
     * 
     * @param s sequence or sequence ID as string
     * @return 
     */
    private Long seqToByteLength(long seqLength)
    {
    	if (seqLength % 4 == 0)
    	{
    		return (long)(seqLength / 4);
    	}
    	else
    	{
    		return (long)((seqLength / 4) + 1);
    	}
    }
    
    /**
     * 
     * @param filePosition
     */
    public String getDataFromFile(long filePosition, int stringLength)
    {
    	String foundLine = "";
    	byte[] tempArray = new byte [(stringLength / 4) + 1];
    	try 
    	{
			memFile.seek(filePosition);
			memFile.read(tempArray);
			foundLine = byteToString(tempArray, stringLength);
			return foundLine;
		} 
    	catch (IOException e) 
    	{
			System.err.println("Error: " + e.getLocalizedMessage());
		}
    	return foundLine;
    }
    
    /**
     * 
     * @param seqToSearch
     */
    public boolean search(String seqId, long seqIdPos)
    {	
        // check if sequence ID is same
    	String fromFile = getDataFromFile((long) seqIdPos, seqId.length());
    	
    	// case where seqID does not match, need to find correct value
    	return fromFile.equalsIgnoreCase(seqId);
    }
    
    /**
     * 
     * @param seqPos
     */
    public void printSeq(long seqPos, int length)
    {
    	String seqFromFile = getDataFromFile((long) seqPos, length);
    	System.out.println("Sequence Found: " + seqFromFile);
    }
    
}