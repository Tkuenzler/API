package ResponseBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import PBM.InsuranceFilter;
import client.Record;

public class AddRecordResponse {
	public static final String SUCCESS = "success";
	public static final String ERROR = "error";
	public static final String ERROR_CODE = "error_code";
	public static final String DATE_ADDED = "date";
	public static final String FIRST = "first_name";
	public static final String LAST = "last_name";
	public static final String RECORD_ID = "id";
	public static final String AGENT = "agent";
	public static final String PHARMACY = "pharmacy";
	public static final String PRODUCTS = "products";
	
	public static String BuildSuccesfulResponse(Record record,boolean telmed) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table style='border: 1px solid black;'>");
		sb.append("<tr bgcolor='#d3d3d3' style='border: 1px solid black;'>");
		sb.append("<td>Status</td>");
		sb.append("<td>Can Telmed</td>");
		sb.append("<td>Name</td>");
		sb.append("<td>State</td>");
		sb.append("<td>Phone</td>");
		sb.append("<td>Pharmacy</td>");
		sb.append("<td>Emdoen Status</td>");
		sb.append("<td>Carrier</td>");
		sb.append("<td>Bin</td>");
		sb.append("<td>Products</td>");
		sb.append("</tr>");
		sb.append("<tr style='border: 1px solid black;'>");
		sb.append("<td>Successful</td>");
		sb.append("<td>"+Boolean.toString(telmed).toUpperCase()+"</td>");
		sb.append("<td>"+record.getFirstName()+" "+record.getLastName()+"</td>");
		sb.append("<td>"+record.getState()+"</td>");
		sb.append("<td>"+record.getPhone()+"</td>");
		sb.append("<td>"+record.getPharmacy()+"</td>");
		sb.append("<td>"+record.getStatus()+"</td>");
		sb.append("<td>"+record.getCarrier()+"</td>");
		sb.append("<td>"+record.getBin()+"</td>");
		sb.append("<td>"+record.getProductsAsString()+"</td>");
		sb.append("</tr>");
		sb.append("</table>");
		return sb.toString();
	}
	public static String BuildSuccesfulUpdateResponse(Record record,boolean telmed) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table style='border: 1px solid black;'>");
		sb.append("<tr bgcolor='#d3d3d3' style='border: 1px solid black;'>");
		sb.append("<td>Status</td>");
		sb.append("<td>Can Telmed</td>");
		sb.append("<td>Name</td>");
		sb.append("<td>Phone</td>");
		sb.append("<td>Pharmacy</td>");
		sb.append("</tr>");
		sb.append("<tr style='border: 1px solid black;'>");
		sb.append("<td>Successfully Updated</td>");
		sb.append("<td>"+Boolean.toString(telmed).toUpperCase()+"</td>");
		sb.append("<td>"+record.getFirstName()+" "+record.getLastName()+"</td>");
		sb.append("<td>"+record.getPhone()+"</td>");
		sb.append("<td>"+record.getPharmacy()+"</td>");
		sb.append("</tr>");
		sb.append("</table>");
		return sb.toString();
	}
	public static String BuildDuplicateResponse(Record record,String agent,String date_added) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table style='border: 1px solid black;'>");
		sb.append("<tr bgcolor='#d3d3d3' style='border: 1px solid black;'>");
		sb.append("<td>Status</td>");
		sb.append("<td>Name</td>");
		sb.append("<td>Phone</td>");
		sb.append("<td>Pharmacy</td>");
		sb.append("<td>Agent</td>");
		sb.append("<td>Date Added</td>");
		sb.append("</tr>");
		sb.append("<tr style='border: 1px solid black;'>");
		sb.append("<td>Duplicate</td>");
		sb.append("<td>"+record.getFirstName()+" "+record.getLastName()+"</td>");
		sb.append("<td>"+record.getPhone()+"</td>");
		sb.append("<td>"+record.getPharmacy()+"</td>");
		sb.append("<td>"+agent+"</td>");
		sb.append("<td>"+date_added+"</td>");
		sb.append("</tr>");
		sb.append("</table>");
		return sb.toString();
	}
	public static String BuildErrorResponse(int error) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table style='border: 1px solid black;'>");
		sb.append("<tr bgcolor='#d3d3d3' style='border: 1px solid black;'>");
		sb.append("<td>Status</td>");
		sb.append("<td>Error Code</td>");
		sb.append("</tr>");
		sb.append("<tr style='border: 1px solid black;'>");
		sb.append("<td>Failed</td>");
		sb.append("<td>"+error+"</td>");
		sb.append("</tr>");
		sb.append("</table>");
		return sb.toString();
	}
	public static String BuildErrorResponse(String error) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table style='border: 1px solid black;'>");
		sb.append("<tr bgcolor='#d3d3d3' style='border: 1px solid black;'>");
		sb.append("<td>Status</td>");
		sb.append("<td>Error Code</td>");
		sb.append("</tr>");
		sb.append("<tr style='border: 1px solid black;'>");
		sb.append("<td>Failed</td>");
		sb.append("<td>"+error+"</td>");
		sb.append("</tr>");
		sb.append("</table>");
		return sb.toString();
	}
	public static String BuildErrorResponse(JSONObject obj) throws JSONException {
		StringBuilder sb = new StringBuilder();
		sb.append("<table style='border: 1px solid black;'>");
		sb.append("<tr bgcolor='#d3d3d3' style='border: 1px solid black;'>");
		sb.append("<td>Status</td>");
		sb.append("<td>Error Code</td>");
		sb.append("<td>Message</td>");
		sb.append("</tr>");
		sb.append("<tr style='border: 1px solid black;'>");
		sb.append("<td>Failed</td>");
		sb.append("<td>"+obj.getInt("value")+"</td>");
		sb.append("<td>"+obj.getString("message")+"</td>");
		sb.append("</tr>");
		sb.append("</table>");
		return sb.toString();
	}
	
	
	
}
