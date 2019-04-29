/**
 * 
 * @author Jovany Cabrera jovanyc4
 * @author Joey Destin Rodgers jdr14
 *
 */
public interface HashTable<K, V> {

	// k is the sequenceID and v is the pair of pairs
	/**
	 * 
	 * @param seqID
	 * @param seq
	 * @param seqLength
	 */
	public void insert(K key, V value);
	
	/**
	 * 
	 * @param seqToRemove
	 * @return 
	 */
	public V remove(K key);
	
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
