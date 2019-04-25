/**
 * Generic class that outlines a new type called pair that 
 * pairs a key to a value (of custom/arbitrary types)
 * @author Joey Rodgers: jdr14
 * @author Jovany Cabrera: jovanyc4
 * @param <K> Custom type k
 * @param <V> Custom type v
 * @version 1.0.0
 */
public class Pair<K, V> 
{
    /**
     * key to be paired with the value
     */
    private K key;
    
    /**
     * Value paired with the key
     */
    private V value;
    
    /**
     * Default constructor
     */
    public Pair()
    {
        key = null;
        value = null;
    }
    
    /**
     * Parameterized constructor
     * @param k key to be set
     * @param val value to be set
     */
    public Pair(K k, V val)
    {
        key = k;
        value = val;
    }
    
    /**
     * Method to set the key
     * @param k key to be set
     */
    public void setKey(K k)
    {
        key = k;
    }
    
    /**
     * Method to set the value
     * @param v value to be set
     */
    public void setValue(V v)
    {
        value = v;
    }
    
    /**
     * Method to get the key
     * @return key K
     */
    public K getKey()
    {
        return key;
    }
    
    /**
     * Method to get the value
     * @return value V
     */
    public V getValue()
    {
        return value;
    }
}
