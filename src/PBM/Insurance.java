package PBM;

public class Insurance {
	public static final String TYPE = "TYPE";
	public static final String LOCATION = "LOCATION";
	public static final String BIN = "BIB";
	public static final String GROUP = "GROUP";
	public static final String PCN = "PCN";
	public static final String CARRIER = "CARRIER";
	public static final String POLICY_ID = "POLICY_ID";
	String bin,pcn,grp,type,location;
	String policyId,info,carrier,planType,status,contractNumber;
	public Insurance() {
		
	}
	public Insurance(String type) {
		this.type = type;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getBin() {
		return bin;
	}
	public void setBin(String bin) {
		this.bin = bin;
	}
	public String getPcn() {
		return pcn;
	}
	public void setPcn(String pcn) {
		this.pcn = pcn;
	}
	public String getGrp() {
		return grp;
	}
	public void setGrp(String grp) {
		this.grp = grp;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getPolicyId() {
		return policyId;
	}
	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getCarrier() {
		return carrier;
	}
	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}
	public String getPlanType() {
		return planType;
	}
	public void setPlanType(String planType) {
		this.planType = planType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getContractNumber() {
		return contractNumber;
	}
	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}
}
