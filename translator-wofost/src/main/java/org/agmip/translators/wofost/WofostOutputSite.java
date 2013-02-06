package org.agmip.translators.wofost;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class WofostOutputSite extends WofostOutput {
	
	public void writeFile(String filePath, Map input) {
		// TODO map all variables of input file with values in input map (json string)
    		errList.add("site");
    		Section = "Site";
    		
			Velocity.init();        
			VelocityContext context = new VelocityContext();
			
			siteFileName = String.format("%ssi.sit", expName);
			
			context.put( "FILENAME", String.format("RUNIO\\%s", siteFileName));
			context.put( "DATE_TIME", new Date().toString());
			
			context.put( "SOFILE", quotedStr(soilFileName));
			context.put( "RUNNAM", quotedStr(expName));
			
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
