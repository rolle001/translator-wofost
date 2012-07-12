package org.agmip.translators.wofost;

import java.util.Map;

import org.agmip.util.MapUtil;

public class WofostOutputController {
	
	private WofostOutputSoil wofostOutputSoil = new WofostOutputSoil();
	private WofostOutputSite wofostOutputSite = new WofostOutputSite();
	private WofostOutputWeather wofostOutputWeather = new WofostOutputWeather();
	
	    public void writeFiles(String filePath, Map input) {
	        
	    	String ExpName = MapUtil.getValueOr(input, "exname", "default");
	    	
	    	wofostOutputSoil.writeFile(filePath, input);
	    	
	    	wofostOutputWeather.writeFile(filePath, input);
	    	
	    	wofostOutputSite.soilFileName = wofostOutputSoil.soilFileName;
	    	wofostOutputSite.ExpName = ExpName;
	    	wofostOutputSite.writeFile(filePath, input);
	    	
	    }
}
