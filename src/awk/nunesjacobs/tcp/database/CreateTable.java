package awk.nunesjacobs.tcp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CreateTable {

	public static void main(String[] args) {

		Connection con = null;
		Statement stmt = null;
		@SuppressWarnings("unused")
		int result = 0;

		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
			con = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/weegeedeebee", "SA", "");
			stmt = con.createStatement();

			result = stmt.executeUpdate("CREATE TABLE chatHistory_tbl (id VARCHAR(36) NOT NULL, msgbody VARCHAR(8000) NOT NULL, login VARCHAR(8000) NOT NULL, receipent VARCHAR(8000) NOT NULL, sent_date DATE,PRIMARY KEY (id));");

		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		System.out.println("Table created successfully");
	}
}