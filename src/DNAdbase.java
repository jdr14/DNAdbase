/**
 * 
 * @author Joey Destin Rodgers jdr14
 * @author Jovany Cabrera jovanyc4
 * @version 3.2.1
 *
 */
public class DNAdbase 
{

	/**
	 * 
	 */
	public DNAdbase() 
	{

	}
    
	/**
	 * The main for the program
	 * @param args is an array of Strings
	 */
	public static void main(String[] args) 
	{
		// On my honor: //
		// - I have not used source code obtained from another student,
		// or any other unauthorized source, either modified or
		// unmodified. //
		// - All source code and documentation used in my program is
		// either my original work, or was derived by me from the
		// source code published in the textbook for this course. //
		// - I have not discussed coding details about this project with
		// anyone other than my partner (in the case of a joint
		// submission), instructor, ACM/UPE tutors or the TAs assigned
		// to this course. I understand that I may discuss the concepts
		// of this program with other students, and that another student
		// may help me debug my program so long as neither of us writes
		// anything during the discussion or modifies any computer file
		// during the discussion. I have violated neither the spirit nor
		// letter of this restriction.
		
		// First check number of arguments
		if (args.length != 4)
		{
			System.err.println("Error: Incorrect amount of arguments");
			System.err.println("    Only received " + args.length + 
					" arguments");
			System.err.println("Usage: java DNAdbase.java <command_file_name> "
					+ "<hash_file_name> <hash-table size> <memory_file_name>");
			
			// Exit with abnormal status
			java.lang.System.exit(-1);
		}
		
		// Extract arguments
		String commandFileName = args[0];
		String hashFileName = args[1];
		long hashTableSize = Long.parseLong(args[2]);
		String memoryFileName = args[3];
		
		// Create a new parser
	    DnaParse parser = new DnaParse(commandFileName, 
	    		hashFileName, hashTableSize, memoryFileName);
	    
	    // Run the parser on the given arguments...
	    DnaMain dMain = new DnaMain(commandFileName, hashFileName,
	    		hashTableSize, memoryFileName);
	    parser.parseMain(dMain);
	    
		// HashTableSize = Integer.parseInt(args[2]) * 32; 
	}

}
