package awk.nunesjacobs.tcp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SelectChatHistory{

	public static void main(String[] args) {
		Connection con = null;
		Statement stmt = null;
		ResultSet result = null;
		
		@SuppressWarnings("unused")
		String id,msgbody,login,receipent,sent_date;

		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
			con = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/weegeedeebee", "SA", "");
			stmt = con.createStatement();
			result = stmt.executeQuery("SELECT id, msgbody, login, receipent, sent_date FROM chatHistory_tbl ORDER BY sent_date DESC");

			while (result.next()) {
				
				id = result.getString("id");
				msgbody= result.getString("msgbody");
				login = result.getString("login");
				receipent = result.getString("receipent");
				sent_date= result.getString("sent_date");
				
				System.out.println(
						result.getString("id") + " | " + result.getString("sent_date") + " | " + result.getString("login")+ " => " + result.getString("receipent")+ " | " + result.getString("msgbody"));
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
}