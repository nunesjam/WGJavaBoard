package awk.nunesjacobs.tcp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.UUID;

public class InsertQuery {
	String msgbody;
	String login;
	String receipent;

	public InsertQuery(String msgBody, String login, String receipent) {
		this.msgbody=msgBody;
		this.login=login;
		this.receipent= receipent;
	}
	public void insert(){
		Connection con = null;
		Statement stmt = null;
		String uniqueID = UUID.randomUUID().toString();
		int result = 0;
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
			con = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/weegeedeebee", "SA", "");
			stmt = con.createStatement();
			stmt.executeUpdate(String.format("INSERT INTO chatHistory_tbl VALUES (%s,'%s', '%s', '%s', NOW())",uniqueID.strip(), msgbody.strip(),login.strip(),receipent.strip()));
			con.commit();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		System.out.println(result + " rows effected");
		System.out.println("Rows inserted successfully");
	}

}