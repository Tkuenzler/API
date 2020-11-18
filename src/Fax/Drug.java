package Fax;

import javax.swing.JOptionPane;
import PBM.InsuranceFilter;
import PBM.InsuranceType;
import client.Record;

public enum Drug {
		//Local Anesthetic
		LidocaineTetracine("Pliaglis (Lidocaine 7% Tetracaine 7% Cream)","300 Grams","Apply a thin layer (2-3 gms) to affected area 3-4 times a day. Peel off after waiting the required application time as directed (max application time of 60 minutes).","LOCAL ANESTHETIC",new String[] {"71800063115"}),
		Lidocaine300("Lidocaine 5% Ointment ","300 Grams","Apply 2-3 gms to affected area(s) 3-4 times a day. (1 gram = 1 dime size).","LOCAL ANESTHETIC",new String[] {"65162091853","52565000855","68462041827"}),
		Lidocaine250("Lidocaine 5% Ointment ","250 Grams","Apply 2-3 gms to affected area(s) 3-4 times a day. (1 gram = 1 dime size).","LOCAL ANESTHETIC",new String[] {"65162091853","52565000855","68462041827"}),
		LidoPrilo360("Lidocaine 2.5% Prilocaine 2.5% Cream","360 Grams"," Apply 2-3 grams to the affected area 3-4 times daily.","LOCAL ANESTHETIC",new String[] {"50383066730","591207030","70512003030","115146845","52565000730"}),
		LidoPrilo240("Lidocaine 2.5% Prilocaine 2.5% Cream","240 Grams"," Apply 2-3 grams to the affected area 3-4 times daily.","LOCAL ANESTHETIC",new String[] {"50383066730","591207030","70512003030","115146845","52565000730"}),
		//Neurpathic Pain
		Doxepin("Doxepin 5% Cream","180 Grams","Apply a thin film (1-2 gms) to painful area 3 times daily. Wait at least 3-4 hours between applications. (1 gram = 1 dime size)","NEUROPATHIC PAIN",new String[] {"378811745"}),
		//Inflammation Management-Non-Sterodial
		Diclofenac3("Diclofenac 3% Topical Gel","300 Grams","Apply 2-3 gms to affected area(s) 3-4 times a day (1gram = 1 dime size).","INFLAMMATION MANAGEMENT: NON-STEROIDAL",new String[] {"00168084401","115148361","68462035594"}),
		Diclofenac1("Diclofenac 1% Topical Gel","300 Grams","Apply 2-3 gms to affected area(s) 3-4 times a day (1gram = 1 dime size).","INFLAMMATION MANAGEMENT: NON-STEROIDAL",new String[] {""}),
		//Inflammation Management-Sterodial
		Diflorasone360("Diflorasone Diacetate Ointment 0.05%","360 Grams"," Apply 2-3 gms to affected area(s) 3-4 times daily (1 gm =1 dime size).","INFLAMMATION MANAGEMENT: STEROID",new String[] {"71800000960","70512003160"}),
		Diflorasone180("Diflorasone Diacetate Ointment 0.05%","180 Grams"," Apply 1-2 gms to affected area(s) 2-3 times daily (1 gm =1 dime size).","INFLAMMATION MANAGEMENT: STEROID",new String[] {"71800000960","70512003160"}),
		Hydrocorisone("Hydrocortisone Butyrate Lotion 0.1%","236 mls","Apply 4 mls to the affected skin area 2 times daily, and rub in gently.","INFLAMMATION MANAGEMENT: STEROID",new String[] {"70512003204"}),
		Fluocinonide("Fluocinonide 0.1% Topical Cream ","240 Grams"," Apply 1-2 gm to affected area 3-4 times daily as directed.","INFLAMMATION MANAGEMENT: STEROID",new String[] {"00168045704"}),
		
