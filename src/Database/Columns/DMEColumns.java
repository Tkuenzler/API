package Database.Columns;

import org.json.JSONException;
import org.json.JSONObject;
import Date.Date;
import DoctorChase.ConfirmDoctor;
import DoctorChase.Disposition;
import DoctorChase.DoctorAnswer;
import DoctorChase.Received;
import JSONParameters.DoctorChaseParameters;
import client.Record;

public class DMEColumns {
	public static final String FIRST_NAME = "first_name";
	public static final String LAST_NAME = "last_name";
	public static final String DOB = "dob";
	public static final String ADDRESS = "address";
	public static final String CITY = "city";
	public static final String STATE = "state";
	public static final String ZIP = "zip";
	public static final String PHONE = "phonenumber";
	public static final String GENDER = "gender";
	public static final String SSN = "ssn";
	public static final String CARRIER = "carrier";
	public static final String POLICY_ID = "policy_id";
	public static final String AGENT = "agent";
	public static final String SOURCE = "SOURCE";
	public static final String DATE_ADDED = "DATE_ADDED";
	public static final String FAX_SENT_DATE = "FAX_SENT_DATE";
	public static final String BRACES = "braces";
	public static final String ID = "_id";
	public static final String LAST_UPDATED = "LAST_UPDATED";
	public static final String NOTES = "NOTES";
	public static final String ATTENTION = "ATTENTION";
	public static final String MESSAGE_ID = "MESSAGE_ID";
	public static final String MESSAGE_STATUS = "MESSAGE_STATUS";
	public static final String USED = "USED";
	public static final String CONFIRM_DOCTOR = "CONFIRM_DOCTOR";
	public static final String DOCTOR_ANSWER = "DOCTOR_ANSWER";
	public static final String RECEIVED = "RECEIVED";
	public static final String FAX_DISPOSITION = "FAX_DISPOSITION";
	public static final String CHASE_COUNT = "CHASE_COUNT";
	public static final String DR_CHASE_AGENT = "DR_CHASE_AGENT";
	public static final String LAST_CHASE_DATE = "LAST_CHASE_DATE";
	public static final String RECEIVED_DATE = "RECEIVED_DATE";
	public static final String CALL_NOTES = "CALL_NOTES";
	//Doctor Columns
	public static final String NPI = "npi";
	public static final String DR_TYPE = "dr_type";
	public static final String DR_FIRST = "dr_first";
	public static final String DR_LAST = "dr_last";
	public static final String DR_ADDRESS = "dr_address";
	public static final String DR_CITY = "dr_city";
	public static final String DR_STATE = "dr_state";
	public static final String DR_ZIP = "dr_zip";
	public static final String DR_PHONE = "dr_phone";
	public static final String DR_FAX = "dr_fax";
	
	//Misc
	public static final String CALL_REFRENCE_CODE = "CALL_REFRENCE_CODE";
	public static final String SOS = "SOS";
	public static final String TRACKING_NUMBER = "TRACKING_NUMBER";
	
