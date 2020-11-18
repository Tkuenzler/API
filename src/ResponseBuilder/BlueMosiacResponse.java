package ResponseBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class BlueMosiacResponse {
	public static final String STATUS = "status";
	public static final String MESSAGE = "message";
	public static final String PATIENT_ID = "patient_id";
	public static final String SUCCESS = "success";
	
	public static JSONObject BuildFailedResponse(String message) {
		try {
			return new JSONObject()
					.put(MESSAGE, message)
					.put(SUCCESS, false);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	public static JSONObject BuildSuccessfulResponse(String message) {
		try {
			return new JSONObject()
					.put(MESSAGE, message)
					.put(SUCCESS, true);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	public static JSONObject BuildResponse(JSONObject obj) {
		try {
			boolean success = obj.getString(STATUS).equalsIgnoreCase("success");
			return new JSONObject()
					.put(MESSAGE, obj.getString(MESSAGE))
					.put(PATIENT_ID,obj.getString(PATIENT_ID))
					.put(SUCCESS, success);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
}
