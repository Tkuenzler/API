package client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Database.Columns.TelmedColumns;
import Database.Query.Query;

public class Database {
	String database;
	Connection connect = null;
	public Database(String database) {
		this.database = database;
	}
	public boolean login() throws SQLException  {
		 try {
			//This will load the MySQL driver, each DB has its own driver
			 Class.forName("com.mysql.jdbc.Driver");
			//Connect to database
			 connect = DriverManager
				      .getConnection("jdbc:mysql://ltf5469.tam.us.siteprotect.com:3306/"+database, "tkuenzler","Tommy6847");
			 return true;
		 } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
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
	public int insert(String table,String[] columns,Object[] params) throws SQLException {
		Query query = new Query();
		query.insert(table).columns(columns).values(params);
		PreparedStatement stmt = connect.prepareStatement(query.getQuery());
		if(params != null){
			int index = 1;
	        for(Object param : params){
	        	stmt.setObject(index, param);
	            index++;
	         }
	    }
		int value = stmt.executeUpdate();
		stmt.close();
		return value;
	}
	public int update(String table,String[] columns,Object[] params,String requirement) throws SQLException {
		Query query = new Query();
		query.update(table).set(columns).where(requirement);
		PreparedStatement stmt = connect.prepareStatement(query.getQuery());
		if(params != null){
			int index = 1;
	        for(Object param : params){
	        	stmt.setObject(index, param);
	            index++;
	         }
	    }
		return stmt.executeUpdate();
	}
	public String test(String table,String[] columns,String[] params,String requirement)  {
		Query query = new Query();
		query.update(table).set(columns).values(params).where(requirement);
		return query.getQuery();
	}
	public boolean exists(String table, Object[] columns, String requirement, Object[] params) throws SQLException {
		PreparedStatement stmt = null;
		Query query = new Query();
		query.select(columns).from(table).where(requirement);
		stmt = connect.prepareStatement(query.getQuery());
		if(params != null){
			int index = 1;
	        for(Object param : params){
	        	stmt.setObject(index, param);
	            index++;
	         }
	    }
		ResultSet set = stmt.executeQuery();
		if(set.next())
			return true;
		else
			return false;
	}
	public ResultSet select(String table, Object[] columns, String requirement, Object[] params) throws SQLException {
		PreparedStatement stmt = null;
		Query query = new Query();
		query.select(columns).from(table).where(requirement);
		stmt = connect.prepareStatement(query.getQuery());
		if(params != null){
			int index = 1;
	        for(Object param : params){
	        	stmt.setObject(index, param);
	            index++;
	         }
	    }
		return stmt.executeQuery();
	}
	public ResultSet selectAs(String table, Object[] columns,String columnName, String requirement, Object[] params) throws SQLException {
		PreparedStatement stmt = null;
		Query query = new Query();
		query.select(columns).as(columnName).from(table).where(requirement);
		stmt = connect.prepareStatement(query.getQuery());
		if(params != null){
			int index = 1;
	        for(Object param : params){
	        	stmt.setObject(index, param);
	            index++;
	         }
	    }
		return stmt.executeQuery();
	}
	public ResultSet selectSort(String table, Object[] columns, String requirement, Object[] params,String[] order_columns,String[] order_by) throws SQLException {
		PreparedStatement stmt = null;
		Query query = new Query();
		query.select(columns).from(table).where(requirement).orderby(order_columns, order_by);
		stmt = connect.prepareStatement(query.getQuery());
		if(params != null){
			int index = 1;
	        for(Object param : params){
	        	stmt.setObject(index, param);
	            index++;
	         }
	    }
		return stmt.executeQuery();
	}
	public int delete(String table,String requirement) throws SQLException {
		Query query = new Query();
		query.delete(table).where(requirement);
		Statement stmt = connect.createStatement();
		return stmt.executeUpdate(query.getQuery());
	}
	
}
