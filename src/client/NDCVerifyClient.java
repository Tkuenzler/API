package client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import PBM.InsuranceFilter;
import PBM.InsuranceType;

public class NDCVerifyClient {
	private static final String FETCH_TOKEN = "https://www.ndcverify.com/api/oauth/fetch-token/";
	private static final String CARD_VERIFY = "https://www.ndcverify.com/api/cardVerify/";
	private static final String NDC_VERIFY = "https://www.ndcverify.com/api/pharmacy/eligibility/";
	private static final String CHECK_TRANSACTION_ID = "https://www.ndcverify.com/api/pharmacy/fetch-transaction/";
	String username,password,token,npi;
	public class JSON {
		public static final String STATUS = "status";
		public static final String ERRORS = "errors";
		public static final String ERROR = "error";
		public static final String TOKEN = "token";
		public static final String BIN = "bin";
		public static final String GRP  = "groupId";
		public static final String PCN   = "pcn";
		public static final String POLICY_ID   = "cardholderId";
		public static final String MESSAGE   = "message";
		public static final String COVERAGE_1 = "coverage-1";
		public static final String COVERAGE_2 = "coverage-2";
		public static final String COVERAGE_3 = "coverage-3";
		public static final String TRANSACTION = "transaction";
		public static final String TRANSACTION_ID = "transactionId";
		public static final String PRODUCTS = "products";
		public static final String ADDITONAL_NOTES = "additionalNotes";
		
	}
	public class PostFields {
		public static final String FIRST_NAME = "firstName";
		public static final String LAST_NAME = "lastName";
		public static final String DOB = "dateOfBirth";
		public static final String GENDER = "gender";
		public static final String POLICY_ID = "cardHolderId";
		public static final String BIN = "binNumber";
		public static final String PCN = "PCN";
		public static final String GROUP_ID = "groupId";
		public static final String DR_NPI = "physicianNPI";
		public static final String DR_LAST_NAME = "physicianLastName";
		public static final String PHARMACY_NPI = "pharmacyNPI";
		public static final String PRODUCTS = "products";
		public static final String BUNDLE_ID = "bundleId";
		public static final String PATIENT_CONSENT = "patientConsent";	
		public static final String TRANSACTION_ID = "transactionId";
	}
	public class Status {
		public static final String SUCCESSFUL = "successful";
		public static final String FAILED = "failed";
	}
	public class ProductFields {
		public static final String STATUS = "status";
		public static final String PRODUCT_NAME = "productName";
		public static final String CATEGORY = "category";
		public static final String QUANTITY = "quantity";
		public static final String SIG = "sig";
		public static final String SUCCESS = "Success";
		public static final String NOT_COVERED = "Not Covered";
		public static final String PATIENT_PAY_AMOUNT = "patientPayAmount";
		public static final String COPAY = "copayAmount";
		public static final String NDC = "ndc";
	}
	public class AdditionalNotes {
		public static final String MAXIMUM = "MAXIMUM DAILY DOSE OF ";
		public static final String MAXIMUM_2 = "MAX QTY OF ";
	}
	public NDCVerifyClient(String username,String password) {
		this.username = username;
		this.password = password;
	}
	public void setNpi(String npi) {
		this.npi = npi;
	}
	public boolean login() throws ClientProtocolException, IOException, JSONException {
		String basic = Base64.getEncoder().encodeToString((username+":"+password).getBytes("utf-8"));
		HttpPost post = new HttpPost(FETCH_TOKEN);
		post.setHeader("Accept","application/json");
		post.setHeader("Authorization", "Basic "+basic);
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse response = client.execute(post);
		HttpEntity entity = response.getEntity();
	    String responseString = EntityUtils.toString(entity, "UTF-8");
	    System.out.println(responseString);
	    int status_code = response.getStatusLine().getStatusCode();
	    if(status_code==200) 
	    	return GetToken(responseString);
	    else
	    	return false;
	}
	private boolean GetToken(String json) throws JSONException {
		JSONObject obj = new JSONObject(json);
		boolean status = obj.getString(JSON.STATUS).equalsIgnoreCase("successful");
		if(status) {
			this.token = obj.getString(JSON.TOKEN);
			return true;
		}
		else
			return false;
	}
	public void CardVerify(Record record,String npi) throws ClientProtocolException, IOException, JSONException {
		HttpPost post = new HttpPost(CARD_VERIFY);
		post.setHeader("Accept","application/json");
		post.setHeader("Authorization","Bearer "+token);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("firstName", record.getFirstName()));
		params.add(new BasicNameValuePair("lastName", record.getLastName()));
		params.add(new BasicNameValuePair("dateOfBirth", record.getDob()));
		params.add(new BasicNameValuePair("gender", record.getGender()));
		params.add(new BasicNameValuePair("zipCode", record.getZip()));
		params.add(new BasicNameValuePair("ssnLast4", record.getSsn()));
		params.add(new BasicNameValuePair("pharmacyNPI", npi));
		post.setEntity(new UrlEncodedFormEntity(params));
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse response = client.execute(post);
		HttpEntity entity = response.getEntity();
		String responseString = EntityUtils.toString(entity, "UTF-8");
	    System.out.println(responseString);
	    ParseInsuranceInfo(record,responseString);
	}
	private void ParseInsuranceInfo(Record record,String json) throws JSONException {
		System.out.println(json);
		JSONObject obj = new JSONObject(json);
		if(obj.getString(JSON.STATUS).equalsIgnoreCase("successful")) {
			record.setStatus("FOUND");
			JSONObject coverage1 = (obj.has(JSON.COVERAGE_1) ? obj.getJSONObject(JSON.COVERAGE_1) : null);
			JSONObject coverage2 = (obj.has(JSON.COVERAGE_2) ? obj.getJSONObject(JSON.COVERAGE_2) : null);
			JSONObject coverage3 = (obj.has(JSON.COVERAGE_3) ? obj.getJSONObject(JSON.COVERAGE_3) : null);
			if(coverage2==null && coverage3==null)
				record.SetInsurance(coverage1);
			else if(coverage1!=null && coverage2!=null) {
				String type1 = null;
				String type2 = null;
				record.SetInsurance(coverage1);
				type1 = InsuranceFilter.Filter(record);
				record.SetInsurance(coverage2);
				type2 = InsuranceFilter.Filter(record);
				if(InsuranceType.IsMedicare(type1)) {
					record.SetInsurance(coverage1);
					return;
				}
				if(InsuranceType.IsMedicare(type2)) {
					record.SetInsurance(coverage2);
					return;
				}
				if(InsuranceType.IsPrivate(type1)) {
					record.SetInsurance(coverage1);
					return;
				}
				if(InsuranceType.IsPrivate(type2)) {
					record.SetInsurance(coverage2);
					return;
				}
				else {
					record.SetInsurance(coverage1);
					return;
				}	
			}	
			else if(coverage1!=null && coverage2!=null && coverage3!=null) {	
				String type1 = null;
				String type2 = null;
				String type3 = null;
				record.SetInsurance(coverage1);
				type1 = InsuranceFilter.Filter(record);
				record.SetInsurance(coverage2);
				type2 = InsuranceFilter.Filter(record);
				record.SetInsurance(coverage3);
				type3 = InsuranceFilter.Filter(record);
				if(InsuranceType.IsMedicare(type1)) {
					record.SetInsurance(coverage1);
					return;
				}
				if(InsuranceType.IsMedicare(type2)) {
					record.SetInsurance(coverage2);
					return;
				}
				if(InsuranceType.IsMedicare(type3)) {
					record.SetInsurance(coverage3);
					return;
				}
				if(InsuranceType.IsPrivate(type1)) {
					record.SetInsurance(coverage1);
					return;
				}
				if(InsuranceType.IsPrivate(type2)) {
					record.SetInsurance(coverage2);
					return;
				}
				if(InsuranceType.IsPrivate(type3)) {
					record.SetInsurance(coverage3);
					return;
				}
				else {
					record.SetInsurance(coverage1);
				}
			}
		} else {
			record.setStatus("Not Found");
		}
	}
}
