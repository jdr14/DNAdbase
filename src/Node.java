
/**
 * Node class to be used in building the linked list in Memory Manager class
 * @author Joey Rodgers: jdr14
 * @author Jovany Cabrera: jovanyc4
 * @param <T> Specify the type of node it is 
 * (i.e. what is it storing internally)
 * @version 1.0.0
 */
public class Node<T>
{
	/**
	 * item is an arbitrary object to be stored in a node
	 */
    private T item;
    
    /**
     * 
     */
    private Node<T> next;
    
    /**
     * Default constructor
     */
	public Node() 
	{
	    item = null;
		next = null;
	}
	
	/**
	 * Constructor to set an item
	 * @param anItem arbitrary item to be set
	 */
	public Node(T anItem)
	{
		item = anItem;
		next = null;
	}
	
	public Node(Node<T> copy)
	{
		item = copy.item();
		next = copy.next();
	}
    
	/**
	 * Constructor to set 
	 * @param anItem
	 * @param n
	 */
	public Node(T anItem, Node<T> n)
	{
		item = anItem;
		next = n;
	}
	
	/**
	 * Accessor for the item
	 * @return item stored in the node
	 */
	public T item()
	{
		return item;
	}
	
	/**
	 * Mutator for the item
	 * @param anItem will set 'item' in the node internally
	 */
	public void setItem(T anItem)
	{
	    item = anItem;
	}
	
	/**
	 * Next node in the linked list
	 * @return next (Child of current node)
	 */
	public Node<T> next()
	{
		return next;
	}
	
	/**
	 * Next node in the linked list
	 * @param n as the next node in the sequence
	 */
	void setNext(Node<T> n)
	{
		next = n;
	}
}
