package org.agmip.translators.wofost;

import java.io.BufferedWriter;
import org.agmip.acmo.translators.AcmoTranslator;
import org.apache.commons.collections.KeyValue;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public class WofostACMO implements AcmoTranslator {
	
	static String tagExpName = "EXNAME";
	static String tagModelVerName = "MODEL_VER";
	static String modelVersion = "7.1.3";
	static String YR_tag = "** YR";
	
	ArrayList<Map.Entry<String, Integer>> colNames = new ArrayList<Map.Entry<String, Integer>>();
	
	private int getColIndexOf(String aTag)
	{
		for (int i = 0; i < colNames.size(); i++)
		{
			Map.Entry<String, Integer> pair = colNames.get(i);
			if (pair.getKey().equals(aTag))
				return pair.getValue();
		}
		
		return -1;
		
	}
	
	
	private BufferedWriter getBufferedWriter(String fileName)
	{
		FileOutputStream fstream;
		
		try {
			fstream = new FileOutputStream(fileName);
			DataOutputStream out = new DataOutputStream(fstream);
			return new BufferedWriter(new OutputStreamWriter(out));	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}		
	}

	private BufferedReader getBufferedReader(String fileName)
	{
		FileInputStream fstream;
		
		try {
			fstream = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(fstream);
			return new BufferedReader(new InputStreamReader(in));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	

	private int getColumnNr(String[] str, String tag)
	{
		for (int i = 0; i < str.length; i++)
		{
			if (str[i].equals(tag))
				return i;
		}	
		return -1;
	}
	
	private int getColumnNames(String[] str)
	{	
		boolean bFound = false;
		int result = -1;
		
		int i;
		// find first column tag
		for (i = 0; i < str.length; i++)
		{
			if (str[i].equals(tagModelVerName))
			{
				bFound = true;
				result = i;
				break;			
			}
		}
		
		if (bFound)
		{
			// get all columNames to fill
			for (int t = i; t < str.length; t++)
			{
				Entry<String, Integer> pair = new AbstractMap.SimpleEntry<String, Integer>(str[t],t);
				colNames.add(pair);
			}
		}
		
		return result;
	}
	
	
/*	
 *  MODEL_VER,
 * 	HWAH_S,
 * 	CWAH_S,
 * 	ADAT_S,
 * 	MDAT_S,
 * 	HADAT_S,
 * 	LAIX_S,
 * 	PRCP_S,
 * 	ETCP_S,
 * 	NUCM_S,
 * 	NLCM_S
 */
		
	private String[] readSummaryOutputFile(String outputFileName, String[] str_in)
	{
				
		ArrayList<Map.Entry<String, String>> summaryValues = new ArrayList<Map.Entry<String, String>>();
		
		String[] str_out;
		str_out = new String[str_in.length];
		
		for (int i = 0; i < str_in.length; i++)
			str_out[i] = str_in[i];
		
		str_out[getColIndexOf(tagModelVerName)] = modelVersion; 
		
		
		BufferedReader br = getBufferedReader(outputFileName);
		boolean bFound = false;
		if (br != null)
		{
			try 
			{
				String line_tag = null;
				line_tag = br.readLine();
			
				// skip comments
				while (line_tag != null)
				{
					if (line_tag.indexOf(YR_tag) >= 0)
					{
						bFound = true;
						break;
					}
					line_tag = br.readLine();
				}
				
				if (bFound)
				{
					String line_values = null;
					try {
						line_values = br.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					// check length of both lines: length line_tag should be length line_values +1				
					String[] strArray_tags = line_tag.trim().replaceAll(" +" , " ").split(" ");
					String[] strArray_values = line_values.trim().replaceAll(" +" , " ").split(" ");
					
					if (strArray_tags.length != strArray_values.length + 1)
					{
						System.out.println(String.format("invalid number of values in summary file %s", outputFileName));
						return str_out;
					}
					
					
					for (int i = 1; i < strArray_tags.length; i++)
					{
						Map.Entry<String, String> pair = new AbstractMap.SimpleEntry<String, String>(strArray_tags[i], strArray_values[i-1]);
						summaryValues.add(pair);
					}
					
					
				}
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}

		}
				
		return str_out;
	}
	
	
	private String getOutPutFileName(String expName, String SourceFolder)
	{
		if (expName.isEmpty())
			return null;
		else
		{
			String fName = String.format("%s\\runs\\%s\\output\\%s.wps",  SourceFolder, expName, expName);
			return String.format("%s\\runs\\%s\\output\\%s.pps",  SourceFolder, expName, expName);		
		}
		
	}
	
	@Override    
	public File execute(String sourceFolder, String destFolder) {
		
		char seperator = ',';
		char quotechar = '"';
		
		// try to find input file
		String acmoFileNameIn = sourceFolder + "\\ACMO_meta.dat";
		BufferedReader br = getBufferedReader(acmoFileNameIn);
		CSVReader cr = new CSVReader(br, seperator, quotechar);
		
		// open output file
		String acmoFileNameOut = destFolder + "\\ACMO_meta.csv";
		BufferedWriter bw = getBufferedWriter(acmoFileNameOut);	
		CSVWriter cw = new CSVWriter(bw, seperator, quotechar);
		
		try {
			// first 3 rows are headers, just copy these lines
			String[] str_in = cr.readNext();
			cw.writeNext(str_in);
			str_in  = cr.readNext();
			cw.writeNext(str_in);
			str_in  = cr.readNext();
			cw.writeNext(str_in);
			
			// get nr of the column of the experiment name
			int colNrExeName = getColumnNr(str_in, tagExpName);
			if (colNrExeName < 0)
			{
				System.out.println(String.format("ACMO base file (%s) is missing required columm %s", acmoFileNameIn, tagExpName));
				return null;
			}
			
			// get required column names. Modelversion is first one.
			if (getColumnNames(str_in) < 0) 
			{
				System.out.println(String.format("ACMO base file (%s) is missing required columm %s", acmoFileNameIn, tagModelVerName));
				return null;
			}
						
			// loop over all expriments
			str_in = cr.readNext();
			while (str_in != null)
			{
				
				String expOutputFileName = getOutPutFileName(str_in[colNrExeName], sourceFolder);
				String[] str_out = readSummaryOutputFile(expOutputFileName, str_in);
				
				
				cw.writeNext(str_out);
				System.out.println(expOutputFileName);
				
				str_in = cr.readNext();
				
			}
			
			bw.close();
			br.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		return null;
	}
	
}
