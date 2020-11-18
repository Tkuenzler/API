package Telmed;

public class Submitted {
	public static final String NOT_SUBMITTED = "0";
	public static final String SUBMITTED = "1";
	public static final String ENROLLED = "-1";
	
	public static String GetStatus(String submitted) {
		switch(submitted) {
			case NOT_SUBMITTED: 
				return "Not Submitted";
			case SUBMITTED: 
				return "Submitted";
			case ENROLLED: 
				return "Enrolled";
			default:
				return "UNKNOWN/INVALID VALUE";
		}
	}
}
