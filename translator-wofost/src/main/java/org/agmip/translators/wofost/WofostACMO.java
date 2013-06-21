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
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.text.DateFormatter;

public class WofostACMO implements AcmoTranslator {
	
	static String tagExpName = "EXNAME";
	static String tagModelVerName = "MODEL_VER";
	static String modelVersion = "7.1.6";
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
	
	private String getValueOf(String tag, ArrayList<Map.Entry<String, String>> values)
	{
		for (int i = 0; i < values.size(); i++)
		{
			Map.Entry<String, String> pair = values.get(i);
			if (pair.getKey().equals(tag))
				return pair.getValue();
		}
		return null;
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
	

	private String getDateFromDayNumber(String dayNr, String year)
	{
		
		String string = String.format("%s-01-01", year);
		
		Date date = null;
		Number nrOfdays = 0;
		NumberFormat format = NumberFormat.getInstance(Locale.US);
		try {
			nrOfdays = format.parse(dayNr);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		try {
			date = new SimpleDateFormat("yyyy-mm-dd", Locale.US).parse(string);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_YEAR, nrOfdays.intValue() - 1);
			date = cal.getTime();
			SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
			return myFormat.format(date);
			
			
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
		
	}
	
	/*	
	 *  MODEL_VER	=	7.1.3,
	 * 	HWAH_S		=	TWSO,
	 * 	CWAH_S		=	TAGP,
	 * 	ADAT_S		=	FLWR (daynumber to date)
	 * 	MDAT_S		=	HALT (is maturity, daynumber to date),
	 * 	HADAT_S		=	not available (yet),
	 * 	LAIX_S		=	LAIM,
	 * 	PRCP_S		=	RAINT (convert from cm to mm),
	 * 	ETCP_S		=	TRANSP + EVSOL (convert from cm to mm),
	 * 	NUCM_S		=	not available (yet),
	 * 	NLCM_S		=	not available (yet),
	 */

	private String[] writeValues(ArrayList<Map.Entry<String, String>> summaryValues, String[] _str_in)
	{
		
		NumberFormat format = NumberFormat.getInstance(Locale.US);
		
		String[] result;
		result = new String[_str_in.length];
		
		for (int i = 0; i < _str_in.length; i++)
			result[i] = _str_in[i];
		
		result[getColIndexOf(tagModelVerName)] = modelVersion; 
		
		int index = getColIndexOf("HWAH_S");
		String value = getValueOf("TWSO", summaryValues);
		if (index >= 0)
			result[index] = value;
		
		index = getColIndexOf("CWAH_S");
		value = getValueOf("TAGP", summaryValues);
		if (index >= 0)
			result[index] = value;
		
		String valueYR = getValueOf("YR", summaryValues);
		index = getColIndexOf("ADAT_S");
		value = getValueOf("FLWR", summaryValues);
		value = getDateFromDayNumber(value, valueYR);
		result[index] = value;
		
		index = getColIndexOf("MDAT_S");
		value = getValueOf("HALT", summaryValues);
		value = getDateFromDayNumber(value, valueYR);
		result[index] = value;

		index = getColIndexOf("PRCP_S");
		value = getValueOf("RAINT", summaryValues);
		if (value != null)
		{
			try {
				Number number = format.parse(value);
				value = format.format(number.floatValue() * 10);
				result[index] = value;
			}
			catch (Exception e) {
				e.printStackTrace();
			}			
		}
		
		index = getColIndexOf("ETCP_S");
		String value1 = getValueOf("TRANSP", summaryValues);
		String value2 = getValueOf("EVSOL", summaryValues);
		try {

			Number number1 = format.parse(value1);
			Number number2 = format.parse(value2);
			value = format.format((number1.floatValue() + number2.floatValue()) * 10.0);
			result[index] = value;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
			
		return result;
		
	}
	
	private ArrayList<Map.Entry<String, String>> readSummaryOutputFile(String outputFileName)
	{				
		ArrayList<Map.Entry<String, String>> summaryValues = new ArrayList<Map.Entry<String, String>>();
				
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
					String line_values = br.readLine();
					
							
					// trim, remove extra spaces and split into words
					String[] strArray_tags = line_tag.trim().replaceAll(" +" , " ").split(" ");
					String[] strArray_values = line_values.trim().replaceAll(" +" , " ").split(" ");
					
					// check length of both lines: length line_tag should be length line_values +1		
					if (strArray_tags.length != strArray_values.length + 1)
					{
						System.out.println(String.format("invalid number of values in summary file %s", outputFileName));
						return null;
					}
					
					
					// store as string string-values pairs
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
				
		return summaryValues;
	}
	
	
	private boolean fileExists(String fName)
	{
		File f = new File(fName);
		File ff = new File(f.getAbsolutePath());
		return ff.exists();
	}
	
	private String getOutPutFileName(String expName, String SourceFolder)
	{
		if ( (expName.isEmpty()) || (SourceFolder.isEmpty()) )
			return null;
		else
		{		
			String fName = String.format("%s\\runs\\%s\\output\\%s.wps",  SourceFolder, expName, expName);
			if (!fileExists(fName))
			{
				fName = String.format("%s\\runs\\%s\\output\\%s.pps",  SourceFolder, expName, expName);
				if (!fileExists(fName))
					return null;
			}
			return fName;	
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
				if (expOutputFileName != null)
				{
					ArrayList<Map.Entry<String, String>> summaryValues = readSummaryOutputFile(expOutputFileName);
					String[] str_out = writeValues(summaryValues, str_in);
					
					cw.writeNext(str_out);
					System.out.println(expOutputFileName);	
				}
						
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
