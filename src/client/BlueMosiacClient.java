package client;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import ResponseBuilder.TelmedResponse;

public class BlueMosiacClient {
	public static final String URL = "https://www.bluemosaichealth.com/insert-patient/CVIntake";
	
	public static JSONObject AddToBlueMosiac(JSONObject data) throws JSONException {
		URIBuilder b = null;
		URL url = null;
		try {
			b = new URIBuilder(URL);
			b.addParameter("data", data.toString());
			url = b.build().toURL();
			HttpGet get = new HttpGet(url.toString());
			CloseableHttpClient client = HttpClients.createDefault();
			CloseableHttpResponse response = client.execute(get);
			int status_code = response.getStatusLine().getStatusCode();
			if(status_code==200) {
				HttpEntity entity = response.getEntity();
				String responseString = EntityUtils.toString(entity, "UTF-8");
				response.close();
				client.close();
				return TelmedResponse.BuildBlueMosiacResponse(responseString, data);
			}
			else 
				return TelmedResponse.BuildFailedResponse("HTTP ERROR CODE: "+status_code);
		} catch (URISyntaxException | MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return TelmedResponse.BuildFailedResponse(e.getMessage());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return TelmedResponse.BuildFailedResponse(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return TelmedResponse.BuildFailedResponse(e.getMessage());
		}
	}
}
