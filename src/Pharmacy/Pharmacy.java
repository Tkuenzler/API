package Pharmacy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import PBM.InsuranceFilter;
import PBM.InsuranceType;
import client.Record;
import client.RoadMapClient;

public class Pharmacy {
	
	public static String GetTelmedPharmacy(RoadMapClient client,Record record,String insurance_type,String roadmap) {
		ArrayList<PharmacyMap> map = client.getPharmaciesForTelmed(insurance_type);
		client.close();
		if(map==null)
			return null;
		else if(map.size()==0)
			return null;
		for(PharmacyMap pharmacy: map) {
			switch(insurance_type) {
				case "Medicare":
				case "Medicaid":
					if(pharmacy.getRoadMap(record.getState()).canTelmedMedicare(record.getCarrier()))
						return pharmacy.getPharmacyName();
					else
						break;
				case "Private Insurance":
				case "Marketplace":
				case "Provided by Job":
					if(pharmacy.getRoadMap(record.getState()).canTelmedPrivate(record.getCarrier()))
						return pharmacy.getPharmacyName();
					else
						break;
				
			default:
				return null;
			}
		}
		return null;
	}
	public static boolean CanTelmed(ArrayList<PharmacyMap> map,Record record,String insurance_type) {
		if(map==null)
			return false;
		else if(map.size()==0)
			return false;
		for(PharmacyMap pharmacy: map) {
			switch(insurance_type) {
				case "Medicare":
					if(pharmacy.getRoadMap(record.getState()).canTelmedMedicare())
						return true;
					else
						break;
				case "Private Insurance":
				case "Marketplace":
				case "Provided by Job":
					if(pharmacy.getRoadMap(record.getState()).canTelmedPrivate())
						return true;
					else
						break;
				
			default:
				return false;
			}
		}
		return false;
	}
	
	
	/////
	public static boolean CanRecordBeTelmed(RoadMapClient client,Record record) {
		try {
			String type = InsuranceFilter.Filter(record);
			ArrayList<PharmacyMap> map = client.getPharmaciesForTelmed(type);
			for(PharmacyMap pharmacy: map) {
				switch(type) {
					case InsuranceType.MEDICARE_TELMED:
						if(pharmacy.getRoadMap(record.getState()).canTelmedMedicare(record.getCarrier()))
							return true;
						else
							break;
					case InsuranceType.PRIVATE_VERIFIED:
					case InsuranceType.PRIVATE_UNKNOWN:
						if(pharmacy.getRoadMap(record.getState()).canTelmedPrivate(record.getCarrier()))
							return true;
						else
							break;
					default:
						return false;
				}
			}
		} catch(Exception ex) {
			
		} finally {
			if(client!=null)client.close();
		}
		return false;
	}
	
	public static int CanRecordBeTelmed1(RoadMapClient client,Record record) {
		String type = InsuranceFilter.Filter(record);
		ArrayList<PharmacyMap> map = client.getPharmaciesForTelmed(type);
		return map.size();
	}
	
