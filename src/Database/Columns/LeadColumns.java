package Database.Columns;

import Date.Date;
import client.Record;

public class LeadColumns {
	public static final String FIRST_NAME = "first_name";
	public static final String LAST_NAME = "last_name";
	public static final String DOB = "dob";
	public static final String AGE = "age";
	public static final String PHONE_NUMBER = "phonenumber";
	public static final String SSN = "ssn";
	public static final String GENDER = "gender";
	public static final String ADDRESS = "address";
	public static final String CITY = "city";
	public static final String STATE = "state";
	public static final String ZIP = "zip";
	public static final String EMDEON_STATUS = "EMDEON_STATUS";
	public static final String TYPE = "TYPE";
	public static final String EMDEON_TYPE = "EMDEON_TYPE";
	public static final String LAST_EMDEON_DATE = "LAST_EMDEON_DATE";
	public static final String INSURANCE_NAME = "insurance_name";
	public static final String CARRIER = "carrier";
	public static final String POLICY_ID = "policy_id";
	public static final String BIN = "bin";
	public static final String GROUP = "grp";
	public static final String PCN = "pcn";
	public static final String CONTRACT_ID = "contract_id";
	public static final String BENEFIT_ID = "benefit_id";
	public static final String NPI = "npi";
	public static final String DR_TYPE = "dr_type";
	public static final String DR_FIRST = "dr_first_name";
	public static final String DR_LAST = "dr_last_name";
	public static final String DR_ADDRESS1 = "dr_address1";
	public static final String DR_CITY = "dr_city";
	public static final String DR_STATE = "dr_state";
	public static final String DR_ZIP = "dr_zip";
	public static final String DR_PHONE = "dr_phone";
	public static final String DR_FAX = "dr_fax";
	public static final String ID = "_id";
	public static final String AGENT = "agent";
	public static final String PHARMACY = "PHARMACY";
	public static final String FAX_ATTEMPTS = "FAX_ATTEMPTS";
	public static final String FAXES_SENT = "FAXES_SENT";
	public static final String FAX_DISPOSITION = "FAX_DISPOSITION";
	public static final String FAX_DISPOSITION_DATE = "FAX_DISPOSITION_DATE";
	public static final String FAX_SENT_DATE = "FAX_SENT_DATE";
	public static final String RECEIVED_DATE = "RECEIVED_DATE";
	public static final String RECEIVED  = "RECEIVED";
	public static final String DOCTOR_ANSWER = "DOCTOR_ANSWER";
	public static final String MESSAGE_STATUS = "MESSAGE_STATUS";
	public static final String USED = "USED";
	public static final String AFID = "AFID";
	public static final String CALL_CENTER = "CALL_CENTER";
	public static final String NOTES = "notes";
	public static final String DATE_ADDED = "DATE_ADDED";
	public static final String LAST_UPDATED = "LAST_UPDATED";
	public static final String MESSAGE_ID = "MESSAGE_ID";
	public static final String CONFIRM_DOCTOR = "CONFIRM_DOCTOR";
	public static final String PAIN_LOCATION = "PAIN_LOCATION";
	public static final String PAIN_CAUSE = "PAIN_CAUSE";
	public static final String CHASE_COUNT = "CHASE_COUNT";
	public static final String DR_CHASE_AGENT = "DR_CHASE_AGENT";
	public static final String LAST_CHASE_DATE = "LAST_CHASE_DATE";
	public static final String SOURCE = "SOURCE";
	public static final String PRODUCTS = "PRODUCTS";
	
	public static final String[] FAX_CHASE = {NPI,DR_FIRST,DR_LAST,DR_ADDRESS1,DR_CITY,DR_STATE,DR_ZIP,DR_PHONE,DR_FAX,NOTES,USED,DR_CHASE_AGENT,LAST_CHASE_DATE,CHASE_COUNT,FAX_DISPOSITION,CONFIRM_DOCTOR,RECEIVED,RECEIVED_DATE,CONFIRM_DOCTOR,DOCTOR_ANSWER};
	public static final String[] DR_INFO = {NPI,DR_FIRST,DR_LAST,DR_ADDRESS1,DR_CITY,DR_STATE,DR_ZIP,DR_PHONE,DR_FAX};
	public static final String[] ADD_RECORD = {ID,FIRST_NAME,LAST_NAME,DOB,GENDER,SSN,PHONE_NUMBER,ADDRESS,CITY,STATE,ZIP
			,PRODUCTS,PAIN_CAUSE,PAIN_LOCATION,AGENT,AFID,
			PHARMACY,CARRIER,POLICY_ID,BIN,GROUP,PCN,
			NPI,DR_FIRST,DR_LAST,DR_ADDRESS1,DR_CITY,DR_STATE,DR_ZIP,DR_PHONE,DR_FAX,DATE_ADDED};
	
	public static String[] ToStringArray(Record record) {
		String[] array = new String[ADD_RECORD.length];
		for(int i = 0; i<array.length;i++) {
			String column = ADD_RECORD[i];
			switch(column) {
				case ID: array[i] = record.getId(); break;
				case FIRST_NAME: array[i] = record.getFirstName(); break;
				case LAST_NAME: array[i] = record.getLastName(); break;
				case GENDER: array[i] = record.getGender(); break;
				case DOB: array[i] = record.getDob(); break;
				case SSN: array[i] = record.getSsn(); break;
				case PHONE_NUMBER: array[i] = record.getPhone(); break;
				case ADDRESS: array[i] = record.getAddress(); break;
				case CITY: array[i] = record.getCity(); break;
				case STATE: array[i] = record.getState(); break;
				case ZIP: array[i] = record.getZip(); break;
				case AGENT: array[i] = record.getAgent(); break;
				case AFID: array[i] = record.getAfid(); break;
				//PRODUCT
				case PRODUCTS: array[i] = record.getProductsAsString(); break;
				case PAIN_LOCATION: array[i] = record.getPainLocation(); break;
				case PAIN_CAUSE: array[i] = record.getPainCause(); break;
				//INSURANCE
				case PHARMACY: array[i] = record.getPharmacy(); break;
				case CARRIER: array[i] = record.getCarrier(); break;
				case POLICY_ID: array[i] = record.getPolicyId(); break;
				case BIN: array[i] = record.getBin(); break;
				case GROUP: array[i] = record.getGrp(); break;
				case PCN: array[i] = record.getPcn(); break;
				//DR
				case NPI: array[i] = record.getNpi(); break;
				case DR_FIRST: array[i] = record.getDrFirst(); break;
				case DR_LAST: array[i] = record.getDrLast(); break;
				case DR_ADDRESS1: array[i] = record.getDrAddress(); break;
				case DR_CITY: array[i] = record.getDrCity(); break;
				case DR_STATE: array[i] = record.getDrState(); break;
				case DR_ZIP: array[i] = record.getDrZip(); break;
				case DR_PHONE: array[i] = record.getDrPhone(); break;
				case DR_FAX: array[i] = record.getDrFax(); break;
				case DATE_ADDED: array[i] = Date.getCurrentDate("yyyy-MM-dd"); break;
			}
		}
		return array;
	}
}
