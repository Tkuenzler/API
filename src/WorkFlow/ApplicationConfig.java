
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
	    register(WorkFlow.DME.class);
	    register(WorkFlow.BlueMosiac.class);
    }
	/*
	 * public Set<Class<?>> getClasses() {
        Set<Class<?>> s = new HashSet<Class<?>>();
        s.add(WorkFlow.Cascade.class);
        s.add(WorkFlow.Verify.class);
        s.add(WorkFlow.DoctorChase.class);
        s.add(WorkFlow.RDSTelmed.class);
        s.add(WorkFlow.InsuranceCheck.class);
        s.add(WorkFlow.Medcore.class);
        s.add(WorkFlow.Telmed.class);
        s.add(WorkFlow.DuplicateCheck.class);
        s.add(WorkFlow.AddRecord.class);
        return s;
        */
}

//extends Application