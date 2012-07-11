package org.agmip.translators.wofost;

import java.util.Map;

public class WofostOutputController {
	
	private WofostOutput[] outputs = {
	        new WofostOutputSoil(),
	        new WofostOutputWeather()};
	    
	    public void writeFiles(String arg0, Map result) {
	        
	        for (int i = 0; i < outputs.length; i++) {
	            outputs[i].writeFile(arg0, result);
	        }
	    }
}
