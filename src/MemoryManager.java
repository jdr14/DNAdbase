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
     * size of hash table, used for calls to sfold
     */
    private int hashTableSize;
    
    /**
     * Private hashTable to track the memory handles
     * M1 should be the handle to sequence ID (offset and length)
     * M2 should be the handle to the actual sequence (offset and length)
     */
    private ArrayList<Pair<Pair<Integer, Integer>,
        Pair<Integer, Integer>>> hashTable;
    
    /**
     * Constructor creates 
     * @param hashFileName
     */
	public MemoryManager(String memoryFileName, long tableSize) 
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
		hashTableSize = (int) tableSize;
		hashTable = new ArrayList<Pair<Pair<Integer, Integer>,
				Pair<Integer, Integer>>>(hashTableSize);
	}
	
	/**
	 * Hashing function to compute the index (slot) of the hash table where
	 * the memory handles are to be stored.
	 * @param s of type string, seqID to be placed/looked-up
	 * @param M of type integer, size of the hash table
	 * @return slot index as a long
	 */
	private long sfold(String s, int M) 
	{
		int intLength = s.length() / 4;
		long sum = 0;
		for (int j = 0; j < intLength; j++) 
		{
		    char c[] = s.substring(j * 4, (j * 4) + 4).toCharArray();
		    long mult = 1;
		    for (int k = 0; k < c.length; k++) 
		    {
		        sum += c[k] * mult;
		        mult *= 256;
		    }
		}

		char c[] = s.substring(intLength * 4).toCharArray();
	    long mult = 1;
		for (int k = 0; k < c.length; k++) 
		{
            sum += c[k] * mult;
	        mult *= 256;
        }

		sum = (sum * sum) >> 8;
		return(Math.abs(sum) % M);
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
	public void fileInsert(String sequence) throws IOException
	{
		// get current hash file location
		long currentPointer = memFile.getFilePointer();
		
		// write string to hash file as bytes
		memFile.write(sequence.getBytes());
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
    
//    // Insert "it" at current position
//    public boolean insert(String seqId, String seq, long seqLength) 
//    		throws IOException
//    {
//    	// Case where listSize is empty means there are currently no empty
//    	// spaces/slots in the file where seqId and/or seq could go
//    	// Therefore, just insert at the end of the file.
//    	if (listSize == 0)
//    	{
//    		// Get the pointer to file offset from the beginning (in bytes)
//    		long seqIdOffset = memFile.getFilePointer();
//    		
//    		//
//    		if (seqIdOffset >= intMaxAsLong)
//    		{
//    			return false;
//    		}
//    		
//    	    // First insert the sequence ID.  
//    		
//    		Pair<Long, > m1 = new Pair();
//    	}
//    	else
//    	{
//    		/*
//    		 Collision resolution will use simple linear probing, 
//    		 with wrap-around at the bottom of the current bucket. 
//    		 For example, if a string hashes to slot 60 in the table, 
//    		 the probe sequence will be slots 61, then 62, then 63, 
//    		 which is the bottom slot of that bucket. 
//    		 The next probe will wrap to the top of the bucket, 
//    		 or slot 32, then to slot 33, and so on. 
//    		 If the bucket is completely full, then the insert request 
//    		 will be rejected. Note that if the insert fails, the 
//    		 corresponding sequence and sequenceID strings must be 
//    		 removed from the memory manager's memory pool as well.
//    		 */
//    	}
//    		
//    	/*
//        curr.setNext(new Node(curr.item(), curr.next()));
//        curr.setItem(it);
//        if (tail == curr) tail = curr.next();  // New tail
//        listSize++;
//        */
//    	return true;
//    }
    
    // Append "it" to list
    private boolean append(Node<Pair<Long, Long>> memoryHandles) 
    {
        tail.setNext(new Node(null));
        tail.setItem(memoryHandles.item());
        tail = tail.next();
        listSize++;
        return true;
    }

    // Remove and return current element
    public void remove (String seqToRemove) 
    {
    	// get positioning using the sfold function
    	long hashPosition = sfold(seqToRemove, hashTableSize);
    	
    	// get values at the position in hash table
    	Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> currPosition =
    			hashTable.get((int) hashPosition);
    	
    	// check if there is a value at that position
    	
    	
    	// check if value is correct value
    	
    	// if correct, remove and add entries to list
    	
    	// else find correct slot 
        Pair<Long, Long> it = curr.item();  // Remember value
        curr.setItem(curr.next().item());  // Pull forward the next element
        if (curr.next() == tail) 
        {
    	    tail = curr;   // Removed last, move tail
        }
        curr.setNext(curr.next().next());       // Point around unneeded link
        listSize--;                             // Decrement element count
    }

    public void moveToStart() { curr = head.next(); } // Set curr at list start
    public void moveToEnd() { curr = tail; }          // Set curr at list end

    // Move curr one step left; no change if now at front
    public void prev() {
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
