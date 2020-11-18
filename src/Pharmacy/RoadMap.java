package Pharmacy;


import java.sql.ResultSet;
import java.sql.SQLException;
import client.Record;
import PBM.InsuranceType;

public class RoadMap {
	String state, pharmacy;
	int caremark,silverscripts_wellcare,esi,prime,cigna,aetna,humana,medimpact;
	int argus,optum,catamaran,catalyst,navitus,envision,anthem, notFound;
	public static final String CAREMARK = "Caremark";
	public static final String SILVER_SCRIPTS_WELL_CARE = "SilverScripts/Wellcare";
	public static final String EXPRESS_SCRIPTS = "Express Scripts";
	public static final String CIGNA = "Cigna";
	public static final String PRIME_THERAPEUTICS = "Prime Therapeutics";
	public static final String AETNA= "Aetna";
	public static final String HUMANA = "Humana";
	public static final String OPTUM_RX= "OptumRx";
	public static final String CATAMARAN = "Catamaran";
	public static final String MEDIMPACT = "Medimpact";
	public static final String ARGUS = "Argus";
	public static final String NAVITUS = "Navitus";
	public static final String ENVISION = "Envision RX";
	public static final String ANTHEM = "Anthem";
	public static final String CATALYST_RX = "Catalyst Rx";
	public static final String NOT_FOUND = "Not Found";
	
	public RoadMap(String pharmacy,ResultSet set) throws SQLException {
		this.pharmacy = pharmacy;
		this.state = set.getString("state");
		this.caremark = set.getInt(CAREMARK);
		this.silverscripts_wellcare = set.getInt(SILVER_SCRIPTS_WELL_CARE);
		this.esi = set.getInt(EXPRESS_SCRIPTS);
		this.prime = set.getInt(PRIME_THERAPEUTICS);
		this.cigna = set.getInt(CIGNA);
		this.aetna = set.getInt(AETNA);
		this.humana = set.getInt(HUMANA);
		this.optum = set.getInt(OPTUM_RX);
		this.catamaran = set.getInt(CATAMARAN);
		this.medimpact = set.getInt(MEDIMPACT);
		this.argus = set.getInt(ARGUS);
		this.navitus = set.getInt(NAVITUS);
		this.envision = set.getInt(ENVISION);
		this.anthem = set.getInt(ANTHEM);
		this.catalyst = set.getInt(CATALYST_RX);
		this.notFound = set.getInt(NOT_FOUND);
	}
	public int getCaremark() {
		return this.caremark;
	}
	public int getSilverScriptsWellCare() {
		return this.silverscripts_wellcare;
	}
	public int getExpressScripts() {
		return this.esi;
	}
	public int getPrimeTherapeutics() {
		return this.prime;
	}
	public int getCigna() {
		return this.cigna;
	}
	public int getAetna() {
		return this.aetna;
	}
	public int getCatamaran() {
		return this.catamaran;
	}
	public int getHumana() {
		return this.humana;
	}
	public int getMedimpact() {
		return this.medimpact;
	}
	public int getArgus() {
		return this.argus;
	}
	public int getNavitus() {
		return this.navitus;
	}
	public int getEnvision() {
		return this.envision;
	}
	public int getAnthem() {
		return this.anthem;
	}
	public int getCatalyst() {
		return this.catalyst;
	}
	public int getOptumRx() {
		return this.optum;
	}
	public int getNotFound() {
		return this.notFound;
	}
	public boolean canTelmedPrivate(String carrier) {
		switch(carrier) {
			case CAREMARK:
				return caremark>=2;
			case SILVER_SCRIPTS_WELL_CARE:
				return silverscripts_wellcare>=2;
			case EXPRESS_SCRIPTS:
				return esi>=2;
			case CIGNA:
				return cigna>=2;
			case AETNA:
				return aetna>=2;
			case ANTHEM:
				return anthem>=2;
			case PRIME_THERAPEUTICS:
				return prime>=2;
			case HUMANA:
				return humana>=2;
			case OPTUM_RX:
				return optum>=2;
			case CATAMARAN:
				return catamaran>=2;
			case CATALYST_RX:
				return catalyst>=2;
			case ENVISION:
				return envision>=2;
			case ARGUS:
				return argus>=2;
			case MEDIMPACT:
				return medimpact>=2;
			case NAVITUS:
				return navitus>=2;
			default:
				return false;
		}
	}
	public boolean canTelmedPrivate() {
		if(caremark>=2)
			return true;
		else if(esi>=2)
			return true;
		else if(cigna>=2)
			return true;
		else if(prime>=2)
			return true;
		else if(aetna>=2)
			return true;
		else if(anthem>=2)
			return true;
		else if(optum>2)
			return true;
		else if(catamaran>=2)
			return true;
		else if(catalyst>=2)
			return true;
		else if(humana>=2)
			return true;
		else if(navitus>=2)
			return true;
		else if(envision>=2)
			return true;
		else if(medimpact>=2)
			return true;
		else if(argus>=2)
			return true;
		else
			return false;
	}
	public boolean canTelmedMedicare(String carrier) {
		switch(carrier) {
			case CAREMARK:
				return caremark>=3;
			case SILVER_SCRIPTS_WELL_CARE:
				return silverscripts_wellcare>=3;
			case EXPRESS_SCRIPTS:
				return esi>=3;
			case CIGNA:
				return cigna>=3;
			case AETNA:
				return aetna>=3;
			case ANTHEM:
				return anthem>=3;
			case PRIME_THERAPEUTICS:
				return prime>=3;
			case HUMANA:
				return humana>=3;
			case OPTUM_RX:
				return optum>=3;
			case CATAMARAN:
				return catamaran>=3;
			case CATALYST_RX:
				return catalyst>=3;
			case ENVISION:
				return envision>=3;
			case ARGUS:
				return argus>=3;
			case MEDIMPACT:
				return medimpact>=3;
			case NAVITUS:
				return navitus>=3;
			default:
				return false;
		}
	}
	public boolean canTelmedMedicare() {
		if(caremark>=3)
			return true;
		if(silverscripts_wellcare>=3)
			return true;
		else if(esi>=3)
			return true;
		else if(cigna>=3)
			return true;
		else if(prime>=3)
			return true;
		else if(aetna>=3)
			return true;
		else if(anthem>=3)
			return true;
		else if(optum>3)
			return true;
		else if(catamaran>=3)
			return true;
		else if(catalyst>=3)
			return true;
		else if(humana>=3)
			return true;
		else if(navitus>=3)
			return true;
		else if(envision>=3)
			return true;
		else if(medimpact>=3)
			return true;
		else if(argus>=3)
			return true;
		else
			return false;
	}
	public String getPharmacy() {
		if(pharmacy==null)
			return "";
		else 
			return this.pharmacy;
	}
	public String getState() {
		if(state==null)
			return "";
		else 
			return this.state;
	}
	public boolean canTake(Record record,int insurance_type,int insurance) {
		switch(insurance_type) {
			case InsuranceType.Type.MEDICAID_INSURANCE:
				if(insurance>=1)
					return true;
				else
					return false;
			case InsuranceType.Type.MEDICARE_INSURANCE:
				if(insurance>=1)
					return true;
				else
					return false;
			case InsuranceType.Type.NOT_FOUND_INSRUACE:
				if(insurance>=1)
					return true;
				else
					return false;
			case InsuranceType.Type.PRIVATE_INSURANCE:
				if(insurance>=1)
					return true;
				else
					return false;
			case InsuranceType.Type.TRICARE_INSURANCE:
				if(insurance>=1)
					return true;
				else
					return false;
			default:
				return false;
		}
	}
}
