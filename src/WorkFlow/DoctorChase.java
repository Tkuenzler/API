package WorkFlow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import DoctorChase.MessageStatus;
import Fax.Fax;
import client.DatabaseClient;
import client.FaxedRecord;
import client.Record;
import client.RingCentralClient;
import images.Script;
import images.Script.ScriptException;

@Path("DoctorChase")

public class DoctorChase {
	private String TABLE = "Leads";
	private String admin = "6666";
	private String password = "tk6847";
	private class Vici {
		public static final String URL = "https://mtkmarketing.vicihost.com/agc/api.php";
	}
	
	@GET
 	@Path("Clear")
 	@Produces(MediaType.APPLICATION_JSON)
	public String Clear(@QueryParam("id") String id,
			@QueryParam("agent") String agent,
			@QueryParam("database") String database) {
		DatabaseClient client = new DatabaseClient(database);
		while(client.connect==null)
			client = new DatabaseClient(database);
		int clear = client.setUsed(id, TABLE, 0,agent);
		client.close();
		switch(clear) {
			case 0:
				return "RECORD NOT FOUND";
			case 1:
				return "RECORD CLEARED";
			case -1:
				return "ERROR";
			default: 
				return "UNEXPECTED ERROR";
		}
	}
	
	@GET
 	@Path("Next")
 	@Produces(MediaType.APPLICATION_JSON)
	public String Next(
			@QueryParam("id") String id,
			@QueryParam("phone") String phone,
			@QueryParam("npi") String npi,
			@QueryParam("drFirst") String drFirst,
			@QueryParam("drLast") String drLast,
			@QueryParam("drAddress") String drAddress,
			@QueryParam("drCity") String drCity,
			@QueryParam("drState") String drState,
			@QueryParam("drZip") String drZip,
			@QueryParam("drPhone") String drPhone,
			@QueryParam("drFax") String drFax,
			@QueryParam("disposition") String disposition,
			@QueryParam("agent") String agent,
			@QueryParam("confirmDoctor") String confirmDoctor,
			@QueryParam("database") String database) {
		Record oldRecord = new Record();
		oldRecord.setId(id);
		oldRecord.setNpi(npi);
		oldRecord.setDrFirst(drFirst);
		oldRecord.setDrLast(drLast);
		oldRecord.setDrAddress(drAddress);
		oldRecord.setDrCity(drCity);
		oldRecord.setDrState(drState);
		oldRecord.setDrZip(drZip);
		oldRecord.setDrPhone(drPhone);
		oldRecord.setDrFax(drFax);
		oldRecord.setDoctorConfirmed(Boolean.parseBoolean(confirmDoctor));
		oldRecord.setAgent(agent);
		if(!disposition.equalsIgnoreCase("NO ANSWER") || !disposition.equalsIgnoreCase("Confirmed Doctor"))
			oldRecord.setFaxDisposition(disposition);
		DatabaseClient client = new DatabaseClient(database);
		while(client.connect==null)
			client = new DatabaseClient(database);
		int update = -2;
		if(!id.equalsIgnoreCase("")) {
			if(disposition.equalsIgnoreCase("NO ANSWER"))
				update = client.setCallBack(oldRecord,TABLE);
			else
				update = client.UpdateRecordForDr(oldRecord, TABLE,0);
		}		
		client.resetUsedForAgent(agent);
		/*
		 * -2 means no id was passed
		 * 0 means update failed
		 * 1 means update was successful
		 */
		String roadmap = GetRoadMap(database);
		Record record = client.GetLiveLead(TABLE,agent,roadmap);
		if(record==null) 
			record = client.ReFax(TABLE,agent,roadmap);
		if(record==null) 
			record = client.ReFaxNotTaggedAgent(TABLE,agent,roadmap);
		if(record==null)
			return new Record().returnPatientJSON(99,"");
		client.IncrementChaseCount(record);
		client.close();
		return record.returnPatientJSON(update,"");
	}
		
