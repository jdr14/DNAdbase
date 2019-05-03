/**
 * 
 * @author Jovany Cabrera jovanyc4
 * @author Joey Destin Rodgers jdr14
 * @version 1.2.1
 */
public interface HashTable<K, V> {

	// k is the sequenceID and v is the pair of pairs
	/**
	 * 
	 * @param seqID
	 * @param seq
	 * @param seqLength
	 */
	public Long insert(K key, V value);
	
	/**
	 * 
	 * @param seqToRemove
	 * @return 
	 */
	public V remove(K key, V vlaue);
	
	/**
	 * 
	 * @param seqToSearch
	 */
	public V search(K key);
	
	/**
	 * 
	 */
	public void print();
	
	
}
