package org.agmip.translators.wofost;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.agmip.core.types.TranslatorOutput;
import org.agmip.util.MapUtil;
import org.agmip.util.MapUtil.BucketEntry;

public class ModelOutput implements TranslatorOutput {
	
    public void writeFile(String filePath, Map input) {
        // Write your file out here.
    	
    	
    	// assumed there is only one weather section 
    	BucketEntry weatherData = MapUtil.getBucket(input, "weather").get(0);
		String stationNumber = "1";
		String WCCFormat = "2";      // cabo format
		
    	LinkedHashMap<String, String> weatherDataValues = weatherData.getValues();
    	String stationName = weatherDataValues.get("wst_name").replace(",", "_");
    	String lat  = MapUtil.getValueOr(weatherDataValues, "wst_lat", "-99");
		String lon  = MapUtil.getValueOr(weatherDataValues, "wst_long", "-99");
		String elev = MapUtil.getValueOr(weatherDataValues, "elev", "-99");
    	  
    	ArrayList<LinkedHashMap<String, String>> daily = weatherData.getDataList();
    	
    	String year   = "";
    	Date date1  = null;
    	BufferedWriter bw = null;
    	DataOutputStream out = null;
    	Integer dayNr = 0;
    	DateFormat formatter = new SimpleDateFormat("yyyymmdd"); 
    	
    	try 
    	{
	    	for (LinkedHashMap<String, String> dailyData : daily  )
	    	{
	    		String date  = MapUtil.getValueOr(dailyData, "w_date", "19010101");
	    		String year1  = String.copyValueOf(date.toCharArray(), 0, 1);
	    		String year2  = String.copyValueOf(date.toCharArray(), 1, 3);
	    		String month  = String.copyValueOf(date.toCharArray(), 4, 2);
	    		String day    = String.copyValueOf(date.toCharArray(), 6, 2);    	
	    	
	    		if (!year.equals(year1+year2))
	    		{
	    			if (bw != null)
	    			{
	    				bw.close();
	    				out.close();
	    			}
	    			
	    			//date1 = (Date)formatter.parse(date);  
	    			
	    			String fName = filePath + stationName + year1 + "." + year2;
	    			year = year1 + year2;
	    			dayNr = 0;
	    			
    				FileOutputStream fstream = new FileOutputStream(fName);
    				out = new DataOutputStream(fstream);
    				bw = new BufferedWriter(new OutputStreamWriter(out));		
    					
    				bw.write("*---------------------------------------------------------*\n");
    				bw.write(String.format("*   Country: %s\n", ""));
    				bw.write(String.format("*   Station: %s\n", stationName));
    				bw.write(String.format("*      Year: %s\n", year));
    				bw.write("*    Source:\n");
    				bw.write("*\n");
    				bw.write("*    Author:\n");
    				bw.write(String.format("* Longitude:%s\n", lon));
    				bw.write(String.format("*  Latitude: %s\n", lat));
    				bw.write(String.format("* Elevation: %s m.\n", elev));
    				bw.write("*  Comments:\n");		
    				bw.write("*\n");
    				bw.write("*  Columns:\n");
    				bw.write("*  ========\n");
    				bw.write("*  station number\n");
    				bw.write("*  year\n");
    				bw.write("*  day\n");
    				bw.write("*  irradiation (kJ m-2 d-1)\n");
    				bw.write("*  minimum temperature (degrees Celsius)\n");
    				bw.write("*  maximum temperature (degrees Celsius)\n");
    				bw.write("*  vapour pressure (kPa)\n");
    				bw.write("*  mean wind speed (m s-1)\n");
    				bw.write("*  precipitation (mm d-1)\n");
    				bw.write(String.format("** WCCDESCRIPTION=%s\n", stationName));
    				bw.write(String.format("** WCCFORMAT=2\n", WCCFormat));  													
    				bw.write(String.format("** WCCYEARNR=%4s\n", year));
    				bw.write("*---------------------------------------------------------*\n");
    				bw.write("   5.67  51.97     7. 0.00 0.00	    	\n");
	    			
	    		}
	    		
	    		String irra = MapUtil.getValueOr(dailyData, "srad",  "-99");
	    		String tmin = MapUtil.getValueOr(dailyData, "tmin",  "-99");
	    		String tmax = MapUtil.getValueOr(dailyData, "tmax",  "-99");
	    		String vap  = MapUtil.getValueOr(dailyData, "vprsd", "-99");
	    		String wind = MapUtil.getValueOr(dailyData, "wind",  "-99");
	    		String prec = MapUtil.getValueOr(dailyData, "rain",  "-99");
	    		
	    		//Date date = (Date)formatter.parse(date);  
	    		dayNr++;
	    		 	    		
	    		//bw.write("1 2000   1   831.   0.1   8.2   0.833   1.0   0.9");
	    		bw.write(String.format("%s %4s %3d %7s %4s %5s %7s %5s %5s\n", stationNumber, year, dayNr, irra, tmin, tmax, vap, wind, prec));
	    	}
	    		

	    	if (bw != null)
			{
	    		bw.close();
	    		out.close();
			}
	    	
		} catch (FileNotFoundException e) {
			System.out.println("file not found");
		} catch (IOException e) {
			System.out.println("IO error");
		} 	
    	
    }
    
}
