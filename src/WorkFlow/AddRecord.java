package WorkFlow;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.json.JSONException;
import org.json.JSONObject;

import Fax.EmdeonStatus;
import client.DatabaseClient;
import client.EmdeonClient;
import client.InfoDatabase;
import client.PharmacyOdds;
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
			@FormParam("policy_id") String policy_id,
			@FormParam("bin") String bin,
			@FormParam("grp") String grp,
			@FormParam("pcn") String pcn,
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
			@FormParam("source") String source) throws IOException, URISyntaxException, JSONException {
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
		record.setCarrier(StripDown(carrier));
		record.setPolicyId(StripDown(policy_id));
		record.setBin(StripDown(bin));
		record.setGrp(StripDown(grp));
		record.setPcn(StripDown(pcn));
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
		if(record.getAge()>=65 && record.getType().equalsIgnoreCase("Medicaid"))
			record.setType("Medicare");
		InfoDatabase info = new InfoDatabase();
		String callCenter = info.GetCallCenter(agent);
		info.GetInsuranceInfo(record);
		info.UpdateIP(request.getRemoteAddr(), agent);
		if(!CheckTelmed(record))
			return "Patient has already been enrolled in Telmed";
		if(record.getStatus()==null)
			EmdeonPatient(record);
		
		JSONObject dme = GetDMEEligibilty(info,record);
		info.close();
		return AddRecordToTable(record,agent,callCenter,"MT_MARKETING","MT-LIVE","TELMED_ROADMAP",dme);
	}
	
	@POST
 	@Path("AddComan")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
 	@Produces(MediaType.TEXT_HTML)
	public String AddLeadComan(
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
			@FormParam("bin") String bin,
			@FormParam("grp") String grp,
			@FormParam("pcn") String pcn,
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
			@FormParam("source") String source) throws IOException, URISyntaxException, JSONException {
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
		record.setCarrier(StripDown(carrier));
		record.setPolicyId(StripDown(policy_id));
		record.setBin(StripDown(bin));
		record.setGrp(StripDown(grp));
		record.setPcn(StripDown(pcn));
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
		if(record.getAge()>=65 && record.getType().equalsIgnoreCase("Medicaid"))
			record.setType("Medicare");
			
		InfoDatabase info = new InfoDatabase();
		String callCenter = info.GetCallCenter(agent);
		info.GetInsuranceInfo(record);
		info.UpdateIP(request.getRemoteAddr(), agent);
		if(!CheckTelmed(record))
			return "Patient has already been enrolled in Telmed";
		if(record.getStatus()==null)
			EmdeonPatient(record);
		JSONObject dme = GetDMEEligibilty(info,record);
		info.close();
		return AddRecordToTable(record,agent,callCenter,"Coman_Marketing","Coman-Live","COMAN_ROADMAP",dme);
	}
	public boolean CheckTelmed(Record record) {
		DatabaseClient client = new DatabaseClient("MT_MARKETING");
		boolean isDupe = client.IsTelmedDuplicate(record);
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
		if(client!=null)
			client.close();
	}
	private String AddRecordToTable(Record record,String agent,String callCenter,String database,String afid,String roadmap,JSONObject dme) throws IOException, URISyntaxException, JSONException {
		StringBuilder builder = new StringBuilder();
		RoadMapClient roadMapClient = new RoadMapClient(roadmap);
		String pharmacy = GetPharmacy(record,roadMapClient);
		roadMapClient.close();
		record.setPharmacy(pharmacy);
		String message = dme.getString("Message");
		DatabaseClient client = new DatabaseClient(database);
		if(client.IsInDatabase(record.getPhone())) {
			if(client.isSameDoctor(record,"Leads")) {
				String duplicateResponse = client.GetDuplicateInfo(record,"Leads");
				client.close();
				return duplicateResponse+"</br>"+message;
			}
			else {
				int updated = client.updateRecord(record,"Leads","MT-LIVE", pharmacy,agent,callCenter);
				client.close();
				if(updated>0) 
					return "Succesfully Updated Record"+"</br>"+message;
				
				else 
					return "ERROR CODE: "+updated;
			}
		}
		int add = client.addRecord(record,"Leads",afid,pharmacy,agent,callCenter);
		switch(add) {
			case 1: 
				builder.append("Successfully Added "+record.getFirstName()+" "+record.getLastName()+" "+record.getPhone()+" to "+pharmacy+"\r\n"+message);			
				client.close();
				return builder.toString();
			case 0:
				String error = client.addRecordString(record,"Leads","MT-LIVE", pharmacy, agent,callCenter);
				client.close();
				return "There was an Erorr: "+error;
			case 1062:
				if(client.isSameDoctor(record,"Leads")) {
					String duplicateResponse = client.GetDuplicateInfo(record,"Leads");
					client.close();
					return duplicateResponse+"</br>"+message;
				}
				else {
					int updated = client.updateRecord(record,"Leads","MT-LIVE", pharmacy,agent,callCenter);
					client.close();
					if(updated>0) 
						return "Succesfully Updated Record"+"</br>"+message;
					
					else 
						return "ERROR CODE: "+updated;
				}
			default:
				String error2 = client.addRecordString(record,"Leads","MT-LIVE", pharmacy, agent,callCenter);
				client.close();
				return "UNKNOWN ERROR CODE "+add+" "+error2;
		}
	}
	private String GetPharmacy(Record record,RoadMapClient client) {
		if(EmdeonStatus.IsNotFoundStatus(record.getStatus())) 
			return NotFoundPharmacy(client,record);
		int type = PBM.InsuranceFilter.GetInsuranceType(record);
		if(type==PBM.InsuranceType.Type.MEDICAID_INSURANCE) 
				return "Medicaid";
		PharmacyOdds[] pharmacies = client.GetInStatePharmacies(record);
		if(pharmacies!=null) {
			String pharmacy = GetPharmacy(record,pharmacies,client);
			if(!pharmacy.equalsIgnoreCase("No Home")) {
				return pharmacy;
			}
		}
		pharmacies = client.GetPharmacyList(record);
		return GetPharmacy(record,pharmacies,client);
	}
	private String NotFoundPharmacy(RoadMapClient client,Record record) {
		PharmacyOdds[] pharmacies = client.GetNotFoundPharmacyList();
		if(pharmacies==null)
			return "Not Found";
		ArrayList<PharmacyOdds> pharmacies_that_can_take = new ArrayList<PharmacyOdds>();
		for(PharmacyOdds pharmacy: pharmacies) {
			if(client.CanTakeNotFound(record, pharmacy.getName())) {
				pharmacies_that_can_take.add(pharmacy);
			}
		}
		//Check if its a No Home or only 1 pharmacy available
		if(pharmacies_that_can_take.size()==0)
			return "Not Found";
		else if(pharmacies_that_can_take.size()==1)
			return pharmacies_that_can_take.get(0).getName();
				
		//Set The Odds 
		for(int i = 0;i<pharmacies_that_can_take.size();i++) {
			PharmacyOdds pharmacy = pharmacies_that_can_take.get(i);
			double odds = 1/(double)pharmacies_that_can_take.size();
			pharmacy.setOdds(odds);
		}		
		//Add records to the pot and draw 1
		ArrayList<PharmacyOdds> oddsList = new ArrayList<PharmacyOdds>();
		for(PharmacyOdds pharmacy: pharmacies_that_can_take) {
			int number = (int)(pharmacy.getOdds()*100);
			for(int x = 0;x<number;x++) {
				oddsList.add(pharmacy);
			}
		}
		Collections.shuffle(oddsList); 
		Random rand = new Random();
		return oddsList.get(rand.nextInt(oddsList.size())).getName();		
	}
	private String GetPharmacy(Record record,PharmacyOdds[] pharmacies,RoadMapClient client) {
		//Get Pharmacies that can take record
		if(record.getState().equalsIgnoreCase("FL")) {
			switch(record.getBin()) {
				//ESI
				case "610014":
				case "003858":
				case "400023":
				//CIGNA
				case "017010":
				//HUMANA
				case "015599":
				case "015581":
				case "610649":
				//OPTUM
				case "610097":
				case "610494":
				case "610279":
				case "610127":
				//CATAMARAN
				case "610011":
				//Catalyst
				case "005947":
				case "015814":
					return "All_Pharmacy";
			}
		}
		ArrayList<PharmacyOdds> pharmacies_that_can_take = new ArrayList<PharmacyOdds>();
		if(pharmacies==null)
			return "No Home";
		for(PharmacyOdds pharmacy: pharmacies) {
			if(client.CanPharmacyTake(record, pharmacy.getName())) {
				pharmacies_that_can_take.add(pharmacy);
			}
		}
		//Check if its a No Home or only 1 pharmacy available
		if(pharmacies_that_can_take.size()==0)
			return "No Home";
		else if(pharmacies_that_can_take.size()==1)
			return pharmacies_that_can_take.get(0).getName();
		
		//Set The Odds 
		for(int i = 0;i<pharmacies_that_can_take.size();i++) {
			PharmacyOdds pharmacy = pharmacies_that_can_take.get(i);
			double odds = 1/(double)pharmacies_that_can_take.size();
			pharmacy.setOdds(odds);
		}
		
		//Add records to the pot and draw 1
		ArrayList<PharmacyOdds> oddsList = new ArrayList<PharmacyOdds>();
		for(PharmacyOdds pharmacy: pharmacies_that_can_take) {
			int number = (int)(pharmacy.getOdds()*100);
			for(int x = 0;x<number;x++) {
				oddsList.add(pharmacy);
			}
		}
		Collections.shuffle(oddsList); 
		Random rand = new Random();
		return oddsList.get(rand.nextInt(oddsList.size())).getName();		
	}
		
	private JSONObject GetDMEEligibilty(InfoDatabase info,Record record) {
		if(!record.getStatus().equalsIgnoreCase("Found"))
			return DMEResponse(0,"INSURANCE NOT FOUND");
		switch(record.getState()) {
			case "AL":
			case "AR":
			case "CO":
			case "CT":
			case "LA":
			case "MS":
			case "NV":
			case "NJ":
			case "ND":
			case "OK":
			case "PA":
			case "TX":
				return DMEResponse(0,"NOT A VALID STATE");
		}
		switch(record.getBin()) {
			case "015581":
			case "610502":
			case "610097":
				if(record.getContractId().equalsIgnoreCase("")) {
					return DMEResponse(1,"ELIGIBLE FOR BRACES IF PATIENT HAS PPO ONLY");
				}
				else {
					if(info.hasContractId(record)) {
						if(info.isPPO(record)) {
							return DMEResponse(1,"PATIENT MAY BE ELIGIBLE FOR BRACES");
						}
						else {
							return DMEResponse(0,"PATIENT DOES NOT HAVE PPO");
						}
					}
				}
			case "610279":
			case "610649":
				return DMEResponse(1,"ELIGIBLE FOR BRACES IF PATIENT HAS PPO ONLY");
			default:
				return DMEResponse(0,"NOT VALID INSURANCE CARRIER");
		}
	}
	private JSONObject DMEResponse(int success,String message) {
 		JSONObject obj =  new JSONObject();
 		try {
	 		obj.put("Success", success);
	 		obj.put("Message", message);
 		} catch(JSONException ex) {
 			ex.printStackTrace();
 		}
 		return obj;
 	}
}
