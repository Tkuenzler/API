package ResponseBuilder;

import client.Record;

public class DMEResponse {
	public static String SucessfulResponse(Record record) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table style='border: 1px solid black;'>");
		sb.append("<tr bgcolor='#d3d3d3' style='border: 1px solid black;'>");
		sb.append("<td>Status</td>");
		sb.append("<td>Name</td>");
		sb.append("<td>Phone</td>");
		sb.append("<td>State</td>");
		sb.append("</tr>");
		sb.append("<tr style='border: 1px solid black;'>");
		sb.append("<td>Successful</td>");
		sb.append("<td>"+record.getFirstName()+" "+record.getLastName()+"</td>");
		sb.append("<td>"+record.getPhone()+"</td>");
		sb.append("<td>"+record.getState()+"</td>");
		sb.append("</tr>");
		sb.append("</table>");
		return sb.toString();
	}
	public static String BuildErrorResponse(int error_code,String error) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table style='border: 1px solid black;'>");
		sb.append("<tr bgcolor='#d3d3d3' style='border: 1px solid black;'>");
		sb.append("<td>Status</td>");
		sb.append("<td>Error Code</td>");
		sb.append("<td>Message</td>");
		sb.append("</tr>");
		sb.append("<tr style='border: 1px solid black;'>");
		sb.append("<td>Failed</td>");
		sb.append("<td>"+error_code+"</td>");
		sb.append("<td>"+error+"</td>");
		sb.append("</tr>");
		sb.append("</table>");
		return sb.toString();
	}
	public static String BuildDuplicateResponse(Record record) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table style='border: 1px solid black;'>");
		sb.append("<tr bgcolor='#d3d3d3' style='border: 1px solid black;'>");
		sb.append("<td>Status</td>");
		sb.append("<td>Name</td>");
		sb.append("<td>Phone</td>");
		sb.append("</tr>");
		sb.append("<tr style='border: 1px solid black;'>");
		sb.append("<td>Duplicate</td>");
		sb.append("<td>"+record.getFirstName()+" "+record.getLastName()+"</td>");
		sb.append("<td>"+record.getPhone()+"</td>");
		sb.append("</tr>");
		sb.append("</table>");
		return sb.toString();
	}
}
