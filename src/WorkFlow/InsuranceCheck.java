package WorkFlow;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import PBM.InsuranceFilter;
import client.Record;

@Path("VerifyInsurance")
public class InsuranceCheck {

	@GET
	@Path("Check")
	@Produces(MediaType.TEXT_PLAIN)
	public String CheckInsuranceType(
			@QueryParam("policyId") String policyId,
			@QueryParam("bin") String bin,
			@QueryParam("grp") String grp,
			@QueryParam("pcn") String pcn) {
		Record record = new Record();
		record.setPolicyId(policyId);
		record.setBin(bin);
		record.setGrp(grp);
		record.setPcn(pcn);
		return InsuranceFilter.Filter(record);
	}
	
}
