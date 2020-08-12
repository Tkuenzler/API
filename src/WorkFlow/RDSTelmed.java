package WorkFlow;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Fax.Group;
import PBM.InsuranceFilter;
import ResponseBuilder.TelmedResponse;
import client.BlueMosiacClient;
import client.DMEClient;
import client.DatabaseClient;
import client.InfoDatabase;
import client.Record;
import client.RoadMapClient;

@Path("RDSTelmed")
public class RDSTelmed {
	public static final String CLN_SIGNATURE = "Qm2yAXl";
	public static final String MTK_SIGNATURE = "jDOBAf$3";
	public static final String SKY_LINE_SIGNATRUE = "t$R748QF";
	public static final String COMAN_SIGNATURE = "vXkfjm344";
	public static final String MTK2_SIGNATURE = "avuzthU9";
	private class URLS {
		public static final String MTK_URL = "http://telemed.quivvytech.com/api/v4/api.php";
		public static final String MTK2_URL = "http://telemed.quivvytech.com/api/v4v2/api.php";
		public static final String PADDLEPOINT_URL = "http://telemed.quivvytech.com/api/v44/api.php";
		public static final String CLN_URL = "http://telemed.quivvytech.com/api/v6/api.php";
		public static final String SKY_LINE_URL = "http://telemed.quivvytech.com/api/v12/api.php";
		public static final String COMAN_URL = "https://telemed.quivvytech.com/api/v-COM/api.php";
	}
	private class Parameters {
		public static final String FIRST_NAME = "first_name";
		public static final String LAST_NAME = "last_name";
		public static final String PHONE_NUMBER ="phone_number";
		public static final String DOB_MONTH = "dob_month";
		public static final String DOB_DAY = "dob_day";
		public static final String DOB_YEAR = "dob_year";
		public static final String GENDER = "gender";
		public static final String ADDRESS = "address1";
		public static final String CITY = "city";
		public static final String STATE = "state";
		public static final String POSTAL_CODE = "postal_code";
		public static final String INSURANCE_CARRIER = "insurance_carrier";
		public static final String PATIENT_ID = "patient_id";
		public static final String GROUP_ID = "group_id";
		public static final String BIN = "bin_number";
		public static final String PCN = "pcnNumber";
		public static final String SOURCE_ID = "SOURCE_ID";
		public static final String MEDICATIONS = "CFT_Sub_Otc_Medications";
		public static final String ALLERGIES = "CFT_Sub_Allergies";
		public static final String PRODUCT_SUGGESTIONS = "productSuggestions";
		public static final String INSERT_TIME = "InsertTime";
		public static final String SIGNATURE_VALIDATION = "SignatureValidation";
		public static final String SUB_PROGRAM = "subProgram";
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
 	@Path("AddLeadPaddlePoint")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
 	@Produces(MediaType.TEXT_HTML)
	public String AddLeadPaddlePoint(
			@Context HttpServletRequest request,
			@FormParam("first_name") String first_name,
			@FormParam("last_name") String last_name,
			@FormParam("phone") String phone,
			@FormParam("agent") String agent,
			@FormParam("cell") String cell,
			@FormParam("dob") String dob,
			@FormParam("gender") String gender,
			@FormParam("address") String address,
			@FormParam("city") String city,
			@FormParam("state") String state,
			@FormParam("zip") String zip,
			@FormParam("carrier") String carrier,
			@FormParam("policy_id") String policy_id,
			@FormParam("ssn") String ssn,
			@FormParam("bin") String bin,
			@FormParam("grp") String grp,
			@FormParam("pcn") String pcn,
			@FormParam("productSuggestions") String productSuggestions,
			@FormParam("marketingGroup") String marketingGroup,
			@FormParam("pharmacy") String pharmacy) throws JSONException {
		RoadMapClient map = GetRoadMap("PADDLEPOINT_ROADMAP");
		boolean validPharmacy = map.AcceptableTelmedPharmacy(pharmacy);
		if(!validPharmacy)
			return TelmedResponse.BuildFailedResponse(TelmedResponse.Errors.INVALID_PHARMACY).toString();
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
		record.setSsn(ssn.toUpperCase());
		record.setInsuranceName(carrier);
		record.setCarrier(record.getPBMFromBin(bin));
		record.setPolicyId(policy_id.toUpperCase());
		record.setBin(bin);
		record.setGrp(grp.toUpperCase());
		record.setPcn(pcn.toUpperCase());
		record.setAgent("PADDLEPOINT");
		record.setSource("PADDLEPOINT");
		record.setType(InsuranceFilter.Filter(record));
		boolean canPharmacyTake = map.CanPharmacyTake(record, pharmacy);
		map.close();
		if(!canPharmacyTake) {
			return TelmedResponse.BuildFailedResponse(TelmedResponse.Errors.NOT_IN_ROAD_MAP).toString();
		}
		URIBuilder b = null;
		URL url = null;
		String[] dob_data = dob.split("/");
		String dob_month = dob_data[0];
		String dob_day = dob_data[1];
		String dob_year = dob_data[2];
		String ip = request.getRemoteAddr();
		try {
			b = new URIBuilder(URLS.PADDLEPOINT_URL);
			b.addParameter(Parameters.FIRST_NAME, first_name)
			.addParameter(Parameters.LAST_NAME, last_name)
			.addParameter(Parameters.GENDER, gender)
			.addParameter(Parameters.PHONE_NUMBER, phone)
			.addParameter(Parameters.DOB_MONTH, dob_month)
			.addParameter(Parameters.DOB_DAY, dob_day)
			.addParameter(Parameters.DOB_YEAR, dob_year)
			.addParameter(Parameters.ADDRESS, address)
			.addParameter(Parameters.CITY, city)
			.addParameter(Parameters.STATE, state)
			.addParameter(Parameters.POSTAL_CODE, zip)
			.addParameter(Parameters.INSURANCE_CARRIER,  carrier)
			.addParameter(Parameters.PATIENT_ID, policy_id)
			.addParameter(Parameters.BIN, bin)
			.addParameter(Parameters.PCN, pcn)
			.addParameter(Parameters.GROUP_ID, grp)
			.addParameter(Parameters.SOURCE_ID, phone)
			.addParameter(Parameters.PRODUCT_SUGGESTIONS, productSuggestions)
			.addParameter(Parameters.SUB_PROGRAM, pharmacy)
			.addParameter(Parameters.INSERT_TIME, GetInsertTime())
			.addParameter(Parameters.SIGNATURE_VALIDATION, MTK_SIGNATURE);
			url = b.build().toURL();
			JSONObject telmedResponse = AddLead(record,url,"MT_MARKETING",ip,"PADDLEPOINT",pharmacy,"PADDLEPOINT");
			return telmedResponse.toString();
		} catch (URISyntaxException | MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
	}
		

	@GET	
 	@Path("AddLeadGet")
 	@Produces(MediaType.TEXT_HTML)
	public String AddGetLeadTelmed(
			@Context HttpServletRequest request,
			@QueryParam("first_name") String first_name,
			@QueryParam("last_name") String last_name,
			@QueryParam("phone_number") String phone,
			@QueryParam("agent") String agent,
			@QueryParam("cell") String cell,
			@QueryParam("dob") String dob,
			@QueryParam("gender") String gender,
			@QueryParam("address") String address,
			@QueryParam("city") String city,
			@QueryParam("state") String state,
			@QueryParam("zip") String zip,
			@QueryParam("ssn") String ssn,
			@QueryParam("carrier") String carrier,
			@QueryParam("policy_id") String policy_id,
			@QueryParam("bin") String bin,
			@QueryParam("grp") String grp,
			@QueryParam("pcn") String pcn,
			@QueryParam("productSuggestions") String productSuggestions,
			@QueryParam("blueMosicProducts") String products,
			@QueryParam("marketingGroup") String marketingGroup,
			@QueryParam("email") String email,
			@QueryParam("pharmacy") String pharmacy,
			@QueryParam("source") String source,
			@QueryParam("allergies") String allergies,
			@QueryParam("medications") String medications) throws JSONException {
		RoadMapClient map = GetRoadMap("TELMED_ROADMAP");
		boolean validPharmacy = map.AcceptableTelmedPharmacy(pharmacy);
		if(!validPharmacy)
			return TelmedResponse.BuildFailedResponse(TelmedResponse.Errors.INVALID_PHARMACY).toString();
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
		record.setInsuranceName(carrier);
		record.setCarrier(record.getPBMFromBin(bin));
		record.setPolicyId(policy_id.toUpperCase());
		record.setBin(bin.toUpperCase());
		record.setGrp(grp.toUpperCase());
		record.setPcn(pcn.toUpperCase());
		record.setAgent(agent);
		record.setSource(source);
		record.setType(InsuranceFilter.Filter(record));
		boolean canPharmacyTake = map.CanPharmacyTake(record, pharmacy);
		map.close();
		if(!canPharmacyTake) {
			return TelmedResponse.BuildFailedResponse(TelmedResponse.Errors.NOT_IN_ROAD_MAP).toString();
		}
		InfoDatabase info = new InfoDatabase();
		String callCenter = info.GetCallCenter(agent);
		info.close();
		URIBuilder b = null;
		URL url = null;
		String[] dob_data = dob.split("/");
		String dob_month = dob_data[0];
		String dob_day = dob_data[1];
		String dob_year = dob_data[2];
		String group = Group.GetGroup(request);
		String ip = request.getRemoteHost();
		try {
			b = new URIBuilder(URLS.MTK_URL);
			b.addParameter(Parameters.FIRST_NAME, first_name)
			.addParameter(Parameters.LAST_NAME, last_name)
			.addParameter(Parameters.GENDER, gender)
			.addParameter(Parameters.PHONE_NUMBER, phone)
			.addParameter(Parameters.DOB_MONTH, dob_month)
			.addParameter(Parameters.DOB_DAY, dob_day)
			.addParameter(Parameters.DOB_YEAR, dob_year)
			.addParameter(Parameters.ADDRESS, address)
			.addParameter(Parameters.CITY, city)
			.addParameter(Parameters.STATE, state)
			.addParameter(Parameters.POSTAL_CODE, zip)
			.addParameter(Parameters.INSURANCE_CARRIER,  carrier)
			.addParameter(Parameters.PATIENT_ID, policy_id)
			.addParameter(Parameters.BIN, bin)
			.addParameter(Parameters.PCN, pcn)
			.addParameter(Parameters.GROUP_ID, grp)
			.addParameter(Parameters.SOURCE_ID, phone)
			.addParameter(Parameters.PRODUCT_SUGGESTIONS, productSuggestions)
			.addParameter(Parameters.SUB_PROGRAM, pharmacy)
			.addParameter(Parameters.INSERT_TIME, GetInsertTime())
			.addParameter(Parameters.MEDICATIONS, medications)
			.addParameter(Parameters.ALLERGIES, allergies)
			.addParameter(Parameters.SIGNATURE_VALIDATION, MTK_SIGNATURE);
			url = b.build().toURL();
			JSONObject telmedResponse = AddLead(record,url,"MT_MARKETING",ip,group,pharmacy,callCenter);
			return telmedResponse.toString();
		} catch (URISyntaxException | MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	@GET	
 	@Path("AddLeadGetComan")
 	@Produces(MediaType.TEXT_HTML)
	public String AddGetLeadTelmedComan(
			@Context HttpServletRequest request,
			@QueryParam("first_name") String first_name,
			@QueryParam("last_name") String last_name,
			@QueryParam("phone") String phone,
			@QueryParam("agent") String agent,
			@QueryParam("cell") String cell,
			@QueryParam("dob") String dob,
			@QueryParam("gender") String gender,
			@QueryParam("address") String address,
			@QueryParam("city") String city,
			@QueryParam("state") String state,
			@QueryParam("zip") String zip,
			@QueryParam("ssn") String ssn,
			@QueryParam("carrier") String carrier,
			@QueryParam("policy_id") String policy_id,
			@QueryParam("bin") String bin,
			@QueryParam("grp") String grp,
			@QueryParam("pcn") String pcn,
			@QueryParam("productSuggestions") String productSuggestions,
			@QueryParam("blueMosicProducts") String products,
			@QueryParam("marketingGroup") String marketingGroup,
			@QueryParam("email") String email,
			@QueryParam("pharmacy") String pharmacy,
			@QueryParam("source") String source,
			@QueryParam("allergies") String allergies,
			@QueryParam("medications") String medications) {
		RoadMapClient map = GetRoadMap("COMAN_ROADMAP");
		boolean validPharmacy = map.AcceptableTelmedPharmacy(pharmacy);
		if(!validPharmacy)
			return "INVALID PHARMACY";
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
		record.setInsuranceName(carrier);
		record.setCarrier(record.getPBMFromBin(bin));
		record.setPolicyId(policy_id.toUpperCase());
		record.setBin(bin.toUpperCase());
		record.setGrp(grp.toUpperCase());
		record.setPcn(pcn.toUpperCase());
		record.setAgent(agent);
		record.setSource(source);
		record.setType(InsuranceFilter.Filter(record));
		boolean canPharmacyTake = map.CanPharmacyTake(record, pharmacy);
		map.close();
		if(!canPharmacyTake) {
			return "INVALID! This lead is not in this pharmacy's roadmap";
		}
		InfoDatabase info = new InfoDatabase();
		String callCenter = info.GetCallCenter(agent);
		info.close();
		URIBuilder b = null;
		URL url = null;
		String[] dob_data = dob.split("/");
		String dob_month = dob_data[0];
		String dob_day = dob_data[1];
		String dob_year = dob_data[2];
		String group = Group.GetGroup(request);
		String ip = request.getRemoteHost();
		try {
			b = new URIBuilder(URLS.COMAN_URL);
			b.addParameter(Parameters.FIRST_NAME, first_name)
			.addParameter(Parameters.LAST_NAME, last_name)
			.addParameter(Parameters.GENDER, gender)
			.addParameter(Parameters.PHONE_NUMBER, phone)
			.addParameter(Parameters.DOB_MONTH, dob_month)
			.addParameter(Parameters.DOB_DAY, dob_day)
			.addParameter(Parameters.DOB_YEAR, dob_year)
			.addParameter(Parameters.ADDRESS, address)
			.addParameter(Parameters.CITY, city)
			.addParameter(Parameters.STATE, state)
			.addParameter(Parameters.POSTAL_CODE, zip)
			.addParameter(Parameters.INSURANCE_CARRIER,  carrier)
			.addParameter(Parameters.PATIENT_ID, policy_id)
			.addParameter(Parameters.BIN, bin)
			.addParameter(Parameters.PCN, pcn)
			.addParameter(Parameters.GROUP_ID, grp)
			.addParameter(Parameters.SOURCE_ID, phone)
			.addParameter(Parameters.PRODUCT_SUGGESTIONS, productSuggestions)
			.addParameter(Parameters.SUB_PROGRAM, pharmacy)
			.addParameter(Parameters.INSERT_TIME, GetInsertTime())
			.addParameter(Parameters.MEDICATIONS, medications)
			.addParameter(Parameters.ALLERGIES, allergies)
			.addParameter(Parameters.SIGNATURE_VALIDATION, COMAN_SIGNATURE);
			url = b.build().toURL();
			JSONObject telmedResponse = AddLead(record,url,"Coman_Marketing",ip,group,pharmacy,callCenter);
			return telmedResponse.toString();
		} catch (URISyntaxException | MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
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
	
	@GET
 	@Path("AddDME")
 	@Produces(MediaType.TEXT_HTML)
	public String AddDME(@Context HttpServletRequest request,
						@QueryParam("phone_number") String phone_number,
						@QueryParam("status") String status) {
		DMEClient client = new DMEClient("MT_MARKETING");
		switch(status) {
			case "":
				return "Pick a status";
			case "Passed SnS":
				client.UpdateSnS(phone_number,status, 1);
				break;
			case "Failed SnS":
				client.UpdateSnS(phone_number,status, -1);
				return "Updated Record to Fail Same and Similar";
			case "HMO":
				client.UpdateSnS(phone_number,status, -2);
				return "Updated Patient has HMO";
			case "Not Covered":
				client.UpdateSnS(phone_number,status, -3);
				return "Updated Not Covered";
			case "Copay Too High":
				client.UpdateSnS(phone_number,status, -4);
				return "Updated Copay Too High";
			case "Medicare Primary":
				client.UpdateSnS(phone_number,status, -5);
				return "Updated Medicare Primary";
			case "Prior Authorization Required":
				client.UpdateSnS(phone_number, status, -6);
				return "Updated Prior Authorization Required";
			default:
				return "Unknown Status";
		}
		Record record = client.getRecord(phone_number);
		if(record==null)
			return "RECORD NOT FOUND";
		URIBuilder b = null;
		URL url = null;
		try {
			String[] dob_data = record.getDob().split("/");
			String dob_month = dob_data[0];
			String dob_day = dob_data[1];
			String dob_year = dob_data[2];
			b = new URIBuilder(URLS.MTK2_URL);
			b.addParameter(Parameters.FIRST_NAME, record.getFirstName())
			.addParameter(Parameters.LAST_NAME, record.getLastName())
			.addParameter(Parameters.GENDER, record.getGender())
			.addParameter(Parameters.PHONE_NUMBER, record.getPhone())
			.addParameter(Parameters.DOB_MONTH, dob_month)
			.addParameter(Parameters.DOB_DAY, dob_day)
			.addParameter(Parameters.DOB_YEAR, dob_year)
			.addParameter(Parameters.ADDRESS, record.getAddress())
			.addParameter(Parameters.CITY, record.getCity())
			.addParameter(Parameters.STATE, record.getState())
			.addParameter(Parameters.POSTAL_CODE, record.getZip())
			.addParameter(Parameters.INSURANCE_CARRIER,  record.getCarrier())
			.addParameter(Parameters.PATIENT_ID, record.getPolicyId())
			.addParameter(Parameters.BIN, record.getBin())
			.addParameter(Parameters.PCN, record.getPcn())
			.addParameter(Parameters.GROUP_ID, record.getGrp())
			.addParameter(Parameters.SOURCE_ID, record.getPhone())
			.addParameter(Parameters.PRODUCT_SUGGESTIONS, record.getBraceList())
			.addParameter(Parameters.SUB_PROGRAM, "Orbis")
			.addParameter(Parameters.INSERT_TIME, GetInsertTime())
			.addParameter(Parameters.SIGNATURE_VALIDATION, MTK2_SIGNATURE);
			url = b.build().toURL();
			return AddLeadToRDS(record,url,null);
		} catch (URISyntaxException | MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	@POST
 	@Path("AddLeadPost")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
 	@Produces(MediaType.TEXT_HTML)
	public String AddLeadPostTelmed(
			@Context HttpServletRequest request,
			@FormParam("first_name") String first_name,
			@FormParam("last_name") String last_name,
			@FormParam("phone") String phone,
			@FormParam("agent") String agent,
			@FormParam("cell") String cell,
			@FormParam("dob") String dob,
			@FormParam("gender") String gender,
			@FormParam("address") String address,
			@FormParam("city") String city,
			@FormParam("state") String state,
			@FormParam("zip") String zip,
			@FormParam("ssn") String ssn,
			@FormParam("carrier") String carrier,
			@FormParam("policy_id") String policy_id,
			@FormParam("bin") String bin,
			@FormParam("grp") String grp,
			@FormParam("pcn") String pcn,
			@FormParam("productSuggestions") String productSuggestions,
			@FormParam("blueMosicProducts") String products,
			@FormParam("marketingGroup") String marketingGroup,
			@FormParam("email") String email,
			@FormParam("source") String source,
			@FormParam("pharmacy") String pharmacy,
			@FormParam("allergies") String allergies,
			@FormParam("medications") String medications) throws JSONException {
		RoadMapClient map = GetRoadMap("TELMED_ROADMAP");
		boolean validPharmacy = map.AcceptableTelmedPharmacy(pharmacy);
		if(!validPharmacy)
			return "INVALID PHARMACY";
		Record record = new Record();
		record.setFirstName(first_name.toUpperCase());
		record.setLastName(last_name.toUpperCase());
		record.setDob(dob);
		record.setGender(gender.toUpperCase());
		record.setPhone(phone.toUpperCase());
		record.setAddress(address.toUpperCase());
		record.setCity(city.toUpperCase());
		record.setState(state.toUpperCase());
		record.setZip(zip);
		record.setSsn(ssn);
		record.setInsuranceName(carrier);
		record.setCarrier(record.getPBMFromBin(bin));
		record.setPolicyId(policy_id.toUpperCase());
		record.setBin(bin.toUpperCase());
		record.setGrp(grp.toUpperCase());
		record.setPcn(pcn.toUpperCase());
		record.setAgent(agent);
		record.setSource(source);
		record.setType(InsuranceFilter.Filter(record));
		boolean canPharmacyTake = map.CanPharmacyTake(record, pharmacy);
		map.close();
		if(!canPharmacyTake) {
			return	TelmedResponse.BuildFailedResponse(TelmedResponse.Errors.NOT_IN_ROAD_MAP).toString();
		}
		InfoDatabase info = new InfoDatabase();
		String callCenter = info.GetCallCenter(agent);
		info.close();
		URIBuilder b = null;
		URL url = null;
		String[] dob_data = dob.split("/");
		String dob_month = dob_data[0];
		String dob_day = dob_data[1];
		String dob_year = dob_data[2];
		String ip = request.getRemoteAddr();
		String group = Group.GetGroup(request);
		try {
			b = new URIBuilder(URLS.MTK_URL);
			b.addParameter(Parameters.FIRST_NAME, first_name)
			.addParameter(Parameters.LAST_NAME, last_name)
			.addParameter(Parameters.GENDER, gender)
			.addParameter(Parameters.PHONE_NUMBER, phone)
			.addParameter(Parameters.DOB_MONTH, dob_month)
			.addParameter(Parameters.DOB_DAY, dob_day)
			.addParameter(Parameters.DOB_YEAR, dob_year)
			.addParameter(Parameters.ADDRESS, address)
			.addParameter(Parameters.CITY, city)
			.addParameter(Parameters.STATE, state)
			.addParameter(Parameters.POSTAL_CODE, zip)
			.addParameter(Parameters.INSURANCE_CARRIER,  carrier)
			.addParameter(Parameters.PATIENT_ID, policy_id)
			.addParameter(Parameters.BIN, bin)
			.addParameter(Parameters.PCN, pcn)
			.addParameter(Parameters.GROUP_ID, grp)
			.addParameter(Parameters.SOURCE_ID, phone)
			.addParameter(Parameters.PRODUCT_SUGGESTIONS, productSuggestions)
			.addParameter(Parameters.SUB_PROGRAM, pharmacy)
			.addParameter(Parameters.INSERT_TIME, GetInsertTime())
			.addParameter(Parameters.MEDICATIONS, medications)
			.addParameter(Parameters.ALLERGIES, allergies)
			.addParameter(Parameters.SIGNATURE_VALIDATION, MTK_SIGNATURE);
			url = b.build().toURL();
			JSONObject telmedResponse = AddLead(record,url,"MT_MARKETING",ip,group,pharmacy,callCenter);
			return telmedResponse.toString();
		} catch (URISyntaxException | MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	private JSONObject AddLead(Record record,URL url,String database,String ip,String grp,String pharmacy,String callCenter) {
		DatabaseClient client = new DatabaseClient(database);
		if(client.connect==null)
			client = new DatabaseClient(database);
		try {
			if(!client.CheckTelmedDuplicate(record.getPhone())) {
				incrementTelmed(grp);
				int add = client.AddToTelmed(record,ip,pharmacy,callCenter);
				if(add==1) {
					String id = AddLeadToRDS(record,url,client);
					return TelmedResponse.BuildSuccessfulResponse(record.getFirstName(), record.getLastName(), id);
				}
				else {
					return TelmedResponse.BuildFailedResponse("Error Code: "+add, record.getFirstName(), record.getLastName());
				}
			}
			else {
				return client.GetDuplicateResponse(record.getPhone());
			}
		}
		catch(JSONException ex) {
			return null;
		}
		finally {
			client.close();
		}
	}
	public String AddLeadToRDS(Record record,URL url,DatabaseClient dbClient) {
		try {
			HttpGet get = new HttpGet(url.toString());
			CloseableHttpClient client = HttpClients.createDefault();
			CloseableHttpResponse response = client.execute(get);
			int status_code = response.getStatusLine().getStatusCode();
			if(status_code==200) {
				HttpEntity entity = response.getEntity();
				String responseString = EntityUtils.toString(entity, "UTF-8");
				String recordId = ExtractRDSId(responseString);
				if(dbClient!=null)
					dbClient.UpdateTelemdID(record, recordId);
				response.close();
				client.close();
				return recordId;
			}
			else {
				response.close();
				client.close();
				return "ERROR CODE: "+status_code+"\n"+response;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return e.getMessage();
		}
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
	
	
}
