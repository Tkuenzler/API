package JSONParameters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import Database.Columns.DMEColumns;
import Database.Columns.LeadColumns;
import Date.Date;
import DoctorChase.ConfirmDoctor;
import DoctorChase.Disposition;
import DoctorChase.DoctorAnswer;
import DoctorChase.Received;
import DoctorChase.RecordType;
import Script.DoctorChaseScript;
import client.Record;

public class DoctorChaseParameters {
	public static final String NAME = "name";
	public static final String DOB = "dob";
	public static final String PHONE = "phone";
	public static final String ID = "id";
	public static final String PHARMACY = "pharmacy";
	public static final String RECORD_TYPE = "record_type";
	public static final String AGENT = "user";
	public static final String PAIN_CAUSE = "pain_cause";
	public static final String PAIN_LOCATION = "pain_location";
	public static final String BRACES = "braces";
	
	public static final String ADDRESS = "address";
	public static final String CITY = "city";
	public static final String STATE = "state";
	public static final String ZIP = "zip";
	
	public static final String NPI = "npi";
	public static final String DR_FIRST = "drFirstName";
	public static final String DR_LAST = "drLastName";
	public static final String DR_ADDRESS = "drAddress";
	public static final String DR_CITY = "drCity";
	public static final String DR_STATE = "drState";
	public static final String DR_ZIP = "drZip";
	public static final String DR_PHONE = "drPhone";
	public static final String DR_FAX = "drFax";
	public static final String CHASE_COUNT = "chase_count";
	public static final String OUR_FAX = "our_fax";
	
	public static final String LEAD_TYPE = "lead_type";
	public static final String CONFIRMED = "confirmed";
	public static final String RECEIVED = "received";
	public static final String RECEIVED_DATE = "received_date";
	public static final String TABLE = "table";
	public static final String NOTES = "notes";
	public static final String DISPOSITION = "disposition";
	public static final String SCRIPT = "script";
	public static final String ATTENTION = "attention";

