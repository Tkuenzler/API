package doctor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser {
	
	public static Doctor CreateDoctor(String line) {
		Doctor d = null;
		try {
			JSONObject obj = new JSONObject(line);
			if(obj.has("result_count")) 
				if (obj.getInt("result_count")==0)	
					return null;
			if(obj.has("Errors"))
				return null;
			JSONObject results = obj.getJSONArray("results").getJSONObject(0);
			
			JSONArray taxonomies = results.getJSONArray("taxonomies");
			JSONObject nameInfo = results.getJSONObject("basic");
			JSONArray addresses = results.getJSONArray("addresses");
			JSONObject address1  = addresses.getJSONObject(0);
		    d = new Doctor(Integer.toString(results.getInt("number")));
		    d.setEnumeration_type(results.getString("enumeration_type"));
		    String[] stateLicensed = new String[taxonomies.length()];
		    String[] type = new String[taxonomies.length()];
		    String[] code = new String[taxonomies.length()];
		    for(int i = 0;i<taxonomies.length();i++) {
		    	type[i] = taxonomies.getJSONObject(i).getString("desc");
		    	stateLicensed[i] = taxonomies.getJSONObject(i).getString("state");
		    	code[i] = taxonomies.getJSONObject(i).getString("code");
		    }
		    d.setStatesLicensed(stateLicensed);
		    d.setType(type);
		    d.setCode(code);
		    if(nameInfo.has("first_name")) {
				d.setFirstName(nameInfo.getString("first_name"));
				d.setLastName(nameInfo.getString("last_name"));;
				d.setGender(nameInfo.getString("gender"));
		    } 
		    else {
		    	d.setFirstName(nameInfo.getString("name"));
		    }
			if(address1.has("address_1"))
				d.setPracticeAddress1(address1.getString("address_1"));
			else
				d.setPracticeAddress1("");
			if(address1.has("address_2"))
				d.setPracticeAddress2(address1.getString("address_2"));
			else 
				d.setPracticeAddress2("");
			if(address1.has("city"))
				d.setPracticeCity(address1.getString("city"));
			else
				d.setPracticeCity("");
			if(address1.has("state"))
				d.setPracticeState(address1.getString("state"));
			else 
				d.setPracticeState("");
			if(address1.has("postal_code"))
				d.setPracticeZipeCode(convertZip(address1.getString("postal_code")));
			else 
				d.setPracticeZipeCode("");
			if(address1.has("telephone_number"))
				d.setPracticePhone(address1.getString("telephone_number"));
			else 
				d.setPracticePhone("");
			if(address1.has("fax_number"))
				d.setPracticeFax(address1.getString("fax_number"));
			else
				d.setPracticeFax("");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		return d;
	}
	private static String convertZip(String zip) {
		return zip.substring(0, 5);
	}
}
