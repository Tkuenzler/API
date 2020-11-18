package Database.Columns;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;
import JSONParameters.BlueMosiacParameters;
import JSONParameters.TriageParameters;


public class TelmedColumns {
	public static final String FIRST_NAME = "first_name";
	public static final String LAST_NAME = "last_name";
	public static final String DOB = "dob";
	public static final String ADDRESS = "address";
	public static final String CITY = "city";
	public static final String STATE = "state";
	public static final String ZIP = "zip";
	public static final String PHONE = "phonenumber";
	public static final String GENDER = "gender";
	public static final String CARRIER = "carrier";
	public static final String POLICY_ID = "policy_id";
	public static final String BIN = "BIN";
	public static final String GRP = "GRP";
	public static final String PCN = "PCN";
	public static final String AGENT = "agent";
	public static final String CALL_CENTER = "CALL_CENTER";
	public static final String SOURCE = "SOURCE";
	public static final String IP = "ip";
	public static final String DATE_ADDED = "DATE_ADDED";
	public static final String DATE_MODIFIED = "DATE_MODIFIED";
	public static final String LAST_UPDATED = "LAST_UPDATED";
	public static final String TELMED_ID = "TELMED_ID";
	public static final String TELMED_STATUS = "TELMED_STATUS";
	public static final String TELMED_COMPANY = "TELMED_COMPANY";
	public static final String BILLING_STATUS = "BILLING_STATUS";
	public static final String PHARMACY = "PHARMACY";
	public static final String TRIAGE = "TRIAGE";
	public static final String SUBMITTED = "SUBMITTED";
	public static final String MEDICATIONS = "MEDICATIONS";
	public static final String NPI = "NPI";
	public static final String PRODUCT_SUGGESTIONS = "PRODUCT_SUGGESTIONS";
	public static final String NOTES = "NOTES";
	
	public static final String[] ADD_TO_DATABASE_COLUMNS = {FIRST_NAME,LAST_NAME,DOB,ADDRESS,CITY,STATE,ZIP,GENDER,PHONE,CARRIER,POLICY_ID,BIN,GRP,PCN,AGENT,CALL_CENTER,
			SOURCE,IP,DATE_ADDED,TELMED_ID,TELMED_STATUS,PHARMACY,TRIAGE,NPI}; 
	public static final String[] ConvertJSON(JSONObject obj,String callCenter,String ip,String status) throws JSONException {
		String[] params = new String[ADD_TO_DATABASE_COLUMNS.length];
		for(int i = 0;i<ADD_TO_DATABASE_COLUMNS.length;i++) {
			switch(ADD_TO_DATABASE_COLUMNS[i]) {
				case FIRST_NAME:
					params[i] = obj.getString(TriageParameters.FIRST_NAME);
					break;
				case LAST_NAME:
					params[i] = obj.getString(TriageParameters.LAST_NAME);
					break;
				case DOB:
					params[i] = obj.getString(TriageParameters.DOB);
					break;
				case ADDRESS:
					params[i] = obj.getString(TriageParameters.ADDRESS);
					break;
				case CITY:
					params[i] = obj.getString(TriageParameters.CITY);
					break;
				case STATE:
					params[i] = obj.getString(TriageParameters.STATE);
					break;
				case ZIP:
					params[i] = obj.getString(TriageParameters.ZIP);
					break;
				case GENDER:
					params[i] = obj.getString(TriageParameters.GENDER);		
					break;
				case PHONE:
					params[i] = obj.getString(TriageParameters.PHONE);
					break;
				case CARRIER:
					params[i] = obj.getString(TriageParameters.CARRIER);
					break;
				case POLICY_ID:
					params[i] = obj.getString(TriageParameters.POLICY_ID);
					break;
				case BIN:
					params[i] = obj.getString(TriageParameters.BIN);
					break;
				case GRP:
					params[i] = obj.getString(TriageParameters.GRP);
					break;
				case PCN:
					params[i] = obj.getString(TriageParameters.PCN);
					break;
				case AGENT:
					params[i] = obj.getString(TriageParameters.AGENT);
					break;
				case CALL_CENTER:
					params[i] = callCenter;
					break;
				case SOURCE:
					params[i] = obj.getString(TriageParameters.SOURCE);
					break;
				case IP:
					params[i] = ip;
					break;
				case DATE_ADDED:
					params[i] = getCurrentDate("yyyy-MM-dd");
					break;
				case TELMED_ID:
					params[i] = "";
					break;
				case TELMED_STATUS:
					params[i] = status;
					break;
				case PHARMACY:
					params[i] = obj.getString(TriageParameters.PHARMACY);
					break;
				case TRIAGE:
					params[i] = obj.toString();
					break;
				case NPI:
					params[i] = obj.getString(TriageParameters.NPI);
					break;
			}
		}
		return params;
	}
	private static String getCurrentDate(String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format); 
		Date date = new Date(); 
		return formatter.format(date);
	}
}
