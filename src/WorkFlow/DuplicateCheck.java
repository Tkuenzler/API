package WorkFlow;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import client.DatabaseClient;

@Path("CheckDupe")
public class DuplicateCheck {

	@GET
	@Path("Telmed")
	@Produces(MediaType.TEXT_PLAIN)
	public String CheckDuplicateTelmed(@QueryParam("phone") String phone) {
		if(phone.length()!=10)
			return "NOT VALID PHONE NUMBER";
		DatabaseClient client = new DatabaseClient("MT_MARKETING");
		boolean duplicate = client.CheckTelmedDuplicate(phone);
		client.close();
		if(!duplicate)
			return "ACCEPTED";
		else
			return "DENIED";
	}
	@GET
	@Path("Leads")
	@Produces(MediaType.TEXT_PLAIN)
	public String CheckDuplicateLead(@QueryParam("phone") String phone) {
		if(phone.length()!=10)
			return "NOT VALID PHONE NUMBER";
		DatabaseClient client = new DatabaseClient("MT_MARKETING");
		boolean dupe = client.IsInDatabase(phone);
		client.close();
		if(dupe)
			return "DENIED";
		else
			return "ACCEPTED";
	}
}
