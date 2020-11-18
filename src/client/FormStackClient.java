package client;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import JSONParameters.FormStack;

public class FormStackClient {
	public static String URL = "https://camelothealth.formstack.com/forms/index.php";
	public static String PostTelmed(JSONObject triage) throws JSONException, ClientProtocolException, IOException {
		JSONObject obj = new JSONObject();
		HttpPost post = new HttpPost(URL);
		post.setHeader("Authorization", "Bearer 0cf9841556c364abb2bf7ad836c08a16");
		post.setHeader("Accept","application/json");
		post.setHeader("Content-Type","application/json");
			
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = httpClient.execute(post);
		HttpEntity responseEntity = response.getEntity();
		String responseString = EntityUtils.toString(responseEntity, "UTF-8");
		return response.getStatusLine().getStatusCode()+" "+responseString;
	}
}
