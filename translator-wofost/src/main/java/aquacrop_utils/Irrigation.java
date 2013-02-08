package aquacrop_utils;

import java.util.ArrayList;
import java.util.List;

//import org.agmip.translators.aquacrop.tools.DateFunctions;
//import org.agmip.translators.aquacrop.tools.IrrigationFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Creates irrigation data usable as input for the AquaCrop model, from
 * AgMIP management event data. The specified management events are
 * validated. 
 * 
 * @author Rob Knapen, Alterra Wageningen-UR, The Netherlands
 */
public class Irrigation {

    private static final Logger LOG = LoggerFactory.getLogger(Irrigation.class);	
	
	private String name;
	private String method;
	private int percSoilSurfaceWetted;
	private String iropCode;
	private int irrigationMethod;
	private long startDayNumber;

	private List<IrrigationEvent> items = new ArrayList<IrrigationEvent>();
	
	
	/**
	 * Creates an instance based on the specified list of events. The
	 * instance will be returned, or null when the events could not be
	 * parsed into a valid irrigation instance.
	 * 
	 * @param events to use to populate the instance
	 * @return created instance or null
	 */
	public static Irrigation create(List<ManagementEvent> events) {
		Irrigation obj = new Irrigation();
		if (obj.from(events)) {
			return obj;
		} else {
			return null;
		}
	}
	
	
	public boolean from(List<ManagementEvent> managementEvents) {
        items.clear();
        ManagementEvent startEvent = null;
        iropCode = null;
        irrigationMethod = -1;
        startDayNumber = -1;
        int errorCount = 0;
        
        for (ManagementEvent event : managementEvents) {
        	// check for planting / sowing event -> keep date as reference
        	if (event instanceof PlantingEvent) {
        		if (startEvent != null) {
        			LOG.error("Multiple planting events within a single experiment can currently not be handled by the translator! Further rotations will be ignored.");
        			errorCount++;
        			break;
        		}
        		startEvent = event;
        		startDayNumber = DateFunctions.calculateDayNumber(event.getDate(), false); 
        	}
        	
        	// check for irrigation event -> create IrrigationEvent data from it
        	if ((startEvent != null) && (event instanceof IrrigationEvent)) {
        		IrrigationEvent irrEvent = (IrrigationEvent)event;
        		if (irrEvent.getIrrigationMethod() == -1) {
        			LOG.error("Irrigation method used in experiment that is not supported by the AquaCrop model. The irrigation event will be ignored.");
        			errorCount++;
        			continue;
        		}
        		
        		if (irrigationMethod == -1) {
        			irrigationMethod = irrEvent.getIrrigationMethod();
        			iropCode = irrEvent.getIropCode();
        		} else if (irrigationMethod != irrEvent.getIrrigationMethod()) {
        			LOG.error("Different types of irrigation events within a single experiment. This can currently not be handled by the translator! The irrigation event will be ignored.");
        			errorCount++;
        			continue;
        		}
        		
        		long dayNr = DateFunctions.calculateDayNumber(irrEvent.getDate(), false);
        		irrEvent.setNumberOfDaysAfterSowingOrPlanting((int)(dayNr - startDayNumber));
            	items.add(irrEvent);
        	}
        	
        	// check for harvesting event -> stop processing, rotations not supported (yet)
        	if ((startEvent != null) && (event instanceof HarvestingEvent)) {
    			LOG.warn("Harvesting event in experiment. In case of crop rotations these will be ignored since the current translator does not handle them.");
        	}
        }
        
        if (irrigationMethod == -1) {
        	LOG.info("No irrigation used in experiment.");
        } else {
        	IrrigationFunctions.IrrigationLookupEntry info = new IrrigationFunctions().lookUp(iropCode);
        	if (info != null) {
	        	percSoilSurfaceWetted = info.soilSurfaceWettedPerc;
	        	method = info.description + " (" + info.units + ")";
	        	name = info.agmipCode + " - " + method;
        	} else {
        		percSoilSurfaceWetted = 100;
        		method = "Unknow irrigation method";
        		name = "Udefined - " + method;
        	}
        }
        
        return (errorCount == 0);
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getMethod() {
		return method;
	}


	public void setMethod(String method) {
		this.method = method;
	}


	public int getPercSoilSurfaceWetted() {
		return percSoilSurfaceWetted;
	}


	public void setPercSoilSurfaceWetted(int percSoilSurfaceWetted) {
		this.percSoilSurfaceWetted = percSoilSurfaceWetted;
	}


	public int getIrrigationMethod() {
		return irrigationMethod;
	}


	public void setIrrigationMethod(int irrigationMethod) {
		this.irrigationMethod = irrigationMethod;
	}


	public List<IrrigationEvent> getItems() {
		return items;
	}
	
}
