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
		{
			if(record.getPolicyId().length()>=3)
				if(record.getPolicyId().charAt(3)=='W')
					return InsuranceType.MEDICARE_TELMED;
			return InsuranceType.MEDICARE_COMMERCIAL;
		}
		default:
			return InsuranceType.PRIVATE_UNKNOWN;
		}	
	}
}
