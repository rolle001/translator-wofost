package org.agmip.translators.wofost;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.agmip.core.types.TranslatorOutput;
import org.agmip.translators.aquacrop.domain.ManagementEvent;

public abstract class WofostOutput implements TranslatorOutput {
	
	protected static String templatePath = "src\\main\\resources\\";
	
	static public String expName = "<default>";
	
	static public String soilFileName = "<none>";;
	static public String soilName = "<none>";;
	
	static public String cropFileName = "<none>";;
	static public String cropName = "<none>";;
	
	static public String timerFileName = "<none>";
	static public String siteFileName = "<none>";
	static public String rerunFileName = "<none>";
	static public String outputFileName = "<none>";
	
	static public String runoptFileName = "<default>";
	
	static List<String> errList = new ArrayList<String>();
	static String noValue = "<no value>";
	
	protected String Section = "default";
	
	public static List<String> errorList()
	{
		return errList;
	}
	
	protected String quotedStr(String aString) {
		return  String.format("'%s'", aString);
	}
	
	public void writeFile(String filePath, Map input, Map<Class, List<ManagementEvent>> eventMap) {
		// TODO Auto-generated method stub
	}
	
	public void writeFile(String filePath, Map input) {
		// TODO Auto-generated method stub
	}
	
	public static Integer calculateDayInYear(int day, int month, int year) 
	{
		Calendar _calendar = new GregorianCalendar();	
		_calendar.set(Calendar.YEAR, year);
		_calendar.set(Calendar.MONTH, month - 1);
		_calendar.set(Calendar.DAY_OF_MONTH, day);
		return _calendar.get(Calendar.DAY_OF_YEAR);
	}
	
	public static Integer getDayInYear(String sDate)
	{
		if (sDate.length() != 8)
			return -1;
		
		int d = Integer.parseInt(sDate.substring(6,8));
		int m = Integer.parseInt(sDate.substring(4,6));
		int y = Integer.parseInt(sDate.substring(0,4));
		
		return calculateDayInYear(d, m, y);
		
	}

	protected String getValue(HashMap<String, String> aMap, String aVarName, String defaultValue, boolean mustExist)
	{
		String result = aMap.get(aVarName);
		
		if (result == null) {
			if (mustExist)
			{
				errList.add(String.format("%s: parameter %s not found.", Section, aVarName));
			}
			result = defaultValue;
		}
		
		return result;
	}
	
	protected String ReplaceIllegalChars(String aName)
	{
		//todo: check illegal chars for mac and unix
		aName.replace("/", "_"); 
		aName.replace("\\", "_"); 
		aName.replace(":", "_"); 
		aName.replace("*", "_"); 
		aName.replace("?", "_"); 
		aName.replace(">", "_"); 
		aName.replace("<", "_"); 
		aName.replace("|", "_"); 
		aName.replace(" ", "_"); 
		
		return aName;
	}
	
	protected String getClimateFileName(String wstID, String stationNumber)
	{
		String result = String.format("%s%s.", wstID, stationNumber);
		return ReplaceIllegalChars(result);
	}
	
//	protected String getSoilFileName(String soilID)
//	{
//		String result = String.format("%s.sol", soilID);
//		return ReplaceIllegalChars(result);
//	}
	
	protected String getRunIniFileName(String aExpName, String aExpDirName)
	{
		return (aExpDirName + ReplaceIllegalChars(aExpName) + ".ini");
	}
	
	protected String getRunOptFileName(String aPath)
	{
		return String.format("%s%s", aPath, "runopt.dat");
	}
	
	protected String getSoilFileName(String soilID)
	{
		return ( ReplaceIllegalChars(soilID) + ".sol");
	}
	
	protected String getSiteFileName(String aExpName)
	{
		return ( ReplaceIllegalChars(aExpName) + ".sit");
	}
	
	protected String getSiteFilePath(String aExpName, String aPath)
	{
		return (aPath + getSiteFileName(aExpName));
	}
	
	protected String getTimerFileName(String aExpName)
	{
		return ( ReplaceIllegalChars(aExpName) + ".tim");
	}
	
	protected String getTimerFilePath(String aExpName, String aPath)
	{
		return (aPath + getTimerFileName(aExpName));
	}
	
	protected String getOutputFileName(String aExpName, boolean inclExt)
	{
		String result =  ReplaceIllegalChars(aExpName);
		if (inclExt)
			result += ".out";
		
		return result;
	}
}
