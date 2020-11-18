package JSONParameters;

public class TriageParameters {
		//Demographics
		public static final String FIRST_NAME = "first_name";
		public static final String LAST_NAME = "last_name";
		public static final String DOB = "date_of_birth";
		public static final String ADDRESS = "street_address";
		public static final String CITY = "city";
		public static final String STATE = "state";
		public static final String ZIP = "zip_code";
		public static final String SSN = "ssn";
		public static final String PHONE = "phone_number";
		public static final String GENDER = "gender";
		public static final String CARRIER = "insurance";
		public static final String POLICY_ID = "rx_insurance_id";
		public static final String BIN = "bin";
		public static final String GRP = "rx_group";
		public static final String PCN = "insurance_pcn";
		public static final String AGENT = "agent";
		public static final String PHARMACY = "pharmacy";
		public static final String NPI = "npi";
		public static final String SOURCE = "source";
		public static final String TELMED_COMPANY = "TELMED_COMPANY";
		
		public static final String[] DEMOGRAPHICS = {FIRST_NAME,LAST_NAME,PHONE,DOB,ADDRESS,CITY,STATE,ZIP,GENDER,CARRIER,POLICY_ID,BIN,GRP,PCN};
		
		//General Questions
		public static final String ALLERGIES = "allergies";
		public static final String MEDICATIONS = "medications";
		public static final String HEIGHT_FEET = "height_feet";
		public static final String HEIGHT_INCHES = "height_inches";
		public static final String WEIGHT = "weight";
		public static final String DRINK = "drink";
		public static final String ALZHEIMERS = "alzheimer";
		public static final String DEMENTIA = "dementia";
		public static final String SMOKE = "smoke";
		public static final String CANCER = "cancer";
		public static final String CHEMOTHERAPY = "chemotherapy";
		public static final String EXCERCISE = "excercise";
		public static final String DIABETES = "diabetes";
		public static final String DIABETES_TEST = "diabetes_test";
		public static final String HEART_DISEASE = "heart_disease";
		public static final String SEEN_DR = "seen_dr";
		public static final String LIVER_TEST = "liver_test";
		public static final String HEART_LIVER_KIDNEY_ISSUES = "heart_liver_kidney_issues";
		
		
		public static final String[] GENERAL_QUESTIONS = {ALLERGIES,MEDICATIONS,HEIGHT_FEET,HEIGHT_INCHES,WEIGHT,ALZHEIMERS,DEMENTIA,SMOKE,CANCER,CHEMOTHERAPY,EXCERCISE,DIABETES};
		//Pain
		public static final String PAIN_LOCATION = "pain_location";
		public static final String PAIN_LEVEL = "pain_level";
		public static final String PAIN_CAUSE = "pain_cause";
		public static final String PAIN_START = "pain_start";
		public static final String PAIN_FREQUENCY = "pain_frequency";
		public static final String PAIN_DURATION = "pain_duration";
		public static final String PAIN_DESCRIPTION = "pain_description";
		public static final String PAIN_TREATMENT = "pain_treatment";
		public static final String PAIN_CAUSING_ACTIVITIES = "pain_causing_activities";
		public static final String PAIN_SURGERY = "pain_surgery";
		public static final String PAIN_OTHER_SURGERIES = "pain_other_surgeries";
		public static final String PAIN_OTHER_SURGERY_REASON = "pain_other_surgery_reason";
		
		public static final String[] PAIN_TRIAGE =  {PAIN_LOCATION,PAIN_LEVEL,PAIN_CAUSE,PAIN_START,PAIN_FREQUENCY,PAIN_DURATION,PAIN_DESCRIPTION
				,PAIN_TREATMENT,PAIN_CAUSING_ACTIVITIES,PAIN_SURGERY,PAIN_OTHER_SURGERIES,PAIN_OTHER_SURGERY_REASON};
		//RASH
		public static final String DERMATITIS_CAUSE = "dermatitis_cause"; 
		public static final String DERMATITIS_LOCATION = "dermatitis_location";
		public static final String DERMATITIS_LENGTH = "dermatitis_length";
		public static final String DERMATITIS_TREATMENT = "dermatitis_treatment";
		public static final String DERMATITIS_REDNESS = "dermatitis_redness";
		public static final String DERMATITIS_INFLAMMATION = "dermatitis_inflammation";
		
		
		public static final String[] DERMATITIS_TRIAGE = {DERMATITIS_CAUSE,DERMATITIS_LOCATION,DERMATITIS_LENGTH,DERMATITIS_TREATMENT};
		
		//ANTI-FUNGAL
		public static final String FUNGAL_TREATMENT = "fungal_treatment";
		public static final String FUNGAL_LENGTH = "fungal_length";
		public static final String FUNGAL_LOCATION = "fungal_location";
		public static final String FUNGAL_ISSUES = "fungal_issues";
		
