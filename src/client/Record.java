package client;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import Database.Columns.DMEColumns;
import Database.Columns.LeadColumns;
import PBM.InsuranceFilter;
import PBM.InsuranceType;

public class Record implements Cloneable{
	public String record_type;
	String firstName,lastName,dob,ssn,gender,email;
	String address,city,state,zip,phone;
	String agent,pharmacy,source,tag,afid,call_notes;
	int telmedId;
	String insuranceName,planType,carrier,id,policyId,bin,grp,pcn,additionalInfo;
	String insuranceType,status,type,emdeon_type;
	String npi,drType,drFirst,drLast,drAddress,drCity,drState,drZip,drPhone,drFax;
	String faxDisposition,telmedDisposition;
	String contractId,benefitId;
	boolean confirmDoctor = false;
	String painCause,painLocation;
	String[] products;
	int received,confirmed,age,chaseCount;
	String receivedDate,notes,faxNotes;
	//DME SHIT
	String braceList;
	public Record(ResultSet result) {
		try {
			setFirstName(toProperCase(result.getString(DatabaseClient.Columns.FIRST_NAME)));
			setLastName(toProperCase(result.getString(DatabaseClient.Columns.LAST_NAME)));
			setPhone(result.getString(DatabaseClient.Columns.PHONE_NUMBER).replaceAll("[()\\s-]+", ""));
			setId(result.getString(DatabaseClient.Columns.ID));
			setDob(result.getString(DatabaseClient.Columns.DOB));
			setSsn(result.getString(DatabaseClient.Columns.SSN));
			setGender(result.getString(DatabaseClient.Columns.GENDER));
			setAddress(toProperCase(result.getString(DatabaseClient.Columns.ADDRESS)));
			setCity(toProperCase(result.getString(DatabaseClient.Columns.CITY)));
			setState(result.getString(DatabaseClient.Columns.STATE).toUpperCase());
			setZip(result.getString(DatabaseClient.Columns.ZIP));
			setPhone(result.getString(DatabaseClient.Columns.PHONE_NUMBER).replaceAll("[()\\s-]+", ""));
			setDob(result.getString(DatabaseClient.Columns.DOB));
			setCarrier(result.getString(DatabaseClient.Columns.CARRIER));
			setBin(result.getString(DatabaseClient.Columns.BIN));
			setPcn(result.getString(DatabaseClient.Columns.PCN));
			setGrp(result.getString(DatabaseClient.Columns.GROUP));
			setPolicyId(result.getString(DatabaseClient.Columns.POLICY_ID));	
			setNpi(result.getString(DatabaseClient.Columns.NPI));
			setDrType(result.getString(DatabaseClient.Columns.DR_TYPE));
			setDrFirst(toProperCase(result.getString(DatabaseClient.Columns.DR_FIRST)));
			setDrLast(toProperCase(result.getString(DatabaseClient.Columns.DR_LAST)));
			setDrAddress(toProperCase(result.getString(DatabaseClient.Columns.DR_ADDRESS1)));
			setDrCity(toProperCase(result.getString(DatabaseClient.Columns.DR_CITY).replaceAll("\"'","")));
			setDrState(result.getString(DatabaseClient.Columns.DR_STATE).replaceAll("\"'","").toUpperCase());
			setDrZip(result.getString(DatabaseClient.Columns.DR_ZIP).replaceAll("\"'",""));
			setDrPhone(result.getString(DatabaseClient.Columns.DR_PHONE).replaceAll("[()\\-\\s]", ""));
			setDrFax(result.getString(DatabaseClient.Columns.DR_FAX));		
			setStatus(result.getString(DatabaseClient.Columns.EMDEON_STATUS));
			setFaxDisposition(result.getString(DatabaseClient.Columns.FAX_DISPOSITION));
			setPharmacy(result.getString(DatabaseClient.Columns.PHARMACY));
			setPainLocation(result.getString(DatabaseClient.Columns.PAIN_LOCATION));
			setPainCause(result.getString(DatabaseClient.Columns.PAIN_CAUSE));
			setDoctorConfirmed(result.getInt(DatabaseClient.Columns.CONFIRM_DOCTOR) == 1 ? true : false);
			setAge(result.getInt(DatabaseClient.Columns.AGE));
			setProducts(result.getString(DatabaseClient.Columns.PRODUCTS));
			setNotes(result.getString(LeadColumns.NOTES));
			setReceived(result.getInt(LeadColumns.RECEIVED));
			setConfirmed(result.getInt(LeadColumns.CONFIRM_DOCTOR));
			setReceivedDate(result.getString(LeadColumns.RECEIVED_DATE));
			setChaseCount(result.getInt(LeadColumns.CHASE_COUNT));
			ResultSetMetaData rsmd = result.getMetaData();
			if(hasColumn(rsmd,"BRACES"))
				setBraceList(result.getString("BRACES"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Record(ResultSet result,boolean dme) {
		try {
			setFirstName(toProperCase(result.getString(DMEColumns.FIRST_NAME)));
			setLastName(toProperCase(result.getString(DMEColumns.LAST_NAME)));
			setPhone(result.getString(DMEColumns.PHONE).replaceAll("[()\\s-]+", ""));
			setId(result.getString(DMEColumns.ID));
			setDob(result.getString(DMEColumns.DOB));
			setSsn(result.getString(DMEColumns.SSN));
			setGender(result.getString(DMEColumns.GENDER));
			setAddress(toProperCase(result.getString(DMEColumns.ADDRESS)));
			setCity(toProperCase(result.getString(DMEColumns.CITY)));
			setState(result.getString(DMEColumns.STATE).toUpperCase());
			setZip(result.getString(DMEColumns.ZIP));
			setCarrier(result.getString(DMEColumns.CARRIER));
			setPolicyId(result.getString(DMEColumns.POLICY_ID));	
			setNpi(result.getString(DMEColumns.NPI));
			setDrType(result.getString(DMEColumns.DR_TYPE));
			setDrFirst(toProperCase(result.getString(DMEColumns.DR_FIRST)));
			setDrLast(toProperCase(result.getString(DMEColumns.DR_LAST)));
			setDrAddress(toProperCase(result.getString(DMEColumns.DR_ADDRESS)));
			setDrCity(toProperCase(result.getString(DMEColumns.DR_CITY).replaceAll("\"'","")));
			setDrState(result.getString(DMEColumns.DR_STATE).replaceAll("\"'","").toUpperCase());
			setDrZip(result.getString(DMEColumns.DR_ZIP).replaceAll("\"'",""));
			setDrPhone(result.getString(DMEColumns.DR_PHONE).replaceAll("[()\\-\\s]", ""));
			setDrFax(result.getString(DMEColumns.DR_FAX));		
			setFaxDisposition(result.getString(DMEColumns.FAX_DISPOSITION));
			setDoctorConfirmed(result.getInt(DMEColumns.CONFIRM_DOCTOR) == 1 ? true : false);
			setNotes(result.getString(DMEColumns.NOTES));
			setReceived(result.getInt(DMEColumns.RECEIVED));
			setConfirmed(result.getInt(DMEColumns.CONFIRM_DOCTOR));
			setChaseCount(result.getInt(DMEColumns.CHASE_COUNT));
			setBraceList(result.getString(DMEColumns.BRACES));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public class PatientKeys {
		public final static String SUCCESS = "success";
		public final static String RECORD_TYPE = "Record_Type";
		public final static String MESSAGE = "message";
		public final static String UPDATED = "updated";
		public final static String FIRST_NAME = "firstName";
		public final static String LAST_NAME = "lastName";
		public final static String GENDER = "gender";
		public final static String SSN = "ssn";
		public final static String ADDRESS = "address";
		public final static String CITY = "city";
		public final static String STATE = "state";
		public final static String ZIP  = "zip";
		public final static String ID = "id";
		public final static String TELMED_ID = "telmed_id";
		public final static String NAME = "name";
		public final static String DOB = "dob";
		public final static String PHONE = "phone";
		public final static String NPI = "npi";
		public final static String DR_FIRST = "drFirst";
		public final static String DR_LAST = "drLast";
		public final static String DR_ADDRESS = "drAddress";
		public final static String DR_CITY = "drCity";
		public final static String DR_STATE = "drState";
		public final static String DR_ZIP = "drZip";
		public final static String DR_PHONE = "drPhone";
		public final static String DR_FAX = "drFax";
		public final static String FAX_DIPSOSITION = "faxDisposition";
		public final static String CONFIRM_DOCTOR = "confirmDoctor";
		public final static String PHARMACY = "pharmacy";
		public final static String ERROR = "error";
	}
	public Record() {
	
	}
	public String getCallNotes() {
		if(this.call_notes==null)
			return "";
		else
			return call_notes;
	}
	public void setCallNotes(String call_notes) {
		if(call_notes==null)
			this.call_notes = "";
		else
			this.call_notes = call_notes;
	}
	public String getAfid() {
		if(this.afid==null)
			return "";
		else
			return afid;
	}
	public void setAfid(String afid) {
		if(afid==null)
			this.afid = "";
		else
			this.afid = afid;
	}
	public String faxNotes() {
		if(this.faxNotes==null)
			return "";
		else
			return this.faxNotes;
	}
	public void setFaxNotes(String faxNotes) {
		if(faxNotes==null)
			this.faxNotes = "";
		else
			this.faxNotes = faxNotes;
	}
	public int getChaseCount() {
		return this.chaseCount;
	}
	public void setChaseCount(int chaseCount) {
		this.chaseCount = chaseCount;
	}
	public int getConfirmed() {
		return this.confirmed;
	}
	public void setConfirmed(int confirmed) {
		this.confirmed = confirmed;
	}
	public int getReceived() {
		return this.received;
	}
	public void setReceived(int received) {
		this.received = received;
	}
	public String getReceivedDate() {
		if(this.receivedDate==null)
			return "0000-00-00";
		else if(this.receivedDate.equalsIgnoreCase(""))
			return "0000-00-00";
		else 
			return this.receivedDate;
	}
	public void setReceivedDate(String receivedDate) {
		if(receivedDate==null)
			this.receivedDate = "0000-00-00";
		else
			this.receivedDate = receivedDate;
	}
	public String getNotes() {
		if(this.notes==null)
			return "";
		else
			return notes;
	}

	public void setNotes(String notes) {
		if(notes==null)
			this.notes = "";
		else 
			this.notes = notes;
	}
	public String getTag() {
		if(this.tag==null)
			return "";
		else
			return tag;
	}

	public void setTag(String tag) {
		if(tag==null)
			this.tag = "";
		else 
			this.tag = tag;
	}
	public String getBraceList() {
		if(braceList==null)
			return "";
		else 
			return braceList;
	}
	public void setProducts(String products) {
		if(products==null)
			this.products = new String[] {"Pain"};
		else 
			this.products = products.split(",");
	}
	public String[] getProducts() {
		if(this.products==null)
			return new String[] {"Pain"};
		else
			return products;
	}
	public String getProductsAsString() {
		StringBuilder sb = new StringBuilder();
		if(products==null)
			return "Pain";
		else {
			for(int i = 0;i<products.length;i++) {
				if(i==products.length-1)
					sb.append(products[i]);
				else 
					sb.append(products[i]+",");
			}
			return sb.toString();
		}
	}
	public void setBraceList(String braceList) {
		this.braceList = braceList;
	}	
	public void setBraceList(ArrayList<String> braces) {
		StringBuilder brace = new StringBuilder();
		for(int i = 0;i<braces.size();i++) {
			String b = braces.get(i);
			if(i==braces.size()-1)
				brace.append(b);
			else
				brace.append(b+",");
		}
		this.braceList = brace.toString();
	}	
	
	public String getPlanType() {
		if(this.planType==null)
			return "";
		else 
			return planType;
	}


	public void setPlanType(String planType) {
		this.planType = planType;
	}


	public String getPainCause() {
		if(painCause==null)
			return "";
		else
			return painCause;
	}
	public String getPainLocation() {
		if(painLocation==null)
			return "";
		else
			return painLocation;
	}
	public void SetInsurance(JSONObject obj) throws JSONException {
		setPolicyId(obj.getString(NDCVerifyClient.JSON.POLICY_ID));
		setBin(obj.getString(NDCVerifyClient.JSON.BIN));
		setPcn(obj.getString(NDCVerifyClient.JSON.PCN));
		setGrp(obj.getString(NDCVerifyClient.JSON.GRP));
		setCarrier(getPBMFromBin(obj.getString(NDCVerifyClient.JSON.BIN)));
	}
	public int getCurrentAge() {
		if(this.dob.equalsIgnoreCase("") || this.dob.equalsIgnoreCase("01/01/1900"))
			return 0;
		else  {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
			LocalDate birthDate = LocalDate.parse(this.dob,formatter);
			LocalDate currentDate = LocalDate.now();
			return Period.between(birthDate, currentDate).getYears();
		}
			
	}
	public void setAge(int age) {
		this.age = age;
	}
	public int getAge() {
		return this.age;
	}
	public void calculateAge() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		LocalDate birthDate = LocalDate.parse(this.dob,formatter);
		LocalDate currentDate = LocalDate.now();
		int currentAge = Period.between(birthDate, currentDate).getYears();
		this.age = currentAge;
	}
	public void setEmdeonType() {
		if(!getStatus().equalsIgnoreCase("FOUND")) {
			this.emdeon_type = getStatus();
			return;
		}
		String type = InsuranceFilter.Filter(this);
		switch(type) {
			//Private 
			case InsuranceType.PRIVATE_NO_TELMED:
			case InsuranceType.PRIVATE_VERIFIED:
			case InsuranceType.PRIVATE_UNKNOWN:
			case InsuranceType.NOT_COVERED:
			case InsuranceType.PRIVATE_NOTVALIDATED:
			case InsuranceType.MOLINA:
				emdeon_type = "Private";
				break;
			//Medicare
			case InsuranceType.MEDICARE_TELMED:
			case InsuranceType.MEDICARE_COMMERCIAL:
			case InsuranceType.MAPD:
			case InsuranceType.MAPD_HMO:
			case InsuranceType.MAPD_PPO:
			case InsuranceType.PDP:
			case InsuranceType.MEDICAID_MEDICARE:
				emdeon_type = "Medicare";
				break;
			//Unknown
			case InsuranceType.UNKNOWN_PBM:
				emdeon_type = "Unknown";
				break;
			case InsuranceType.OUT_OF_NETWORK:
			case InsuranceType.MEDICAID:
				emdeon_type = "Medicaid";
				break;
			case InsuranceType.TRICARE:
				emdeon_type = "Tricare";
				break;
			default:
				emdeon_type = "NONE";
				break;
		}
	}
	public String getEmdeonType() {
		if(this.emdeon_type==null)
			return "";
		else
			return this.emdeon_type;
	}
	public void setPainCause(String painCause) {
		if(painCause==null)
			this.painCause = "";
		else
			this.painCause = painCause;
	}
	public void setPainLocation(String painLocation) {
		if(painLocation==null)
			this.painLocation = "";
		else
			this.painLocation = painLocation;
	}
	public int getTelmedId() {
		return telmedId;
	}
	public void setTelmedId(int telmedId) {
		this.telmedId = telmedId;
	}

	public String getEmail() {
		if(email==null)
			return "";
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getContractId() {
		if(contractId==null)
			return "";
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public String getBenefitId() {
		if(benefitId==null)
			return "";
		return benefitId;
	}

	public void setBenefitId(String benefitId) {
		this.benefitId = benefitId;
	}

	public String getPharmacy() {
		if(this.pharmacy==null)
			return "";
		else 
			return pharmacy;
	}

	public void setPharmacy(String pharmacy) {
		if(pharmacy==null)
			this.pharmacy = "";
		else
			this.pharmacy = pharmacy;
	}

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = StripDown(firstName);
	}
	public String getLastName() {
		if(this.lastName==null)
			return "";
		else
			return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = StripDown(lastName);
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	public String getSsn() {
		return ssn;
	}
	public void setSsn(String ssn) {
		this.ssn = ssn.trim();
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = StripDown(gender);
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = StripDown(address);
	}
	public String getCity() {
		if(city==null)
			return "";
		else
			return city;
	}
	public void setCity(String city) {
		if(city==null)
			this.city = "";
		else
			this.city = StripDown(city);
	}
	public String getState() {
		if(state==null)
			return "";
		else
			return state;
	}
	public void setState(String state) {
		if(state==null)
			this.state = "";
		else
			this.state = StripDown(state);
	}
	public String getZip() {
		if(zip==null)
			return "";
		else 
			return zip;
	}
	public void setZip(String zip) {
		if(zip==null)
			this.zip = "";
		else
			this.zip = StripDown(zip);
	}
	public String getPhone() {
		if(this.phone==null)
			return "";
		else
			return phone;
	}
	public void setPhone(String phone) {
		this.phone = StripDown(phone);
	}
	public String getAdditionalInfo() {
		if(additionalInfo==null)
			return "";
		else
			return additionalInfo;
	}
	public void setAdditionalInfo(String additionalInfo) {
		if(additionalInfo==null)
			this.additionalInfo = "";
		else
			this.additionalInfo = additionalInfo;
	}
	public String getCarrier() {
		if(this.carrier==null)
			return "";
		else
			return carrier;
	}
	public void setCarrier(String carrier) {
		if(carrier==null)
			this.carrier = "";
		else
			this.carrier = StripDown(carrier);
	}
	public String getInsuranceName() {
		if(this.insuranceName==null)
			return "";
		else
			return insuranceName;
	}
	public void setInsuranceName(String insuranceName) {
		if(insuranceName==null)
			this.insuranceName = "";
		else 
			this.insuranceName = StripDown(insuranceName);
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	public String getInsuranceType() {
		return insuranceType;
	}
	public void setInsuranceType(String insuranceType) {
		this.insuranceType = insuranceType;
	}
	public String getSource() {
		if(this.source==null)
			return "";
		else 
			return source;
	}

	public void setSource(String source) {
		if(source==null)
			this.source = "";
		else 
			this.source = source;
	}

	public String getStatus() {
		if(this.status==null)
			return "";
		else
			return status;
	}
	public void setStatus(String status) {
		if(status==null)
			this.status = "";
		else
			this.status = status;
	}
	public String getType() {
		if(this.type==null)
			return "";
		else 
			return type;
	}
	public void setType(String type) {
		if(type==null)
			this.type = "";
		else 
			this.type = type;
	}
	public String getPolicyId() {
		if(policyId==null)
			return "";
		else
			return policyId;
	}
	public void setPolicyId(String policyId) {
		this.policyId = StripDown(policyId);
	}
	public String getBin() {
		if(this.bin==null)
			return "";
		else
			return bin;
	}
	public void setBin(String bin) {
		this.bin = StripDown(bin);
	}
	public String getGrp() {
		if(this.grp==null)
			return "";
		else
			return grp;
	}
	public void setGrp(String grp) {
		if(grp==null)
			this.grp = "";
		else
			this.grp = grp;
	}
	public String getPcn() {
		if(this.pcn==null)
			return "";
		else 
			return pcn;
	}
	public void setPcn(String pcn) {
		if(pcn==null)
			this.pcn = "";
		else
			this.pcn = pcn;
	}
	public String getAgent() {
		if(this.agent==null)
			return "";
		else
			return agent;
	}
	public void setAgent(String agent) {
		this.agent = agent;
	}
	public String getNpi() {
		if(this.npi==null)
			return "";
		else 
			return npi;
	}
	public void setNpi(String npi) {
		this.npi = StripDown(npi);
	}
	public String getDrType() {
		return drType;
	}
	public void setDrType(String drType) {
		this.drType = StripDown(drType);
	}
	public String getDrFirst() {
		return drFirst;
	}
	public void setDrFirst(String drFirst) {
		this.drFirst = StripDown(drFirst);
	}
	public String getDrLast() {
		if(this.drLast==null)
			return "";
		else
			return drLast;
	}
	public void setDrLast(String drLast) {
		this.drLast = StripDown(drLast);
	}
	
	public String getDrAddress() {
		return drAddress;
	}
	public void setDrAddress(String drAddress) {
		this.drAddress = StripDown(drAddress);
	}
	public String getDrCity() {
		return drCity;
	}
	public void setDrCity(String drCity) {
		this.drCity = StripDown(drCity);
	}
	public String getDrState() {
		return drState;
	}
	public void setDrState(String drState) {
		this.drState = StripDown(drState);
	}
	public String getDrZip() {
		return drZip;
	}
	public void setDrZip(String drZip) {
		this.drZip = StripDown(drZip);
	}
	public String getDrPhone() {
		return drPhone;
	}
	public void setDrPhone(String drPhone) {
		this.drPhone = StripDown(drPhone);
	}
	public String getDrFax() {
		return drFax;
	}
	public void setDrFax(String drFax) {
		this.drFax = StripDown(drFax);
	}
	public String getFaxDisposition() {
		if(this.faxDisposition==null)
			return "";
		else	
			return faxDisposition;
	}
	public void setFaxDisposition(String faxDisposition) {
		if(faxDisposition.equalsIgnoreCase("Confirmed Doctor"))
			this.faxDisposition = "";
		else
			this.faxDisposition = faxDisposition;
	}
	public String getTelmedDisposition() {
		return telmedDisposition;
	}
	public void setTelmedDisposition(String telmedDisposition) {
		this.telmedDisposition = telmedDisposition;
	}
	public void setDoctorConfirmed(boolean confirmDoctor) {
		this.confirmDoctor = confirmDoctor;
	}
	public boolean isDoctorConfirmed() {
		return confirmDoctor;
	}
	public String returnPatientJSON(int i,String message) {
		JSONObject obj = new JSONObject();
		try {
			obj.put(PatientKeys.SUCCESS, true);
			obj.put(PatientKeys.MESSAGE, message);
			obj.put(PatientKeys.NAME, getFirstName()+" "+getLastName());
			obj.put(PatientKeys.PHONE, getPhone());
			obj.put(PatientKeys.FIRST_NAME, getFirstName());
			obj.put(PatientKeys.LAST_NAME, getLastName());
			obj.put(PatientKeys.GENDER, getGender());
			obj.put(PatientKeys.SSN, getSsn());
			obj.put(PatientKeys.ADDRESS, getAddress());
			obj.put(PatientKeys.CONFIRM_DOCTOR, isDoctorConfirmed());
			obj.put(PatientKeys.CITY, getCity());
			obj.put(PatientKeys.STATE, getState());
			obj.put(PatientKeys.ZIP, getZip());
			obj.put(PatientKeys.DOB, getDob());
			obj.put(PatientKeys.PHONE, getPhone());
			obj.put(PatientKeys.NPI, getNpi());
			obj.put(PatientKeys.DR_FIRST, getDrFirst());
			obj.put(PatientKeys.DR_LAST, getDrLast());
			obj.put(PatientKeys.DR_ADDRESS, getDrAddress());
			obj.put(PatientKeys.DR_CITY, getDrCity());
			obj.put(PatientKeys.DR_STATE, getDrState());
			obj.put(PatientKeys.DR_ZIP, getDrZip());
			obj.put(PatientKeys.DR_PHONE, getDrPhone());
			obj.put(PatientKeys.DR_FAX, getDrFax());
			obj.put(PatientKeys.PHARMACY, getPharmacy());
			obj.put(PatientKeys.FAX_DIPSOSITION, getFaxDisposition());
			obj.put(PatientKeys.UPDATED, i);
			if(getId().equalsIgnoreCase(""))
				obj.put(PatientKeys.ID, getFirstName()+getLastName()+getPhone());
			else
				obj.put(PatientKeys.ID, getId());
			obj.put(PatientKeys.RECORD_TYPE, this.record_type);
		} catch(JSONException ex) {
			ex.printStackTrace();
			return ex.getMessage();
		}
		return obj.toString();
	}
	public String returnTelmedJSON(boolean success,int i,String error) {
		JSONObject obj = new JSONObject();
		try {
			obj.put(PatientKeys.SUCCESS, success);
			obj.put(PatientKeys.ERROR, error);
			obj.put(PatientKeys.UPDATED, i);
			obj.put(PatientKeys.FIRST_NAME, getFirstName());
			obj.put(PatientKeys.LAST_NAME, getLastName());
			obj.put(PatientKeys.PHONE, getPhone());
			obj.put(PatientKeys.TELMED_ID, getTelmedId());
		} catch(JSONException ex) {
			ex.printStackTrace();
			return ex.getMessage();
		}
		return obj.toString();
	}
	
	public static String toProperCase(String text) {
	    if (text == null || text.isEmpty()) {
	        return text;
	    }
	    StringBuilder converted = new StringBuilder();
	    boolean convertNext = true;
	    for (char ch : text.toCharArray()) {
	        if (Character.isSpaceChar(ch)) {
	            convertNext = true;
	        } else if (convertNext) {
	            ch = Character.toTitleCase(ch);
	            convertNext = false;
	        } else {
	            ch = Character.toLowerCase(ch);
	        }
	        converted.append(ch);
	    }
	    return converted.toString();
	}
	public String getPBMFromBin(String bin) {
		if(bin==null)
			return "";
		switch(bin) {
			case "004336": 
			case "610239":
			case "610591":
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
			case "012353":
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
			case "012312":
			case "009893":
				return "Envision RX";
			case "014864":
				return "Magellan  Rx";
			default:
				return "";
		}
	}
	public String StripDown(String s) {
		if(s==null)
			return "";
		else
			return s.trim().replaceAll("[^A-Za-z0-9\\s]", "");
	}
	public static boolean hasColumn(ResultSetMetaData rs, String columnName) throws SQLException {
	    int columns = rs.getColumnCount();
	    for (int x = 1; x <= columns; x++) {
	        if (columnName.equals(rs.getColumnName(x))) {
	            return true;
	        }
	    }
	    return false;
	}
	
}