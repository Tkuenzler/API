package PBM;

import client.Record;

public class Catamaran {
	public static String Filter(Record record) {
		switch(record.getGrp()) {
			case "SWPCOM":
				return InsuranceType.NOT_COVERED;
			case "IBCFOCHCR":
			case "IBCFOCHCJ":
			case "IBCFOCCOM":
			case "IBCNA":
				return InsuranceType.PRIVATE_VERIFIED;
				
			//610011
			case "FEDEX":
			case "SHNCOMMER":
			case "RXBENEFIT":
			case "11960000":
				return InsuranceType.PRIVATE_NO_TELMED;
			case "GDYEAR":
			case "CAESARS":
			case "SONJCOM":
			case "EXELON":
			case "NISSAN":
			case "JFKHEALTH":
			case "PURCORNEL":
			case "WASTEPRO":
			case "STMI":
			case "SRHS":
			case "0202":
			case "202087501":
			case "ALABAMA":
			case "CALPANTP":
			case "CGHC":
			case "CHCARE":
			case "CHURCHILL":
			case "DCHHS":
			case "FIELDALE":
			case "H001XHMR001":
			case "HLTCAT":
			case "I2045768":
			case "I2050508":
			case "I8132224":
			case "IRNMTN":
			case "IRXPRK":
			case "iuoe12":
			case "KVATRX":
			case "LINCARE":
			case "LINDE":
			case "MFAA":
			case "MHHPPO":
			case "MIWIND":
			case "NB1RX":
			case "NLHC":
			case "OHPCOMM":
			case "PCP":
			case "RWF":
			case "SNKR":
			case "SFIFF":
			case "SFIJMFG":
			case "SFISMFD":
			case "SLCLWF":
			case "TECHNI":
			case "TMKRX":
			case "WALMART":
				return InsuranceType.PRIVATE_VERIFIED;
			case "GCOCOMM":
				return InsuranceType.PRIVATE_NO_TELMED;
			case "*":
			case "CORMCARE":
			case "YOURCARE":
			case "IN0M1814":
			case "SPTM01":
			case "CORMCAID":
			case "CCMCAID":
			case "*ALL":
			case "034":
				return InsuranceType.MEDICAID;
			case "GCOMEDD":
				return InsuranceType.MEDICARE_COMMERCIAL;
			default: 
				return FilterByPcn(record);
		}
	}
	public static String FilterByPcn(Record record) {
		switch(record.getPcn()) {
			case "FRH":
			case "OPH":
				return InsuranceType.MEDICARE_COMMERCIAL;
			case "CTRXMEDD":
				return InsuranceType.MEDICARE_TELMED;
			case "IRX":
				return InsuranceType.PRIVATE_UNKNOWN;
			case "No Data Returned":
				return InsuranceType.MEDICAID;
			case "06430000":
				return InsuranceType.PRIVATE_VERIFIED;
			default:
				return InsuranceType.UNKNOWN_PBM;
		}
	}
}
