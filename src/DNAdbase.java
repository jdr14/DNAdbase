public class DNAdbase 
{

	public DNAdbase() 
	{
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) 
	{
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
	    parser.parseMain();
	    
		// HashTableSize = Integer.parseInt(args[2]) * 32; 
	}

}
