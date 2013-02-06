package org.agmip.translators.wofost;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class WofostOutputSite extends WofostOutput {
	
	public void writeFile(String filePath, HashMap<String, String> input) {
		// TODO map all variables of input file with values in input map (json string)
    		Section = "Site";
    		
			Velocity.init();        
			VelocityContext context = new VelocityContext();
			
			siteFileName = String.format("%s.sit", expName);
			
			context.put( "FILENAME", String.format("%s%s", filePath, siteFileName));
			context.put( "DATE_TIME", new Date().toString());
			
			String soilID = getValue(input, "soil_id", noValue, true);
			soilFileName = String.format("%s.sol", soilID.replace(' ' ,'_'));
			context.put( "SOFILE", soilFileName);
			
			// Write template.        
			Template template = Velocity.getTemplate(templatePath + "wofost_template.sit");        
			FileWriter F;        
			try {            
				F = new FileWriter(String.format("%s%s", filePath, siteFileName));            
				template.merge( context, F );            
				F.close();                    
				} 
			catch (IOException ex) 
			{            
				      
			}        
	}
			

}
