import java.util.*;

/**
 * 
 * @author Jovany Cabrera jovanyc4
 * @author Joey Destin Rodgers jdr14
 * @version 2.3.1
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
        Pair<Pair<Long, Long>, Pair<Long, Long>>> dnaHash;
    
    /**
     * 
     */
    private int bucketSize = 32;
    
    /**
     * 
     */
    private List<Pair<Long, String>> resultList;
    
    /**
     * Default Constructor
     * @param c is the name of the command file
     * @param h is the name of the hash file
     * @param s is the size of the hash table
     * @param m is the name of the memory file
     */
    DnaMain(String c, String h, long s, String m)
    {
        resultList = new ArrayList<Pair<Long, String>>();
        mDna = new MemoryManager(m);
        dnaHash = new HashTableDna<String, 
                Pair<Pair<Long, Long>, Pair<Long, Long>>>(h, s);
    }
    
    /**
     * 
     * @param seqId is the sequence ID being inserted
     * @param sequence is the sequence being inserted
     * @param seqLength is the length of the sequence
     */
    public void insert(String seqId, String sequence, int seqLength)
    {
        // check if the insertion was valid
        if (contains(seqId))
        {
            // if not, print error message and return
            System.out.println("SequenceID " + seqId + " exists");
            return;
        }
        // else continue down the code and insert into memFile
        
        
        // create a value from the results of writing to memory file
        Pair<Pair<Long, Long>, Pair<Long, Long>> fileResult = 
                mDna.insert(seqId, sequence);
        
        
        // use sequenceId and result from memory insert to create
        //entry in hash table
        long hashSlot = dnaHash.insert(seqId, fileResult);
        if (hashSlot < 0)
        {
            System.out.println("Bucket full. Sequence " +
                seqId + " could not be inserted");
            return;
        }
        
        String msg = seqId + ": hash slot [" + hashSlot + "]";
        if (resultList.isEmpty())
        {
            resultList.add(new Pair<Long, String>(hashSlot, msg));
            return;
        }
        else
        {
            for (int i = 0; i < resultList.size(); i++)
            {
                long pos = resultList.get(i).getKey();
                if (hashSlot > pos)
                {
                    resultList.add(i, new Pair<Long, String>(hashSlot, msg));
                    return;
                }
            }
        }
        // add pair to results list for printing later
        resultList.add(new Pair<Long, String>(hashSlot, msg));
    }
    
    
    /**
     * 
     * @param seqId is the sequence Id being checked
     * @return true if insertion is possible
     */
    public boolean contains(String seqId)
    {
        long hashPosition = dnaHash.getsFold(seqId);
        
        // check if slot is available
        if (dnaHash.get((int)hashPosition) == null)
        {
            return false;
        }
        
        int currHashIndex = (int)hashPosition;
        int initialHashPos = (int)hashPosition;
//      currHashIndex += 1;
        
        int startIndex = ((int)hashPosition / bucketSize) * bucketSize;  
        int endIndex = (((int)hashPosition / bucketSize) + 1) * bucketSize;
        
        // FSM: (Finite State Machine)
        // Iterate through entire bucket in attempt to find seq ID
        while (true)
        {           
            // Case 1: Null position
            while (dnaHash.get(currHashIndex) == null)  // Null Handler
            {
//              incrementPosWithinBucket(currHashIndex, startIndex, endIndex);
                // 1) increment the hash index
                currHashIndex += 1;
                
                // 2) Check for null position
                if (currHashIndex == endIndex)
                {
                    currHashIndex = startIndex;
                }
                
                if (currHashIndex == initialHashPos)
                {
                    return false;
                }
            }
            
            // Case 2: Tombstone position (has already been checked for
            // null at this point)
            while (dnaHash.get(currHashIndex).getKey().getValue() < 0)
            {   
//              incrementPosWithinBucket(currHashIndex, startIndex, endIndex);
                currHashIndex += 1;
                
                // 2) Check for null position
                if (currHashIndex == endIndex)
                {
                    currHashIndex = startIndex;
                }
                
                // Case where all tombstones were found in the bucket
                if (currHashIndex == initialHashPos)
                {
                    return false;
                }
                
                // Return to case 1 if next position is null
                if (dnaHash.get(currHashIndex) == null)
                {
                    break;
                }
            }
            
            // Check Null again 
            while (dnaHash.get(currHashIndex) == null)  // Null Handler
            {
//              incrementPosWithinBucket(currHashIndex, startIndex, endIndex);
                currHashIndex += 1;
                
                // 2) Check for null position
                if (currHashIndex == endIndex)
                {
                    currHashIndex = startIndex;
                }
                
                if (currHashIndex == initialHashPos)
                {
                    return false;
                }
            }
            
            // Case 3: Actual sequence is stored at that hash position
            while (dnaHash.get(currHashIndex).getKey().getValue() >= 0)
            {
                Pair<Pair<Long, Long>, Pair<Long, Long>> memHandles = 
                        dnaHash.get(currHashIndex);
                
                if (memHandles.getKey().getValue() == (seqId.length()))
                {
                    // Check the actual sequence stored on disk
                    long fileOffset = memHandles.getKey().getKey();
                    
                    String siD = mDna.getDataFromFile(fileOffset, 
                            seqId.length());
                    
                    // Equivalent seq found in hash table
                    if (siD.equals(seqId))
                    {
                        return true;
                    }
                    
//                  incrementPosWithinBucket(currHashIndex, startIndex, 
//                          endIndex);
                    currHashIndex += 1;
                    
                    // 2) Check for null position
                    if (currHashIndex == endIndex)
                    {
                        currHashIndex = startIndex;
                    }
                    
                    if (currHashIndex == initialHashPos)
                    {
                        return false;
                    }
                    
                    if (dnaHash.get(currHashIndex) == null)
                    {
                        break;
                    }
                }
                else
                {
//                  incrementPosWithinBucket(currHashIndex, startIndex, 
//                          endIndex);
                    currHashIndex += 1;
                    
                    // 2) Check for null position
                    if (currHashIndex == endIndex)
                    {
                        currHashIndex = startIndex;
                    }
                    
                    if (currHashIndex == initialHashPos)
                    {
                        return false;
                    }
                    
                    if (dnaHash.get(currHashIndex) == null)
                    {
                        break;
                    }
                }
            }  // End case 3 while
        }  // End master while
    }  // End contains
    
    /**
     * Helper function for the contains method
     * @param currHashIndex
     * @param startOfBucket
     * @param endOfBucket
     */
    private void incrementPosWithinBucket(int currHashPos,
            int startOfBucket, int endOfBucket)
    {
        // 1) increment the hash index
        currHashPos++;
        
        // 2) Check for null position
        if (currHashPos == endOfBucket)
        {
            currHashPos = startOfBucket;
        }
    }
    
    /**
     * @param seqToRemove as a string
     */
    public void remove(String seqToRemove)
    {   
        Pair<Pair<Long, Long>, Pair<Long, Long>> hashEntry =
                dnaHash.search(seqToRemove);
        
        // remove something that does not exist
        if (hashEntry == null)
        {
            return;
        }
        
        long seqIdFoundLength = hashEntry.getKey().getValue();
        
        // case where sequence is found in its correct spot
        if (mDna.search(seqToRemove, hashEntry.getKey().getKey()) &&
                (seqToRemove.length() == (int) seqIdFoundLength))
        {
            mDna.remove(hashEntry, seqToRemove.length());
            // mark as tomb stone
            long pos = dnaHash.getsFold(seqToRemove);
            
            //Pair<Pair<Long, Long>, Pair<Long, Long>> memHandles = 
            dnaHash.remove(Long.toString(pos), hashEntry );
            
            // Update the printable list of sequences
            updateResultList(pos);
            
            // remove from hash table as well
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
                System.out.println("Sequcnce ID " 
                    + seqToRemove + " not found");
                return;
            }
            // update hashEntry variable
            try
            {
                hashEntry = dnaHash.get((int) correctPosition);
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
//              System.err.println(e);
                System.out.println("Sequcnce ID " 
                    + seqToRemove + " not found");
                return;
            }
            
            // remove correct entry
            mDna.remove(hashEntry, seqToRemove.length());
            // mark as tomb stone
            dnaHash.remove(Long.toString(correctPosition), hashEntry);
            
            // Remove the sequence from the printable list of sequences
            updateResultList(correctPosition);
        }           
    }
    
    /**
     * Remove the sequence from the printable list
     * @param hashPos is the position in the hash table
     */
    private void updateResultList(long hashPos)
    {
        for (int i = 0; i < resultList.size(); i++)
        {
            if (hashPos == resultList.get(i).getKey())
            {
                resultList.remove(i);
                break;
            }
        }
    }
    
    /**
     * 
     * @param seqToFind is the sequence that needs to be found
     */
    public void search(String seqToFind)
    {
        Pair<Pair<Long, Long>, Pair<Long, Long>> hashEntry =
                dnaHash.search(seqToFind);
        
        // search something that does not exist
        if (hashEntry == null)
        {
            return;
        }
        
        // case where sequence was removed
        if (hashEntry.getKey().getValue() == -1)
        {
            System.out.println("SequenceID " + seqToFind + " not found");
            return;
        }
        
        long seqIdFoundLength = hashEntry.getKey().getValue();
        // case where sequence is found in its correct spot
        if (mDna.search(seqToFind, hashEntry.getKey().getKey()) && 
                (seqToFind.length() == (int) seqIdFoundLength))
        {
            // just print the sequence found at the offset
            // of the second pair
            long tempSize = hashEntry.getValue().getValue();
            mDna.printSeq(hashEntry.getValue().getKey(), (int) tempSize);
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
                System.out.println("Sequence ID " +
                    seqToFind + " does not exist.");
                return;
            }
            
            // update hashEntry variable
            hashEntry = dnaHash.get((int) correctPosition);
            // print out correct sequence
            long tempSize = hashEntry.getValue().getValue();
            mDna.printSeq(hashEntry.getValue().getKey(), (int) tempSize);
        }
        
    }
    
    /**
     * 
     * @param oldPos is the old position of the seqId 
     * @param seqNeeded is the sequence needed
     * @return the correct position of seqNeeded
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
        
        while (!isFound)
        {
            // check a new hash entry
            oldPos += 1;
            
            // if reach end of bucket, loop back to beginning
            if (oldPos > (end - 1))
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
            
            if (currHashPos.getKey().getValue() != (long) seqNeeded.length())
            {
                continue;
            }

            // case where correct seqID is found, need to break from loop
            // double check to see if this search function does what is expected
//          if (mDna.search(seqNeeded, currHashPos.getKey().getKey()))
//          {
            newPos = oldPos;
            isFound = true;
//          }
        }
        
        return newPos;
    }
    
    /**
     * 
     * @param seqLength is the length of the sequence
     * @return the length of the sequence as a byte[]
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
     * Function used for the print command
     */
    public void printResult()
    {
        System.out.println("Sequence IDs:");
        for (int i = resultList.size() - 1; i >= 0; i--)
        {
            System.out.println(resultList.get(i).getValue());
        }
        
        if (mDna.getList().isEmpty())
        {
            System.out.println("Free Block List: none");
        }
        else
        {
            System.out.println("Free Block List:");
            
            long byteLength;
            LinkedList<Pair<Long, Long>> freeNodeList = mDna.getList();
            for (int i = 0; i < freeNodeList.size(); i++)
            {
                byteLength = 
                        seqToByteLength(mDna.getList().get(i).getValue());
                System.out.println("[Block " + (i + 1) + "] Starting Byte " +
                        "Location: " + freeNodeList.get(i).getKey() +
                        ", Size " + (byteLength + 1) + " bytes");
            }
        }
    }
}
