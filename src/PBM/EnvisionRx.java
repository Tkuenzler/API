package PBM;

import client.Record;

public class EnvisionRx {
	public static String Filter(Record record) {
		switch(record.getPcn()) {
			case "PARTD":
				return InsuranceType.PDP;
			case "RC1":
			case "RC2":
				return InsuranceType.UNKNOWN_PBM;
			case "ROIRX":
				return InsuranceType.PRIVATE_UNKNOWN;
			default: 
				return FilterByGroup(record);
		}
	}
	private static String FilterByGroup(Record record) {
		switch(record.getGrp()) {
			case "EICH002":
			case "EICH003":
			case "EICH030":
			case "HS006":
				return InsuranceType.MEDICARE_TELMED;
			case "BOS0RCII":
			case "SIG0SRCI":
			case "MER0SRCI":
			case "MER0RCIX":
			case "STH0RCII":
			case "SIG0RCII":
				return InsuranceType.MEDICAID;
			default: 
				return InsuranceType.UNKNOWN_PBM;
		}
	}
}
