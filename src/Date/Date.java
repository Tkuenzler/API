package Date;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Date {
	
	public static String getCurrentDate(String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format); 
		java.util.Date date = new java.util.Date(); 
		return formatter.format(date);
	}
	public static boolean isBefore(String time,int offset) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"); 
		String date = getCurrentDate("yyyy/MM/dd");
		String dateTime = date+" "+time;
		LocalDateTime comparisonTime = LocalDateTime.parse(dateTime, dtf);
		LocalDateTime currentTime = LocalDateTime.now().minusHours(offset);	
		if(currentTime.isBefore(comparisonTime))
			return true;
		else
			return false;
		
	}
	public static boolean isAfter(String time,int offset) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"); 
		String date = getCurrentDate("yyyy/MM/dd");
		String dateTime = date+" "+time;
		LocalDateTime comparisonTime = LocalDateTime.parse(dateTime, dtf);
		LocalDateTime currentTime = LocalDateTime.now().minusHours(offset);	
		if(currentTime.isAfter(comparisonTime))
			return true;
		else
			return false;
		
	}
}
