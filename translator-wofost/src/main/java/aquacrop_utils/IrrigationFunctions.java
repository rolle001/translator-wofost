package aquacrop_utils;

import java.util.HashMap;
import java.util.Map;

public class IrrigationFunctions {

	// lookup table
	private Map<String, IrrigationLookupEntry> lookup = new HashMap<String, IrrigationLookupEntry>(); 
	
	// lookup entry class
	public class IrrigationLookupEntry {
		public String agmipCode;
		public String description;
		public String units;
		public int aquaCropCode;
		public int soilSurfaceWettedPerc;
		
		public IrrigationLookupEntry(String agmipCode, String description, String units, int aquaCropCode, int soilSurfaceWettedPerc) {
			this.agmipCode = agmipCode;
			this.description = description;
			this.units = units;
			this.aquaCropCode = aquaCropCode;
			this.soilSurfaceWettedPerc = soilSurfaceWettedPerc;
		}
	}

	
	public IrrigationFunctions() {
		// set up the lookup table
		lookup.put("IR001", new IrrigationLookupEntry("IR001", "Furrow", "mm", 4, 60));
		lookup.put("IR002", new IrrigationLookupEntry("IR002", "Alternating furrows", "mm", 4, 40));
		lookup.put("IR003", new IrrigationLookupEntry("IR003", "Flood", "mm", 2, 100));
		lookup.put("IR004", new IrrigationLookupEntry("IR004", "Sprinkler", "mm", 1, 100));
		lookup.put("IR005", new IrrigationLookupEntry("IR005", "Drip or trickle", "mm", 5, 30));
		// methods currently not supported by AquaCrop
		// IR006, Flood depth, mm, NA
		// IR007, Water table depth, mm, NA
		// IR008, Percolation rate, mm day-1, NA
		// IR009, Bund height, mm, NA
		// IR010, Puddling (for Rice only), NA
		// IR011, Constant flood depth, mm, NA	
	}

	
	public IrrigationLookupEntry lookUp(String iropCode) {
		return lookup.get(iropCode);
	}
	
	
	public int lookUpAquaCropIrrigationCode(String iropCode) {
		IrrigationLookupEntry entry = lookup.get(iropCode);
		if (entry != null) {
			return entry.aquaCropCode;
		}
		return -1;
	}
	
	
	public int lookUpAquaCropSoilSurfaceWetted(String iropCode) {
		IrrigationLookupEntry entry = lookup.get(iropCode);
		if (entry != null) {
			return entry.soilSurfaceWettedPerc;
		}
		return -1;
	}
	
}
