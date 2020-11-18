package client;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.net.URL;
import JSONParameters.RDSParameters;
import JSONParameters.TriageParameters;
import ResponseBuilder.TelmedResponse;

public class RDSClient {
	public class Signature {
		public static final String MTK_SIGNATURE = "jDOBAf$3";
		public static final String MTK2_SIGNATURE = "avuzthU9";
	}
	public class Url {
		public static final String MTK_URL1 = "http://telemed.quivvytech.com/api/v4/api.php";
		public static final String MTK_URL = "https://telemed.quivvytech.com/api/v4/api.php";
		public static final String MTK2_URL = "http://telemed.quivvytech.com/api/v4v2/api.php";
		public static final String PADDLEPOINT_URL = "http://telemed.quivvytech.com/api/v44/api.php";
	}
	
	public static JSONObject AddLeadToRDS(JSONObject obj)  {
		int status_code = 0;
		String responseString = null;
		StringBuilder params = new StringBuilder();
		URIBuilder b = null;
		URL url = null;	
		try {
			b = new URIBuilder(Url.MTK_URL);
			JSONArray keys = obj.names ();
			b.addParameter(RDSParameters.SIGNATURE_VALIDATION, Signature.MTK_SIGNATURE);
			for (int i = 0; i < keys.length (); ++i) {
				String key = keys.getString(i);
				String value = obj.getString(key);
				b.addParameter(keys.getString(i), value);
			}
			url = b.build().toURL();
			HttpGet get = new HttpGet(url.toString());
			CloseableHttpClient client = HttpClients.createDefault();
			CloseableHttpResponse response = client.execute(get);
			status_code = response.getStatusLine().getStatusCode();
			switch(status_code) {
				case 200:
					HttpEntity entity = response.getEntity();
					responseString = EntityUtils.toString(entity, "UTF-8");
					String id = ExtractRDSId(responseString);
					return TelmedResponse.BuildSuccessfulResponse(obj.getString(TriageParameters.FIRST_NAME), obj.getString(TriageParameters.LAST_NAME), id);
				default:
					return TelmedResponse.BuildFailedResponse("HTTP ERROR", status_code);
			}
		} catch(JSONException ex) {
			return TelmedResponse.BuildFailedResponse(responseString);
		} catch (ClientProtocolException ex) {
			return TelmedResponse.BuildFailedResponse(ex.getMessage());
		} catch (IOException ex) {
			return TelmedResponse.BuildFailedResponse(ex.getMessage());
		} catch (URISyntaxException ex) {
			// TODO Auto-generated catch block
			return TelmedResponse.BuildFailedResponse(ex.getMessage());
		}
	}
	private static String ExtractRDSId(String response) {
		try {
			JSONObject obj = new JSONObject(response);
			JSONArray info = obj.getJSONArray("responseInsertInfos");
			for(int i = 0;i<info.length();i++) {
				if(!info.getJSONObject(i).has("fieldName"))
					continue;
				if(info.getJSONObject(i).isNull("fieldName"))
					continue;
				else if(info.getJSONObject(i).getString("fieldName").equalsIgnoreCase("newPatientId"))
					return ""+info.getJSONObject(i).getInt("returnValue");
				
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return response;
		}
		return "ERROR";
	}
}
