package org.agmip.translators.wofost;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class WofostOutputRunopt extends WofostOutput {
	
	public void writeFile(String filePath, Map input) {
		// TODO map all variables of input file with values in input map (json string)
		    Section = "Runopt";
			Velocity.init();        
			VelocityContext context = new VelocityContext();
			
			//outputFileName = String.format("%s.out", expName);
			
			String fName = getRunOptFileName(filePath);
			
			context.put( "FILENAME", fName);
			
			context.put( "DATE_TIME", new Date().toString());
			
			context.put( "TIMFIL", getTimerFileName(expName));
			context.put( "SITFIL", getSiteFileName(expName));
			context.put( "WOFOUT", getOutputFileName(expName, true));
			context.put( "FIXNAM", getOutputFileName(expName, false));
			
			// Write template.        
			Template template = Velocity.getTemplate(templatePath + "wofost_template.wcc");        
			FileWriter F;        
			try {            
				F = new FileWriter(fName);            
				template.merge( context, F );            
				F.close();                    
			} 
			catch (FileNotFoundException e) {
				System.out.println("file not found");
			} catch (IOException e) {
				System.out.println("IO error");
			}         
	}

}
