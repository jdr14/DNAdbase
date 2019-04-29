import java.util.*;
import java.io.*;

/**
 * 
 * @author Jovany Cabrera jovanyc4
 * @author Joey Destin Rodgers jdr14
 *
 */
public class DnaMain {

	MemoryManager mDna;
	
	HashTableDna<Pair<Long, Long>, Pair<Long, Long>> dnaHash;
	
	/**
	 * Default Constructor
	 */
	DnaMain()
	{
		mDna = new MemoryManager();
		dnaHash = new HashTableDna<Pair<Long, Long>, Pair<Long, Long>>();
	}
	
}
