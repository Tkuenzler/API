package JSONParameters;

import javax.json.JsonException;

import org.json.JSONException;
import org.json.JSONObject;

public class RDSParameters {
	//Demographics
	public static final String FIRST_NAME = "CFT_First";
	public static final String LAST_NAME = "CFT_Last";
	public static final String PHONE = "CFT_Phone";
	public static final String DOB = "CFT_dob";
	public static final String ADDRESS = "CFT_Address";
	public static final String CITY = "CFT_City";
	public static final String STATE = "CFT_State";
	public static final String ZIP = "CFT_Zip";
	public static final String GENDER = "CFT_Gender";
	public static final String CARRIER = "CFT_Payor";
	public static final String POLICY_ID = "CFT_MemberID";
	public static final String BIN = "CFT_BinRX";
	public static final String GRP = "CFT_Group";
	public static final String PCN = "CFT_PCN";
	public static final String PRODUCTS = "CFT_Source_Products";
	public static final String SIGNATURE_VALIDATION = "SignatureValidation";
	public static final String SUB_PROGRAM = "subProgram";
	
	public static final String[] DEMOGRAPHICS = {FIRST_NAME,LAST_NAME,PHONE,DOB,ADDRESS,CITY,STATE,ZIP,GENDER,CARRIER,POLICY_ID,BIN,GRP,PCN,PRODUCTS};
	
	//General Questions
	public static final String ALLERGIES = "CFT_Sub_Allergies";
	public static final String MEDICATIONS = "CFT_Sub_Prescriptions";
	public static final String HEIGHT = "CFT_Sub_Height";
	public static final String WEIGHT = "CFT_Sub_Weight";
	public static final String SMOKE = "CFT_Sub_Smoker_YesNo";
	public static final String CANCER  = "CFT_Sub_Cancer";
	public static final String CHEMOTHERAPY  = "CFT_Sub_Chemo";
	
	public static final String[] GENERAL_QUESTIONS = {ALLERGIES,MEDICATIONS,HEIGHT,WEIGHT,SMOKE,CANCER,CHEMOTHERAPY};
	
	//Pain
	public static final String PAIN_LOCATION = "CFT_Sub_Pain_Complaint";
	public static final String PAIN_LEVEL = "CFT_Sub_Pain_Level";
	public static final String PAIN_CAUSE = "CFT_Sub_Pain_Caused_By";
	public static final String PAIN_START = "CFT_Sub_When_Problem_Began";
	public static final String PAIN_FREQUENCY = "CFT_Sub_Pain_Frequency";
	public static final String PAIN_DURATION = "CFT_Sub_Pain_Duration";
	public static final String PAIN_DESCRIPTION = "CFT_Sub_Pain_Description";
	public static final String PAIN_TREATMENT = "CFT_Sub_Other_Treatments";
	public static final String PAIN_CAUSING_ACTIVITIES = "CFT_Sub_Pain_Causing_Activites";
	public static final String PAIN_SURGERY = "CFT_Sub_Surgery";
	public static final String PAIN_OTHER_SURGERIES = "CFT_Sub_Other_Surgery_Description";
	public static final String PAIN_OTHER_SURGERY_REASON = "CFT_Sub_Other_Surgery";
	
	public static final String[] PAIN_TRIAGE =  {PAIN_LOCATION,PAIN_LEVEL,PAIN_CAUSE,PAIN_START,PAIN_FREQUENCY,PAIN_DURATION,PAIN_DESCRIPTION
			,PAIN_TREATMENT,PAIN_CAUSING_ACTIVITIES,PAIN_SURGERY,PAIN_OTHER_SURGERIES,PAIN_OTHER_SURGERY_REASON};
	
	//RASH
	public static final String DERMATITIS_CAUSE = "CFT_Sub_Rash_Cause"; 
	public static final String DERMATITIS_LOCATION = "CFT_Sub_Rash_Location";
	public static final String DERMATITIS_LENGTH = "CFT_SUb_Rash_How_Long";
	public static final String DERMATITIS_TREATMENT = "CFT_Sub_Rash_Prior_Treatment";
			
	public static final String[] DERMATITIS_TRIAGE = {DERMATITIS_CAUSE,DERMATITIS_LOCATION,DERMATITIS_LENGTH,DERMATITIS_TREATMENT};
	
	//ANTI-FUNGAL
	public static final String FUNGAL_ISSUE = "NOT_USED_SKIN_FUNGAL_ISSUE";
	public static final String FUNAL_LENGTH = "CFT_Sub_SkinFungal_How_Long";
	
	public static final String[] FUNGAL_TRIAGE = {FUNGAL_ISSUE,FUNAL_LENGTH};
			
	//ACID REFLUX LENGTH
	public static final String ACID_REFLUX_LENGTH = "CFT_Sub_AcifdReflux_How_Long";
	public static final String ACID_REFLUX_TREATMENT = "CFT_Sub_AcidReflux_Treatment";
	
	public static final String[] ACID_REFLUX_TRIAGE = {ACID_REFLUX_LENGTH,ACID_REFLUX_TREATMENT};

