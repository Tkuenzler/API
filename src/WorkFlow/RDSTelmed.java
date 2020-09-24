package WorkFlow;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Database.Columns.TelmedColumns;
import Database.Tables.Tables;
import JSONParameters.BlueMosiacParameters;
import JSONParameters.TriageParameters;
import ResponseBuilder.TelmedResponse;
import client.DMEClient;
import client.Database;
import client.DatabaseClient;
import client.InfoDatabase;
import client.Record;
import client.RoadMapClient;

@Path("RDSTelmed")
public class RDSTelmed {
	

	@POST
	@Path("SubmitTriage")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String SubmitRecord(@Context HttpServletRequest request,String data) throws JSONException {
		
		String ip = request.getRemoteHost();
		Database client = new Database("MT_MARKETING");
		ResultSet set = null;
		InfoDatabase info = null;
		JSONObject record = null;
		try {
			record = new JSONObject(data);
			info = new InfoDatabase();
			String callCenter = info.GetCallCenter(record.getString("agent"));
			if(!client.login())
				return "";
			set = client.select(Tables.TELMED, null, TelmedColumns.PHONE+" = ?", new String[] {record.getString(TriageParameters.PHONE)});
			int value = 0;
			//UPDATE RECORD
			if(set.next()) 
				value = client.update(Tables.TELMED, TelmedColumns.ADD_TO_DATABASE_COLUMNS , TelmedColumns.ConvertJSON(record, callCenter, ip, TriageParameters.TRIAGE_COMPLETE),TelmedColumns.PHONE+" = '"+record.getString(TriageParameters.PHONE)+"'");
			//INSERT RECORD
			else 
				value = client.insert(Tables.TELMED,TelmedColumns.ADD_TO_DATABASE_COLUMNS,TelmedColumns.ConvertJSON(record, callCenter, ip, TriageParameters.TRIAGE_COMPLETE));
			if(value==1)
				return TelmedResponse.BuildSuccessfulResponse(record).toString();
			else
				return TelmedResponse.BuildFailedResponse(value).toString();
		} catch(SQLException ex) {
			StringBuilder b = new StringBuilder();
			for(StackTraceElement s: ex.getStackTrace())
				b.append(s.toString());
			return TelmedResponse.BuildFailedResponse(client.test(Tables.TELMED, new String[] {TelmedColumns.TELMED_STATUS,TelmedColumns.TRIAGE} , new String[] {TriageParameters.TRIAGE_COMPLETE,"'"+record.toString()+"'"},TelmedColumns.PHONE+" = '"+record.getString(TriageParameters.PHONE)+"'"), ex.getErrorCode()).toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			StringBuilder b = new StringBuilder();
			for(StackTraceElement s: e.getStackTrace())
				b.append(s.toString());
			return TelmedResponse.BuildFailedResponse(b.toString()).toString();
		} finally {
			try {
				if(set!=null)set.close();
				if(client!=null) client.close();
				if(info!=null) info.close();
			} catch(SQLException ex) {
				
			}
		}
	}
	
