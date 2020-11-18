package PBM;

import client.Record;

public class InsuranceFilter {
	public static String GetPBMFromBin(Record record) {
		switch(record.getBin()) {
			case "004336": 
				if(record.getGrp().equalsIgnoreCase("788257") || record.getGrp().equalsIgnoreCase("RXCVSD"))
					return "SilverScripts/Wellcare";
				else
					return "Caremark";
			case "610239":
			case "610591":
			case "020107":
			case "610084":
				return "Caremark";
			case "020115":
			case "020099":
				return "Anthem";
			case "610502":
				return "Aetna";
			case "017010":
				return "Cigna";
			case "610014":
			case "400023":
			case "003858":
			case "011800":
				return "Express Scripts";
			case "012833":
			case "011552":
			case "016499":
			case "016895":
			case "014897":
			case "800001":
			case "004915":
			case "015905":
			case "610455":
			case "610212":
				return "Prime Therapeutics";
			case "610011":
			case "015814":
			case "001553":
			case "610593":
			case "011214":
				return "Catamaran";
			case "015574":
			case "015921":
			case "003585":
				return "Medimpact";
			case "011842":
			case "610279":
			case "610097":
			case "610494":
			case "610127": 
				return "OptumRx";
			case "600428":
				return "Argus";
			case "005947":
			case "603286":
				return "Catalyst Rx";
			case "015599":
			case "015581":
			case "610649":
				return "Humana";
			case "018117":
				return "Magellan Rx ";
			case "610602":
				return "Navitus";
			case "017043":
				return "ProCare Rx";
			default:
				return record.getBin();
		}
	}
	public static String Filter(Record record) {
		String insurance = null;
		switch(record.getBin()) {
			//ESI & MEDCO
			case "400023":
				insurance =  InsuranceType.MEDICARE_TELMED;
				break;
			case "011800":
				insurance =  InsuranceType.MEDICARE_COMMERCIAL;
			case "610575":
				insurance = InsuranceType.PRIVATE_VERIFIED;
				break;
			case "610014":
			case "003858":
				insurance =  ExpressScripts.Filter(record);
				break;
			//Caremark
			case "004336":
			case "610502":
			case "610591":
				insurance =  Caremark.Filter(record);
				break;
			//Anthem
			case "020099":
			case "020115":
				insurance = Anthem.Filter(record);
				break;
			case "610084":
				insurance =  InsuranceType.MEDICAID;
				break;
			case "020107":
				insurance =  Caremark.Filter(record);
				break;
			case "610239":
				insurance =  InsuranceType.MEDICARE_COMMERCIAL;
				break;
			//Prime Therapeutics
			case "012833":
			case "011552":
			case "016499":
			case "015905":
			case "610455":
				insurance =  PrimeTherapeutics.Filter(record);
				break;
			case "800001":
			case "004915":
				insurance =  InsuranceType.PRIVATE_VERIFIED;
				break;
			case "016895":
			case "014897":
				insurance =  InsuranceType.MEDICARE_COMMERCIAL;
				break;
			//Catalyst Rx
			case "005947":
			case "603286":
				insurance =  CatalystRx.Filter(record);
				break;
			//Catamaran 
			case "610011":
			case "015814":
				insurance =  Catamaran.Filter(record);
				break;
			case "001553":
			case "610593":
				insurance =  InsuranceType.MEDICAID;
				break;
			case "011214":
				insurance =  InsuranceType.MEDICARE_COMMERCIAL;
				break;
			//Medimpact
			case "015574":
				insurance =  Medimpact.Filter(record);
				break;
			case "015921":
				insurance = InsuranceType.NOT_COVERED;
			case "003585":
				insurance =  Medimpact.Filter(record);
				break;
			//OptumRx
			case "011172":
			case "011842":
				insurance =  InsuranceType.OUT_OF_NETWORK;
				break;
			case "610279":
				insurance =  InsuranceType.PRIVATE_NO_TELMED;
				break;
			case "610097": 
				insurance =  OptumRx.Filter(record);
				break;
			case "610127":
			case "610494": 
				insurance =  OptumRx.Filter(record);
				break;
			//Humana
			case "015581":
			case "015599":
				insurance =  InsuranceType.MEDICARE_TELMED;
				break;
			case "017480":
				insurance =  InsuranceType.MEDICAID;
				break;
			case "012353":
				insurance =  InsuranceType.MEDICARE_COMMERCIAL;
				break;
			case "610245":
			case "610649":
				insurance =  Humana.Filter(record);
				break;
			//Argus
			case "600428":
			case "017010":
				insurance =  Argus.Filter(record);
				break;
			//Envision Rx 
			case "012312":
			case "009893":
				insurance =  EnvisionRx.Filter(record);
				break;
			//Nativitus
			case "610602":
				insurance =  Nativitus.Filter(record);
				break;
			//Horizion Medicaid
			case "610606":
				insurance =  InsuranceType.MEDICAID;
				break;
			//West Virginia Medicaid
			case "610164":
				insurance =  InsuranceType.MEDICAID;
				break;
			case "":
				insurance =  "";
				break;
			//Meridan
			case "610241":
				insurance = Meridan.Filter(record);
			case "017142":
				return InsuranceType.MEDICAID;
			//ID Medicaid
			case "014864":
				return InsuranceType.MEDICAID;
			default:
				insurance =  InsuranceType.UNKNOWN_PBM;
				break;
		}
		if(record.getAdditionalInfo()!=null) {
			if(record.getAdditionalInfo().startsWith("ERX") && !insurance.equalsIgnoreCase(InsuranceType.MEDICARE_TELMED))
				return InsuranceType.MEDICARE_COMMERCIAL;
			else
				return insurance;
		}
		else
			return insurance;
	}
	public static int GetInsuranceType(Record record) {
		String filter = Filter(record);
		switch(filter) {
			case InsuranceType.PRIVATE_VERIFIED:
			case InsuranceType.PRIVATE_UNKNOWN:
			case InsuranceType.PRIVATE_NO_TELMED:
			case InsuranceType.OUT_OF_NETWORK:
			case InsuranceType.NOT_COVERED:
			case InsuranceType.PRIVATE_NOTVALIDATED:
				return InsuranceType.Type.PRIVATE_INSURANCE;
			case InsuranceType.MAPD:
			case InsuranceType.MAPD_PPO:
			case InsuranceType.PDP:
			case InsuranceType.MAPD_HMO:
			case InsuranceType.MEDICARE_COMMERCIAL:
			case InsuranceType.MEDICARE_TELMED:
			case InsuranceType.MEDICAID_MEDICARE:
			case InsuranceType.UNKNOWN_PBM:
				return InsuranceType.Type.MEDICARE_INSURANCE;
			case InsuranceType.MOLINA:
			case InsuranceType.MEDICAID:
				return InsuranceType.Type.MEDICAID_INSURANCE;
			case InsuranceType.TRICARE:
				return InsuranceType.Type.TRICARE_INSURANCE;
			default:
				return 0;
		}
	}
}
