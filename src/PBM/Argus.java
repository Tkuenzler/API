package PBM;

import client.Record;

public class Argus {
	public static String Filter(Record record) {
		switch(record.getPcn()) {
			case "02150000":
			case "05180000":
			case "05190000":
			case "06840000":
			case "06810000":
			case "01910000":
				return InsuranceType.PRIVATE_VERIFIED;
			case "CIHSCARE":
				return InsuranceType.MEDICARE_COMMERCIAL;
			case "CIMCARE":
				return InsuranceType.MEDICARE_TELMED;
			case "07630000":
				return InsuranceType.MEDICARE_COMMERCIAL;
			case "01420000":
				return InsuranceType.PRIVATE_UNKNOWN;
			case "06180000":
			case "06210000":
			case "02530000":
			case "01940000":
			case "07640000":
			case "CIHSCAID":
				return InsuranceType.MEDICAID;
			default:
				return InsuranceType.UNKNOWN_PBM;
		}
	}
}
