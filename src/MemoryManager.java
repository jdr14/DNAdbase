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
    		posOfseqId = emplaceInList(arrayofId);
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
    			new Pair<Long, Long>(posOfseqId, (long)seqId.length());  //(long)arrayofId.length);
    	
    	// case where space was found in list for the seq
    	// parameter of this function might be string
    	if (spaceInList(arrayofSeq))
    	{
    		// if space available, place in list and save position
    		posOfSeq = emplaceInList(arrayofSeq);
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
    			new Pair<Long, Long>(posOfSeq, (long)seq.length());  // (long)arrayofSeq.length);
    	
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
    	BitSet b1;
    	boolean mult4 = (convertThis.length() % 4) == 0;
    	if (mult4)
    	{
    		// Allocate 2 bits per letter
    		b1 = new BitSet(convertThis.length() * 2);
    	}
    	else  // not multiple of 4
    	{
    		int numLetters = (convertThis.length() / 4) + 1;
    		b1 = new BitSet(numLetters * 2);
    	}

    	for (int i = 0; i < convertThis.length(); i++)
    	{
    		char currChar = convertThis.charAt(i);
    		if (currChar == 'A')
    		{
    		    b1.set(2 * i, 0);
    		    b1.set((2 * i) + 1, 0);
    		}
    		else if (currChar == 'C')
    		{
    		    b1.set(2 * i, 0);
    		    b1.set((2 * i) + 1, 1);
    		}
    		else if (currChar == 'G')
    		{
    		    b1.set(2 * i, 1);
    		    b1.set((2 * i) + 1, 0);
    		}
    		else if (currChar == 'T')
    		{
    		    b1.set(2 * i, 1);
    		    b1.set((2 * i) + 1, 1);
    		}
    	}
    	
    	return b1.toByteArray();
    }
    
    /**
     * 
     * @param convertThis
     * @return
     */
    private String byteToString(byte[] convertThis)
    {
    	String result = "";
    	BitSet bits1 = BitSet.valueOf(convertThis);
    	int length = bits1.length();
    	int numOfChars = length/4;
    	
    	// loop through length of array and convert to string
    	byte[] tempArray = new byte[2];
    	for (int i = 0; i < numOfChars; i++)
    	{
    		if (i <= numOfChars)
    		{
    			tempArray[0] = convertThis[i * 2];
    			tempArray[1] = convertThis[i * 2 + 1];
    			result += byteToStringHelper(tempArray);
    		}
    	}
    	return result;
    }
    
    
    private Character byteToStringHelper(byte[] tempBytes)
    {
    	
    	if (tempBytes[0] == (byte)0)
    	{
    		if (tempBytes[1] == (byte)0)
    		{
    			return 'A';
    		}
    		else
    		{
    			return 'C';
    		}
    	}
    	else
    	{
    		if (tempBytes[1] == (byte)0)
    		{
    			return 'G';
    		}
    		else
    		{
    			return 'T';
    		}
    	}
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
    	return false;
    }
    
    /**
     * Function that places the seq/seqID into its position
     * @param insertThis
     * @return the position in the file of the seq/seqId
     */
    private long emplaceInList(byte[] insertThis)
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



// ACTIVE TESTING FOR BITSETS BELOW!!! 


/*
public class Main
{
    public static void main(String[] args)
{
    p parse = new p();
    String s = "ACGTCGA";
    
    byte[] b = parse.stringToByte(s);
    System.out.println(b.length);
   
    String n = parse.byteToString(b);
    System.out.println(n);
}
}

import java.util.*;

public class p
{
    public byte[] stringToByte(String convertThis)
    {
    	BitSet b1;
    	boolean mult4 = (convertThis.length() % 4) == 0;

    	b1 = new BitSet(convertThis.length() * 2);
    	System.out.println(b1.length());
    	// int length = (int) Math.ceil((double)convertThis.length() / 4.0);
    	//byte[] result = new byte[length];
    	
    	// a lot more code is needed here!
    	//int arrayIndex = 0;
    	// check each char in the seqID and insert correct bytes into array
    	// go for the length of the string
    	for (int i = 0; i < convertThis.length(); i++)
    	{
    		char currChar = convertThis.charAt(i);
    		if (currChar == 'A')  // 00
    		{

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
    	
    	return b1.toByteArray();
    }
    
    public String byteToString(byte[] convertThis)
    {
    	String result = "";
    	int numOfChars = convertThis.length/2;
    	
    	// loop through length of array and convert to string
    	byte[] tempArray = new byte[2];
    	for (int i = 0; i < numOfChars; i++)
    	{
    		if (i <= numOfChars)
    		{
    			tempArray[0] = convertThis[i * 2];
    			tempArray[1] = convertThis[i * 2 + 1];
    			result += byteToStringHelper(tempArray);
    		}
    	}
    	return result;
    }
    
    private Character byteToStringHelper(byte[] tempBytes)
    {
    	
    	if (tempBytes[0] == (byte)0)
    	{
    		if (tempBytes[1] == (byte)0)
    		{
    			return 'A';
    		}
    		else
    		{
    			return 'C';
    		}
    	}
    	else
    	{
    		if (tempBytes[1] == (byte)0)
    		{
    			return 'G';
    		}
    		else
    		{
    			return 'T';
    		}
    	}
    }
}

*/
