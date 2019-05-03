import java.util.*;

import student.TestCase;

public class DnaDbaseTest extends TestCase
{

	public DnaDbaseTest() 
	{
		// TODO Auto-generated constructor stub
	}

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
}
