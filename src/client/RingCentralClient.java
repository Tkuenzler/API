package client;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RingCentralClient {
	private static class RateLimit {
		public static int HEAVY = 10;
		public static int MEDIUM = 40;
		public static int LIGHT = 50;
		public static int AUTH = 5;
	}
	private class StatusCodes {
		public static final int SUCCESFUL_200 = 200;
		public static final int SUCCESFUL_201 = 201;
		public static final int SUCCESFUL_202 = 202;
		public static final int SUCCESFUL_204 = 204;
		public static final int SUCCESFUL_206 = 206;
		public static final int SUCCESFUL_BULK = 207;
		public static final int EXPIRED_TOKEN = 401;
		public static final int INVALID_URL = 404;
		public static final int TOO_MANY_REQUESTS = 429;
		public static final int SERVICE_UNAVAILABLE = 503;
	}
	public class Errors {
		public static final String INVALID_URL = "INVALID URL";
		public static final String TOO_MANY_REQUEST = "TOO MANY REQUESTS";
		public static final String SERVICE_UNAVAILABLE = "SERVICE UNAVAILABLE";
		public static final String EXPIRED_TOKEN = "EXPIRED TOKEN";
		public static final String UNKNOWN_ERROR = "UNKNOWN_ERROR";
	}
	private class URLS {
		public static final String URL = "https://platform.ringcentral.com";
		public static final String AUTHORIZE = "/restapi/oauth/token";
		public static final String REVOKE = "/restapi/oauth/revoke";
		public static final String MESSAGE_BY_ID = "/restapi/v1.0/account/~/extension/~/message-store/";
		public static final String FAX = "/restapi/v1.0/account/~/extension/~/fax";
		public static final String CHECK_CONNECTION = "/restapi/";
		public static final String GET_MESSAGE_LIST = "/restapi/v1.0/account/~/extension/~/message-store";
		public static final String GET_ATTACHMENT = "/restapi/v1.0/account/~/extension/~/message-store/content/";
	}
	private String ACCESS_TOKEN;
	private String REFRESH_TOKEN;
	public String number,password;
	private String CLIENT,SECRET;
	public boolean loggedIn = false;
	public RingCentralClient(String number,String password,String client, String secret) {
		this.number = number;
		this.password = password;
		this.CLIENT = client;
		this.SECRET = secret;
	}
	public boolean login() {
		HttpPost post = new HttpPost(URLS.URL+URLS.AUTHORIZE);
		try {
			String basic = Base64.getEncoder().encodeToString((CLIENT+":"+SECRET).getBytes("utf-8"));
			post.setHeader("Accept","application/json");
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setHeader("Authorization", "Basic "+basic);
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("grant_type", "password"));
			params.add(new BasicNameValuePair("username", number));
			params.add(new BasicNameValuePair("password", password));
			params.add(new BasicNameValuePair("access_token_ttl","0"));
			post.setEntity(new UrlEncodedFormEntity(params));
			CloseableHttpClient client = HttpClients.createDefault();
			CloseableHttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
		    String responseString = EntityUtils.toString(entity, "UTF-8");
		    System.out.println(responseString);
		    extractTokens(responseString);
		    boolean status = response.getStatusLine().getStatusCode()==200;
		    LogResponse(URLS.AUTHORIZE,response.getStatusLine().getStatusCode());
		    client.close();
		    response.close();
		    loggedIn = status;
		    return status;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	public void revokeToken() {
		HttpPost post = new HttpPost(URLS.URL+URLS.REVOKE);
		try {
			String encode = Base64.getEncoder().encodeToString((CLIENT+":"+SECRET).getBytes("utf-8"));
			post.addHeader("Content-Type", "application/x-www-form-urlencoded");
			post.addHeader("Authorization","Basic "+encode);
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("token", ACCESS_TOKEN));
			post.setEntity(new UrlEncodedFormEntity(params));
			CloseableHttpClient client = HttpClients.createDefault();
			CloseableHttpResponse responseClient = client.execute(post);	
			boolean status = responseClient.getStatusLine().getStatusCode()==200;
		    LogResponse(URLS.REVOKE,responseClient.getStatusLine().getStatusCode());
		    client.close();
		    responseClient.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String refreshToken() {
		HttpPost post = new HttpPost(URLS.URL+URLS.AUTHORIZE);
		try {
			String basic = Base64.getEncoder().encodeToString((CLIENT+":"+SECRET).getBytes("utf-8"));
			post.setHeader("Accept","application/json");
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setHeader("Authorization", "Basic "+basic);
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("refresh_token", REFRESH_TOKEN));
			params.add(new BasicNameValuePair("grant_type", "refresh_token"));
			post.setEntity(new UrlEncodedFormEntity(params));
			CloseableHttpClient client = HttpClients.createDefault();
			CloseableHttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			String responseString = EntityUtils.toString(entity, "UTF-8");
			LogResponse(URLS.AUTHORIZE,response.getStatusLine().getStatusCode());
			client.close();
		    response.close();
		    extractTokens(responseString);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public String GetMessageList() throws IOException {
		HttpGet get = new HttpGet(URLS.URL+URLS.GET_MESSAGE_LIST);
		CloseableHttpClient client = HttpClients.createDefault();
		get.setHeader("Authorization", "Bearer "+ACCESS_TOKEN);
		get.setHeader("Accept", "application/json");
		CloseableHttpResponse response = client.execute(get);
		int status_code = response.getStatusLine().getStatusCode();
		LogResponse(URLS.GET_MESSAGE_LIST,status_code);
		switch(status_code) {
			case StatusCodes.SUCCESFUL_200:
			case StatusCodes.SUCCESFUL_201:
			case StatusCodes.SUCCESFUL_202:
			case StatusCodes.SUCCESFUL_204:
			case StatusCodes.SUCCESFUL_206:
			case StatusCodes.SUCCESFUL_BULK:
				HttpEntity entity = response.getEntity();
				String responseString = EntityUtils.toString(entity, "UTF-8");
				client.close();
				response.close();
				return responseString;
			case StatusCodes.EXPIRED_TOKEN:
				refreshToken();
				return GetMessageList();
			case StatusCodes.INVALID_URL: 
				return Errors.INVALID_URL;
			case StatusCodes.TOO_MANY_REQUESTS:
				return Errors.TOO_MANY_REQUEST;
			case StatusCodes.SERVICE_UNAVAILABLE:
				return Errors.SERVICE_UNAVAILABLE;
			default: 
				return Errors.UNKNOWN_ERROR;
		}
	}
	public JSONObject getMessageById(String id) throws IOException, JSONException {
		//API-GROUP LIGHT
		HttpGet get = new HttpGet(URLS.URL+URLS.MESSAGE_BY_ID+id);
		CloseableHttpClient client = HttpClients.createDefault();
		get.setHeader("Authorization", "Bearer "+ACCESS_TOKEN);
		get.setHeader("Accept", "application/json");
		CloseableHttpResponse response = client.execute(get);
		int status_code = response.getStatusLine().getStatusCode();
		LogResponse(URLS.MESSAGE_BY_ID,status_code);
		switch(status_code) {
			case StatusCodes.SUCCESFUL_200:
			case StatusCodes.SUCCESFUL_201:
			case StatusCodes.SUCCESFUL_202:
			case StatusCodes.SUCCESFUL_204:
			case StatusCodes.SUCCESFUL_206:
			case StatusCodes.SUCCESFUL_BULK:
				HttpEntity entity = response.getEntity();
				String responseString = EntityUtils.toString(entity, "UTF-8");
				client.close();
				response.close();
				String messageStatus = GetMessageStatus(responseString);
				return CreateJSONObjectResult(messageStatus,1,RateLimit.LIGHT);
			case StatusCodes.EXPIRED_TOKEN:
				refreshToken();
				return getMessageById(id);
			case StatusCodes.INVALID_URL: 
				return CreateJSONObjectResult(Errors.INVALID_URL,0,RateLimit.LIGHT);
			case StatusCodes.TOO_MANY_REQUESTS:
				return CreateJSONObjectResult(Errors.TOO_MANY_REQUEST,0,RateLimit.LIGHT);
			case StatusCodes.SERVICE_UNAVAILABLE:
				return CreateJSONObjectResult(Errors.SERVICE_UNAVAILABLE,0,RateLimit.LIGHT);
			default: 
				return CreateJSONObjectResult("UNKNOWN ERROR",0,RateLimit.LIGHT);
		}
	}
	public JSONObject SendFax(Record record,File file) throws IOException, JSONException {
		//API-GROUP HEAVY
		if(record.getDrFax().length()!=10) {
			return CreateJSONObjectResult("INVALID FAX NUMBER",0,RateLimit.HEAVY);
		}
		String responseString = null;
		System.out.println("ATTEMPTING TO FAX "+record.getFirstName()+" "+record.getLastName());
		String boundary = "--Boundary_1_14413901_1361871080888";
		JSONObject json = createFaxJSON(record.getDrFax());
		HttpPost post = new HttpPost(URLS.URL+URLS.FAX);
		post.setHeader("Authorization", "Bearer "+ACCESS_TOKEN);
		post.setHeader("Content-Type","multipart/form-data; boundary="+boundary);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setBoundary(boundary);
		builder.addTextBody("json", json.toString(), ContentType.APPLICATION_JSON);
		builder.addBinaryBody("content", file,ContentType.APPLICATION_OCTET_STREAM, file.getName());
		HttpEntity multipart = builder.build();
		post.setEntity(multipart);
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse response = client.execute(post);		
		HttpEntity entity = response.getEntity();
		responseString = EntityUtils.toString(entity, "UTF-8");
		int status_code = response.getStatusLine().getStatusCode();
		LogResponse(URLS.FAX,status_code);
		System.out.println("Status Code: "+status_code);
		switch(status_code) {
		case StatusCodes.SUCCESFUL_200:
		case StatusCodes.SUCCESFUL_201:
		case StatusCodes.SUCCESFUL_202:
		case StatusCodes.SUCCESFUL_204:
		case StatusCodes.SUCCESFUL_206:
		case StatusCodes.SUCCESFUL_BULK:
				client.close();
				response.close();
				String messageId = GetMessageId(responseString);
				return CreateJSONObjectResult(messageId,1,RateLimit.HEAVY);
			case StatusCodes.EXPIRED_TOKEN:
				refreshToken();
				return SendFax(record,file);
			case StatusCodes.INVALID_URL: 
				return CreateJSONObjectResult("Invalid URL",0,RateLimit.HEAVY);
			case StatusCodes.TOO_MANY_REQUESTS:
				return CreateJSONObjectResult("Too many requests",0,RateLimit.HEAVY);
			case StatusCodes.SERVICE_UNAVAILABLE:
				return CreateJSONObjectResult("Service Unavailable",0,RateLimit.HEAVY);
			default: 
				return CreateJSONObjectResult("Unkown Error",0,RateLimit.HEAVY);
		}
	}
	
	public boolean checkConnection() {
		HttpGet get = new HttpGet(URLS.URL+URLS.CHECK_CONNECTION);
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse response;
		
		try {
			response = client.execute(get);
			int status_code = response.getStatusLine().getStatusCode();
			LogResponse(URLS.CHECK_CONNECTION,status_code);
			HttpEntity entity = response.getEntity();
			if(status_code==200) 
				return true;			
			else 
				return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}	
	}
	private void extractTokens(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			ACCESS_TOKEN = obj.getString("access_token");
			REFRESH_TOKEN = obj.getString("refresh_token");
			System.out.println("ACCESS_TOKEN: "+ACCESS_TOKEN);
			System.out.println("REFRESH_TOKEN: "+REFRESH_TOKEN);
			int expired = obj.getInt("expires_in")*1000;
			System.out.println("EXPIRED: "+expired);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private JSONObject createFaxJSON(String faxLine) {
		JSONObject json = null;
		try {
			json = new JSONObject("{\"to\":[{\"phoneNumber\":\"+1"+faxLine+"\"}],\"faxResolution\":\"High\",\"coverIndex\":0}");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	public void close() {
		System.out.println("CLOSING");
		revokeToken();
		
	}
	private String getMessageId(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			String messageId = String.valueOf(obj.get("id"));
			return messageId;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	private String GetMessageStatus(String json) {
		JSONObject obj;
		try {
			obj = new JSONObject(json);
			JSONArray to = obj.getJSONArray("to");
			return to.getJSONObject(0).getString("messageStatus");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "ERROR";
		}
		
	}
	private JSONObject CreateJSONObjectResult(String message_id,int success,int rate) throws JSONException {
		JSONObject result = new JSONObject();
		result.put("Success", success);
		result.put("Message_Id", message_id);
		result.put("Rate", 60000/rate);
		return result;
	}
	public String GetStatusFromRingCentral(JSONObject obj) throws JSONException {
 		if(obj.has("Message_Id"))
 			return obj.getString("Message_Id");
 		else
 			return "Error No Message Found";
 	}
 	public boolean IsRingCentralResponseSuccesful(JSONObject obj) throws JSONException {
 		if(obj.has("Success"))
 			return obj.getInt("Success")==1;
 		else
 			return false;
 	}
 	private String GetMessageId(String json) throws JSONException {
		JSONObject obj = new JSONObject(json);
		String messageId = String.valueOf(obj.get("id"));
		return messageId;
	}
 	private void LogResponse(String endpoint,int status_code) {
 		switch(status_code) {
 		case StatusCodes.SUCCESFUL_200:
 		case StatusCodes.SUCCESFUL_201:
		case StatusCodes.SUCCESFUL_202:
		case StatusCodes.SUCCESFUL_204:
		case StatusCodes.SUCCESFUL_206:
		case StatusCodes.SUCCESFUL_BULK:
			return;
		case StatusCodes.EXPIRED_TOKEN:
			ErrorSQLLog.AddErrorMessage(status_code, Errors.EXPIRED_TOKEN, endpoint, "SERVER",this.number);
			break;
		case StatusCodes.INVALID_URL: 
			ErrorSQLLog.AddErrorMessage(status_code, Errors.INVALID_URL, endpoint, "SERVER",this.number);
			break;
		case StatusCodes.TOO_MANY_REQUESTS:
			ErrorSQLLog.AddErrorMessage(status_code, Errors.TOO_MANY_REQUEST, endpoint, "SERVER",this.number);
			break;
		case StatusCodes.SERVICE_UNAVAILABLE:
			ErrorSQLLog.AddErrorMessage(status_code, Errors.SERVICE_UNAVAILABLE, endpoint, "SERVER",this.number);
			break;
		default: 
			ErrorSQLLog.AddErrorMessage(status_code, Errors.UNKNOWN_ERROR, endpoint, "SERVER",this.number);
			break;
 		}
 	}
}