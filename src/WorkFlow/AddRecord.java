package WorkFlow;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.json.JSONException;
import Pharmacy.PharmacyMap;
import ResponseBuilder.AddRecordResponse;
import client.DatabaseClient;
import client.EmdeonClient;
import client.InfoDatabase;
import client.Record;
import client.RoadMapClient;
import Database.Columns.TelmedColumns;
import Database.Tables.Tables;


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
		if(record.getStatus().equalsIgnoreCase(""))
			EmdeonPatient(record);
		info.close();
		return AddRecordToTable(record,agent,callCenter,"MT_MARKETING","MT-LIVE","TELMED_ROADMAP");
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
		if(client!=null)
			client.close();
	}
	private String AddRecordToTable(Record record,String agent,String callCenter,String database,String afid,String roadmap) throws IOException, URISyntaxException, JSONException {
		RoadMapClient roadMapClient = new RoadMapClient(roadmap);
		record.setPharmacy(GetPharmacy(record,roadMapClient));
		roadMapClient.close();
		DatabaseClient client = new DatabaseClient(database);
		try {
			int add = client.addRecord(record,Tables.LEADS,afid,record.getPharmacy(),agent,callCenter);
			switch(add) {
				case 1:
					return AddRecordResponse.BuildSuccesfulResponse(record);
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
						int updated = client.updateRecord(record, Tables.LEADS, afid, record.getPharmacy(), agent, callCenter);
						if(updated==1)
							AddRecordResponse.BuildSuccesfulUpdateResponse(record);
						else
							return AddRecordResponse.BuildErrorResponse(add);
							
					}
				}
				default:
					return AddRecordResponse.BuildErrorResponse(add);
			}
		} catch(Exception ex) {
			return AddRecordResponse.BuildErrorResponse(ex.getMessage());
		} finally {
			if(client!=null) client.close();
		}
		
		
	}
	private String GetPharmacy(Record record,RoadMapClient client) {
		ArrayList<PharmacyMap> roadMap = new ArrayList<PharmacyMap>();
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
			roadMap.add(map);
		}
		client.close();
		return Pharmacy.Pharmacy.GetPharmacy(roadMap, record);
	}
}
