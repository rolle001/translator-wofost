package org.agmip.translators.wofost;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.agmip.util.MapUtil;
import org.agmip.util.MapUtil.BucketEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WofostOutputWeather extends WofostOutput {
	
	// todo: check default values in case of missing values
	
    public void writeFile(String filePath, Map input) {
        // Write your file out here.
    	Section = "Weathers";
    	Logger Log = LoggerFactory.getLogger(WofostOutputWeather.class);
    	
    	NumberFormat nf = NumberFormat.getInstance(Locale.US);
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);
		nf.setGroupingUsed(false);
		
    	// get all weather stations
    	ArrayList<BucketEntry> weatherStations = MapUtil.getPackageContents(input, "weathers");
  	
		String WCCFormat = "2";      // CABO format
		String stationNumber = "1";
		
		for (BucketEntry weatherStation: weatherStations)
		{
	    	HashMap<String, String> weatherDataValues = weatherStation.getValues();
			 	
	    	String wstID = getValue(weatherDataValues, "wst_id", noValue, true, Section, noValue);
	    	String climateName = getValue(weatherDataValues, "wst_name",noValue, false, Section, wstID);
	    	String wthNotes = getValue(weatherDataValues, "wth_notes", noValue, false, Section, wstID);
	    	
	    	String climateFileName = getClimateFileName(wstID, stationNumber);	    
	    	
	    	String country = getValue(weatherDataValues, "wst_loc_1", noValue, false, Section, wstID);
	    	
	    	String lat  = getValue(weatherDataValues, "wst_lat", noValue, true, Section, wstID);
			String lon  = getValue(weatherDataValues, "wst_long", "-99", false, Section, wstID);
			String elev = getValue(weatherDataValues, "wst_elev", "-99", false, Section, wstID);
	    	
	    	ArrayList<HashMap<String, String>> daily = weatherStation.getDataList();
	    	
	    	String year = "";
	    	BufferedWriter bw = null;
	    	DataOutputStream out = null;
	    	try 
	    	{	
		    	for (HashMap<String, String> dailyData : daily  )
		    	{
		    		String date = getValue(dailyData, "w_date", noValue, true, Section, wstID);
		    		if ((date == noValue) || (date.length() != 8))
		    			continue;
		    		
		    		String _year = String.copyValueOf(date.toCharArray(), 0, 4);
			    	String year2 = String.copyValueOf(date.toCharArray(), 1, 3);
			    	String month = String.copyValueOf(date.toCharArray(), 4, 2);
			    	String day   = String.copyValueOf(date.toCharArray(), 6, 2);    	
		    			    		
		    		if (!year.equals(_year))
		    		{
		    			if (bw != null)
		    			{
		    				bw.close();
		    				out.close();
		    			}
		    			
		    			year = _year;
		    			
		    			String fName = filePath + climateFileName + year2;
		    			
	    				FileOutputStream fstream = new FileOutputStream(fName);
	    				out = new DataOutputStream(fstream);
	    				bw = new BufferedWriter(new OutputStreamWriter(out));		
	    					
	    				bw.write("*---------------------------------------------------------*\n");
	    				bw.write(String.format("*   Station: %s\n", climateName));
	    				bw.write(String.format("*   Country: %s\n", country));
	    				bw.write(String.format("*      Year: %s\n", year));
	    				
	    				if (!wthNotes.equals(noValue))
	    					bw.write(String.format("*    Notes: %s\n", wthNotes));
   				
	    				bw.write("*\n");
	    				bw.write(String.format("* Longitude:%s\n", lon));
	    				bw.write(String.format("*  Latitude: %s\n", lat));
	    				bw.write(String.format("* Elevation: %s m.\n", elev));
	    				bw.write("*\n");
	    				bw.write("*    Source: AgMIP database\n");
	    				bw.write("*    Author: AgMIP translator\n");
	    				bw.write("*  Comments: automatic generated input file.\n");		
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
	    				bw.write(String.format("** WCCDESCRIPTION=%s\n", climateName));
	    				bw.write(String.format("** WCCFORMAT=2\n", WCCFormat));  													
	    				bw.write(String.format("** WCCYEARNR=%4s\n", year));
	    				bw.write("*---------------------------------------------------------*\n");
	    				
	    				bw.write(String.format("   %s %s %s 0.00 0.00\n", lon, lat, elev));
		    		}
		    		
		    		String irra = getValue(dailyData, "srad", noValue, true, Section, wstID);
		    		
		    		if (irra != noValue)
		    		{
		    			double firra= Float.parseFloat(irra);
		    			firra = firra * 1000.0;  // units conversion from MJ/m2/d -> KJ/m2/d
		    			irra = nf.format(firra);	
		    		}
		    				    			
		    		String tmin = getValue(dailyData, "tmin", noValue, true, Section, wstID);
		    		String tmax = getValue(dailyData, "tmax", noValue, true, Section, wstID);
		    		String vap  = getValue(dailyData, "vprsd", noValue, true, Section, wstID);
		    		String wind = getValue(dailyData, "wind", noValue, true, Section, wstID);
		    		String prec = getValue(dailyData, "rain", noValue, true, Section, wstID);
		    		
		    		int iYear = Integer.parseInt(year);
		    		int iMonth = Integer.parseInt(month);
		    		int iDay = Integer.parseInt(day);
		    		    	
		    		Integer dayNr = calculateDayInYear(iDay, iMonth, iYear);
		    		
		    		bw.write(String.format("   %s %4s %3d %8s %5s %5s %7s %5s %5s\n", stationNumber, year, dayNr, irra, tmin, tmax, vap, wind, prec));
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
    
}
