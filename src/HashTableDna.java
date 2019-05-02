import java.util.ArrayList;

/**
 * 
 * @author Jovany Cabrera jovanyc4
 * @author Joey Destin Rodgers jdr14
 *
 * @param <K>
 * @param <V>
 */
public class HashTableDna<K, V> implements HashTable<K, V> 
{
    /**
     * Private hashTable to track the memory handles
     * M1 should be the handle to sequence ID (offset and length)
     * M2 should be the handle to the actual sequence (offset and length)
     */
    private Pair<Pair<Long,Long>, Pair<Long, Long>> [] hashTable;
    
    /**
     * 
     */
    private String hashFileName;
    
    /**
     * 
     */
    private long hashTableSize;
    
    /**
     * 
     */
    private int bucketSize = 32;
    
	/**
	 * Default Constructor
	 */
	public HashTableDna(String h, long s)
	{
		hashFileName = h;
		hashTableSize = s;
		// probably need to change this
		hashTable = new Pair[(int) hashTableSize];
	}
    
	/**
	 * Overrides the interface provided by the HashTable.java file
	 * @param key : Should be the sequence ID as (as string)
	 * @param value : Should be the actual sequence (as string)
	 */
	@Override
	public Long insert(K key, V value)
	{
		String result;
		// Get positioning using the sfold function
		long hashPosition = sfold((String)key, (int)hashTableSize);
		
		// check if slot is available
		if (hashTable[(int)hashPosition] == null)
		{
			// if it is. put entry there
			hashTable[(int)hashPosition] = 
					(Pair<Pair<Long, Long>, Pair<Long, Long>>) value;
		}
		// else call collision resolution
		else
		{
			// Execute collision resolution policy to find the correct position
			int adjustedHashPosition = collisionResolutionPolicy((String)key);
			
			// Validity check for the hash position
			if (adjustedHashPosition < 0)
			{
				return (long)-1;  // Fails
			}
			
			// Set memory handles to the correct slot index in the hash table
			hashTable[adjustedHashPosition] = 
					(Pair<Pair<Long, Long>, Pair<Long, Long>>) value;
			
			hashPosition = (long) adjustedHashPosition;
		}
		
		//result = (String)key + ": hash slot [" + hashPosition + "]";
		
		return hashPosition;
	}
	
    /**
     * 
     * @param oldPos
     * @param seqNeeded
     * Helper method handles finding an empty index within the correct bucket
     * in the hash table.
     * @param seq the sequence which the slot index will be based off of
     * @return slotIndex if an empty slot was found within the bucket, 
     * otherwise -1 will be returned signifying a failure to find a valid slot
     */
    private int collisionResolutionPolicy(String seq)
    {
		// This index will be occupied which is what this function will resolve
		long slotIndex = getsFold(seq);

    	// Create variables to help track which 
    	// indices were checked in the bucket
    	boolean emptySlotFound = false;
    	int startIndex = ((int)slotIndex / bucketSize) * bucketSize;  
    	int endIndex = (((int)slotIndex / bucketSize) + 1) * bucketSize;
    	int initialSlotIndex = (int)slotIndex;
    	Pair<Pair<Long, Long>, Pair<Long, Long>> currSlotPair; 

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
        	currSlotPair = hashTable[(int) slotIndex];

        	if (currSlotPair == null)
        	{
        		return (int)slotIndex;
        	}
    	}
    	return (int)slotIndex;
    }

	@Override
	public V search(K key) 
	{
		// get positioning using the sfold function
		long hashPosition = sfold((String)key, (int)hashTableSize);
		
    	// get pair at the position in hash table
		Pair<Pair<Long, Long>, Pair<Long, Long>> currHashPos =
				hashTable[(int)hashPosition];
		
    	// check if there is a pair at that position
    	if (currHashPos == null)
    	{
    		// case where sfold returns empty position
    		// sequence does not exist
    		System.out.println("SequenceID " + (String) key + " not found.");
    		return null;
    	}
    	

		return (V) currHashPos;
	}

	@Override
	public void print() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public V remove(K key, V value) 
	{

		Pair<Pair<Long, Long>, Pair<Long, Long>> currHashPos =
				(Pair<Pair<Long, Long>, Pair<Long, Long>>) value;
		
		String positionString = (String) key;
		int position = Integer.parseInt(positionString);
//    	// get positioning using the sfold function
//		long hashPosition = sfold((String)key, (int)hashTableSize);
//		
//    	// get pair at the position in hash table
//		Pair<Pair<Long, Long>, Pair<Long, Long>> currHashPos =
//				hashTable[(int)hashPosition];
//		
//    	// check if there is a pair at that position
//    	if (currHashPos == null)
//    	{
//    		// case where sfold returns empty position
//    		// sequence does not exist
//    		System.out.println("No sequence found using sequenceID: " + (String) key);
//    		return null;
//    	}
    	
    	// set flag for tomb stone. Sequence ID length value will be
    	// -1 indicating that something used to be here
    	currHashPos.getKey().setValue((long)-1);
    	
    	hashTable[position] = currHashPos;

		return (V) currHashPos;
	}
	
	/**
	 * helper function to get value at specific location
	 * @param position
	 * @return
	 */
	public V get(int position) throws ArrayIndexOutOfBoundsException
	{
		if (position >= hashTableSize || position < 0)
		{
			// TODO: Unsure if printing is necessary here
			throw new ArrayIndexOutOfBoundsException("Error: trying to access"
					+ " index greater than hash table size");
		}
		return (V) hashTable[position];
	}
	
	/**
	 * Helper function to get result of sfold
	 * @param seqId
	 * @return
	 */
	public long getsFold(String seqId)
	{
		return sfold(seqId, (int)hashTableSize);
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
		}// move function to hash

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
	
}
