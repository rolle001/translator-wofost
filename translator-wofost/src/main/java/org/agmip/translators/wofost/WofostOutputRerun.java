package org.agmip.translators.wofost;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class WofostOutputRerun extends WofostOutput {
	
	public void writeFile(String filePath, Map input) {
		// TODO map all variables of input file with values in input map (json string)
		    Section = "Rerun";
			Velocity.init();        
			VelocityContext context = new VelocityContext();
			
			String rerunFileName = String.format("%srr.rer", expName);
			
			context.put( "FILENAME", String.format("RUNIO\\%s", rerunFileName));
			context.put( "DATE_TIME", new Date().toString());
			
			context.put( "RUNNAM", quotedStr(expName));
						
			// Write template.        
			Template template = Velocity.getTemplate(templatePath + "wofost_template.rer");        
			FileWriter F;        
			try {            
				F = new FileWriter(String.format("%s%s", filePath, rerunFileName));            
				template.merge( context, F );            
				F.close();                    
				} 
			catch (IOException ex) 
			{            
				      
			}        
	}
}
