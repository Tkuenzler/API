package WorkFlow;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.json.JSONException;
import org.json.JSONObject;

import PBM.InsuranceFilter;
import PBM.InsuranceType;
import Pharmacy.Pharmacy;
import Pharmacy.PharmacyMap;
import ResponseBuilder.InsuranceResponse;
import client.DatabaseClient;
import client.EmdeonClient;
import client.InfoDatabase;
import client.PVerifyClient;
import client.Record;
import client.RoadMapClient;

@Path("Insurance")
public class Cascade {
	
	
	
	@GET
	@Path("Emdeon")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public String EmdeonLookup(
			@Context HttpServletRequest request,
			@QueryParam("first_name") String first_name,
			@QueryParam("last_name") String last_name,
			@QueryParam("dob") String dob,
			@QueryParam("address") String address,
			@QueryParam("city") String city,
			@QueryParam("state") String state,
			@QueryParam("zip") String zip,
			@QueryParam("gender") String gender,
			@QueryParam("ssn") String ssn,
			@QueryParam("phone_number") String phone,
			@QueryParam("insurance_type") String insurance_type,
			@QueryParam("agent") String agent,
			@QueryParam("roadmap") String roadmap) throws JSONException, ParseException {
		Record record = new Record();
		record.setFirstName(first_name.trim());
		record.setLastName(last_name.trim());
		record.setPhone(phone.trim());
		record.setDob(convertDob(dob.trim()));
		record.setGender(gender.trim());
		record.setState(state.trim());
		record.setZip(zip.trim());
		record.setSsn(ssn.trim());
		record.setPhone(phone);
		record.setAddress(address);
		record.setCity(city);
		record.setAgent(agent);
		InfoDatabase info = GetInfoDatabase();
		JSONObject valid = CheckLead(record,info,roadmap,insurance_type);
		if(valid!=null) {
			info.close();
			return valid.toString();
		}
		String callCenter = info.GetCallCenter(agent);
		if(callCenter==null)
			callCenter = "SOS";
		RoadMapClient client = new RoadMapClient(roadmap);
		try {
			ArrayList<PharmacyMap> map = client.getPharmaciesForTelmed(insurance_type);
			//if(!Pharmacy.CanTelmed(map,record,insurance_type))
				//return InsuranceResponse.BuildFailedResponse("CAN'T TAKE "+insurance_type+" IN "+record.getState()).toString();
			getInsuranceInformation(info,record,callCenter,insurance_type);
			String type = InsuranceFilter.Filter(record);
			record.setType(type);
			if(record.getStatus().equalsIgnoreCase("FOUND")) {
				UpdateCheckedStatus(info,record,type);
			}
			else {
				UpdateCheckedStatus(info,record,"");
				return InsuranceResponse.BuildFailedResponse("Patient insurance "+record.getStatus()).toString();
			}
			info.close();
			String pharmacy = Pharmacy.GetTelmedPharmacy(record,insurance_type,roadmap);
			record.setPharmacy(pharmacy);
			if(pharmacy==null)
				return InsuranceResponse.BuildFailedResponse("Patient insurance "+record.getStatus()).toString();
			return InsuranceResponse.BuildInsuranceResponse(record).toString();
		} catch(Exception ex) {
			return InsuranceResponse.BuildFailedResponse(ex.getLocalizedMessage()).toString();
		} finally {
			try {
				if(info!=null) info.close();
				if(client!=null)client.close();
			} catch(Exception ex) {
				
			}
		}
	}		
	
	@GET
	@Path("GetInsurance")
	@Produces(MediaType.TEXT_PLAIN)
	public String GetInsuranceData(@QueryParam("first_name") String first_name,
								@QueryParam("last_name") String last_name,
								@QueryParam("dob") String dob,
								@QueryParam("state") String state,
								@QueryParam("zip") String zip,
								@QueryParam("gender") String gender,
								@QueryParam("ssn") String ssn,
								@QueryParam("agent") String agent,
								@QueryParam("phone") String phone,
								@QueryParam("plan_type") String plan_type) throws ParseException, org.apache.http.ParseException, IOException, JSONException {
		DatabaseClient client = new DatabaseClient("MT_MARKETING");
		Record record = client.GetRecordByPhone(phone, "Leads");
		if(record==null) {
			record = new Record();
			record.setFirstName(first_name.trim());
			record.setLastName(last_name.trim());
			record.setDob(convertDob(dob.trim()));
			record.setGender(gender.trim());
			record.setState(state.trim());
			record.setZip(zip.trim());
			record.setSsn(ssn.trim());
			record.setPhone(phone.trim());
			record.setAgent(agent);
			EmdeonClient emdeon = new EmdeonClient();
			emdeon.login("rxcg", "pharmacy123", "1619320132");
			emdeon.fillOutForm(record);
			emdeon.close();
			if(record.getStatus().equalsIgnoreCase("Not Found"))
				return "";
		}
		InfoDatabase info = new InfoDatabase();
		String requestId = info.GetRequestId(record);
		info.close();
		PVerifyClient pverify = new PVerifyClient();
		if(!pverify.Login())
			return "Login Failed";
		String npi = null;
		if(record.getNpi().equalsIgnoreCase(""))
			npi = "1013575406";
		else
			npi = record.getNpi();
		if(requestId==null)
			return pverify.GetEligibiltySummary(record, npi).toString();
		else
			return pverify.GetEligibiltySummaryById(record, npi, requestId).toString();
	}
	
	
	private boolean CheckDuplicate(Record record) {
		DatabaseClient client = new DatabaseClient("MT_MARKETING");
		boolean value = client.IsTelmedDuplicate(record.getPhone());
		client.close();
		return value;
	}
	
