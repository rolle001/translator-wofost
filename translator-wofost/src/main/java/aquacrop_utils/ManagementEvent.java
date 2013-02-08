package aquacrop_utils;

import java.util.Map;


//import org.agmip.translators.aquacrop.tools.DateFunctions;
//import org.agmip.translators.aquacrop.tools.AgMIPFunctions;


@SuppressWarnings({"rawtypes"})
public class ManagementEvent {

	private String event;
	private String date;
	private int[] dayMonthYear;

	
	public void from(Map data) {
		event = AgMIPFunctions.getValueFor(data, "Unknown", "event");
		date = AgMIPFunctions.getValueFor(data, "190101", "date");
		dayMonthYear = DateFunctions.decodeDateString(date);
	}
	
	
	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}


	public int[] getDayMonthYear() {
		return dayMonthYear;
	}


	public void setDayMonthYear(int[] dayMonthYear) {
		this.dayMonthYear = dayMonthYear;
	}

}
