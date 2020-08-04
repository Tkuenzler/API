package client;

public class InsuranceInfo {
	public String status;
	public Insurance privatePrimary;
	public Insurance medicarePrimary;
	public boolean isCommercial,isMedicare;
	String ssn;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setInsuranceType() {
		if(getStatus()==null) 
			return;
		if(!getStatus().equalsIgnoreCase("FOUND"))
			return;
		if(medicarePrimary==null && privatePrimary!=null) {
			isCommercial = true;
			isMedicare = false;
		}
		else if (medicarePrimary!=null) {
			isCommercial = false;
			isMedicare = true;
		}
		else {
			isCommercial = false;
			isMedicare = false;
		}
	}
	public boolean isCommercial() {
		return isCommercial;
	}
	public boolean isMedicare() {
		return isMedicare;
	}
}
