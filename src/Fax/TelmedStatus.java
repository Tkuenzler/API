package Fax;

public class TelmedStatus {
	//PENDING
	public static final String NEW = "New Patient";
	public static final String IN_PROCESS = "Verifications - In Process";
	public static final String FOLLOW_UP = "Verifications - Long Term Follow Up";
	//PAID
	public static final String XFER = "Telemed - Xfer to Doctor";
	public static final String DISQUALIFY = "Telemed - Disqualify";
	public static final String CONSULT_APPROVED = "Telemed - Consult Approved";
	public static final String APPROVED = "Telemed - Sent to Pharmacy";
	//FAILED
	public static final String UNABLE_TO_CONTACT = "Verifications - Unable to Contact";
	public static final String NOT_INTERESTED = "Verifications - Not Interested";
	public static final String REFUSED = "Telemed - Patient Refused";
	public static final String MEDICAL_DISQUALIFY = "Verifications - Medical Disqualify";
	public static final String DUPLICATE = "Verifications - Duplicate Record";
	
	public static final String[] SUCCESFUL = {XFER,DISQUALIFY,CONSULT_APPROVED,APPROVED};
	public static final String[] PENDING = {NEW,IN_PROCESS,FOLLOW_UP};
	public static final String[] FAILED = {UNABLE_TO_CONTACT,NOT_INTERESTED,REFUSED,MEDICAL_DISQUALIFY,DUPLICATE};
	public static final String[] CAN_ENROLL = {UNABLE_TO_CONTACT,NOT_INTERESTED,REFUSED,MEDICAL_DISQUALIFY,DISQUALIFY};
	
	public static String GetSuccesfulQuery() {
		return "(`TELMED_STATUS` = '"+XFER+"' OR `TELMED_STATUS` = '"+DISQUALIFY+"' OR `TELMED_STATUS` = '"+CONSULT_APPROVED+"' OR `TELMED_STATUS` = '"+APPROVED+"')";
	}
	public static String GetPendingQuery() {
		return "(`TELMED_STATUS` = '"+NEW+"' OR `TELMED_STATUS` = '"+IN_PROCESS+"' OR `TELMED_STATUS` = '"+FOLLOW_UP+"')";
	}
	public static String GetFailedQuery() {
		return "(`TELMED_STATUS` = '"+DUPLICATE+"' OR `TELMED_STATUS` = '"+UNABLE_TO_CONTACT+"' OR `TELMED_STATUS` = '"+NOT_INTERESTED+"' OR `TELMED_STATUS` = '"+REFUSED+"' OR `TELMED_STATUS` = '"+MEDICAL_DISQUALIFY+"')";
	}
	
	public static boolean IfCanEnroll(String status) {
		for(String s: CAN_ENROLL) {
			if(status.equalsIgnoreCase(s))
				return true;
		}
		return false;
	}
	public static boolean IfPaidStatus(String status) {
		for(String s: SUCCESFUL) {
			if(status.equalsIgnoreCase(s))
				return true;
		}
		return false;
	}
	public static boolean IfNotPaid(String status) {
		for(String s: PENDING) {
			if(status.equalsIgnoreCase(s))
				return true;
		}
		for(String s: FAILED) {
			if(status.equalsIgnoreCase(s))
				return true;
		}
		return false;
	}
	
	
}