		ClobetasolCream("Clobetasol 0.05% Cream","360 Grams","Apply 2-3 grams to affected area(s) 3-4 times daily.","INFLAMMATION MANAGEMENT: STEROID",new String[] {"00168016260","713065660","51672125903","70710140103","52565003960","68462053065","70700010617"}),
		Clobetasol360("Clobetasol 0.05% Ointment","360 Grams","Apply 2-3 grams to affected area(s) 3-4 times daily.","INFLAMMATION MANAGEMENT: STEROID",new String[] {"00168016260","713065660","51672125903","70710140103","52565003960","68462053065","70700010617"}),
		Clobetasol180("Clobetasol 0.05% Ointment","180 Grams","Apply 1-2 grams to affected area(s) 3-4 times daily. Maximum use 50g perweek","INFLAMMATION MANAGEMENT: STEROID",new String[] {"00168016260","713065660","51672125903","70710140103","52565003960","68462053065","70700010617"}),
		Clobetasol120("Clobetasol 0.05% Ointment","120 Grams","Apply 1-2 grams to affected area(s) 1-2 times daily. Maximum use 50g perweek","INFLAMMATION MANAGEMENT: STEROID",new String[] {"00168016260","713065660","51672125903","70710140103","52565003960","68462053065","70700010617"}),
		Desoximetasone360("Desoximetasone 0.05% Cream","360 Grams","Apply 2-3 grams to affected area(s) 3-4 times daily.","INFLAMMATION MANAGEMENT: STEROID",new String[] {"51672127103"}),
		Desoximetasone120("Desoximetasone 0.05% Cream","120 Grams","Apply 1-2 grams to affected area(s) 1-2 times daily.","INFLAMMATION MANAGEMENT: STEROID",new String[] {"51672127103"}),
		Triamcinolone("Triamcinolone Acetonide 0.147 mg/gm Topical Aerosol","400 Grams","Apply 2-3 sprays to affected area(s) 3-4 times daily (2 second spray = 1 gram)","INFLAMMATION MANAGEMENT: STEROID",new String[] {"71800015631"}),
		CalcipotreneBetaMethasoone("Calciporene BetaMethasone","","","INFLAMMATION MANAGEMENT: STEROID",new String[] {"00781716535"}),
		//Psoriasis
		CalcipotrieneOintment("Calcipotriene 0.005% Topical Ointment","360 Grams","Apply 2-3 gm to affected area 3-4 times daily as directed.","PSORIASIS",new String[] {""}),
		CalcipotrieneFoam("Calcipotriene 0.005% Foam","360 Granms","Apply 2-3 gm to affected area 3-4 times daily as directed.","PSORIASIS",new String[] {}),
		Calcipotriene360("Calcipotriene 0.005% Topical Cream","360 Grams","Apply 2-3 gm to affected area 3-4 times daily as directed.","PSORIASIS",new String[] {"00781711783","16714076302","68462050166"}),
		Calcipotriene240("Calcipotriene 0.005% Topical Cream","240 Grams","Apply 2-3 gm to affected area 2-3 times daily as directed.","PSORIASIS",new String[] {"00781711783","16714076302","68462050166"}),
		//SCAR
		SilKPad("Sil-K Pad","4 Patches","Cut pad to fit scar with 1/4 inch beyond the scar on all sides. Apply patch and leave on for 8-12 hours per day then remove (Discard the pad and replace with a new one every 7 days)","SCAR",new String[] {"70350261501"}),
		//NSAID
		Fenoprofen200("Fenoprofen 200mg Capsule","180 Capsules","Take 1-2 capsules by mouth up to 3 times daily","NSAID",new String[] {"69336012410"}),
		Fenoprofen400("Fenoprofen 400mg Capsule","120 Capsules","Take 1 capsules by mouth 3-4 times daily.","NSAID",new String[] {"69336012410"}),
		Ketoprofen180("Ketoprofen 25 mg Capsule","180 Capsules","Take 1-2 capsules by mouth up to 3 times daily.","NSAID",new String[] {"69336012710"}),
		Ketoprofen240("Ketoprofen 25 mg Capsule","240 Capsules","Take 1-2 capsules by mouth up to 4 times daily.","NSAID",new String[] {"69336012710"}),
		NaproxenOralSuspension("Naproxen Oral Suspenion (125MG/5ML) ","946 ML","Take 10-20ml by mouth twice daily as needed for pain.","NSAID",new String[] {"68134020116","70868020016"}),
		Naproxen375("Naproxen CR 375 mg","60 Tablets","Take 1-2 tablet by mouth daily for pain.","NSAID",new String[] {"47781015301"}),
		Naproxen500("Naproxen 500 mg","60 Tablets","Take 1 tablet twice as a Day with Food","NSAID",new String[] {""}),
		Indomethacin("Indomethacin Oral Capsule 20 MG","180 Capsules","Take 2 capsules by mouth three times daily for pain","NSAID",new String[] {}),
		Ibuprofen800("Ibuprofen 800mg","60 Tablets","Take 2 tablets by mouth daily for pain.","NSAID",new String[] {}),
		//Muscle Relaxants
		Chlorzoxazone250("Chlorzoxazone 250mg Tablet","120 Tablets","Take 1 tablet by mouth 3 or 4 times daily for muscle spasm.","MUSCLE RELAXANT",new String[] {"69499033060"}),
		Chlorzoxazone375("Chlorzoxazone 375mg Tablet","120 Tablets","Take 1 tablet by mouth 3 or 4 times daily for muscle spasm.","MUSCLE RELAXANT",new String[] {"13811071710"}),
		Chlorzoxazone500("Chlorzoxazone 500mg Tablet","120 Tablets","Take 1 tablet by mouth 3 or 4 times daily for muscle spasm.","MUSCLE RELAXANT",new String[] {"13811071710"}),
		Cyclobenzaprine7_5mg("Cyclobenzaprine 7.5mg Tablet","120 Tablets","Take one tablet by mouth up to 4 times daily as need muscle spasm.","MUSCLE RELAXANT",new String[] {"69420100101","591333001","71800000301","52817033110"}),
		Cyclobenzaprine5mg("Cyclobenzaprine 5mg Tablet","90 Tablets","Take one tablet by mouth up to 3 times daily as need muscle spasm.","MUSCLE RELAXANT",new String[] {""}),
		Cyclobenzaprine10mg("Cyclobenzaprine 10mg Tablet","90 Tablets","Take one tablet by mouth up to 3 times daily as need muscle spasm.","MUSCLE RELAXANT",new String[] {"69097084607"}),
		Cyclobenzaprine90("Cyclobenzaprine 7.5mg Tablet","90 Tablets","Take one tablet by mouth up to 2-3 times daily as needed.","MUSCLE RELAXANT",new String[] {"69420100101","591333001","71800000301","52817033110"}),
		Metaxalone("Metaxalone 800mg Tablet","120 Tablets","Take 1 tablet by mouth 3-4 times daily.","MUSCLE RELAXANT",new String[] {"00591234101","591234101","64980047201"}),
		Methocarbamol750("Methocarbamol 750 mg Tablets","120 Tablets","Take 1 table by mouth 3-4 times daily as needed for spasms.","MUSCLE RELAXANT",new String[] {}),
		//Migranes
		DihydroergotamineSpray("Dihydroergotamine Nasal Spray","2 boxes (16 Vials)","Use 1 spray in each nostril at onset of headache. Repeat after 15 min. If necessary, up to 4 sprays max. Prime pump by squeezing 4x before first dose.","MIGRAINES",new String[] {}),
		ErgotamineCaggeine("Ergotamine / Caffeine Tablets 1mg / 100mg","120 Tablets","Take 2 tablets by mouth at first sign of an attack; 1 additional tablet every half-hour as needed not to exceed maximum does. (Maximum doses: 6 tablets per attack; 10 tablets per 7-day period) ","MIGRAINE",new String[] {}),
		//Skin Emollinent
		Phlag("Phlag Rx Skin Emulsion","240 ML","Use 1-2 sprays on affected area and let dry re apply upto 4 times a day or as needed.","LOCAL ANESTHETIC", new String[] {"70569002502"}),

