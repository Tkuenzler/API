package ResponseBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class DoctorChaseReponse {
	private class Parameters {
		public static final String SUCCESS = "success";
		public static final String MESSAGE = "message";
	}
	public static JSONObject BuildSuccesfulResponse(String message) throws JSONException {
		return new JSONObject()
				.put(Parameters.SUCCESS, true)
				.put(Parameters.MESSAGE, message);
	}
	
	public static JSONObject BuildFailedResponse(String message) throws JSONException {
		return new JSONObject()
				.put(Parameters.SUCCESS, false)
				.put(Parameters.MESSAGE, message);
	}
	
	public static JSONObject BuildSuccesfulNext(String message) throws JSONException {
		return new JSONObject()
				.put(Parameters.SUCCESS, true)
				.put(Parameters.MESSAGE, message);
	}
	
	public static JSONObject BuildSuccesfulNext(JSONObject obj) throws JSONException {
		return obj.put(Parameters.SUCCESS, true);
	}
}
