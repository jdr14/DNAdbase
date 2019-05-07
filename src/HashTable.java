/**
 * 
 * @author Jovany Cabrera jovanyc4
 * @author Joey Destin Rodgers jdr14
 * @version 1.2.1
 * @param <K> is key
 * @param <V> is value
 */
public interface HashTable<K, V> {

    // k is the sequenceID and v is the pair of pairs
    /**
     * 
     * @param key is the K being used
     * @param value is the V being used
     * @return the position that value was inserted
     */
    public Long insert(K key, V value);
    
    /**
     * 
     * @param key is the K being used
     * @param vlaue is the V being used
     * @return V is a value type
     */
    public V remove(K key, V vlaue);
    
    /**
     * 
     * @param key is the K being used
     * @return V is a value type
     */
    public V search(K key);
    
    /**
     * the default printing function
     */
    public void print();
    
    
}