		public static final String[] FUNGAL_TRIAGE = {FUNGAL_TREATMENT,FUNGAL_LENGTH,FUNGAL_LOCATION};
		//ACID REFLUX LENGTH
		public static final String ACID_REFLUX_LENGTH = "acid_reflux_length";
		public static final String ACID_REFLUX_TREATMENT = "acid_reflux_treatment";

		public static final String[] ACID_REFLUX_TRIAGE = {ACID_REFLUX_LENGTH,ACID_REFLUX_TREATMENT};
		
		//MIGRAINES
		public static final String MIGRAINE_AMOUNT = "migraines_amount";
		public static final String MIGRAINE_DURATION = "migraine_duration";
		public static final String MIGRAINE_TREATMENT = "migraine_treatment";
		public static final String MIGRAINE_LENGTH = "migraine_length";
		public static final String MIGRAINE_SENSITIVITY = "migraine_sensitivity";

		public static final String[] MIGRAINE_TRIAGE = {MIGRAINE_AMOUNT,MIGRAINE_DURATION,MIGRAINE_TREATMENT,MIGRAINE_LENGTH,MIGRAINE_SENSITIVITY};
		
		//COLD SORES
		public static final String COLD_SORE_FREQUENCY = "cold_sore_frequency";
		public static final String COLD_SORE_TREATMENT = "cold_sore_treatments";
		
		public static final String[] COLD_SORE_TRIAGE = {COLD_SORE_FREQUENCY,COLD_SORE_TREATMENT};
		
		//Footbath
		public static final String FOOTBATH_PROBLEMS = "foothbath_problems";
		public static final String FOOTBATH_SEEN_DR = "foothbath_seen_dr";
		public static final String FOOTBATH_TREATMENT = "foothbath_treatment";
		public static final String FOOTBATH_ULCERS = "footbath_ulcers";
		public static final String FOOTBATH_ULCERS_INTERESTED = "footbath_interested_ulcers";
		public static final String FOOTBATH_ITCH = "footbath_itch";
		public static final String FOOTBATH_BLISTERS = "footbath_blisters";
		public static final String FOOTBATH_CRACKING = "footbath_cracking";
		public static final String FOOTBATH_DRY_SKIN = "footbath_dry_skin";
		
		
		public static final String[] FOOTBATH_TRIAGE = {FOOTBATH_PROBLEMS};

		//Inflammation
		public static final String INFLAMMATION_LOCATION = "inflammation_location";
		public static final String INFLAMMATION_LENGTH = "inflammation_length";
		public static final String INFLAMMATION_TREATMENT = "inflammation_treatment";
		
		
		//Muscle Relaxants
		public static final String MUSCLE_RELAXANT_LENGTH = "muscle_relaxant_length";
		public static final String MUSCLE_RELAXANT_TREATMENT = "muscle_relaxant_treatment";
		
		//Wellnness
		public static final String WELLNESS_FLU = "wellness_flu";
		public static final String WELLNESS_SLUGGISH = "wellness_sluggish";
		//Disclaimers
		public static final String CALL_BACK = "best_time_to_call";
		public static final String AUTO_SHIP = "auto_ship";
		public static final String AUTO_REFILL = "auto_refill";
		public static final String DISCLAIMER = "disclaimer";
		public static final String CONSULTATION_FEE = "copay";
		public static final String HOUSE_HOLD_SIZE = "house_size";
		public static final String ANNUAL_INCOME = "house_hold_income";
		
		public static final String[] DISCLOSURES = {AUTO_SHIP,AUTO_SHIP,DISCLAIMER};
		
		//PRODUCTS
		public static final String PAIN = "pain";
		public static final String RASH = "rash";
		public static final String ANTI_FUNGAL = "anti-fungal";
		public static final String ACID_REFLUX = "acid_reflux";
		public static final String MIGRAINES = "migraines";
		public static final String FOOTBATH = "footbath";
		public static final String WELLNESS = "wellness";
		public static final String COLD_SORES = "cold_sores";
		public static final String INFLAMMATION = "inflammation";
		public static final String MUSCLE_RELAXANT = "muscle_relaxant";
		
		public static final String[] PRODUCTS = {PAIN,RASH,ANTI_FUNGAL,ACID_REFLUX,MIGRAINES,FOOTBATH,WELLNESS,COLD_SORES,INFLAMMATION,MUSCLE_RELAXANT};
		
		//TRIAGE STATUS
		public static final String TRIAGE_INCOMPLETE = "Triage Incomplete";
		public static final String TRIAGE_COMPLETE = "Triage Complete";
		public static final String TRIAGE_SUBMITTED = "Triage Submitted";
}
