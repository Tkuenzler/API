
package WorkFlow;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("rest")
public class ApplicationConfig extends ResourceConfig {
	public ApplicationConfig() {
	    //register(MultiPartFeature.class);
		register(WorkFlow.Cascade.class);
	    register(WorkFlow.Verify.class);
	    register(WorkFlow.DoctorChase.class);
	    register(WorkFlow.RDSTelmed.class);
	    register(WorkFlow.Telmed.class);
	    register(WorkFlow.AddRecord.class);
	    register(WorkFlow.Report.class);
	    register(WorkFlow.Chase.class);
    }
}