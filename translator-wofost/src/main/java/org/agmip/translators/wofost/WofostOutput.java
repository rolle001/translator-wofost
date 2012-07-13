package org.agmip.translators.wofost;

import java.util.Map;

import org.agmip.core.types.TranslatorOutput;

public abstract class WofostOutput implements TranslatorOutput {
	
	protected static String templatePath = "src\\main\\resources\\";
	
	static public String expName = "<default>";
	
	static public String soilFileName = "<none>";;
	static public String soilName = "<none>";;
	
	static public String climateFileName = "<none>";;
	static public String climateName = "<none>";;
	
	static public String cropFileName = "<none>";;
	static public String cropName = "<none>";;
	
	static public String rainfallFileName = "<none>";
	static public String rainfallName = "<none>";
	
	static public String timerFileName = "<none>";
	static public String siteFileName = "<none>";
	static public String rerunFileName = "<none>";
	static public String outputFileName = "<none>";
	
	static public String runoptFileName = "<default>";
	
	
	protected String quotedStr(String aString) {
		return  String.format("'%s'", aString);
	}
	
	public void writeFile(String filePath, Map input) {
		// TODO Auto-generated method stub
	}

}
