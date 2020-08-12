package PBM;

import client.Record;

public class Medimpact {
	public static String Filter(Record record) {
		if(record.getBin().equalsIgnoreCase("015574"))
			return Filter015574(record);
		switch(record.getPcn()) {
			default:
				return FilterByGroup(record);
		}
	}
	private static String FilterByGroup(Record record) {
		switch(record.getGrp()) {
			case "MDW":
			case "DHM02":
			case "UOA01":
			case "GHS30":
			case "103774":
			case "10016720":
			case "CCA01":
				return InsuranceType.MEDICAID;
			case "10008217":
				return InsuranceType.PRIVATE_UNKNOWN;
			case "AHS":
			case "OGB":
			case "10009467":
			case "EBD02":
			case "0046":
			case "0057":
			case "036894":
			case "05BCBS":
			case "10012241":
			case "206200":
			case "23025":
			case "23049":
			case "28914":
			case "35000":
			case "3502":
			case "35487":
			case "40BCBS":
			case "41BCBS":
			case "64001":
			case "76411309":
			case "900003":
			case "901127":
			case "901745":
			case "902207":
			case "902354":
			case "904191":
			case "904236":
			case "ACH01":
			case "AUH01":
			case "BAN01":
			case "BHE01":
			case "GHS01":
			case "JWN01":
			case "MNA":
			case "NOR01":
			case "OHS":
			case "PHE01":
			case "PHE03":
			case "PHI11":
			case "STD":
			case "U01":
			case "U05":
			case "U26":
			case "U40":
			case "UTY03":
			case "WHR01":
			case "29324":
			case "JEF03":
			case "35919":
			case "MFP01":
			case "ACH02":
			case "140":
			case "ML118":
			case "ML142":
			case "24002":
			case "SEC03":
			case "74498":
			case "HTR01":
			case "08BCBS":
			case "57":
				return InsuranceType.PRIVATE_VERIFIED;
			case "19020":
			case "903486":
			case "ICAREPRIME":
				return InsuranceType.OUT_OF_NETWORK;
			case "GHS90":
				return InsuranceType.NOT_COVERED;
			default:
				return InsuranceType.PRIVATE_UNKNOWN;
		}
	}
	private static String Filter015574(Record record) {
		switch(record.getGrp()) {
			default:
				return InsuranceType.MEDICARE_TELMED;
		}
	}
}
