package WorkFlow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import client.DMEClient;
import client.DatabaseClient;
import client.Record;

@Path("Telmed")
public class Telmed {

	@GET
	@Path("GetPayableLeadsRange")
	@Produces(MediaType.TEXT_PLAIN)
	public String GetPayableLeadsRange(
		@QueryParam("start") String start,
		@QueryParam("end") String end,
		@QueryParam("complete") String complete,
		@QueryParam("agent") String agent) throws SQLException {
		DatabaseClient client = new DatabaseClient("MT_MARKETING");
		String value = null;
		if(complete==null)
			value = client.GetPayableLeadsRange(start, end,agent);
		else if(Boolean.parseBoolean(complete))
			value = client.GetPayableLeadsRange(start, end,agent);
		else
			value = client.GetNonPayableLeadsRange(start, end,agent);
		client.close();
		return value;
	}
	@GET
	@Path("GetPayableLeadsDate")
	@Produces(MediaType.TEXT_PLAIN)
	public String GetPayableLeadsDate(
		@QueryParam("start") String start,
		@QueryParam("complete")String complete) throws SQLException {
		DatabaseClient client = new DatabaseClient("MT_MARKETING");
		String value = null;
		if(complete==null)
			value = client.GetPayableLeadsDate(start);
		else if(Boolean.parseBoolean(complete))
			value = client.GetPayableLeadsDate(start);
		else 
			value = client.GetNonPayableLeadsDate(start);
		client.close();
		return value;
	}
	
	@GET
	@Path("GetDailyInfo")
	@Produces(MediaType.TEXT_PLAIN)
	public String GetPayableLeads(@QueryParam("date") String date) throws SQLException {
		DatabaseClient client = new DatabaseClient("MT_MARKETING");
		String value = client.GetTodaysLeads(date);
		client.close();
		return value;
		
	}
	
	@GET
	@Path("GetFailed")
	@Produces(MediaType.APPLICATION_JSON)
	public String GetFailedLeads(@QueryParam("phone") String phone,
								@QueryParam("disposition") String disposition) {
		DatabaseClient client = new DatabaseClient("MT_MARKETING");
		int update = UpdateDisposition(phone,disposition,client);
		Record record = client.LoadFailedTelmed();
		client.close();
		if(record==null)
			return new Record().returnTelmedJSON(false,-3,"No Records Available");
		else
			return record.returnTelmedJSON(true,update,"none");
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
	@Path("Duplicate")
	@Produces(MediaType.TEXT_PLAIN)
	public String CheckDuplicate(@QueryParam("phone") String phone) throws SQLException {
		DatabaseClient client = new DatabaseClient("MT_MARKETING");
		boolean duplicate = client.CheckTelmedDuplicate(phone);
		client.close();
		if(!duplicate)
			return "Unique";
		else
			return "Duplicate";
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
	
	private void loadFile(InputStream  input) {
		String name = "test.pdf";
		OutputStream out = null;
		try {
			int read = 0;
			byte[] bytes = new byte[1024];
			
			out = new FileOutputStream(new File(name));
			while((read=input.read(bytes))!= -1) 
				out.write(bytes, 0, read);
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(out!=null) {
					out.flush();
					out.close();
				} 
			} catch(IOException e) {
				
			}
		}
	}
	private int UpdateDisposition(String phone,String disposition,DatabaseClient client) {
		if(phone.equalsIgnoreCase(""))
			return -3;
		else {
			return client.UpdateFailedTelmedDisposition(phone, disposition);
		}
	}
}
