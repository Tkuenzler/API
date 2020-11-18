package JSONParameters;

import org.json.JSONException;
import org.json.JSONObject;

public class BlueMosiacParameters {
	public class ResponseParameters {
		public static final String STATUS = "status";
		public static final String MESSAGE = "message";
		public static final String PATIENT_ID = "patient_id";
		
	}
	public static final String MTK_ACCESS_KEY = "0L9kZohewUGZzxoEV7zD";
	
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
	public static final String PHARMACY_ID = "pharmacy_id";
	public static final String LANGUAGE = "language";
	public static final String INSURANCE_TYPE = "insurance_type";
	public static final String THERAPY_TYPE = "therapy_type";
	public static final String POLICY_ID = "rx_insurance_id";
	public static final String BIN = "bin";
	public static final String GRP = "rx_group";
	public static final String PCN = "insurance_pcn";
	public static final String AGENT = "agent";
	public static final String PHARMACY = "pharmacy";
	public static final String NPI = "npi";
	public static final String SOURCE = "source";
	public static final String TELMED_COMPANY = "TELMED_COMPANY";
	public static final String PECOS = "is_pecos_certified";
	public static final String RECORD_ID = "patient_uid";
		
	public static final String[] DEMOGRAPHICS = {FIRST_NAME,LAST_NAME,PHONE,DOB,ADDRESS,CITY,STATE,ZIP,GENDER,CARRIER,POLICY_ID,BIN,GRP,PCN};
	
	//General Questions
	public static final String HEIGHT = "what_is_your_height_GENERAL";
	public static final String WEIGHT = "what_is_your_weight_GENERAL";
	public static final String MEDICATIONS = "what_current_medications_are_you_taking_GENERAL";
	public static final String ALLERGIES = "do_you_have_any_allergies_GENERAL";
	public static final String DIABETIC = "are_you_diabetic_GENERAL";
	public static final String DIABETES_TYPE = "what_type_of_diabetes_do_you_have_GENERAL";
	public static final String DIABETES_TEST = "how_frequently_do_you_test_per_day_GENERAL";
	public static final String INSULIN = "do_you_take_oral_or_insulin_to_treat_diabetes_GENERAL";
	public static final String HEART_DISEASE = "do_you_have_a_history_of_coronary_heart_disease_GENERAL";
	public static final String ACCESS_KEY = "access_key";
	public static final String QUICK_CODES = "quick_codes";
	public static final String RX_TYPE = "therapy_type";
	public static final String COPAY_DISCLOSURE = "co_pay_disclosure_CLOSING_STATEMENT";
	public static final String PECOS_CERTIFIED = "is_pecos_certified";
	public static final String LOCATIONS_OF_PAIN = "locations_of_pain_PAIN";
	public static final String HEIGHT_FEET = "height_feet";
	public static final String HEIGHT_INCHES = "height_inches";
	public static final String SEEN_DR = "have_you_seen_doctor_in_last_12_months_GENERAL";
	public static final String HEART_LIVER_KIDNEY_ISSUES = "any_recent_medical_issue_related_to_your_heart_liver_or_kidneys_GENERAL";
	public static final String LIVER_TEST = "have_you_had_a_liver_test_or_liver_function_test_GENERAL";
	