	private boolean HasBeenLookedUp(InfoDatabase info,Record record) {
		return info.HasBeenLookedUp(record);
	}
	private void AddToCheck(InfoDatabase info,Record record,String afid,String type) {
		info.AddToCheck(record, afid,type);
	}
	private void UpdateCheckedStatus(InfoDatabase info,Record record,String type) {
		info.UpdateCheck(record,type);
	}
	private void incrementLookup(InfoDatabase info,String group) {
		info.incrementLookup(group);
	}
	private InfoDatabase GetInfoDatabase() {
		InfoDatabase info = new InfoDatabase();
		int connect = 0;
		while(info.connect==null && connect<8) {
			info = new InfoDatabase();
			connect++;
		}
		if(info.connect==null)
			return null;
		return info;
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
	private String convertDob(String dob) throws ParseException {
 		SimpleDateFormat correctFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date date = null;
 		date  = correctFormat.parse(dob);
		return correctFormat.format(date);
 	}
 	private JSONObject CheckLead(Record record,InfoDatabase info,String roadmap,String insurance_type) throws JSONException {
 		if(!checkState(record.getState()))
 			return InsuranceResponse.BuildFailedResponse("There is no DR in "+record.getState());
 		if(!UnderAge(record.getDob(),75))
			return InsuranceResponse.BuildFailedResponse("Must be under 75");
 		if(record.getPhone().length()!=10)
			return InsuranceResponse.BuildFailedResponse("Phone number must be 10 digits");
		if(record.getZip().length()!=5)
			return InsuranceResponse.BuildFailedResponse("Invalid Zipcode must be 5 numbers");
		if(record.getDob().length()!=10)
			return InsuranceResponse.BuildFailedResponse("Invalid DOB Formate must be MM/DD/YYYY");
		if(record.getFirstName().length()>13)
			return InsuranceResponse.BuildFailedResponse("First name can only be 13 characters or less");
		if(record.getLastName().length()>13)
			return InsuranceResponse.BuildFailedResponse("Last name can only be 13 characters or less");
		if(info.CheckIfAudited(record.getPhone())) 
			return InsuranceResponse.BuildFailedResponse("PATIENT HAS BEEN AUDITED PLEASE HANG UP");
		if(CheckDuplicate(record)) 
			return InsuranceResponse.BuildFailedResponse("Patient has already been submitted to Telmed");
		else 
			return null;
 	}
 	private void getInsuranceInformation(InfoDatabase info,Record record,String callCenter,String insurance_type) {
 		if(HasBeenLookedUp(info,record)) {
			info.GetInsuranceInfo(record);
		}
		else {
			incrementLookup(info,callCenter);
			AddToCheck(info,record,callCenter,insurance_type);
			EmdeonClient client = new EmdeonClient();
			client.login("rxcg", "pharmacy123", "1619320132");
			client.fillOutForm(record);
			if(client!=null)
				client.close();
		}
 	}
 	private boolean checkState(String state) {
		switch(state) {
			case "AL":
			case "AZ":
			case "CA":
			case "CO":
			case "CT":
			case "DC":
			case "FL":
			case "GA":
			case "IA":
			case "ID":
			case "IL":
			case "IN":
			case "KS":
			case "KY":
			case "LA":
			case "MA":
			case "MD":
			case "ME":
			case "MI":
			case "MN":
			case "NC":
			case "ND":
			case "NE":
			case "NH":
			case "NJ":
			case "NM":
			case "NV":
			case "NY":
			case "OH":
			case "OK":
			case "PA":
			case "RI":
			case "SC":
			case "TN":
			case "TX":
			case "UT":
			case "VA":
			case "WA":
			case "WI":
				return true;
			default:
				return false;
		}
	}
	public boolean UnderAge(String dob,int age) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		LocalDate birthDate = LocalDate.parse(dob,formatter);
		LocalDate currentDate = LocalDate.now();
		int currentAge = Period.between(birthDate, currentDate).getYears();
		if(currentAge<age)
			return true;
		else 
			return false;
	}
}
