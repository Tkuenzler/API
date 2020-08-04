package PBM;

import client.Record;

public class Meridan {
	public static String Filter(Record record) {
		switch(record.getGrp()) {
			case "MHPILMCD":
				return InsuranceType.MEDICAID;
			default:
				return InsuranceType.UNKNOWN_PBM;
		}
	}
}
