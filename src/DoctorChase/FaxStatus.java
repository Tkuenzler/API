package DoctorChase;

public class FaxStatus {
	public static final String BLANK = "";
	public static final String APPROVED = "APPROVED";
	public static final String DENIED = "DENIED";
	public static final String WRONG_DOCTOR = "WRONG DOCTOR";
	public static final String WRONG_FAX = "WRONG FAX";
	public static final String NEED_PCP = "NEED PCP";
	public static final String NOT_INTERESTED = "NOT INTERESTED";
	public static final String NEEDS_TO_BE_SEEN = "NEEDS TO BE SEEN";
	public static final String NEEDS_NEW_SCRIPT = "NEEDS NEW SCRIPT";
	public static final String DECEASED = "DECEASED";
	public static final String ESCRIBE = "E-SCRIBE ONLY";
	public static final String PAIN_MANAGEMENT = "NEEDS TO SEE PAIN MANAGEMENT";
	
	public static final String[] FAX_STATUSES = {BLANK,APPROVED,DENIED,ESCRIBE,WRONG_DOCTOR,WRONG_FAX,NEED_PCP,NOT_INTERESTED,NEEDS_TO_BE_SEEN,NEEDS_NEW_SCRIPT,PAIN_MANAGEMENT,DECEASED};
	public static final String[] FIXABLE_STATUSES = {DENIED,WRONG_DOCTOR,NEED_PCP,NEEDS_TO_BE_SEEN};
}
