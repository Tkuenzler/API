package Pharmacy;

public class PharmacyOdds {
	String name;
	double extra,odds;
	public PharmacyOdds(String name,double extra) {
		this.name = name;
		this.extra = extra;
		this.setOdds(extra);
	}
	public String getName() {
		return name;
	}
	public void setOdds(double odds) {
		if(this.extra==0)
			this.odds = odds;
		else
			this.odds = extra/odds;
	}
	public double getOdds() {
		return odds;
	}
	public double getExtra() {
		return extra;
	}
}
