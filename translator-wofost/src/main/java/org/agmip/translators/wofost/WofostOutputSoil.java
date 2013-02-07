package org.agmip.translators.wofost;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.agmip.util.MapUtil;
import org.agmip.util.MapUtil.BucketEntry;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class WofostOutputSoil extends WofostOutput {
	
	//public String soilFileName = "";
	//public String soilName = "";;

	public void writeFile(String filePath, Map input) {
		
		// TODO map all variables of input file with values in input map (json string)
		Section = "Soil";
		Velocity.init();        
		
		// get all soils.        
		ArrayList<BucketEntry> soils = MapUtil.getPackageContents(input, "soils");

		for (BucketEntry soil: soils)
		{	
			VelocityContext context = new VelocityContext();
			
			String soilName = MapUtil.getValueOr(soil.getValues(), "soil_id", "default_name");
			String soilFileName = getSoilFileName(soilName);
			
			context.put( "SOLNAM", quotedStr(soilName));
			context.put( "ID", MapUtil.getValueOr(soil.getValues(), "soil_id", ""));  
			context.put( "CLASSIFICATION", MapUtil.getValueOr(soil.getValues(), "classification", ""));
			context.put( "NOTES", MapUtil.getValueOr(soil.getValues(), "sl_notes", ""));
			context.put( "SOILSITE", MapUtil.getValueOr(soil.getValues(), "soil_site", ""));        
			context.put( "SOURCE", MapUtil.getValueOr(soil.getValues(), "sl_source", ""));        
			
			context.put( "SLWP", MapUtil.getValueOr(soil.getValues(), "slwp", "-9.999"));        
			context.put( "SLSAT", MapUtil.getValueOr(soil.getValues(), "slsat", "-9.999"));        
			
			
			// Write template.        
			Template template = Velocity.getTemplate(templatePath + "wofost_template.sol");        
			FileWriter F;        
			try {              
				F = new FileWriter(String.format("%s%s", filePath, soilFileName));            
				template.merge( context, F );            
				F.close();                    
				} 
			catch (IOException ex) 
			{            
				      
			}          
		}		
	}

}
