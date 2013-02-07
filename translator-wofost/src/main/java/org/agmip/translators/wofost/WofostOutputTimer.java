package org.agmip.translators.wofost;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.agmip.translators.aquacrop.domain.ManagementEvent;
import org.agmip.translators.aquacrop.domain.PlantingEvent;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class WofostOutputTimer extends WofostOutput {
	
	public void writeFile(String filePath, HashMap<String, String> input, Map<Class, List<ManagementEvent>> eventMap) 
	{
		// TODO map all variables of input file with values in input map (json string)
			Section = "Timer";
			Velocity.init();        
			VelocityContext context = new VelocityContext();
			
			timerFileName = String.format("%s.tim", expName);
			
			String expDate = getValue(input, "sdat", noValue, true);
			String expYear = noValue;
			String expDay = noValue;
			
			if (!expDate.equals(noValue))
			{
				expYear = expDate.substring(0, 4);
				expDay =  getDayInYear(expDate).toString();	
			}
								
			context.put( "FILENAME", String.format("%s%s", filePath, timerFileName));
			context.put( "DATE_TIME", new Date().toString());
			context.put( "RUNNAM", expName);			
						
			String wstID = getValue(input, "wst_id", noValue, true);
			if (wstID.equals(noValue))
				context.put( "CLFILE", noValue);
			else
				context.put( "CLFILE", String.format("%s1.", wstID));
			
			context.put( "ISYR", expYear);
			context.put( "CRFILE", cropFileName);
			
			String idem = null;
			List<ManagementEvent> plantingList = eventMap.get(PlantingEvent.class);
			if(plantingList.size()==1){
				
				PlantingEvent planting = (PlantingEvent) plantingList.iterator().next();
				idem = getDayInYear(planting.getDate()).toString();
			}
			if(idem.equalsIgnoreCase(null)){
				throw new NullPointerException ("no or several planting event found ");
			}
			
			
			
			context.put( "IDEM", idem);
			context.put( "CRPNAM", cropName);
			
			String soilID = getValue(input, "soil_id", noValue, true);
			context.put( "SOLNAM", soilID);
						
			context.put( "CLMNAM", wstID);
			context.put( "ISDAY", expDay);
						
			// Write template.        
			Template template = Velocity.getTemplate(templatePath + "wofost_template.tim");        
			FileWriter F;        
			try {            
				F = new FileWriter(String.format("%s%s", filePath, timerFileName));            
				template.merge( context, F );            
				F.close();                    
				} 
			catch (IOException ex) 
			{            
				      
			}        
	}
}
