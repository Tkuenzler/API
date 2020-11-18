package Database.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseResponseBuilder {
	public static final String SUCCESS = "success";
	public static final String VALUE = "value";
	public static final String ERROR_CODE = "error_code";
	public static final String MESSAGE = "message";
	public static JSONObject BuildSuccesfulAddition(int add) {
		try {
			return new JSONObject()
					.put(SUCCESS,true)
					.put(VALUE,add);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	public static JSONObject BuildFailedResponse(String message,int error_code)  {
		try {
			return new JSONObject()
					.put(SUCCESS,false)
					.put(MESSAGE, message)
					.put(ERROR_CODE, error_code);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
}