	//GENERAL QUESTIONS
	public static final String FOOTBATH_GENERAL = "do_you_suffer_from_foot_ulcers_blisters_on_your_feet_or_dry_skin_on_soles_of_your_feet_GENERAL";
	public static final String ANTI_FUNGAL_GENERAL = "do_you_suffer_from_any_of_the_following_fungal_infections_of_the_skin_GENERAL";
	public static final String ANTI_FUNGAL_GENERAL2 = "have_you_in_the_past_or_currently_have_a_fungal_infection_such_as_athletes_foot_or_general_fungus_between_your_toes_GENERAL";
	public static final String ACID_REFLUX_GENERAL = "do_you_have_chronic_heartburn_or_acid_reflux_GENERAL";
	public static final String WELLNESS_GENERAL = "do_you_often_feel_sluggish_lack_energy_or_get_frequent_colds_or_flu_GENERAL";
	public static final String SCAR_RASH_GENERAL = "do_you_have_any_rashes_or_scars_on_your_body_GENERAL";
	public static final String PSORIASIS_ECZEMA = "do_you_suffer_from_any_itchydry_skin_psoriasis_or_eczema_GENERAL";
	public static final String INFLAMMATION_GENERAL = "do_you_have_arthritis_joint_pain_or_any_other_pain_do_to_inflammation_that_you_would_like_treated_GENERAL";
	public static final String MUSCLE_RELAXANT_GENERAL = "have_you_recently_experienced_pain_due_to_muscle_spasms_body_tension_or_tight_muscles_that_you_would_like_treated_GENERAL";
	public static final String PAIN_LOCATION_GENERAL = "locations_of_pain_GENERAL";
	public static final String COLD_SORES_GENERAL = "do_you_experience_cold_sores_GENERAL";
	public static final String MIGRAINE_GENERAL = "do_you_get_migraine_or_sinus_headaches_GENERAL";
	
	
	

	//PAIN
	public static final String PAIN_LOCATION = "locations_of_pain_PAIN";
	public static final String PAIN_BEGAN = "when_was_the_onset_of_pain_PAIN";
	public static final String PAIN_CAUSE = "what_was_the_cause_of_pain_PAIN";
	public static final String PAIN_DURATION = "what_is_the_duration_of_pain_PAIN";
	public static final String PAIN_DESCRIPTION = "can_you_describe_the_pain_PAIN";
	public static final String PAIN_TREATMENT = "what_makes_your_pain_feel_better_PAIN";
	public static final String PAIN_CAUSING_ACTIVITES = "what_makes_it_worse_PAIN";
	public static final String PAIN_LEVEL = "rate_of_pain_PAIN";
	//INFLAMMATION
	public static final String INFLAMMATION_LOCATION = "with_regards_to_the_pain_you_indicated_do_you_experience_muscle_or_joint_inflammation_in_any_areas_if_yes_which_areas_INFLAMMATION";
	public static final String INFLAMMATION_LENGTH = "how_long_have_you_experienced_inflammation_INFLAMMATION";
	public static final String INFLAMMATION_TREATMENT = "what_have_you_done_to_treat_in_the_past_INFLAMMATION";
	public static final String INFLAMMATION_INTERESTED = "would_you_be_interested_in_an_oral_non_steroidal_anti_inflammatory_product_if_the_physician_deems_you_a_good_candidate_for_this_treatment_option_INFLAMMATION";
	//MUSCLE RELAXANTS
	public static final String MUSCLE_RELAXANT_TREATMENT = "what_have_you_done_to_treat_in_the_past_MUSCLE_SPASMS";
	public static final String MUSCLE_RELAXANT_LENGTH = "how_long_have_you_experienced_muscle_spasms_or_muscle_tightness_MUSCLE_SPASMS";
	public static final String MUSCLE_RELAXANT_INTERESTED = "would_you_be_interested_in_an_oral_muscle_relaxant_if_the_physician_deems_you_a_good_candidate_for_this_treatment_option_MUSCLE_SPASMS";
	//RASH
	public static final String RASH_LOCATION = "where_is_your_rash_skin_irritation_located_RASH_SKIN_IRRITATION";
	public static final String RASH_LENGTH = "how_long_have_you_had_it_RASH_SKIN_IRRITATION";
	public static final String RASH_CAUSE = "what_is_the_cause_of_your_skin_irritation_RASH_SKIN_IRRITATION";
	public static final String RASH_TREATMENT = "have_you_treated_the_skin_irritation_before_RASH_SKIN_IRRITATION";
	//ANT-FUNGAL
	public static final String ANTI_FUNGAL_LOCATION = "where_is_the_skin_issue_located_ANTI_FUNGAL";
	public static final String ANTI_FUNGAL_LENGTH = "how_long_have_you_had_this_skin_issue_ANTI_FUNGAL";
	public static final String ANTI_FUNGAL_TREATMENT = "have_you_previously_treated_the_skin_issue_ANTI_FUNGAL";
	//Foot Ulcers 
	public static final String FOOTBATH_ULCERS = "do_you_suffer_from_foot_ulcers_FOOTBATH";
	public static final String FOOTBATH_ULCERS_INTERESTED = "would_you_like_to_receive_a_treatment_option_for_your_foot_ulcers_FOOTBATH";
	public static final String FOOTBATH_BLISTERS = "do_you_have_blisters_on_your_feet_that_itch_FOOTBATH";
	public static final String FOOTBATH_CRACKING = "do_you_have_cracking_and_peeling_skin_on_your_feet_especially_between_your_toes_and_on_your_soles_FOOTBATH";
	public static final String FOOTBATH_DRY_SKIN = "do_you_have_dry_skin_on_your_soles_FOOTBATH";
	public static final String FOOTBATH_ITCH = "are_you_experiencing_itching_stinging_and_or_burning_between_your_toes_or_on_the_soles_of_your_feet_FOOTBATH";
	
