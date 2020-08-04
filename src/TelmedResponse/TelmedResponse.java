package TelmedResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class TelmedResponse {
	public static final String SUCCESS = "success";
	public static final String ERROR = "error";
	public static final String DATE_ADDED = "date";
	public static final String FIRST = "first_name";
	public static final String LAST = "last_name";
	public static final String RECORD_ID = "id";
	public static final String AGENT = "agent";
	public static final String PHARMACY = "pharmacy";
	
	public static class Errors {
		public static final String DUPLICATE = "duplicate";
		public static final String NOT_IN_ROAD_MAP = "Not in road-map";
		public static final String INVALID_PHARMACY = "Invalid Pharmacy";
	}
	public static JSONObject BuildBlueMosiacResponse(String json, JSONObject data) throws JSONException {
		JSONObject response = new JSONObject();
		JSONObject blue_response = new JSONObject(json);
		if(blue_response.getString("status").equalsIgnoreCase("success")) {
			response.put(SUCCESS, true);
			response.put(FIRST, data.getString("first_name"));
			response.put(LAST, data.getString("last_name"));
			response.put(RECORD_ID, blue_response.getString("patient_id"));
		}
		else {
			response.put(SUCCESS, false);
			response.put(ERROR, blue_response.getString("message"));
		}
		return response;
	}
	public static JSONObject BuildSuccessfulResponse(String first,String last,String id) throws JSONException {
		return new JSONObject()
				.put(SUCCESS, true)
				.put(FIRST, first)
				.put(LAST, last)
				.put(RECORD_ID, id);
	}
	public static JSONObject BuildSuccessfulResponse(JSONObject obj) throws JSONException {
		return new JSONObject()
				.put(SUCCESS, true)
				.put(FIRST, obj.getString("first_name"))
				.put(LAST, obj.getString("last_name"))
				.put(RECORD_ID, "");
	}
	public static JSONObject BuildFailedResponse(int add) throws JSONException {
		return new JSONObject()
				.put(SUCCESS, false)
				.put(ERROR, add);
	}
	public static JSONObject BuildFailedResponse(String error,String date,String first,String last,String id,String agent) throws JSONException {
		return new JSONObject()
				.put(SUCCESS, false)
				.put(ERROR, error)
				.put(DATE_ADDED, date)
				.put(FIRST, first)
				.put(LAST, last)
				.put(RECORD_ID, id)
				.put(AGENT, agent);
	}
	public static JSONObject BuildFailedResponse(String error) throws JSONException {
		return new JSONObject()
				.put(SUCCESS, false)
				.put(ERROR, error);
	}
}
