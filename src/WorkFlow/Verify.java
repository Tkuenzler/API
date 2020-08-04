package WorkFlow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Fax.Fax;
import Fax.MessageStatus;
import client.DatabaseClient;
import client.FaxedRecord;
import client.InfoDatabase;
import client.Record;
import client.RingCentralClient;
import doctor.Doctor;
import doctor.JSONParser;
import images.Script;
import images.Script.ScriptException;

@Path("Verify")
public class Verify {

	@GET
	@Path("GetAllFaxStatus")
	@Produces(MediaType.TEXT_PLAIN) 
	public String GetFaxStatuses(@QueryParam("database") String database) throws IOException, JSONException, InterruptedException {
		if(database==null)
			return "Please provide database";
		DatabaseClient client = new DatabaseClient(database);
		List<FaxedRecord> list = client.getQueued();
		RingCentralClient ring = null;
		StringBuilder sb = new StringBuilder();
		sb.append("<table border='1'>");
		sb.append("<tr bgcolor='#d3d3d3'>");
		sb.append("<td>ID</td>");
		sb.append("<td>Message ID</td>");
		sb.append("<td>Status</td>");
		sb.append("<td>Record Type</td>");
		sb.append("<td>Pharmacy</td>");
		sb.append("</tr>");
		for(FaxedRecord record: list) {
			ring = Fax.GetRingCentralClient(record.getPharmacy(),database);
			if(!ring.login())
				continue;
			String status = GetMessageFromJSON(record,ring);
			if(MessageStatus.IsValidStatus(status)) {
				record.setStatus(status);
				client.UpdateFaxedRecord(record);
				client.UpdateMessageStatus(record);
			}
			if(status.equalsIgnoreCase(MessageStatus.SENT))
				client.DeleteFaxedRecord(record.getPhone());
			sb.append("<tr>");
			sb.append("<td>"+record.getId()+"</td>");
			sb.append("<td>"+record.getMessage_id()+"</td>");
			sb.append("<td>"+status+"</td>");
			sb.append("<td>"+record.getRecord_type()+"</td>");
			sb.append("<td>"+record.getPharmacy()+"</td>");
			sb.append("</tr>");
		}
		client.close();
		sb.append("</table>");
		return sb.toString();
	}
	@GET
	@Path("ReFaxFailed")
	@Produces(MediaType.TEXT_HTML)
	public String ReFaxFailed(@QueryParam("database") String database) throws IOException, ScriptException, URISyntaxException, JSONException, InterruptedException {
		if(database==null)
			return "Please provide database";
		DatabaseClient client = new DatabaseClient(database);
		List<FaxedRecord> list = client.getSendingFailed();
		RingCentralClient ring = null;
		StringBuilder sb = new StringBuilder();
		sb.append("<table border='1'>");
		sb.append("<tr bgcolor='#d3d3d3'>");
		sb.append("<td>ID</td>");
		sb.append("<td>Message ID</td>");
		sb.append("<td>Status</td>");
		sb.append("<td>Record Type</td>");
		sb.append("<td>Pharmacy</td>");
		sb.append("</tr>");
		for(FaxedRecord record: list) {
			ring = Fax.GetRingCentralClient(record.getPharmacy(),database);
			if(!ring.login())
				continue;
			Record r = client.GetFullRecord(record.getId(), "Leads");
			Script script = Fax.GetScript(r,database);
			String result = GetFaxFromJSON(r,ring,script);
			if(result.startsWith("1")) {
				record.setMessage_id(result);
				record.setStatus(MessageStatus.QUEUED);
				client.IncrementFax(record.getPhone());
				client.UpdateMessageId(r, "Leads", result,MessageStatus.QUEUED);
				client.UpdateFaxedRecord(record);
			}
			sb.append("<tr>");
			sb.append("<td>"+record.getId()+"</td>");
			sb.append("<td>"+record.getMessage_id()+"</td>");
			sb.append("<td>"+MessageStatus.QUEUED+"</td>");
			sb.append("<td>"+record.getRecord_type()+"</td>");
			sb.append("<td>"+record.getPharmacy()+"</td>");
			sb.append("</tr>");
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		sb.append("</table>");
		client.close();
		return sb.toString();
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
 	@GET
 	@Path("Doctor")
 	@Produces(MediaType.APPLICATION_JSON)
	public String VerifyDoctor(@QueryParam("npi") String npi) throws MalformedURLException, IOException, JSONException {
 		HttpURLConnection connection = (HttpURLConnection) new URL("https://npiregistry.cms.hhs.gov/api?version=2.0&number="+npi).openConnection();
		connection.connect();
		BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while((line=rd.readLine())!=null)
			sb.append(line);
		rd.close();
		connection.disconnect();
		if(CheckError(sb.toString()))
			return createDoctorRepsonse("DEACTIVE","DOCTOR NPI IS DEACTIVE").toString();
		Doctor d = JSONParser.CreateDoctor(sb.toString());
		switch(d.getEnumeration_type()) {
			case "NPI-1":
				break;
			case "NPI-2":
				return createDoctorRepsonse("INVALID MUST GET DR NPI NOT OFFICE NPI",d.getEnumeration_type()).toString();
			default:
				return createDoctorRepsonse("UNKNOWN ENUMERATION TYPE",d.getEnumeration_type()).toString();
		}
		InfoDatabase db = new InfoDatabase();
		for(int i = 0;i<d.getCode().length;i++) {
			String code = d.getCode()[i];
			String s = db.CheckDrType(code);
			switch(s) {
				case "true":
					db.close();
					return createDoctorRepsonse("VALID",d.getType()[i]).toString();
				case "false":
					continue;
				default:
					db.close();
					return createDoctorRepsonse("INVALID",d.getType()[i]).toString();
			}		
		}
		return createDoctorRepsonse("INVALID",d.getType()[0]).toString();
 	}
 	
 	@GET
 	@Path("SetRequalified")
 	@Produces(MediaType.APPLICATION_JSON)
 	public String SetRequalified(@QueryParam("phonenumber") String phonenumber) {
 		InfoDatabase info = new InfoDatabase();
 		int update = info.SetRecordAsRequalified(phonenumber);
 		info.close();
 		switch(update) {
	 		case 1: return "Successfully Requalified";
	 		case 0: return "Not Updated";
	 		default: return "Error: "+update;
 		}
 	}
 	
 	private boolean CheckError(String line) {
 		try {
			JSONObject obj = new JSONObject(line);
			if(obj.has("Errors"))
				return true;
			else
				return false;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
 		
 	}
 	private JSONObject createDoctorRepsonse(String valid,String drType) throws JSONException {
 		JSONObject obj = new JSONObject();
 		obj.put(ResponseKeys.VALID, valid);
 		obj.put(ResponseKeys.DR_TYPE, drType);
 		return obj;
 	}
 	private String GetFaxFromJSON(Record record,RingCentralClient ring,Script script) throws IOException, JSONException, InterruptedException, ScriptException {
 		String status = null;
 		boolean success = false;
 		do {
 			JSONObject result = Fax.SendFax(record,ring,script);
 			status = ring.GetStatusFromRingCentral(result);
			success = ring.IsRingCentralResponseSuccesful(result);
			if(!success) {
				switch(status) {
					case RingCentralClient.Errors.TOO_MANY_REQUEST:
						Thread.sleep(result.getInt("Rate"));
						break;
					default:
						return status;
				}
			}
		} while(!success);
 		return status;
 	}
 	private String GetMessageFromJSON(FaxedRecord record,RingCentralClient ring) throws IOException, JSONException, InterruptedException {
 		String status = null;
 		boolean success = false;
 		do {
 			JSONObject result = ring.getMessageById(record.getMessage_id());
 			status = ring.GetStatusFromRingCentral(result);
			success = ring.IsRingCentralResponseSuccesful(result);
			if(!success) {
				switch(status) {
					case RingCentralClient.Errors.TOO_MANY_REQUEST:
						Thread.sleep(result.getInt("Rate"));
						break;
					case RingCentralClient.Errors.INVALID_URL:
						RingCentralClient fax = Fax.GetRingCentralClient("","");
						fax.login();
						return GetMessageFromJSON(record, fax);
					default:
						return status;
				}
			}
		} while(!success);
 		return status;
 	}
 	private class ResponseKeys {
 		public static final String SUCCESS = "Success";
 		public static final String ERRORS = "Errors";
 		public static final String ERROR = "Error";
 		
 		public static final String VALID = "VALID";
 		public static final String DR_TYPE = "DR_TYPE";
 	}
}

