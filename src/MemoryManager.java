import java.io.*;

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
     * Constructor creates 
     * @param hashFileName
     */
	public MemoryManager(String memoryFileName) 
	{
        // Each pair should have the length of sequence as well 
		// as the pointer to the actual sequence on file as well
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
	}
	
	/**
	 * Hashing function to compute the index (slot) of the hash table where
	 * the memory handles are to be stored.
	 * @param s
	 * @param M
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
        curr = tail = new Node(null); // Create trailer
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
    
    // Insert "it" at current position
    public boolean insert(Node<Pair<Long, String>> it) 
    {
        curr.setNext(new Node(curr.item(), curr.next()));
        curr.setItem(it);
        if (tail == curr) tail = curr.next();  // New tail
        listSize++;
        return true;
    }
    
    // Append "it" to list
    private boolean append(Node<Pair<Long, Long>> memoryHandles) 
    {
        tail.setNext(new Node(null));
        tail.setItem(it);
        tail = tail.next();
        listSize++;
        return true;
    }

    // Remove and return current element
    public Pair<Long, Long> remove () 
    {
        if (curr == tail) 
        {
    	    return null;          // Nothing to remove
        }
        Pair<Long, Long> it = curr.item();  // Remember value
        curr.setItem(curr.next().item());  // Pull forward the next element
        if (curr.next() == tail) 
        {
    	    tail = curr;   // Removed last, move tail
        }
        curr.setNext(curr.next().next());       // Point around unneeded link
        listSize--;                             // Decrement element count
        return it;                              // Return value
    }

    public void moveToStart() { curr = head.next(); } // Set curr at list start
    public void moveToEnd() { curr = tail; }          // Set curr at list end

    // Move curr one step left; no change if now at front
    public void prev() {
      if (head.next() == curr) return; // No previous element
      Link temp = head;
      // March down list until we find the previous element
      while (temp.next() != curr) temp = temp.next();
      curr = temp;
    }

    // Move curr one step right; no change if now at end
    public void next() { if (curr != tail) curr = curr.next(); }

    public int length() { return listSize; } // Return list length


    // Return the position of the current element
    public int currPos() {
      Link temp = head.next();
      int i;
      for (i=0; curr != temp; i++)
        temp = temp.next();
      return i;
    }
    
    // Move down list to "pos" position
    public boolean moveToPos(int pos) {
      if ((pos < 0) || (pos > listSize)) return false;
      curr = head.next();
      for(int i=0; i<pos; i++) curr = curr.next();
      return true;
    }

    // Return true if current position is at end of the list
    public boolean isAtEnd() { return curr == tail; }

    // Return current element value. Note that null gets returned if curr is at the tail
    public Object getValue() { return curr.element(); }

    // Check if the list is empty
    public boolean isEmpty() { return listSize == 0; }
    
    public String toString() {
  		Link temp = head.next();
  		StringBuffer out = new StringBuffer((listSize + 1) * 4);

  		out.append("< ");
  		for (int i = 0; i < currPos(); i++) {
  			out.append(temp.element());
  			out.append(" ");
  			temp = temp.next();
  		}
  		out.append("| ");
  		for (int i = currPos(); i < listSize; i++) {
  			out.append(temp.element());
  			out.append(" ");
  			temp = temp.next();
  		}
  		out.append(">");
  		return out.toString();
  	  }
}
