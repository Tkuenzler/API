package client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import Database.Columns.FaxedColumns;

public class FaxedRecord {
	String phone,message_id,status,id,last_updated;
	String record_type,pharmacy,attention,lead_type,agent;
	int faxAttempts;
	File file;
	public FaxedRecord(ResultSet set) throws SQLException, IOException {
		this.phone = set.getString(FaxedColumns.PHONE);
		this.message_id = set.getString(FaxedColumns.MESSAGE_ID);
		this.status = set.getString(FaxedColumns.STATUS);
		this.id = set.getString(FaxedColumns.ID);
		this.record_type = set.getString(FaxedColumns.RECORD_TYPE);
		this.pharmacy = set.getString(FaxedColumns.PHARMACY);
		this.lead_type = set.getString(FaxedColumns.LEAD_TYPE);
		this.faxAttempts = set.getInt(FaxedColumns.FAX_ATTEMPTS);
		this.attention = set.getString(FaxedColumns.ATTENTION);
		this.last_updated = set.getString(FaxedColumns.LAST_UPDATED);		
		this.agent = set.getString(FaxedColumns.AGENT);	
		file = new File("Re-Fax.pdf");
		FileOutputStream output = new FileOutputStream(file);
		InputStream input = set.getBinaryStream(FaxedColumns.SCRIPT);
		byte[] buffer = new byte[1024];
		while (input.read(buffer) > 0) {
			output.write(buffer);
		}
		input.close();
		output.close();
		
	}
	public File getFile() {
		return this.file;
	}
	public String getAgent() {
		if(this.agent==null)
			return "";
		else
			return this.agent;
	}
	public String getLeadType() {
		if(this.lead_type==null)
			return "";
		else
			return this.lead_type;
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
