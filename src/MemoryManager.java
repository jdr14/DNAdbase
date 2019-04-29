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
     * Define the bucket size of the hash table
     */
    private int bucketSize = 32;
    
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
	public void writeToFile(String s) throws IOException
	{
		// get current hash file location
		long currentPointer = memFile.getFilePointer();
		
		// write string to hash file as bytes
		memFile.write(s.getBytes());
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
    
    // Insert "it" at current position
    public boolean insert(String seqId, String seq, int seqLength) 
    		throws IOException
    {
		Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> memoryHandles;
		
		// Find a valid slot in the bucket
		int slotPos = collisionResolutionPolicy(seqId);
		if (slotPos < 0)
		{
			// Current bucket is full and insert cannot be completed
			return false;
		}
		
    	// Case where listSize is empty means there are currently no empty
    	// spaces/slots in the file where seqId and/or seq could go
    	// Therefore, just insert at the end of the file.
    	if (listSize == 0)
    	{
    		// Get the pointer to file offset from the beginning (in bytes)
    		long firstFilePointer = memFile.getFilePointer();
    		
    		// Check if the length after potential insertion of sequence ID
    		// is within valid range of integer type
    		if (firstFilePointer + seqId.getBytes().length > intMaxAsLong)
    		{
    			return false;
    		}
    		
    	    // First insert the sequence ID.  
    		int seqIdOffset = (int)firstFilePointer;
    		
    		// Create memory handle 1 containing seqID offset and length
    		Pair<Integer, Integer> m1 = new Pair<Integer, Integer>(
    				seqIdOffset, seqId.length());
    		
    		// Write the sequence ID to the file
    		writeToFile(seqId);
    		
            // file pointer should be updated after writing the sequence bytes
    		// to the memory file.  Convert 2nd file pointer to sequence offset
    		int seqOffset = (int)memFile.getFilePointer();
    		
    		// Create memory handle 2 containing sequence offset and length
    		Pair<Integer, Integer> m2 = new Pair<Integer, Integer>(
    				seqOffset, seqLength);
    		
    		// Write the actual sequence to the file
    		writeToFile(seq);
    		
    		// Create a the pair of memory handles
            memoryHandles = new 
    		    Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(m1, m2);
    		
    		// Finally, store the pair of memory handles pair 
    		// at the calculated slot position
    		hashTable.set(slotPos, memoryHandles);  // Should return null
    	}
    	else  // Handle for more than 1 node
    	{	
    		// Iterate through the linked list of nodes 
    		// tracking the free space in the memory file
    		curr = head; 
    		Node<Pair<Long, Long>> prev = curr;
    		
    		boolean seqIdInserted = false;
    		boolean seqInserted = false;
    		
    		Pair<Integer, Integer> m1 = null;
    		Pair<Integer, Integer> m2 = null;
    		
    		long fileOffset = prev.item().getKey();
    		long holeLength = prev.item().getValue();
    	    
    		if (seqId.length() <= holeLength)
    		{
    			m1 = new Pair<Integer, Integer>(
    					(int)fileOffset, seqId.length());
    			writeToFile(seqId);  // write to memory file
    			fileOffset = memFile.getFilePointer();
    			holeLength -= seqId.length();
    			seqIdInserted = true;
    		}
    		
    		if (seqId.length() <= holeLength)
    		{
    			m1 = new Pair<Integer, Integer>(
    					(int)fileOffset, seqId.length());
    			writeToFile(seqId);  // write to memory file
    			fileOffset = memFile.getFilePointer();
    			holeLength -= seqId.length();
    			seqIdInserted = true;
    		}
    		
    		// Update linked list accordingly
    		if (holeLength != 0)
    		{
    			curr.setItem(new Pair<Long, Long>(fileOffset, holeLength));
    		}
    		else
    		{
    			head = curr.next();  // could be null
    		}
    		
    		if (seqIdInserted && seqInserted)
    		{
    			memoryHandles = new Pair<Pair<Integer, Integer>, 
    					Pair<Integer, Integer>>(m1, m2);
    			
    			hashTable.set(slotPos, memoryHandles);
    			return true;
    		}
    		
    		prev = curr;
    	    curr = curr.next();
    		while (curr != null)  // iterate through the rest of the list
    		{
    		    fileOffset = curr.item().getKey();  // offset
    			holeLength = curr.item().getValue();  // length
    			
    			// If the hole can fit the sequence ID, insert there
    			if (!seqIdInserted && seqId.length() <= holeLength)
    			{
    				// Insert the sequence ID in the first available slot
    	    		m1 = new Pair<Integer, Integer>(
    	    				(int)fileOffset, seqId.length());
    	    		
    	    		// Write sequence ID to file
    	    		writeToFile(seqId);
    	    		
    	    		// Update the amount of space available in that free slot
    	    		fileOffset = memFile.getFilePointer();
    	    		holeLength -= seqId.length();
    	    		seqIdInserted = true;
    			}
    			
    			// Repeat similar process for sequence
    			if (!seqInserted && seqLength <= holeLength)
    			{
    				m2 = new Pair<Integer, Integer>(
    						(int)fileOffset, seqLength);
    				
    				writeToFile(seq);
    				
    				// Update accordingly
    				fileOffset = memFile.getFilePointer();
    				holeLength -= seqLength;
    				seqInserted = true;
    			}
    			
    			// Re-insert node back into the same place if the hole 
    			// hasn't been filled completely
    			if (holeLength != 0)
    			{
    				curr.setItem(new Pair<Long, Long>(fileOffset, holeLength));
    			}
    			else  // Otherwise remove the node
    			{
    				prev.setNext(curr.next());
    				curr = null;
    			}
    			
    			// If both memory handles have already been populated, break
    			if (seqIdInserted && seqInserted)
    			{
    				break;
    			}
    			
    			prev = curr;
    			curr = curr.next();
    		}
    		
    		// Handle case for when neither was inserted into a hole
    		if (!seqIdInserted)
    		{
        		// Get the pointer to file offset from the beginning (in bytes)
        		long firstFilePointer = memFile.getFilePointer();
        		
        		// Check if the length after potential insertion of sequence ID
        		// is within valid range of integer type
        		if (firstFilePointer + seqId.getBytes().length > intMaxAsLong)
        		{
        			return false;
        		}
        		
        	    // First insert the sequence ID.  
        		int seqIdOffset = (int)firstFilePointer;
        		
        		// Create memory handle 1 containing seqID offset and length
        		m1 = new Pair<Integer, Integer>(
        				seqIdOffset, seqId.length());
        		
        		// Write the sequence ID to the file
        		writeToFile(seqId);
        		
                // file pointer should be updated after writing the sequence bytes
        		// to the memory file.  Convert 2nd file pointer to sequence offset
        		int seqOffset = (int)memFile.getFilePointer();
        		
        		// Create memory handle 2 containing sequence offset and length
        		m2 = new Pair<Integer, Integer>(
        				seqOffset, seqLength);
        		
        		// Write the actual sequence to the file
        		writeToFile(seq);
        		
        		// Create a the pair of memory handles 
        		memoryHandles = new 
        		    Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(m1, m2);
        		
        		// Finally, store the pair of memory handles pair 
        		// at the calculated slot position
        		hashTable.set(slotPos, memoryHandles);  // Should return null
    		}
    		// Handle case for when only seqId was inserted into a hole
    		else if (!seqInserted)
    		{       		
                // file pointer should be updated after writing the sequence bytes
        		// to the memory file.  Convert 2nd file pointer to sequence offset
        		int seqOffset = (int)memFile.getFilePointer();
        		
        		// Create memory handle 2 containing sequence offset and length
        		m2 = new Pair<Integer, Integer>(
        				seqOffset, seqLength);
        		
        		// Write the actual sequence to the file
        		writeToFile(seq);
        		
        		// Create a the pair of memory handles
        		memoryHandles = new 
        		    Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(m1, m2);
        		
        		// Finally, store the pair of memory handles pair 
        		// at the calculated slot position
        		hashTable.set(slotPos, memoryHandles);  // Should return null
    		}
    	}
    		
    	/*
        curr.setNext(new Node(curr.item(), curr.next()));
        curr.setItem(it);
        if (tail == curr) tail = curr.next();  // New tail
        listSize++;
        */
    	return true;
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

    // Remove and return current element
    public void remove (String seqToRemove) 
    {
    	// get positioning using the sfold function
    	long hashPosition = sfold(seqToRemove, hashTableSize);
    	
    	// get pair at the position in hash table
    	Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> currHashPos =
    			hashTable.get((int) hashPosition);
    	
    	// check if there is a pair at that position
    	if (currHashPos == null)
    	{
    		// case where sfold returns empty position
    		// sequence does not exist
    		System.out.println("No sequence found using sequenceID: " + seqToRemove);
    		return;
    	}
    	
    	// get position of seqID
    	int seqIdPos = currHashPos.getKey().getKey();
    	
        // check if sequence ID is same
    	String fromFile = getDataFromFile((long) seqIdPos);
    	
    	// if sequence does not match, search the rest of the bucket
    	if (!fromFile.equalsIgnoreCase(seqToRemove))
    	{
    		// look for correct position in bucket
    		hashPosition = findCorrect(seqIdPos, fromFile);
    		
    		// if enters this case, no entry in bucket matches
    		if (hashPosition == -1)
    		{
    			System.out.println("No sequence found using sequenceID: " + seqToRemove);
        		return;
    		}
    	}
    	
    	// re-set the variables to correct values
    	currHashPos = hashTable.get((int) hashPosition);
    	seqIdPos = currHashPos.getKey().getKey();
    	fromFile = getDataFromFile((long) seqIdPos);
    	
    	// if correct, add entries to linked list of free spaces
    	curr.setNext(new Node(currHashPos.getKey()));
    	curr = curr.next();
    	curr.setNext(new Node(currHashPos.getValue()));
    	listSize += 2;
    	
    	// if correct, remove entry from the hash table
    	hashTable.set((int) hashPosition, null);
    	
    	// print out sequence
    	System.out.println("Sequence Removed " + seqToRemove + ": ");
    	int seqPos = currHashPos.getValue().getKey();
    	String seqFromFile = getDataFromFile((long) seqPos);
    	System.out.println(seqFromFile);
        
    	// add another check for good coding style
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
    public void search(String seqToSearch)
    {
    	// get positioning using the sfold function
    	long hashPosition = sfold(seqToSearch, hashTableSize);
    	
    	// get pair at the position in hash table
    	Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> currHashPos =
    			hashTable.get((int) hashPosition);
    	
    	// check if there is a pair at that position
    	if (currHashPos == null)
    	{
    		// case where sfold returns empty position
    		// sequence does not exist
    		System.out.println("SequenceID " + seqToSearch + " not found");
    		return;
    	}
    	
    	// get position of seqID
    	int seqIdPos = currHashPos.getKey().getKey();
    	
        // check if sequence ID is same
    	String fromFile = getDataFromFile((long) seqIdPos);
    	
    	
    	// case where seqID does not match, need to find correct value
    	if (!fromFile.equalsIgnoreCase(seqToSearch))
    	{
    		hashPosition = findCorrect(hashPosition, fromFile);
    		
    		// case where the sequence is not found in the bucket
        	if (hashPosition == -1)
        	{
        		System.out.println("SequenceID " + seqToSearch + " not found");
        		return;
        	}
    	}
    	
    	// reset the value of to correct value
    	currHashPos = hashTable.get((int) hashPosition);
    	
    	// case where correct seqID is found
    	// get seq offset and print string found
    	int seqPos = currHashPos.getValue().getKey();
    	String seqFromFile = getDataFromFile((long) seqPos);
    	System.out.println("Sequence Found: " + seqFromFile);
    	
    	// add another check for good coding style
    }
    
    /**
     * Helper method handles finding an empty index within the correct bucket
     * in the hash table.
     * @param seq the sequence which the slot index will be based off of
     * @return slotIndex if an empty slot was found within the bucket, 
     * otherwise -1 will be returned signifying a failure to find a valid slot
     */
    private int collisionResolutionPolicy(String seq)
    {
		// Compute index of slot where the two memory handles will be 
		// stored.  Type cast to integer because anything greater than
		// integer max will be out of range
	    int slotIndex = (int)sfold(seq, hashTableSize);
	    
    	// Get the pair at the position in hash table
    	Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> currSlotPair =
    			hashTable.get((int) slotIndex);
		
    	// If the slot is not available, execute the 
    	// linear collision resolution policy
    	if (currSlotPair == null)
    	{
    		return slotIndex;
    	}
    	
    	// Create variables to help track which 
    	// indices were checked in the bucket
    	boolean emptySlotFound = false;
    	int startIndex = (slotIndex / bucketSize) * bucketSize;  
    	int endIndex = ((slotIndex / bucketSize) + 1) * bucketSize;
    	int initialSlotIndex = slotIndex;
    	
    	// Linearly loop through and around the bucket 
    	// until an empty slot is found
    	while (!emptySlotFound)
    	{
    		// Check the next slot index
    		slotIndex++;
    		
    		// All slots are full in the bucket
    		if (slotIndex == initialSlotIndex)
    		{
    			slotIndex = -1;
    			break;
    		}
    		
    		// Loop around to beginning if end of the bucket is reached
    		if (slotIndex >= endIndex)
    		{
    			slotIndex = startIndex;
    		}
    		
        	// Get the pair at the position in hash table
        	currSlotPair = hashTable.get((int) slotIndex);
    		
        	if (currSlotPair == null)
        	{
        		return slotIndex;
        	}
    	}
    	return slotIndex;
    }
    
    /**
     * Helper method for remove
     * @param oldPos is the slot position which was not empty
     * @param seqNeeded sequence needing to be inserted
     * @return
     */
    private long findCorrect(long oldPos, String seqNeeded)
    {
    	boolean isFound = false;
    	
    	//default. if -1 is returned the sequence was never found
    	long newPos = -1;
    	
    	// set and starting and ending index for the bucket
    	int start = (int)oldPos / bucketSize;
    	int end = start + 1;
    	
    	// remember original old position to use for conclusion of search
    	long vOldPos = oldPos;
    	
    	// shift to set to correct bucket positioning
    	start *= bucketSize;
    	end *= bucketSize;
    	
    	while (!isFound)
    	{
    		// check a new hash entry
    		oldPos += 1;
    		
    		// if reach end of bucket, loop back to beginning
    		if (oldPos >= end)
    		{
    			oldPos = start;
    		}
    		
    		// iterated through entire bucket at least once
    		// sequence was not found
    		if (oldPos == vOldPos)
    		{
    			break;
    		}
    		
    		// get pair at the position in hash table
        	Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> currHashPos =
        			hashTable.get((int) oldPos);
        	
        	// check if there is a pair at that position
        	if (currHashPos == null)
        	{
        		// case where entry is null
        		continue;
        	}
        	
        	// get position of seqID
        	int seqIdPos = currHashPos.getKey().getKey();
        	
        	// check if sequence ID is same
        	String fromFile = getDataFromFile((long) seqIdPos);
        	
        	// case where correct seqID is found, need to break from loop
        	if (fromFile.equalsIgnoreCase(seqNeeded))
        	{
        		newPos = oldPos;
                isFound = true;
        	}
    	}
    	
    	return newPos;
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
