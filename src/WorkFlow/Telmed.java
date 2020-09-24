package WorkFlow;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.JSONException;
import org.json.JSONObject;

import Database.Columns.TelmedColumns;
import Database.Query.Query;
import Database.Tables.Tables;
import JSONParameters.BlueMosiacParameters;
import JSONParameters.RDSParameters;
import JSONParameters.TriageParameters;
import ResponseBuilder.BlueMosiacResponse;
import ResponseBuilder.TelmedResponse;
import Telmed.BillingStatus;
import Telmed.TelmedCompany;
import client.BlueMosiacClient;
import client.DMEClient;
import client.Database;
import client.DatabaseClient;
import client.RDSClient;

@Path("Telmed")
public class Telmed {
	
	@GET
	@Path("GetQA")
	@Produces(MediaType.TEXT_HTML) 
	public String GetQA() {
		Database client = new Database("MT_MARKETING");
		ResultSet set = null;
		try {
			if(!client.login())
				return "LOGIN FAILED";
			set = client.select(Tables.TELMED, null, TelmedColumns.BILLING_STATUS+" = ? AND "+TelmedColumns.SUBMITTED+" = ?", new String[] {"Covered","0"});
			StringBuilder sb = new StringBuilder();
			sb.append("<table id='records'>");
			sb.append("<tr bgcolor='#d3d3d3'>");
			sb.append("<td>Date Added</td>");
			sb.append("<td>Pharmacy</td>");
			sb.append("<td>Name</td>");
			sb.append("<td>Phone Number</td>");
			sb.append("<td>Record ID</td>");
			sb.append("<td>Telmed Company</td>");
			sb.append("<td>Medications</td>");
			sb.append("<td>Carrier</td>");
			sb.append("<td>Policy Id</td>");
			sb.append("<td>Bin</td>");
			sb.append("<td>Group</td>");
			sb.append("<td>PCN</td>");
			sb.append("<td>Submit</td>");
			sb.append("<td>Submit Recording</td>");
			sb.append("</tr>");
			int count = 1;
			while(set.next()) {
				String name = set.getString(TelmedColumns.FIRST_NAME)+" "+set.getString(TelmedColumns.LAST_NAME);
				sb.append("<tr>");
				sb.append("<td>"+set.getString(TelmedColumns.DATE_ADDED)+"</td>");
				sb.append("<td>"+set.getString(TelmedColumns.PHARMACY)+"</td>");
				sb.append("<td>"+name+"</td>");
				sb.append("<td>"+set.getString(TelmedColumns.PHONE)+"</td>");
				sb.append("<td>"+set.getString(TelmedColumns.TELMED_ID)+"</td>");
				sb.append("<td>"+set.getString(TelmedColumns.TELMED_COMPANY)+"</td>");
				sb.append("<td>"+set.getString(TelmedColumns.MEDICATIONS)+"</td>");
				sb.append("<td>"+set.getString(TelmedColumns.CARRIER)+"</td>");
				sb.append("<td>"+set.getString(TelmedColumns.POLICY_ID)+"</td>");
				sb.append("<td>"+set.getString(TelmedColumns.BIN)+"</td>");
				sb.append("<td>"+set.getString(TelmedColumns.GRP)+"</td>");
				sb.append("<td>"+set.getString(TelmedColumns.PCN)+"</td>");
				sb.append("<td><input type='button' value='Submit "+name+"' onclick='SubmitData("+count+")'/></td>");
				sb.append("<td><input type='button' value='Submit Recording "+name+"' onclick='RecordingWebform("+count+")'/></td>");
				sb.append("</tr>");
				count++;
			}
			sb.append("</table>");
			return sb.toString();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return TelmedResponse.BuildFailedResponse(e.getMessage(), e.getErrorCode()).toString();
		} finally {
			try {
				if(set!=null)set.close();
				if(client!=null)client.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
	
	@GET
	@Path("UpdateQA")
	@Produces(MediaType.TEXT_HTML)
	public String GetTelmeds(@QueryParam("phone_number") String phone_number,
							@QueryParam("telmed_company") String telmed_company) {
		try {
			Database client = new Database("MT_MARKETING");
			if(!client.login())
				return "LOGIN FAILED";
			JSONObject triage = null;
			switch(telmed_company) {
				case TelmedCompany.BLUE_MOSIAC:
					ResultSet set = client.select(Tables.TELMED, null, TelmedColumns.PHONE+" = ?", new String[] {phone_number});
					if(set.next())
						triage = new JSONObject(set.getString(TelmedColumns.TRIAGE));
					else 
						return "NO TRIAGE";
					JSONObject obj = BlueMosiacClient.AddToBlueMosiac(BlueMosiacParameters.ConvrtToBlueMosiacJSON(triage));
					client.update(Tables.TELMED, new String[] {TelmedColumns.TELMED_ID}, new String[] {obj.getString(BlueMosiacResponse.PATIENT_ID)}, TelmedColumns.PHONE+" = '"+phone_number+"'");
					return obj.toString();
				case TelmedCompany.RDS:
					return BlueMosiacResponse.BuildSuccessfulResponse("NOT SET UP FOR RDS").toString();
				case TelmedCompany.CAMELOT:
					client.update(Tables.TELMED, new String[] {TelmedColumns.SUBMITTED}, new String[] {"1"}, TelmedColumns.PHONE+" = '"+phone_number+"'");
					return BlueMosiacResponse.BuildSuccessfulResponse("Patient has been updated").toString();
				default:
					return BlueMosiacResponse.BuildFailedResponse("INVALID TELMED COMPANY").toString();
			}
		} catch(SQLException ex) {
			return BlueMosiacResponse.BuildFailedResponse(ex.getMessage()).toString();
		} catch(JSONException ex) {
			return BlueMosiacResponse.BuildFailedResponse(ex.getMessage()).toString();
		}
	}
	
	@GET
	@Path("GetTelmeds")
	@Produces(MediaType.TEXT_HTML) 
	public String GetTelmeds(@QueryParam("status") String status) {
		Database client = new Database("MT_MARKETING");
		ResultSet set = null;
		try {
			if(!client.login())
				return "LOGIN FAILED";
			set = client.selectSort(Tables.TELMED, null, "("+TelmedColumns.TELMED_STATUS+" = ? OR "+TelmedColumns.TELMED_STATUS+" = ?) AND "+TelmedColumns.BILLING_STATUS+" = ?", new String[] {"Pharmacy - Pending Pharmacy",TriageParameters.TRIAGE_COMPLETE,status},new String[] {TelmedColumns.PHARMACY,TelmedColumns.DATE_ADDED},new String[] {Query.Order.ASCENDING,Query.Order.ASCENDING});
			StringBuilder sb = new StringBuilder();
			sb.append("<table id='records'>");
			boolean additionalInfo = false;
			if(status.equalsIgnoreCase(BillingStatus.ENROLL))
				additionalInfo = true;
			/*
			 * BUILD HEADERS
			 */
			sb.append("<tr bgcolor='#d3d3d3'>");
			sb.append("<td>Date Added</td>");
			sb.append("<td>Pharmacy</td>");
			sb.append("<td>Name</td>");
			sb.append("<td>Phone Number</td>");
			sb.append("<td>Record ID</td>");
			sb.append("<td>DOB</td>");
			if(additionalInfo) {
				sb.append("<td>Address</td>");
				sb.append("<td>City</td>");
				sb.append("<td>State</td>");
			}
			sb.append("<td>Zip-Code</td>");
			sb.append("<td>Policy ID</td>");
			sb.append("<td>Bin</td>");
			sb.append("<td>Rx Group</td>");
			sb.append("<td>PCN</td>");
			sb.append("<td>NPI</td>");
			if(additionalInfo) {
				sb.append("<td>Pain Location</td>");
				sb.append("<td>Pain Cause</td>");
			}
			sb.append("<td>Product Suggestions</td>");
			sb.append("<td>Telmed Company</td>");
			sb.append("<td>Billing Status</td>");
			sb.append("<td>Select Products</td>");
			sb.append("<td>Submit</td>");
			sb.append("<td>Webform</td>");
			sb.append("</tr>");
			int count = 1;
			while(set.next()) {
				/*
				 * EXTRACT INFORMATION FROM RESULT SET
				 */
				String traigeText = set.getString(TelmedColumns.TRIAGE);
				String pain_location = null;
				String pain_cause = null;
				JSONObject triage = null;
				if(traigeText.startsWith("{")) {
					triage = new JSONObject(traigeText);
					pain_location = triage.getString(TriageParameters.PAIN_LOCATION);
					pain_cause = triage.getString(TriageParameters.PAIN_CAUSE);
				}
				else {
					pain_location = "";
					pain_cause = "";
				}
					
				String id = set.getString(TelmedColumns.TELMED_ID);
				String name = set.getString(TelmedColumns.FIRST_NAME)+" "+set.getString(TelmedColumns.LAST_NAME);
				String pharmacy = set.getString(TelmedColumns.PHARMACY);
				String date_added = set.getString(TelmedColumns.DATE_ADDED);
				String npi = set.getString(TelmedColumns.NPI);
				String number = set.getString(TelmedColumns.PHONE);
				String dob = set.getString(TelmedColumns.DOB);
				String address = set.getString(TelmedColumns.ADDRESS);
				String city = set.getString(TelmedColumns.CITY);
				String state = set.getString(TelmedColumns.STATE);
				String zip = set.getString(TelmedColumns.ZIP);
				String policy_id = set.getString(TelmedColumns.POLICY_ID);
				String bin = set.getString(TelmedColumns.BIN);
				String grp = set.getString(TelmedColumns.GRP);
				String pcn = set.getString(TelmedColumns.PCN);
				String products = getRequestedProducts(triage,count);
				String telmedCompanies = getTelmedCompanyOptions(count);
				
				/*
				 * Extract Products 
				 */
				
				/*
				 * BUILD HTML TABLE WITH INFORMATION
				 */
				sb.append("<tr>");
				sb.append("<td>"+date_added+"</td>");
				sb.append("<td>"+pharmacy+"</td>");
				sb.append("<td>"+name+"</td>");
				sb.append("<td>"+number+"</td>");
				sb.append("<td>"+id+"</td>");
				sb.append("<td>"+dob+"</td>");
				if(additionalInfo) {
					sb.append("<td>"+address+"</td>");
					sb.append("<td>"+city+"</td>");
					sb.append("<td>"+state+"</td>");
				}
			
				sb.append("<td>"+zip+"</td>");
				sb.append("<td>"+policy_id+"</td>");
				sb.append("<td>"+bin+"</td>");
				sb.append("<td>"+grp+"</td>");
				sb.append("<td>"+pcn+"</td>");
				sb.append("<td>"+npi+"</td>");
				if(additionalInfo) {
					sb.append("<td>"+pain_location+"</td>");
					sb.append("<td>"+pain_cause+"</td>");
				}
				sb.append("<td>"+products+"</td>");
				sb.append("<td>"+telmedCompanies+"</td>");
				sb.append("<td>"+getBillingStatus(count)+"</td>");
				sb.append("<td><input type='text' value='' id='medications "+count+"' /></td>");
				sb.append("<td><input type='button' value='Submit "+name+"' name='"+count+"' id='"+count+"' onclick='SubmitData("+count+")' /></td>");
				sb.append("<td><input type='button' value='Open Webform "+name+"' onclick='OpenWebform("+count+")' /></td>");
				sb.append("</tr>");
				count++;
			}
			sb.append("</table>");
			return sb.toString();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return TelmedResponse.BuildFailedResponse(e.getMessage(), e.getErrorCode()).toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return TelmedResponse.BuildFailedResponse(e.getMessage()).toString();
		} 
		finally {
			try {
				if(set!=null)set.close();
				if(client!=null)client.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
	
	@GET
	@Path("SubmitBillingStatus")
	@Produces(MediaType.TEXT_HTML) 
	public String SubmitBillingStatus(@QueryParam("phone_number") String phone_number,
			@QueryParam("billing_status") String billing_status,
			@QueryParam("products") String products,
			@QueryParam("telmed_company") String telmed_company,
			@QueryParam("medications") String medications) throws JSONException {
		Database client = new Database("MT_MARKETING");
		ResultSet set = null;
		try {
			if(!client.login())
				return "Login Failed";
			if(billing_status.equalsIgnoreCase("Covered")) {
				JSONObject triage = null;
				int value;
				switch(telmed_company) {
					case TelmedCompany.BLUE_MOSIAC:
						set = client.select(Tables.TELMED, null, TelmedColumns.PHONE+" = ?", new String[] {phone_number});
						if(set.next()) {
							String triageString = set.getString(TelmedColumns.TRIAGE);
							if(triageString!=null)
								triage = new JSONObject(triageString);
							else
								return TelmedResponse.BuildFailedResponse("NO TRIAGE").toString();
							client.update(Tables.TELMED, new String[] {TelmedColumns.BILLING_STATUS,TelmedColumns.TELMED_COMPANY,TelmedColumns.MEDICATIONS},
									new String[] {billing_status,telmed_company,medications}, TelmedColumns.PHONE+" = '"+phone_number+"'");
						}
						return TelmedResponse.BuildSuccessfulResponse("Updated Patient Billing Status").toString();
					case TelmedCompany.CAMELOT:
						value = client.update(Tables.TELMED, new String[] {TelmedColumns.BILLING_STATUS,TelmedColumns.TELMED_COMPANY,TelmedColumns.MEDICATIONS},
								new String[] {billing_status,telmed_company,medications}, TelmedColumns.PHONE+" = '"+phone_number+"'");
						if(value==1)
							return TelmedResponse.BuildSuccessfulResponse("Updated Patient Billing Status").toString();
						else
							return TelmedResponse.BuildFailedResponse(value, "UNKOWN ERROR").toString();
					case TelmedCompany.RDS:
						value = client.update(Tables.TELMED, new String[] {TelmedColumns.BILLING_STATUS,TelmedColumns.TELMED_COMPANY},
								new String[] {billing_status,telmed_company}, TelmedColumns.PHONE+" = '"+phone_number+"'");
						set =client.select(Tables.TELMED, null, TelmedColumns.PHONE+" = ?", new String[] {phone_number});
						if(set.next()) {
							String triageString = set.getString(TelmedColumns.TRIAGE);
							if(triageString!=null)
								triage = new JSONObject(triageString);
							else
								return TelmedResponse.BuildFailedResponse("NO TRIAGE").toString();
						}
						return RDSClient.AddLeadToRDS(RDSParameters.ConvertToRDSJSON(triage)).toString();
					default:
						return TelmedResponse.BuildFailedResponse("INVALID TELMED COMPANY").toString();
				}
			}
			else {
				int value = client.update(Tables.TELMED, new String[] {TelmedColumns.BILLING_STATUS},
						new String[] {billing_status}, TelmedColumns.PHONE+" = '"+phone_number+"'");
				return TelmedResponse.BuildSuccessfulResponse("SUCCESFULLY UPDATED AS "+billing_status+" "+value).toString();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return TelmedResponse.BuildFailedResponse(e.getMessage()).toString();
		} finally {
			try {
				if(set!=null) set.close();
				if(client!=null)client.close();
			} catch(SQLException ex) {
				
			}
		}
	}
	private String getTelmedCompanyOptions(int count) {
		StringBuilder sb = new StringBuilder();
		sb.append("<select id='telmed_company "+count+"' >");
		sb.append("<option value=''></option>");
		for(String company: TelmedCompany.COMPANIES) 
			sb.append("<option value='"+company+"'>"+company+"</option>");
		sb.append("</select>");
		return sb.toString();
	}
	private String willPayConsultationFees(JSONObject obj) throws JSONException {
		StringBuilder sb = new StringBuilder();
		String answer = null;
		String checked = null;
		if(obj!=null) {
			if(obj.has(TriageParameters.CONSULTATION_FEE)) {
				answer = obj.getString(TriageParameters.CONSULTATION_FEE);
				switch(answer) {
					case "Yes":
						checked = "checked";
						break;
					case "No":
						checked = "";
					case "Cant Afford":
					{
						int house_size = Integer.parseInt(obj.getString(TriageParameters.HOUSE_HOLD_SIZE));
						int annual_income = Integer.parseInt(obj.getString(TriageParameters.ANNUAL_INCOME));
						switch(house_size) {
							case 1:
								if(annual_income<12760)
									checked = "checked";
								break;
							case 2:
								if(annual_income<17240)
									checked = "checked";
								break;
							case 3:
								if(annual_income<21740)
									checked = "checked";
								break;
							case 4:
								if(annual_income<26200)
									checked = "checked";
								break;
							case 5:
								if(annual_income<30680)
									checked = "checked";
								break;
							case 6:
								if(annual_income<35160)
									checked = "checked";
								break;
							case 7:
								if(annual_income<39640)
									checked = "checked";
								break;
							case 8:
								if(annual_income<44120)
									checked = "checked";
								break;
							default:
								int max = annual_income+(4480*(house_size-8));
								if(annual_income<max)
									checked = "checked";
						}
						if(checked==null)
							checked = "";
					}
				}
			}
		}
		else
			checked = "";
		sb.append("<input type='checkbox' "+checked+" onclick='return false' />");
		return sb.toString();
	}
	private String getRequestedProducts(JSONObject triage,int count)  {
		StringBuilder sb = new StringBuilder();
		sb.append("<select id='triaged_products "+count+"'>");
		if(triage==null) {
			sb.append("<option value='pain'>pain</option>");
			sb.append("</select>");
			return sb.toString();
		}
		try {
			for(String product: TriageParameters.PRODUCTS) {
				if(triage.has(product))
					if(triage.getString(product).equalsIgnoreCase("Yes"))
						sb.append("<option value='"+product+"'>"+product+"</option>");
			}
			sb.append("</select>");	
			return sb.toString();
		} catch(JSONException ex) {
			sb.append("<option value='Pain'>Pain</option>");
			sb.append("</select>");
			return sb.toString();
		}
	}
	
	private String getBillingStatus(int count) {
		StringBuilder sb = new StringBuilder();
		sb.append("<select id='status "+count+"'>");
		sb.append("<option value=''></option>");
		for(String status: BillingStatus.STATUSES) {
			sb.append("<option value='"+status+"'>"+status+"</option>");
		}
		sb.append("</select>");
		return sb.toString();
	
	}
	
	@GET
	@Path("Status")
	@Produces(MediaType.TEXT_PLAIN)
	public String GetStatus(@QueryParam("phone") String phone,@QueryParam("database") String database) throws SQLException {
		DatabaseClient client = new DatabaseClient("MT_MARKETING");
		String status = client.GetTelmedStatus(phone);
		client.close();
		return status;
	}
	
	@GET
	@Path("GetTraige")
	@Produces(MediaType.TEXT_PLAIN)
	public String GetTraige(@QueryParam("phone") String phone,@QueryParam("database") String database) throws SQLException {
		Database client = new Database(database);
		client.login();
		ResultSet set = client.select(Tables.LEADS, new String[] {TelmedColumns.TRIAGE}, TelmedColumns.PHONE+" = ? AND "+TelmedColumns.TELMED_ID, new String[] {phone,""});
		String triage = set.getString(TelmedColumns.TRIAGE);
		client.close();
		return triage;
	}
	
	@GET
	@Path("GetDMEStatus")
	@Produces(MediaType.TEXT_PLAIN)
	public String GetDMEStatus(@QueryParam("phone") String phone,@QueryParam("database") String database) throws SQLException {
		DMEClient client = new DMEClient("MT_MARKETING");
		String telmedStatus = client.GetDMETelmedStatus(phone);
		String status = client.GetTelmedStatus(phone);
		int covered_status = client.GetCoveredStatus(phone);
		client.close();
		if(covered_status==1 && telmedStatus.equalsIgnoreCase(""))
			return "PATIENT IS GOOD TO TRANSFER";
		else if(covered_status<0)
			return "PATIENT FAILED SAME AND SIMILAR DO NOT TRANSFER";
		else
			return status;
	}
	
	@GET
	@Path("UpdateBlueMosiac")
	@Produces(MediaType.TEXT_PLAIN)
	public String UpdateBlueMosiac(@QueryParam("record_id") String record_id) throws SQLException {
		Database client = new Database("MT_MARKETING");
		if(!client.login())
			return "Login Failed";
		int update = client.update(Tables.TELMED, new String[] {TelmedColumns.SUBMITTED}, new String[] {"1"}, TelmedColumns.TELMED_ID+" =  '"+record_id+"'");
		return ""+update;
	}
	
	
	@GET
	@Path("UpdateStatus")
	@Produces(MediaType.TEXT_PLAIN)
	public String UpdateStatus(@QueryParam("phone") String phone,
								@QueryParam("id") String id,
								@QueryParam("status") String status,
								@QueryParam("notes") String notes) throws SQLException {
		DatabaseClient client = new DatabaseClient("MT_MARKETING");
		int update = client.UpdateTelmedFromRDS(phone,id,status,notes+" \r\n");
		String response = null;
		if(update>0)
			response = "Successful "+update;
		else {
			DMEClient dme = new DMEClient("MT_MARKETING");
			if(dme.UpdateTelmedFromRDS(phone, id, status, notes)>0)
				response = "Successful";
			else
				response = "Failed";
			dme.close();
			
		}
		client.close();
		return response;
	}
}
