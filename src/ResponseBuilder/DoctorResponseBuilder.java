package ResponseBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class DoctorResponseBuilder {
	public static final String VALID = "VALID";
	public static final String MESSAGE = "MESSAGE";
	public static final String DR_TYPE = "DR_TYPE";
	public static JSONObject DoctorResponse(boolean success, String message,String dr_type) throws JSONException {
		return new JSONObject()
				.put(VALID, success)
				.put(MESSAGE, message)
				.put(DR_TYPE,dr_type);
	}
}
