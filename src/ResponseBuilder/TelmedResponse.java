package ResponseBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import JSONParameters.TriageParameters;

public class TelmedResponse {
	public static final String SUCCESS = "success";
	public static final String MESSAGE = "message";
	public static final String ERROR = "error";
	public static final String ERROR_CODE = "error_code";
	public static final String DATE_ADDED = "date";
	public static final String FIRST = "first_name";
	public static final String LAST = "last_name";
	public static final String RECORD_ID = "id";
	public static final String AGENT = "agent";
	public static final String TRIAGE = "TRIAGE";
	public static final String PHARMACY = "pharmacy";
	
	public static class Errors {
		public static final String DUPLICATE = "duplicate";
		public static final String NOT_IN_ROAD_MAP = "Not in road-map";
		public static final String INVALID_PHARMACY = "Invalid Pharmacy";
	}
	public static JSONObject BuildBlueMosiacResponse(String json, JSONObject data) {
		JSONObject response = new JSONObject();
		try {
			JSONObject blue_response = new JSONObject(json);
			if(blue_response.getString("status").equalsIgnoreCase("success")) {
				response.put(SUCCESS, true);
				response.put(FIRST, data.getString(TriageParameters.FIRST_NAME));
				response.put(LAST, data.getString(TriageParameters.LAST_NAME));
			}
			else {
				response.put(SUCCESS, false);
				response.put(ERROR, blue_response.getString("message"));
			}
			return response;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return null;
		}
		
	}
	public static JSONObject BuildSuccessfulResponse(String first,String last,String id) {
		try {
			return new JSONObject()
					.put(SUCCESS, true)
					.put(FIRST, first)
					.put(LAST, last)
					.put(RECORD_ID, id);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	public static JSONObject BuildSuccessfulResponse(String message) {
		try {
			return new JSONObject()
					.put(SUCCESS, true)
					.put(MESSAGE, message);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	public static JSONObject BuildSuccessfulResponse(JSONObject obj) {
		try {
			return new JSONObject()
					.put(SUCCESS, true)
					.put(FIRST, obj.getString(TriageParameters.FIRST_NAME))
					.put(LAST, obj.getString(TriageParameters.LAST_NAME))
					.put(TRIAGE, obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	public static JSONObject BuildFailedResponse(int add) {
		try {
			return new JSONObject()
					.put(SUCCESS, false)
					.put(ERROR, add);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	public static JSONObject BuildFailedResponse(int add,String message) {
		try {
			return new JSONObject()
					.put(SUCCESS, false)
					.put(ERROR_CODE, add)
					.put(ERROR, message);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	public static JSONObject BuildFailedResponse(String error,String first,String last) {
		try {
			return new JSONObject()
					.put(SUCCESS, false)
					.put(ERROR, error)
					.put(FIRST, first)
					.put(LAST, last);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	public static JSONObject BuildFailedResponse(String error) {
		try {
			return new JSONObject()
					.put(SUCCESS, false)
					.put(ERROR, error);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return null;
		
		}
	}
	
	public static JSONObject BuildFailedResponse(String error,int error_code) {
		try {
			return new JSONObject()
					.put(SUCCESS, false)
					.put(ERROR, error)
					.put(ERROR_CODE, error_code);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
}
