package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import model.UserBean;
import util.Util;

public class ManagerDAO {
	private DataSource dataSource;
	
	public ManagerDAO() {
		try {			
			Context context = new InitialContext();
			dataSource = (DataSource) context.lookup("java:comp/env/jdbc/newbankdb");
		} catch(NamingException e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	public void create(int branchId, String managerName, 
						String managerEmail, long managerPhone) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String managerPassword = Util.genPassword(), msg = "";
		boolean exceptionOccured = false;
		long managerId = 0;
		
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("INSERT INTO manager (name, password, phone, email, branch_id) values (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
	        stmt.setString(1, managerName);
	        stmt.setString(2, managerPassword);
	        stmt.setLong(3, managerPhone);
	        stmt.setString(4, managerEmail);
	        stmt.setInt(5, branchId);

	        stmt.executeUpdate();
	        rs = stmt.getGeneratedKeys();

	        if(rs.next()) {
	            managerId = rs.getLong(1);
	        }
		} catch(SQLException e) {
			exceptionOccured = true;
			msg = "cannot create manager";
		} finally {
            try {
                if(rs != null)
                    rs.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }

            try {
                if(stmt != null)
                    stmt.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }

            try {
                if(conn != null)
                    conn.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
        }
		
		if(exceptionOccured)
			throw new SQLException(msg);
	}
	
	
	public void delete(long id) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		String msg = "";
		boolean exceptionOccured = false;
		
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("DELETE FROM manager WHERE id = ?");
			stmt.setLong(1,  id);
			stmt.executeUpdate();
		} catch(SQLException e) {
			exceptionOccured = true;
			msg = "Error removing manager";
		} finally {
            try {
                if(stmt != null)
                    stmt.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }

            try {
                if(conn != null)
                    conn.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
        }
		
		if(exceptionOccured)
			throw new SQLException(msg);
	}
}