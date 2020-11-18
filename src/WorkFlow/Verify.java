package WorkFlow;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.json.JSONException;
import org.json.JSONObject;

import Database.Columns.DMEColumns;
import Database.Columns.LeadColumns;
import Database.Tables.Tables;
import DoctorChase.ConfirmDoctor;
import DoctorChase.DoctorAnswer;
import DoctorChase.FaxStatus;
import DoctorChase.MessageStatus;
import Fax.Drug;
import Fax.Fax;
import PBM.InsuranceFilter;
import PBM.InsuranceType;
import ResponseBuilder.DoctorResponseBuilder;
import client.Database;
import client.DatabaseClient;
import client.FaxedRecord;
import client.InfoDatabase;
import client.Record;
import client.RingCentralClient;
import doctor.Doctor;
import doctor.JSONParser;
import images.Script.ScriptException;
import images.ScriptNew;
import images.ScriptNew.ScriptNewException;

@Path("Verify")
public class Verify {

	
	
	@GET
	@Path("TEST")
	@Produces
	public String TEST() {
		StringBuilder sb = new StringBuilder();
		Database client = new Database("MT_MARKETING");
		try {
			sb.append("<table border='1' id='records'>");
			sb.append("<tr bgcolor='#d3d3d3'>");
			sb.append("<td>Name</td>");
			sb.append("<td>Phone/td>");
			sb.append("</tr>");
			if(!client.login())
				return "<h1>Failed to Login</h1>";
			ResultSet set = client.select(Tables.LEADS, null, LeadColumns.GROUP+" = ?", new String[] {"RXCVSD"});
			while(set.next()) {
				Record record = new Record(set);
				if(!JointPain(record))
					continue;
				if(!VerifyDMEState(record.getState()))
					continue;
				if(!CheckFaxDisposition(record.getFaxDisposition()))
					continue;
				if(!client.select(Tables.DME, null, DMEColumns.PHONE+" = ?", new String[] {record.getPhone()}).next()) {
					int add = client.insert("DME_Leads_Requal", DMEColumns.ADD_TO_DATABASE_COLUMNS, DMEColumns.ConverToStringArray(record));
					if(add==1) {
						sb.append("<tr>");
						sb.append("<td>"+record.getFirstName()+" "+record.getLastName()+"</td>");
						sb.append("<td>"+record.getPhone()+"</td>");
						sb.append("</tr>");
					}
				}
			}
			sb.append("</table>");
		} catch(SQLException ex) {
			return ex.getMessage();
		} finally {
			if(client!=null)
				client.close();
		}
		return sb.toString();
	}
	private boolean JointPain(Record record) {
		switch(record.getPainLocation()) {
			case "Back":
			case "Knees":
			case "Hip":
			case "Elbow":
			case "Wrist":
			case "Shoulder":
			case "Ankle":
				return true;
			default:
				return false;
		}
	}
	private static boolean VerifyDMEState(String state) {
		switch(state.toUpperCase()) {
			case "AL":
			case "AK":
			case "CO":
			case "CT":
			case "HI":
			case "LA":
			case "MD":
			case "MS":
			case "ND":
			case "NV":		
				return false;
			default:
				return true;
		}
	}
	private static boolean CheckFaxDisposition(String disposition) {
		switch(disposition) {
			case FaxStatus.WRONG_DOCTOR:
			case FaxStatus.DECEASED:
				return false;
			default:
				return true;
		}
	}
	@GET
	@Path("UHGCount")
	@Produces(MediaType.TEXT_HTML)
	public String UHGCount() {
		StringBuilder sb = new StringBuilder();
		Database client = new Database("MT_MARKETING");
		try {
			if(!client.login())
				return "<h1>Failed to Login</h1>";
			sb.append("<table border='1' id='records'>");
			sb.append("<tr bgcolor='#d3d3d3'>");
			sb.append("<td>Name</td>");
			sb.append("<td>Phone</td>");
			sb.append("<td>Status</td>");
			sb.append("</tr>");
			ResultSet set = client.select(Tables.UHG, null, LeadColumns.AFID+" = ?", new String[] {"UHG"});
			int totalCount = 0;
			while(set.next()) {
				Record record = new Record(set);
				int confirm = set.getInt(LeadColumns.CONFIRM_DOCTOR);
				String fax_dispo = set.getString(LeadColumns.FAX_DISPOSITION);
				String status = null;
				if(!fax_dispo.equalsIgnoreCase(""))
					status = fax_dispo;
				else if(confirm==1) {
					status = "Doctor Confirmed";
					totalCount++;
				}
				else
					status = "Not Confirmed Yet";
				sb.append("<tr>");
				sb.append("<td>"+record.getFirstName()+" "+record.getLastName()+"</td>");
				sb.append("<td>"+record.getPhone()+"</td>");
				sb.append("<td>"+status+"</td>");
				sb.append("</tr>");
			}
			sb.append("<tr bgcolor='#d3d3d3'>");
			sb.append("<td>Total</td>");
			sb.append("<td></td>");
			sb.append("<td>"+totalCount+"</td>");
			sb.append("</tr>");
			sb.append("</table>");
			
		} catch(SQLException ex) {
			return ex.getMessage();
		} finally {
			if(client!=null)
				client.close();
		}
		return sb.toString();
	}
	@GET
	@Path("GetUHG")
	@Produces(MediaType.TEXT_HTML)
	public String GetUHG() {
		StringBuilder sb = new StringBuilder();
		Database client = new Database("MT_MARKETING");
		try {
			if(!client.login())
				return "<h1>Failed to Login</h1>";
			//CREAT TABLE
			sb.append("<table border='1' id='records'>");
			sb.append("<tr bgcolor='#d3d3d3'>");
			sb.append("<td>Name</td>");
			sb.append("<td>Phone</td>");
			sb.append("<td>DOB</td>");
			sb.append("<td>Address</td>");
			sb.append("<td>NPI</td>");
			sb.append("<td>Dr First</td>");
			sb.append("<td>Dr Last</td>");
			sb.append("<td>Dr Address</td>");
			sb.append("<td>Dr City</td>");
			sb.append("<td>Dr State</td>");
			sb.append("<td>Dr Zip</td>");
			sb.append("<td>Dr Phone</td>");
			sb.append("<td>Dr Fax</td>");
			sb.append("<td>Attention</td>");
			sb.append("<td>Status</td>");
			sb.append("<td>Submit</td>");
			sb.append("</tr>");
			ResultSet set = client.select(Tables.UHG, null, LeadColumns.CONFIRM_DOCTOR+" = "+ConfirmDoctor.NOT_CONFIRMED+" AND "+LeadColumns.FAX_DISPOSITION+" = '' AND ("+LeadColumns.DOCTOR_ANSWER+" = "+DoctorAnswer.DEFAULT+" OR ("+LeadColumns.DOCTOR_ANSWER+" = "+DoctorAnswer.NO_ONE_ANSWERED+" AND "+LeadColumns.LAST_UPDATED+" < DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL - 30 MINUTE)))", null);
			int count = 1;
			while(set.next()) {
				String name = set.getString(LeadColumns.FIRST_NAME)+" "+set.getString(LeadColumns.LAST_NAME);
				String phone = set.getString(LeadColumns.PHONE_NUMBER);
				String dob = set.getString(LeadColumns.DOB);
				String address = set.getString(LeadColumns.ADDRESS)+" "+set.getString(LeadColumns.CITY)+" "+set.getString(LeadColumns.STATE)+" "+set.getString(LeadColumns.ZIP);
				String npi = set.getString(LeadColumns.NPI);
				String dr_first = set.getString(LeadColumns.DR_FIRST);
				String dr_last = set.getString(LeadColumns.DR_LAST);
				String dr_address = set.getString(LeadColumns.DR_ADDRESS1);
				String dr_city = set.getString(LeadColumns.DR_CITY);
				String dr_state = set.getString(LeadColumns.DR_STATE);
				String dr_zip = set.getString(LeadColumns.DR_ZIP);
				String dr_phone = set.getString(LeadColumns.DR_PHONE);
				String dr_fax = set.getString(LeadColumns.DR_FAX);
				String statuses = "<select id='"+count+"'>"
						+ "<option value=''></option>"
						+ "<option value='Confirmed Doctor'>Confirmed Doctor</option>"
						+ "<option value='Wrong Doctor'>Wrong Doctor</option>"
						+ "<option value='No Answer'>No Answer</option>"
						+ "</select>";
				sb.append("<tr>");
				sb.append("<td>"+name+"</td>");
				sb.append("<td>"+phone+"</td>");
				sb.append("<td>"+dob+"</td>");
				sb.append("<td>"+address+"</td>");
				sb.append("<td>"+npi+"</td>");
				sb.append("<td>"+dr_first+"</td>");
				sb.append("<td>"+dr_last+"</td>");
				sb.append("<td>"+dr_address+"</td>");
				sb.append("<td>"+dr_city+"</td>");
				sb.append("<td>"+dr_state+"</td>");
				sb.append("<td>"+dr_zip+"</td>");
				sb.append("<td>"+dr_phone+"</td>");
				sb.append("<td>"+dr_fax+"</td>");
				sb.append("<td>                </td>");
				sb.append("<td>"+statuses+"</td>");
				sb.append("<td><input type='button' value='Submit "+name+"' onclick='Submit("+count+")' /></td>");
				sb.append("</tr>");
				count++;
			}
		} catch(SQLException ex) {
			return ex.getMessage();
		} finally {
			if(client!=null)
				client.close();
		}
		sb.append("</table>");
		return sb.toString();
	}
	
