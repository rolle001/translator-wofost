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
	
	private VelocityContext context = new VelocityContext();
	
	private void getPutVariable(HashMap<String, String> aMap, String varName, String tag)
	{
		String aVal = getValue(aMap, varName, noValue, true);
		context.put(tag, aVal);
	}
	
	public void writeFile(String filePath, Map _input) {
		// TODO map all variables of input file with values in input map (json string)
    		Section = "Site";
    		HashMap<String, String> input = (HashMap<String, String>) _input;
			Velocity.init();        
					
			String siteFilePath = getSiteFilePath(expName, filePath);
			
			context.put( "FILENAME", siteFilePath);
			context.put( "DATE_TIME", new Date().toString());
			
			String soilID = getValue(input, "soil_id", noValue, true);
			String soilFileName = getSoilFileName(soilID);
			context.put( "SOFILE", soilFileName);
			
			// not in icasa list should come from DOME
			getPutVariable(input, "wofost_ssmax", "SSMAX");
			getPutVariable(input, "wofost_ssi", "SSI");
			getPutVariable(input, "wofost_wav", "WAV");
			getPutVariable(input, "wofost_rdmsol", "RDMSOL");
			getPutVariable(input, "wofost_notinf", "NOTINF");
			getPutVariable(input, "wofost_nbase", "NBASE");
			getPutVariable(input, "wofost_nrec", "NREC");
			getPutVariable(input, "wofost_pbase", "PBASE");
			getPutVariable(input, "wofost_prec", "PREC");
			getPutVariable(input, "wofost_kbase", "KBASE");
			getPutVariable(input, "wofost_krec", "KREC");
			getPutVariable(input, "wofost_smlim", "SMLIM");
					
			// Write template.        
			Template template = Velocity.getTemplate(templatePath + "wofost_template.sit");        
			FileWriter F;        
			try {            
				F = new FileWriter(siteFilePath);            
				template.merge( context, F );            
				F.close();                    
				} 
			catch (IOException ex) 
			{            
				System.out.println("IO error");   
			}        
	}
			

}
