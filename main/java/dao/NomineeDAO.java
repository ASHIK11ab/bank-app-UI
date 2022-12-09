package dao;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Nominee;

public class NomineeDAO {
	public Nominee create(Connection conn, String name, long adhaar, long phone,
							String relationship, long customerId) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO nominee (customer_id, name, adhaar, phone, relationship) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		ResultSet rs = null;
		
		Nominee nominee = null;
		long nomineeId;
		boolean exceptionOccured = false;
		
		try {
			stmt.setLong(1, customerId);
			stmt.setString(2, name);
			stmt.setLong(3, adhaar);
			stmt.setLong(4, phone);
			stmt.setString(5, relationship);	
			
			stmt.executeUpdate();
			
			rs = stmt.getGeneratedKeys();
			
			if(rs.next()) {
				nomineeId = rs.getLong(1);
				nominee = new Nominee(nomineeId, name, adhaar, phone, relationship);
			}
		} catch(SQLException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
		} finally {
			try {
				if(rs != null)
					rs.close();
			} catch(SQLException e) { System.out.println(e.getMessage()); } 
			
			try {
				if(stmt != null)
					stmt.close();
			} catch(SQLException e) { System.out.println(e.getMessage()); } 
		}
		
		if(exceptionOccured)
			throw new SQLException("internal error");
		else
			return nominee;
	}
	
	
	public Nominee get(Connection conn, long id) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM nominee WHERE id = ?");
		ResultSet rs = null;
		
		Nominee nominee = null;
		long nomineeId, adhaar, phone;
		String name, relationship;
		boolean exceptionOccured = false;
		
		try {
			stmt.setLong(1, id);	
			
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				nomineeId = rs.getLong("id");
				name = rs.getString("name");
				adhaar = rs.getLong("adhaar");
				phone = rs.getLong("phone");
				relationship = rs.getString("relationship");
				nominee = new Nominee(nomineeId, name, adhaar, phone, relationship);
			}
		} catch(SQLException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
		} finally {
			try {
				if(rs != null)
					rs.close();
			} catch(SQLException e) { System.out.println(e.getMessage()); } 
			
			try {
				if(stmt != null)
					stmt.close();
			} catch(SQLException e) { System.out.println(e.getMessage()); } 
		}
		
		if(exceptionOccured)
			throw new SQLException("internal error");
		else
			return nominee;
	}
}