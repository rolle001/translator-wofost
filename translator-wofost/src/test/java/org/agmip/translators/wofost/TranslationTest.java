package org.agmip.translators.wofost;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
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
      
        ModelOutput modelOutput = new ModelOutput();
        //JSONAdapter adapter = new JSONAdapter();
        String json = new String();
        
        System.out.println("start");
        FileInputStream fstream = new FileInputStream("D:\\UserData\\projecten\\AGMIP\\git\\translator-wofost\\translator-wofost\\src\\test\\resources\\ufga8201_mzx.json");
        DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		json = br.readLine();
        
		@SuppressWarnings("unchecked")
		Map <String, Object> theMap = JSONAdapter.fromJSON(json);
        
        modelOutput.writeFile("D:\\UserData\\projecten\\AGMIP\\git\\translator-wofost\\output\\",  theMap);
        
        System.out.println("finished");
    }
}