	@GET
	@Path("SubmitUHG")
	@Produces(MediaType.TEXT_HTML)
	public String SubmitUHG(@QueryParam("phone_number") String phone_number,
							@QueryParam("npi") String npi,
							@QueryParam("dr_first") String dr_first,
							@QueryParam("dr_last") String dr_last,
							@QueryParam("dr_address") String dr_address,
							@QueryParam("dr_city") String dr_city,
							@QueryParam("dr_state") String dr_state,
							@QueryParam("dr_zip") String dr_zip,
							@QueryParam("dr_phone") String dr_phone,
							@QueryParam("dr_fax") String dr_fax,
							@QueryParam("attention") String attention,
							@QueryParam("disposition") String disposition) {
		Database client = new Database("MT_MARKETING");
		try {
			if(!client.login())
				return "Failed Response";
			int value = 0;
			switch(disposition) {
				case "Confirmed Doctor":
					value = client.update(Tables.UHG, LeadColumns.DR_INFO, new Object[] {npi,dr_first,dr_last,dr_address,dr_city,dr_state,dr_zip,dr_phone,dr_fax}, LeadColumns.PHONE_NUMBER+" = '"+phone_number+"'");
					if(value==1) {
						ResultSet set = client.select(Tables.UHG, null, LeadColumns.PHONE_NUMBER+" = ?", new String[] {phone_number});
						if(set.next()) {
							Record record = new Record(set);
							record.setAfid("UHG");
							int insert = 0;
							insert = client.insert(Tables.LEADS, LeadColumns.ADD_RECORD, LeadColumns.ToStringArray(record));
							if(insert==1) {
								client.update(Tables.UHG, new String[] {LeadColumns.CONFIRM_DOCTOR}, new String[] {ConfirmDoctor.CONFIRMED}, LeadColumns.PHONE_NUMBER+" = '"+phone_number+"'");
								String messageId = FaxRecord(record,attention);
								if(messageId!=null) {
									if(messageId.startsWith("1")) {
										client.update(Tables.LEADS, new String[] {LeadColumns.CONFIRM_DOCTOR,LeadColumns.MESSAGE_ID,LeadColumns.MESSAGE_STATUS}, new Object[] {ConfirmDoctor.CONFIRMED,messageId,MessageStatus.QUEUED}, LeadColumns.PHONE_NUMBER+" = '"+phone_number+"'");
										return "Succesfully Confirmed Doctor and Faxed";
									}
								}
								return "Succesfully Confirmed Doctor and Faxed Failed";
							}
							return "ERROR INSERTING RECORD";
						}
						else
							return "Record Not Found";
					}
					else
						return "Error Updating Record";
				case "Wrong Doctor":
					value = client.update(Tables.UHG, new String[] {LeadColumns.FAX_DISPOSITION}, new Object[] {FaxStatus.WRONG_DOCTOR}, LeadColumns.PHONE_NUMBER+" = '"+phone_number+"'");
					if(value==1)
						return "Succesfully updated as Wrong Doctor";
					else 
						return "Error Updating Record";
				case "No Answer":
					value = client.update(Tables.UHG, new String[] {LeadColumns.DOCTOR_ANSWER}, new Object[] {DoctorAnswer.NO_ONE_ANSWERED}, LeadColumns.PHONE_NUMBER+" = '"+phone_number+"'");
					if(value==1)
						return "Succesfully updated as No Answer";
					else 
						return "Error Updating Record";
				default:
					return "Unknown Disposition";
			}
		} catch(SQLException ex) {
			return ex.getMessage();
		} catch (InvalidPasswordException e) {
			return e.getMessage();
		} catch (IOException e) {
			return e.getMessage();
		} catch (URISyntaxException e) {
			return e.getMessage();
		} catch (ScriptNewException e) {
			return e.getMessage();
		} catch (JSONException e) {
			return e.getMessage();
		} catch (InterruptedException e) {
			return e.getMessage();
		} catch (ScriptException e) {
			return e.getMessage();
		} finally {
			if(client!=null)
				client.close();
		}
	}
	private String FaxRecord(Record record,String attention) throws InvalidPasswordException, IOException, URISyntaxException, ScriptNewException, JSONException, InterruptedException, ScriptException {
		RingCentralClient ringClient = Fax.GetRingCentralClient(record,"MT_MARKETING");
		ScriptNew script = Fax.GetScriptNew(record, ringClient.number);
		script.setAttention(attention);
		if(record.getPharmacy().equalsIgnoreCase("All_Pharmacy")) 
			AddSingleScripts(record,script);
		else 
			script.PopulateRecord(null, null,"");
		for(String product: record.getProducts()) {
			switch(product.trim()) {
				case "Migraines":
					script.AddScript(Drug.GetMigraineScript(record), null,"Patient suffers from Migraines");
					break;
				case "Anti-Fungal":
					script.AddScript(Drug.GetFootSoak(record), null,"Patient suffers from skin infections or flaky skin");
					break;
				case "Podiatry":
					script.AddScript(Drug.GetAntiFungal(record), null,"Patient suffers from foot fungus or infections on the feet");
					break;
				case "Acid Reflux":
					script.AddScript(Drug.Omeprazole, null,"Patient suffers from chronic Acid Reflux or Gerd");
					break;
				case "Dermatitis":
					script.AddScript(Drug.GetDermatitis(record), null,"Psoriasis, Eczema, Dermatitis or Dry/Irritated skin.");
					break;
			}
		}
		script.reducePDFSize();
		/*
		* CHECK IF FAX SCRIPT IS NULL
		*/
		if(script.getFile()==null)
			return null;
		/*
		 * FAX SCRIPT 
		 */
		String messageId = GetFaxFromJSON(record,ringClient,script.getFile());
		return messageId;
	}
	private void AddSingleScripts(Record record,ScriptNew script) throws InvalidPasswordException, IOException, URISyntaxException, ScriptNewException {
		int type = InsuranceFilter.GetInsuranceType(record);
		switch(record.getBin()) {
			case "015581":
			case "015589":
			{
				script.PopulateRecord(Drug.Diflorasone180, null,"");
				if(record.getCurrentAge()>=60)
					script.AddScript(Drug.Methocarbamol750, null,"");
				else
					script.AddScript(Drug.Cyclobenzaprine5mg, null,"");	
				script.AddScript(Drug.OmegaEthylEster, null,"");
				break;
			}
			case "610014":
			case "003858":
			{
				if(type==InsuranceType.Type.PRIVATE_INSURANCE) {
					script.PopulateRecord(Drug.Ketoprofen240, null,"");
					script.AddScript(Drug.Diflorasone180, null,"");
					script.AddScript(Drug.Chlorzoxazone250, null,"");
					script.AddScript(Drug.Lidocaine250, null,"");
					break;
				} 
				else {
					script.PopulateRecord(Drug.Ketoprofen180, null,"");
					script.AddScript(Drug.Diflorasone180, null,"");
					script.AddScript(Drug.Chlorzoxazone250, null,"");
					script.AddScript(Drug.Lidocaine250, null,"");
					break;
				}
			}
			case "017010":
			{
				if(type==InsuranceType.Type.PRIVATE_INSURANCE) {
					script.PopulateRecord(Drug.Fenoprofen400, null,"");
					script.AddScript(Drug.Diflorasone180, null,"");
					script.AddScript(Drug.Chlorzoxazone250, null,"");
					script.AddScript(Drug.Cyclobenzaprine7_5mg, null,"");
					break;
				}
				else {
					script.PopulateRecord(Drug.Diflorasone180, null,"");
					script.AddScript(Drug.Naproxen375, null,"");
					script.AddScript(Drug.OmegaEthylEster, null,"");
					break;
				}
			}
			case "610097":
			{
				script.PopulateRecord(Drug.Ketoprofen180, null,"");
				script.AddScript(Drug.Clobetasol180, null,"");
				script.AddScript(Drug.OmegaEthylEster, null,"");
				break;
			}
			case "610279":
			case "610494":
			{
				script.PopulateRecord(Drug.Cyclobenzaprine7_5mg, null,"");
				script.AddScript(Drug.Clobetasol180, null,"");
				script.AddScript(Drug.Ketoprofen180, null,"");
				break;
			}
			default:
				script.PopulateRecord(null, null,"");
				break;
			}
	}
	@GET
	@Path("GetAllFaxStatus")
	@Produces(MediaType.TEXT_PLAIN) 
	public String GetFaxStatuses(@QueryParam("database") String database) throws IOException, JSONException, InterruptedException {
		if(database==null)
			return "Please provide database";
		HashMap<String, RingCentralClient> map = new HashMap<String,RingCentralClient>(); 
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
		int count = 0;
		for(FaxedRecord record: list) {
			Record r = null;
			if(record.getLeadType().equalsIgnoreCase("DME")) {
				r = client.GetFullRecord(record.getId(), "DME_Leads");
				r.setPharmacy("MT_Medical_Supplies");
			}
			else
				r = client.GetFullRecord(record.getId(), "Leads");
			String number = Fax.GetRingCentralClient(r,database).number;
			if(map.containsKey(number))
				ring = map.get(number);
			else {
				ring = Fax.GetRingCentralClient(r,database);
				map.put(ring.number, ring);
			}
			if(!ring.loggedIn)
				if(!ring.login())
					return "FAILED TO LOGIN";
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
		count++;
		if(count>=30)
			break;
		}
		client.close();
		sb.append("</table>");
		return sb.toString();
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
			return DoctorResponseBuilder.DoctorResponse(false,"DOCTOR NPI IS DEACTIVE","").toString();
		Doctor d = JSONParser.CreateDoctor(sb.toString());
		switch(d.getEnumeration_type()) {
			case "NPI-1":
				break;
			case "NPI-2":
				return DoctorResponseBuilder.DoctorResponse(false,"INVALID MUST GET DR NPI NOT OFFICE NPI","").toString();
			default:
				return DoctorResponseBuilder.DoctorResponse(false,"UNKNOWN ENUMERATION TYPE","").toString();
		}
		InfoDatabase db = new InfoDatabase();
		for(int i = 0;i<d.getCode().length;i++) {
			String code = d.getCode()[i];
			String s = db.CheckDrType(code);
			switch(s) {
				case "true":
					db.close();
					return DoctorResponseBuilder.DoctorResponse(true,"",d.getType()[i]).toString();
				case "false":
					continue;
				default:
					db.close();
					return DoctorResponseBuilder.DoctorResponse(false,"INVALID DOCTOR",d.getType()[i]).toString();
			}		
		}
		return DoctorResponseBuilder.DoctorResponse(false,"INVALID DOCTOR",d.getType()[0]).toString();
 	}
 	
