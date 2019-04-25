import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Extends abstract class Parse and provides parsing functionality 
 * specific to the tasks outlined by the DNAdbase project (project 4)
 * @author Joey Rodgers jdr14
 * @author Jovany Cabrera jovanyc4
 * @version 1.0.0
 */
public class DnaParse extends Parse
{
    /**
     * Constructor which calls parent (Parse) constructor internally
     * @param fileName (file name of a file that exists passed in as a string)
     */
	public DnaParse(String commandFileName) 
	{
		super(commandFileName);
	}

}
