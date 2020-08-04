package PBM;

public class InsuranceType {
	public class Type {
		public static final int MEDICAID_INSURANCE = -1;
		public static final int MEDICARE_INSURANCE = 1;
		public static final int PRIVATE_INSURANCE = 2;
	}
	public static final String PRIVATE_VERIFIED = "VALIDATED / VERIFIED";
	public static final String PRIVATE_UNKNOWN = "VALIDATED / UNKNOWN";
	public static final String PRIVATE_NO_TELMED = "VALIDATED / NO TELMED";
	public static final String PRIVATE_NOTVALIDATED = "INVALID / VERIFIED";

	//DONT BUYS
	public static final String NOT_COVERED = "NOT COVERED";
	public static final String OUT_OF_NETWORK = "OUT OF NETWORK";
	public static final String MOLINA = "MOLINA MEDICAID";
	public static final String MEDICAID = "MEDICAID";
	public static final String NO_COVERAGE = "NO COVERAGE";
	public static final String TRICARE = "TRICARE";
	
	
	//public static final String MEDICARE_PART_D = "MEDICARE PART D";
	public static final String MEDICARE_COMMERCIAL = "MEDICARE COMMERCIAL";
	public static final String PDP = "PDP";
	public static final String MAPD = "MAPD";
	public static final String MAPD_PPO = "MAPD PPO";
	public static final String MAPD_HMO = "MAPD HMO";
	public static final String MEDICARE_TELMED = "VALIDATED / TELMED";
	public static final String UNKNOWN_PBM = "UNKNOWN PBM";
	public static final String MEDICAID_MEDICARE = "MEDICAID/MEDICARE";
	
	public static final String[] MEDICARES = {MEDICARE_COMMERCIAL,PDP,MAPD,MAPD_PPO,MAPD_HMO,MEDICARE_TELMED,UNKNOWN_PBM,MEDICAID_MEDICARE};
	public static final String[] PRIVATES = {PRIVATE_VERIFIED,PRIVATE_UNKNOWN,PRIVATE_NO_TELMED,PRIVATE_NOTVALIDATED};
	public static final String[] MEDICAIDS = {MEDICAID,NOT_COVERED,OUT_OF_NETWORK,MOLINA,NO_COVERAGE,TRICARE};
	
	public static boolean IsPrivate(String type) {
		for(String s: PRIVATES) {
			if(type.equalsIgnoreCase(s))
				return true;
		}
		return false;
	}
	public static boolean IsMedicare(String type) {
		for(String s: MEDICARES) {
			if(type.equalsIgnoreCase(s))
				return true;
		}
		return false;
	}
	public static boolean IsMedicaid(String type) {
		for(String s: MEDICAIDS) {
			if(type.equalsIgnoreCase(s))
				return true;
		}
		return false;
	}
}
