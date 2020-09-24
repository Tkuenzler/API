package ResponseBuilder;

import org.json.JSONException;
import org.json.JSONObject;
import client.Record;

public class InsuranceResponse {
	public static class Parameters {
		public final static String STATUS = "STATUS";
		public final static String TYPE = "TYPE";
		public final static String CARRIER = "CARRIER";
		public final static String POLICY_ID = "POLICY_ID";
		public final static String BIN = "BIN";
		public final static String GROUP = "GROUP";
		public final static String PCN = "PCN";
		public final static String CHECK = "CHECK";
		public final static String MESSAGE = "MESSAGE";
		public final static String PHARMACY = "PHARMACY";
		public final static String CONTRACT_ID = "CONTRACT_ID";
		public final static String BENEFIT_ID = "BENEFIT_ID";
		public final static String ROAD_MAP = "ROAD_MAP";
		public final static String DME ="DME";
	}
	public static class Errors {
		public static final String DUPLICATE = "duplicate";
		public static final String NOT_IN_ROAD_MAP = "Not in road-map";
		public static final String INVALID_PHARMACY = "Invalid Pharmacy";
	}
	
	public static JSONObject BuildInsuranceResponse(Record record) throws JSONException {
		boolean check = false;
		if(!record.getPharmacy().equalsIgnoreCase("")) 
			check = true;
		return new JSONObject()
				.put(Parameters.STATUS, record.getStatus())
				.put(Parameters.CHECK, check)
				.put(Parameters.CARRIER, record.getCarrier())
				.put(Parameters.POLICY_ID, record.getPolicyId())
				.put(Parameters.BIN, record.getBin())
				.put(Parameters.GROUP, record.getGrp())
				.put(Parameters.PCN, record.getPcn())
				.put(Parameters.PHARMACY, record.getPharmacy())
				.put(Parameters.TYPE, record.getType())
				.put(Parameters.BENEFIT_ID, record.getBenefitId());
	}
	public static JSONObject BuildFailedResponse(String message) throws JSONException {
		return new JSONObject()
				.put(Parameters.CHECK, false)
				.put(Parameters.MESSAGE, message);
		
	}
}
