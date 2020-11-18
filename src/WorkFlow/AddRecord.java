package WorkFlow;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import Database.Columns.DMEColumns;
import Database.Columns.LeadColumns;
import Database.Columns.PlanTypeColumns;
import Database.Columns.TelmedColumns;
import Database.Tables.Tables;
import PBM.InsuranceFilter;
import PBM.InsuranceType;
import Pharmacy.Pharmacy;
import Pharmacy.PharmacyMap;
import ResponseBuilder.AddRecordResponse;
import ResponseBuilder.DMEResponse;
import client.Database;
import client.DatabaseClient;
import client.EmdeonClient;
import client.InfoDatabase;
import client.Record;
import client.RoadMapClient;


@Path("AddRecord")
public class AddRecord {
	
	@POST
 	@Path("AddMTK")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
 	@Produces(MediaType.TEXT_HTML)
	public String AddLeadMTK(
			@Context HttpServletRequest request,
			@FormParam("first_name") String first_name,
			@FormParam("last_name") String last_name,
			@FormParam("phone") String phone,
			@FormParam("agent") String agent,
			@FormParam("dob") String dob,
			@FormParam("ssn") String ssn,
			@FormParam("gender") String gender,
			@FormParam("address") String address,
			@FormParam("city") String city,
			@FormParam("state") String state,
			@FormParam("zip") String zip,
			@FormParam("carrier") String carrier,
			@FormParam("pbm") String pbm,
			@FormParam("npi") String npi,
			@FormParam("dr_type") String dr_type,
			@FormParam("dr_first") String dr_first,
			@FormParam("dr_last") String dr_last,
			@FormParam("dr_address") String dr_address,
			@FormParam("dr_city") String dr_city,
			@FormParam("dr_state") String dr_state,
			@FormParam("dr_zip") String dr_zip,
			@FormParam("dr_phone") String dr_phone,
			@FormParam("dr_fax") String dr_fax,
			@FormParam("insurance_type") String insurance_type,
			@FormParam("pain_cause") String pain_cause,
			@FormParam("pain_location") String pain_location,
			@FormParam("source") String source,
			@FormParam("products") String products) throws IOException, URISyntaxException, JSONException {
		Record record = new Record();
		record.setAgent(agent);
		record.setFirstName(StripDown(first_name));
		record.setLastName(StripDown(last_name));
		record.setPhone(StripDown(phone));
		record.setDob(StripDown(dob));
		record.calculateAge();
		record.setSsn(StripDown(ssn));
		record.setGender(StripDown(gender));
		record.setAddress(StripDown(address));
		record.setCity(StripDown(city));
		record.setState(StripDown(state));
		record.setZip(StripDown(zip));
		record.setInsuranceName(carrier);
		record.setNpi(StripDown(npi));
		record.setDrType(StripDown(dr_type));
		record.setDrFirst(StripDown(dr_first));
		record.setDrLast(StripDown(dr_last));
		record.setDrAddress(StripDown(dr_address));
		record.setDrCity(StripDown(dr_city));
		record.setDrState(StripDown(dr_state));
		record.setDrZip(StripDown(dr_zip));
		record.setDrPhone(StripDown(dr_phone));
		record.setDrFax(StripDown(dr_fax));
		record.setPainLocation(pain_location);
		record.setPainCause(pain_cause);
		record.setType(insurance_type);
		record.setId(first_name+last_name+phone);
		record.setSource(source);
		record.setProducts(products);
		if(record.getAge()>=65 && record.getType().equalsIgnoreCase("Medicaid"))
			record.setType("Medicare");
		InfoDatabase info = new InfoDatabase();
		String callCenter = info.GetCallCenter(agent);
		info.GetInsuranceInfo(record);
		info.UpdateIP(request.getRemoteAddr(), agent);
		/*
		if(!CheckTelmed(record))
			return "Patient has already been enrolled in Telmed";
		*/
		if(record.getStatus().equalsIgnoreCase(""))
			EmdeonPatient(record);
		if(IsPPO(record) && JointPain(record) && VerifyDMEState(record.getState()))
			AddToDME(record);
		info.close();
		return AddRecordToTable(record,agent,callCenter,"MT_MARKETING","MT-LIVE","TELMED_ROADMAP");
	}

