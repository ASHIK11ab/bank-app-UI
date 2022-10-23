package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import model.EmployeeBean;
import model.UserBean;
import util.Factory;
import util.Util;

public class ManagerDAO {
	// Create a new manager account and assign to branch.
	public void create(Connection conn, int branchId, String managerName, 
						String managerEmail, long managerPhone) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String managerPassword = Util.genPassword(), msg = "";
		boolean exceptionOccured = false;
		long managerId = 0;
		
		try {
			stmt = conn.prepareStatement("INSERT INTO manager (name, password, phone, email, branch_id) values (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			// Assign new manager.
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
			msg = "error assigning manager";
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
			throw new SQLException(msg);
	}
	
	
	public EmployeeBean get(long id) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		EmployeeBean manager = null;
		boolean exceptionOccured = false;
		String msg = "";
		
		try {
			conn = Factory.getDataSource().getConnection();
			stmt = conn.prepareStatement("SELECT m.id, m.name as manager_name, m.phone, m.email, m.password, m.branch_id, b.name as branch_name FROM manager m JOIN branch b ON m.branch_id = b.id WHERE m.id = ?");
			stmt.setLong(1, id);
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				manager = new EmployeeBean();
				manager.setId(rs.getLong("id"));
				manager.setName(rs.getString("manager_name"));
				manager.setPhone(rs.getLong("phone"));
				manager.setEmail(rs.getString("email"));
				manager.setPassword(rs.getString("password"));
				manager.setBranchId(rs.getInt("branch_id"));
				manager.setBranchName(rs.getString("branch_name"));
			}
		} catch(SQLException e) {
			exceptionOccured = true;
			msg = "internal error";
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
		else
			return manager;
	}
	
	
	public LinkedList<EmployeeBean> getAll() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		LinkedList<EmployeeBean> managers = new LinkedList<EmployeeBean>();
		EmployeeBean manager = null;
		boolean exceptionOccured = false;
		String msg = "";
		
		try {
			conn = Factory.getDataSource().getConnection();
			stmt = conn.createStatement();	
			rs = stmt.executeQuery("SELECT m.id, m.name as manager_name, m.phone, m.email, m.password, m.branch_id, b.name as branch_name FROM manager m JOIN branch b ON m.branch_id = b.id");
			
			while(rs.next()) {
				manager = new EmployeeBean();
				manager.setId(rs.getLong("id"));
				manager.setName(rs.getString("manager_name"));
				manager.setPhone(rs.getLong("phone"));
				manager.setEmail(rs.getString("email"));
				manager.setPassword(rs.getString("password"));
				manager.setBranchId(rs.getInt("branch_id"));
				manager.setBranchName(rs.getString("branch_name"));
				
				managers.add(manager);
			}
		} catch(SQLException e) {
			exceptionOccured = true;
			msg = "internal error";
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
		else
			return managers;
	}
	
	
	public void delete(Connection conn, long id) throws SQLException {
		PreparedStatement stmt = null;
		
		String msg = "";
		boolean exceptionOccured = false;
		
		try {
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
        }
		
		if(exceptionOccured)
			throw new SQLException(msg);
	}
}