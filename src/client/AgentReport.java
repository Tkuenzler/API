package client;

public class AgentReport {
	String name;
	int leadCount;
	//Fax disoosition count
	int approved,denied,ntbs,needPcp,wrongDoctor,wrongFax,deceased,blank,notIntrested,escribe;
	int topicalScriptApproval,oralScriptApproval;
	int telmed,pendingTelmed,failedTelmed,dmeTelmed;
	public AgentReport(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getDMETelmed() {
		return dmeTelmed;
	}
	public void setDMETelmed(int dmeTelmed) {
		this.dmeTelmed = dmeTelmed;
	}
	
	public int getTelmed() {
		return telmed;
	}
	public void setTelmed(int telmed) {
		this.telmed = telmed;
	}
	
	public int getPendingTelmed() {
		return pendingTelmed;
	}
	public void setPendingTelmed(int pendingTelmed) {
		this.pendingTelmed = pendingTelmed;
	}
	public void setFailedTelmed(int failedTelmed) {
		this.failedTelmed = failedTelmed;
	}
	public int getFailedTelmed() {
		return this.failedTelmed;
	}
	public void setLeadCount(int leadCount) {
		this.leadCount = leadCount;
	}
	public void setApproved(int approved) {
		this.approved = approved;
	}
	public void setDenied(int denied) {
		this.denied = denied;
	}
	public void setNtbs(int ntbs) {
		this.ntbs = ntbs;
	}
	public void setNeedPcp(int needPcp) {
		this.needPcp = needPcp;
	}
	public void setWrongDoctor(int wrongDoctor) {
		this.wrongDoctor = wrongDoctor;
	}
	public void setWrongFax(int wrongFax) {
		this.wrongFax = wrongFax;
	}
	public void setDeceased(int deceased) {
		this.deceased = deceased;
	}
	public void setBlank(int blank) {
		this.blank = blank;
	}
	public int getLeadCount() {
		return leadCount;
	}
	public void incrementLeadCount() {
		this.leadCount++;
	}
	public void incrementApproved() {
		this.approved++;
	}
	public void incrementDenied() {
		this.denied++;
	}
	public void incrementNeedsToBeSeen() {
		this.ntbs++;
	}
	public void incrementNeedPcp() {
		this.needPcp++;
	}
	public void incrementWrongDoctor() {
		this.wrongDoctor++;
	}
	public void incrementDeceased() {
		this.deceased++;
	}
	public void incrementBlank() {
		this.blank++;
	}
	public void incrementWrongFax() {
		this.wrongFax++;
	}
	public void incrementNotInterested() {
		this.notIntrested++;
	}
	public void incrementEscribe() {
		this.escribe++;
	}
	public void incrementTopicalScriptApproval() {
		this.topicalScriptApproval++;
	}
	public void setTopicalScriptApproval(int topicalScriptApproval) {
		this.topicalScriptApproval = topicalScriptApproval;
	}
	public void incrementOralScriptApproval() {
		this.oralScriptApproval++;
	}
	public void setOralScriptApproval(int oralScriptApproval) {
		this.oralScriptApproval = oralScriptApproval;
	}
	public int getApproved() {
		return approved;
	}
	public int getDenied() {
		return denied;
	}
	public int getNtbs() {
		return ntbs;
	}
	public int getNeedPcp() {
		return needPcp;
	}
	public int getWrongDoctor() {
		return wrongDoctor;
	}
	public int getWrongFax() {
		return wrongFax;
	}
	public int getDeceased() {
		return deceased;
	}
	public int getBlank() {
		return blank;
	}
	public int getNotIntrested() {
		return notIntrested;
	}
	public void setNotInterested(int notInterested) {
		this.notIntrested = notInterested;
	}
	public int getEscribe() {
		return escribe;
	}
	public void setEscribe(int escribe) {
		this.escribe = escribe;
	}
	public int getTopicalScriptApproval() {
		return topicalScriptApproval;
	}
	public int getOralScriptApproval() {
		return oralScriptApproval;
	}
}