	public static String[] CreateRXDrChaseArray(JSONObject obj,Record record) throws JSONException {
		String[] array = new String[LeadColumns.FAX_CHASE.length];
		String confirm = null,received = null,doctorAnswer = null,receivedDate = null;
		for(int i = 0;i<array.length;i++) {
			switch(LeadColumns.FAX_CHASE[i]) {
				case LeadColumns.NPI: array[i] = obj.getString(NPI); break;
				case LeadColumns.DR_FIRST: array[i] = obj.getString(DR_FIRST); break;
				case LeadColumns.DR_LAST: array[i] = obj.getString(DR_LAST); break;
				case LeadColumns.DR_ADDRESS1: array[i] = obj.getString(DR_ADDRESS); break;
				case LeadColumns.DR_CITY: array[i] = obj.getString(DR_CITY); break;
				case LeadColumns.DR_STATE: array[i] = obj.getString(DR_STATE); break;
				case LeadColumns.DR_ZIP: array[i] = obj.getString(DR_ZIP); break;
				case LeadColumns.DR_PHONE: array[i] = obj.getString(DR_PHONE); break;
				case LeadColumns.DR_FAX: array[i] = obj.getString(DR_FAX); break;
				case LeadColumns.NOTES: array[i] = obj.getString(NOTES); break;
				case LeadColumns.USED: array[i] = "0"; break;
				case LeadColumns.DR_CHASE_AGENT:  array[i] = obj.getString(AGENT); break;
				case LeadColumns.LAST_CHASE_DATE: array[i] = Date.getCurrentDate("yyyy-MM-dd"); break;
				case LeadColumns.CHASE_COUNT: array[i] = ""+obj.getInt(CHASE_COUNT)+1; break;
				case LeadColumns.CONFIRM_DOCTOR: array[i] = confirm; break;
				case LeadColumns.RECEIVED: array[i] = received; break;
				case LeadColumns.RECEIVED_DATE: array[i] = receivedDate; break;
				case LeadColumns.DOCTOR_ANSWER: array[i] = doctorAnswer; break;
				case LeadColumns.FAX_DISPOSITION: {
					switch(obj.getString(DISPOSITION)) {
						case Disposition.DECEASED:
						case Disposition.DENIED:
						case Disposition.NEEDS_TO_BE_SEEN:
						case Disposition.NOT_INTERESTED:
							array[i] = obj.getString(DISPOSITION); 
							confirm = ConfirmDoctor.CONFIRMED; 
							received = Received.RECEIVED;
							receivedDate = Date.getCurrentDate("yyyy-MM-dd"); 
							doctorAnswer = ""+DoctorAnswer.DOCTOR_ANSWER;
							break;
						case Disposition.WRONG_DOCTOR: 
							array[i] = obj.getString(DISPOSITION); 
							confirm = ConfirmDoctor.NOT_CONFIRMED; 
							received = Received.NOT_RECEIVED; 
							receivedDate = "0000-00-00"; 
							doctorAnswer = ""+DoctorAnswer.DOCTOR_ANSWER;
							break;
						case Disposition.NO_ANSWER:
							array[i] = ""; 
							confirm = ""+record.getConfirmed();
							received = ""+GetIntegerFromYesNo(obj.getString(RECEIVED)); 
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
	
	public static JSONObject CreateJSON(Record record,int record_type,String ourFax) throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put(NAME, record.getFirstName()+" "+record.getLastName());
		obj.put(DOB, record.getDob());
		obj.put(PHONE,record.getPhone());
		obj.put(ID, record.getId());
		obj.put(PHARMACY, record.getPharmacy());
		obj.put(RECORD_TYPE, record_type);
		obj.put(ADDRESS, record.getAddress());
		obj.put(CITY, record.getCity());
		obj.put(STATE, record.getState());
		obj.put(ZIP, record.getZip());
		obj.put(NPI,record.getNpi());
		obj.put(DR_FIRST, record.getDrFirst());
		obj.put(DR_LAST, record.getDrLast());
		obj.put(DR_ADDRESS, record.getDrAddress());
		obj.put(DR_CITY, record.getDrCity());
		obj.put(DR_STATE, record.getDrState());
		obj.put(DR_ZIP, record.getDrZip());
		obj.put(DR_PHONE, record.getDrPhone());
		obj.put(DR_FAX, record.getDrFax());
		obj.put(CONFIRMED, record.getConfirmed());
		obj.put(RECEIVED, record.getReceived());
		obj.put(RECEIVED_DATE, record.getReceivedDate());
		obj.put(NOTES, record.getNotes());
		obj.put(PAIN_CAUSE, record.getPainCause());
		obj.put(PAIN_LOCATION, record.getPainLocation());
		obj.put(CHASE_COUNT, record.getChaseCount());
		obj.put(BRACES, record.getBraceList());
		obj.put(OUR_FAX, ourFax);
		switch(record_type) {
			case RecordType.NOT_CONFIRMED:
				obj.put(RECORD_TYPE, "NOT CONFIRMED");
				obj.put(SCRIPT, getScript(DoctorChaseParameters.class.getClassLoader().getResource(DoctorChaseScript.NOT_CONFIRMED),record));
				break;
			case RecordType.RECEIVED_NO_RESPONSE:
				obj.put(RECORD_TYPE, "RECEIVED NO RESPONSE");
				obj.put(SCRIPT, getScript(DoctorChaseParameters.class.getClassLoader().getResource(DoctorChaseScript.RECEIVED_NO_RESPONSE),record));
				break;
			case RecordType.CONFIRMED_NOT_RECEIVED:
				obj.put(RECORD_TYPE, "CONFIRMED NOT RECEIVED");
				obj.put(SCRIPT, getScript(DoctorChaseParameters.class.getClassLoader().getResource(DoctorChaseScript.CONFIRMED_NOT_RECEIVED),record));
				break;
			case RecordType.NOT_CONFIRMED_DME:
				obj.put(RECORD_TYPE, "NOT CONFIRMED DME");
				obj.put(SCRIPT, getScript(DoctorChaseParameters.class.getClassLoader().getResource(DoctorChaseScript.NOT_CONFIRMED_DME),record));
				break;
			case RecordType.CONFIRMED_NOT_RECEIVED_DME:
				obj.put(RECORD_TYPE, "CONFIRMED NOT RECEIVED DME");
				obj.put(SCRIPT, getScript(DoctorChaseParameters.class.getClassLoader().getResource(DoctorChaseScript.NOT_CONFIRMED_DME),record));
				break;
			default:
				obj.put(RECORD_TYPE, "UNKNOWN RECORD");
				obj.put(SCRIPT, "????");
				break;
		}
		/*
		 * ADD CODE TO ACCOUNT FOR IF IT HAS BEEN RECIEVED? OR CONFIRMED?
		 */
		return obj;
	}
	private static String getScript(URL url,Record record) {
		StringBuilder sb  = new StringBuilder();
		File file = null;
		BufferedReader br = null;
		try {
			file = new File(url.toURI());
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while((line=br.readLine())!=null)
				sb.append(line);	
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			sb.append(e.getMessage());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			sb.append(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			sb.append(e.getMessage());
		}
		return  sb.toString().replace("<PATIENT NAME>", record.getFirstName()+" "+record.getLastName())
				.replace("<DOB>", record.getDob())
				.replace("<DR NAME>", record.getDrFirst()+" "+record.getDrLast())
				.replace("<DR ADDRESS>", record.getDrAddress()+" "+record.getDrCity()+" "+record.getState()+" "+record.getZip())
				.replace("<DR FAX NUMBER>", record.getDrFax())
				.replace("<RECEIEVED DATE>", record.getReceivedDate());
	}
	public static int GetIntegerFromYesNo(String answer) {
		if(answer.equalsIgnoreCase("Yes"))
			return 1;
		else if(answer.equalsIgnoreCase("No"))
			return 0;
		else
			return -1;
	}
}
