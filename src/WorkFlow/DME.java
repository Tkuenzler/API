package WorkFlow;

import java.sql.SQLException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.json.JSONException;
import Database.Columns.DMEColumns;
import Database.Tables.Tables;
import ResponseBuilder.DoctorChaseReponse;
import client.Database;

@Path("DME")
public class DME {
	@GET
 	@Path("Clear")
 	@Produces(MediaType.APPLICATION_JSON)
	public String Clear(@QueryParam("id") String id,
			@QueryParam("agent") String agent,
			@QueryParam("database") String database) throws JSONException {
		Database client = new Database(database);
		try {
			if(!client.login())
				return DoctorChaseReponse.BuildFailedResponse("Failed to login").toString();
			int used = client.update(Tables.DME, new String[] {DMEColumns.USED},new String[] {"0"}, DMEColumns.ID+" = '"+id+"'");
			switch(used) {
				case 0:
					return DoctorChaseReponse.BuildFailedResponse("RECORD NOT FOUND").toString();
				case 1:
					return DoctorChaseReponse.BuildSuccesfulResponse("RECORD CLEARED").toString();
				case -1:
					return DoctorChaseReponse.BuildFailedResponse("ERROR").toString();
				default: 
					return DoctorChaseReponse.BuildFailedResponse("UNEXPECTED ERROR").toString();
			}
		} catch(JSONException ex) {
			return DoctorChaseReponse.BuildFailedResponse(ex.getMessage()).toString();
		} catch (SQLException ex) {
			// TODO Auto-generated catch block
			return DoctorChaseReponse.BuildFailedResponse(ex.getMessage()).toString();
		} finally {
			if(client!=null) client.close();
		}
	}
	
	
}
