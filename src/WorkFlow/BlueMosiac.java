package WorkFlow;

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

import TelmedResponse.TelmedResponse;
import client.BlueMosiacClient;
import client.DatabaseClient;
import client.InfoDatabase;

@Path("BlueMosiac")
public class BlueMosiac {

	
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
		DatabaseClient client = new DatabaseClient();
		try {
			if(!client.CheckTelmedDuplicate(obj.getString("phone_number"))) {
				return client.AddToTelmedBlueMosiac(obj,"",ip,callCenter,"Triage Incomplete").toString();
			}
			else {
				if(client.IsIncompleteTriage(obj.getString("phone_number"))) {
					return client.UpdateTriage(obj.getString("phone_number"),obj,"Triage Incomplete","").toString();
				}
				else 
					return client.GetDuplicateResponse(obj.getString("phone_number")).toString();
			}
		} catch(JSONException ex) {
			return TelmedResponse.BuildFailedResponse(ex.getMessage()).toString();
		} finally {
			client.close();
		}
		
	}
	
	@GET
	@Path("GetTriage")
	@Produces(MediaType.APPLICATION_JSON)
	public String GetTriage(@QueryParam("phone_number") String phone_number) throws JSONException {
		DatabaseClient client = new DatabaseClient();
		JSONObject obj = client.GetTriage(phone_number);
		client.close();
		return obj.toString();
	}
	
	@POST
	@Path("AddLead")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String AddLead(@Context HttpServletRequest request,String data)  {
		DatabaseClient client = new DatabaseClient();
		InfoDatabase info = new InfoDatabase();
		try {
			JSONObject obj = new JSONObject(data);
			obj.put("is_pecos_certified","Yes");
			obj.put("co_pay_disclosure_CLOSING_STATEMENT", "Yes");
			obj.put("rx_type", "Rx");
			obj.put("insurance_type","private_insurance");
			obj.put("access_key", "0L9kZohewUGZzxoEV7zD ");
			obj.put("locations_of_pain_PAIN", obj.getString("locations_of_pain_GENERAL"));
			String feet = obj.getString("height_feet");
			String inches = obj.getString("height_inches");
			obj.put("what_is_your_height_GENERAL", feet+"'"+inches+"\"");
			if(obj.has("do_you_often_feel_sluggish_lack_energy_or_get_frequent_colds_or_flu_GENERAL")) {
				if(obj.getString("do_you_often_feel_sluggish_lack_energy_or_get_frequent_colds_or_flu_GENERAL").equalsIgnoreCase("YES")) {
					obj.put("are_you_generally_feeling_sluggish_or_lacking_energy_MULTI_VITAMIN", "Yes");
					obj.put("do_you_often_get_sick_with_the_cold_or_flu_MULTI_VITAMIN", "Yes");
				}
			}
			String ip = request.getRemoteHost();
			String callCenter = info.GetCallCenter(obj.getString("agent"));
			JSONObject response = null;
			if(!client.CheckTelmedDuplicate(obj.getString("phone_number"))) {
				response = BlueMosiacClient.AddToBlueMosiac(obj);
				return client.AddToTelmedBlueMosiac(obj,response.getString(TelmedResponse.RECORD_ID),ip,callCenter,"Complete").toString();
				
			}
			else  {
				if(client.IsIncompleteTriage(obj.getString("phone_number"))) {
					response = BlueMosiacClient.AddToBlueMosiac(obj);
					client.UpdateTriage(obj.getString("phone_number"),obj,"Complete",response.getString(TelmedResponse.RECORD_ID));
					return TelmedResponse.BuildSuccessfulResponse(obj).toString();
				}
				else
					return client.GetDuplicateResponse(obj.getString("phone_number")).toString();
			}
		} catch(JSONException ex) {
			return ex.toString();
		} finally {
			client.close();
			info.close();
		}
	}
	
}
