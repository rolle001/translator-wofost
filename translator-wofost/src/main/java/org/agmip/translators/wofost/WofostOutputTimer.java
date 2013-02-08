package org.agmip.translators.wofost;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import aquacrop_utils.ManagementEvent;
import aquacrop_utils.PlantingEvent;

public class WofostOutputTimer extends WofostOutput {
	
	public void writeFile(String filePath, Map _input, Map eventMap) 
	{
			Velocity.init();        
			VelocityContext context = new VelocityContext();
			HashMap<String, String> input = (HashMap<String, String>) _input;
			
			String timerFilePath = getTimerFilePath(expName, filePath);
			
			String expDate = getValue(input, "sdat", noValue, true, Section, expName);
			String expYear = noValue;
			String expDay = noValue;
			  
			if (!expDate.equals(noValue))
			{
				expYear = expDate.substring(0, 4);
				expDay =  getDayInYear(expDate).toString();	
			}
			else
			{
				//todo: error logging
			}
								
			context.put( "FILENAME",  timerFilePath);
			context.put( "DATE_TIME", new Date().toString());
			context.put( "RUNNAM", ReplaceIllegalChars(expName).substring(0, 6));			
					
			String wstID = getValue(input, "wst_id", noValue, true, Section, expName);
			context.put( "CNTR", wstID);
			
			context.put( "ISYR", expYear);
			
			// not in icasa list should come from DOME
			String crfile = getValue(input, "wofost_crfile", noValue, true, Section, expName);
			context.put( "CRFILE", crfile);
						
			String idem = noValue;
			List<ManagementEvent> plantingList = (List<ManagementEvent>) eventMap.get(PlantingEvent.class);
			if(plantingList.size() > 0)
			{
				PlantingEvent planting = (PlantingEvent) plantingList.iterator().next();
				idem = getDayInYear(planting.getDate()).toString();
			}
			
			if(idem.equals(noValue))
			{
				// todo do error logging
				//throw new NullPointerException ("no or several planting event found ");
			}
						
			context.put( "IDEM", idem);
			
			String clmnam = getValue(input, "wth_notes", "weather from AgMIP experiment", false, Section, expName);
			context.put( "CLMNAM", clmnam);
			
			context.put( "ISDAY", expDay);
						
			// Write template.        
			Template template = Velocity.getTemplate(templatePath + "wofost_template.tim");        
			FileWriter F;        
			try {            
				F = new FileWriter(timerFilePath);            
				template.merge( context, F );            
				F.close();                    
				} 
			catch (IOException e) {
				System.out.println("IO error");
			}       
	}
}
