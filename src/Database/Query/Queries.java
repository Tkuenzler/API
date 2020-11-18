package Database.Query;
import Database.Columns.DMEColumns;
import Database.Columns.LeadColumns;
import DoctorChase.ConfirmDoctor;
import DoctorChase.DoctorAnswer;
import DoctorChase.FaxStatus;
import DoctorChase.MessageStatus;
import DoctorChase.Received;
import DoctorChase.Used;

public class Queries {
	private static String WITH_ANSWERING_MACHINE = "(("+LeadColumns.DOCTOR_ANSWER+" = "+DoctorAnswer.DOCTOR_ANSWER+" OR "+LeadColumns.DOCTOR_ANSWER+" = "+DoctorAnswer.DEFAULT+") OR ("+LeadColumns.DOCTOR_ANSWER+" = "+DoctorAnswer.NO_ONE_ANSWERED+" AND "
			+ ""+LeadColumns.LAST_UPDATED+" < DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL - 30 MINUTE)))";
	
	private static String WRONG_OR_BLANK_FAX = "("+LeadColumns.FAX_DISPOSITION+" = '' OR "+LeadColumns.FAX_DISPOSITION+" = '"+FaxStatus.WRONG_FAX+"')";
	
	private static String ADDED_WITHIN_ONE_YEAR = LeadColumns.DATE_ADDED+" > DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL - 1 YEAR)";
	
	public static class Select {
		public static final String RECEIVED_NO_RESPONSE = "("+ADDED_WITHIN_ONE_YEAR+" AND "+LeadColumns.USED+" = "+Used.NOT_USED+" AND "+WRONG_OR_BLANK_FAX+" AND "+WITH_ANSWERING_MACHINE+" AND "
									+LeadColumns.MESSAGE_STATUS+" = '"+MessageStatus.SENT+"' AND "+LeadColumns.RECEIVED_DATE+" <= DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL - 5 DAY) AND "+LeadColumns.RECEIVED+" = "+Received.RECEIVED+" AND "+LeadColumns.CONFIRM_DOCTOR+" = "+ConfirmDoctor.CONFIRMED;
		
		public static final String CONFIRMED_NOT_RECEIVED = "("+ADDED_WITHIN_ONE_YEAR+" AND "+LeadColumns.USED+" = "+Used.NOT_USED+" AND "+WITH_ANSWERING_MACHINE+" AND "
				+LeadColumns.MESSAGE_STATUS+" = '"+MessageStatus.SENT+"' AND "+LeadColumns.RECEIVED+" = "+Received.NOT_RECEIVED+" AND "+WITH_ANSWERING_MACHINE+" AND "+LeadColumns.CONFIRM_DOCTOR+" = "+ConfirmDoctor.CONFIRMED+")";
		
		
		public static final String INVALID_FAX = "("+ADDED_WITHIN_ONE_YEAR+" AND "+LeadColumns.USED+" = "+Used.NOT_USED+" AND "+LeadColumns.RECEIVED+" = "+Received.NOT_RECEIVED+" AND "+WITH_ANSWERING_MACHINE+" AND "+WRONG_OR_BLANK_FAX
				+" AND "+LeadColumns.CONFIRM_DOCTOR+" = "+ConfirmDoctor.NOT_CONFIRMED+" AND CHAR_LENGTH("+LeadColumns.DR_FAX+") != 10)";
		
		public static final String NOT_CONFIRMED = "("+ADDED_WITHIN_ONE_YEAR+" AND "+LeadColumns.USED+" = "+Used.NOT_USED+" AND "+LeadColumns.RECEIVED+" = "+Received.NOT_RECEIVED+" AND "+WITH_ANSWERING_MACHINE+" AND "+WRONG_OR_BLANK_FAX
				+" AND "+LeadColumns.CONFIRM_DOCTOR+" = "+ConfirmDoctor.NOT_CONFIRMED+")";
			
	}
	public static class SelectDME {
		public static final String DME_NOT_CONFIRMED = "("+DMEColumns.USED+" = "+Used.NOT_USED+" AND "+DMEColumns.RECEIVED+" = "+Received.NOT_RECEIVED+" AND "+WITH_ANSWERING_MACHINE+" AND "+WRONG_OR_BLANK_FAX
				+" AND "+DMEColumns.CONFIRM_DOCTOR+" = "+ConfirmDoctor.NOT_CONFIRMED+")";
		
		public static final String DME_CONFIRMED_NO_RESPONSE = "("+DMEColumns.USED+" = "+Used.NOT_USED+" AND "+WITH_ANSWERING_MACHINE+" AND "+WRONG_OR_BLANK_FAX
				+" AND "+DMEColumns.CONFIRM_DOCTOR+" = "+ConfirmDoctor.CONFIRMED+" AND "+DMEColumns.FAX_SENT_DATE+" <=   DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL - 5 DAY))";
	
		public static final String DME_NOT_CONFIRMED_WITH_POLICY_ID = "("+DMEColumns.USED+" = "+Used.NOT_USED+" AND "+DMEColumns.RECEIVED+" = "+Received.NOT_RECEIVED+" AND "+WITH_ANSWERING_MACHINE+" AND "+WRONG_OR_BLANK_FAX
				+" AND "+DMEColumns.CONFIRM_DOCTOR+" = "+ConfirmDoctor.NOT_CONFIRMED+" AND CHAR_LENGTH("+DMEColumns.POLICY_ID+") = 11 )";
	}
}
