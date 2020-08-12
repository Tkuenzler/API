package WorkFlow;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.json.JSONException;
import org.json.JSONObject;

import Database.Columns.TelmedColumns;
import Database.Tables.Tables;
import JSONParameters.BlueMosiacParameters;
import ResponseBuilder.TelmedResponse;
import client.BlueMosiacClient;
import client.InfoDatabase;
import client.TelmedDatabaseClient;

@Path("BlueMosiac")
public class BlueMosiac {

	@GET
	@Path("SubmitRecord")
	@Produces(MediaType.TEXT_HTML)
	public String SubmitRecord(@QueryParam("phone_number") String phone_number,
									@QueryParam("quick_codes") String quick_codes,
									@QueryParam("covered") String covered) {
		TelmedDatabaseClient client = new TelmedDatabaseClient("MT_MARKETING");
		JSONObject obj = null;
		ResultSet set = null;
		String response = null;
		try {
			if(!client.login())
				return "";
			set = client.select(Tables.TELMED, null, TelmedColumns.PHONE+" = ?", new String[] {phone_number});
			if(set.next()) {
				obj = new JSONObject(set.getString(TelmedColumns.TRIAGE));
				if(covered.equalsIgnoreCase("Covered")) {
					//COVERED FUNCTIONS
					obj.put(BlueMosiacParameters.QUICK_CODES, quick_codes);
					//String recordId = BlueMosiacClient.AddToBlueMosiac(obj).getString(BlueMosiacParameters.RECORD_ID);
					return BlueMosiacClient.AddToBlueMosiac(obj).toString();
					/*
					int update = client.update(Tables.TELMED, new String[] {TelmedColumns.TELMED_ID,TelmedColumns.TRIAGE}, new String[] {recordId,obj.toString()});
					if(update==1)
						return TelmedResponse.BuildSuccessfulResponse(obj).toString();
					else
						return TelmedResponse.BuildFailedResponse(update).toString();
					*/ 
				}
				else {
					//NOT COVERED FUNCTIONS
					int update = client.update(Tables.TELMED, new String[] {TelmedColumns.TELMED_STATUS}, new String[] {covered});
					return TelmedResponse.BuildFailedResponse("NOT COVERED").toString();
				}
			}
			else 
				return TelmedResponse.BuildFailedResponse("NOT FOUND: "+phone_number).toString();
		} catch(SQLException ex) {
			return TelmedResponse.BuildFailedResponse(ex.getMessage(), ex.getErrorCode()).toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return TelmedResponse.BuildFailedResponse(response).toString();
		} finally {
			try {
				if(set!=null)set.close();
				if(client!=null) client.close();
			} catch(SQLException ex) {
				
			}
		}
	}
	
