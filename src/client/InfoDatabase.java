package client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

public class InfoDatabase {
	public Connection connect = null;
	private final String HOST_NAME = "ltf5469.tam.us.siteprotect.com";
	public InfoDatabase() {
		try {
			//This will load the MySQL driver, each DB has its own driver
			 Class.forName("com.mysql.jdbc.Driver"); 
			 //Connect to database
			connect = DriverManager
					      .getConnection("jdbc:mysql://"+HOST_NAME+":3306/Info_Table", "tkuenzler","Tommy6847");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: "+e.getMessage());
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: "+e.getMessage());
			e.printStackTrace();
		} 
	}
	public int AddToDMECheck(Record record,String requestId) {
		String sql = "INSERT INTO `PVERIFY_ID` (`first_name`,`last_name`,`phonenumber`,`request_id`) VALUES ('"+record.getFirstName()+"','"+record.getLastName()+"','"+record.getPhone()+"','"+requestId+"')";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		}   catch (SQLException e) {
			// TODO Auto-generated catch block
			return e.getErrorCode();
		} finally {
			try {
				if(stmt!=null)stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public boolean hasContractId(Record record) {
		String sql = "SELECT * FROM `PLAN_TYPE` WHERE `contract_id` = '"+record.getContractId()+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) {
				return true;
			}
			return false;
		}   catch (SQLException e) {
			// TODO Auto-generated catch block
			return false;
		} finally {
			try {
				if(stmt!=null)stmt.close();
				if(set!=null)set.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public boolean isPPO(Record record) {
		String sql = "SELECT * FROM `PLAN_TYPE` WHERE `contract_id` = '"+record.getContractId()+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) {
				String plan_type = set.getString("PLAN_TYPE");
				switch(plan_type) {
					case "PPO":
					case "Regional PPO":
						return true;
					default:
						return false;
				}
			}
			return false;
		}   catch (SQLException e) {
			// TODO Auto-generated catch block
			return false;
		} finally {
			try {
				if(stmt!=null)stmt.close();
				if(set!=null)set.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public String GetRequestId(Record record) {
		String sql = "SELECT * FROM `PVERIFY_ID` WHERE `phonenumber` = '"+record.getPhone()+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) 
				return set.getString("request_id");
			else
				return null;
		}   catch (SQLException e) {
			// TODO Auto-generated catch block
			return null;
		} finally {
			try {
				if(stmt!=null)stmt.close();
				if(set!=null)set.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public int SetRecordAsRequalified(String number) {
		String sql = "UPDATE `REQUALIFY` SET `REQUALIFIED_DATE` = '"+getCurrentDate("yyyy-MM-dd")+"' WHERE `phone_number` = '"+number+"'";
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
	public HashMap<String, AgentReport> GetAgentsFromCallCenter(String callCenter,String company) {
		HashMap<String, AgentReport> agents = new HashMap<String, AgentReport>();
		String sql = null;
		if(callCenter.equalsIgnoreCase("ALL"))
			sql = "SELECT * FROM `AGENTS` WHERE `COMPANY` = '"+company+"'";
		else
			sql = "SELECT * FROM `AGENTS` WHERE `COMPANY` = '"+company+"' AND `CALL_CENTER` = '"+callCenter+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set =  stmt.executeQuery(sql);
			while(set.next()) {
				String name = set.getString("AGENT");
				AgentReport agent = new AgentReport(name);
				agents.put(name,agent);
			}
			return agents;
		} catch(SQLException ex) {
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
	public int UpdateIP(String ip,String agent) {
		String sql = "UPDATE `AGENTS` SET `IP` = '"+ip+"' WHERE `AGENT` = '"+agent+"'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch(SQLException ex) {
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
	public String GetState(String zipcode) {
		String sql = "SELECT `state` FROM `ALL_ZIPCODE_INCOME` WHERE `ZIP_CODE` = '"+zipcode+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set =  stmt.executeQuery(sql);
			if(set.next())
				return set.getString("state");
			else 
				return null;
		} catch(SQLException ex) {
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
	public int GetOffSet(String zipcode,String state) {
		String sql = "SELECT `offset` FROM `ALL_ZIPCODE_INCOME` WHERE `ZIP_CODE` = '"+zipcode+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set =  stmt.executeQuery(sql);
			if(set.next())
				return set.getInt("offset");
			else 
				return GetAverageOffset(state);
		} catch(SQLException ex) {
			return ex.getErrorCode();
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
	public int GetAverageOffset(String state) {
		String sql = "SELECT ROUND(AVG(`offset`)) AS OFFSET FROM `ALL_ZIPCODE_INCOME` WHERE `state` = 'state'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set =  stmt.executeQuery(sql);
			if(set.next())
				return set.getInt("OFFSET");
			else
				return 0;
		} catch(SQLException ex) {
			return ex.getErrorCode();
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
	public int addToDNF(String fax) {
		String sql = "INSERT INTO `DNF` (`DONT_FAX`) VALUES ('"+fax+"')";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch(SQLException ex) {
				ex.printStackTrace();
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
	public boolean IsInDNF(String fax) {
		String sql  = "SELECT * FROM `DNF` WHERE `DONT_FAX` = '"+fax+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			boolean result;
			if(set.next())
				result = true;
			else
				result = false;
			return result;
		} catch(SQLException ex) {
			ex.printStackTrace();
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
	
	public String CheckDrType(String code) {
		String sql = "SELECT * FROM `DOCTOR_TYPE` WHERE `CODE` = '"+code+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next())
				return "true";
			else
				return "false";
			//return set.next();
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
	public int incrementLookup(String name) {
		String sql = "UPDATE `LOOKUPS` SET `lookups` = `lookups` + 1 WHERE `NAME` = '"+name+"'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch(SQLException ex) {
			return -1;
		} catch(NullPointerException ex) {
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
	public int incrementTelmed(String name) {
		String sql = "UPDATE `LOOKUPS` SET `TELMEDS` = `TELMEDS` + 1 WHERE `NAME` = '"+name+"'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch(SQLException ex) {
			return -1;
		} catch(NullPointerException ex) {
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
	public boolean HasBeenLookedUp(Record record) {
		String sql = "SELECT * FROM `Checked` WHERE `phonenumber` = '"+record.getPhone()+"'";
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
		String sql = "INSERT INTO `Checked` (`AFID`,`phonenumber`,`first_name`,`last_name`,`dob`,`ssn`,`address`,`city`,`state`,`zip`,`gender`,`AGENT_INSURANCE_TYPE`) VALUES ('"+afid+"','"+record.getPhone()+"','"+record.getFirstName()+"','"+record.getLastName()+"',"
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
				+ " `bin` = '"+record.getBin()+"', `grp` =  '"+record.getGrp()+"', `pcn` = '"+record.getPcn()+"', `additional_info` = '"+record.getAdditionalInfo()+"' WHERE `phonenumber` = '"+record.getPhone()+"'";
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
	public JSONObject GetCallCenters(String company) {
		String sql = "SELECT DISTINCT `CALL_CENTER` FROM `AGENTS` WHERE `COMPANY` = '"+company+"'";
		Statement stmt = null;
		ResultSet set = null;
		JSONObject obj = new JSONObject();
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			obj.put("0", "ALL");
			int count = 1;
			
			while(set.next()) {
				String call_center = set.getString("CALL_CENTER");
				obj.put(count+"", call_center);
				count++;
			}
			return obj;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			try {
				if(set!=null) set.close();
				if(stmt!=null) stmt.close();	
			} catch (SQLException e) {
				
			}
		}
	}
	public JSONObject GetAgents(String params,String company) {
		String sql = "SELECT * FROM `AGENTS` WHERE `COMPANY` = '"+company+"' "+params;
		Statement stmt = null;
		ResultSet set = null;
		JSONObject obj = new JSONObject();
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			obj.put("0", "ALL");
			int count = 1;
			
			while(set.next()) {
				String agent = set.getString("AGENT");
				obj.put(count+"", agent);
				count++;
			}
			return obj;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			try {
				if(set!=null) set.close();
				if(stmt!=null) stmt.close();	
			} catch (SQLException e) {
				
			}
		}
	}
	public String GetCallCenter(String agent) {
		String sql = "SELECT * FROM `AGENTS` WHERE `AGENT` = '"+agent+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) 
				return set.getString("CALL_CENTER");
			else
				return "NONE";
		} catch(SQLException ex) {
			ex.getMessage();
			return null;
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
		String sql = "SELECT * FROM `Checked` WHERE `phonenumber` = '"+record.getPhone()+"'";
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
				record.setAdditionalInfo(set.getString("additional_info"));
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
	public boolean CheckIfAudited(String phone) {
		String sql = "SELECT * FROM `AUDIT` WHERE `PHONE` = '"+phone+"'";
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
	public boolean isClosed() {
		try {
			return connect.isClosed();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return true;
		}
	}
	private String GetCurrentMonthColumn() {
		String sql = "SELECT MONTH(CURRENT_DATE()),YEAR(CURRENT_DATE())";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) 
				return set.getString("MONTH(CURRENT_DATE())")+"_"+set.getString("YEAR(CURRENT_DATE())");
			else
				return null;
		} catch(SQLException ex) {
			return "";
		} finally {
			try {
				if(stmt!=null)stmt.close();
				if(set!=null)set.close();
			} catch(SQLException ex) {
				
			}
		}
	}
	public String GetEPH(String callCenter,String company) {
		//String column = GetCurrentMonthColumn();
		String column = "EPH"; 
		String sql = null;
		if(callCenter.equalsIgnoreCase("ALL")) 
			sql = "SELECT * FROM `AGENTS` WHERE `HOURS_LAST_UPDATED` <> '0000-00-00' AND `COMPANY` = '"+company+"' ORDER BY `"+column+"` DESC";	
		else 
			sql = "SELECT * FROM `AGENTS` WHERE `HOURS_LAST_UPDATED` <> '0000-00-00' AND `COMPANY` = '"+company+"' AND `CALL_CENTER` = '"+callCenter+"' ORDER BY `"+column+"` DESC";
		Statement stmt = null;
		ResultSet set = null;
		StringBuilder sb = new StringBuilder();
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			sb.append("<h1>"+callCenter+"</h1>");
			sb.append("<table border='1' >");
			sb.append("<tr bgcolor='#d3d3d3'>");
			sb.append("<td>Agent Name</td>");
			sb.append("<td>Call Center</td>");
			sb.append("<td>EPH</td>");
			sb.append("<td>Last Updated</td>");
			sb.append("</tr>");
			while(set.next()) {
				String agent = set.getString("AGENT");
				String eph = set.getString(column);
				String center = set.getString("CALL_CENTER");
				String updated = set.getDate("HOURS_LAST_UPDATED").toString();
				sb.append("<tr>");
				sb.append("<td>"+agent+"</td>");
				sb.append("<td>"+center+"</td>");
				sb.append("<td>"+eph+"</td>");
				sb.append("<td>"+updated+"</td>");
				sb.append("</tr>");
			}
			sb.append("</table>");
			return sb.toString();
		} catch(SQLException ex) {
			return ex.getMessage();
		} finally {
			try {
				if(stmt!=null)stmt.close();
				if(set!=null)set.close();
			} catch(SQLException ex) {
				
			}
		}
	}
	public String GetDispositionPercent(String callCenter,String company) {
		NumberFormat defaultFormat = NumberFormat.getPercentInstance();
		defaultFormat.setMinimumFractionDigits(3);
		String column = "EnrollmentPercent"; 
		String sql = null;
		String avg =  null;
		if(callCenter.equalsIgnoreCase("ALL")) {
			sql = "SELECT * FROM `AGENTS` WHERE `DISPO_LAST_UPDATED` <> '0000-00-00' AND `COMPANY` = '"+company+"' ORDER BY `"+column+"` DESC";
			avg = "SELECT AVG(`EnrollmentPercent`) FROM `AGENTS` WHERE `HOURS_LAST_UPDATED` <> '0000-00-00' AND `COMPANY` = '"+company+"' ORDER BY `"+column+"` DESC";
		}
		else  {
			sql = "SELECT * FROM `AGENTS` WHERE `DISPO_LAST_UPDATED` <> '0000-00-00' AND `COMPANY` = '"+company+"' AND `CALL_CENTER` = '"+callCenter+"' ORDER BY `"+column+"` DESC";
			avg = "SELECT AVG(`EnrollmentPercent`) FROM `AGENTS` WHERE `DISPO_LAST_UPDATED` <> '0000-00-00' AND `COMPANY` = '"+company+"' AND `CALL_CENTER` = '"+callCenter+"' ORDER BY `"+column+"` DESC";
		}
		
		Statement stmt = null;
		ResultSet set = null;
		StringBuilder sb = new StringBuilder();
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			sb.append("<h1>"+callCenter+"</h1>");
			sb.append("<table border='1' >");
			sb.append("<tr bgcolor='#d3d3d3'>");
			sb.append("<td>Agent Name</td>");
			sb.append("<td>Call Center</td>");
			sb.append("<td>Conversion Percent</td>");
			sb.append("<td>Last Updated</td>");
			sb.append("</tr>");
			while(set.next()) {
				String agent = set.getString("AGENT");
				double percent = set.getDouble("EnrollmentPercent");
				String center = set.getString("CALL_CENTER");
				String updated = set.getDate("DISPO_LAST_UPDATED").toString();
				sb.append("<tr>");
				sb.append("<td>"+agent+"</td>");
				sb.append("<td>"+center+"</td>");
				sb.append("<td>"+defaultFormat.format(percent)+"</td>");
				sb.append("<td>"+updated+"</td>");
				sb.append("</tr>");
			}
			set.close();
			set = stmt.executeQuery(avg);
			while(set.next()) {
				double percent = set.getDouble("AVG(`EnrollmentPercent`)");
				sb.append("<tr>");
				sb.append("<td>Average</td>");
				sb.append("<td>"+callCenter+"</td>");
				sb.append("<td>"+defaultFormat.format(percent)+"</td>");
				sb.append("<td></td>");
				sb.append("</tr>");
			}
			sb.append("</table>");
			return sb.toString();
		} catch(SQLException ex) {
			return ex.getMessage();
		} finally {
			try {
				if(stmt!=null)stmt.close();
				if(set!=null)set.close();
			} catch(SQLException ex) {
				
			}
		}
	}
	public String GetAverageEPH() {
		String sql = "SELECT SEC_TO_TIME(SUM(TIME_TO_SEC(`EPH`)) / COUNT(CASE WHEN `EPH` <> '' THEN 1 END)) AS `TIME` FROM `AGENTS`";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next())
				return set.getString("TIME");
			else
				return "ERROR";
		} catch(SQLException ex) {
			return "";
		} finally {
			try {
				if(stmt!=null)stmt.close();
				if(set!=null)set.close();
			} catch(SQLException ex) {
				
			}
		}
	}
	private String getCurrentDate(String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format); 
		Date date = new Date(); 
		return formatter.format(date);
	}
	
	/*
	 * AMERITRADE DATA
	 */
	public int UpdateCode(String code) {
		String sql = "UPDATE `AMERITRADE` SET `CODE` = '"+code+"' WHERE `PROGRAM` = 'Tommy'";
		Statement stmt = null;
		try {
			stmt = connect.createStatement();
			return stmt.executeUpdate(sql);
		} catch(SQLException ex) {
			return ex.getErrorCode();
		}finally {
			try {
				if(stmt!=null)stmt.close();
			} catch(SQLException ex) {
				
			}
		}
	}
}