	public static final String[] FAX_CHASE = {NPI,DR_FIRST,DR_LAST,DR_ADDRESS,DR_CITY,DR_STATE,DR_ZIP,DR_PHONE,DR_FAX,NOTES,USED,DR_CHASE_AGENT,LAST_CHASE_DATE,CHASE_COUNT,FAX_DISPOSITION,CONFIRM_DOCTOR,RECEIVED,RECEIVED_DATE,CONFIRM_DOCTOR,DOCTOR_ANSWER};
	public static final String[] DR_COLUMNS = {NPI,DR_FIRST,DR_LAST,DR_ADDRESS,DR_CITY,DR_STATE,DR_ZIP,DR_PHONE,DR_FAX};
	public static String[] DrColumnParams(Record record) {
		String[] columns = new String[DR_COLUMNS.length];
		for(int i = 0;i<DR_COLUMNS.length;i++) {
			String column = DR_COLUMNS[i];
			switch(column) {
				case NPI: columns[i] = record.getNpi(); break;
				case DR_FIRST: columns[i] = record.getDrFirst(); break;
				case DR_LAST: columns[i] = record.getDrLast(); break;
				case DR_ADDRESS: columns[i] = record.getDrAddress(); break;
				case DR_CITY: columns[i] = record.getDrCity(); break;
				case DR_STATE: columns[i] = record.getDrState(); break;
				case DR_ZIP: columns[i] = record.getDrZip(); break;
				case DR_PHONE: columns[i] = record.getDrPhone(); break;
				case DR_FAX: columns[i] = record.getDrFax(); break;
			}
		}
		return columns;
	}
	public static String[] CreateDMEDrChaseArray(JSONObject obj,Record record) throws JSONException {
		String[] array = new String[DMEColumns.FAX_CHASE.length];
		String confirm = null,received = null,doctorAnswer = null,receivedDate = null;
		for(int i = 0;i<array.length;i++) {
			switch(DMEColumns.FAX_CHASE[i]) {
				case DMEColumns.NPI: array[i] = obj.getString(DoctorChaseParameters.NPI); break;
				case DMEColumns.DR_FIRST: array[i] = obj.getString(DoctorChaseParameters.DR_FIRST); break;
				case DMEColumns.DR_LAST: array[i] = obj.getString(DoctorChaseParameters.DR_LAST); break;
				case DMEColumns.DR_ADDRESS: array[i] = obj.getString(DoctorChaseParameters.DR_ADDRESS); break;
				case DMEColumns.DR_CITY: array[i] = obj.getString(DoctorChaseParameters.DR_CITY); break;
				case DMEColumns.DR_STATE: array[i] = obj.getString(DoctorChaseParameters.DR_STATE); break;
				case DMEColumns.DR_ZIP: array[i] = obj.getString(DoctorChaseParameters.DR_ZIP); break;
				case DMEColumns.DR_PHONE: array[i] = obj.getString(DoctorChaseParameters.DR_PHONE); break;
				case DMEColumns.DR_FAX: array[i] = obj.getString(DoctorChaseParameters.DR_FAX); break;
				case DMEColumns.NOTES: array[i] = obj.getString(DoctorChaseParameters.NOTES); break;
				case DMEColumns.USED: array[i] = "0"; break;
				case DMEColumns.DR_CHASE_AGENT:  array[i] = obj.getString(DoctorChaseParameters.AGENT); break;
				case DMEColumns.LAST_CHASE_DATE: array[i] = Date.getCurrentDate("yyyy-MM-dd"); break;
				case DMEColumns.CHASE_COUNT: array[i] = ""+obj.getInt(DoctorChaseParameters.CHASE_COUNT)+1; break;
				case DMEColumns.CONFIRM_DOCTOR: array[i] = confirm; break;
				case DMEColumns.RECEIVED: array[i] = received; break;
				case DMEColumns.RECEIVED_DATE: array[i] = receivedDate; break;
				case DMEColumns.DOCTOR_ANSWER: array[i] = doctorAnswer; break;
				case DMEColumns.FAX_DISPOSITION: {
					switch(obj.getString(DoctorChaseParameters.DISPOSITION)) {
						case Disposition.DECEASED:
						case Disposition.DENIED:
						case Disposition.NEEDS_TO_BE_SEEN:
						case Disposition.NOT_INTERESTED:
							array[i] = obj.getString(DoctorChaseParameters.DISPOSITION); 
							confirm = ConfirmDoctor.CONFIRMED; 
							received = Received.RECEIVED;
							receivedDate = Date.getCurrentDate("yyyy-MM-dd"); 
							doctorAnswer = ""+DoctorAnswer.DOCTOR_ANSWER;
							break;
						case Disposition.WRONG_DOCTOR: 
							array[i] = obj.getString(DoctorChaseParameters.DISPOSITION); 
							confirm = ConfirmDoctor.NOT_CONFIRMED; 
							received = Received.NOT_RECEIVED; 
							receivedDate = "0000-00-00"; 
							doctorAnswer = ""+DoctorAnswer.DOCTOR_ANSWER;
							break;
						case Disposition.NO_ANSWER:
							array[i] = ""; 
							confirm = ""+record.getConfirmed();
							received = ""+DoctorChaseParameters.GetIntegerFromYesNo(obj.getString(DoctorChaseParameters.RECEIVED)); 
							receivedDate = obj.getString(RECEIVED_DATE); 
							doctorAnswer = ""+DoctorAnswer.NO_ONE_ANSWERED;
							break;
						case Disposition.CONFIRMED_DOCTOR:  
							array[i] = ""; 
							confirm = ConfirmDoctor.CONFIRMED; 
							received = Received.NOT_RECEIVED; 
							receivedDate = "0000-00-00"; 
							doctorAnswer = ""+DoctorAnswer.DOCTOR_ANSWER;
							break;
						case Disposition.RECEIVED: 
							array[i] = ""; 
							confirm = ConfirmDoctor.CONFIRMED; 
							received = Received.RECEIVED; 
							receivedDate = Date.getCurrentDate("yyyy-MM-dd"); 
							doctorAnswer = ""+DoctorAnswer.DOCTOR_ANSWER;
							break;
						case Disposition.REFAX:
							array[i] = ""; 
							confirm = ConfirmDoctor.CONFIRMED; 
							received = Received.NOT_RECEIVED; 
							receivedDate = "0000-00-00"; 
							doctorAnswer = ""+DoctorAnswer.DOCTOR_ANSWER;
							break;
					}
				}
				break;
			}
		}
		return array;
	}
	public static final String[] ADD_TO_DATABASE_COLUMNS = {FIRST_NAME,LAST_NAME,DOB,ADDRESS,CITY,STATE,ZIP,GENDER,PHONE,CARRIER,POLICY_ID,AGENT,
			SOURCE,DATE_ADDED,NPI,DR_TYPE,DR_FIRST,DR_LAST,DR_ADDRESS,DR_CITY,DR_STATE,DR_ZIP,DR_PHONE,DR_FAX,BRACES,ID,NOTES,SSN,CALL_NOTES}; 
	public static final String[] ConverToStringArray(Record record) {
		String[] values = new String[ADD_TO_DATABASE_COLUMNS.length];
		for(int i = 0;i<ADD_TO_DATABASE_COLUMNS.length;i++) {
			String value = ADD_TO_DATABASE_COLUMNS[i];
			switch(value) {
				case FIRST_NAME:
					values[i] = record.getFirstName();
					break;
				case LAST_NAME:
					values[i] = record.getLastName();
					break;
				case DOB:
					values[i] = record.getDob();
					break;
				case SSN:
					values[i] = record.getSsn();
					break;
				case ADDRESS:
					values[i] = record.getAddress();
					break;
				case CITY:
					values[i] = record.getCity();
					break;
				case STATE:
					values[i] = record.getState();
					break;
				case ZIP:
					values[i] = record.getZip();
					break;
				case GENDER:
					values[i] = record.getGender();
					break;
				case PHONE:
					values[i] = record.getPhone();
					break;
				case CARRIER:
					values[i] = record.getCarrier();
					break;
				case POLICY_ID:
					values[i] = record.getPolicyId();
					break;
				case AGENT:
					values[i] = record.getAgent();
					break;
				case SOURCE:
					values[i] = record.getSource();
					break;
				case DATE_ADDED:
					values[i] = Date.getCurrentDate("yyyy-MM-dd");
					break;
				case NPI:
					values[i] = record.getNpi();
					break;
				case DR_TYPE:
					values[i] = record.getDrType();
					break;
				case DR_FIRST:
					values[i] = record.getDrFirst();
					break;
				case DR_LAST:
					values[i] = record.getDrLast();
					break;
				case DR_ADDRESS:
					values[i] = record.getDrAddress();
					break;
				case DR_CITY:
					values[i] = record.getDrCity();
					break;
				case DR_STATE:
					values[i] = record.getDrState();
					break;
				case DR_ZIP:
					values[i] = record.getDrZip();
					break;
				case DR_PHONE:
					values[i] = record.getDrPhone();
					break;
				case DR_FAX:
					values[i] = record.getDrFax();
					break;
				case BRACES:
					String braces = null;
					if(record.getBraceList()==null)
						braces = record.getPainLocation();
					else if(record.getBraceList().equalsIgnoreCase(""))
						braces = record.getPainLocation();
					else
						braces = record.getBraceList();
					values[i] = braces;
					break;
				case ID:
					values[i] = record.getId();
					break;
				case NOTES:
					values[i] = "";
					break;
				case CALL_NOTES:
					values[i] = record.getCallNotes();
					break;
			}
		}
		return values;
	}
}
