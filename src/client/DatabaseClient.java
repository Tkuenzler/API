package client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;

import DoctorChase.FaxStatus;
import DoctorChase.MessageStatus;
import Fax.TelmedStatus;
import JSONParameters.TriageParameters;
import PBM.InsuranceFilter;
import PBM.InsuranceType;
import ResponseBuilder.TelmedResponse;

public class DatabaseClient {
	String table;
	public Connection connect = null;
	public DatabaseClient() {
		try {
			
			//This will load the MySQL driver, each DB has its own driver
			 Class.forName("com.mysql.jdbc.Driver"); 
			 //Connect to database
			 connect = DriverManager
				      .getConnection("jdbc:mysql://ltf5469.tam.us.siteprotect.com:3306/MT_MARKETING", "tkuenzler","Tommy6847");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	public DatabaseClient(String database) {
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
	}
	public JSONObject updateRecord(Record record,String table,String afid,String pharmacy,String agent,String callCenter) throws JSONException {
		try {
			Statement stmt = connect.createStatement();
			int value =  stmt.executeUpdate(buildUpdateStatement(record,table,afid,pharmacy,agent,callCenter));
			return new JSONObject().put("value", value);
		} catch(SQLException ex) {
			return new JSONObject().put("value", ex.getErrorCode()).put("message", ex.getMessage());
		}
	}
	public JSONObject addRecord(Record record,String table,String AFID,String pharmacy,String agent,String callCenter) throws JSONException {
		PreparedStatement stmt = null;
		try {
			stmt = connect.prepareStatement(buildAddStatement(table));
			for(int i = 0;i<Columns.HEADERS.length;i++) {
				String column = Columns.HEADERS[i];
				switch(column) {
				case Columns.FIRST_NAME:
					stmt.setString(i, record.getFirstName());
					break;
				case Columns.LAST_NAME:
					stmt.setString(i, record.getLastName());
					break;
				case Columns.DOB:
					stmt.setString(i, record.getDob());
					break;
				case Columns.AGE:
					stmt.setString(i, ""+record.getAge());
					break;
				case Columns.PHONE_NUMBER:
					stmt.setString(i, record.getPhone());
					break;
				case Columns.ADDRESS:
					stmt.setString(i, record.getAddress());
					break;
				case Columns.CITY:
					stmt.setString(i, record.getCity());
					break;
				case Columns.STATE:
					stmt.setString(i, record.getState());
					break;
				case Columns.ZIP:
					stmt.setString(i, record.getZip());
					break;
				case Columns.EMDEON_STATUS:
					stmt.setString(i, record.getStatus());
					break;
				case Columns.EMDEON_TYPE:
					stmt.setString(i, record.getEmdeonType());
					break;
				case Columns.TYPE:
					stmt.setString(i, record.getType());
					break;
				case Columns.LAST_EMDEON_DATE:
					stmt.setString(i, getCurrentDate("yyyy-MM-dd"));
					break;
				case Columns.CARRIER:
					stmt.setString(i, record.getCarrier());
					break;
				case Columns.INSURANCE_NAME:
					stmt.setString(i, record.getInsuranceName());
					break;
				case Columns.POLICY_ID:
					stmt.setString(i, record.getPolicyId());
					break;
				case Columns.BIN:
					stmt.setString(i, record.getBin());
					break;
				case Columns.GROUP:
					stmt.setString(i, record.getGrp());
					break;
				case Columns.PCN:
					stmt.setString(i, record.getPcn());
					break;
				case Columns.CONTRACT_ID:
					stmt.setString(i, record.getContractId());
					break;
				case Columns.BENEFIT_ID:
					stmt.setString(i, record.getBenefitId());
					break;
				case Columns.NPI:
					stmt.setString(i, record.getNpi());
					break;
				case Columns.DR_TYPE:
					stmt.setString(i, record.getDrType());
					break;
				case Columns.NOTES:
					stmt.setString(i, "");
					break;
				case Columns.DR_FIRST:
					stmt.setString(i, record.getDrFirst());
					break;
				case Columns.DR_LAST:
					stmt.setString(i, record.getDrLast());
					break;
				case Columns.DR_ADDRESS1:
					stmt.setString(i, record.getDrAddress());
					break;
				case Columns.DR_CITY:
					stmt.setString(i, record.getDrCity());
					break;
				case Columns.DR_STATE:
					stmt.setString(i, record.getDrState());
					break;
				case Columns.DR_ZIP:
					stmt.setString(i, record.getDrZip());
					break;
				case Columns.DR_PHONE:
					stmt.setString(i, record.getDrPhone());
					break;
				case Columns.DR_FAX:
					stmt.setString(i, record.getDrFax());
					break;
				case Columns.SSN:
					stmt.setString(i, record.getSsn());
					break;
				case Columns.ID:
					stmt.setString(i, record.getFirstName()+record.getLastName()+record.getPhone());
					break;
				case Columns.GENDER:
					stmt.setString(i, record.getGender());
					break;		
				case Columns.PAIN_LOCATION:
					stmt.setString(i, record.getPainLocation());
					break;
				case Columns.PAIN_CAUSE:
					stmt.setString(i, record.getPainCause());
					break;
				case Columns.FAX_DISPOSITION:
					stmt.setString(i, "");
					break;
				case Columns.MESSAGE_STATUS:
					stmt.setString(i,"");
					break;
				case Columns.AFID:
					stmt.setString(i, AFID);
					break;
				case Columns.AGENT:
					stmt.setString(i, agent);
					break;
				case Columns.PHARMACY:
					stmt.setString(i, pharmacy);
					break;
				case Columns.CALL_CENTER:
					stmt.setString(i, callCenter);
					break;
				case Columns.DATE_ADDED:
					stmt.setString(i, getCurrentDate("yyyy-MM-dd"));
					break;
				case Columns.MESSAGE_ID:
					stmt.setString(i, "");
					break;
				case Columns.SOURCE:
					stmt.setString(i, record.getSource());
					break;
				case Columns.PRODUCTS:
					stmt.setString(i, record.getProductsAsString());
					break;
				}				
			} 
			int add = stmt.executeUpdate();
			if(add==1)
				AddToAlternateScript(record);
			return new JSONObject().put("value", add);
		} catch(SQLException ex) {
			return new JSONObject().put("value", ex.getErrorCode()).put("message", ex.getMessage());
		} finally {
			try {
				if(stmt!=null) stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
	}
	public String getColumn(String phone,String column,String table) {
		String sql = "SELECT * FROM `"+table+"` WHERE `"+Columns.PHONE_NUMBER+"` = '"+phone+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) 
				return set.getString(column);
			else
				return "";
		} catch(SQLException ex) {
			return null;
		} finally {
			try {
				if(set!=null) set.close();
				if(stmt!=null) stmt.close();
			} catch(SQLException ex) {
				
			}
		}
	}
	private int AddToAlternateScript(Record record) {
		String sql = "INSERT INTO `Alternate_Scripts` (`_id`) VALUES ('"+record.getId()+"')";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch(SQLException ex) {
			return ex.getErrorCode();
		} 
		finally {
			try {
				if(stmt!=null)stmt.close();
			} catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
		
	}
	public boolean isSameDoctor(Record record,String table) {
		String sql = "SELECT * FROM `"+table+"` WHERE `"+Columns.PHONE_NUMBER+"` = '"+record.getPhone()+"' AND `"+Columns.NPI+"` = '"+record.getNpi()+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			return set.next();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
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
	public String buildAddStatement(String table) {
		StringBuilder base = new StringBuilder("INSERT into `"+table+"` (");
		for(String s: Columns.HEADERS) {
			if(s.equalsIgnoreCase(Columns.ALL))
				continue;
			else if(s.equalsIgnoreCase(Columns.DATE_ADDED)) 
				base.append(s+")");
			else 
				base.append(s+",");

		}
		base.append(" values(");
		for(int i = 0;i<Columns.HEADERS.length-1;i++) {
			if(i==Columns.HEADERS.length-2) 
				base.append("?)");
			else 
				base.append("?,");
		}
		return base.toString();
	}
	public String buildUpdateStatement(Record record,String table,String afid,String pharmacy,String agent,String callCenter) {
		StringBuilder base = new StringBuilder("UPDATE "+table+" SET ");
		for(String s: Columns.UPDATE_RECORD_HEADERS) {
			switch(s) {
			case Columns.FIRST_NAME:
				base.append("`"+Columns.FIRST_NAME+"` = '"+record.getFirstName()+"', ");
				break;
			case Columns.LAST_NAME:
				base.append("`"+Columns.LAST_NAME+"` = '"+record.getLastName()+"', ");
				break;
			case Columns.DOB:
				base.append("`"+Columns.DOB+"` = '"+record.getDob()+"', ");
				break;
			case Columns.PHONE_NUMBER:
				base.append("`"+Columns.PHONE_NUMBER+"` = '"+record.getPhone()+"', ");
				break;
			case Columns.ADDRESS:
				base.append("`"+Columns.ADDRESS+"` = '"+record.getAddress()+"', ");
				break;
			case Columns.CITY:
				base.append("`"+Columns.CITY+"` = '"+record.getCity()+"', ");
				break;
			case Columns.STATE:
				base.append("`"+Columns.STATE+"` = '"+record.getState()+"', ");
				break;
			case Columns.ZIP:
				base.append("`"+Columns.ZIP+"` = '"+record.getZip()+"', ");
				break;
			case Columns.EMDEON_STATUS:
				base.append("`"+Columns.EMDEON_STATUS+"` = '"+record.getStatus()+"', ");
				break;
			case Columns.EMDEON_TYPE:
				base.append("`"+Columns.EMDEON_TYPE+"` = '"+record.getEmdeonType()+"', ");
				break;
			case Columns.TYPE:
				base.append("`"+Columns.TYPE+"` = '"+record.getType()+"', ");
				break;
			case Columns.LAST_EMDEON_DATE:
				base.append("`"+Columns.LAST_EMDEON_DATE+"` = '"+getCurrentDate("yyyy-MM-dd")+"', ");
				break;
			case Columns.CARRIER:
				base.append("`"+Columns.CARRIER+"` = '"+record.getCarrier()+"', ");
				break;
			case Columns.POLICY_ID:
				base.append("`"+Columns.POLICY_ID+"` = '"+record.getPolicyId()+"', ");
				break;
			case Columns.BIN:
				base.append("`"+Columns.BIN+"` = '"+record.getBin()+"', ");
				break;
			case Columns.GROUP:
				base.append("`"+Columns.GROUP+"` = '"+record.getGrp()+"', ");
				break;
			case Columns.PAIN_CAUSE:
				base.append("`"+Columns.PAIN_CAUSE+"` = '"+record.getPainCause()+"', ");
				break;
			case Columns.PAIN_LOCATION:
				base.append("`"+Columns.PAIN_LOCATION+"` = '"+record.getPainLocation()+"', ");
				break;
			case Columns.AGE:
				base.append("`"+Columns.AGE+"` = '"+record.getAge()+"', ");
				break;
			case Columns.PCN:
				base.append("`"+Columns.PCN+"` = '"+record.getPcn()+"', ");
				break;
			case Columns.NPI:
				base.append("`"+Columns.NPI+"` = '"+record.getNpi()+"', ");
				break;
			case Columns.DR_FIRST:
				base.append("`"+Columns.DR_FIRST+"` = '"+record.getDrFirst()+"', ");
				break;
			case Columns.DR_LAST:
				base.append("`"+Columns.DR_LAST+"` = '"+record.getDrLast()+"', ");
				break;
			case Columns.DR_ADDRESS1:
				base.append("`"+Columns.DR_ADDRESS1+"` = '"+record.getDrAddress()+"', ");
				break;
			case Columns.DR_CITY:
				base.append("`"+Columns.DR_CITY+"` = '"+record.getDrCity()+"', ");
				break;
			case Columns.DR_STATE:
				base.append("`"+Columns.DR_STATE+"` = '"+record.getDrState()+"', ");
				break;
			case Columns.DR_ZIP:
				base.append("`"+Columns.DR_ZIP+"` = '"+record.getDrZip()+"', ");
				break;
			case Columns.DR_PHONE:
				base.append("`"+Columns.DR_PHONE+"` = '"+record.getDrPhone()+"', ");
				break;
			case Columns.DR_FAX:
				base.append("`"+Columns.DR_FAX+"` = '"+record.getDrFax()+"', ");
				break;
			case Columns.SSN:
				base.append("`"+Columns.SSN+"` = '"+record.getSsn()+"', ");
				break;
			case Columns.GENDER:
				base.append("`"+Columns.GENDER+"` = '"+record.getGender()+"',");
				break;
			case Columns.AFID:
				base.append("`"+Columns.AFID+"` = '"+afid+"', ");
				break;
			case Columns.PHARMACY:
				base.append("`"+Columns.PHARMACY+"` = '"+pharmacy+"', ");
				break;
			case Columns.AGENT:
				base.append("`"+Columns.AGENT+"` = '"+agent+"', ");
				break;
			case Columns.DATE_ADDED:
				base.append("`"+Columns.DATE_ADDED+"` = '"+getCurrentDate("yyyy-MM-dd")+"', ");
				break;
			case Columns.FAX_DISPOSITION:
				base.append("`"+Columns.FAX_DISPOSITION+"` = '', ");
				break;
			case Columns.MESSAGE_STATUS:
				base.append("`"+Columns.MESSAGE_STATUS+"` = '', ");
				break;
			case Columns.MESSAGE_ID:
				base.append("`"+Columns.MESSAGE_ID+"` = '', ");
				break;
			case Columns.FAX_ATTEMPTS:
				base.append("`"+Columns.FAX_ATTEMPTS+"` = 0, ");
				break;
			case Columns.CALL_CENTER:
				base.append("`"+Columns.CALL_CENTER+"` = '"+callCenter+"', ");
				break;
			case Columns.FAXES_SENT:
				base.append("`"+Columns.FAXES_SENT+"` = 0");
				break;
			}		
		}
		base.append(" WHERE `"+Columns.ID+"` = '"+record.getId()+"'");
		return base.toString();
	}
	
	public Record GetFullRecord(String id,String table) {
		String sql = "SELECT * FROM `"+table+"` WHERE `"+Columns.ID+"` = '"+id+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) 
				return new Record(set);
			else
				return null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
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
	public Record GetRecordById(String id,String table) {
		String sql = "SELECT * FROM `"+table+"` WHERE `"+Columns.ID+"` = '"+id+"'";
		Record record = null;
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) 
				record = new Record(set);
			return record;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
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
	public Record GetRecordByPhone(String phone,String table) {
		String sql = "SELECT * FROM `"+table+"` WHERE `"+Columns.PHONE_NUMBER+"` = '"+phone+"'";
		Record record = null;
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) 
				record = new Record(set);
			return record;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
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
	public int setCallBack(Record record,String table) {
		String sql = "Update `"+table+"` SET `"+Columns.CONFIRM_DOCTOR+"` = -1, `"+Columns.LAST_UPDATED+"` = CURRENT_TIMESTAMP WHERE `"+Columns.ID+"` = '"+record.getId()+"'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getErrorCode();
		} finally {
			try {
				if(stmt!=null) stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public int GetLiveLeadCount(String table,String roadmap,String pharmacy) {
		RoadMapClient map = new RoadMapClient(roadmap);
		String pharmacies = null;
		if(pharmacy==null)
			pharmacies = map.getPharmacyQueryForDrChase();
		else
			pharmacies = "`PHARMACY` = '"+pharmacy+"'";
		String sql = "SELECT * FROM `"+table+"` WHERE `CHASE_COUNT` < 20 AND `"+Columns.USED+"` = 0 AND (`"+Columns.FAX_DISPOSITION+"` = '' OR `"+Columns.FAX_DISPOSITION+"` = '"+FaxStatus.WRONG_FAX+"')"
				+ " AND (`"+Columns.CONFIRM_DOCTOR+"` = 0 OR (`"+Columns.CONFIRM_DOCTOR+"` = -1 AND `"+Columns.LAST_UPDATED+"` < DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL - 30 MINUTE))) AND"+pharmacies+" ORDER BY `DATE_ADDED` DESC";
		Statement stmt = null;
		ResultSet set = null;
		InfoDatabase info = new InfoDatabase();
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			int count = 0;
			while(set.next()) { 
				String zipcode = set.getString("zip");
				String state = set.getString("state");
				int offset = info.GetOffSet(zipcode,state);
				if(IsOpen(offset)) {
					count++;
				}
				else
					continue;
			}
			return count;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getErrorCode();
		} finally {
			try {
				if(set!=null) set.close();
				if(stmt!=null) stmt.close();
				if(info!=null)info.close();
				if(map!=null)map.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public int NoAnswerCount(String table,String roadmap,String pharmacy) {
		RoadMapClient map = new RoadMapClient(roadmap);
		String pharmacies = null;
		if(pharmacy==null)
			pharmacies = map.getPharmacyQueryForDrChase();
		else
			pharmacies = "`PHARMACY` = '"+pharmacy+"'";
		String sql = "SELECT * FROM `"+table+"` WHERE `FAX_DISPOSITION` = '' AND `CHASE_COUNT` >= 20 AND `CONFIRM_DOCTOR` <> 1 AND"+pharmacies;
		Statement stmt = null;
		ResultSet set = null;
		InfoDatabase info = new InfoDatabase();
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			int count = 0;
			while(set.next()) { 
				String zipcode = set.getString("zip");
				String state = set.getString("state");
				int offset = info.GetOffSet(zipcode,state);
				if(IsOpen(offset)) {
					count++;
				}
				else
					continue;
			}
			return count;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getErrorCode();
		} finally {
			try {
				if(set!=null) set.close();
				if(stmt!=null) stmt.close();
				if(info!=null)info.close();
				if(map!=null)map.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public int GetRefaxCount(String roadmap) {
		RoadMapClient map = new RoadMapClient(roadmap);
		String pharmacies = map.getPharmacyQueryForDrChase();
		String SEVEN_DAYS_OLD = "(`FAX_DISPOSITION` = '' AND `MESSAGE_STATUS` = 'Sent' AND `FAX_SENT_DATE` < DATE_ADD(CURDATE(), INTERVAL -5 DAY))";
		String SENDING_FAILED = "(`FAX_DISPOSITION` = '' AND `MESSAGE_STATUS` = 'SendingFailed' AND `FAX_ATTEMPTS` <= 3)";
		String refax = "SELECT COUNT(*) FROM `Leads` WHERE ("+SEVEN_DAYS_OLD+" OR "+SENDING_FAILED+") AND "+pharmacies+" ORDER BY `FAX_SENT_DATE` ASC";
		Statement stmt = null;
		ResultSet set = null;
		int count = 0;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(refax);
			if(set.next()) 
				count += set.getInt("COUNT(*)");
			return count;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getErrorCode();
		} finally {
			try {
				if(set!=null) set.close();
				if(stmt!=null) stmt.close();
				if(map!=null)map.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public int GetLiveCount(String roadmap) {
		RoadMapClient map = new RoadMapClient(roadmap);
		String pharmacies = map.getPharmacyQueryForDrChase();
		String live = "SELECT COUNT(*) FROM `Leads` WHERE `CHASE_COUNT` < 20  AND `"+Columns.USED+"` = 0 AND (`"+Columns.FAX_DISPOSITION+"` = '' OR `"+Columns.FAX_DISPOSITION+"` = '"+FaxStatus.WRONG_FAX+"')"
		+" AND (`"+Columns.CONFIRM_DOCTOR+"` = 0 OR (`"+Columns.CONFIRM_DOCTOR+"` = -1 AND `"+Columns.LAST_UPDATED+"` < DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL - 60 MINUTE))) AND"+pharmacies+" ORDER BY `DATE_ADDED` DESC";
		Statement stmt = null;
		ResultSet set = null;
		int count = 0;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(live);
			if(set.next()) 
				count += set.getInt("COUNT(*)");
			return count;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getErrorCode();
		} finally {
			try {
				if(set!=null) set.close();
				if(stmt!=null) stmt.close();
				if(map!=null)map.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public int GetTotalBlank(String roadmap) {
		RoadMapClient map = new RoadMapClient(roadmap);
		String pharmacies = map.getPharmacyQueryForDrChase();
		String sql = "SELECT COUNT(*) FROM `Leads` WHERE `FAX_DISPOSITION` = '' AND"+pharmacies;
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next())
				return set.getInt("COUNT(*)");
			else 
				return 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} finally {
			try {
				if(set!=null) set.close();
				if(stmt!=null) stmt.close();
				if(map!=null)map.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public synchronized Record GetLiveLead(String table,String agent,String roadmap) {
		RoadMapClient map = new RoadMapClient(roadmap);
		String pharmacies = map.getPharmacyQueryForDrChase();
		String sql = "SELECT * FROM `"+table+"` WHERE (`EMDEON_STATUS` = 'FOUND' AND `carrier` <> 'SilverScripts/Wellcare') AND `CHASE_COUNT` < 20  AND `"+Columns.USED+"` = 0 AND (`"+Columns.FAX_DISPOSITION+"` = '' OR `"+Columns.FAX_DISPOSITION+"` = '"+FaxStatus.WRONG_FAX+"')"
				+ " AND (`"+Columns.CONFIRM_DOCTOR+"` = 0 OR (`"+Columns.CONFIRM_DOCTOR+"` = -1 AND `"+Columns.LAST_UPDATED+"` < DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL - 60 MINUTE))) AND"+pharmacies+" ORDER BY `DATE_ADDED` DESC";
		Record record = null;
		Statement stmt = null;
		ResultSet set = null;
		InfoDatabase info = new InfoDatabase();
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			while(set.next()) { 
				String zipcode = set.getString("zip");
				String state = set.getString("state");
				int offset = info.GetOffSet(zipcode,state);
				if(IsOpen(offset)) {
					record = new Record(set);
					record.record_type = "Live";
					setUsed(record.getFirstName()+""+record.getLastName()+""+record.getPhone(),table,1,agent);
					return record;
				}
				else
					continue;
			}
			return null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			try {
				if(set!=null) set.close();
				if(stmt!=null) stmt.close();
				if(info!=null)info.close();
				if(map!=null)map.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private boolean IsOpen(int offset) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"); 
		String date = getCurrentDate("yyyy/MM/dd");
		String open = date+" 09:00:00";
		String close = date+" 18:00:00";
		LocalDateTime openingTime = LocalDateTime.parse(open, dtf);
		LocalDateTime closingTime = LocalDateTime.parse(close, dtf);
		LocalDateTime currentTime = LocalDateTime.now().minusHours(offset);
		if(currentTime.isAfter(openingTime) && currentTime.isBefore(closingTime))
			return true;
		else 
			return false;
	}
	
	public synchronized Record ReFax(String table,String agent,String roadmap) {
		RoadMapClient map = new RoadMapClient(roadmap);
		String pharmacies = map.getPharmacyQueryForDrChase();
		String confirm_and_recall = "(`CONFIRM_DOCTOR` = 1 OR (`CONFIRM_DOCTOR` = -1 AND `LAST_UPDATED` < DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL - 30 MINUTE)))";
		String SEVEN_DAYS_OLD = "(`FAX_DISPOSITION` = '' AND `MESSAGE_STATUS` = 'Sent' AND `FAX_SENT_DATE` < DATE_ADD(CURDATE(), INTERVAL -5 DAY))";
		String SENDING_FAILED = "(`FAX_DISPOSITION` = '' AND `MESSAGE_STATUS` = 'SendingFailed' AND `FAX_ATTEMPTS` <= 3)";
		String sql = "SELECT * FROM `"+table+"` WHERE (`EMDEON_STATUS` = 'FOUND' AND `carrier` <> 'SilverScripts/Wellcare') AND "+confirm_and_recall+" AND ("+SEVEN_DAYS_OLD+" OR "+SENDING_FAILED+") AND `"+Columns.DR_CHASE_AGENT+"` = '"+agent+"' AND "+pharmacies+" ORDER BY `FAX_SENT_DATE` ASC";
		Record record = null;
		Statement stmt = null;
		ResultSet set = null;
		InfoDatabase info = new InfoDatabase();
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			while(set.next()) { 
				String zipcode = set.getString("zip");
				String state = set.getString("state");
				int offset = info.GetOffSet(zipcode,state);
				if(IsOpen(offset)) {
					record = new Record(set);
					record.record_type = "ReFax";
					setUsed(record.getFirstName()+""+record.getLastName()+""+record.getPhone(),table,1,agent);
					return record;
				}
				else
					continue;
			}
			return null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			try {
				if(set!=null) set.close();
				if(stmt!=null) stmt.close();
				if(info!=null)info.close();
				if(map!=null)map.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public synchronized Record ReFaxNotTaggedAgent(String table,String agent,String roadmap) {
		RoadMapClient map = new RoadMapClient(roadmap);
		String pharmacies = map.getPharmacyQueryForDrChase();
		String confirm_and_recall = "(`CONFIRM_DOCTOR` = 1 OR (`CONFIRM_DOCTOR` = -1 AND `LAST_UPDATED` < DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL - 30 MINUTE)))";
		String SEVEN_DAYS_OLD = "(`FAX_DISPOSITION` = '' AND `MESSAGE_STATUS` = 'Sent' AND `FAX_SENT_DATE` < DATE_ADD(CURDATE(), INTERVAL -5 DAY))";
		String SENDING_FAILED = "(`FAX_DISPOSITION` = '' AND `MESSAGE_STATUS` = 'SendingFailed' AND `FAX_ATTEMPTS` <= 3)";
		String sql = "SELECT * FROM `"+table+"` WHERE (`EMDEON_STATUS` = 'FOUND' AND `carrier` <> 'SilverScripts/Wellcare') AND "+confirm_and_recall+" AND ("+SEVEN_DAYS_OLD+" OR "+SENDING_FAILED+") AND "+pharmacies+" ORDER BY `FAX_SENT_DATE` ASC";
		Record record = null;
		Statement stmt = null;
		ResultSet set = null;
		InfoDatabase info = new InfoDatabase();
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			while(set.next()) { 
				String zipcode = set.getString("zip");
				String state = set.getString("state");
				int offset = info.GetOffSet(zipcode,state);
				if(IsOpen(offset)) {
					record = new Record(set);
					record.record_type = "ReFax Not Tagged By Agent";
					setUsed(record.getFirstName()+""+record.getLastName()+""+record.getPhone(),table,1,agent);
					return record;
				}
				else
					continue;
			}
			return null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			try {
				if(set!=null) set.close();
				if(stmt!=null) stmt.close();
				if(info!=null)info.close();
				if(map!=null)map.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public int GetReFaxCount(String table,String roadmap,String pharmacy) {
		RoadMapClient map = new RoadMapClient(roadmap);
		String pharmacies = null;
		if(pharmacy==null)
			pharmacies = map.getPharmacyQueryForDrChase();
		else
			pharmacies = "`PHARMACY` = '"+pharmacy+"'";
		String SEVEN_DAYS_OLD = "(`FAX_DISPOSITION` = '' AND `MESSAGE_STATUS` = 'Sent' AND `FAX_SENT_DATE` < DATE_ADD(CURDATE(), INTERVAL -5 DAY))";
		String SENDING_FAILED = "(`FAX_DISPOSITION` = '' AND `MESSAGE_STATUS` = 'SendingFailed' AND `FAX_ATTEMPTS` <= 3)";
		String sql = "SELECT * FROM `"+table+"` WHERE ("+SEVEN_DAYS_OLD+" OR "+SENDING_FAILED+") AND "+pharmacies+" ORDER BY `FAX_SENT_DATE` ASC";
		Statement stmt = null;
		ResultSet set = null;
		InfoDatabase info = new InfoDatabase();
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			int count = 0;
			while(set.next()) { 
				String zipcode = set.getString("zip");
				String state = set.getString("state");
				int offset = info.GetOffSet(zipcode,state);
				if(IsOpen(offset)) 
					count++;
				else
					continue;
			}
			return count;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return e.getErrorCode();
		} finally {
			try {
				if(set!=null) set.close();
				if(stmt!=null) stmt.close();
				if(info!=null)info.close();
				if(map!=null)map.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public int setUsed(String id,String table,int use,String agent) {
		String sql = "UPDATE `"+table+"` SET `"+Columns.USED+"` = "+use+", `"+Columns.DR_CHASE_AGENT+"` = '"+agent+"' WHERE `"+Columns.ID+"` = '"+id+"'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} finally {
			try {
				if(stmt!=null) stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public int IncrementChaseCount(Record record) {
		String sql = "UPDATE `Leads` SET `CHASE_COUNT` = `CHASE_COUNT`+1 WHERE `"+Columns.ID+"` = '"+record.getId()+"'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} finally {
			try {
				if(stmt!=null) stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public int UpdateMessageId(Record record,String table,String messageId,String status) {
		String sql = "UPDATE `"+table+"` SET `"+Columns.MESSAGE_ID+"` = '"+messageId+"', `"+Columns.MESSAGE_STATUS+"` = '"+status+"', `"+Columns.FAX_SENT_DATE+"` = '"+getCurrentDate("yyyy-MM-dd")+"' "
				+ "WHERE `"+Columns.ID+"` = '"+record.getId()+"'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} finally {
			try {
				if(stmt!=null) stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public int UpdateMessageIdNewFax(Record record,String messageId) {
		String sql = "UPDATE `Leads` SET `"+Columns.MESSAGE_ID+"` = '"+messageId+"', `"+Columns.MESSAGE_STATUS+"` = '"+MessageStatus.QUEUED+"', `"+Columns.FAX_SENT_DATE+"` = '"+getCurrentDate("yyyy-MM-dd")+"' "
				+ "WHERE `"+Columns.ID+"` = '"+record.getId()+"'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} finally {
			try {
				if(stmt!=null) stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private int DeleteRecord(String id) {
		String sql = "DELETE FROM `Leads` WHERE `"+Columns.ID+"` = '"+id+"'";
		Statement stmt = null;
		try {
			 stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch(SQLException ex) {
			return ex.getErrorCode();
		} catch(NullPointerException ex) {
			return -3;
		} finally {
			try {
				if(stmt!=null) stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public int UpdateRecordForDr(Record record,String table,int used) {
		StringBuilder base = new StringBuilder("UPDATE `"+table+"` SET ");
		for(String s: Columns.DR_HEADERS) {
			switch(s) {
				case Columns.NPI:
					base.append("`"+Columns.NPI+"` = '"+record.getNpi()+"', ");
					break;
				case Columns.DR_FIRST:
					base.append("`"+Columns.DR_FIRST+"` = '"+record.getDrFirst()+"', ");
					break;
				case Columns.DR_LAST:
					base.append("`"+Columns.DR_LAST+"` = '"+record.getDrLast()+"', ");
					break;
				case Columns.DR_ADDRESS1:
					base.append("`"+Columns.DR_ADDRESS1+"` = '"+record.getDrAddress()+"', ");
					break;
				case Columns.DR_CITY:
					base.append("`"+Columns.DR_CITY+"` = '"+record.getDrCity()+"', ");
					break;
				case Columns.DR_STATE:
					base.append("`"+Columns.DR_STATE+"` = '"+record.getDrState()+"', ");
					break;
				case Columns.DR_ZIP:
					base.append("`"+Columns.DR_ZIP+"` = '"+record.getDrZip()+"', ");
					break;
				case Columns.DR_PHONE:
					base.append("`"+Columns.DR_PHONE+"` = '"+record.getDrPhone()+"', ");
					break;
				case Columns.DR_FAX:
					base.append("`"+Columns.DR_FAX+"` = '"+record.getDrFax()+"', ");
					break;
				case Columns.FAX_DISPOSITION:
					base.append("`"+Columns.FAX_DISPOSITION+"` = '"+record.getFaxDisposition()+"', ");
					break;
				case Columns.FAX_DISPOSITION_DATE:
					switch(record.getFaxDisposition()) {
						case FaxStatus.DENIED:
						case FaxStatus.NEEDS_TO_BE_SEEN:
						case FaxStatus.DECEASED:
						case FaxStatus.ESCRIBE:
						case FaxStatus.WRONG_DOCTOR:
						case FaxStatus.NOT_INTERESTED:
						case FaxStatus.PAIN_MANAGEMENT:
							base.append("`"+Columns.FAX_DISPOSITION_DATE+"` = '"+getCurrentDate("yyyy-MM-dd")+"', ");
							break;
						default:
							base.append("`"+Columns.FAX_DISPOSITION_DATE+"` = '0000-00-00', ");
							break;
					
					}
					break;
				case Columns.USED:
					base.append("`"+Columns.USED+"` = "+used+", ");
					break;
				case Columns.CONFIRM_DOCTOR:
					base.append("`"+Columns.CONFIRM_DOCTOR+"` = "+toInt(record.isDoctorConfirmed()));
					break;
			}
		}
		base.append(" WHERE `"+Columns.ID+"` = '"+record.getId()+"'");
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(base.toString());
		} catch(SQLException ex) {
			ex.printStackTrace();
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
	public int UpdateChaseDate(Record record,String table) {
		String sql = "UPDATE `"+table+"` SET `LAST_CHASE_DATE` = '"+getCurrentDate("yyyy-MM-dd")+"' WHERE `"+Columns.ID+"` = '"+record.getId()+"'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch(SQLException ex) {
			return ex.getErrorCode();
		} finally {
			try {
				if(stmt!=null)stmt.close();
			} catch(SQLException ex) {
				
			}
		}
	}
	public int ResetMessageStatus(Record record,String table) {
		String sql = "UPDATE `"+table+"` SET `"+Columns.MESSAGE_STATUS+"` = '', `"+Columns.FAXES_SENT+"` = 0, `"+Columns.FAX_DISPOSITION+"` = '', `"+Columns.FAX_ATTEMPTS+"` = 0 WHERE `"+Columns.ID+"` = '"+record.getId()+"'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch(SQLException ex) {
			return ex.getErrorCode();
		} finally {
			try {
				if(stmt!=null)stmt.close();
			} catch(SQLException ex) {
				
			}
		}
	}
	private int toInt(boolean bool) {
		if(bool)
			return 1;
		else
			return 0;
	}
	public JSONObject AddToTelmed(Record record,String ip,String pharmacy,String callCenter) throws JSONException {
		String sql = "INSERT INTO `TELMED` (`first_name`,`last_name`,`dob`,`address`,`city`,`state`,`zip`,`phonenumber`,`gender`,`ssn`,`agent`,`TELMED_STATUS`,`ip`,`carrier`,`insurance_name`,`policy_id`,`bin`,`grp`,`pcn`,`DATE_MODIFIED`,`DATE_ADDED`,`PHARMACY`,`CALL_CENTER`,`SOURCE`,`NOTES`,`INSURANCE_TYPE`,`NPI`) "
				+ "VALUES ('"+record.getFirstName()+"','"+record.getLastName()+"','"+record.getDob()+"','"+record.getAddress()+"','"+record.getCity()+"','"+record.getState()+"','"+record.getZip()+"','"+record.getPhone()+"','"+record.getGender()+"','"+record.getSsn()+"','"+record.getAgent()+"',"
				+ "'New Patient','"+ip+"', '"+record.getCarrier()+"', '"+record.getInsuranceName()+"','"+record.getPolicyId()+"','"+record.getBin()+"','"+record.getGrp()+"','"+record.getPcn()+"','"+getCurrentDate("yyyy-MM-dd")+"','"+getCurrentDate("yyyy-MM-dd")+"','"+pharmacy+"','"+callCenter+"','"+record.getSource()+"','','"+record.getType()+"','"+record.getNpi()+"')";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			int value =  stmt.executeUpdate(sql);
			return new JSONObject().put("value",value);
		} catch(SQLException ex) {
			return new JSONObject().put("value", ex.getErrorCode()).put("message", ex.getMessage());
		}  finally {
			try {
				if(stmt!=null) stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public JSONObject GetTriage(String phone_number) throws JSONException {
		String sql = "SELECT * FROM `TELMED` WHERE `phonenumber` = '"+phone_number+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) {
				String json = set.getString("TRIAGE");
				if(json.equalsIgnoreCase(""))
					return new JSONObject()
							.put("success", false)
							.put("error", 0)
							.put("message", "NO TRIAGE SAVED");
				else
					return new JSONObject(json).put("success", true);
			}
			else
				return new JSONObject()
						.put("success", false)
						.put("error", 0)
						.put("message", "RECORD NOT FOUND");
		} catch(SQLException ex) {
			return new JSONObject()
					.put("success", false)
					.put("error", ex.getErrorCode())
					.put("message", ex.getMessage());
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
	public JSONObject AddToTelmedBlueMosiac(JSONObject obj,String id,String ip,String callCenter,String status) throws JSONException {
		Statement stmt = null;
		try {
				String sql = "INSERT INTO `TELMED` (`first_name`,`last_name`,`dob`,`address`,`city`,`state`,`zip`,`phonenumber`,`gender`,`ssn`,`agent`,`TELMED_STATUS`,`ip`,`carrier`,`insurance_name`,`policy_id`,`bin`,`grp`,`pcn`,`DATE_MODIFIED`,`DATE_ADDED`,`PHARMACY`,`CALL_CENTER`,`SOURCE`,`TRIAGE`) "
						+ "VALUES ('"+obj.getString("first_name")+"','"+obj.getString("last_name")+"','"+obj.getString("date_of_birth")+"','"+obj.getString("street_address")+"','"+obj.getString("city")+"','"+obj.getString("state")+"','"+obj.getString("zip_code")+"','"+obj.getString("phone_number")+"','"+obj.getString("gender")+"','"+obj.getString("ssn")
						+"','"+obj.getString("agent")+"',"+ "'"+status+"','"+ip+"', '"+obj.getString("insurance")+"', '"+obj.getString("carrier")+"','"+obj.getString("rx_insurance_id")+"','"+obj.getString("bin")+"','"+obj.getString("rx_group")+"','"+obj.getString("insurance_pcn")+"','"+getCurrentDate("yyyy-MM-dd")+"','"+getCurrentDate("yyyy-MM-dd")+"','"
						+obj.getString("pharmacy")+"','"+callCenter+"','"+obj.getString("source")+"','"+obj.toString()+"')";
				stmt = connect.createStatement();
				int value = stmt.executeUpdate(sql);
				if(value==1)
					return TelmedResponse.BuildSuccessfulResponse(obj);
				else 
					return TelmedResponse.BuildFailedResponse(value);
			} catch(SQLException ex) {
				return TelmedResponse.BuildFailedResponse(ex.getMessage(),ex.getErrorCode());
			} catch (JSONException ex) {
				// TODO Auto-generated catch block
				return TelmedResponse.BuildFailedResponse(ex.getMessage());
			}  finally {
				try {
					if(stmt!=null) stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
	}
	public String AddToTelmedString(Record record,String ip,String pharmacy,String callCenter) {
		String sql = "INSERT INTO `TELMED` (`first_name`,`last_name`,`dob`,`address`,`city`,`state`,`zip`,`phonenumber`,`gender`,`ssn`,`agent`,`TELMED_STATUS`,`ip`,`carrier`,`insurance_name`,`bin`,`grp`,`pcn`,`DATE_MODIFIED`,`DATE_ADDED`,`PHARMACY`,`CALL_CENTER`,`SOURCE`,`NOTES`) "
				+ "VALUES ('"+record.getFirstName()+"','"+record.getLastName()+"','"+record.getDob()+"','"+record.getAddress()+"','"+record.getCity()+"','"+record.getState()+"','"+record.getZip()+"','"+record.getPhone()+"','"+record.getGender()+"','"+record.getSsn()+"','"+record.getAgent()+"',"
				+ "'New Patient','"+ip+"', '"+record.getCarrier()+"', '"+record.getInsuranceName()+"','"+record.getBin()+"','"+record.getGrp()+"','"+record.getPcn()+"','"+getCurrentDate("yyyy-MM-dd")+"','"+getCurrentDate("yyyy-MM-dd")+"','"+pharmacy+"','"+callCenter+"','"+record.getSource()+"','')";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			int value =stmt.executeUpdate(sql);
			return ""+value;
		} catch(SQLException ex) {
			return ex.getMessage();
		}  finally {
			try {
				if(stmt!=null) stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public String getAgent(String recordId) {
		String sql = "SELECT `agent` FROM `TELMED` WHERE `RECORD_ID` = '"+recordId+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) 
				return set.getString("agent");
			else
				return "";
		} catch(SQLException ex) {
			return ex.getMessage();
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
	public int setUsed(String id,int used) {
		String sql = "UPDATE `Leads` SET `"+Columns.USED+"` = "+used+" WHERE `"+Columns.ID+"` = '"+id+"'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} finally {
			try {
				if(stmt!=null) stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public int resetUsedForAgent(String agent) {
		String sql = "UPDATE `Leads` SET `"+Columns.USED+"` = 0 WHERE `"+Columns.DR_CHASE_AGENT+"` = '"+agent+"'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} finally {
			try {
				if(stmt!=null) stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public int SetRecordId(String recordID, String phone) {
		String sql = "UPDATE `TELMED` SET `RECORD_ID` = '"+recordID+"' WHERE `phonenumber` = '"+phone+"'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch(SQLException ex) {
			return -1;
		} finally {
			try {
				if(stmt!=null) stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public boolean CheckTelmedDuplicate(String phone) {
		String sql = "SELECT * FROM `TELMED` WHERE `phonenumber` = '"+phone+"'";
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
			return true;
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
	public JSONObject UpdateTriage(String phone,JSONObject obj,String status,String id) throws JSONException {
		String sql = "UPDATE `TELMED` SET `TRIAGE` = '"+obj.toString()+"',`TELMED_STATUS` = '"+status+"', `TELMED_ID` = '"+id+"' WHERE `phonenumber` = '"+phone+"'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			int value =  stmt.executeUpdate(sql);
			if(value>0)
				return TelmedResponse.BuildSuccessfulResponse(obj);
			else
				return TelmedResponse.BuildFailedResponse(value);
		} catch(SQLException ex) {
			return TelmedResponse.BuildFailedResponse(ex.getMessage());
		} finally {
			try {
				if(stmt!=null) stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public boolean IsIncompleteTriage(String phone) {
		String sql = "SELECT * FROM `TELMED` WHERE `phonenumber` = '"+phone+"' AND `TELMED_STATUS` = 'Triage Incomplete'";
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
			return true;
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
	public JSONObject GetDuplicateResponse(String phone) throws JSONException {
		String sql = "SELECT * FROM `TELMED` WHERE `phonenumber` = '"+phone+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) {
				String first = set.getString("first_name");
				String last = set.getString("last_name");
				return TelmedResponse.BuildFailedResponse(TelmedResponse.Errors.DUPLICATE,first,last);
			}
			else 
				return null;
		} catch(SQLException ex) {
			return TelmedResponse.BuildFailedResponse(ex.getMessage(),"","");
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
	private String getCurrentDate(String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format); 
		Date date = new Date(); 
		return formatter.format(date);
	}
	public int UpdateTelemdID(Record record, String id) {
		String sql = "UPDATE `TELMED` SET `TELMED_ID` = '"+id+"' WHERE `phonenumber` = '"+record.getPhone()+"'";
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
		String sql = "UPDATE `TELMED` SET `TELMED_ID` = '"+id+"',"+NOTES+DATE_MODIFIED+"`TELMED_STATUS` = '"+status+"',"+COST+"WHERE `phonenumber` = '"+phone+"'";
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
	
	public String GetTelmedStatus(String phone) {
		String sql = "SELECT * FROM `TELMED` WHERE `phonenumber` = '"+phone+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			StringBuilder sb = new StringBuilder();
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) { 
				String first = set.getString(TelmedColumns.FIRST_NAME);
				String last = set.getString(TelmedColumns.LAST_NAME);
				String id = set.getString(TelmedColumns.TELMED_ID);
				String phonenumber = set.getString(TelmedColumns.PHONE_NUMBER);
				sb.append(first+" "+last+"("+id+"): "+phonenumber+"\r\n");
				sb.append(set.getString("TELMED_STATUS")+"\r\n");
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
	public int AddFaxedRecord(Record record,String messageId,String agent,String record_type) {
		String sql = "INSERT INTO `FAXED` (`_id`,`"+Columns.PHONE_NUMBER+"`,`"+Columns.MESSAGE_ID+"`,`STATUS`,`AGENT`,`RECEIVED`,`CALL_BACK`,`FAX_DISPOSITION`,`RECORD_TYPE`,`PHARMACY`) VALUES ('"+record.getId()+"','"+record.getPhone()+"','"+messageId+"','"+MessageStatus.QUEUED+"','"+agent+"',0,0,'','"+record_type+"','"+record.getPharmacy()+"')";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch(SQLException ex) {
			if(ex.getErrorCode()==1062)
				return UpdatedFaxedRecod(record,messageId,agent);
			return ex.getErrorCode();
		} finally {
			try {
				if(stmt!=null)stmt.close();
			} catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
			
	}
	public int UpdatedFaxedRecod(Record record,String messageId,String agent) {
		String sql = "UPDATE `FAXED` SET `STATUS` = 'Queued', `"+Columns.MESSAGE_ID+"` = '"+messageId+"' WHERE `"+Columns.PHONE_NUMBER+"` = '"+record.getPhone()+"'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch(SQLException ex) {
			return ex.getErrorCode();
		} finally {
			try {
				if(stmt!=null)stmt.close();
			} catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	public String GetSentRecord(String agent) {
		String callback =  GetCallBack(agent);
		if(callback!=null)
			return callback;
		String sql= "SELECT * FROM `FAXED` WHERE `STATUS` = 'Sent' AND `CALL_BACK` = 0 AND `RECEIVED` = 0 AND `FAX_DISPOSITION` = '' AND `AGENT` = '"+agent+"' ORDER BY `LAST_UPDATED` ASC";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next())
				return set.getString(Columns.PHONE_NUMBER);
			else 
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
	public String GetCallBack(String agent) {
		String sql= "SELECT * FROM `FAXED` WHERE `STATUS` = 'Sent' AND `RECEIVED` = 0 AND `CALL_BACK` = 1 AND `AGENT` = '"+agent+"' AND `LAST_UPDATED` <= DATE_ADD(CURRENT_TIMESTAMP, INTERVAL - 30 MINUTE) ORDER BY `LAST_UPDATED` ASC";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next())
				return set.getString(Columns.PHONE_NUMBER);
			else 
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
	public int SetReceived(String phone) {
		String sql = "UPDATE `FAXED` SET `RECEIVED` = 1, `CALL_BACK` = 0 WHERE `phonenumber` = '"+phone+"'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch(SQLException ex) {
			return ex.getErrorCode();
		} finally {
			try {
				if(stmt!=null)stmt.close();
			} catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	public int SetFaxedDisposition(String phone,String disposition) {
		String sql = "UPDATE `FAXED` SET `FAX_DISPOSITION` = '"+disposition+"' WHERE `phonenumber` = '"+phone+"'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch(SQLException ex) {
			return ex.getErrorCode();
		} finally {
			try {
				if(stmt!=null)stmt.close();
			} catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	public int DeleteFaxedRecord(String phone) {
		String sql = "DELETE FROM `FAXED` WHERE `"+Columns.PHONE_NUMBER+"` = '"+phone+"'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch(SQLException ex) {
			return ex.getErrorCode();
		} finally {
			try {
				if(stmt!=null)stmt.close();
			} catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	public boolean IsAlreadyFaxing(String id) {
		String sql = "SELECT * FROM `FAXED` WHERE `"+Columns.ID+"` = '"+id+"' AND (`STATUS` = '"+MessageStatus.QUEUED+"' OR `STATUS` = '"+MessageStatus.SENDING_FAILED+"')";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next())
				return true;
			else
				return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return false;
		} finally {
			try {
				if(set!=null)set.close();
				if(stmt!=null)stmt.close();
			} catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	public int UpdateFaxedRecord(FaxedRecord record) {
		String sql = "UPDATE `FAXED` SET `MESSAGE_ID` = '"+record.getMessage_id()+"',`Status` = '"+record.getStatus()+"' WHERE `"+Columns.PHONE_NUMBER+"` =  '"+record.getPhone()+"'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			if(record.getStatus().equalsIgnoreCase(MessageStatus.SENT)) 
				incrementFaxSent(record);
			else if(record.getStatus().equalsIgnoreCase(MessageStatus.SENDING_FAILED))
				incrementFaxAttempt(record);
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return e.getErrorCode();
		} finally {
			try {
				if(stmt!=null)stmt.close();
			} catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	public boolean Attempted5Times(FaxedRecord record) {
		String sql = "SELECT * FROM `FAXED` WHERE `phonenumber` = '"+record.getPhone()+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) {
				if(set.getInt("FAX_ATTEMPTS")>=5)
					return true;
				else
					return false;
			}
			else 
				return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
	public int UnconfirmDoctor(FaxedRecord record) {
		String sql = "UPDATE `Leads` SET `"+Columns.CONFIRM_DOCTOR+"` = 0, `"+Columns.USED+"` = 0 WHERE `"+Columns.ID+"` = '"+record.getId()+"'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return e.getErrorCode();
		} finally {
			try {
				if(stmt!=null)stmt.close();
			} catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	public int UpdateMessageStatus(FaxedRecord record) {
		String sql = "UPDATE `Leads` SET `"+Columns.MESSAGE_STATUS+"` = '"+record.getStatus()+"', `"+Columns.FAX_SENT_DATE+"` = '"+getCurrentDate("yyyy-MM-dd")+"' WHERE `"+Columns.PHONE_NUMBER+"` =  '"+record.getPhone()+"'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			if(record.getStatus().equalsIgnoreCase(MessageStatus.SENT)) 
				incrementFaxSent(record);
			else if(record.getStatus().equalsIgnoreCase(MessageStatus.SENDING_FAILED))
				incrementFaxAttempt(record);
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return e.getErrorCode();
		} finally {
			try {
				if(stmt!=null)stmt.close();
			} catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	private int incrementFaxSent(FaxedRecord record) {
		String sql = "UPDATE `Leads` SET `"+Columns.FAXES_SENT+"` = `"+Columns.FAXES_SENT+"` + 1 WHERE "
				+ "`"+Columns.PHONE_NUMBER+"` = '"+record.getPhone()+"'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			int increment = stmt.executeUpdate(sql);
			if(increment>0)
				resetFaxAttempts(record);
			return increment;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return e.getErrorCode();
		} finally {
			try {
				if(stmt!=null)stmt.close();
			} catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	private int incrementFaxAttempt(FaxedRecord record) {
		String sql = "UPDATE `Leads` SET `"+Columns.FAX_ATTEMPTS+"` = `"+Columns.FAX_ATTEMPTS+"` + 1 WHERE "
				+ "`"+Columns.PHONE_NUMBER+"` = '"+record.getPhone()+"'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return e.getErrorCode();
		} finally {
			try {
				if(stmt!=null)stmt.close();
			} catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	private int resetFaxAttempts(FaxedRecord record) {
 		String sql = "UPDATE `Leads` SET `"+Columns.FAX_ATTEMPTS+"` = 0 WHERE "
				+ "`"+Columns.PHONE_NUMBER+"` = '"+record.getPhone()+"'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "ERROR "+e.getMessage());
			return 0;
		} finally {
			try {
				if(stmt!=null)stmt.close();
			} catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
 	}
	public List<FaxedRecord> getMessageIds(String agent) {
		String sql = "SELECT * FROM `FAXED` WHERE `AGENT` = '"+agent+"' AND  `LAST_UPDATED` <= DATE_ADD(CURRENT_TIMESTAMP, INTERVAL - 5 MINUTE)";
		Statement stmt = null;
		ResultSet set = null;
		List<FaxedRecord> list = new ArrayList<FaxedRecord>();
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			while(set.next()) {
				String phone = set.getString("phonenumber");
				String message_id = set.getString("MESSAGE_ID");
				String status = set.getString("STATUS");
				String id = set.getString("_id");
				String record_type = set.getString("RECORD_TYPE");
				String pharmacy = set.getString("PHARMACY");
				list.add(new FaxedRecord(id,phone,message_id,status,record_type,pharmacy));
			}
			return list;
		} catch(SQLException ex) {
			return null;
		} finally {
			try {
				if(stmt!=null)stmt.close();
			} catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	public List<FaxedRecord> getAllFaxed() {
		String sql = "SELECT * FROM `FAXED`";
		Statement stmt = null;
		ResultSet set = null;
		List<FaxedRecord> list = new ArrayList<FaxedRecord>();
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			while(set.next()) {
				String phone = set.getString("phonenumber");
				String message_id = set.getString("MESSAGE_ID");
				String status = set.getString("STATUS");
				String id = set.getString("_id");
				String record_type = set.getString("RECORD_TYPE");
				String pharmacy = set.getString("PHARMACY");
				list.add(new FaxedRecord(id,phone,message_id,status,record_type,pharmacy));
			}
			return list;
		} catch(SQLException ex) {
			return null;
		} finally {
			try {
				if(stmt!=null)stmt.close();
			} catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	public List<FaxedRecord> getSendingFailed() {
		String sql = "SELECT * FROM `FAXED` WHERE `STATUS` = '"+MessageStatus.SENDING_FAILED+"' AND `FAX_ATTEMPTS` <= 5";
		Statement stmt = null;
		ResultSet set = null;
		List<FaxedRecord> list = new ArrayList<FaxedRecord>();
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			while(set.next()) {
				String phone = set.getString("phonenumber");
				String message_id = set.getString("MESSAGE_ID");
				String status = set.getString("STATUS");
				String id = set.getString("_id");
				String record_type = set.getString("RECORD_TYPE");
				String pharmacy = set.getString("PHARMACY");
				list.add(new FaxedRecord(id,phone,message_id,status,record_type,pharmacy));
			}
			return list;
		} catch(SQLException ex) {
			return null;
		} finally {
			try {
				if(stmt!=null)stmt.close();
			} catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	public List<FaxedRecord> getQueued() {
		String sql = "SELECT * FROM `FAXED` WHERE `STATUS` = '"+MessageStatus.QUEUED+"' AND `LAST_UPDATED` <= DATE_ADD(CURRENT_TIMESTAMP, INTERVAL - 5 MINUTE)";
		Statement stmt = null;
		ResultSet set = null;
		List<FaxedRecord> list = new ArrayList<FaxedRecord>();
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			while(set.next()) {
				String phone = set.getString("phonenumber");
				String message_id = set.getString("MESSAGE_ID");
				String status = set.getString("STATUS");
				String id = set.getString("_id");
				String record_type = set.getString("RECORD_TYPE");
				String pharmacy = set.getString("PHARMACY");
				list.add(new FaxedRecord(id,phone,message_id,status,record_type,pharmacy));
			}
			return list;
		} catch(SQLException ex) {
			return null;
		} finally {
			try {
				if(stmt!=null)stmt.close();
			} catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	public int IncrementFax(String phone) {
		String sql = "UPDATE `FAXED` SET `FAX_ATTEMPTS` = `FAX_ATTEMPTS`+1 WHERE `"+Columns.PHONE_NUMBER+"` = '"+phone+"'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch(SQLException ex) {
			return ex.getErrorCode();
		} finally {
			try {
				if(stmt!=null)stmt.close();
			} catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public boolean IsTelmedDuplicate(String phone) {
		String sql = "SELECT * FROM `TELMED` WHERE `"+TelmedColumns.PHONE_NUMBER+"` = '"+phone+"'";
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
			ex.printStackTrace();
			return false;
		} finally {
			try {
				if(stmt!=null) stmt.close();
				if(set!=null) set.close();
			} catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	public boolean CanEnroll(Record record) {
		String sql = "SELECT * FROM `TELMED` WHERE `"+TelmedColumns.PHONE_NUMBER+"` = '"+record.getPhone()+"' AND `"+TelmedColumns.DOB+"` = '"+record.getDob()+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) {
				String status = set.getString(TelmedColumns.TELMED_STATUS);
				if(TelmedStatus.IfCanEnroll(status))
					return true;
				else if(status.equalsIgnoreCase(TriageParameters.TRIAGE_COMPLETE) || status.equalsIgnoreCase(TriageParameters.TRIAGE_INCOMPLETE))
					return true;
				else
					return false;
			}
			else
				return false;
		} catch(SQLException ex) {
			ex.printStackTrace();
			return false;
		} finally {
			try {
				if(stmt!=null) stmt.close();
				if(set!=null) set.close();
			} catch(SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	public void close() {
		try {
			connect.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized Record GetLiveLeadDME(String table,String agent) {
		String sql = "SELECT * FROM `"+table+"` WHERE `CHASE_COUNT` < 20  AND `"+Columns.USED+"` = 0 AND (`"+Columns.FAX_DISPOSITION+"` = '' OR `"+Columns.FAX_DISPOSITION+"` = '"+FaxStatus.WRONG_FAX+"')"
				+ " AND (`"+Columns.CONFIRM_DOCTOR+"` = 0 OR (`"+Columns.CONFIRM_DOCTOR+"` = -1 AND `"+Columns.LAST_UPDATED+"` < DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL - 60 MINUTE))) ORDER BY `DATE_ADDED` DESC";
		Record record = null;
		Statement stmt = null;
		ResultSet set = null;
		InfoDatabase info = new InfoDatabase();
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			while(set.next()) { 
				String zipcode = set.getString("zip");
				String state = set.getString("state");
				int offset = info.GetOffSet(zipcode,state);
				if(IsOpen(offset)) {
					record = new Record(set);
					record.record_type = "Live";
					setUsed(record.getFirstName()+""+record.getLastName()+""+record.getPhone(),table,1,agent);
					return record;
				}
				else
					continue;
			}
			return null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			try {
				if(set!=null) set.close();
				if(stmt!=null) stmt.close();
				if(info!=null)info.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public synchronized Record GetSentLeadDME(String table,String agent) {
		String sql = "SELECT * FROM `"+table+"` WHERE `MESSAGE_STATUS` = 'SENT' AND `LAST_UPDATED` >= ";
		Record record = null;
		Statement stmt = null;
		ResultSet set = null;
		InfoDatabase info = new InfoDatabase();
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			while(set.next()) { 
				String zipcode = set.getString("zip");
				String state = set.getString("state");
				int offset = info.GetOffSet(zipcode,state);
				if(IsOpen(offset)) {
					record = new Record(set);
					record.record_type = "Live";
					setUsed(record.getFirstName()+""+record.getLastName()+""+record.getPhone(),table,1,agent);
					return record;
				}
				else
					continue;
			}
			return null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			try {
				if(set!=null) set.close();
				if(stmt!=null) stmt.close();
				if(info!=null)info.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static class Columns {
		public static final String ALL = "ALL";
		public static final String FIRST_NAME = "first_name";
		public static final String LAST_NAME = "last_name";
		public static final String DOB = "dob";
		public static final String AGE = "age";
		public static final String PHONE_NUMBER = "phonenumber";
		public static final String SSN = "ssn";
		public static final String GENDER = "gender";
		public static final String ADDRESS = "address";
		public static final String CITY = "city";
		public static final String STATE = "state";
		public static final String ZIP = "zip";
		public static final String EMDEON_STATUS = "EMDEON_STATUS";
		public static final String TYPE = "TYPE";
		public static final String EMDEON_TYPE = "EMDEON_TYPE";
		public static final String LAST_EMDEON_DATE = "LAST_EMDEON_DATE";
		public static final String INSURANCE_NAME = "insurance_name";
		public static final String CARRIER = "carrier";
		public static final String POLICY_ID = "policy_id";
		public static final String BIN = "bin";
		public static final String GROUP = "grp";
		public static final String PCN = "pcn";
		public static final String CONTRACT_ID = "contract_id";
		public static final String BENEFIT_ID = "benefit_id";
		public static final String NPI = "npi";
		public static final String DR_TYPE = "dr_type";
		public static final String DR_FIRST = "dr_first_name";
		public static final String DR_LAST = "dr_last_name";
		public static final String DR_ADDRESS1 = "dr_address1";
		public static final String DR_CITY = "dr_city";
		public static final String DR_STATE = "dr_state";
		public static final String DR_ZIP = "dr_zip";
		public static final String DR_PHONE = "dr_phone";
		public static final String DR_FAX = "dr_fax";
		public static final String ID = "_id";
		public static final String AGENT = "agent";
		public static final String PHARMACY = "PHARMACY";
		public static final String FAX_ATTEMPTS = "FAX_ATTEMPTS";
		public static final String FAXES_SENT = "FAXES_SENT";
		public static final String FAX_DISPOSITION = "FAX_DISPOSITION";
		public static final String FAX_DISPOSITION_DATE = "FAX_DISPOSITION_DATE";
		public static final String FAX_SENT_DATE = "FAX_SENT_DATE";
		public static final String MESSAGE_STATUS = "MESSAGE_STATUS";
		public static final String USED = "USED";
		public static final String AFID = "AFID";
		public static final String CALL_CENTER = "CALL_CENTER";
		public static final String NOTES = "notes";
		public static final String DATE_ADDED = "DATE_ADDED";
		public static final String LAST_UPDATED = "LAST_UPDATED";
		public static final String MESSAGE_ID = "MESSAGE_ID";
		public static final String CONFIRM_DOCTOR = "CONFIRM_DOCTOR";
		public static final String PAIN_LOCATION = "PAIN_LOCATION";
		public static final String PAIN_CAUSE = "PAIN_CAUSE";
		public static final String DR_CHASE_AGENT = "DR_CHASE_AGENT";
		public static final String SOURCE = "SOURCE";
		public static final String PRODUCTS = "PRODUCTS";
		public static final String[] DR_HEADERS = {NPI,DR_FIRST,DR_LAST,DR_ADDRESS1,DR_CITY,DR_STATE,DR_ZIP,DR_PHONE,DR_FAX,FAX_DISPOSITION,FAX_DISPOSITION_DATE,USED,CONFIRM_DOCTOR};
		
		public static final String[] HEADERS = {ALL,FIRST_NAME,LAST_NAME,DOB,AGE,PHONE_NUMBER,ADDRESS,CITY,STATE,ZIP,EMDEON_STATUS,EMDEON_TYPE,TYPE,LAST_EMDEON_DATE,//12
				CARRIER,INSURANCE_NAME,POLICY_ID,BIN,GROUP,PCN,NPI,DR_TYPE,DR_FIRST,DR_LAST,DR_ADDRESS1,DR_CITY,DR_STATE,NOTES,CONTRACT_ID,BENEFIT_ID,PRODUCTS,
				DR_ZIP,DR_PHONE,DR_FAX,SSN,GENDER,ID,PAIN_LOCATION,PAIN_CAUSE,AGENT,FAX_DISPOSITION,MESSAGE_STATUS,AFID,SOURCE,MESSAGE_ID,CALL_CENTER,PHARMACY,DATE_ADDED};//16
		
		public static final String[] UPDATE_RECORD_HEADERS = {FIRST_NAME,LAST_NAME,DOB,PHONE_NUMBER,ADDRESS,CITY,STATE,ZIP,
				EMDEON_STATUS,EMDEON_TYPE,LAST_EMDEON_DATE,TYPE,CARRIER,POLICY_ID,BIN,GROUP,PCN,CALL_CENTER,
				NPI,DR_TYPE,DR_FIRST,DR_LAST,DR_ADDRESS1,DR_CITY,DR_STATE,DR_ZIP,DR_PHONE,DR_FAX,PAIN_CAUSE,PAIN_LOCATION,AGE,
				SSN,GENDER,AGENT,AFID,PHARMACY,AGENT,DATE_ADDED,FAX_DISPOSITION,MESSAGE_STATUS,MESSAGE_ID,FAX_ATTEMPTS,FAXES_SENT};
	}
	private static class TelmedColumns {
		public static final String FIRST_NAME = "first_name";
		public static final String LAST_NAME = "last_name";
		public static final String PHONE_NUMBER = "phonenumber";
		public static final String DOB = "dob";
		public static final String AGENT = "agent";
		public static final String DATE_ADDED = "DATE_ADDED";
		public static final String TELMED_ID = "TELMED_ID";
		public static final String TELMED_STATUS = "TELMED_STATUS";
	}
	public class ErrorCodes {
		public static final int DUPLICATE = 1062;
		public static final int SYNTAX = 1064;
		public static final int NULL = 1364;
		public static final int DATA_TOO_LONG = 1406;
	}
}