		//Fungal
		Econazole("Econazole Nitrate 1% Cream","340 Grams","Apply 3-6 grams to affected area(s) 3 times daily (1 gm=1 dime size)","ANTI-FUNGAL",new String[] {"52565002285"}),
		Mupirocin("Mupirocin 2% Cream","360 Grams","Apply 2-3 grams to the affected area(s) 3-4 times daily.","ANTI-FUNGAL",new String[] {"68462056435"}),
		Naftifine("Naftifine HCL 2% Cream","340 Grams","Apply 2 grams to the affected area(s) 4 times daily.","ANTI-FUNGAL",new String[] {""}),
		Ketoconazole("Ketoconazole 2% Cream","300 Grams","Apply 2-3 grams to the affected area(s) 3 times daily.","ANTI-FUNGAL",new String[] {""}),
		
		//FOOT SOAKS
		Cubicin("Cubicin 500mg Intravenous solution","30 Vials","Add 1 bottle to footbath and soak for 10 minutes","FOOTSOAK",new String[] {""}),
		Voriconazole("Voriconazole Inj 200mg","60 Vials","Fill Soaking device with warm water empty contents of 1-2 vials into soaking.","FOOTSOAK",new String[] {}),
		Vancomycin("Vancomycin 250 mg Capsule","180 Capsules","Add 6 capsules to footsoak for 10-20 minutes.","FOOTSOAK",new String[] {}),
		Gentamicin("Gentamicin 0.1% Cream","1800 Grams","Add 30g to foot bath and soak affected area for 15-20 minutes 1-2 times daily.","FOOTSOAK",new String[] {}),
		
