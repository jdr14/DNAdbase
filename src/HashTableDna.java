import java.util.ArrayList;

/**
 * 
 * @author Jovany Cabrera jovanyc4
 * @author Joey Destin Rodgers jdr14
 *
 * @param <K>
 * @param <V>
 */
public class HashTableDna<K, V> implements HashTable<K, V> {

	
	
    /**
     * Private hashTable to track the memory handles
     * M1 should be the handle to sequence ID (offset and length)
     * M2 should be the handle to the actual sequence (offset and length)
     */
    private Pair<Pair<Long,Long>, Pair<Long, Long>>[] hashTable;
    
    /**
     * 
     */
    private String hashFileName;
    
    /**
     * 
     */
    private long hashTableSize;
    
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
	 * 
	 * @param seqId
	 * @return true if insertion is possible
	 */
	public boolean contains(String seqId)
	{
		return false;
	}

	@Override
	public void insert(K key, V value) 
	{
		// get positioning using the sfold function
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
			
		}
		
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
    		System.out.println("No sequence found using sequenceID: " + (String) key);
    		return null;
    	}
    	

		return (V) currHashPos;
	}

	@Override
	public void print() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public V remove(K key) 
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
    		System.out.println("No sequence found using sequenceID: " + (String) key);
    		return null;
    	}
    	

		return (V) currHashPos;
	}
	
	/**
	 * helper function to get value at specific location
	 * @param position
	 * @return
	 */
	public V get(int position)
	{
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
