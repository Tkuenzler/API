package WorkFlow;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.http.client.utils.URIBuilder;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.json.JSONException;
import org.json.JSONObject;
import Database.Columns.DMEColumns;
import Database.Columns.FaxedColumns;
import Database.Columns.LeadColumns;
import Database.Columns.RoadMapColumns;
import Database.Query.Queries;
import Database.Query.Query;
import Database.Tables.Tables;
import Date.Date;
import DoctorChase.ConfirmDoctor;
import DoctorChase.Disposition;
import DoctorChase.LeadType;
import DoctorChase.MessageStatus;
import DoctorChase.RecordType;
import DoctorChase.Used;
import Fax.Drug;
import Fax.Fax;
import JSONParameters.DoctorChaseParameters;
import client.Database;
import client.FaxedRecord;
import client.Record;
import client.RingCentralClient;
import images.Script;
import images.Script.ScriptException;
import images.ScriptNew;
import images.ScriptNew.ScriptNewException;


@Path("Chase")
public class Chase {
	private String admin = "6666";
	private String password = "tk6847";
	private class Vici {
		public static final String URL = "https://mtkmarketing.vicihost.com/agc/api.php";
	}
	
	@POST
	@Path("NextRx")
	@Consumes(MediaType.APPLICATION_JSON)
 	@Produces(MediaType.TEXT_PLAIN)
	public String NextRx(String json) throws JSONException {
		JSONObject obj = new JSONObject(json);
		Database db = new Database("MT_MARKETING");
		Database info = new Database("Info_Table");
		Record record = null;
		//LOGIN TO DATABASE
		try {
			if(!db.login() || !info.login())
				return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("LOGIN FAILED").toString();
			/*
			 * GET FULL RECORD AND THE RECORD BEFORE GET
			 */
			if(!obj.getString(DoctorChaseParameters.ID).equalsIgnoreCase("")) {
				/*
				 * GET FULL RECORD
				 */
				ResultSet set = db.select(Tables.LEADS, null, LeadColumns.ID+" = ?", new String[] {obj.getString(DoctorChaseParameters.ID)});
				if(!set.next()) 
					return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("UNABLE TO FIND RECORD").toString();
				record = new Record(set);
				/*
				 * RESET TO NOT USED
				 */
				int reset = db.update(Tables.LEADS, new String[] {LeadColumns.USED}, new String[] {Used.NOT_USED}, LeadColumns.DR_CHASE_AGENT+" = '"+obj.getString(DoctorChaseParameters.AGENT)+"'");
				/*
				 * UPDATE OLD RECORD
				 */
				int update = db.update(Tables.LEADS, LeadColumns.FAX_CHASE, DoctorChaseParameters.CreateRXDrChaseArray(obj,record), LeadColumns.ID+" = '"+obj.getString(DoctorChaseParameters.ID)+"'");
				if(update!=1)
					return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("FAILED TO UPDATE RECORD").toString();
			}
			ResultSet set0 = null,set1 = null,set2 = null;
			
			String[] pharmacies = GetPharmacies();
			if(pharmacies==null)
				return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("ERROR GETTING ROAD MAP").toString();
			String pharmacyQuery = CreateQuery(pharmacies);
			/*
			 * GET RECORDS WITH INVALID FAX NUMBER 
			 */
			set0 = db.selectSort(Tables.LEADS, null, Queries.Select.INVALID_FAX+" AND "+pharmacyQuery, null,new String[] {LeadColumns.LAST_UPDATED},new String[] {Query.Order.DESCENDING});
			while(set0.next()) {
				record = new Record(set0);
				if(!CheckTimeZone(record,info))
					continue;
				RingCentralClient ringClient = Fax.GetRingCentralClient(record,"MT_MARKETING");
				JSONObject response = DoctorChaseParameters.CreateJSON(record,RecordType.NOT_CONFIRMED,ringClient.number);
				return ResponseBuilder.DoctorChaseReponse.BuildSuccesfulNext(response).toString();
			}
			/*
			 * GET RECORDS THAT HAVE BEEN NOT BEEN CONFIRMED
			 */
			set1 = db.selectSort(Tables.LEADS, null, Queries.Select.NOT_CONFIRMED+" AND "+pharmacyQuery, null,new String[] {LeadColumns.LAST_UPDATED},new String[] {Query.Order.DESCENDING});
			while(set1.next()) {
				record = new Record(set1);
				if(!CheckTimeZone(record,info))
					continue;
				RingCentralClient ringClient = Fax.GetRingCentralClient(record,"MT_MARKETING");
				JSONObject response = DoctorChaseParameters.CreateJSON(record,RecordType.NOT_CONFIRMED,ringClient.number);
				return ResponseBuilder.DoctorChaseReponse.BuildSuccesfulNext(response).toString();
			}
			
			/*
			 * GET RECORDS THAT HAVE BEEN NOT BEEN CONFIRMED
			 */
			set2 = db.selectSort(Tables.LEADS, null, Queries.Select.CONFIRMED_NOT_RECEIVED+" AND "+pharmacyQuery, null,new String[] {LeadColumns.LAST_UPDATED},new String[] {Query.Order.DESCENDING});
			while(set2.next()) {
				record = new Record(set2);
				if(!CheckTimeZone(record,info))
					continue;
				RingCentralClient ringClient = Fax.GetRingCentralClient(record,"MT_MARKETING");
				JSONObject response = DoctorChaseParameters.CreateJSON(record,RecordType.NOT_CONFIRMED,ringClient.number);
				return ResponseBuilder.DoctorChaseReponse.BuildSuccesfulNext(response).toString();
			}
			
			
			return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("NO MORE RECORDS LEFT").toString();
		} catch(SQLException ex) {
			return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse(ex.getMessage()).toString();
		} finally {
			try {
				if(record!=null)
					db.update(Tables.LEADS, new String[] {LeadColumns.USED,LeadColumns.DR_CHASE_AGENT}, new String[] {Used.USED,obj.getString(DoctorChaseParameters.AGENT)}, LeadColumns.ID+" = '"+record.getId()+"'");
				if(db!=null)db.close();
				if(info!=null)info.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@POST
	@Path("NextDME")
	@Consumes(MediaType.APPLICATION_JSON)
 	@Produces(MediaType.TEXT_PLAIN)
	public String NextDME(String json) throws JSONException {
		JSONObject obj = new JSONObject(json);
		Database db = new Database("MT_MARKETING");
		Database info = new Database("Info_Table");
		Record record = null;
		//LOGIN TO DATABASE
		try {
			if(!db.login() || !info.login())
				return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("LOGIN FAILED").toString();
			/*
			 * GET FULL RECORD AND THE RECORD BEFORE GET
			 */
			if(!obj.getString(DoctorChaseParameters.ID).equalsIgnoreCase("")) {
				/*
				 * GET FULL RECORD
				 */
				ResultSet set = db.select(Tables.DME, null, DMEColumns.ID+" = ?", new String[] {obj.getString(DoctorChaseParameters.ID)});
				if(!set.next()) 
					return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("UNABLE TO FIND RECORD").toString();
				record = new Record(set);
				/*
				 * RESET TO NOT USED
				 */
				int reset = db.update(Tables.DME, new String[] {DMEColumns.USED}, new String[] {Used.NOT_USED}, DMEColumns.DR_CHASE_AGENT+" = '"+obj.getString(DoctorChaseParameters.AGENT)+"'");
				/*
				 * UPDATE OLD RECORD
				 */
				int update = db.update(Tables.DME, DMEColumns.FAX_CHASE, DMEColumns.CreateDMEDrChaseArray(obj,record), DMEColumns.ID+" = '"+obj.getString(DoctorChaseParameters.ID)+"'");
				if(update!=1)
					return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("FAILED TO UPDATE RECORD").toString();
			}
			ResultSet set1 = null,set2 = null,set3 = null;
			/*
			 * GET RECORDS THAT HAVE BEEN NOT BEEN CONFIRMED WITH 11 DIGIT POLICY ID
			 */
			set1 = db.selectSort(Tables.DME, null, Queries.SelectDME.DME_NOT_CONFIRMED_WITH_POLICY_ID, new String[] {},new String[] {DMEColumns.LAST_UPDATED},new String[] {Query.Order.DESCENDING});
			while(set1.next()) {
				record = new Record(set1,true);
				if(!CheckTimeZone(record,info))
					continue;
				JSONObject response = DoctorChaseParameters.CreateJSON(record,RecordType.NOT_CONFIRMED_DME,"15614869163");
				return ResponseBuilder.DoctorChaseReponse.BuildSuccesfulNext(response).toString();
			}

			/*
			 * GET RECORDS THAT HAVE BEEN NOT BEEN CONFIRMED
			 */
			
			set2 = db.selectSort(Tables.DME, null, Queries.SelectDME.DME_NOT_CONFIRMED, new String[] {},new String[] {DMEColumns.LAST_UPDATED},new String[] {Query.Order.DESCENDING});
			while(set2.next()) {
				record = new Record(set2,true);
				if(!CheckTimeZone(record,info))
					continue;
				JSONObject response = DoctorChaseParameters.CreateJSON(record,RecordType.NOT_CONFIRMED_DME,"15614869163");
				return ResponseBuilder.DoctorChaseReponse.BuildSuccesfulNext(response).toString();
			}
			
			/*
			 * GET RECORDS THAT HAVE BEEN NOT BEEN CONFIRMED
			 */
			set3 = db.selectSort(Tables.DME, null, Queries.SelectDME.DME_CONFIRMED_NO_RESPONSE, new String[] {},new String[] {DMEColumns.LAST_UPDATED},new String[] {Query.Order.DESCENDING});
			while(set3.next()) {
				record = new Record(set3,true);
				if(!CheckTimeZone(record,info))
					continue;
				JSONObject response = DoctorChaseParameters.CreateJSON(record,RecordType.CONFIRMED_NOT_RECEIVED_DME,"15614869163");
				return ResponseBuilder.DoctorChaseReponse.BuildSuccesfulNext(response).toString();
			}
			
			return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("NO MORE RECORDS LEFT").toString();
		} catch(SQLException ex) {
			return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse(ex.getMessage()).toString();
		} finally {
			try {
				if(db!=null) {
					if(record!=null)
						db.update(Tables.DME, new String[] {DMEColumns.USED,DMEColumns.DR_CHASE_AGENT}, new String[] {Used.USED,obj.getString(DoctorChaseParameters.AGENT)}, LeadColumns.ID+" = '"+record.getId()+"'");
					db.close();
				}
				if(info!=null)info.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse(e.getMessage()).toString();
			}
		}
	}
	
	@POST
	@Path("FaxRx")
	@Consumes(MediaType.APPLICATION_JSON)
 	@Produces(MediaType.TEXT_PLAIN)
	public String Fax(String json) throws JSONException {
		JSONObject obj = new JSONObject(json);
		Database db = new Database("MT_MARKETING");
		Record record = null;
		ScriptNew script = null;
		ResultSet fullRecordSet = null, faxRecordsSet = null;
		try {
			/*
			 * LOGIN TO DATABASE
			 */
			if(!obj.getString(DoctorChaseParameters.DISPOSITION).equalsIgnoreCase(Disposition.CONFIRMED_DOCTOR))
				return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("YOU CAN ONLY FAX CONFIRMED DOCTORS").toString();
			if(!db.login())
				return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("LOGIN FAILED").toString();
						
			/*
			 * CHECK TO SEE IF RECORD IS FAXING
			 */
			fullRecordSet = db.select(Tables.FAXED, null, FaxedColumns.ID+"= ?", new String[] {obj.getString(DoctorChaseParameters.ID)});
			if(fullRecordSet.next())
				return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("RECORD IS ALREADY FAXING").toString();
			
			/*
			 * UPDATE OLD RECORD
			 */
			
			
			int update = db.update(Tables.LEADS, LeadColumns.FAX_CHASE, DoctorChaseParameters.CreateRXDrChaseArray(obj,record), LeadColumns.ID+" = '"+obj.getString(DoctorChaseParameters.ID)+"'");
			if(update!=1)
				return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("UNABLE TO UPDATE RECORD").toString();
			
			/*
			 * GET FULL RECORD
			 */
			ResultSet set = db.select(Tables.LEADS, null, LeadColumns.ID+" = ?", new String[] {obj.getString(DoctorChaseParameters.ID)});
			if(!set.next()) 
				return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("UNABLE TO FIND RECORD").toString();
			record = new Record(set);
			
			
			
			/*
			 * GET AND LOGIN TO RING CENTRAL
			 */
			RingCentralClient ringClient = Fax.GetRingCentralClient(record,"MT_MARKETING");
			if(!ringClient.login())
				return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("UNABLE TO LOGIN TO RING CENTRAL").toString();
			
			/*
			 * GET SCRIPT AND ADD ADDITIONAL SCRIPTS
			 */
			
			script = Fax.GetScriptNew(record, ringClient.number);
			script.setAttention(obj.getString(DoctorChaseParameters.ATTENTION));
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
				return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("ERROR CREATING SCRIPT").toString();
			/*
			 * FAX SCRIPT 
			 */
			String messageId = GetFaxFromJSON(record,ringClient,script.getFile());
			
			/*
			 * CHECK TO SEE IF FAXED CORRECTLY
			 */
			if(!messageId.startsWith("1"))
				return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("FAILED FAXING SCRIPT: "+messageId).toString();
		
			/*
			 * INSET INTO FAXED TABLE AND UPDATE MESSAGE_ID AND STATUS
			 */
			byte[] pdfData = new byte[(int) script.getFile().length()];
			DataInputStream dis = new DataInputStream(new FileInputStream(script.getFile()));
			dis.readFully(pdfData);  // read from file into byte[] array
			dis.close();
			int insertFaxedRecord = db.insert(Tables.FAXED, FaxedColumns.ALL, FaxedColumns.ToStringArray(obj, messageId,pdfData));
			if(insertFaxedRecord!=1)
				return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("FAILED TO ADD TO FAXED TABLE").toString();
			
			int updateLead = db.update(Tables.LEADS, new String[] {LeadColumns.MESSAGE_ID,LeadColumns.MESSAGE_STATUS}, new String[] {messageId,MessageStatus.QUEUED}, LeadColumns.ID+"= '"+record.getId()+"'");
			if(updateLead!=1)
				return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("FAILED TO UPDATE LEAD").toString();
			
			/*
			 * GET FAX STATUS AND MESSAGE STATUS OF REMAINING QUEUED LEADS
			 */
			List<FaxedRecord> list = new ArrayList<FaxedRecord>();
			faxRecordsSet = db.select(Tables.FAXED, null, FaxedColumns.AGENT+" = ? AND LAST_UPDATED <= DATE_ADD(CURRENT_TIMESTAMP, INTERVAL - 5 MINUTE)", new String[] {obj.getString(DoctorChaseParameters.AGENT)});
			while(faxRecordsSet.next()) {
				list.add(new FaxedRecord(faxRecordsSet));
			}
			if(list.size()==0)
				return ResponseBuilder.DoctorChaseReponse.BuildSuccesfulNext("SUCCESSFULLY FAXED RECORD").toString();
			HashMap<String, RingCentralClient> map = new HashMap<String,RingCentralClient>(); 
			for(FaxedRecord faxedRecord: list) {
				switch(faxedRecord.getLeadType()) 
				{
					case LeadType.RX:
					case LeadType.BLANK:
						CheckRxResults(faxedRecord,db,map);
						break;
					case LeadType.DME:
						CheckDMEResults(faxedRecord,db,map);
						break;
				}
			}
			return ResponseBuilder.DoctorChaseReponse.BuildSuccesfulNext("SUCCESSFULLY FAXED RECORD").toString();
		} catch(SQLException ex) {
			return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse(ex.getMessage()).toString();
		} catch (InvalidPasswordException ex) {
			return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse(ex.getMessage()).toString();
		} catch (IOException ex) {
			return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse(ex.getMessage()).toString();
		} catch (URISyntaxException ex) {
			return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse(ex.getMessage()).toString();
		} catch (InterruptedException ex) {
			return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse(ex.getMessage()).toString();
		} catch (ScriptException ex) {
			return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse(ex.getMessage()).toString();
		} catch (ScriptNewException ex) {
			return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse(ex.getMessage()).toString();
		} finally {
			if(script!=null)
				if(script.getFile()!=null)script.getFile().delete();
			if(db!=null)db.close();
		}	
	}
	@POST
	@Path("FaxDME")
	@Consumes(MediaType.APPLICATION_JSON)
 	@Produces(MediaType.TEXT_PLAIN)
	public String FaxDME(String json) throws JSONException {
		JSONObject obj = new JSONObject(json);
		Database db = new Database("MT_MARKETING");
		Record record = null;
		ResultSet fullRecordSet = null, faxRecordsSet = null;
		try {
			/*
			 * LOGIN TO DATABASE
			 */
			
			if(!db.login())
				return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("LOGIN FAILED").toString();
			/*
			 * CHECK TO SEE IF RECORD IS FAXING
			 */
			fullRecordSet = db.select(Tables.FAXED, null, FaxedColumns.ID+"= ?", new String[] {obj.getString(DoctorChaseParameters.ID)});
			if(fullRecordSet.next())
				return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("RECORD IS ALREADY FAXING").toString();
			 
			/*
			 * UPDATE OLD RECORD
			 */
			
			
			int update = db.update(Tables.DME, DMEColumns.FAX_CHASE, DMEColumns.CreateDMEDrChaseArray(obj,record), DMEColumns.ID+" = '"+obj.getString(DoctorChaseParameters.ID)+"'");
			if(update!=1)
				return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("UNABLE TO UPDATE RECORD").toString();
			
			
			/*
			 * GET FULL RECORD
			 */
			ResultSet set = db.select(Tables.DME, null, LeadColumns.ID+" = ?", new String[] {obj.getString(DoctorChaseParameters.ID)});
			if(!set.next()) 
				return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("UNABLE TO FIND RECORD").toString();
			record = new Record(set,true);
			
			/*
			 * TAG AS MT MEDICAL SUPPLIES DME LEAD
			 */
			
			record.setPharmacy("MT_Medical_Supplies");
			
			
			/*
			 * GET AND LOGIN TO RING CENTRAL
			 */
			RingCentralClient ringClient = Fax.GetRingCentralClient(record,"MT_MARKETING");
			if(!ringClient.login())
				return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("UNABLE TO LOGIN TO RING CENTRAL").toString();
			
			/*
			 * GET SCRIPT AND ADD ADDITIONAL SCRIPTS
			 */
			
			ScriptNew script = new ScriptNew(record,Fax.class.getClassLoader().getResource(Script.DME),ringClient.number);
			script.PopulateDMEScript("",obj.getString(DoctorChaseParameters.ATTENTION),obj.getString(DoctorChaseParameters.AGENT));
			/*
			* CHECK IF FAX SCRIPT IS NULL
			*/
			if(script.getFile()==null)
				return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("ERROR CREATING SCRIPT").toString();
			/*
			 * FAX SCRIPT 
			 */
			String messageId = GetFaxFromJSON(record,ringClient,script.getFile());
			
			/*
			 * CHECK TO SEE IF FAXED CORRECTLY
			 */
			if(!messageId.startsWith("1"))
				return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("FAILED FAXING SCRIPT: "+messageId).toString();
			
			/*
			 * INSET INTO FAXED TABLE AND UPDATE MESSAGE_ID AND STATUS
			 */
			byte[] pdfData = new byte[(int) script.getFile().length()];
			DataInputStream dis = new DataInputStream(new FileInputStream(script.getFile()));
			dis.readFully(pdfData);  // read from file into byte[] array
			dis.close();
			int insertFaxedRecord = db.insert(Tables.FAXED, FaxedColumns.ALL, FaxedColumns.ToStringArray(obj, messageId,pdfData));
			if(insertFaxedRecord!=1)
				return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("FAILED TO ADD TO FAXED TABLE").toString();
			
			int updateLead = db.update(Tables.DME, new String[] {DMEColumns.MESSAGE_ID,DMEColumns.MESSAGE_STATUS}, new String[] {messageId,MessageStatus.QUEUED}, DMEColumns.ID+"= '"+record.getId()+"'");
			if(updateLead!=1)
				return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("FAILED TO UPDATE LEAD").toString();
			
			/*
			 * GET FAX STATUS AND MESSAGE STATUS OF REMAINING QUEUED LEADS
			 */
			List<FaxedRecord> list = new ArrayList<FaxedRecord>();
			faxRecordsSet = db.select(Tables.FAXED, null, FaxedColumns.AGENT+" = ? AND LAST_UPDATED <= DATE_ADD(CURRENT_TIMESTAMP, INTERVAL - 5 MINUTE)", new String[] {obj.getString(DoctorChaseParameters.AGENT)});
			while(faxRecordsSet.next()) {
				list.add(new FaxedRecord(faxRecordsSet));
			}
			if(list.size()==0)
				return ResponseBuilder.DoctorChaseReponse.BuildSuccesfulNext("SUCCESSFULLY FAXED RECORD").toString();
			HashMap<String, RingCentralClient> map = new HashMap<String,RingCentralClient>(); 
			for(FaxedRecord faxedRecord: list) {
				switch(faxedRecord.getLeadType()) 
				{
					case LeadType.RX:
					case LeadType.BLANK:
						CheckRxResults(faxedRecord,db,map);
						break;
					case LeadType.DME:
						CheckDMEResults(faxedRecord,db,map);
						break;
				}
				
			}
			return ResponseBuilder.DoctorChaseReponse.BuildSuccesfulNext("SUCCESSFULLY FAXED RECORD").toString();
		} catch(SQLException ex) {
			return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse(ex.getMessage()).toString();
		} catch (InvalidPasswordException ex) {
			return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse(ex.getMessage()).toString();
		} catch (IOException ex) {
			return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse(ex.getMessage()).toString();
		} catch (URISyntaxException ex) {
			return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse(ex.getMessage()).toString();
		} catch (InterruptedException ex) {
			return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse(ex.getMessage()).toString();
		} catch (ScriptException ex) {
			return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse(ex.getMessage()).toString();
		} catch (ScriptNewException ex) {
			return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse(ex.getMessage()).toString();
		} finally {
			if(db!=null)db.close();
		}
	}
	
	@GET
	@Path("LookUp")
	@Produces(MediaType.TEXT_PLAIN)
	public String LookUp(@QueryParam("phone") String phone, @QueryParam("lead_type") String lead_type) throws JSONException {
		Database db = new Database("MT_MARKETING");
		try {
			if(!db.login())
				return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("LOGIN FAILED").toString();
			ResultSet set = null;
			Record record = null;
			switch(lead_type) {
			case LeadType.RX:
				set =  db.select(Tables.LEADS, null, LeadColumns.PHONE_NUMBER+" = ?", new String[] {phone});
				if(set.next()) {
					record = new Record(set);
					RingCentralClient ringClient = Fax.GetRingCentralClient(record,"MT_MARKETING");
					JSONObject response =  DoctorChaseParameters.CreateJSON(record,RecordType.RECEIVED_NO_RESPONSE,ringClient.number);
					return ResponseBuilder.DoctorChaseReponse.BuildSuccesfulNext(response).toString();
				}
				else
					return  ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("NO RECORD FOUND").toString();
			case LeadType.DME:
				set =  db.select(Tables.DME, null, DMEColumns.PHONE+" = ?", new String[] {phone});
				if(set.next()) {
					record = new Record(set,true);
					JSONObject response =  DoctorChaseParameters.CreateJSON(record,RecordType.RECEIVED_NO_RESPONSE,"15614869163");
					return ResponseBuilder.DoctorChaseReponse.BuildSuccesfulNext(response).toString();
				}
				else
					return  ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("NO RECORD FOUND").toString();
				
			default:
				return  ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("NO RECORD FOUND").toString();
			}
		} catch(SQLException ex) {
			return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse(ex.getMessage()).toString();
		} finally {
			if(db!=null)db.close();
		}
	}
	
	@GET
 	@Path("Clear")
 	@Produces(MediaType.APPLICATION_JSON)
	public String Clear(@QueryParam("id") String id,@QueryParam("lead_type") String lead_type) throws JSONException {
		Database db = new Database("MT_MARKETING");
		try {
			if(!db.login())
				return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("LOGIN FAILED").toString();
			int updated = 0;
			switch(lead_type) {
				case LeadType.RX:
				case LeadType.BLANK: 
					updated = db.update(Tables.LEADS, new String[] {LeadColumns.USED}, new String[] {Used.NOT_USED}, LeadColumns.ID+" = '"+id+"'");
					break;
				case LeadType.DME:
					updated = db.update(Tables.DME, new String[] {DMEColumns.USED}, new String[] {Used.NOT_USED}, DMEColumns.ID+" = '"+id+"'");
					break;
			}
			if(updated==1)
				return ResponseBuilder.DoctorChaseReponse.BuildSuccesfulNext("SUCCESFULLY CLEARED").toString();
			else
				return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse("FAILED TO CLEAR").toString();
		} catch(SQLException ex) {
			return ResponseBuilder.DoctorChaseReponse.BuildFailedResponse(ex.getMessage()).toString();
		} finally {
			if(db!=null)db.close();
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
	
	private String GetFaxFromJSON(Record record,RingCentralClient ring,File file) throws IOException, JSONException, InterruptedException, ScriptException {
 		String message_id = null;
 		boolean success = false;
 		do {
 			JSONObject result = Fax.SendFax(record,ring,file);
 			message_id = ring.GetStatusFromRingCentral(result);
			success = ring.IsRingCentralResponseSuccesful(result);
			if(!success) {
				switch(message_id) {
					/*
					case RingCentralClient.Errors.TOO_MANY_REQUEST:
						Thread.sleep(result.getInt("Rate"));
						break;
					*/
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
					default:
						return status;
				}
			}
		} while(!success);
 		return status;
 	}
	private boolean CheckTimeZone(Record record,Database info) throws SQLException {
		ResultSet set1 = info.select(Tables.INCOME_TABLE, null, "ZIP_CODE = ?", new String[] {record.getZip()});
		ResultSet set2 = null;
		int offset = 0;
		if(set1.next()) {
			offset = set1.getInt("offset");
		}
		else {
			set2 = info.selectAs(Tables.INCOME_TABLE, new String[] {"ROUND(AVG(`offset`))"},"offset", "STATE = ?", new String[] {record.getState()});
			if(set2.next()) 
				offset = set2.getInt("offset");
			else
				offset = 0;
		}
		if(set1!=null)set1.close();
		if(set2!=null)set2.close();
		if(Date.isBefore("18:00:00",offset) && Date.isAfter("08:00:00",offset))
			return true;
		else
			return false;
		
	}
	private void CheckDMEResults(FaxedRecord faxedRecord,Database db,HashMap<String, RingCentralClient> map) throws SQLException, ScriptNewException, IOException, JSONException, InterruptedException, URISyntaxException, ScriptException {
		ResultSet frSet = db.select(Tables.DME, null, DMEColumns.ID+" = ?", new String[] {faxedRecord.getId()});
		if(!frSet.next())
			return;
		Record record = new Record(frSet,true);
		record.setPharmacy("MT_Medical_Supplies");

		RingCentralClient ringCentral = null;
		String number = Fax.GetRingCentralClient(record, "MT_MARKETING").number;
		if(map.containsKey(number))
			ringCentral = map.get(number);
		else {
			ringCentral = Fax.GetRingCentralClient(record, "MT_MARKETING");
			map.put(ringCentral.number, ringCentral);
		}
		if(!ringCentral.loggedIn)
			if(!ringCentral.login())
				return;
	
		String status = GetMessageFromJSON(faxedRecord,ringCentral);
		switch(status) {
			case MessageStatus.QUEUED:
				db.update(Tables.FAXED, new String[] {FaxedColumns.STATUS,FaxedColumns.MESSAGE_ID}, new String[] {status,faxedRecord.getMessage_id()}, FaxedColumns.ID+" = '"+faxedRecord.getId()+"'");
				break;
			case MessageStatus.SENDING_FAILED:
				if(faxedRecord.getFaxAttempts()>=4) {
					db.update(Tables.DME, new String[] {DMEColumns.MESSAGE_STATUS,DMEColumns.MESSAGE_ID,DMEColumns.CONFIRM_DOCTOR}, new String[] {MessageStatus.BLANK,"",ConfirmDoctor.NOT_CONFIRMED}, LeadColumns.ID+" = '"+faxedRecord.getId()+"'");
					db.delete(Tables.FAXED,FaxedColumns.ID+" = '"+faxedRecord.getId()+"'");
					return;
				}
				/*
				ScriptNew script = Fax.GetScriptNew(record, ringCentral.number);
				script.PopulateDMEScript("",faxedRecord.getAttention(),faxedRecord.getAgent());
				byte[] pdfData = new byte[(int) script.getFile().length()];
				DataInputStream dis = new DataInputStream(new FileInputStream(script.getFile()));
				dis.readFully(pdfData);  // read from file into byte[] array
				dis.close();
				*/
				String id = GetFaxFromJSON(record,ringCentral,faxedRecord.getFile());
				db.update(Tables.FAXED, new String[] {FaxedColumns.MESSAGE_ID,FaxedColumns.STATUS,FaxedColumns.FAX_ATTEMPTS}, new Object[] {id,MessageStatus.QUEUED,""+faxedRecord.getFaxAttempts()+1}, FaxedColumns.ID+" = '"+faxedRecord.getId()+"'");
				db.update(Tables.DME, new String[] {DMEColumns.MESSAGE_STATUS,DMEColumns.MESSAGE_ID}, new String[] {MessageStatus.QUEUED,id}, FaxedColumns.ID+" = '"+faxedRecord.getId()+"'");
				break;
			case MessageStatus.SENT:
				db.update(Tables.DME, new String[] {DMEColumns.MESSAGE_STATUS,DMEColumns.FAX_SENT_DATE}, new String[] {MessageStatus.SENT,Date.getCurrentDate("yyyy-MM-dd")}, DMEColumns.ID+" = '"+faxedRecord.getId()+"'");
				db.delete(Tables.FAXED,FaxedColumns.ID+" = '"+faxedRecord.getId()+"'");
				break;
		}
		
		
	}
	private void CheckRxResults(FaxedRecord faxedRecord,Database db,HashMap<String, RingCentralClient> map) throws SQLException, IOException, JSONException, InterruptedException, URISyntaxException, ScriptNewException, ScriptException {
		ResultSet frSet = db.select(Tables.LEADS, null, LeadColumns.ID+" = ?", new String[] {faxedRecord.getId()});
		if(!frSet.next())
			return;
		Record r = new Record(frSet);
		RingCentralClient ringCentral = null;
		String number = Fax.GetRingCentralClient(r, "MT_MARKETING").number;
		if(map.containsKey(number))
			ringCentral = map.get(number);
		else {
			ringCentral = Fax.GetRingCentralClient(r, "MT_MARKETING");
			map.put(ringCentral.number, ringCentral);
		}
		if(!ringCentral.loggedIn)
			if(!ringCentral.login())
				return;
		String status = GetMessageFromJSON(faxedRecord,ringCentral);
		switch(status) {
			case MessageStatus.QUEUED:
				db.update(Tables.FAXED, new String[] {FaxedColumns.STATUS,FaxedColumns.MESSAGE_ID}, new String[] {status,faxedRecord.getMessage_id()}, FaxedColumns.ID+" = '"+faxedRecord.getId()+"'");
				break;
			case MessageStatus.SENDING_FAILED:
				if(faxedRecord.getFaxAttempts()>=4) {
					db.update(Tables.LEADS, new String[] {LeadColumns.MESSAGE_STATUS,LeadColumns.MESSAGE_ID,LeadColumns.CONFIRM_DOCTOR}, new String[] {MessageStatus.BLANK,"",ConfirmDoctor.NOT_CONFIRMED}, LeadColumns.ID+" = '"+faxedRecord.getId()+"'");
					db.delete(Tables.FAXED,FaxedColumns.ID+" = '"+faxedRecord.getId()+"'");
					return;
				}
				String id = GetFaxFromJSON(r,ringCentral,faxedRecord.getFile());
				db.update(Tables.FAXED, new String[] {FaxedColumns.MESSAGE_ID,FaxedColumns.STATUS,FaxedColumns.FAX_ATTEMPTS}, new Object[] {id,MessageStatus.QUEUED,""+faxedRecord.getFaxAttempts()+1}, FaxedColumns.ID+" = '"+faxedRecord.getId()+"'");
				db.update(Tables.LEADS, new String[] {LeadColumns.MESSAGE_STATUS,LeadColumns.MESSAGE_ID}, new String[] {MessageStatus.QUEUED,id}, FaxedColumns.ID+" = '"+faxedRecord.getId()+"'");
				break;
			case MessageStatus.SENT:
				db.update(Tables.LEADS, new String[] {LeadColumns.MESSAGE_STATUS,LeadColumns.FAX_SENT_DATE}, new String[] {MessageStatus.SENT,Date.getCurrentDate("yyyy-MM-dd")}, LeadColumns.ID+" = '"+faxedRecord.getId()+"'");
				db.delete(Tables.FAXED,FaxedColumns.ID+" = '"+faxedRecord.getId()+"'");
				break;
		}
	}
	private String[] GetPharmacies() {
		List<String> list = new ArrayList<String>();
		Database roadMap = new Database("Road_Map");
		try {
			if(!roadMap.login())
				return null;
			ResultSet set = roadMap.select(Tables.TELMED_ROADMAP, null, RoadMapColumns.FAX_CHASE+" = 1", null);
			while(set.next()) {
				list.add(set.getString(RoadMapColumns.PHARMACY));
			}
			return list.toArray(new String[list.size()]);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			if(roadMap!=null)roadMap.close();
		}
		
	}
	private String CreateQuery(String[] pharmacies) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for(int i = 0;i<pharmacies.length;i++) {
			if(i==pharmacies.length-1)
				sb.append(LeadColumns.PHARMACY+" = '"+pharmacies[i]+"'");
			else
				sb.append(LeadColumns.PHARMACY+" = '"+pharmacies[i]+"' OR ");
			
		}
		sb.append(")");
		return sb.toString();
	}
}
