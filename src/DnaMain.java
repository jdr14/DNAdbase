import java.util.*;
import java.io.*;

/**
 * 
 * @author Jovany Cabrera jovanyc4
 * @author Joey Destin Rodgers jdr14
 *
 */
public class DnaMain {

	/**
	 * 
	 */
	private MemoryManager mDna;
	
	/**
	 * 
	 */
	private HashTableDna<String, 
	    Pair<Pair<Long,Long>, Pair<Long, Long>>> dnaHash;
	
	/**
	 * 
	 */
	private String commandFileName;
	
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
	private String memoryFileName;
	
	/**
	 * Default Constructor
	 */
	DnaMain(String c, String h, long s, String m)
	{
		commandFileName = c;
		hashFileName = h;
		hashTableSize = s;
		memoryFileName = m;
		mDna = new MemoryManager(m);
		dnaHash = new HashTableDna<String, 
				Pair<Pair<Long, Long>, Pair<Long, Long>>>(h, s);
	}
	
	/**
	 * 
	 */
	public void insert(String seqId, String sequence, int seqLength)
	{
		// check if the insertion was valid
		if (!dnaHash.contains(seqId))
		{
			// if not, print error message and return
			System.err.println("Sequence " + seqId + "already exists!");
			return;
		}
		// else continue down the code and insert into memFile
		
		
		// create a value from the results of writing to memory file
		Pair<Pair<Long, Long>, Pair<Long, Long>> fileResult = 
				mDna.insert(seqId, sequence);
		
		// use sequenceId and result from memory insert to create
		//entry in hash table
		dnaHash.insert(seqId, fileResult);
		
	}
	
	/**
	 * 
	 */
	public void remove(String seqToRemove)
	{	
		Pair<Pair<Long, Long>, Pair<Long, Long>> hashEntry =
				dnaHash.search(seqToRemove);
		
		// case where sequence is found in its correct spot
		if(mDna.search(seqToRemove, hashEntry.getKey().getKey()))
		{
			mDna.remove(hashEntry);
		}
		// else start checking around in the bucket
		else
		{
			// get correct function with helper function
			long correctPosition = 
					findCorrect(dnaHash.getsFold(seqToRemove), seqToRemove);
			
			// check if it was ever found
			if (correctPosition == -1)
			{
				// throw remove error error
			}
			// update hashEntry variable
			hashEntry = dnaHash.get((int) correctPosition);
			// remove correct entry
			mDna.remove(hashEntry);
		}
					
	}
	
	/**
	 * 
	 * @param seqToFind
	 */
	public void search(String seqToFind)
	{
		Pair<Pair<Long, Long>, Pair<Long, Long>> hashEntry =
				dnaHash.search(seqToFind);
		
		// case where sequence is found in its correct spot
		if(mDna.search(seqToFind, hashEntry.getKey().getKey()))
		{
			// just print the sequence found at the offset
			// of the second pair
			mDna.printSeq(hashEntry.getValue().getKey());
		}
		// else start checking around in the bucket
		else
		{
			// get correct function with helper function
			long correctPosition = 
					findCorrect(dnaHash.getsFold(seqToFind), seqToFind);
			
			// check if it was ever found
			if (correctPosition == -1)
			{
				// throw search error
			}
			
			// update hashEntry variable
			hashEntry = dnaHash.get((int) correctPosition);
			// print out correct sequence
			mDna.printSeq(hashEntry.getValue().getKey());
		}
		
	}
	
	/**
     * 
     * @param oldPos
     * @param seqNeeded
     * @return
     */
    public long findCorrect(long oldPos, String seqNeeded)
    {
    	boolean isFound = false;
    	
    	//default. if -1 is returned the sequence was never found
    	long newPos = -1;
    	
    	// set and starting and ending index for the bucket
    	int start = (int) oldPos / 32;
    	int end = start + 1;
    	
    	// remember original old position to use for conclusion of search
    	long vOldPos = oldPos;
    	
    	// shift to set to correct bucket positioning
    	start *= 32;
    	end *= 32;
    	
    	while(!isFound)
    	{
    		// check a new hash entry
    		oldPos += 1;
    		
    		// if reach end of bucket, loop back to beginning
    		if (oldPos > end)
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
        	Pair<Pair<Long, Long>, Pair<Long, Long>> currHashPos =
        			dnaHash.get((int) oldPos);
        	
        	// check if there is a pair at that position
        	// this check needs to be changed to a tombstone check
        	// tombstone just means that there was once something here
        	// but now there isnt (removed) and we set a flag of some sort
        	if (currHashPos == null)
        	{
        		// case where entry is null
        		continue;
        	}

        	// case where correct seqID is found, need to break from loop
        	// double check to see if this search function does what is expected
        	if (mDna.search(seqNeeded, currHashPos.getKey().getKey()))
        	{
        		newPos = oldPos;
                isFound = true;
        	}
    	}
    	
    	return newPos;
    }
}
