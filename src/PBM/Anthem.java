package PBM;

import client.Record;

public class Anthem {
	public static String Filter(Record record) {
		switch(record.getPcn()) {
		case "WG":
		case "AC":
		case "FC":
			return InsuranceType.PRIVATE_NO_TELMED;
		case "IS":
			return InsuranceType.MEDICARE_COMMERCIAL;
		default:
			return InsuranceType.PRIVATE_UNKNOWN;
		}	
	}
}