	@POST
	@Path("SaveTriage")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String SaveTriage(@Context HttpServletRequest request,String data) throws JSONException {
		JSONObject obj = new JSONObject(data);
		String ip = request.getRemoteHost();
		InfoDatabase info = new InfoDatabase();
		String callCenter = info.GetCallCenter(obj.getString("agent"));
		info.close();
		Database client = new Database("MT_MARKETING");
		ResultSet set = null;
		try {
			if(!client.login())
				return TelmedResponse.BuildFailedResponse("FAILED TO LOGIN TO DATABASE").toString();
			if(!client.exists("TELMED", null, TelmedColumns.PHONE+" = ?", new String[] {obj.getString(BlueMosiacParameters.PHONE)})) {
				int value = client.insert("TELMED",TelmedColumns.ADD_TO_DATABASE_COLUMNS,TelmedColumns.ConvertJSON(obj, callCenter, ip,TriageParameters.TRIAGE_INCOMPLETE));
				if(value==1)
					return TelmedResponse.BuildSuccessfulResponse(obj).toString();
				else 
					return TelmedResponse.BuildFailedResponse(value).toString();
			}
			else {
				int value = client.update("TELMED", new String[] {TelmedColumns.TELMED_STATUS,TelmedColumns.TRIAGE}, new String[] {TriageParameters.TRIAGE_INCOMPLETE,obj.toString()},TelmedColumns.PHONE+" = '"+obj.getString(TriageParameters.PHONE)+"'");
				if(value==1)
					return TelmedResponse.BuildSuccessfulResponse(obj).toString();
				else 
					return TelmedResponse.BuildFailedResponse(value).toString();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return TelmedResponse.BuildFailedResponse(e.getMessage(), e.getErrorCode()).toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return TelmedResponse.BuildFailedResponse(e.getMessage()).toString();
		} finally {
			try {
				if(set!=null) set.close();
				if(client!=null)client.close();
			} catch(SQLException ex) {
				
			}
		}
		
	}
	
	@GET
	@Path("GetTriage")
	@Produces(MediaType.APPLICATION_JSON)
	public String GetTriage(@QueryParam("phone_number") String phone_number) throws JSONException {
		Database client = new Database("MT_MARKETING");
		ResultSet set = null;
		try {
			if(!client.login())
				return TelmedResponse.BuildFailedResponse("FAILED TO LOGIN TO DATABASE").toString();
			set = client.select("TELMED", null,TelmedColumns.PHONE+" = ?", new String[] {phone_number});
			set.next();
			JSONObject obj = new JSONObject(set.getString(TelmedColumns.TRIAGE));
			return TelmedResponse.BuildSuccessfulResponse(obj).toString();
		} catch (SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
			return TelmedResponse.BuildFailedResponse(ex.getMessage(),ex.getErrorCode()).toString();
		} finally {
			try {
				if(set!=null) set.close();
				if(client!=null) client.close();
			} catch(SQLException ex) {
				
			}
		}
		
	}
	
	
	@GET
	@Path("CheckTelmed")
	@Produces(MediaType.TEXT_PLAIN)
	public String CheckTelmed(@QueryParam("phone") String phone) {
		DatabaseClient client = new DatabaseClient("MT_MARKETING");
		String status = client.GetTelmedStatus(phone);
		client.close();
		if(status==null) {
			DMEClient dme = new DMEClient("MT_MARKETING");
			status = dme.GetTelmedStatus(phone);
			dme.close();
		}
		if(status==null)
			status = "RECORD NOT FOUND";
		return status;
	}
	
	@POST
 	@Path("AddDME")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
 	@Produces(MediaType.TEXT_HTML)
	public String AddDME(@Context HttpServletRequest request,
			@FormParam("first_name") String first_name,
			@FormParam("last_name") String last_name,
			@FormParam("phone") String phone,
			@FormParam("agent") String agent,
			@FormParam("alternate_phone") String alternate_phone,
			@FormParam("dob") String dob,
			@FormParam("gender") String gender,
			@FormParam("address") String address,
			@FormParam("city") String city,
			@FormParam("state") String state,
			@FormParam("zip") String zip,
			@FormParam("ssn") String ssn,
			@FormParam("carrier") String carrier,
			@FormParam("policy_id") String policy_id,
			@FormParam("plan") String plan,
			@FormParam("source") String source,
			@FormParam("co_insurance") String co_insurance,
			@FormParam("deductible") String deductible,
			@FormParam("brace_list") String brace_list) {
		Record record = new Record();
		record.setFirstName(first_name.toUpperCase());
		record.setLastName(last_name.toUpperCase());
		record.setDob(dob);
		record.setGender(gender.toUpperCase());
		record.setPhone(phone.toUpperCase());
		record.setAddress(address.toUpperCase());
		record.setCity(city.toUpperCase());
		record.setState(state.toUpperCase());
		record.setZip(zip.toUpperCase());
		record.setSsn(ssn);
		record.setCarrier(carrier);
		record.setPolicyId(policy_id.toUpperCase());
		record.setAgent(agent);
		record.setSource(source);
		InfoDatabase info = new InfoDatabase();
		String callCenter = info.GetCallCenter(agent);
		info.close();
		DMEClient client = new DMEClient("MT_MARKETING");
		if(client.isDMEDuplicate(record))
			return "Duplicate Record";
		else 
			return client.AddDME(record,callCenter,plan,co_insurance,deductible,brace_list.substring(0,brace_list.length()-1));
	}
	
	private String ExtractRDSId(String response) {
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
	private String GetInsertTime()  {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}
	private void incrementTelmed(String group) {
		if(group.equalsIgnoreCase(""))
			return;
		InfoDatabase info = new InfoDatabase();
		int connect = 0;
		while(info.connect==null && connect<8) {
			info = new InfoDatabase();
			connect++;
		}
		if(info.connect==null)
			return;
		info.incrementTelmed(group);	
		info.close();
	}
	private RoadMapClient GetRoadMap(String roadmap) {
		RoadMapClient map = new RoadMapClient(roadmap);
		int connect = 0;
		while(map.connect==null && connect<8) {
			map = new RoadMapClient(roadmap);
			connect++;
		}
		if(map.connect==null)
			return null;
		return map;
	}
	private String switchToAllFamilyPharmacyProducts(String productSuggestions) {
		String[] products = productSuggestions.split(",");
		ArrayList<String> newProducts = new ArrayList<String>();
		for(String s: products) {
			switch(s) {
				case "Topical_Pain_MTK":
					newProducts.add("Topical_Pain_MTK");
					newProducts.add("Topical_Nsaid_Diflorasone");
					newProducts.add("Muscle_Relaxer_Cyclobenzaprine");
					break;
				case "DrySkin_MTK":
					newProducts.add("Dry_Skin_Calcipotrene");
					break;
				case "Migraines_MTK":
					newProducts.add("Migraines_MTK");
					break;
				case "AntiFungal_MTK":
					newProducts.add("Anti_Fungal_Econazole");
					break;
				default: 
					newProducts.add(s); 
					break;
			}
		}
		newProducts.add("Metabolic_Omega3");
		StringBuilder response = new StringBuilder();
		for(int i = 0;i<newProducts.size();i++) {
			if(i==newProducts.size()-1)
				response.append(newProducts.get(i));
			else
				response.append(newProducts.get(i)+", ");
		}
		return response.toString();
	}
	
}
