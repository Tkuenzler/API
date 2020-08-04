package PBM;

import client.Record;

public class Nativitus {
	public static String Filter(Record record) {
		switch(record.getPcn()) {
			case "MCAL":
				return InsuranceType.MEDICAID;
			case "NVTD":
				return InsuranceType.MEDICARE_COMMERCIAL;
			case "NVT":			
				return InsuranceType.PRIVATE_NO_TELMED;
			case "CHCNCPYS":
			case "ICOICO":
				return InsuranceType.OUT_OF_NETWORK;
			default:
				return InsuranceType.UNKNOWN_PBM;
		}
	}
}
