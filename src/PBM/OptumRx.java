package PBM;

import client.Record;

public class OptumRx {
	public static String Filter(Record record) {
		if(record.getGrp().startsWith("ACU"))
			return InsuranceType.MEDICAID;
		if(record.getBin().equalsIgnoreCase("610127"))
			return FilterByPcn(record);
		if(record.getBin().equalsIgnoreCase("610097"))
			return Filter610097(record);
		switch(record.getGrp()) {
			case "WEISRX":
			case "LABCORP":
			case "DENSO":
			case "MSG":
			case "AHCRX":
			case "ATLUFCW":
			case "OE3":
			case "LOVESRX":
			case "SHIRE":
			case "PCCA":
			case "TRNSTRX":
			case "MILWPS":
			case "MILWCTY":
			case "ILWU":
			case "UTF":
			case "CTYMILW":
			case "YRGRX":
			case "ERSTX":
				return InsuranceType.PRIVATE_NO_TELMED;
			case "CHRISTUS":
			case "ETNRX":
			case "SIE":
			case "AMNJ":
			case "NAESRX":
			case "MIMEDICAID":
				return InsuranceType.OUT_OF_NETWORK;
			case "BRUNRX":
			case "FRB":
			case "LOCAL813":
			case "MTRX":
			case "JBPRX":
			case "RXPAPER":
			case "CURX":
			case "PSI4005":
			case "PSI6238":
				return InsuranceType.PRIVATE_VERIFIED;
			case "ACUOHMMP":
				return InsuranceType.MEDICARE_COMMERCIAL;
			case "OHHRX":
			case "GECRX":
				return InsuranceType.NOT_COVERED;
			default:
				if(record.getPcn().equalsIgnoreCase("9999"))
					return InsuranceType.PRIVATE_UNKNOWN;
				else
					return InsuranceType.UNKNOWN_PBM;
		}
	}
	private static String Filter610097(Record record) {
		switch(record.getGrp()) {
			case "COS":
				return InsuranceType.MEDICARE_TELMED;
			case "PDPIND":
			case "MPDCSP":
				return InsuranceType.MEDICARE_TELMED;
			default:
				return InsuranceType.MEDICARE_TELMED;
		}
	}
	private static String FilterByPcn(Record record) {
		switch(record.getPcn()) {
			case "01960000":
			case "02330000":
				return InsuranceType.PRIVATE_NO_TELMED;
			default:
				return InsuranceType.UNKNOWN_PBM;
		}
	}
}