	@GET
	@Path("Fax")
	@Produces(MediaType.TEXT_PLAIN)
	public String Fax(@QueryParam("id") String id,
			@QueryParam("npi") String npi,
			@QueryParam("drFirst") String drFirst,
			@QueryParam("drLast") String drLast,
			@QueryParam("drAddress") String drAddress,
			@QueryParam("drCity") String drCity,
			@QueryParam("drState") String drState,
			@QueryParam("drZip") String drZip,
			@QueryParam("drPhone") String drPhone,
			@QueryParam("drFax") String drFax,
			@QueryParam("disposition") String disposition,
			@QueryParam("confirmDoctor") String confirmDoctor,
			@QueryParam("agent") String agent,
			@QueryParam("pharmacy") String pharmacy,
			@QueryParam("database") String database,
			@QueryParam("record_type") String record_type) {
		Record oldRecord = new Record();
		oldRecord.setId(id);
		oldRecord.setNpi(npi);
		oldRecord.setDrFirst(drFirst);
		oldRecord.setDrLast(drLast);
		oldRecord.setDrAddress(drAddress);
		oldRecord.setDrCity(drCity);
		oldRecord.setDrState(drState);
		oldRecord.setDrZip(drZip);
		oldRecord.setDrPhone(drPhone);
		oldRecord.setDrFax(drFax);
		oldRecord.setFaxDisposition(disposition);
		oldRecord.setDoctorConfirmed(Boolean.parseBoolean(confirmDoctor));
		oldRecord.setPharmacy(pharmacy);
		oldRecord.setAgent(agent);
		if(record_type==null)
			record_type = "EMPTY";
		//Check to see if Fax is being sent already
		DatabaseClient client = new DatabaseClient(database);
		if(client.IsAlreadyFaxing(id)) {
			client.close();
			return "THIS SCRIPT IS ALREADY BEING FAXED";
		}
		//Check to see if doctor is being confirmed
		if(oldRecord.isDoctorConfirmed()) {
			client.ResetMessageStatus(oldRecord,TABLE);
			client.UpdateChaseDate(oldRecord,TABLE);
		}
		//Update Record
		int updated = client.UpdateRecordForDr(oldRecord, TABLE, 1);
		//Pull full record
		Record record = client.GetFullRecord(id,TABLE);
		if(record==null)
			return "RECORD IS NULL";
		//Get faxing client and login
		RingCentralClient ringClient = Fax.GetRingCentralClient(record,database);
		if(!ringClient.login())
			return "LOGIN FAILED";
		String result = null;
		try {
			Script script = Fax.GetScript(record,database);
			script.CreateAndPopulate(record, ringClient.number);
			result = GetFaxFromJSON(record,ringClient,script);
			if(result.startsWith("1")) {
				String messageId = result;
				client.IncrementFax(record.getPhone());
				client.UpdateMessageId(record, TABLE, messageId,MessageStatus.QUEUED);
				client.AddFaxedRecord(record,messageId,agent,record_type);
				CheckFaxedRecords(client,agent,database);
				switch(updated) {
					case 1:
						return "Successfully Faxed Message ID: "+messageId+" and data was updated";
					case 0:
						return "Successfully Faxed Message ID: "+messageId+" data was not updated";
					default:
						return "Successfully Faxed Message ID: "+messageId+" unknown error code: "+updated;
				}
			}
			else {
				return "FAILED TO SEND FAX BECAUSE: "+result;
			}
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			return "FAILED: "+e.getMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return "FAILED: "+e.getMessage();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			return "FAILED: "+e.getMessage();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return "FAILED: "+e.getMessage();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			return "FAILED: "+e.getMessage();
		} finally {
			ringClient.close();
			client.close();
		}
	}
	
	@GET
	@Path("Call")
	@Produces(MediaType.TEXT_PLAIN)
	public String Call(@QueryParam("user") String user, @QueryParam("phone") String phone) {
		URIBuilder b = null;
		URL url = null;
		try {
			b = new URIBuilder(Vici.URL);
			b.addParameter("source", "test");
			b.addParameter("user", admin);
			b.addParameter("pass", password);
			b.addParameter("agent_user", user);
			b.addParameter("function", "external_dial");
			b.addParameter("value", phone);
			b.addParameter("phone_code", "1");
			b.addParameter("search", "NO");
			b.addParameter("preview", "NO");
			b.addParameter("focus", "NO");
			url = b.build().toURL();
			HttpURLConnection connection;
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = null;
			StringBuilder builder = new StringBuilder();
			while((line=rd.readLine())!=null)
				builder.append(line);
			return builder.toString();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return e.getMessage();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				return e.getMessage();
			}
	}
	
