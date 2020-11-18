package Database.Columns;

import org.json.JSONException;
import org.json.JSONObject;

import DoctorChase.MessageStatus;
import JSONParameters.DoctorChaseParameters;

public class FaxedColumns {
	public static final String ID = "_id";
	public static final String PHONE= "phonenumber";
	public static final String MESSAGE_ID = "MESSAGE_ID";
	public static final String AGENT = "AGENT";
	public static final String STATUS = "STATUS";
	public static final String FAX_ATTEMPTS= "FAX_ATTEMPTS";
	public static final String LAST_UPDATED = "LAST_UPDATED";
	public static final String RECORD_TYPE = "RECORD_TYPE";
	public static final String LEAD_TYPE = "LEAD_TYPE";
	public static final String PHARMACY = "PHARMACY";
	public static final String ATTENTION = "ATTENTION";
	public static final String SCRIPT = "SCRIPT";
	
	public static final String[] ALL = {ID,PHONE,MESSAGE_ID,AGENT,STATUS,FAX_ATTEMPTS,RECORD_TYPE,LEAD_TYPE,PHARMACY,ATTENTION,SCRIPT};
	
	public static Object[] ToStringArray(JSONObject obj,String messageId,byte[] pdfData) throws JSONException {
		Object[] array = new Object[ALL.length];
		for(int i = 0; i<array.length;i++) {
			String column = ALL[i];
			switch(column) {
				case ID: array[i] = obj.getString(DoctorChaseParameters.ID); break;
				case PHONE: array[i] = obj.getString(DoctorChaseParameters.PHONE); break;
				case MESSAGE_ID: array[i] = messageId; break;
				case AGENT: array[i] = obj.getString(DoctorChaseParameters.AGENT); break;
				case STATUS: array[i] = MessageStatus.QUEUED; break;
				case FAX_ATTEMPTS: array[i] = "0"; break;
				case RECORD_TYPE: array[i] = obj.getString(DoctorChaseParameters.RECORD_TYPE); break;
				case LEAD_TYPE: array[i] = obj.getString(DoctorChaseParameters.LEAD_TYPE); break;
				case PHARMACY: array[i] = obj.getString(DoctorChaseParameters.PHARMACY); break;
				case ATTENTION: array[i] = obj.getString(DoctorChaseParameters.ATTENTION); break;
				case SCRIPT: array[i] = pdfData; break;
			}
		}
		return array;
	}
}
