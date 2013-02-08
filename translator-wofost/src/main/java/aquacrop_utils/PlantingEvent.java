package aquacrop_utils;

import java.util.Map;

import org.agmip.util.MapUtil;


@SuppressWarnings("rawtypes")
public class PlantingEvent extends ManagementEvent {

/*
  "event" : "planting",
  "dssat_cul_id" : "GH0010",
  "crid" : "MAZ",
  "plma" : "S",
  "plpop" : "3.5",
  "apsim_cul_id" : "AZ422852_89",
  "cul_id" : "GH0010",
  "date" : "19810318",
  "plrs" : "75",
  "pldp" : "5",
  "cul_name" : "OBATAMPA"

	aquacrop_cul_id
 */
	
	private String cropId; // crid
	private String cultivarName; // cul_name
	private String aquaCropCultivarId; // aquacrop_cul_id
	
	
	public static PlantingEvent create(Map data) {
		PlantingEvent obj = new PlantingEvent();
		obj.from(data);
		return obj;
	}

	
	public void from(Map data) {
		super.from(data);
		cropId = MapUtil.getValueOr(data, "crid", "Undefined");
		aquaCropCultivarId = MapUtil.getValueOr(data, "aquaCrop_cul_id", "Undefined");
		cultivarName = MapUtil.getValueOr(data, "cul_name", "Undefined");
	}


	@Override
	public String toString() {
		return "PlantingEvent [cropId=" + cropId + ", cultivarName="
				+ cultivarName + ", aquaCropCultivarId=" + aquaCropCultivarId
				+ "]";
	}
	
}