 	@GET
 	@Path("VerifyDoctor")
 	@Produces(MediaType.TEXT_HTML)
	public String VerifyDoctor2(@QueryParam("npi") String npi) throws MalformedURLException, IOException, JSONException {
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
			return "<h2>Dr NPI is DEACTIVE</h2>";
		Doctor d = JSONParser.CreateDoctor(sb.toString());
		switch(d.getEnumeration_type()) {
			case "NPI-1":
				break;
			case "NPI-2":
				return "<h2>INVALID MUST GET DR NPI NOT OFFICE NPI</h2>";
			default:
				return "<h2>UNKNOWN ENUMERATION TYPE</h2>";
		}
		InfoDatabase db = new InfoDatabase();
		for(int i = 0;i<d.getCode().length;i++) {
			String code = d.getCode()[i];
			String s = db.CheckDrType(code);
			switch(s) {
				case "true":
					db.close();
					return "<h2>VALID DOCTOR</h2>";
				case "false":
					continue;
				default:
					db.close();
					return "<h2>PLEASE GET NEW DOCTOR IT IS INVALID DOCTOR</h2>";
			}		
		}
		return "<h2>PLEASE GET NEW DOCTOR IT IS INVALID DOCTOR</h2>";
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
 	private String GetFaxFromJSON(Record record,RingCentralClient ring,File file) throws IOException, JSONException, InterruptedException, ScriptException {
 		String message_id = null;
 		boolean success = false;
 		do {
 			JSONObject result = Fax.SendFax(record,ring,file);
 			message_id = ring.GetStatusFromRingCentral(result);
			success = ring.IsRingCentralResponseSuccesful(result);
			if(!success) {
				switch(message_id) {
					case RingCentralClient.Errors.TOO_MANY_REQUEST:
						Thread.sleep(result.getInt("Rate"));
						break;
					default:
						return message_id;
				}
			}
		} while(!success);
 		return message_id;
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
						return status;
						//RingCentralClient fax = Fax.GetRingCentralClient(null,"");
						//fax.login();
						//return GetMessageFromJSON(record, fax);
					default:
						return status;
				}
			}
		} while(!success);
 		return status;
 	}
}

