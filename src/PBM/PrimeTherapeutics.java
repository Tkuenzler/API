package PBM;

import client.Record;

public class PrimeTherapeutics {
	public static String Filter(Record record) {
		if(record.getBin().equalsIgnoreCase("015905")) {
			switch(record.getPcn()) {
				case "HMONC":
				case "PPONC":
				case "HMONCG":
				case "PPONCG":
					return InsuranceType.MAPD;
				case "PDPNC":
				case "PDPNCG":
					return InsuranceType.PDP;
				default: 
					return InsuranceType.PRIVATE_VERIFIED;
				
			}
		}
		if(record.getBin().equalsIgnoreCase("610455")) {
			switch(record.getPcn()) {
				case "MCAIDMN":
					return InsuranceType.MEDICAID;
				case "CAPD":
				case "CAPDG":
				case "CAPBGM":
				case "CAPD2":
				case "CAPDG2":
				case "CAPBGM2":
				case "CAPPDP":
				case "CAPPDPG":
					return InsuranceType.MEDICARE_COMMERCIAL;
				case "HMHS":
				default:
					return InsuranceType.PRIVATE_VERIFIED;
			}
		}
		if(record.getPcn().equalsIgnoreCase("FLBC"))
			switch(record.getGrp()) {
				case "FM99999MBS01":
				case "FM99999M1301":
				case "FNS899700401":
				case "FS99999U5F01":
				case "FN99999ZFG01":
				case "FZ99999G5K01":
				case "FN99999ZU701":
				case "FS99999UF201":
				case "FM99999MBA01":
				case "FM99999MB801":
				case "FS99999UCA01":
				case "FM99999MBL01":
				case "FNJ009100101":
				case "FS99999U5T01":
				case "FM99999MBW01":
				case "FNS798000301":
				case "FZ5660900401":
					return InsuranceType.NOT_COVERED;
				case "FN6474000301":
				case "FN1603515B01":
				case "FN45635R2202":
				case "FS99999UCX01":
				case "FN6385300101":
				case "FS99999UU501":
				case "FN3727100101":
				case "FS99999UU301":
				case "FND049300101":
				case "FN4522101101":
				case "FS99999UBU01":
				case "FZ99999GBU01":
				case "FN99999ZBU01":
				case "FZ9640400101":
				case "FZ99999GFW01":
				case "FN7882600401":
				case "FN2493600101":
				case "FZ99999GCX01":
				case "FZ99999GFY01":
				case "FNB771200101":
				case "FN99999Z5M01":
				case "FN6466400201":
				case "FN99999ZU501":
				case "FZS777600301":
				case "FZ99999GFU01":
				case "FN6922001501":
				case "FN3153900101":
				case "FN3204700101":
				case "FN7815500201":
				case "FN9880102801":
					return InsuranceType.PRIVATE_NO_TELMED;
				default:
					return InsuranceType.PRIVATE_NO_TELMED;
			}
			switch(record.getPcn()) {
				case "BCTX":
					if(record.getGrp().equalsIgnoreCase("0231"))
						return InsuranceType.OUT_OF_NETWORK;
					else
						return InsuranceType.PRIVATE_VERIFIED;
				case "ILDR":
				case "NMDR":
				case "BCBSKS": 
				case "1215":
				case "HZRX":
				case "DSNPPRI":
					return InsuranceType.PRIVATE_VERIFIED;
				case "ILDEMD":
				case "MEDDPRIME":
				case "HMOPOSNJ":
				case "HMOPOSNJG":
					return InsuranceType.MEDICARE_COMMERCIAL;
				case "PDPTX":
				case "PDPNJ":
				case "PDPIL": 
					return InsuranceType.PDP;
				case "SALUD":
				case "ILCAID":
					return InsuranceType.MEDICAID;
				default:
					return FilterByGroup(record);
				}
	}
	private static String FilterByGroup(Record record) {
		switch(record.getGrp()) {
			case "B0000002":
				return InsuranceType.PRIVATE_VERIFIED;
			default:
				return InsuranceType.UNKNOWN_PBM;
		}
	}
}