	//MIGRAINES
	public static final String MIGRAINE_YES_NO = "do_you_experience_migraines_MIGRAINE";
	public static final String MIGRAINE_FREQUENCY = "how_often_do_you_experience_migraines_MIGRAINE";
	
	//ACID REFLUX GERD
	public static final String ACID_REFLUX_FREQUENCY = "how_often_do_you_experience_heartburnacid_re_flux_GERD";
	public static final String ACID_REFLUX_TREATMENT = "what_do_you_do_for_relief_GERD";
	//Wellnness
	public static final String WELLNESS_SLUGGISH = "are_you_generally_feeling_sluggish_or_lacking_energy_MULTI_VITAMIN";
	public static final String WELLNESS_FLU = "do_you_often_get_sick_with_the_cold_or_flu_MULTI_VITAMIN";
	
	
	//DISCLAIMER 
	public static final String CALL_BACK = "best_time_to_call";
	public static final String CONSENT = "consent_for_consultation_CLOSING_STATEMENT";
	public static final String TCPA = "tcpa_consent_CLOSING_STATEMENT";
	public static final String AUTO_SHIP = "auto_ship_consent_CLOSING_STATEMENT";
	public static final String AUTO_REFILL = "auto_refill_option_CLOSING_STATEMENT";
	public static final JSONObject ConvrtToBlueMosiacJSON(JSONObject triage) throws JSONException {
		JSONObject blue = new JSONObject();
		blue.put(ACCESS_KEY, MTK_ACCESS_KEY);
		blue.put(PHARMACY_ID, "MTK_pharmacy_4");
		if(triage.has(TriageParameters.CALL_BACK))
			blue.put(CALL_BACK,triage.getString(TriageParameters.CALL_BACK));
		else
			blue.put(CALL_BACK, "Available Immediately");
		blue.put(LANGUAGE, "english");
		blue.put(THERAPY_TYPE, "Rx");
		blue.put(INSURANCE_TYPE, "private_insurance");
		blue.put(PECOS, "1");
		for(int i = 0;i<DEMOGRAPHICS.length;i++) {
			blue.put(DEMOGRAPHICS[i], triage.getString(TriageParameters.DEMOGRAPHICS[i]));
		}
		blue.put(MEDICATIONS,triage.getString(TriageParameters.MEDICATIONS));
		blue.put(ALLERGIES,triage.getString(TriageParameters.ALLERGIES));
		blue.put(HEIGHT,triage.get(TriageParameters.HEIGHT_FEET)+"'"+triage.getString(TriageParameters.HEIGHT_INCHES)+"\"");
		blue.put(WEIGHT,triage.getString(TriageParameters.WEIGHT));
		blue.put(HEART_DISEASE, triage.getString(TriageParameters.HEART_DISEASE));
		blue.put(LIVER_TEST, triage.getString(TriageParameters.LIVER_TEST));
		blue.put(HEART_LIVER_KIDNEY_ISSUES, triage.getString(TriageParameters.HEART_LIVER_KIDNEY_ISSUES));
		blue.put(SEEN_DR, triage.getString(TriageParameters.SEEN_DR));
		blue.put(DIABETES_TEST, triage.getString(TriageParameters.DIABETES_TEST));
		String diabetic = triage.getString(TriageParameters.DIABETES);
		switch(diabetic) {
			case "Type 1":
				blue.put(DIABETIC,"Yes");
				blue.put(DIABETES_TYPE, diabetic);
				blue.put(INSULIN, "Insulin");
				break;
			case "Type 2":
				blue.put(DIABETIC,"Yes");
				blue.put(DIABETES_TYPE, diabetic);
				blue.put(INSULIN, "Oral");
				break;
			case "No":
				blue.put(DIABETIC,"No");
				blue.put(DIABETES_TYPE, "Not Diabetic");
				blue.put(INSULIN, "Not Applicable");
				break;
		
		}
		
		//Products
		for(String product: TriageParameters.PRODUCTS) {
			switch(product) {
				case TriageParameters.RASH: SetRash(blue,triage,product); break;
				case TriageParameters.PAIN: SetPain(blue,triage,product); break;
				case TriageParameters.INFLAMMATION: SetInflammation(blue,triage,product); break;
				case TriageParameters.MUSCLE_RELAXANT: SetMuscleRelaxant(blue,triage,product); break;
				case TriageParameters.ANTI_FUNGAL: SetAntiFungal(blue,triage,product); break;
				case TriageParameters.MIGRAINES: SetMigraines(blue,triage,product); break;
				case TriageParameters.FOOTBATH: SetFootbath(blue,triage,product); break;
				case TriageParameters.ACID_REFLUX: SetAcidReflux(blue,triage,product); break;
				case TriageParameters.WELLNESS: SetWellness(blue,triage,product); break;
	
			}
		}
		blue.put(CONSENT, triage.getString(TriageParameters.DISCLAIMER));
		blue.put(AUTO_REFILL,triage.getString(TriageParameters.AUTO_REFILL));
		blue.put(AUTO_SHIP, triage.getString(TriageParameters.AUTO_SHIP));
		blue.put(TCPA, "Yes");
		return blue;
	}
	private static void SetWellness(JSONObject blue,JSONObject triage,String product) throws JSONException {
		if(triage.getString(product).equalsIgnoreCase("No")) {
			blue.put(WELLNESS_GENERAL, "No");
			return;
		}
		blue.put(WELLNESS_GENERAL, "Yes");
		blue.put(WELLNESS_SLUGGISH, triage.getString(TriageParameters.WELLNESS_SLUGGISH));
		blue.put(WELLNESS_FLU, triage.getString(TriageParameters.WELLNESS_FLU));
	}
	private static void SetAcidReflux(JSONObject blue,JSONObject triage,String product) throws JSONException {
		if(triage.getString(product).equalsIgnoreCase("No")) {
			blue.put(ACID_REFLUX_GENERAL, "No");
			return;
		}
		blue.put(ACID_REFLUX_GENERAL, "Yes");
		blue.put(ACID_REFLUX_FREQUENCY, triage.getString(TriageParameters.ACID_REFLUX_LENGTH));
		blue.put(ACID_REFLUX_TREATMENT, triage.getString(TriageParameters.ACID_REFLUX_TREATMENT));
	}
	private static void SetMigraines(JSONObject blue,JSONObject triage,String product) throws JSONException {
		if(triage.getString(product).equalsIgnoreCase("No")) {
			blue.put(MIGRAINE_GENERAL, "No");
			return;
		}
		blue.put(MIGRAINE_GENERAL, "Yes");
		blue.put(MIGRAINE_YES_NO, "Yes");
		String frequency = null;
		switch(triage.getString(TriageParameters.MIGRAINE_DURATION)) {
			case "Daily": frequency = "DAILY"; break;
			case "Weekly": frequency = "WEEKLY"; break;
			case "Monthly": frequency = "MONTHLY"; break;
			default: frequency = "DAILY"; break;
		}
		blue.put(MIGRAINE_FREQUENCY, frequency);
	}
	private static void SetFootbath(JSONObject blue,JSONObject triage,String product) throws JSONException {
		if(triage.getString(product).equalsIgnoreCase("No")) {
			blue.put(FOOTBATH_GENERAL, "No");
			return;
		}
		blue.put(FOOTBATH_GENERAL, "Yes");
		blue.put(FOOTBATH_ULCERS, triage.getString(TriageParameters.FOOTBATH_ULCERS));
		blue.put(FOOTBATH_ULCERS_INTERESTED, triage.getString(TriageParameters.FOOTBATH_ULCERS_INTERESTED));
		blue.put(FOOTBATH_CRACKING, triage.getString(TriageParameters.FOOTBATH_CRACKING));
		blue.put(FOOTBATH_BLISTERS, triage.getString(TriageParameters.FOOTBATH_BLISTERS));
		blue.put(FOOTBATH_DRY_SKIN, triage.getString(TriageParameters.FOOTBATH_DRY_SKIN));
		blue.put(FOOTBATH_ITCH, triage.getString(TriageParameters.FOOTBATH_ITCH));
	}
	private static void SetAntiFungal(JSONObject blue,JSONObject triage,String product) throws JSONException {
		if(triage.getString(product).equalsIgnoreCase("No")) {
			blue.put(ANTI_FUNGAL_GENERAL2, "No");
			return;
		}
		blue.put(ANTI_FUNGAL_GENERAL, triage.getString(TriageParameters.FUNGAL_ISSUES));
		blue.put(ANTI_FUNGAL_LOCATION, triage.getString(TriageParameters.FUNGAL_LOCATION));
		blue.put(ANTI_FUNGAL_LENGTH, triage.getString(TriageParameters.FUNGAL_LENGTH));
		blue.put(ANTI_FUNGAL_TREATMENT, triage.getString(TriageParameters.FUNGAL_TREATMENT));
		
	}
	private static void SetRash(JSONObject blue,JSONObject triage,String product) throws JSONException {
		if(triage.getString(product).equalsIgnoreCase("No")) {
			blue.put(SCAR_RASH_GENERAL, "No");
			blue.put(PSORIASIS_ECZEMA, "No");
			return;
		}
		blue.put(SCAR_RASH_GENERAL, "Yes");
		blue.put(PSORIASIS_ECZEMA, "Yes");
		blue.put(RASH_LOCATION, triage.get(TriageParameters.DERMATITIS_LOCATION));
		blue.put(RASH_LENGTH, triage.get(TriageParameters.DERMATITIS_LENGTH));
		blue.put(RASH_CAUSE, triage.get(TriageParameters.DERMATITIS_CAUSE));
		blue.put(RASH_TREATMENT, triage.get(TriageParameters.DERMATITIS_TREATMENT));
	}
	private static void SetInflammation(JSONObject blue,JSONObject triage,String product) throws JSONException {
		if(triage.getString(product).equalsIgnoreCase("No")) {
			blue.put(INFLAMMATION_GENERAL, "No");
			return;
		}
		blue.put(INFLAMMATION_GENERAL, "Yes");
		blue.put(INFLAMMATION_LOCATION, triage.getString(TriageParameters.INFLAMMATION_LOCATION));
		blue.put(INFLAMMATION_LENGTH, triage.getString(TriageParameters.INFLAMMATION_LENGTH));
		blue.put(INFLAMMATION_TREATMENT, triage.getString(TriageParameters.INFLAMMATION_TREATMENT));
		blue.put(INFLAMMATION_INTERESTED, "Yes");
	}
	private static void SetMuscleRelaxant(JSONObject blue,JSONObject triage,String product) throws JSONException {
		if(triage.getString(product).equalsIgnoreCase("No")) {
			blue.put(MUSCLE_RELAXANT_GENERAL, "No");
			return;
		}
		blue.put(MUSCLE_RELAXANT_GENERAL, "Yes");
		blue.put(MUSCLE_RELAXANT_LENGTH, triage.getString(TriageParameters.MUSCLE_RELAXANT_LENGTH));
		blue.put(MUSCLE_RELAXANT_TREATMENT, triage.getString(TriageParameters.MUSCLE_RELAXANT_TREATMENT));
		blue.put(MUSCLE_RELAXANT_INTERESTED, "Yes");
	}
	private static void SetPain(JSONObject blue,JSONObject triage,String product) throws JSONException {
		blue.put(PAIN_LOCATION, triage.get(TriageParameters.PAIN_LOCATION));
		blue.put(PAIN_LOCATION_GENERAL, triage.get(TriageParameters.PAIN_LOCATION));
		blue.put(PAIN_BEGAN, triage.get(TriageParameters.PAIN_START));
		blue.put(PAIN_CAUSE, triage.getString(TriageParameters.PAIN_CAUSE));
		blue.put(PAIN_LEVEL, triage.getString(TriageParameters.PAIN_LEVEL));
		
		String frequency = null;
		switch(triage.getString(TriageParameters.PAIN_FREQUENCY)) {
			case "constantly": frequency = "Constant";  break;
			case "intermittently": frequency = "Constant";  break;
			case "daily": frequency = "Daily";  break;
			case "time to time": frequency = "Time to Time";  break;
			case "activity activated": frequency = "Activity Activated";  break;
			default: frequency = "Constant";  break;
		}
		blue.put(PAIN_DURATION, frequency);
		
		String description = null;
		switch(triage.getString(TriageParameters.PAIN_DESCRIPTION)) {
			case "sharp and stabbing": description = "Sharp/Stabbing"; break;
			case "dull and achy": description = "Dull Ache"; break;
			case "throbbing and pulsating": description = "Throbbing/Pulsating"; break;
			case "stiffness and tightness": description = "Stiffness/Tightness"; break;
			case "weak feeling and unstable": description = "Weak Feeling/Unstable"; break;
			case "radiating and traveling": description = ""; break;
			case "pins and needles": description = "Pins & Needles"; break; 
			default: description = "Sharp/Stabbing"; break;
		}
		blue.put(PAIN_DESCRIPTION, description);
		
		String treatment = null;
		switch(triage.getString(TriageParameters.PAIN_TREATMENT)) {
			case "applying heat": treatment = "Heat"; break;
			case "applying ice": treatment = "Ice"; break;
			case "lying down": treatment = "Rest"; break;
			case "resting": treatment = "Rest"; break;
			case "taking a hot shower": treatment = "Hot shower"; break;
			case "taking medication": treatment = "Medication"; break;
			case "nothing specific": treatment = "Nothing Specific"; break;
			case "seeing a chiropractor": treatment = "Nothing Specific"; break;
			default: treatment = "Nothing Specific"; break;
		}
		blue.put(PAIN_TREATMENT, treatment);
	
		String pain_causing_activities = null;
		switch(triage.getString(TriageParameters.PAIN_CAUSING_ACTIVITIES)) {
			case "standing": pain_causing_activities = "Standing/Sitting"; break;
			case "sitting": pain_causing_activities = "Standing/Sitting"; break;
			case "bending": pain_causing_activities = "Bending/Stooping"; break;
			case "stooping": pain_causing_activities = "Bending/Stooping"; break;
			case "twisting": pain_causing_activities = "Twisting"; break;
			case "walking": pain_causing_activities = "Walking"; break;
			case "driving": pain_causing_activities = "Driving"; break;
			case "lifting": pain_causing_activities = "Lifting"; break;
			case "Laying": pain_causing_activities = "Standing/Sitting"; break;
			default:  pain_causing_activities = "Standing/Sitting"; break;
		}
		blue.put(PAIN_CAUSING_ACTIVITES, pain_causing_activities);
	}
	
}