	@GET
	@Path("LookUp")
	@Produces(MediaType.TEXT_PLAIN)
	public String LookUp(@QueryParam("phone") String phone,
			@QueryParam("database") String database) {
		DatabaseClient client = new DatabaseClient(database);
		Record record = client.GetRecordByPhone(phone, TABLE);
		client.close();
		return record.returnPatientJSON(0,"");
	}
	
	@GET
	@Path("Hangup")
	@Produces() 
	public String Hangup(@QueryParam("user") String user) {
		String HANGUP = Vici.URL+"?source=test&user="+admin+"&pass="+password+"&agent_user="+user+"&function=external_hangup&value=1";
		String DISPOSITION = Vici.URL+"?source=test&user="+admin+"&pass="+password+"&agent_user="+user+"&function=external_status&value=A";
		String PAUSE = Vici.URL+"?source=test&user="+admin+"&pass="+password+"&agent_user="+user+"&function=external_pause&value=PAUSE";
		StringBuilder response = new StringBuilder();
		try {
			String[] functions = {HANGUP,DISPOSITION,PAUSE};
			for(String s: functions) {
				URL url = new URL(s);
				HttpURLConnection connection;
				connection = (HttpURLConnection) url.openConnection();
				connection.connect();
				BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line = null;
				StringBuilder builder = new StringBuilder();
				while((line=rd.readLine())!=null)
					builder.append(line);
				response.append(builder.toString());
				response.append("\n");
				if(s.equalsIgnoreCase(PAUSE))
					Thread.sleep(7000);
				else
					Thread.sleep(500);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
		return response.toString();	
	} 
	
	private void CheckFaxedRecords(DatabaseClient client,String agent,String database) throws IOException, ScriptException, URISyntaxException, JSONException, InterruptedException {
		List<FaxedRecord> list = client.getMessageIds(agent);
		if(list==null)
			return;
		for(FaxedRecord faxedRecord: list) {
			Record r = client.GetRecordById(faxedRecord.getId(), TABLE);
			RingCentralClient ringCentral = Fax.GetRingCentralClient(r, database);
			if(!ringCentral.login())
				continue;
			if(faxedRecord.getStatus().equalsIgnoreCase(MessageStatus.QUEUED)) {
				String status = GetMessageFromJSON(faxedRecord,ringCentral);
				if(!MessageStatus.IsValidStatus(status))  
					continue;
				else
					faxedRecord.setStatus(status);
			}
			switch(faxedRecord.getStatus()) {
				case MessageStatus.SENDING_FAILED:
					if(client.Attempted5Times(faxedRecord)) {
						//Unconfirm Doctor and Delete Record
						client.UnconfirmDoctor(faxedRecord);
						client.DeleteFaxedRecord(faxedRecord.getPhone());
						continue;
					}
					Script script = Fax.GetScript(r,database);
					script.CreateAndPopulate(r, ringCentral.number);
					JSONObject result = Fax.SendFax(r,ringCentral,script.getFile());
					if(ringCentral.IsRingCentralResponseSuccesful(result)) {
						String id = ringCentral.GetStatusFromRingCentral(result);
						faxedRecord.setMessage_id(id);
						faxedRecord.setStatus(MessageStatus.QUEUED);
						client.UpdateMessageStatus(faxedRecord);
						client.IncrementFax(faxedRecord.getPhone());
						client.UpdateFaxedRecord(faxedRecord);
					}
					break;
				case MessageStatus.SENT:
					client.UpdateMessageStatus(faxedRecord);
					client.UpdateFaxedRecord(faxedRecord);
					client.DeleteFaxedRecord(faxedRecord.getPhone());
					break;
				case MessageStatus.QUEUED:
					client.UpdateFaxedRecord(faxedRecord);
					break;
				case "ERROR":
					break;
				default:
					continue;
			}
		}
	}
	
	private String GetRoadMap(String database) {
		switch(database) {
			case "MT_MARKETING":
				return "TELMED_ROADMAP";
			case "Coman_Marketing":
				return "COMAN_ROADMAP";
			default:
				return "";
		}
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
					default:
						return status;
				}
			}
		} while(!success);
 		return status;
 	}
	private String GetFaxFromJSON(Record record,RingCentralClient ring,Script script) throws IOException, JSONException, InterruptedException, ScriptException {
 		String status = null;
 		boolean success = false;
 		do {
 			JSONObject result = Fax.SendFax(record,ring,script.getFile());
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
}
