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