	@GET
	@Path("GetCompletedTriages")
	@Produces(MediaType.TEXT_HTML)
	public String GetCompletedTriages() {
		TelmedDatabaseClient client = new TelmedDatabaseClient("MT_MARKETING");
		ResultSet set = null;
		try {
			if(!client.login())
				return "LOGIN FAILED";
			set = client.select(Tables.TELMED, null, TelmedColumns.TELMED_STATUS+" = ? AND "+TelmedColumns.TELMED_ID+" = ?", new String[] {"Complete",""});
			StringBuilder sb = new StringBuilder();
			sb.append("<table id='records'>");
			/*
			 * BUILD HEADERS
			 */
			sb.append("<tr bgcolor='#d3d3d3'>");
			sb.append("<td>Name</td>");
			sb.append("<td>Phone Number</td>");
			sb.append("<td>State</td>");
			sb.append("<td>DOB</td>");
			sb.append("<td>Zip-Code</td>");
			sb.append("<td>Policy ID</td>");
			sb.append("<td>Bin</td>");
			sb.append("<td>Rx Group</td>");
			sb.append("<td>PCN</td>");
			sb.append("<td>Dr. NPI</td>");
			sb.append("<td>Interested Products</td>");
			sb.append("<td>Quick Codes</td>");
			sb.append("<td>Covered</td>");
			sb.append("<td>Submit</td>");
			sb.append("</tr>");
			int count = 1;
			while(set.next()) {
				/*
				 * EXTRACT INFORMATION FROM RESULT SET
				 */
				JSONObject obj = new JSONObject(set.getString(TelmedColumns.TRIAGE));
				String name = set.getString(TelmedColumns.FIRST_NAME)+" "+set.getString(TelmedColumns.LAST_NAME);
				String number = set.getString(TelmedColumns.PHONE);
				String dob = set.getString(TelmedColumns.DOB);
				String zip = set.getString(TelmedColumns.ZIP);
				String state = set.getString(TelmedColumns.STATE);
				String policy_id = set.getString(TelmedColumns.POLICY_ID);
				String bin = set.getString(TelmedColumns.BIN);
				String grp = set.getString(TelmedColumns.GRP);
				String pcn = set.getString(TelmedColumns.PCN);
				String npi = obj.getString(BlueMosiacParameters.NPI);
				String coveredSelect = "<select id='covered "+count+"'><option value='Covered'>Covered</option><option value='Not Covered'>Not Covered</option></select>";
				StringBuilder productsList = new StringBuilder();
				/*
				 * Parse through triage to get products
				 */
				for(String product: BlueMosiacParameters.PRODUCTS) {
					if(!obj.has(product))
						continue;
					if(obj.getString(product).equalsIgnoreCase("Yes"))
					switch(product) {
						case BlueMosiacParameters.PAIN:
							productsList.append("<option value='Pain'>Pain</option>");
							break;
						case BlueMosiacParameters.MUSCLE_SPASMS:
							productsList.append("<option value='Muscle Spasms'>Muscle Spasms</option>");
							break;
						case BlueMosiacParameters.INFLAMMATION:
							productsList.append("<option value='Inflammation Pain'>Inflammation Pain</option>");
							break;
						case BlueMosiacParameters.SCAR_RASH:
							productsList.append("<option value='Scar/Rash'>Scar/Rash</option>");
							break;
						case BlueMosiacParameters.ANTI_FUNGAL:
							productsList.append("<option value='Anti-Fungal'>Anti-Fungal</option>");
							break;
						case BlueMosiacParameters.COLD_SORES:
							productsList.append("<option value='Cold Sores'>Cold Sores Pain</option>");
							break;
						case BlueMosiacParameters.ACID_REFLUX:
							productsList.append("<option value='Acid Reflux'>Acid Reflux</option>");
							break;
						case BlueMosiacParameters.MIGRAINES:
							productsList.append("<option value='Migraines'>Migraines</option>");
							break;
						case BlueMosiacParameters.WELLNESS:
							productsList.append("<option value='Wellness'>Wellness</option>");
							break;
						default:
							productsList.append("<option value='"+product+"'>UNKNOWN PRODUCT</option>");
							break;
							
					}
				}
				/*
				 * Extract Products 
				 */
				
				/*
				 * BUILD HTML TABLE WITH INFORMATION
				 */
				sb.append("<tr>");
				sb.append("<td>"+name+"</td>");
				sb.append("<td>"+number+"</td>");
				sb.append("<td>"+state+"</td>");
				sb.append("<td>"+dob+"</td>");
				sb.append("<td>"+zip+"</td>");
				sb.append("<td>"+policy_id+"</td>");
				sb.append("<td>"+bin+"</td>");
				sb.append("<td>"+grp+"</td>");
				sb.append("<td>"+pcn+"</td>");
				sb.append("<td>"+npi+"</td>");
				sb.append("<td><select id='' >"+productsList.toString()+"</select></td>");
				sb.append("<td><input type='text' id='quick_codes "+count+"' /></td>");
				sb.append("<td>"+coveredSelect+"</td>");
				sb.append("<td><input type='button' value='Submit "+name+"' name='"+count+"' id='"+count+"' onclick='SubmitData(this)' /></td>");
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
	
	@POST
	@Path("SaveTriage")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String SaveTriage(@Context HttpServletRequest request,String data) throws JSONException {
		JSONObject obj = new JSONObject(data);
		String ip = request.getRemoteHost();
		InfoDatabase info = new InfoDatabase();
		String callCenter = info.GetCallCenter(obj.getString("agent"));
		info.close();
		TelmedDatabaseClient client = new TelmedDatabaseClient("MT_MARKETING");
		ResultSet set = null;
		try {
			if(!client.login())
				return TelmedResponse.BuildFailedResponse("FAILED TO LOGIN TO DATABASE").toString();
			if(!client.exists("TELMED", null, TelmedColumns.PHONE+" = ?", new String[] {obj.getString(BlueMosiacParameters.PHONE)})) {
				int value = client.insert("TELMED",TelmedColumns.ADD_TO_DATABASE_COLUMNS,TelmedColumns.ConvertJSON(obj, callCenter, ip,"riage Incomplete"));
				if(value==1)
					return TelmedResponse.BuildSuccessfulResponse(obj).toString();
				else 
					return TelmedResponse.BuildFailedResponse(value).toString();
			}
			else {
				int value = client.update("TELMED", new String[] {TelmedColumns.TELMED_STATUS,TelmedColumns.TRIAGE}, new String[] {"Complete",obj.toString()});
				if(value==1)
					return TelmedResponse.BuildSuccessfulResponse(obj).toString();
				else 
					return TelmedResponse.BuildFailedResponse(value).toString();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return TelmedResponse.BuildFailedResponse(e.getMessage(), e.getErrorCode()).toString();
		} catch (JSONException e) {
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
	
	@GET
	@Path("GetTriage")
	@Produces(MediaType.APPLICATION_JSON)
	public String GetTriage(@QueryParam("phone_number") String phone_number) throws JSONException {
		TelmedDatabaseClient client = new TelmedDatabaseClient("MT_MARKETING");
		ResultSet set = null;
		try {
			if(!client.login())
				return TelmedResponse.BuildFailedResponse("FAILED TO LOGIN TO DATABASE").toString();
			set = client.select("TELMED", null,TelmedColumns.PHONE+" = ?", new String[] {phone_number});
			set.next();
			JSONObject obj = new JSONObject(set.getString(TelmedColumns.TRIAGE));
			return TelmedResponse.BuildSuccessfulResponse(obj).toString();
		} catch (SQLException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
			return TelmedResponse.BuildFailedResponse(ex.getMessage(),ex.getErrorCode()).toString();
		} finally {
			try {
				if(set!=null) set.close();
				if(client!=null) client.close();
			} catch(SQLException ex) {
				
			}
		}
		
	}
	
	@POST
	@Path("AddLead")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String AddLead(@Context HttpServletRequest request,String data)  {
		//DatabaseClient client = new DatabaseClient();
		TelmedDatabaseClient client = new TelmedDatabaseClient("MT_MARKETING");
		InfoDatabase info = new InfoDatabase();
		try {
			if(!client.login())
				return TelmedResponse.BuildFailedResponse("FAILED TO LOGIN TO DATABASE").toString();
			JSONObject obj = new JSONObject(data);
			obj.put(BlueMosiacParameters.PECOS_CERTIFIED,"Yes");
			obj.put(BlueMosiacParameters.COPAY_DISCLOSURE, "Yes");
			obj.put(BlueMosiacParameters.RX_TYPE, "Rx");
			obj.put(BlueMosiacParameters.INSURANCE_TYPE, "private_insurance");
			obj.put(BlueMosiacParameters.ACCESS_KEY, "0L9kZohewUGZzxoEV7zD ");
			obj.put(BlueMosiacParameters.LOCATIONS_OF_PAIN, obj.getString("locations_of_pain_GENERAL"));
			String feet = obj.getString(BlueMosiacParameters.HEIGHT_FEET);
			String inches = obj.getString(BlueMosiacParameters.HEIGHT_INCHES);
			obj.put(BlueMosiacParameters.HEIGHT, feet+"'"+inches+"\"");
			if(obj.has("do_you_often_feel_sluggish_lack_energy_or_get_frequent_colds_or_flu_GENERAL")) {
				if(obj.getString("do_you_often_feel_sluggish_lack_energy_or_get_frequent_colds_or_flu_GENERAL").equalsIgnoreCase("YES")) {
					obj.put("are_you_generally_feeling_sluggish_or_lacking_energy_MULTI_VITAMIN", "Yes");
					obj.put("do_you_often_get_sick_with_the_cold_or_flu_MULTI_VITAMIN", "Yes");
				}
			}
			String ip = request.getRemoteHost();
			String callCenter = info.GetCallCenter(obj.getString(BlueMosiacParameters.AGENT));
			if(!client.exists(Tables.TELMED, null, TelmedColumns.PHONE+" = ?", new String[] {obj.getString(BlueMosiacParameters.PHONE)})) {
				int value = client.insert(Tables.TELMED,TelmedColumns.ADD_TO_DATABASE_COLUMNS,TelmedColumns.ConvertJSON(obj, callCenter, ip,"Complete"));
				if(value==1)
					return TelmedResponse.BuildSuccessfulResponse(obj).toString();
				else 
					return TelmedResponse.BuildFailedResponse(value).toString();
			}
			else  {
				ResultSet set = client.select(Tables.TELMED, null, TelmedColumns.PHONE+" = ?", new String[] {obj.getString(BlueMosiacParameters.PHONE)});
				set.next();
				if(set.getString(TelmedColumns.TELMED_STATUS).equalsIgnoreCase("Triage Incomplete")) {
					int value = client.update(Tables.TELMED, new String[] {TelmedColumns.TELMED_STATUS,TelmedColumns.TRIAGE}, new String[] {"Complete",obj.toString()});
					if(value==1)
						return TelmedResponse.BuildSuccessfulResponse(obj).toString();
					else 
						return TelmedResponse.BuildFailedResponse(value).toString();
					
				}
				else {
					return TelmedResponse.BuildFailedResponse(TelmedResponse.Errors.DUPLICATE,obj.getString(TelmedColumns.FIRST_NAME),obj.getString(TelmedColumns.LAST_NAME)).toString();
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return TelmedResponse.BuildFailedResponse(e.getMessage(), e.getErrorCode()).toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return TelmedResponse.BuildFailedResponse(e.getMessage()).toString();
		} finally {
			client.close();
			info.close();
		}
	}
}