	//MIGRAINES
	public static final String MIGRAINE_AMOUNT = "NOT_USED_MIGRAINE_AMOUNT";
	public static final String MIGRAINE_DURATION = "CFT_Sub_Migraine_Duration";
	public static final String MIGRAINE_TREATMENT = "CFT_Sub_Past_Treatments_Migraines";
	public static final String MIGRAINE_LENGTH = "CFT_Sub_Migraine_Time";
	public static final String MIGRAINE_SENSITIVITY = "CFT_Sub_Migraine_Sensitivity";
	
	public static final String[] MIGRAINE_TRIAGE = {MIGRAINE_AMOUNT,MIGRAINE_DURATION,MIGRAINE_TREATMENT,MIGRAINE_LENGTH,MIGRAINE_SENSITIVITY};
		
	//COLD SORES
	public static final String COLD_SORE_FREQUENCY = "NOT_USED_COLD_SORE_FREQUENCY";
	public static final String COLD_SORE_TREATMENT = "NOT_USED_COLD_SORE_TREATMENT";
	
	public static final String[] COLD_SORE_TRIAGE = {COLD_SORE_FREQUENCY,COLD_SORE_TREATMENT};
	
	//Footbath
	public static final String FOOTBATH_ISSUES = "CFT_Sub_SkinFungal_Issues";
	
	public static final String[] FOOTBATH_TRIAGE = {FOOTBATH_ISSUES};

	//Disclaimers
	public static final String AUTO_SHIP = "CFT_AutoShip";
	public static final String AUTO_SHIPAUTO_SHIP = "CFT_AutoRefill";
	public static final String DISCLAIMER = "CFT_Sub_Agree_To_Disclaimer";
	
	public static final String[] DISCLOSURES = {AUTO_SHIP,AUTO_SHIP,DISCLAIMER};
	
	public static JSONObject ConvertToRDSJSON(JSONObject obj) {
		JSONObject rds = new JSONObject();
		try {
			rds.put(SUB_PROGRAM, obj.get(TriageParameters.PHARMACY));
			for(int i = 0;i<DEMOGRAPHICS.length;i++) {
				if(DEMOGRAPHICS[i].equalsIgnoreCase(PRODUCTS)) {
					StringBuilder products = new StringBuilder();
					for(String product: TriageParameters.PRODUCTS) {
						if(!obj.has(product))
							continue;
						else if(obj.getString(product).equalsIgnoreCase("Yes")) {
							switch(product) {
								case TriageParameters.PAIN:
									products.append("Topical_Pain_MTK,");
									break;
								case TriageParameters.RASH:
									products.append("DrySkin_MTK,");
									break;
								case TriageParameters.MIGRAINES:
									products.append("Migraines_MTK,");
									break;
								case TriageParameters.FOOTBATH:
									products.append("Podiatry_MTK,");
									break;
								case TriageParameters.ANTI_FUNGAL:
									products.append("AntiFungal_MTK,");
									break;
								case TriageParameters.ACID_REFLUX:
									products.append("AcidReflux_MTK,");
									break;
								case TriageParameters.WELLNESS:
									products.append("Metabolic_MTK,");
									break;
							}
						}
					}
					products.deleteCharAt(products.lastIndexOf(","));
					rds.put(DEMOGRAPHICS[i], products.toString());
				}
				else
					rds.put(DEMOGRAPHICS[i], obj.get(TriageParameters.DEMOGRAPHICS[i]));
			}
			for(int i = 0;i<GENERAL_QUESTIONS.length;i++) {
				if(GENERAL_QUESTIONS[i].equalsIgnoreCase(HEIGHT))
					rds.put(GENERAL_QUESTIONS[i], obj.get(TriageParameters.HEIGHT_FEET)+"'"+obj.get(TriageParameters.HEIGHT_INCHES)+"\"");
				else
				rds.put(GENERAL_QUESTIONS[i], obj.get(TriageParameters.GENERAL_QUESTIONS[i]));
			}
			
			for(int i = 0;i<PAIN_TRIAGE.length;i++) 
				rds.put(PAIN_TRIAGE[i], obj.get(TriageParameters.PAIN_TRIAGE[i]));
			
			for(int i = 0;i<DERMATITIS_TRIAGE.length;i++) 
				rds.put(DERMATITIS_TRIAGE[i], obj.get(TriageParameters.DERMATITIS_TRIAGE[i]));
			
			for(int i = 0;i<FUNGAL_TRIAGE.length;i++) 
				rds.put(FUNGAL_TRIAGE[i], obj.get(TriageParameters.FUNGAL_TRIAGE[i]));
			
			for(int i = 0;i<ACID_REFLUX_TRIAGE.length;i++) 
				rds.put(ACID_REFLUX_TRIAGE[i], obj.get(TriageParameters.ACID_REFLUX_TRIAGE[i]));
			
			for(int i = 0;i<MIGRAINE_TRIAGE.length;i++) 
				rds.put(MIGRAINE_TRIAGE[i], obj.get(TriageParameters.MIGRAINE_TRIAGE[i]));
			
			for(int i = 0;i<COLD_SORE_TRIAGE.length;i++) 
				rds.put(COLD_SORE_TRIAGE[i], obj.get(TriageParameters.COLD_SORE_TRIAGE[i]));
			
			for(int i = 0;i<DISCLOSURES.length;i++) 
				rds.put(DISCLOSURES[i], obj.get(TriageParameters.DISCLOSURES[i]));	
		} catch(JSONException ex) {
			return null;
		}
		return rds;
	}
}
