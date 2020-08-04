package client;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONCreator {
	public static JSONObject CreateJSON() {
		return new JSONObject();
	}
	public static void AddStringToJSON(JSONObject obj,String key, Object value) {
		try {
			obj.append(key, value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}	