	public static String GetPharmacy(HashMap<String,PharmacyMap> roadMap,Record record) {
		ArrayList<PharmacyMap> pharmacies_that_can_take = new ArrayList<PharmacyMap>();
		ArrayList<PharmacyOdds> odds = new ArrayList<PharmacyOdds>();
		int insurance_type = InsuranceFilter.GetInsuranceType(record);
		System.out.println(insurance_type);
		/*
		 * First we add all pharmacies that can take a particular insurance type
		 */
		for (String key : roadMap.keySet()) {
			if(key.equalsIgnoreCase("Carepoint"))
				continue;
			PharmacyMap pharmacy = roadMap.get(key);
			switch(insurance_type) {
				case InsuranceType.Type.PRIVATE_INSURANCE:
					if(pharmacy.canTakePrivate())
						pharmacies_that_can_take.add(pharmacy);
					break;
				case InsuranceType.Type.MEDICARE_INSURANCE:
					if(pharmacy.canTakeMedicare())
						pharmacies_that_can_take.add(pharmacy);
					break;
				case InsuranceType.Type.NOT_FOUND_INSRUACE:
					if(pharmacy.canTakeNotFound())
						pharmacies_that_can_take.add(pharmacy);
					break;
				case InsuranceType.Type.TRICARE_INSURANCE:
					if(pharmacy.canTakeTricare())
						pharmacies_that_can_take.add(pharmacy);
					break;
				case InsuranceType.Type.MEDICAID_INSURANCE:
					return "Medicaid";
				default:
					continue;
			}
		}
		if(pharmacies_that_can_take.size()==0)
			return "No Home";
		/*
		 * Then we check if there is a pharmacy in the same state that can take the insurance
		 */
		for(PharmacyMap pharmacy: pharmacies_that_can_take) {
			if(pharmacy.isInSameState(record.getState()))
				if(CheckRoadMap(record,insurance_type,pharmacy)) 
					odds.add( new PharmacyOdds(pharmacy.getPharmacyName(),pharmacy.getExtra()));
				
		}
		if(odds.size()>0)
			return GetPharmacyName(odds);
		/*
		 * 
		 */
		for(PharmacyMap pharmacy: pharmacies_that_can_take) {
			if(CheckRoadMap(record,insurance_type,pharmacy)) 
				odds.add(new PharmacyOdds(pharmacy.getPharmacyName(),pharmacy.getExtra()));
		}
		if(odds.size()>0)
			return GetPharmacyName(odds);
		else
			return "No Home";
	}
	public static boolean CanCarepointTake(PharmacyMap carepoint,Record record) {
		int insurance_type = InsuranceFilter.GetInsuranceType(record);
		return CheckRoadMap(record,insurance_type,carepoint);
	}
	private static boolean CheckRoadMap(Record record,int insurance_type,PharmacyMap pharmacy) {
		RoadMap map = pharmacy.getRoadMap(record.getState());
		if(map==null)
			return false;
		switch(record.getCarrier()) {
			case RoadMap.AETNA:	
				if(map.canTake(record,insurance_type,map.getAetna()))
					return true;
				else
					return false;
			case RoadMap.ANTHEM:
				if(map.canTake(record,insurance_type,map.getAnthem()))
					return true;
				else
					return false;
			case RoadMap.ARGUS:
				if(map.canTake(record,insurance_type,map.getArgus()))
					return true;
				else
					return false;
			case RoadMap.CAREMARK:
				if(map.canTake(record,insurance_type,map.getCaremark()))
					return true;
				else
					return false;
			case RoadMap.CATALYST_RX:
				if(map.canTake(record,insurance_type,map.getCatalyst()))
					return true;
				else
					return false;
			case RoadMap.CATAMARAN:
				if(map.canTake(record,insurance_type,map.getCatamaran()))
					return true;
				else
					return false;
			case RoadMap.CIGNA:
				if(map.canTake(record,insurance_type,map.getCigna()))
					return true;
				else
					return false;
			case RoadMap.ENVISION:
				if(map.canTake(record,insurance_type,map.getEnvision()))
					return true;
				else
					return false;
			case RoadMap.EXPRESS_SCRIPTS:
				if(map.canTake(record,insurance_type,map.getExpressScripts()))
					return true;
				else
					return false;
			case RoadMap.HUMANA:
				if(map.canTake(record,insurance_type,map.getHumana()))
					return true;
				else
					return false;
			case RoadMap.MEDIMPACT:
				if(map.canTake(record,insurance_type,map.getMedimpact()))
					return true;
				else
					return false;
			case RoadMap.NAVITUS:
				if(map.canTake(record,insurance_type,map.getNavitus()))
					return true;
				else
					return false;
			case RoadMap.NOT_FOUND:
				if(map.canTake(record,insurance_type,map.getNotFound()))
					return true;
				else
					return false;
			case RoadMap.OPTUM_RX:
				if(map.canTake(record,insurance_type,map.getOptumRx()))
					return true;
				else
					return false;
			case RoadMap.PRIME_THERAPEUTICS:
				if(map.canTake(record,insurance_type,map.getPrimeTherapeutics()))
					return true;
				else
					return false;
			case RoadMap.SILVER_SCRIPTS_WELL_CARE:
				if(map.canTake(record,insurance_type,map.getSilverScriptsWellCare()))
					return true;
				else
					return false;
			default:
				if(map.canTake(record,insurance_type,map.getNotFound()))
					return true;
				else
					return false;
	}
	}
	private static String GetPharmacyName(ArrayList<PharmacyOdds> list) {
		ArrayList<PharmacyOdds> temp = new ArrayList<PharmacyOdds>();
		for(PharmacyOdds pharmacy: list) {
			int number = (int)(pharmacy.getOdds()*100);
			for(int x = 0;x<number;x++) {
				temp.add(pharmacy);
			}
		}
		Collections.shuffle(temp); 
		Random rand = new Random();
		return temp.get(rand.nextInt(temp.size())).getName();
	}
	public static boolean GoodForAllFamily(Record record) {
		int type = InsuranceFilter.GetInsuranceType(record);
		switch(record.getState()) {
			case "FL":
			{
				switch(record.getBin()) {
					case "610239":
					case "004336":
					case "610502":
					case "020115":
					case "020099":
					case "610084":
					case "020107":
					case "012833":
						return false;
					default:
						return true;
				}
			}
			case "NY":
			case "MA":
			{
				switch(record.getBin()) {
					case "017010":
					case "012312":
					case "009893":
						return true;
					case "610014":
					case "003858":
						if(type==InsuranceType.Type.PRIVATE_INSURANCE)
							return true;
						else
							return false;
					
				}
			}
			default:
				return false;
		}
	}
}
