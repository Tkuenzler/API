package client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Fax.TelmedStatus;

public class DMEClient {
	String table;
	public Connection connect = null;
	public DMEClient(String database) {
		try {	
			//This will load the MySQL driver, each DB has its own driver
			 Class.forName("com.mysql.jdbc.Driver"); 
			 //Connect to database
			 connect = DriverManager
				      .getConnection("jdbc:mysql://ltf5469.tam.us.siteprotect.com:3306/"+database, "tkuenzler","Tommy6847");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		this.table = "DME_TELMED";
	}
	public String GetDMETelmedStatus(String phone) {
		String sql = "SELECT * FROM `DME_TELMED` WHERE `phonenumber` = '"+phone+"' AND `SnS` = 1";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) 
				return set.getString("TELMED_STATUS");
			else
				return null;
		} catch(SQLException ex) {
			return ex.getMessage();
		} finally {
			try {
				if(stmt!=null)stmt.close();
				if(set!=null)set.close();
			} catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	public String GetTelmedStatus(String phone) {
		String sql = "SELECT * FROM `DME_TELMED` WHERE `phonenumber` = '"+phone+"' AND `SnS` = 1";
		Statement stmt = null;
		ResultSet set = null;
		try {
			StringBuilder sb = new StringBuilder();
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) { 
				String first = set.getString(Columns.FIRST_NAME);
				String last = set.getString(Columns.LAST_NAME);
				String id = set.getString(Columns.TELMED_ID);
				String phonenumber = set.getString(Columns.PHONE_NUMBER);
				sb.append(first+" "+last+"("+id+"): "+phonenumber+"\r\n");
				sb.append(set.getString(Columns.TELMED_STATUS)+"\r\n");
				sb.append(set.getString("NOTES"));
				return sb.toString();
			}
			else
				return null;
		} catch(SQLException ex) {
			return ex.getMessage();
		} finally {
			try {
				if(stmt!=null)stmt.close();
				if(set!=null)set.close();
			} catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	public int GetCoveredStatus(String phone) {
		String sql = "SELECT * FROM `DME_TELMED` WHERE `phonenumber` = '"+phone+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next())
				return set.getInt(Columns.SnS);
			else 
				return -999;
		} catch(SQLException ex) {
			return ex.getErrorCode();
		} finally {
			try {
				if(stmt!=null)stmt.close();
				if(set!=null)set.close();
			} catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	public int UpdateTelmedFromRDS(String phone, String id,String status,String notes) {
		String COST = null,DATE_MODIFIED = null,NOTES = null;
		if(TelmedStatus.IfPaidStatus(status)) {
			COST = " `COST` = -90 ";
			NOTES = "`NOTES` = '', ";
		}
		else {
			COST = " `COST` = 0 ";
			NOTES = "`NOTES` = CONCAT(`NOTES`,'"+notes.replaceAll("[^A-Za-z0-9\\s]", "")+"'), ";
		}
		if((TelmedStatus.IfPaidStatus(status) && TelmedStatus.IfPaidStatus(GetCurrentTelmedStatus(phone))) ||
			(TelmedStatus.IfNotPaid(status) && TelmedStatus.IfNotPaid(GetCurrentTelmedStatus(phone)))   )
			DATE_MODIFIED = " ";
		else
			DATE_MODIFIED = " `DATE_MODIFIED` = '"+getCurrentDate("yyyy-MM-dd")+"', ";
		String sql = "UPDATE `DME_TELMED` SET `TELMED_ID` = '"+id+"',"+NOTES+DATE_MODIFIED+"`TELMED_STATUS` = '"+status+"',"+COST+"WHERE `phonenumber` = '"+phone+"'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch(SQLException ex) {
			return ex.getErrorCode();
		} finally {
			try {
				if(stmt!=null) stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public String GetCurrentTelmedStatus(String phone) {
		String sql = "SELECT `TELMED_STATUS` FROM `TELMED` WHERE `phonenumber` = '"+phone+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next())
				return set.getString("TELMED_STATUS");
			else
				return "";
		}  catch(SQLException ex) {
			return "";
		} finally {
			try {
				if(set!=null) set.close();
				if(stmt!=null) stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public int UpdateSnS(String phone,String status,int x) {
		String telmedStatus = null;
		if(status.equalsIgnoreCase("Passed SnS")) {
			telmedStatus = "`TELMED_STATUS` = 'New Patient', ";
		}
		telmedStatus = "";
		String sql = "UPDATE `DME_TELMED` SET "+telmedStatus+"`"+Columns.SnS+"` = '"+x+"' WHERE `"+Columns.PHONE_NUMBER+"` = '"+phone+"'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		}  catch(SQLException ex) {
			return ex.getErrorCode();
		} finally {
			try {
				if(stmt!=null)stmt.close();
			} catch(SQLException ex) {
				
			}
		}
	}
	public List<Record> GetUnverifiedRecords() {
		String sql = "SELECT * FROM `DME_TELMED` WHERE `SnS` = 0";
		Statement stmt = null;
		ResultSet set = null;
		List<Record> list = new ArrayList<Record>();	
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			while(set.next()) {
				Record record = new Record();
				record.setFirstName(set.getString(Columns.FIRST_NAME));
				record.setLastName(set.getString(Columns.LAST_NAME));
				record.setPhone(set.getString(Columns.PHONE_NUMBER));
				record.setDob(set.getString(Columns.DOB));
				record.setSsn(set.getString(Columns.SSN));
				record.setGender(set.getString(Columns.GENDER));
				record.setAddress(set.getString(Columns.ADDRESS));
				record.setCity(set.getString(Columns.CITY));
				record.setState(set.getString(Columns.STATE));
				record.setZip(set.getString(Columns.ZIP));
				record.setPlanType(set.getString(Columns.PLAN_TYPE));
				record.setCarrier(set.getString(Columns.CARRIER));
				record.setPolicyId(set.getString(Columns.POLICY_ID));
				record.setBraceList(set.getString(Columns.BRACES));
				list.add(record);
			}
			return list;
		} catch(SQLException ex) {
			return null;
		} finally {
			try {
				if(stmt!=null)stmt.close();
				if(set!=null)set.close();
			} catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	public Record getRecord(String phone) {
		String sql = "SELECT * FROM `DME_TELMED` WHERE `phonenumber` = '"+phone+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) {
				Record record = new Record();
				record.setFirstName(set.getString(Columns.FIRST_NAME));
				record.setLastName(set.getString(Columns.LAST_NAME));
				record.setPhone(set.getString(Columns.PHONE_NUMBER));
				record.setDob(set.getString(Columns.DOB));
				record.setSsn(set.getString(Columns.SSN));
				record.setGender(set.getString(Columns.GENDER));
				record.setAddress(set.getString(Columns.ADDRESS));
				record.setCity(set.getString(Columns.CITY));
				record.setState(set.getString(Columns.STATE).toUpperCase());
				record.setZip(set.getString(Columns.ZIP));
				record.setCarrier(set.getString(Columns.CARRIER));
				record.setPolicyId(set.getString(Columns.POLICY_ID));
				record.setBraceList(set.getString(Columns.BRACES));
				return record;
			}
			return null;
		} catch(SQLException ex) {
			return null;
		} finally {
			try {
				if(stmt!=null)stmt.close();
				if(set!=null)set.close();
			} catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	public boolean isDMEDuplicate(Record record) {
		String sql = "SELECT * FROM `"+table+"` WHERE `phonenumber` = '"+record.getPhone()+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next())
				return true;
			else
				return false;
		} catch(SQLException ex) {
			return false;
		} finally {
			try {
				if(stmt!=null)stmt.close();
				if(set!=null)set.close();
			} catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	public String AddDME(Record record,String callCenter,String plan,String co_insurance,String deductible, String brace_list) {
		PreparedStatement stmt = null;
		try {
			stmt = connect.prepareStatement(buildAddStatement(table));
			int count = 1;
			for(String column: Columns.HEADERS) {
				switch(column) {
					case Columns.FIRST_NAME:
						stmt.setString(count, record.getFirstName());
						break;
					case Columns.LAST_NAME:
						stmt.setString(count, record.getLastName());
						break;
					case Columns.DOB:
						stmt.setString(count, record.getDob());
						break;
					case Columns.SSN:
						stmt.setString(count, record.getSsn());
						break;
					case Columns.PHONE_NUMBER:
						stmt.setString(count, record.getPhone());
						break;
					case Columns.GENDER:
						stmt.setString(count, record.getGender());
						break;
					case Columns.ADDRESS:
						stmt.setString(count, record.getAddress());
						break;
					case Columns.CITY:
						stmt.setString(count, record.getCity());
						break;
					case Columns.STATE:
						stmt.setString(count, record.getState());
						break;
					case Columns.ZIP:
						stmt.setString(count, record.getZip());
						break;
					case Columns.CARRIER:
						stmt.setString(count, record.getCarrier());
						break;
					case Columns.POLICY_ID:
						stmt.setString(count, record.getPolicyId());
						break;
					case Columns.PLAN_TYPE:
						stmt.setString(count, plan);
						break;
					case Columns.DEDUCTIBLE:
						stmt.setString(count, deductible);
						break;
					case Columns.CO_INSURANCE:
						stmt.setString(count, co_insurance);
						break;
					case Columns.BRACES:
						stmt.setString(count, brace_list);
						break;
					case Columns.AGENT:
						stmt.setString(count, record.getAgent());
						break;
					case Columns.CALL_CENTER:
						stmt.setString(count, callCenter);
						break;
					case Columns.SOURCE:
						stmt.setString(count, record.getSource());
						break;
					case Columns.DATE_ADDED:
						stmt.setString(count, getCurrentDate("yyyy-MM-dd"));
						break;
				}
				count++;
			}
			int add = stmt.executeUpdate();
			if(add==1)
				return "Success";
			else
				return stmt.toString()+" "+add;
		} catch(SQLException ex) {
			return ex.getMessage();
		} finally {
			try {
				if(stmt!=null)stmt.close();
			} catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	public String buildAddStatement(String table) {
		StringBuilder base = new StringBuilder("INSERT into `"+table+"` (");
		for(String s: Columns.HEADERS) {
			if(s.equalsIgnoreCase(Columns.DATE_ADDED)) 
				base.append(s+")");
			else 
				base.append(s+",");

		}
		base.append(" values(");
		for(int i = 0;i<Columns.HEADERS.length;i++) {
			if(i==Columns.HEADERS.length-1) 
				base.append("?)");
			else 
				base.append("?,");
		}
		return base.toString();
	}
	private String getCurrentDate(String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format); 
		Date date = new Date(); 
		return formatter.format(date);
	}
	public void close() {
		try {
			this.connect.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static class Columns {
		public static final String FIRST_NAME = "first_name";
		public static final String LAST_NAME = "last_name";
		public static final String DOB = "dob";
		public static final String PHONE_NUMBER = "phonenumber";
		public static final String SSN = "ssn";
		public static final String GENDER = "gender";
		public static final String ADDRESS = "address";
		public static final String CITY = "city";
		public static final String STATE = "state";
		public static final String ZIP = "zip";
		public static final String CARRIER = "carrier";
		public static final String POLICY_ID = "policy_id";
		public static final String PLAN_TYPE = "plan_type";
		public static final String CO_INSURANCE = "co_insurance";
		public static final String DEDUCTIBLE = "deductible";
		public static final String BRACES = "BRACES";
		public static final String SOURCE = "SOURCE";
		public static final String AGENT = "agent";
		public static final String CALL_CENTER = "CALL_CENTER";
		public static final String DATE_ADDED = "DATE_ADDED";
		public static final String TELMED_ID = "TELMED_ID";
		public static final String TELMED_STATUS = "TELMED_ID";
		public static final String[] HEADERS = {FIRST_NAME,LAST_NAME,DOB,AGENT,CALL_CENTER,PHONE_NUMBER,SSN,GENDER,ADDRESS,CITY,STATE,ZIP,CARRIER,POLICY_ID,PLAN_TYPE,CO_INSURANCE,
											DEDUCTIBLE,BRACES,SOURCE,DATE_ADDED};
	
		public static final String SnS = "SnS";
	}
}
