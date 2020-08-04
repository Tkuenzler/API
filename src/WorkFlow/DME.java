package WorkFlow;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import client.DMEClient;
import client.Record;

@Path("DME")
public class DME {
	@Path("GetLeads")
	@GET()
	@Produces(MediaType.TEXT_HTML)
	public String GetDME(@QueryParam("status") boolean complete) {
		DMEClient client = new DMEClient("MT_MARKETING");
		StringBuilder sb = new StringBuilder();
		sb.append("<table border='1' id='records' name='records' >");
		sb.append("<tr bgcolor='#d3d3d3'>");
		sb.append("<td>Name</td>");
		sb.append("<td>DOB</td>");
		sb.append("<td>Last 4 ssn</td>");
		sb.append("<td>Phone</td>");
		sb.append("<td>Address</td>");
		sb.append("<td>City</td>");
		sb.append("<td>State</td>");
		sb.append("<td>Zip</td>");
		sb.append("<td>Plan Type</td>");
		sb.append("<td>Carrier</td>");
		sb.append("<td>Policy Id</td>");
		sb.append("<td>Brace List</td>");
		sb.append("<td>Approved?</td>");
		sb.append("<td>Submit</td>");
		sb.append("</tr>");
		int count = 1;
		List<Record> list = client.GetUnverifiedRecords();
		for(Record record: list) {
			sb.append("<tr name='"+count+"' id='"+count+"'>");
			sb.append("<td>"+record.getFirstName()+" "+record.getLastName()+"</td>");
			sb.append("<td>"+record.getDob()+"</td>");
			sb.append("<td>"+record.getSsn()+"</td>");
			sb.append("<td>"+record.getPhone()+"</td>");
			sb.append("<td>"+record.getAddress()+"</td>");
			sb.append("<td>"+record.getCity()+"</td>");
			sb.append("<td>"+record.getState()+"</td>");
			sb.append("<td>"+record.getZip()+"</td>");
			sb.append("<td>"+record.getPlanType()+"</td>");
			sb.append("<td>"+record.getCarrier()+"</td>");
			sb.append("<td>"+record.getPolicyId()+"</td>");
			sb.append("<td>"+record.getBraceList()+"</td>");
			sb.append("<td>"+createSelect(count)+"</td>");
			sb.append("<td>"+getButton(count,record.getFirstName()+" "+record.getLastName())+"</td>");
			sb.append("</tr>");
			count++;
		}
		sb.append("</table>");
		return sb.toString();
	}
	private String createSelect(int count) {
		StringBuilder sb = new StringBuilder();
		sb.append("<select id='status"+count+"'>");
		sb.append("<option value=''></option>");
		sb.append("<option value='Passed SnS'>Passed SnS</option>");
		sb.append("<option value='Failed SnS'>Failed SnS</option>");
		sb.append("<option value='HMO'>HMO</option>");
		sb.append("<option value='Not Covered'>Not Covered</option>");
		sb.append("<option value='Copay Too High'>Copay Too High</option>");
		sb.append("<option value='Medicare Primary'>Medicare Primary</option>");
		sb.append("<option value='Prior Authorization Required'>Prior Authorization Required</option>");
		sb.append("</select>");
		return sb.toString();
	}
	private String getButton(int count,String name) {
		StringBuilder sb = new StringBuilder();
		sb.append("<input type='button' id='"+count+"' name='"+count+"' value='SUBMIT "+name+" ' onclick='AddLead(this)'/>");
		return sb.toString();
	}
}
