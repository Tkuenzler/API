package client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ErrorSQLLog {
	
	public static int AddErrorMessage(int httpcode,String error,String endpoint, String location,String fax) {
		Connection connect = null;
		try {
			Class.forName("com.mysql.jdbc.Driver"); 
			 //Connect to database
			connect = DriverManager
					.getConnection("jdbc:mysql://ltf5469.tam.us.siteprotect.com:3306/Info_Table", "tkuenzler","Tommy6847");
			String sql = "INSERT INTO `ERROR_LOG`(`HTTP_CODE`,`ERROR`,`END_POINT`, `COMPANY`, `LOCATION`) VALUES ('"+httpcode+"','"+error+"','"+endpoint+"','MT_MARKETING','"+location+"','"+fax+"')";
			Statement stmt = connect.createStatement();
			int value = stmt.executeUpdate(sql);
			return value;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -99;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return e.getErrorCode();
		} finally {
			try {
				if(connect!=null)connect.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				return e.getErrorCode();
			} 
		}
	}
}