package awk.nunesjacobs.tcp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SelectQuery {

	public static void main(String[] args) {
		Connection con = null;
		Statement stmt = null;
		ResultSet result = null;

		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
			con = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/weegeedeebee", "SA", "");
			stmt = con.createStatement();
			result = stmt.executeQuery("SELECT id, msgbody, login, receipent, sent_date FROM chatHistory_tbl");

			while (result.next()) {
				System.out.println(
						result.getInt("id") + " | " + result.getString("msgbody") + " | " + result.getString("login")+ " | " + result.getString("receipent")+ " | " + result.getString("sent_date"));
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
}