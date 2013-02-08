package aquacrop_utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateFunctions {

	private static final double ELAPSED_DAYS[] = { 0.0, 31.0, 59.25, 90.25,
			120.25, 151.25, 181.25, 212.25, 243.25, 273.25, 304.25, 334.25 };
	
	private static final Calendar _calendar = new GregorianCalendar();

	
	/**
	 * Splits the specified dateString into day, month and year values.
	 * 
	 * @param dateString to split
	 * @return int[] with day, month and year value (in that order)
	 */
	public static int[] decodeDateString(String dateString) {
		int dayMonthYear[] = new int[]{1, 1, 1901};
        dayMonthYear[2] = Integer.valueOf(dateString.substring(0, 4));
        dayMonthYear[1] = Integer.valueOf(dateString.substring(4, 6));
        dayMonthYear[0] = Integer.valueOf(dateString.substring(6, 8));
		return dayMonthYear;
	}
	
	
	public static int calculateDayInYear(String dateString, boolean checked) {
		int[] dmy = decodeDateString(dateString);
		return calculateDayInYear(dmy[0], dmy[1], dmy[2], checked);
	}
	
	
	public static int calculateDayInYear(int day, int month, int year, boolean checked) {
		if (checked) {
			checkDayNumberInput(day, month, year);
		}
		
		_calendar.set(Calendar.YEAR, year);
		_calendar.set(Calendar.MONTH, month - 1);
		_calendar.set(Calendar.DAY_OF_MONTH, day);
		
		return _calendar.get(Calendar.DAY_OF_YEAR);
	}
	
	
	public static long calculateDayNumber(String dateString, boolean checked) {
		int[] dmy = decodeDateString(dateString);
		return calculateDayNumber(dmy[0], dmy[1], dmy[2], checked);
	}
	
	
	/**
	 * AquaCrop uses day numbers to specify the start and end of the simulation
	 * period and of the growing cycle.
	 * 
	 * The day number refers to the days elapsed since 0th January 1901 at 0 am.
	 * The method is valid from 1901 to 2099.
	 * 
	 * @param day in month (starts at 1)
	 * @param month of year (starts at 1)
	 * @param year
	 * @return calculated day number for AquaCrop
	 */
	public static long calculateDayNumber(int day, int month, int year, boolean checked) {
		if (checked) {
			checkDayNumberInput(day, month, year);
		}
		return (long) ((year - 1901) * 365.25 + ELAPSED_DAYS[month - 1] + day + 0.05);
	}
	
	
	/**
	 * Checks wether the specified arguments for the day number calculation
	 * are valid. Throws an IllegalArgumentException when not.
	 *  
	 * @param day in month (starts at 1)
	 * @param month of year (starts at 1)
	 * @param year (must be between 1901 and 2099)
	 */
	public static void checkDayNumberInput(int day, int month, int year) {
		if ((year < 1901) || (year > 2099)) {
			throw new IllegalArgumentException("Year must be between 1901 and 2099");
		}
		if ((month < 1) || (month > 12)) {
			throw new IllegalArgumentException("Month must be between 1 and 12");
		}
		
		_calendar.set(Calendar.YEAR, year);
		_calendar.set(Calendar.MONTH, month - 1);
		int days = _calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		if ((day < 1) || (day > days)) {
			throw new IllegalArgumentException("Day must be between 1 and max days in month");
		}
	}
}
