package PBM;

import client.Record;

public class CatalystRx {
	public static String Filter(Record record) {
		switch(record.getGrp()) {
			case "STOH":
			case "LEAR":
			case "JMFAM":
			case "CCOK":
			case "MGMRI":
			case "HERTZ":
			case "MPSERS":
			case "216458":
				return InsuranceType.PRIVATE_VERIFIED;
			case "PUBLIX":
				return InsuranceType.OUT_OF_NETWORK;
			default:
				return FilterByPcn(record);
		}
	}
	private static String FilterByPcn(Record record) {
		switch(record.getPcn()) {
			case "CLAIMCR":
				return InsuranceType.PRIVATE_VERIFIED;
			default:
				return InsuranceType.UNKNOWN_PBM;
		}
	}
}
