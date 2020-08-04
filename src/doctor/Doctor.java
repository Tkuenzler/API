package doctor;

public class Doctor {
	public String fullName,firstName,lastName,gender,npi;
	public String[] statesLicensed,type,code;
	public String businessAddress1,businessAddress2,businessCity,businessState,businessZipeCode;
	public String businessPhone,businessFax;
	public String practiceAddress1,practiceAddress2,practiceCity,practiceState,practiceZipeCode;
	public String practicePhone,practiceFax;
	public String officeLink;
	public String enumeration_type;
	public Doctor(String npi){
		this.npi = npi;
	}
	public Doctor() {
		
	}
	
	public String getEnumeration_type() {
		return enumeration_type;
	}
	public void setEnumeration_type(String enumeration_type) {
		this.enumeration_type = enumeration_type;
	}
	public String[] getStatesLicensed() {
		return statesLicensed;
	}
	public void setStatesLicensed(String[] statesLicensed) {
		this.statesLicensed = statesLicensed;
	}
	public String[] getType() {
		return type;
	}
	public void setType(String[] type) {
		this.type = type;
	}
	public String[] getCode() {
		return code;
	}
	public void setCode(String[] code) {
		this.code = code;
	}
	public String getOfficeLink() {
		return officeLink;
	}
	public void setOfficeLink(String officeLink) {
		this.officeLink = officeLink;
	}
	public String getBusinessAddress1() {
		return businessAddress1;
	}

	public void setBusinessAddress1(String businessAddress1) {
		this.businessAddress1 = businessAddress1;
	}

	public String getBusinessAddress2() {
		return businessAddress2;
	}

	public void setBusinessAddress2(String businessAddress2) {
		this.businessAddress2 = businessAddress2;
	}

	public String getBusinessCity() {
		return businessCity;
	}

	public void setBusinessCity(String businessCity) {
		this.businessCity = businessCity;
	}

	public String getBusinessState() {
		return businessState;
	}

	public void setBusinessState(String businessState) {
		this.businessState = businessState;
	}

	public String getBusinessZipeCode() {
		return businessZipeCode;
	}

	public void setBusinessZipeCode(String businessZipeCode) {
		this.businessZipeCode = businessZipeCode;
	}

	public String getBusinessPhone() {
		return businessPhone;
	}

	public void setBusinessPhone(String businessPhone) {
		this.businessPhone = businessPhone;
	}

	public String getBusinessFax() {
		return businessFax;
	}

	public void setBusinessFax(String businessFax) {
		this.businessFax = businessFax;
	}

	public String getPracticeAddress1() {
		return practiceAddress1;
	}

	public void setPracticeAddress1(String practiceAddress1) {
		this.practiceAddress1 = practiceAddress1;
	}

	public String getPracticeAddress2() {
		return practiceAddress2;
	}

	public void setPracticeAddress2(String practiceAddress2) {
		this.practiceAddress2 = practiceAddress2;
	}

	public String getPracticeCity() {
		return practiceCity;
	}

	public void setPracticeCity(String practiceCity) {
		this.practiceCity = practiceCity;
	}

	public String getPracticeState() {
		return practiceState;
	}

	public void setPracticeState(String practiceState) {
		this.practiceState = practiceState;
	}

	public String getPracticeZipeCode() {
		return practiceZipeCode;
	}

	public void setPracticeZipeCode(String practiceZipeCode) {
		this.practiceZipeCode = practiceZipeCode;
	}

	public String getPracticePhone() {
		return practicePhone;
	}

	public void setPracticePhone(String practicePhone) {
		this.practicePhone = practicePhone;
	}

	public String getPracticeFax() {
		return practiceFax;
	}

	public void setPracticeFax(String practiceFax) {
		this.practiceFax = practiceFax;
	}
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getNpi() {
		return npi;
	}

	public void setNpi(String npi) {
		this.npi = npi;
	}
	public void printDoctor() {
		System.out.println(this.fullName);
		System.out.println(this.practiceZipeCode+" "+this.practiceState);
	}
}
