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
			
			outputFileName = String.format("%s.out", expName);
			
			context.put( "FILENAME", String.format("%s%s", filePath, "runopt.dat"));
			
			context.put( "DATE_TIME", new Date().toString());
			
			context.put( "TIMFIL", quotedStr(expName + ".tim"));
			context.put( "SITFIL", quotedStr(expName + ".sit"));
			context.put( "WOFOUT", quotedStr(expName + ".out"));
			context.put( "FIXNAM", quotedStr(expName));
			
			// Write template.        
			Template template = Velocity.getTemplate(templatePath + "wofost_template.wcc");        
			FileWriter F;        
			try {            
				F = new FileWriter(String.format("%s%s", filePath, "runopt.dat"));            
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
