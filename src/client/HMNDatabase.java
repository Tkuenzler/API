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

public class HMNDatabase {
	String table;
	public Connection connect = null;
	public HMNDatabase() {
		try {
			
			//This will load the MySQL driver, each DB has its own driver
			 Class.forName("com.mysql.jdbc.Driver"); 
			 //Connect to database
			 connect = DriverManager
				      .getConnection("jdbc:mysql://ltf5469.tam.us.siteprotect.com:3306/HMN", "tkuenzler","Tommy6847");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	private String getCurrentDate(String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format); 
		Date date = new Date(); 
		return formatter.format(date);
	}
	/*
	 * ADD LEAD
	 * 
	 */
	public int addRecord(Record record) {
		PreparedStatement stmt = null;
		try {
			stmt = connect.prepareStatement(buildAddStatement("Leads"));
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
				case Columns.CARRIER:
					stmt.setString(i, record.getCarrier());
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
				case Columns.NPI:
					stmt.setString(i, record.getNpi());
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
				}				
			} 
			return stmt.executeUpdate();
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
	private String buildAddStatement(String table) {
		StringBuilder base = new StringBuilder("INSERT into `"+table+"` (");
		for(String s: Columns.HEADERS) {
			if(s.equalsIgnoreCase(Columns.ALL))
				continue;
			else if(s.equalsIgnoreCase(Columns.ID)) 
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

	/*
	 * TELMED FUNCTIONS
	 */
	public int AddToTelmed(Record record,String recordId,String ip,String pharmacy) {
		String sql = "INSERT INTO `TELMED` (`first_name`,`last_name`,`phonenumber`,`agent`,`TELMED_STATUS`,`ip`,`bin`,`grp`,`pcn`,`DATE_MODIFIED`,`DATE_ADDED`,`PHARMACY`) "
				+ "VALUES ('"+record.getFirstName()+"','"+record.getLastName()+"','"+record.getPhone()+"','"+record.getAgent()+"','New Patient','"+ip+"',"
						+ "'"+record.getBin()+"','"+record.getGrp()+"','"+record.getPcn()+"','"+getCurrentDate("yyyy-MM-dd")+"','"+getCurrentDate("yyyy-MM-dd")+"','"+pharmacy+"')";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch(SQLException ex) {
			if(ex.getErrorCode()==1062)
				return -1;
			else
				return 0;
		}  finally {
			try {
				if(stmt!=null) stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public String GetDuplicateInfo(String phone) {
		String sql = "SELECT * FROM `TELMED` WHERE `phonenumber` = '"+phone+"'";
		String response = null;
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) {
				String name = set.getString("first_name")+" "+set.getString("last_name");
				String recordId = set.getString("phonenumber");
				String agent = set.getString("agent");
				String date = set.getString("DATE_ADDED");
				StringBuilder sb = new StringBuilder();
				sb.append("<html>");
				sb.append("<body>");
				sb.append("<h1>DUPLICATE ENTRY</h1>");
				sb.append("<h2>Patient Name: "+name+"</h2>");
				sb.append("<h2>Record ID: "+recordId+"</h2>");
				sb.append("<h2>Submitted By: "+agent+"</h2>");
				sb.append("<h2>Date Added: "+date+"</h3>");
				sb.append("</body>");
				sb.append("</html>");
				response = sb.toString();
			}
			else 
				response = "";
			return response;
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
	public boolean CheckDuplicateTelmed(String phone) {
		String sql = "SELECT * FROM `TELMED` WHERE `phonenumber` = '"+phone+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			return set.next();
		} catch(SQLException ex) {
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
	/*
	 * INFO DATABASE FUNCTION 	
	 */
	
	public boolean HasBeenLookedUp(Record record) {
		String sql = "SELECT * FROM `Checked` WHERE `PHONE` = '"+record.getPhone()+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next())
				return true;
			return false;
		} catch(SQLException ex) {
			return false;
		} catch(NullPointerException ex) {
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
	public int AddToCheck(Record record,String afid,String type) {
		String sql = "INSERT INTO `Checked` (`AFID`,`PHONE`,`FIRST`,`LAST`,`DOB`,`SSN`,`ADDRESS`,`CITY`,`STATE`,`ZIP`,`GENDER`,`AGENT_INSURANCE_TYPE`) VALUES ('"+afid+"','"+record.getPhone()+"','"+record.getFirstName()+"','"+record.getLastName()+"',"
				+ "'"+record.getDob()+"','"+record.getSsn()+"','"+record.getAddress()+"','"+record.getCity()+"','"+record.getState()+"','"+record.getZip()+"','"+record.getGender()+"','"+type+"')";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch(SQLException ex) {
			return -1;
		} catch(NullPointerException ex) {
			return -2;
		} finally {
			try {
				if(stmt!=null) stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public int UpdateCheck(Record record,String type) {
		String sql = "UPDATE `Checked` SET `STATUS` = '"+record.getStatus()+"', `INSURANCE_TYPE` = '"+type+"', `carrier` = '"+record.getCarrier()+"', `policy_id` = '"+record.getPolicyId()+"',"
				+ " `bin` = '"+record.getBin()+"', `grp` =  '"+record.getGrp()+"', `pcn` = '"+record.getPcn()+"' WHERE `PHONE` = '"+record.getPhone()+"'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch(SQLException ex) {
			return -1;
		} catch(NullPointerException ex) {
			return -2;
		} finally {
			try {
				if(stmt!=null) stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void GetInsuranceInfo(Record record) {
		String sql = "SELECT * FROM `Checked` WHERE `PHONE` = '"+record.getPhone()+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) {
				record.setStatus(set.getString("STATUS"));
				record.setCarrier(set.getString("carrier"));
				record.setPolicyId(set.getString("policy_id"));
				record.setBin(set.getString("bin"));
				record.setGrp(set.getString("grp"));
				record.setPcn(set.getString("pcn"));
			}
		} catch(SQLException ex) {
			ex.getMessage();
		} finally {
			try {
				if(stmt!=null) stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * ROAD MAP FUNCTIONS
	 */
	public boolean AcceptableTelmedPharmacy(String pharmacy) {
		String sql = "SELECT * FROM `TELMED_ROADMAP` WHERE `PHARMACY` = '"+pharmacy+"' AND (`MEDICARE_TELMED` = 1 OR `COMMERCIAL_TELMED` = 1)";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			return set.next();
		} catch(SQLException ex) {
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
	public String[] getPharmacies() {
		String sql = "SELECT * FROM `PHARMCIES`";
		Statement stmt = null;
		ResultSet pharmacies = null;
		try {
			stmt = connect.createStatement();
			pharmacies = stmt.executeQuery(sql);
			List<String> list = new ArrayList<String>();
			while(pharmacies.next()) {
				list.add(pharmacies.getString("NAME"));
			}
			return list.toArray(new String[list.size()]);
		}catch(SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if(pharmacies!=null) pharmacies.close();
				if(stmt!=null) stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	public int isInRoadMap(String pharmacy,String pbm,String state) {
		String sql = "SELECT `"+pbm+"` FROM `"+pharmacy+"` WHERE `State` = '"+state+"'";
		int value;
		ResultSet set = null;
		Statement stmt  = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) 
				value = set.getInt(pbm);
			else  
				return -2;
			return value;
		} catch(SQLException ex) {
			ex.printStackTrace();
			return -1;
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
	public int isInRoadMap(Record record,String pharmacy) {
		String sql = "SELECT `"+record.getPBMFromBin(record.getBin())+"` FROM `"+pharmacy+"` WHERE `State` = '"+record.getState()+"'";
		int value;
		ResultSet set = null;
		Statement stmt  = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) 
				value = set.getInt(record.getPBMFromBin(record.getBin()));
			else  
				return -2;
			return value;
		} catch(SQLException ex) {
			ex.printStackTrace();
			return -1;
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
	public int hasState(Record record,String pharmacy) {
		String sql = "SELECT (`Caremark`+`Express Scripts`+`Prime Therapeutics`+`Cigna`+`Aetna`+`Humana`+`OptumRx`+`Catamaran`+`Medimpact`+`Argus`+`Navitus`+`Envision RX`) AS SUM FROM `"+pharmacy+"` WHERE `State` = '"+record.getState()+"'";
		int value = 0;
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) {
				value = set.getInt("SUM");
			}
			return value;
		} catch(SQLException ex) {
			ex.printStackTrace();
			return -1;
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
	public String CheckPrivateTier(Record record) {
		String sql = "SELECT * FROM `TELMED_ROADMAP` WHERE `COMMERCIAL_TIER` > 0 ORDER BY `COMMERCIAL_TIER` ASC";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			while(set.next()) {
				String pharmacy = set.getString("PHARMACY");
				if(isInRoadMap(record,pharmacy)>0)
					return pharmacy;
			}
			return null;
		} catch(SQLException ex) {
			ex.printStackTrace();
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
	public String CheckMedicareTier(Record record) {
		String sql = "SELECT * FROM `TELMED_ROADMAP` WHERE `MEDICARE_TIER` > 0 ORDER BY `MEDICARE_TIER` ASC";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			while(set.next()) {
				String pharmacy = set.getString("PHARMACY");
				if(isInRoadMap(record,pharmacy)>0)
					return pharmacy;
			}
			return null;
		} catch(SQLException ex) {
			ex.printStackTrace();
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
	public String CheckTelmedPrivate(Record record,boolean pbm) {
		String sql = "SELECT * FROM `TELMED_ROADMAP` WHERE `COMMERCIAL_TELMED` > 0 ORDER BY `COMMERCIAL_TIER` ASC";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			while(set.next()) {
				String pharmacy = set.getString("PHARMACY");
				if(pbm) {
					if(isInRoadMap(record,pharmacy)>0)
						return pharmacy;
					else 
						continue;
				}
				else {
					if(hasState(record,pharmacy)>0)
						return pharmacy;
					else
						continue;
			
				}
			}
		return null;
		} catch(SQLException ex) {
			ex.printStackTrace();
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
	public String CheckTelmedMedicare(Record record, boolean pbm) {
		String sql = "SELECT * FROM `TELMED_ROADMAP` WHERE `MEDICARE_TELMED` > 0 ORDER BY `MEDICARE_TIER` ASC";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			while(set.next()) {
				String pharmacy = set.getString("PHARMACY");
				if(pbm) {
					if(isInRoadMap(record,pharmacy)>0)
						return pharmacy;
					else 
						continue;
				}
				else {
					if(hasState(record,pharmacy)>0)
						return pharmacy;
					else
						continue;
			
				}
			}
			return null;
		} catch(SQLException ex) {
			ex.printStackTrace();
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
	public String CanTelmedGetPharmacy(Record record,String type) {
		switch(type) {
			case "Medicare":
				return CheckTelmedMedicare(record,false);
			case "Private Insurance":
			case "Provided by Job":
			case "Marketplace":
				return CheckTelmedPrivate(record,false);
			default: 
				return null;
		}
	}
	public boolean CanTelmed(Record record,String type) {
		switch(type) {
			case "Medicare":
				if(CheckTelmedMedicare(record,false)!=null)
					return true;
				else
					return false;
			case "Private Insurance":
			case "Provided by Job":
			case "Marketplace":
				if(CheckTelmedPrivate(record,false)!=null)
					return true;
				else
					return false;
			default: 
				return false;
		}
	}
	public static class Columns {
		public static final String ALL = "ALL";
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
		public static final String BIN = "bin";
		public static final String GROUP = "grp";
		public static final String PCN = "pcn";
		public static final String NPI = "npi";
		public static final String DR_FIRST = "dr_first_name";
		public static final String DR_LAST = "dr_last_name";
		public static final String DR_ADDRESS1 = "dr_address1";
		public static final String DR_CITY = "dr_city";
		public static final String DR_STATE = "dr_state";
		public static final String DR_ZIP = "dr_zip";
		public static final String DR_PHONE = "dr_phone";
		public static final String DR_FAX = "dr_fax";
		public static final String ID = "_id";

		
		public static final String[] HEADERS = {ALL,FIRST_NAME,LAST_NAME,DOB,PHONE_NUMBER,ADDRESS,CITY,STATE,ZIP,CARRIER,POLICY_ID,BIN,GROUP,PCN,NPI,
				DR_FIRST,DR_LAST,DR_ADDRESS1,DR_CITY,DR_STATE,DR_ZIP,DR_PHONE,DR_FAX,SSN,GENDER,ID};
		private class ErrorCodes {
			public static final int MYSQL_DUPLICATE_PK = 1062;
		}	
	}
}
