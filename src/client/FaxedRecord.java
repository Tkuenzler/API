package client;

public class FaxedRecord {
	String phone,message_id,status,id;
	String record_type,pharmacy,attention;
	int faxAttempts;
	public FaxedRecord(String id,String phone,String message_id,String status,String record_type,String pharmacy) {
		this.id = id;
		this.phone = phone;
		this.message_id = message_id;
		this.status = status;
		this.record_type = record_type;
		this.pharmacy = pharmacy;
	}
	public FaxedRecord(String id,String phone,String message_id,String status,String record_type,String pharmacy,String attention, int faxAttempts) {
		this.id = id;
		this.phone = phone;
		this.message_id = message_id;
		this.status = status;
		this.record_type = record_type;
		this.pharmacy = pharmacy;
		this.attention = attention;
		this.faxAttempts = faxAttempts;
	}
	public String getAttention() {
		if(this.attention==null)
			return "";
		else
			return this.attention;
	}
	public String getPharmacy() {
		if(this.pharmacy==null)
			return "";
		else
			return pharmacy;
	}
	public int getFaxAttempts() {
		return this.faxAttempts;
	}
	public void setPharmacy(String pharmacy) {
		this.pharmacy = pharmacy;
	}

	public String getRecord_type() {
		return record_type;
	}

	public void setRecord_type(String record_type) {
		this.record_type = record_type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getMessage_id() {
		return message_id;
	}
	public void setMessage_id(String message_id) {
		this.message_id = message_id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