		//Derm for head
		CalcipotreneSolution("Calcipotriene Scalp Solution 0.005%","180 ML","Apply 3 drops twice daily to affected area.","PSORIASIS",new String[] {"0115147555"}),
		//Constipation
		Lactulose("Lactulose","60 Packets","Dissolve a 10 gram packet in 4 oz of water 2 times daily.","CONSTIPATION",new String[] {"69067001015"}),
		//Dietary
		Xvite("Xvite Tablet","60 Tablets","Take one table by mouth twice daily.","DIETARY SUPPLEMENT",new String[] {"69067004030"}),
		OmegaEthylEster("Omega-3 Acid Ethyl Esters","360 Capsules","Take one capsule four times a day.","DIETARY SUPPLEMENT (90 Day Supply)",new String[] {"6050531707"}),
		//Cleaning
		AlcoholPad("Alcohol Pad","300"," Cleanse the skin by using 1 sterile pad on the affected area directly before applying any topical Cream / Gel / Solution","CLEANING",new String[] {"42423027101","47781015301","91237000128"}),
		//Acif Reflux
		Omeprazole("Omeprazole and Sodium Bicarbonate Capsules 40/1100 mg","30 Capsules","Take 1 capsule daily, 1 hour before breakfast","ACID REFLUX/GERD",new String[] {});
	String name,qty,sig,therapy;
	String[] ndc;
	Drug(String name,String qty, String sig,String therapy,String[] ndc) {
		this.name = name;
		this.qty = qty;
		this.sig = sig;
		this.ndc = ndc;
		this.therapy = therapy;
	}
	public String getName() {
		return name;
	}
	public String getQty() {
		return qty;
	}
	public String getQtyAmount() {
		return qty.split(" ")[0];
	}
	public String getSig() {
		return sig;
	}
	public String[] getNDCs() {
		return ndc;
	}
	public String getTherapy() {
		return therapy;
	}
	public boolean IsSameDrug(Drug drug) {
		return this.getName().equalsIgnoreCase(drug.getName());
	}
	public static String GetADrug(String message) {
		Drug[] drugs = Drug.class.getEnumConstants();
		String[] names = new String[drugs.length];
		for(int i = 0;i<names.length;i++) {
			names[i] = drugs[i].getName();
		}
		Object selected = JOptionPane.showInputDialog(null, message, "Selection", JOptionPane.DEFAULT_OPTION, null, names, names[0]);
		return selected.toString();
	}
	public static Drug GetDrug(String name) {
		Drug[] drugs = Drug.class.getEnumConstants();
		for(Drug drug: drugs) {
			if(name.equalsIgnoreCase(drug.getName()))
				return drug;
		}
		return null;
	}
	public static Drug GetDrugByNdc(String ndc) {
		Drug[] drugs = Drug.class.getEnumConstants();
		for(Drug drug: drugs) {
			for(String ndcs: drug.getNDCs()) {
				if(ndc.equalsIgnoreCase(ndcs))
					return drug;
			}
		}
		return null;
	}
	public static Drug GetTopicalScript(Record record) {
		switch(record.getBin()) {
			case "610014":
			case "003858":
			case "400023":
				return Diflorasone360;
			case "017010":
				return Diflorasone360;
			//Argus
			case "600428":
			{
				switch(record.getPcn()) {
					case "02960000":
						return Phlag;
					default:
						return Clobetasol360;
				}
				
			}
			//Medimpact 
			case "015574":
			//Optum Rx
			case "610097": {
				switch(record.getGrp().toUpperCase()) {
					case "SHCA":
						return Clobetasol360;
					default:
						return Diflorasone360;
				}
			}
			case "610494":
				return Diflorasone360;
			case "610011":
				return Diflorasone360;
			//Prime Therapeutics
			case "610455":
				return Diflorasone360;
			case "011552":{
				if(record.getPcn().equalsIgnoreCase("BCTX") || record.getPcn().equalsIgnoreCase("ILDR"))
					return null; //Prime Private
			}
			//Caremark	
			case "020099":
				return Diflorasone360;
			case "020107":
			case "020115":
			case "610502":
				return Clobetasol360;
			case "004336": {
					//Private Caremark
					if(record.getPcn().equalsIgnoreCase("ADV")) {
						if(InsuranceFilter.Filter(record).equalsIgnoreCase(InsuranceType.PRIVATE_VERIFIED))
							return Phlag;
						else 
							return Clobetasol360;
					}
					//Wellcare and Silver Scripts
					else if(record.getGrp().equalsIgnoreCase("RXCVSD") || record.getGrp().equalsIgnoreCase("788257"))
						return Drug.Clobetasol360;
					//Good MEDDADV?
					else if(record.getPcn().equalsIgnoreCase("MEDDADV")) {
						switch(record.getGrp().toUpperCase()) {
							case "RX6270":
							case "FCHP":
								return Diflorasone360;
							default:
								return Clobetasol360;
						}
					}
					else if(record.getPcn().equalsIgnoreCase("MCAIDOH"))
						return Clobetasol360;

			}
			default: 
				System.out.println("DEFAULT: "+record.getBin()+" "+record.getGrp()+" "+record.getPcn());
				return Drug.Diflorasone360;
		}
	}
	public static Drug GetOralScript(Record record) {
		switch(record.getBin()) {
			case "610455":
				return NaproxenOralSuspension;
			case "610011":
			{
				switch(record.getPcn().toUpperCase()) {
					case "IRX":
						return Fenoprofen200;
				}
			}
			case "011552":
				return Ketoprofen240;
			case "610014":
			case "003858":
			case "400023":
				return Ketoprofen240;
			case "610097":
				return Ketoprofen240;
			case "004336":
			case "020099":
			case "020107":
			case "020115":
			case "610502":
				return Cyclobenzaprine7_5mg;
			default:
				return null;
		}
	}
	public static Drug GetMigraineScript(Record record) {
		switch(record.getBin()) {
			case "015581":
			case "015599":
			case "610649":
				return DihydroergotamineSpray;
			case "610097":
			case "610279":
			case "610494":
			case "610011":
			case "017010":
				return DihydroergotamineSpray;
			case "610014":
			case "003858":
			case "400023":
				return DihydroergotamineSpray;
			default:
				return ErgotamineCaggeine;
		}
	}
	public static Drug GetFootSoak(Record record) {
		switch(record.getBin()) {
			case "015581":
			case "015599":
			case "610649":
				return Gentamicin;
			case "610097":
			case "610279":
			case "610494":
			case "610011":
			case "017010":
				return Voriconazole;
			case "610014":
			case "003858":
			case "400023":
				return Vancomycin;
			default:
				return Vancomycin;
		}
	}
	public static Drug GetAntiFungal(Record record) {
		switch(record.getBin()) {
			case "015581":
			case "015599":
			case "610649":
				return Econazole;
			case "610097":
			case "610279":
			case "610494":
			case "610011":
			case "017010":
				return Mupirocin;
			case "610014":
			case "003858":
			case "400023":
				return  Naftifine;
			default:
				return Econazole;
		}
	}
	public static Drug GetDermatitis(Record record) {
		int type = InsuranceFilter.GetInsuranceType(record);
		switch(record.getBin()) {
			case "610279":
				return CalcipotrieneFoam;
			case "003858":
			case "610014":
				if(type==InsuranceType.Type.PRIVATE_INSURANCE)
					return CalcipotrieneFoam;
				else
					return Calcipotriene360;
			case "004336":
			case "020015":
			case "610502":
			case "610591":
				return CalcipotrieneOintment;
			case "015581":
			case "015599":
			case "610649":
				return Calcipotriene360;
			case "610097":
			case "610494":
			case "610011":
			case "017010":
				return Calcipotriene360;
			case "400023":
				return  Calcipotriene360; 
			default:
				return Calcipotriene360;
		}
	}
}
