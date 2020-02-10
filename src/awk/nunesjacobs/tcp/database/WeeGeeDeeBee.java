package awk.nunesjacobs.tcp.database;

import java.sql.Connection;
import java.sql.DriverManager;

public class WeeGeeDeeBee {

	public static void main(String[] args) {

		Connection con = null;
		// Connect
		//java -classpath lib/hsqldb.jar org.hsqldb.server.Server --database.0 file:hsqldb/WeeGeeDB --dbname.0 weegeedeebee
		try {
			// Registering the HSQLDB JDBC driver
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
			// Creating the connection with HSQLDB
			con = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/weegeedeebee", "SA", "");
			if (con != null) {
				System.out.println("Connection created successfully");

			} else {
				System.out.println("Problem with creating connection");
			}

		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
}
