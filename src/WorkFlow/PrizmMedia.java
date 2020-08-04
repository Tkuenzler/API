package WorkFlow;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import PBM.InsuranceFilter;
import PBM.InsuranceType;
import client.DatabaseClient;
import client.EmdeonClient;
import client.InfoDatabase;
import client.Record;

@Path("Prizm")
public class PrizmMedia {
	private static final String PRIZEM_KEY = "5$3&41";
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("DupeCheck")
	public Response DupeCheck(@QueryParam("phone") String phone) {
		DatabaseClient client = new DatabaseClient("MT_MARKETING");
		if(client.IsTelmedDuplicate(phone)) {
			client.close();
			return Response.status(400).entity("DUPLICATE").build();
		}
		if(client.isDuplicate(phone)) {
			Record record = client.GetRecordByPhone(phone, "Leads");
			//Add Record to prism database
			client.close();
			return Response.status(400).entity("DUPLICATE").build();
		}
		client.close();
		return Response.status(200).entity("UNIQUE").build();
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("InsuranceCheck")
	public Response InsuranceCheck(@QueryParam("first_name") String first_name,
								@QueryParam("last_name") String last_name,
								@QueryParam("dob") String dob,
								@QueryParam("gender") String gender,
								@QueryParam("zip") String zip,
								@QueryParam("phone") String phone,
								@QueryParam("key") String key) {
		if(!key.equalsIgnoreCase(PRIZEM_KEY))
			return Response.status(400).entity("INVALID KEY").build();
		Record record = new Record();
		record.setFirstName(first_name.trim());
		record.setLastName(last_name.trim());
		record.setDob(dob.trim());
		record.setGender(gender.trim());
		record.setZip(zip.trim());
		record.setPhone(phone.trim());
		if(OverAge64(record.getDob()))
			return Response.status(400).entity("OVER 65").build();
		if(record.getZip().length()!=5)
			return Response.status(400).entity("INVALID ZIP").build();
		InfoDatabase info = GetInfoDatabase();
		if(HasBeenLookedUp(info,record)) {
			info.GetInsuranceInfo(record);
			info.close();
		}
		else {
			incrementLookup(info,"PRIZM");
			AddToCheck(info,record,"PRIZM","");
			CheckInsurance(record);
			if(record.getStatus().equalsIgnoreCase("FOUND"))
				UpdateCheckedStatus(info,record,InsuranceFilter.Filter(record));
			else
				UpdateCheckedStatus(info,record,"");
		}
		if(!record.getStatus().equalsIgnoreCase("FOUND"))
			return Response.status(400).entity("INSURANCE NOT FOUND").build();
		String insuranceType = InsuranceFilter.Filter(record);
		switch(insuranceType) {
			case InsuranceType.PRIVATE_VERIFIED:
				return Response.status(200).entity("ACCEPTED").build();
			case InsuranceType.MAPD:
			case InsuranceType.MAPD_HMO:
			case InsuranceType.MAPD_PPO:
			case InsuranceType.MEDICAID_MEDICARE:
			case InsuranceType.MEDICARE_COMMERCIAL:
			case InsuranceType.MEDICARE_TELMED:
				return Response.status(400).entity("Medicare").build();
			case InsuranceType.PRIVATE_NO_TELMED:
			case InsuranceType.NOT_COVERED:
			case InsuranceType.PRIVATE_UNKNOWN:
			case InsuranceType.UNKNOWN_PBM:
				return Response.status(400).entity("NOT ACCEPTED").build();
			case InsuranceType.MEDICAID:
			case InsuranceType.MOLINA:
			case InsuranceType.NO_COVERAGE:
				return Response.status(400).entity("Medicaid").build();
			default:
				return Response.status(400).entity("Unknown").build();
		}
		
	}
	public static boolean OverAge64(String dob) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		LocalDate birthDate = LocalDate.parse(dob,formatter);
		LocalDate currentDate = LocalDate.now();
		int currentAge = Period.between(birthDate, currentDate).getYears();
		if(currentAge>=64)
			return true;
		else 
			return false;
	}
	private Record CheckInsurance(Record record) {
		EmdeonClient client = new EmdeonClient();
		client.login("rxcg", "pharmacy123", "1619320132");
		client.fillOutForm(record);
		if(client!=null);
			client.close();
		return record;
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
}
