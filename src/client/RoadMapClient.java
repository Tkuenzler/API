package client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;

import PBM.InsuranceFilter;
import PBM.InsuranceType;
import Pharmacy.PharmacyMap;
import Pharmacy.RoadMap;

public class RoadMapClient {
	String table,database;
	public Connection connect = null;
	private final String HOST_NAME = "ltf5469.tam.us.siteprotect.com";
	public RoadMapClient(String table) {
		try {
			//This will load the MySQL driver, each DB has its own driver
			 Class.forName("com.mysql.jdbc.Driver"); 
			 connect = DriverManager
				      .getConnection("jdbc:mysql://"+HOST_NAME+":3306/Road_Map", "tkuenzler","Tommy6847");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		this.database = "Road_Map";
		this.table = table;
	}
	public void setTable(String pharmacy) {
		this.table = pharmacy;
	}
	public ArrayList<PharmacyMap> getPharmaciesForTelmed(String type) {
		ArrayList<PharmacyMap> map = new ArrayList<PharmacyMap>();
		String sql = null;
		switch(type) {
			case "Medicare":
			case InsuranceType.MEDICARE_TELMED:
				sql = "SELECT * FROM `"+table+"` WHERE `MEDICARE_TELMED` = 1 OR `COMMERCIAL_TELMED` = 1";
				break;
			case InsuranceType.PRIVATE_VERIFIED:
			case InsuranceType.PRIVATE_UNKNOWN:
			case "Private Insurance":
			case "Marketplace":
			case "Provided by Job":
				sql = "SELECT * FROM `"+table+"` WHERE `COMMERCIAL_TELMED` = 1";
				break;
			default:
				return null;
		}
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			while(set.next()) {
				PharmacyMap pharmacy = new PharmacyMap(set);
				LoadAllStates(pharmacy);
				map.add(pharmacy);
			}
			return map;
		} catch(SQLException ex) {
			return null;
		} finally {
			try {
				if(set!=null)set.close();
				if(stmt!=null)stmt.close();
			} catch(SQLException ex) {
				
			} 
		}
	}
	public String getPharmacyQueryForDrChase() {
		String sql = "SELECT * FROM `"+table+"` WHERE `FAX_CHASE` = 1";
		Statement stmt = null;
		ResultSet set = null;
		try {
			StringBuilder sb = new StringBuilder();
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			sb.append("(");
			while(set.next()) {
				String pharmacy = set.getString("PHARMACY");
				sb.append("`PHARMACY` = '"+pharmacy+"'");
				if(!set.isLast())
					sb.append(" OR ");
			} 
			sb.append(")");
			return sb.toString();
		} catch (SQLException e) {
			return "";
		} finally {
			try {
				if(set!=null) set.close();
				if(stmt!=null) stmt.close();	
			} catch (SQLException e) {
				
			}
		}
	}
	public String[] getPharmacyForDrChase() {
		String sql = "SELECT * FROM `"+table+"` WHERE `FAX_CHASE` > 0";
		Statement stmt = null;
		ResultSet set = null;
		try {
			ArrayList<String> list = new ArrayList<String>();
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			while(set.next()) 
				list.add(set.getString("PHARMACY"));
			return list.toArray(new String[list.size()]);
		} catch (SQLException e) {
			return null;
		} finally {
			try {
				if(set!=null) set.close();
				if(stmt!=null) stmt.close();	
			} catch (SQLException e) {
				
			}
		}
	}
	public JSONObject getPharmaciesAsJSON() {
		String sql = "SELECT * FROM `"+table+"`";
		Statement stmt = null;
		ResultSet set = null;
		JSONObject obj = new JSONObject();
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			obj.put("0", "ALL");
			int count = 1;
			
			while(set.next()) {
				String pharmacy = set.getString("PHARMACY");
				obj.put(count+"", pharmacy);
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
	public PharmacyMap getPharmacy(String pharmacy) {
		String sql = "SELECT * FROM `"+table+"` WHERE `PHARMACY` = '"+pharmacy+"'";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			if(set.next()) 
				return new PharmacyMap(set);
			else
				return null;
		} catch(SQLException ex) {
			ex.printStackTrace();
			return null;
		} finally {
			try {
				if(set!=null) set.close();
				if(stmt!=null)stmt.close();
			} catch(SQLException ex) {
				
			} 
		}
	}
	public void LoadAllStates(PharmacyMap map) {
		String sql = "SELECT * FROM `"+map.getPharmacyName()+"`";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			while(set.next()) {
				map.addState(new RoadMap(map.getPharmacyName(),set));
			}
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
	}
	public String[] getPharmacies() {
		String sql = "SELECT * FROM `"+table+"` WHERE `FAX_CHASE` = 1";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			List<String> list = new ArrayList<String>();
			while(set.next())
				list.add(set.getString("PHARMACY"));
			return list.toArray(new String[list.size()]);
		}catch(SQLException e) {
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
	public boolean AcceptableTelmedPharmacy(String pharmacy) {
		String sql = "SELECT * FROM `"+table+"` WHERE `PHARMACY` = '"+pharmacy+"' AND (`MEDICARE_TELMED` >= 1 OR `COMMERCIAL_TELMED` >= 1)";
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
	public boolean CanPharmacyTake(Record record,String pharmacy) {
		String sql = "SELECT * FROM `"+pharmacy+"` WHERE `State` = '"+record.getState()+"' AND `"+record.getCarrier()+"` >= 1";
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
	private class PharmacyModel {
		public static final int DR_CHASE_ONLY = 1;
		public static final int CAN_TELMED_PRIVATE = 2;
		public static final int CAN_TELMED_MEDICARE = 3;
		
	}
}
