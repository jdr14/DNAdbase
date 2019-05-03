import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import student.TestCase;

/**
 * 
 * @author Jovany Cabrera jovanyc4
 * @author Joseph Rodgers jdr14
 * @version 2.1.3
 *
 */
public class DnaDbaseTest extends TestCase
{

    /**
     * test function for pair
     */
    public void testPair()
    {
        Pair<Integer, String> p = new Pair<Integer, String>();
        Pair<Integer, String> p2 = new Pair<Integer, String>(2, "test");
        
        p.setKey(1);
        p.setValue("testing");
        
        int i = p.getKey();
        String j = p.getValue();
        
        assertEquals(i, 1);
        assertEquals(j, "testing");
        
        i = p2.getKey();
        j = p2.getValue();
        assertEquals(i, 2);
        assertEquals(j, "test");
    }
    
    /**
     * 
     */
    public void testMain()
    {
        // Create new print streams to temporarily redirect the out and err
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream oStream = new PrintStream(out);
        final ByteArrayOutputStream err = new ByteArrayOutputStream();
        final PrintStream eStream = new PrintStream(err);
        
        // Redirect System.out and System.err to the print stream created ^
        System.setOut(oStream);
        System.setErr(eStream);
        
        String[] args = new String[4];
        args[0] = "command_file.txt";
        args[1] = "hash_table.txt";
        args[2] = "64";
        args[3] = "mem_file";
        
        DNAdbase d = new DNAdbase();
        d.main(args);
        
        String[] output = out.toString().split("\\n");
        String actualOutput = "Sequence IDs:\n" + 
        		"AAAAA: hash slot [6]\n" + 
        		"Free Block List: none\n" + 
        		"Sequence IDs:\n" + 
        		"AAAAA: hash slot [6]\n" + 
        		"AAAA: hash slot [18]\n" + 
        		"Free Block List: none\n" + 
        		"Sequence IDs:\n" + 
        		"AAAAA: hash slot [6]\n" + 
        		"AAAA: hash slot [18]\n" + 
        		"AAA: hash slot [19]\n" + 
        		"Free Block List: none\n" + 
        		"Sequence IDs:\n" + 
        		"TTTTTTTTTTAAAAACCCA: hash slot [0]\n" + 
        		"AAAAA: hash slot [6]\n" + 
        		"AAAA: hash slot [18]\n" + 
        		"AAA: hash slot [19]\n" + 
        		"Free Block List: none\n" + 
        		"Sequence IDs:\n" + 
        		"TTTTTTTTTTAAAAACCCA: hash slot [0]\n" + 
        		"AAAAA: hash slot [6]\n" + 
        		"AAAA: hash slot [18]\n" + 
        		"AAA: hash slot [19]\n" + 
        		"TCATATCTATCCAAAAAAAA: hash slot [62]\n" + 
        		"Free Block List: none\n" + 
        		"Sequence IDs:\n" + 
        		"TTTTTTTTTTAAAAACCCA: hash slot [0]\n" + 
        		"AAAAA: hash slot [6]\n" + 
        		"AAAA: hash slot [18]\n" + 
        		"AAA: hash slot [19]\n" + 
        		"TCATATCTATCCAAAAAAAA: hash slot [62]\n" + 
        		"TCATATCTATCCAAAAAAA: hash slot [63]\n" + 
        		"Free Block List: none\n" + 
        		"Sequence IDs:\n" + 
        		"TTTTTTTTTTAAAAACCCA: hash slot [0]\n" + 
        		"AAAAA: hash slot [6]\n" + 
        		"AAAA: hash slot [18]\n" + 
        		"AAA: hash slot [19]\n" + 
        		"TCATATCTATCCAAAAAA: hash slot [32]\n" + 
        		"TCATATCTATCCAAAAAAAA: hash slot [62]\n" + 
        		"TCATATCTATCCAAAAAAA: hash slot [63]\n" + 
        		"Free Block List: none\n" + 
        		"Sequence Removed AAAAA:\n" + 
        		"AAAATTTTCCCCGGGGAAAACCCCGGGGTTTTAAAATTTT\n" + 
        		"Sequence Removed AAA:\n" + 
        		"AAAA\n" + 
        		"Sequence Removed AAAA:\n" + 
        		"AAAATTTTCCCCGGGGAAAACCCCGGGGTTTTAAAATTTT\n" + 
        		"Sequence IDs:\n" + 
        		"TTTTTTTTTTAAAAACCCA: hash slot [0]\n" + 
        		"TCATATCTATCCAAAAAA: hash slot [32]\n" + 
        		"TCATATCTATCCAAAAAAAA: hash slot [62]\n" + 
        		"TCATATCTATCCAAAAAAA: hash slot [63]\n" + 
        		"Free Block List:\n" + 
        		"[Block 1] Starting Byte Location: 0, Size 25 bytes\n" + 
        		"SequenceID AAAAA not found\n" + 
        		"Sequence Found: CCTTTTCCCCGGGGAAAACCCCGGGGTTTTAAAATTTT\n";
        String[] expectedOutput = actualOutput.split("\\n");
        
        // Test that all the output lines are as expected
        for (int i = 0; (i < expectedOutput.length && i < output.length); i++)
        {
        	System.out.println("i = " + i);
            assertEquals(output[i], expectedOutput[i]);
        } 
    }
    
    /**
     * 
     */
//    public void testInsert()
//    {
//    	
//    }
}