	@POST
 	@Path("AddUHG")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public Response AddLeadUHG(
			@Context HttpServletRequest request,
			@FormParam("first_name") String first_name,
			@FormParam("last_name") String last_name,
			@FormParam("phone") String phone,
			@FormParam("agent") String agent,
			@FormParam("dob") String dob,
			@FormParam("ssn") String ssn,
			@FormParam("gender") String gender,
			@FormParam("address") String address,
			@FormParam("city") String city,
			@FormParam("state") String state,
			@FormParam("zip") String zip,
			@FormParam("npi") String npi,
			@FormParam("dr_first") String dr_first,
			@FormParam("dr_last") String dr_last,
			@FormParam("dr_address") String dr_address,
			@FormParam("dr_city") String dr_city,
			@FormParam("dr_state") String dr_state,
			@FormParam("dr_zip") String dr_zip,
			@FormParam("dr_phone") String dr_phone,
			@FormParam("dr_fax") String dr_fax,
			@FormParam("pain_cause") String pain_cause,
			@FormParam("pain_location") String pain_location,
			@FormParam("products") String products) throws IOException, URISyntaxException, JSONException {
		String ip_address = request.getRemoteAddr();
		Record record = new Record();
		record.setAgent("UHG");
		record.setAfid("UHG");
		record.setFirstName(StripDown(first_name));
		record.setLastName(StripDown(last_name));
		record.setPhone(StripDown(phone));
		record.setDob(StripDown(dob));
		record.calculateAge();
		record.setSsn(StripDown(ssn));
		record.setGender(StripDown(gender));
		record.setAddress(StripDown(address));
		record.setCity(StripDown(city));
		record.setState(StripDown(state));
		record.setZip(StripDown(zip));
		record.setNpi(StripDown(npi));
		record.setDrFirst(StripDown(dr_first));
		record.setDrLast(StripDown(dr_last));
		record.setDrAddress(StripDown(dr_address));
		record.setDrCity(StripDown(dr_city));
		record.setDrState(StripDown(dr_state));
		record.setDrZip(StripDown(dr_zip));
		record.setDrPhone(StripDown(dr_phone));
		record.setDrFax(StripDown(dr_fax));
		record.setPainLocation(pain_location);
		record.setPainCause(pain_cause);
		record.setId(first_name+last_name+phone);
		StringBuilder p = new StringBuilder();
		if(products.toUpperCase().contains("PAIN"))
			p.append("Pain, ");
		if(products.toUpperCase().contains("SKIN"))
			p.append("Dermatitis, ");
		if(products.toUpperCase().contains("MIGRAINE"))
			p.append("Migraines");
		if(products.toUpperCase().contains("FUNGAL"))
			p.append("Anti-Fungal, ");
		if(products.toUpperCase().contains("ACID"))
			p.append("Acid Reflux, ");
		if(products.toUpperCase().contains("FOOTBATH"))
			p.append("Podiatry, ");
		p.deleteCharAt(p.length() -1);
		p.deleteCharAt(p.length() -1);
		record.setProducts(p.toString());
		Database db = new Database("MT_MARKETING");
		try {
			if(!db.login())
				 return Response.status(400).entity( "FAILED TO LOGIN TO DATABASE").build();
			ResultSet set = db.select(Tables.LEADS, null, LeadColumns.PHONE_NUMBER+" = ?", new String[] {record.getPhone()});
			if(set.next())
				return Response.status(400).entity("DUPLICATE").build();
			EmdeonPatient(record);
			if(!record.getStatus().equalsIgnoreCase("FOUND"))
				 return Response.status(400).entity("NO INSURANCE FOUND").build();
			if(record.getBin().equalsIgnoreCase("015581") || record.getBin().equalsIgnoreCase("015599")) {
				if(!record.getPolicyId().startsWith("H") && !record.getPolicyId().startsWith("0"))
					return Response.status(400).entity("Policy Termed").build();
			}
			switch(record.getBin()) {
				case "003858":
				case "610014":
				case "015581":
				case "015599":	
				case "400023":
				case "015574":
				case "012312":
				case "003585":
					if(record.getState().equalsIgnoreCase("FL")) {
						record.setPharmacy("All_Pharmacy");
						break;
					}
					else if(record.getState().equalsIgnoreCase("TX")) {
						record.setPharmacy("West Plano");
						break;
					}
					else
						return Response.status(400).entity("INVALID STATE").build();
				case "017010":
					if(record.getPcn().startsWith("02")) {
						if(record.getState().equalsIgnoreCase("FL")) {
							record.setPharmacy("All_Pharmacy");
							break;
						}
						else if(record.getState().equalsIgnoreCase("TX")) {
							record.setPharmacy("West Plano");
							break;
						}
						else
							return Response.status(400).entity("INVALID STATE").build();
					}
					else
						return Response.status(400).entity("INVALID INSURANCE").build();
				case "610011":
				case "610097":
				case "011552":
				case "004915":
				case "005947":
				case "015905":
				case "610455":
					if(record.getState().equalsIgnoreCase("TX")) {
						record.setPharmacy("West Plano");
						break;
					}
					return Response.status(400).entity("INVALID STATE").build();
				default:
					return Response.status(400).entity("INVALID INSURANCE").build();
			}
			if(InsuranceFilter.GetInsuranceType(record)==InsuranceType.Type.MEDICAID_INSURANCE)
				return Response.status(400).entity("MEDICAID").build();
			int value = db.insert(Tables.UHG, LeadColumns.ADD_RECORD, LeadColumns.ToStringArray(record));
			if(value==1)
				return Response.status(200).entity("SUCCESSFULLY ADDED PATIENT").build();
			else
				return Response.status(400).entity("SOMETHING FAILED ERROR CODE: "+value).build();
		} catch(SQLException ex) {
			 return Response.status(400).entity(ex.getMessage()).build();
		} finally {
			if(db!=null)db.close();
		}
	}
	@POST
 	@Path("AddDME")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
 	@Produces(MediaType.TEXT_HTML)
	public String AddDMELead(
			@Context HttpServletRequest request,
			@FormParam("first_name") String first_name,
			@FormParam("last_name") String last_name,
			@FormParam("phone") String phone,
			@FormParam("agent") String agent,
			@FormParam("dob") String dob,
			@FormParam("ssn") String ssn,
			@FormParam("gender") String gender,
			@FormParam("address") String address,
			@FormParam("city") String city,
			@FormParam("state") String state,
			@FormParam("zip") String zip,
			@FormParam("carrier") String carrier,
			@FormParam("policy_id") String policy_id,
			@FormParam("npi") String npi,
			@FormParam("dr_type") String dr_type,
			@FormParam("dr_first") String dr_first,
			@FormParam("dr_last") String dr_last,
			@FormParam("dr_address") String dr_address,
			@FormParam("dr_city") String dr_city,
			@FormParam("dr_state") String dr_state,
			@FormParam("dr_zip") String dr_zip,
			@FormParam("dr_phone") String dr_phone,
			@FormParam("dr_fax") String dr_fax,
			@FormParam("pain_cause") String pain_cause,
			@FormParam("pain_location") String pain_location,
			@FormParam("back") String back,
			@FormParam("right ankle") String right_ankle,
			@FormParam("left ankle") String left_ankle,
			@FormParam("right elbow") String right_elbow,
			@FormParam("left elbow") String left_elbow,
			@FormParam("right_knee") String right_knee,
			@FormParam("left_knee") String left_knee,
			@FormParam("right_wrist") String right_wrist,
			@FormParam("left_wrist") String left_wrist,
			@FormParam("right_shoulder") String right_shoulder,
			@FormParam("left_shoulder") String left_shoulder,
			@FormParam("hip") String hip,
			@FormParam("source") String source,
			@FormParam("call_notes") String call_notes) throws IOException, URISyntaxException, JSONException {
		Record record = new Record();
		record.setAgent(agent);
		record.setFirstName(StripDown(first_name));
		record.setLastName(StripDown(last_name));
		record.setPhone(StripDown(phone));
		record.setDob(StripDown(dob));
		record.calculateAge();
		record.setSsn(StripDown(ssn));
		record.setGender(StripDown(gender));
		record.setAddress(StripDown(address));
		record.setCity(StripDown(city));
		record.setState(StripDown(state));
		record.setZip(StripDown(zip));
		record.setCarrier(carrier);
		record.setPolicyId(policy_id);
		record.setNpi(StripDown(npi));
		record.setDrType(StripDown(dr_type));
		record.setDrFirst(StripDown(dr_first));
		record.setDrLast(StripDown(dr_last));
		record.setDrAddress(StripDown(dr_address));
		record.setDrCity(StripDown(dr_city));
		record.setDrState(StripDown(dr_state));
		record.setDrZip(StripDown(dr_zip));
		record.setDrPhone(StripDown(dr_phone));
		record.setDrFax(StripDown(dr_fax));
		record.setPainLocation(pain_location);
		record.setPainCause(pain_cause);
		record.setId(record.getFirstName()+record.getLastName()+record.getPhone());
		record.setCallNotes(call_notes);
		record.setSource(source);
		ArrayList<String> braces = new ArrayList<String>();
		if(back!=null)
			braces.add(back);
		if(right_ankle!=null)
			braces.add(right_ankle);
		if(left_ankle!=null)
			braces.add(left_ankle);
		if(right_wrist!=null)
			braces.add(right_wrist);
		if(left_wrist!=null)
			braces.add(left_wrist);
		if(right_knee!=null)
			braces.add(right_knee);
		if(left_knee!=null)
			braces.add(left_knee);
		if(right_shoulder!=null)
			braces.add(right_shoulder);
		if(left_shoulder!=null)
			braces.add(left_shoulder);
		if(right_elbow!=null)
			braces.add(right_elbow);
		if(hip!=null)
			braces.add(hip);
		if(left_elbow!=null)
			braces.add(left_elbow);
		record.setBraceList(braces);
		Database client = new Database("MT_MARKETING");
		try {
			if(!client.login())
				return DMEResponse.BuildErrorResponse(0, "LOGIN FAILED");
			int value = client.insert(Tables.DME, DMEColumns.ADD_TO_DATABASE_COLUMNS, DMEColumns.ConverToStringArray(record));
			if(value==1)
				return DMEResponse.SucessfulResponse(record);
			else
				return DMEResponse.BuildErrorResponse(value, "UNKNOWN ERROR");
		} catch (SQLException ex) {
			// TODO Auto-generated catch block
			if(ex.getErrorCode()==1062)
				return DMEResponse.BuildDuplicateResponse(record);
			else
				return DMEResponse.BuildErrorResponse(ex.getErrorCode(), ex.getMessage());
		} finally {
			if(client!=null) client.close();
		}
	}
	private boolean IsPPO(Record record) {
		if(record.getContractId().equalsIgnoreCase(""))
			return false;
		else if(record.getContractId().startsWith("R"))
			return true;
		if(record.getBin().equalsIgnoreCase("610502") && record.getPolicyId().startsWith("MEB"))
			return true;
		Database client = new Database("Info_Table");
		try {
			if(!client.login())
				return false;
			ResultSet set = client.select(Tables.PLAN_TYPE, null, PlanTypeColumns.CONTRACT_ID+" = ?", new String[] {record.getContractId()});
			if(set.next()) {
				String plan_type = set.getString(PlanTypeColumns.PLAN_TYPE);
				if(plan_type.equalsIgnoreCase("PPO"))
					return true;
			}
			return false;
		} catch(SQLException ex) {
			ex.printStackTrace();
			return false;
		} finally {
			if(client!=null)client.close();
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
	private void AddToDME(Record record) {
		Database client = new Database("MT_MARKETING");
		try {
			if(!client.login())
				return;
			record.setBraceList(record.getPainLocation());
			client.insert(Tables.DME, DMEColumns.ADD_TO_DATABASE_COLUMNS, DMEColumns.ConverToStringArray(record));
		} catch (SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
			return;
		} finally {
			if(client!=null) client.close();
		}
	}
	public boolean CheckTelmed(Record record) {
		DatabaseClient client = new DatabaseClient("MT_MARKETING");
		boolean isDupe = client.IsTelmedDuplicate(record.getPhone());
		boolean canEnroll = true;
		if(isDupe)
			canEnroll = client.CanEnroll(record);
		client.close();
		return canEnroll;
	}
	public String StripDown(String s) {
		return s.trim().replaceAll("[^A-Za-z0-9]\\s", "");
	}
	private void EmdeonPatient(Record record) {
		EmdeonClient client = new EmdeonClient();
		client.login("rxcg", "pharmacy123", "1619320132");
		client.fillOutForm(record);
		
		if(record.getStatus()!=null)
			if(record.getStatus().equalsIgnoreCase("NOT FOUND")) {
				record.setBin("");
				record.setGrp("");
				record.setPcn("");
				record.setPolicyId("");
			}
		record.setEmdeonType();
		String type = InsuranceFilter.Filter(record);
		record.setType(type);
		InfoDatabase info = new InfoDatabase();
		info.UpdateCheck(record, type);
		if(client!=null)
			client.close();
	}
	private String AddRecordToTable(Record record,String agent,String callCenter,String database,String afid,String roadmap) throws IOException, URISyntaxException, JSONException {
		RoadMapClient roadMapClient = new RoadMapClient(roadmap);
		HashMap<String,PharmacyMap> roadMap = GetRoadMap(roadMapClient);
		String pharmacy = GetPharmacy(roadMap, record);
		if(pharmacy.equalsIgnoreCase("No Home")) {
			if(Pharmacy.CanCarepointTake(roadMap.get("Carepoint"),record))
				record.setPharmacy("Carepoint");
			else
				record.setPharmacy("No Home");
		}
		else
			record.setPharmacy(pharmacy);
		boolean telmed = Pharmacy.CanRecordBeTelmed(roadMapClient,record);
		roadMapClient.close();
		DatabaseClient client = new DatabaseClient(database);
		try {
			JSONObject obj = client.addRecord(record,Tables.LEADS,afid,record.getPharmacy(),agent,callCenter);
			int add = obj.getInt("value");
			switch(add) {
				case 1:
					return AddRecordResponse.BuildSuccesfulResponse(record,telmed);
				case 0:
					return AddRecordResponse.BuildErrorResponse(add);
				case 1062:
				{
					if(client.isSameDoctor(record,Tables.LEADS)) {
						String agent_dupe = client.getColumn(record.getPhone(), TelmedColumns.AGENT, Tables.LEADS);
						String date_added = client.getColumn(record.getPhone(), TelmedColumns.DATE_ADDED, Tables.LEADS);
						return AddRecordResponse.BuildDuplicateResponse(record,agent_dupe,date_added);
					}
					else  {
						JSONObject update = client.updateRecord(record, Tables.LEADS, afid, record.getPharmacy(), agent, callCenter);
						int updated = update.getInt("value");
						if(updated==1)
							return AddRecordResponse.BuildSuccesfulUpdateResponse(record,telmed);
						else if(updated==0)
							return AddRecordResponse.BuildErrorResponse(updated);
						else
							return AddRecordResponse.BuildErrorResponse(update);
							
					}
				}
				default:
					return AddRecordResponse.BuildErrorResponse(obj);
			}
		} catch(JSONException ex) {
			return AddRecordResponse.BuildErrorResponse(ex.getMessage());
		} finally {
			if(client!=null) client.close();
		}
		
		
	}
	private HashMap<String,PharmacyMap> GetRoadMap(RoadMapClient client) {
		
		HashMap<String,PharmacyMap> roadMap = new HashMap<String,PharmacyMap>();
		/*
		 * Get all pharmacy names
		 */
		String[] pharmacies = client.getPharmacies();
		/*
		 * Create and populate all pharmacies
		 */
		for(String pharmacy: pharmacies) {
			PharmacyMap map = client.getPharmacy(pharmacy);
			client.LoadAllStates(map);
			roadMap.put(map.getPharmacyName(),map);
		}
		return roadMap;
	}
	private String GetPharmacy(HashMap<String,PharmacyMap> roadMap,Record record) {
		if(Pharmacy.GoodForAllFamily(record))
			return "All_Pharmacy";
		return Pharmacy.GetPharmacy(roadMap, record);
	}
}
