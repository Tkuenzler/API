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
	//
	public String getPharmaciesQuery() {
		String sql = "SELECT * FROM `"+table+"`";
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
	public String[] getPbms() {
		String sql = "SELECT * FROM `BLANK_ROADMAP` ORDER BY `State` ASC";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			List<String> list = new ArrayList<String>();
			ResultSetMetaData data = set.getMetaData();
			for(int i = 1;i<=data.getColumnCount();i++) {
				if(data.getColumnName(i).equalsIgnoreCase("State"))
					continue;
				else
					list.add(data.getColumnName(i));
			}
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
	//TELMED FUNCTIONS
	private String[] getMedicareTelmedPharmacies() {
		String sql = "SELECT * FROM `"+table+"` WHERE `MEDICARE_TELMED` > 0";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			ArrayList<String> list = new ArrayList<String>();
			while(set.next()) {
				list.add(set.getString("PHARMACY"));
			}
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
	private String[] getPrivateTelmedPharmacies() {
		String sql = "SELECT * FROM `"+table+"` WHERE `COMMERCIAL_TELMED` > 0";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			ArrayList<String> list = new ArrayList<String>();
			while(set.next()) {
				list.add(set.getString("PHARMACY"));
			}
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
	public String getPrivateTelmedPharmacy(Record record) {
		String[] list = getPrivateTelmedPharmacies();
		for(String pharmacy: list) {
			if(CanPharmacyTakeTelmed(pharmacy,record,PharmacyModel.CAN_TELMED_PRIVATE))
				return pharmacy;
		}
		return null;	
	}
	public String getMedicareTelmedPharmacy(Record record) {
		String[] list = getMedicareTelmedPharmacies();
		for(String pharmacy: list) {
			if(CanPharmacyTakeTelmed(pharmacy,record,PharmacyModel.CAN_TELMED_MEDICARE))
				return pharmacy;
		}
		return null;	
	}
	private boolean CanPharmacyTakeTelmed(String pharmacy, Record record, int telmedType) {
		String sql = "SELECT * FROM `"+pharmacy+"` WHERE `State` = '"+record.getState()+"' AND `"+record.getCarrier()+"` >= "+telmedType;
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
			return false;
		} finally {
			try {
				if(set!=null) set.close();
				if(stmt!=null) stmt.close();	
			} catch (SQLException e) {
				
			}
		}
	}
	public String[] getPharmacies() {
		String sql = "SHOW TABLES";
		Statement stmt = null;
		ResultSet pharmacies = null;
		try {
			stmt = connect.createStatement();
			pharmacies = stmt.executeQuery(sql);
			List<String> list = new ArrayList<String>();
			list.add("No Home");
			list.add("Medicaid");
			list.add("Not Found");
			while(pharmacies.next()) {
				String pharmacy = pharmacies.getString("Tables_in_Road_Map");
				switch(pharmacy) {
					case "BLANK_MAP":
					case "TELMED_ROADMAP":
					case "CLN_ROADMAP":
					case "SKYLINE_ROADMAP":
						continue;
					default:
						list.add(pharmacy);
				}
				
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
	
	
	
	
	public String CanTelmedGetPharmacy(Record record,String type) {
		switch(type) {
			case "Medicare":
				return CheckTelmedMedicare(record);
			case "Private Insurance":
			case "Provided by Job":
			case "Marketplace":
				return CheckTelmedPrivate(record);
			default: 
				return null;
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
	public PharmacyOdds[] GetInStatePharmacies(Record record) {
		int type = PBM.InsuranceFilter.GetInsuranceType(record);
		String sql = null;
		if(type==PBM.InsuranceType.Type.MEDICARE_INSURANCE) 
			sql =  "SELECT * FROM `"+table+"` WHERE `FAX_CHASE` > 0 AND `State`  = '"+record.getState()+"' AND `MEDICARE_TIER` > 0";
		else if(type==PBM.InsuranceType.Type.PRIVATE_INSURANCE) 
			sql =  "SELECT * FROM `"+table+"` WHERE `FAX_CHASE` > 0 AND `State`  = '"+record.getState()+"' AND `COMMERCIAL_TIER` > 0";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			List<PharmacyOdds> list = new ArrayList<PharmacyOdds>();
			while(set.next())
				list.add(new PharmacyOdds(set.getString("PHARMACY"),set.getDouble("EXTRA")));
			if(list.size()>0)
				return list.toArray(new PharmacyOdds[list.size()]);
			else 
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
	public PharmacyOdds[] GetPharmacyList(Record record) {
		int type = PBM.InsuranceFilter.GetInsuranceType(record);
		String sql = null;
		if(type==PBM.InsuranceType.Type.MEDICARE_INSURANCE) 
			sql =  "SELECT * FROM `"+table+"` WHERE `FAX_CHASE` > 0 AND `MEDICARE_TIER` > 0";
		else if(type==PBM.InsuranceType.Type.PRIVATE_INSURANCE) 
			sql =  "SELECT * FROM `"+table+"` WHERE `FAX_CHASE` > 0 AND `COMMERCIAL_TIER` > 0";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			List<PharmacyOdds> list = new ArrayList<PharmacyOdds>();
			while(set.next()) {
				list.add(new PharmacyOdds(set.getString("PHARMACY"),set.getDouble("EXTRA")));
			}
			if(list.size()>0)
				return list.toArray(new PharmacyOdds[list.size()]);
			else 
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
	public PharmacyOdds[] GetNotFoundPharmacyList() {
		String sql = "SELECT * FROM `"+table+"` WHERE `FAX_CHASE` > 0 AND `NOT_FOUND` > 0";
		Statement stmt = null;
		ResultSet set = null;
		try {
			stmt = connect.createStatement();
			set = stmt.executeQuery(sql);
			List<PharmacyOdds> list = new ArrayList<PharmacyOdds>();
			while(set.next()) {
				list.add(new PharmacyOdds(set.getString("PHARMACY"),set.getDouble("EXTRA")));
			}
			if(list.size()>0)
				return list.toArray(new PharmacyOdds[list.size()]);
			else 
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
	public boolean CanTakeNotFound(Record record,String pharmacy) {
		String sql = "SELECT * FROM `"+pharmacy+"` WHERE `State` = '"+record.getState()+"' AND `Not Found` = 1";
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
	
	//CHECK TO SEE IF PATIENT IS ELIGIBLE FOR RUNNING INSURANCE BY CHECK INSURANCE TYPE AND STATE
	
	public boolean CanTelmed(Record record,String type) {
		switch(type) {
			case "Medicare":
				if(CheckTelmedMedicare(record)!=null)
					return true;
				else
					return false;
			case "Private Insurance":
			case "Provided by Job":
			case "Marketplace":
				if(CheckTelmedPrivate(record)!=null)
					return true;
				else
					return false;
			default: 
				return false;
		}
	}
	
	public String CheckTelmedMedicare(Record record) {
		String[] pharmacies = getMedicareTelmedPharmacies();
		for(String pharmacy: pharmacies) {
			if(CanStateTelemd(record,pharmacy,PharmacyModel.CAN_TELMED_MEDICARE))
				return pharmacy;
		}
		return null;
	}
	public String CheckTelmedPrivate(Record record) {
		String[] pharmacies = getPrivateTelmedPharmacies();
		for(String pharmacy: pharmacies) {
			if(CanStateTelemd(record,pharmacy,PharmacyModel.CAN_TELMED_PRIVATE))
				return pharmacy;
		}
		return null;
	}
	private boolean CanStateTelemd(Record record,String pharmacy,int insuranceType) {
		String[] pbms = getPbms();
		for(String pbm: pbms) {
			if(CheckStatePBM(record.getState(),pbm,pharmacy,insuranceType))
				return true;
		}
		return false;
	}	
	private boolean CheckStatePBM(String state,String pbm,String pharmacy,int insuranceType) {
		String sql = "SELECT * FROM `"+pharmacy+"` WHERE `State` = '"+state+"' AND `"+pbm+"` >= "+insuranceType;
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
