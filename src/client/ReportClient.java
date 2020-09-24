package client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import DoctorChase.FaxStatus;
import Fax.TelmedStatus;

public class ReportClient {
	String table;
	public Connection connect = null;
	public ReportClient(String database) {
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
	public String GetRequalify(String to,String from) {
		String sql = "SELECT * FROM `REQUALIFY` WHERE `REQUALIFIED_DATE` >= '"+from+"' AND `REQUALIFIED_DATE` <= '"+to+"' ORDER BY `REQUALIFIED_DATE` ASC";
		Statement stmt = null;
		ResultSet set = null;
		try {
			int count = 1;
			StringBuilder sb = new StringBuilder();
			sb.append("<table border='1'>");
			sb.append("<tr bgcolor='#d3d3d3'>");
			sb.append("<td>#</td>");
			sb.append("<td>Date</td>");
			sb.append("<td>Name</td>");
			sb.append("<td>Phone</td>");
			sb.append("<td>Ailment</td>");
			sb.append("<td>Pharmacy</td>");
			sb.append("<td>Marketing team</td>");
			sb.append("</tr>");
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			while(set.next()) {
				sb.append("<tr>");
				sb.append("<td>"+count+"</td>");
				sb.append("<td>"+set.getString("REQUALIFIED_DATE")+"</td>");
				sb.append("<td>"+set.getString("first_name") +" "+set.getString("last_name")+"</td>");
				sb.append("<td>"+set.getString("phone_number")+"</td>");
				sb.append("<td>"+set.getString("medication_category")+"</td>");
				sb.append("<td>"+set.getString("PHARMACY")+"</td>");
				sb.append("<td>"+set.getString("marketing_team")+"</td>");
				sb.append("</tr>");
				count++;
			}
			sb.append("</table>");
			return sb.toString();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return e.getMessage();
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
	public HashMap<String, Integer> GetFaxesSentByPharmacy(String to,String from) {
		String sql = "SELECT * FROM `Leads` WHERE (`"+Columns.LAST_CHASE_DATE+"` >= '"+from+"' AND `"+Columns.LAST_CHASE_DATE+"` <= '"+to+"') AND `"+Columns.MESSAGE_STATUS+"` = 'Sent'";
		Statement stmt = null;
		ResultSet set = null;
		HashMap<String, Integer> list = new HashMap<String, Integer>();
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			while(set.next()) {
				String pharmacy = set.getString(Columns.PHARMACY);
				if(!list.containsKey(pharmacy))
					list.put(pharmacy, 0);
				list.put(pharmacy, list.get(pharmacy)+1);
			}
			return list;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
	public JSONObject GetColumns(String table) throws JSONException {
		try {
			Statement stmt = connect.createStatement();
			String sql = "SHOW COLUMNS FROM `"+table+"`";
			ResultSet columns = stmt.executeQuery(sql);
			JSONObject obj = new JSONObject();
			int count = 0;
			while(columns.next()) {
				String column = columns.getString("Field");
				switch(column) {
					case "_id":
					case "phonenumber":
						continue;
					default:
						obj.put(count+"", column);
						count++;
				}
			}
			return obj;
		} catch(SQLException e) {
			e.printStackTrace();
			return null;
		}	
	}
	public boolean validColumn(String column,String table) {
		String sql = "SHOW COLUMNS FROM `"+table+"`";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			while(set.next()) {
				String field = set.getString("Field");
				if(column.equalsIgnoreCase(field))
					return true;
			}
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
	public HashMap<String, AgentReport> GetFaxesSent(String from,String to) {
		String sql = "SELECT * FROM `Leads` WHERE `LAST_CHASE_DATE` >= '"+from+"' AND `LAST_CHASE_DATE` <= '"+to+"' AND `MESSAGE_STATUS` = 'Sent'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			HashMap<String, AgentReport> list = new HashMap<String, AgentReport>();
			while(set.next()) {
				String agent = set.getString("DR_CHASE_AGENT");
				if(!list.containsKey(agent))
					list.put(agent, new AgentReport(agent));
				list.get(agent).incrementLeadCount();
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
	public String GetWrongDoctors(String leadsTimeFrame,String callCenter) {
		String sql = null;
		StringBuilder sb = new StringBuilder();
		if(callCenter.equalsIgnoreCase("ALL"))
			sql = "SELECT * FROM `Leads` WHERE `"+Columns.FAX_DISPOSITION+"` = '"+FaxStatus.WRONG_DOCTOR+"' AND "+leadsTimeFrame;
		else
			sql = "SELECT * FROM `Leads` WHERE `"+Columns.FAX_DISPOSITION+"` = '"+FaxStatus.WRONG_DOCTOR+"' AND "+leadsTimeFrame+" AND `CALL_CENTER` = '"+callCenter+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			sb.append("<table border='1'>");
			sb.append("<tr bgcolor='#d3d3d3'>");
			sb.append("<td>Date Added</td>");
			sb.append("<td>Agent Name</td>");
			sb.append("<td>Patient Name</td>");
			sb.append("<td>Phone Number</td>");
			sb.append("<td>Dr Name</td>");
			sb.append("<td>NPI</td>");
			sb.append("<td>Pharmacy</td>");
			sb.append("</tr>");
			while(set.next()) {
				String date = set.getString(Columns.DATE_ADDED);
				String agent = set.getString(Columns.AGENT);
				String name = set.getString(Columns.FIRST_NAME)+" "+set.getString(Columns.LAST_NAME);
				String phone = set.getString(Columns.PHONE_NUMBER);
				String drName = set.getString(Columns.DR_FIRST)+" "+set.getString(Columns.DR_LAST);
				String npi = set.getString(Columns.NPI);
				String pharmacy = set.getString(Columns.PHARMACY);
				sb.append("<tr>");
				sb.append("<td>"+date+"</td>");
				sb.append("<td>"+agent+"</td>");
				sb.append("<td>"+name+"</td>");
				sb.append("<td>"+phone+"</td>");
				sb.append("<td>"+drName+"</td>");
				sb.append("<td>"+npi+"</td>");
				sb.append("<td>"+pharmacy+"</td>");
				sb.append("</tr>");
			}
			return sb.toString();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
	public Set<Entry<String, AgentReport>> GetLeadCount(String from,String to,String column,String table,String params) {
		String sql = "SELECT * FROM `"+table+"` WHERE "+params+"(`DATE_ADDED` >= '"+from+"' AND `DATE_ADDED` <= '"+to+"') ORDER BY `"+column+"` DESC";
		Statement stmt = null;
		ResultSet set = null;
		HashMap<String, AgentReport> list = new HashMap<String, AgentReport>();
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			while(set.next()) {
				String type = set.getString(column);
				if(!list.containsKey(type))
					list.put(type, new AgentReport(type));
				AgentReport report = list.get(type);
				report.incrementLeadCount();
			}
			return SortMap(list);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
	public int GetLeadCount(String from,String to,String table,String params) {
		String sql = "SELECT COUNT(*) FROM `"+table+"` WHERE "+params+"(`DATE_ADDED` >= '"+from+"' AND `DATE_ADDED` <= '"+to+"')";
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
	public PivotTable GetPivotTable(String to,String from,String column1,String column2,String table,String params) {
		String sql = "SELECT * FROM `"+table+"` WHERE "+params+"(`DATE_ADDED` >= '"+from+"' AND `DATE_ADDED` <= '"+to+"') ORDER BY `"+column1+"` DESC";
		Statement stmt = null;
		ResultSet set = null;
		PivotTable pivot = new PivotTable(column1,column2);
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			while(set.next()) {
				pivot.incrementLeadCount();
				String col1 = set.getString(column1).trim();
				String col2 = set.getString(column2).trim();
				if(col1.equalsIgnoreCase(""))
					col1 = "BLANK";
				if(col2.equalsIgnoreCase(""))
					col2 = "BLANK";
				if(!pivot.containsRow(col1))
					pivot.addRow(col1);
				PivotTable.Row row = pivot.getRow(col1);
				row.report.incrementLeadCount();
				if(!row.containsRow(col2))
					row.addRow(col2);
				AgentReport row2 = row.getRow(col2);
				row2.incrementLeadCount();
			}
			return pivot;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
	public HashMap<String, AgentReport> GetFaxDispositionTable(String from,String to,String column,String params) {
		String sql = "SELECT * FROM `Leads` WHERE "+params+"`AFID` <> '' AND (`FAX_DISPOSITION_DATE` >= '"+from+"' AND `FAX_DISPOSITION_DATE` <= '"+to+"') ORDER BY `AGENT` DESC";
		HashMap<String, AgentReport> list = new HashMap<String, AgentReport>();
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			while(set.next()) {
				String col = set.getString(column);
				String faxDisposition = set.getString(Columns.FAX_DISPOSITION);
				if(!list.containsKey(col)) 
					list.put(col,new AgentReport(col));
				AgentReport report = list.get(col);
				report.incrementLeadCount();
				switch(faxDisposition) {
					case FaxStatus.APPROVED:
						report.incrementApproved();
						break;
					case FaxStatus.DECEASED:
						report.incrementDeceased();
						break;
					case FaxStatus.DENIED:
						report.incrementDenied();
						break;
					case FaxStatus.NEEDS_TO_BE_SEEN:
						report.incrementNeedsToBeSeen();
						break;
					case FaxStatus.NEED_PCP:
						report.incrementNeedPcp();
						break;
					case FaxStatus.WRONG_DOCTOR:
						report.incrementWrongDoctor();
						break;
					case FaxStatus.NOT_INTERESTED:
						report.incrementNotInterested();
						break;
					case FaxStatus.ESCRIBE:
						report.incrementEscribe();
						break;
				}
				
			}
			AddTopicalScriptApprovals(list,from,to,column,params);
			AddOralScriptApprovals(list,from,to,column,params);
			return list;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
	private void AddTopicalScriptApprovals(HashMap<String, AgentReport> list,String from,String to,String column,String params) {
		String sql = "SELECT * FROM `Alternate_Scripts` INNER JOIN `Leads` ON `Leads`.`_id` = `Alternate_Scripts`.`_id`"
				+ "  WHERE +"+params+"`Alternate_Scripts`.`TOPICAL_SCRIPT_FAX_DISPOSITION` = 'APPROVED' AND `AFID` = 'MT-LIVE' AND (`DATE_ADDED` > '"+from+"' AND `DATE_ADDED` < '"+to+"')";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			while(set.next()) {
				String agent = set.getString(column);
				AgentReport report = list.get(agent);
				report.incrementTopicalScriptApproval();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
	private void AddOralScriptApprovals(HashMap<String, AgentReport> list,String from,String to,String column,String params) {
		String sql = "SELECT * FROM `Alternate_Scripts` INNER JOIN `Leads` ON `Leads`.`_id` = `Alternate_Scripts`.`_id`"
				+ "  WHERE "+params+"`Alternate_Scripts`.`ORAL_SCRIPT_FAX_DISPOSITION` = 'APPROVED' AND `AFID` = 'MT-LIVE' AND (`DATE_ADDED` >= '"+from+"' AND `DATE_ADDED` <= '"+to+"')";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			while(set.next()) {
				String agent = set.getString(column);
				AgentReport report = list.get(agent);
				report.incrementOralScriptApproval();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
	public String GetFailed(String from,String to,String params) {
		StringBuilder sb = new StringBuilder();
		String sql =  "SELECT * FROM `TELMED` WHERE "+params+""+TelmedStatus.GetFailedQuery()+" AND (`DATE_MODIFIED` >= '"+from+"' AND `DATE_MODIFIED` <= '"+to+"') ORDER BY `DATE_ADDED` DESC";		
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			sb.append("<table border='1'>");
			sb.append("<tr bgcolor='#d3d3d3'>");
			sb.append("<td>Date Added</td>");
			sb.append("<td>Agent Name</td>");
			sb.append("<td>Patient Name</td>");
			sb.append("<td>Phone Number</td>");
			sb.append("<td>ID</td>");
			sb.append("<td>Status</td>");
			sb.append("<td>Pharmacy</td>");
			sb.append("</tr>");
			while(set.next()) {
				String name = set.getString("first_name")+" "+set.getString("last_name");
				String date = set.getString("DATE_ADDED");
				String phone = set.getString("phonenumber");
				String id = set.getString("TELMED_ID");
				String status = set.getString("TELMED_STATUS");
				String pharm = set.getString("PHARMACY");
				String agent = set.getString(Columns.AGENT);
				sb.append("<tr>");
				sb.append("<td>"+date+"</td>");
				sb.append("<td>"+agent+"</td>");
				sb.append("<td>"+name+"</td>");
				sb.append("<td>"+phone+"</td>");
				sb.append("<td>"+id+"</td>");
				sb.append("<td>"+status+"</td>");
				sb.append("<td>"+pharm+"</td>");
				sb.append("</tr>");
			}
			sb.append("</table>");
			return sb.toString();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return e.getMessage();
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
	
	public String GetPending(String from,String to,String params) {
		StringBuilder sb = new StringBuilder();
		String sql =  "SELECT * FROM `TELMED` WHERE "+params+""+TelmedStatus.GetPendingQuery()+" AND (`DATE_MODIFIED` >= '"+from+"' AND `DATE_MODIFIED` <= '"+to+"') ORDER BY `DATE_ADDED` DESC";		
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			sb.append("<table border='1'>");
			sb.append("<tr bgcolor='#d3d3d3'>");
			sb.append("<td>Date Added</td>");
			sb.append("<td>Agent Name</td>");
			sb.append("<td>Patient Name</td>");
			sb.append("<td>Phone Number</td>");
			sb.append("<td>ID</td>");
			sb.append("<td>Status</td>");
			sb.append("<td>Pharmacy</td>");
			sb.append("</tr>");
			while(set.next()) {
				String name = set.getString("first_name")+" "+set.getString("last_name");
				String date = set.getString("DATE_ADDED");
				String phone = set.getString("phonenumber");
				String id = set.getString("TELMED_ID");
				String status = set.getString("TELMED_STATUS");
				String pharm = set.getString("PHARMACY");
				String agent = set.getString(Columns.AGENT);
				sb.append("<tr>");
				sb.append("<td>"+date+"</td>");
				sb.append("<td>"+agent+"</td>");
				sb.append("<td>"+name+"</td>");
				sb.append("<td>"+phone+"</td>");
				sb.append("<td>"+id+"</td>");
				sb.append("<td>"+status+"</td>");
				sb.append("<td>"+pharm+"</td>");
				sb.append("</tr>");
			}
			sb.append("</table>");
			return sb.toString();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return e.getMessage();
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
	
	public String GetSuccessfulTransfers(String from,String to,String params) {
		StringBuilder sb = new StringBuilder();
		String sql =  "SELECT * FROM `TELMED` WHERE "+params+""+TelmedStatus.GetSuccesfulQuery()+" AND (`DATE_MODIFIED` >= '"+from+"' AND `DATE_MODIFIED` <= '"+to+"') ORDER BY `DATE_ADDED` DESC";		
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			sb.append("<table border='1'>");
			sb.append("<tr bgcolor='#d3d3d3'>");
			sb.append("<td>Date Added</td>");
			sb.append("<td>Agent Name</td>");
			sb.append("<td>Patient Name</td>");
			sb.append("<td>Phone Number</td>");
			sb.append("<td>ID</td>");
			sb.append("<td>Status</td>");
			sb.append("<td>Pharmacy</td>");
			sb.append("</tr>");
			while(set.next()) {
				String name = set.getString("first_name")+" "+set.getString("last_name");
				String date = set.getString("DATE_ADDED");
				String phone = set.getString("phonenumber");
				String id = set.getString("TELMED_ID");
				String status = set.getString("TELMED_STATUS");
				String pharm = set.getString("PHARMACY");
				String agent = set.getString(Columns.AGENT);
				sb.append("<tr>");
				sb.append("<td>"+date+"</td>");
				sb.append("<td>"+agent+"</td>");
				sb.append("<td>"+name+"</td>");
				sb.append("<td>"+phone+"</td>");
				sb.append("<td>"+id+"</td>");
				sb.append("<td>"+status+"</td>");
				sb.append("<td>"+pharm+"</td>");
				sb.append("</tr>");
			}
			sb.append("</table>");
			return sb.toString();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return e.getMessage();
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
	
	public int GetLeadsByPharmacy(String pharmacy,String timeFrame) {
		String sql = "SELECT COUNT(*) FROM `Leads` WHERE `PHARMACY` = '"+pharmacy+"' AND "+timeFrame;
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) {
				return set.getInt("COUNT(*)");
			}
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
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
	}
	public int GetConfirmedDoctors(String pharmacy,String timeFrame,int confirm) {
		String sql = "SELECT COUNT(*) FROM `Leads` WHERE `CONFIRM_DOCTOR` = "+confirm+" AND `PHARMACY` = '"+pharmacy+"' AND "+timeFrame;
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) {
				return set.getInt("COUNT(*)");
			}
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
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
	}
	public int GetLeadsByFaxDisposition(String agent,String timeFrame,String fax_disposition) {
		String sql = "SELECT COUNT(*) FROM `Leads` WHERE "+timeFrame+" AND `agent` = '"+agent+"' AND `FAX_DISPOSITION` = '"+fax_disposition+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) {
				return set.getInt("COUNT(*)");
			}
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
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
	}
	public int GetLeadsByCountByAgent(String agent,String timeFrame) {
		String sql = "SELECT COUNT(*) FROM `Leads` WHERE "+timeFrame+" AND `agent` = '"+agent+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) {
				return set.getInt("COUNT(*)");
			}
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
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
	}
	public int GetLeadsByCountByCallCenter(String callCenter,String timeFrame) {
		String sql = null;
		if(callCenter.equalsIgnoreCase("ALL"))
			sql = "SELECT COUNT(*) FROM `Leads` WHERE "+timeFrame;
		else
			sql = "SELECT COUNT(*) FROM `Leads` WHERE "+timeFrame+" AND `CALL_CENTER` = '"+callCenter+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) {
				return set.getInt("COUNT(*)");
			}
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
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
	}
	public int GetSuccesfulTelmedByAgent(String agent,String timeFrame) {
		String sql = "SELECT COUNT(*) FROM `TELMED` WHERE "+timeFrame+" AND `agent` = '"+agent+"' AND "+TelmedStatus.GetSuccesfulQuery();
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) {
				return set.getInt("COUNT(*)");
			}
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
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
	}
	public int GetDMETelmedByAgent(String agent,String timeFrame) {
		String sql = "SELECT COUNT(*) FROM `DME_TELMED` WHERE "+timeFrame+" AND `agent` = '"+agent+"' AND "+TelmedStatus.GetSuccesfulQuery();
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) {
				return set.getInt("COUNT(*)");
			}
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
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
	}
	public int GetSuccesfulTelmedByCallCenter(String callCenter,String timeFrame) {
		String sql = null;
		if(callCenter.equalsIgnoreCase("ALL"))
			sql = "SELECT COUNT(*) FROM `TELMED` WHERE "+timeFrame+" AND "+TelmedStatus.GetSuccesfulQuery();
		else
			sql = "SELECT COUNT(*) FROM `TELMED` WHERE "+timeFrame+" AND `CALL_CENTER` = '"+callCenter+"' AND "+TelmedStatus.GetSuccesfulQuery();
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) {
				return set.getInt("COUNT(*)");
			}
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
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
	}
	public int GetDMETelmeds(String callCenter,String timeFrame) {
		String sql = null;
		if(callCenter.equalsIgnoreCase("ALL"))
			sql = "SELECT COUNT(*) FROM `DME_TELMED` WHERE "+timeFrame+" AND "+TelmedStatus.GetSuccesfulQuery();
		else
			sql = "SELECT COUNT(*) FROM `DME_TELMED` WHERE "+timeFrame+" AND `CALL_CENTER` = '"+callCenter+"' AND "+TelmedStatus.GetSuccesfulQuery();
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) {
				return set.getInt("COUNT(*)");
			}
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
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
	}
	public int GetPendingTelmedByAgent(String agent,String timeFrame) {
		String sql = "SELECT COUNT(*) FROM `TELMED` WHERE "+timeFrame+" AND `agent` = '"+agent+"' AND (`TELMED_STATUS` = '"+TelmedStatus.NEW+"' OR `TELMED_STATUS` = '"+TelmedStatus.IN_PROCESS+"')";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) {
				return set.getInt("COUNT(*)");
			}
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
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
	}
	public int GetFailedTelmedByAgent(String agent,String timeFrame) {
		String sql = "SELECT COUNT(*) FROM `TELMED` WHERE "+timeFrame+" AND `agent` = '"+agent+"' AND (`TELMED_STATUS` = '"+TelmedStatus.REFUSED+"' OR `TELMED_STATUS` = '"+TelmedStatus.UNABLE_TO_CONTACT+"' OR `TELMED_STATUS` = '"+TelmedStatus.NOT_INTERESTED+"')";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) {
				return set.getInt("COUNT(*)");
			}
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
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
	}
	public int GetPendingTelmedByCallCenter(String callCenter,String timeFrame) {
		String sql = null;
		if(callCenter.equalsIgnoreCase("ALL"))
			sql = "SELECT COUNT(*) FROM `TELMED` WHERE "+timeFrame+" AND (`TELMED_STATUS` = '"+TelmedStatus.NEW+"' OR `TELMED_STATUS` = '"+TelmedStatus.IN_PROCESS+"')";
		else
			sql = "SELECT COUNT(*) FROM `TELMED` WHERE "+timeFrame+" AND `CALL_CENTER` = '"+callCenter+"' AND (`TELMED_STATUS` = '"+TelmedStatus.NEW+"' OR `TELMED_STATUS` = '"+TelmedStatus.IN_PROCESS+"')";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) {
				return set.getInt("COUNT(*)");
			}
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
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
	}
	public int GetFailedTelmedByCallCenter(String callCenter,String timeFrame) {
		String sql = null;
		if(callCenter.equalsIgnoreCase("ALL"))
			sql = "SELECT COUNT(*) FROM `TELMED` WHERE "+timeFrame+" AND (`TELMED_STATUS` = '"+TelmedStatus.REFUSED+"' OR `TELMED_STATUS` = '"+TelmedStatus.UNABLE_TO_CONTACT+"' OR `TELMED_STATUS` = '"+TelmedStatus.NOT_INTERESTED+"')";
		else
			sql = "SELECT COUNT(*) FROM `TELMED` WHERE "+timeFrame+" AND `CALL_CENTER` = '"+callCenter+"' AND (`TELMED_STATUS` = '"+TelmedStatus.REFUSED+"' OR `TELMED_STATUS` = '"+TelmedStatus.UNABLE_TO_CONTACT+"' OR `TELMED_STATUS` = '"+TelmedStatus.NOT_INTERESTED+"')";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) {
				return set.getInt("COUNT(*)");
			}
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
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		public static final String EMDEON_STATUS = "EMDEON_STATUS";
		public static final String TYPE = "TYPE";
		public static final String LAST_EMDEON_DATE = "LAST_EMDEON_DATE";
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
		public static final String TELMED_DISPOSITION = "TELMED_DISPOSITION";
		public static final String PAIN_STATEMENT = "PAIN_STATEMENT";
		public static final String DR_CHASE_AGENT = "DR_CHASE_AGENT";
		public static final String LAST_CHASE_DATE = "LAST_CHASE_DATE";
	}
	private Set<Entry<String, AgentReport>> SortMap(HashMap<String, AgentReport> map) {
		Set<Entry<String, AgentReport>> entries = map.entrySet();
        List<Entry<String, AgentReport>> listOfEntries = new ArrayList<Entry<String, AgentReport>>(entries);
        Collections.sort(listOfEntries, CompareLeadCount);
        LinkedHashMap<String, AgentReport> sortedByValue = new LinkedHashMap<String, AgentReport>(listOfEntries.size());
        for(Entry<String, AgentReport> entry : listOfEntries){
            sortedByValue.put(entry.getKey(), entry.getValue());
        }
        Set<Entry<String, AgentReport>> entrySetSortedByValue = sortedByValue.entrySet();
        return entrySetSortedByValue;
	}
	Comparator<Map.Entry<String, AgentReport>> CompareLeadCount = new Comparator<Map.Entry<String, AgentReport>>() {
			            @Override
			            public int compare(
			                  Map.Entry<String, AgentReport> e1,
			                  Map.Entry<String, AgentReport> e2) {
			 
			                Integer int1 = e1.getValue().getLeadCount();
			                Integer int2 = e2.getValue().getLeadCount();
			                return int2.compareTo(int1);
			            }
	};
}
