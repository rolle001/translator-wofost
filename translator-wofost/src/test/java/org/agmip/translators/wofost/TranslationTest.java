package org.agmip.translators.wofost;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.agmip.util.JSONAdapter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class TranslationTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TranslationTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( TranslationTest.class );
    }

    /**
     * Rigourous Test :-)
     * @throws IOException 
     */
    public void testTranslation() throws IOException
    {
      
        System.out.println("start");
        
        //FileInputStream fstream = new FileInputStream("src\\test\\resources\\ufga8201_mzx.json");
        FileInputStream fstream = new FileInputStream("src\\test\\resources\\mach_full.json");
        DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String json = br.readLine();
        
		Map <String, Object> theMap = JSONAdapter.fromJSON(json);
        
		WofostOutputController wc = new WofostOutputController();
		String outputPath = "src\\main\\resources\\";
		wc.writeFiles(outputPath, theMap);
		
		System.out.println("finished");
        
    }
}
