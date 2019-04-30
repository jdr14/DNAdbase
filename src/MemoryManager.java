import java.io.*;
import java.util.*;

/**
 * Class to keep track of the empty spaces in the binary (hash file)
 * @author jovany cabrera jovanyc4
 * @author joey destin rodgers jdr14
 * @version 1.3.4
 *
 */
public class MemoryManager
{
	/**
	 * Create nodes to track the head, tail, and the current node
	 */
    private Node<Pair<Long, Long>> head;
    
    /**
     * Current to tack the current node
     */
    private Node<Pair<Long, Long>> curr;
    
    /**
     * 
     */
    private Node<Pair<Long, Long>> tail;
    
    /**
     * variable to track the size of the linked list
     */
    private long listSize;
    
    /**
     * File which the memory manager will be tracking
     */
    private RandomAccessFile memFile;
    
    /**
     * Project spec declares offset should be stored as an int
     * Use this private variable to help with casting from long to int
     */
    private long intMaxAsLong = (long)Integer.MAX_VALUE;
    

    
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
		curr = tail = new Node();
		head = new Node();
	}

    // Remove all elements and reset the linked list
    public void clear() 
    {
        curr = tail = new Node(); // Create trailer
        head = new Node(tail);        // Create header
        listSize = 0;
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
		
		// delete sequence
		
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
    	byte[] arrayofSeq = stringToByte(seq);
    	
    	// variable used to save position of seqId in file
    	long posOfseqId = 0;
    	// variable used to save position of seq in file
    	long posOfSeq = 0;
    	
        // case where space was found in list for the seqID
    	// parameter of this function might be string
    	if (spaceInList(arrayofId))
    	{
    		// if space available, place in list and save position
    		posOfseqId = enplaceInList(arrayofId);
    	}
    	else
    	{
    		// else put seqId at the end of file
    		try {
				posOfseqId = placeAtEndOfFile(arrayofId);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
        
    	// creation of the first entry in the pair to be returned
    	// second parameter might have to be length of string
    	Pair<Long, Long> bigKey = 
    			new Pair<Long, Long>(posOfseqId, (long)arrayofId.length);
    	
    	// case where space was found in list for the seq
    	// parameter of this function might be string
    	if (spaceInList(arrayofSeq))
    	{
    		// if space available, place in list and save position
    		posOfSeq = enplaceInList(arrayofSeq);
    	}
    	else
    	{
    		// else put seq at end of file
    		try {
				posOfSeq = placeAtEndOfFile(arrayofSeq);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	// creation of the second entry in pair to be returned
    	// second parameter might have to be length of string
    	Pair<Long, Long> bigValue = 
    			new Pair<Long, Long>(posOfSeq, (long)arrayofSeq.length);
    	
    	// set result to have the correct values calculated
    	result.setKey(bigKey);
    	result.setValue(bigValue);
    	
    	return result;
    }
    
    /**
     * 
     * @param convertThis
     * @return the byte array of the string given
     */
    private byte[] stringToByte(String convertThis)
    {
    	int length = (int) Math.ceil((double)convertThis.length());
    	
    	byte[] result = new byte[length];
    	
    	// a lot more code is needed here!
    	
    	return result;
    }
    
    /**
     * Function used to check if there is space in the
     * linked list for byte array passed in
     * @param insertThis
     * @return
     */
    private boolean spaceInList(byte[] insertThis)
    {
    	return false;
    }
    
    /**
     * Function that places the seq/seqID into its position
     * @param insertThis
     * @return the position in the file of the seq/seqId
     */
    private long enplaceInList(byte[] insertThis)
    {
    	return 0;
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
    	// Get the pointer to file offset from the beginning (in bytes)
		long posInFile = memFile.getFilePointer();
		
	    // First insert the sequence ID.  
		int seqIdOffset = (int)posInFile;
		
		// Write the sequence ID to the file
		writeToFile(insertThis);
		
    	return posInFile;
    }
    
    // Append "it" to list
    private boolean append(Node<Pair<Long, Long>> memoryHandles) 
    {
        tail.setNext(new Node(null));
        tail.setItem(memoryHandles.item());
        tail = tail.next();
        listSize++;
        return true;
    }

    /**
     * 
     * @param seqToRemove
     * Remove and return current element
     */
    public boolean remove (Pair<Pair<Long, Long>,
    		Pair<Long, Long>> hashEntry) 
    {
    	
		// if correct, add entries to linked list of free spaces
		// make sure that this is correct but it makes sense
    	curr.setNext(new Node(hashEntry.getKey()));
    	curr = curr.next();
    	curr.setNext(new Node(hashEntry.getValue()));
    	listSize += 2;
    	
    	
    	// print out sequence
    	// these next couple of lines need to get fixed
    	// need to decode the bytes grabbed from the file
    	// need to double check if these values are what I think
    	// this double checking needs to be done in all: insert,
    	// search, and remove
    	long seqPos1 = hashEntry.getKey().getValue();
    	String seqToRemove = getDataFromFile(seqPos1);
    	System.out.println("Sequence Removed " + seqToRemove + ": ");
    	long seqPos = hashEntry.getValue().getValue();
    	String seqFromFile = getDataFromFile(seqPos);
    	System.out.println(seqFromFile);
        return true;
    }
    
    /**
     * 
     * @param filePosition
     */
    private String getDataFromFile(long filePosition)
    {
    	String foundLine = "";
    	try 
    	{
			memFile.seek(filePosition);
			foundLine = memFile.readLine();
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
    	String fromFile = getDataFromFile((long) seqIdPos);
    	
    	
    	// case where seqID does not match, need to find correct value
    	return fromFile.equalsIgnoreCase(seqId);

    }
    
    /**
     * 
     * @param seqPos
     */
    public void printSeq(long seqPos)
    {
    	String seqFromFile = getDataFromFile((long) seqPos);
    	System.out.println("Sequence Found: " + seqFromFile);
    }
    

    public void moveToStart() { curr = head.next(); } // Set curr at list start
    public void moveToEnd() { curr = tail; }          // Set curr at list end

    // Move curr one step left; no change if now at front
    public void prev() 
    {
      if (head.next() == curr) return; // No previous element
      Node<Pair<Long, Long>> temp = head;
      // March down list until we find the previous element
      while (temp.next() != curr) temp = temp.next();
      curr = temp;
    }

    // Move curr one step right; no change if now at end
    public void next() 
    { 
    	if (curr != tail) 
    	{
    		curr = curr.next(); 
    	}
    }

    /**
     * Return list length
     * @return length of the list as an integer
     */
    public long length() 
    { 
    	return listSize; 
    } 


    /**
     * Return the position of the current element
     * @return position of current element as an integer
     */
    public int currPos() 
    {
        Node<Pair<Long, Long>> temp = head.next();
        int i;
        for (i=0; curr != temp; i++)
        {
        	temp = temp.next();
        }
        return i;
    }
    
    /**
     * Move down list to "pos" position
     * @param pos (position) to move 
     * @return boolean value based on move success
     */
    public boolean moveToPos(int pos) 
    {
        if ((pos < 0) || (pos > listSize)) 
        {
    	    return false;
        }
        curr = head.next();
        for (int i=0; i<pos; i++) 
        {
        	curr = curr.next();
        }
        return true;
    }

    /**
     * Return true if current position is at end of the list
     * @return boolean value based on if the curr node is equal to the tail
     */
    public boolean isAtEnd() 
    { 
    	return curr == tail; 
    }

    /**
     * Return current element value. Note that null gets returned if curr is at the tail
     * @return item of the current node
     */
    public Object getValue() 
    { 
    	return curr.item(); 
    }

    /**
     * Check if the list is empty
     * @return true if the list is empty and false otherwise
     */
    public boolean isEmpty() 
    { 
    	return listSize == 0; 
    }
    
}
