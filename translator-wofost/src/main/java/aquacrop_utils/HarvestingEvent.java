package aquacrop_utils;

import java.util.Map;


@SuppressWarnings("rawtypes")
public class HarvestingEvent extends ManagementEvent {

	public static HarvestingEvent create(Map data) {
		HarvestingEvent obj = new HarvestingEvent();
		obj.from(data);
		return obj;
	}
	
	
	public void from(Map data) {
		super.from(data);
		// TODO: fill custom fields
	}
	
}
