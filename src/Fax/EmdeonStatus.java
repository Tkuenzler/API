package Fax;

public class EmdeonStatus {
	public static final String FOUND = "Found";
	public static final String NOT_FOUND = "Not Found";
	public static final String NOT_COVERED = "Patient Is Not Covered";
	public static final String NOT_ACTIVE = "Not active";
	public static final String PBM_NOT_PARTICIPATE = "PBM NOT PARTICIPATE";
	public static final String CONNECTION_ISSUES = "Connecton issues";
	public static final String CONNECTION_ISSUES2 = "Connection Issues";
	public static final String REJECT_UNKNOWN = "Reject Unknown";
	public static final String PART_D_NOT_ACTIVE = "Part D Not Active";
	
	public static final String PAYOR_DOWN = "Payor Is Down";
	public static final String WRONG_FIRST_NAME = "WRONG FIRST NAME";
	public static final String INCOMPLETE_RECORD = "Record Incomplete";
	public static final String LAST_NAME_TOO_LONG = "Last Name to long";
	public static final String FIRST_NAME_TOO_LONG = "First Name too long";
	public static final String INVALID_DOB = "Invalid DOB";
	public static final String NAME_TOO_LONG = "NAME TOO LONG";
	public static final String NO_DATA_TO_DISPLAY = "No data to display";
	public static final String TIMED_OUT = "TIMED OUT";
	
	public static String[] ALL = {FOUND,NOT_FOUND,NOT_COVERED,NOT_ACTIVE,PBM_NOT_PARTICIPATE,CONNECTION_ISSUES,CONNECTION_ISSUES2,REJECT_UNKNOWN,
			WRONG_FIRST_NAME,INCOMPLETE_RECORD,LAST_NAME_TOO_LONG,FIRST_NAME_TOO_LONG,INVALID_DOB,NAME_TOO_LONG,NO_DATA_TO_DISPLAY,TIMED_OUT,PAYOR_DOWN};
	public static String[] NOT_FOUND_STATUS = {NOT_FOUND,NOT_COVERED,NOT_ACTIVE,PBM_NOT_PARTICIPATE,CONNECTION_ISSUES,CONNECTION_ISSUES2,REJECT_UNKNOWN,TIMED_OUT,PAYOR_DOWN};
	public static String[] INVALID_INFO = {WRONG_FIRST_NAME,INCOMPLETE_RECORD,NO_DATA_TO_DISPLAY,NAME_TOO_LONG,INVALID_DOB,LAST_NAME_TOO_LONG,FIRST_NAME_TOO_LONG,TIMED_OUT};
	
	public static String GetNotFoundStatus() {
		StringBuilder query = new StringBuilder();
		for(int i = 0;i<NOT_FOUND_STATUS.length;i++) {
			if(i==0)
				query.append(" (`EMDEON_STATUS` =  '"+NOT_FOUND_STATUS[i]+"' OR ");
			else if(i<=NOT_FOUND_STATUS.length-2)
				query.append("`EMDEON_STATUS` =  '"+NOT_FOUND_STATUS[i]+"' OR ");
			else if(i==NOT_FOUND_STATUS.length-1)
				query.append("`EMDEON_STATUS` = '"+NOT_FOUND_STATUS[i]+"')");
		}
		return query.toString();
	}

	public static String GetInvalidInfo() {
		StringBuilder query = new StringBuilder();
		for(int i = 0;i<INVALID_INFO.length;i++) {
			if(i==0)
				query.append(" (`EMDEON_STATUS` =  '"+INVALID_INFO[i]+"' OR ");
			else if(i<=INVALID_INFO.length-2)
				query.append("`EMDEON_STATUS` =  '"+INVALID_INFO[i]+"' OR ");
			else if(i==INVALID_INFO.length-1)
				query.append("`EMDEON_STATUS` = '"+INVALID_INFO[i]+"')");
		}
		return query.toString();
	}
	public static boolean IsValidStatus(String staus) {
		for(String s: ALL) {
			if(s.equalsIgnoreCase(staus))
				return true;
		}
		return false;
	}
	
	public static boolean IsNotFoundStatus(String staus) {
		for(String s: NOT_FOUND_STATUS) {
			if(s.equalsIgnoreCase(staus))
				return true;
		}
		for(String s: INVALID_INFO) {
			if(s.equalsIgnoreCase(staus))
				return true;
		}
		return false;
	}

}
