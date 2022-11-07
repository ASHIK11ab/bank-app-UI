package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import cache.AppCache;
import model.Branch;
import model.user.Employee;
import util.Factory;
import util.Util;

public class ManagerDAO {
	// Create a new manager account and assign to branch.
	public Employee create(Connection conn, int branchId, String managerName, 
								String managerEmail, long managerPhone) throws SQLException {
		BranchDAO branchDAO = Factory.getBranchDAO();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		Employee manager = null;
		String managerPassword = Util.genPassword(), msg = "", branchName;
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
	            
	            branchName = branchDAO.get(branchId).name;
	            manager = new Employee(managerId, managerName, managerPassword, managerEmail, managerPhone, branchId, branchName);
	        }
		} catch(SQLException e) {
			System.out.println(e.getMessage());
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
		else
			return manager;
	}
	
	
	public Employee get(long id, int branchId) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		Branch branch = AppCache.getBranch(branchId);
		Employee manager = null;
		boolean exceptionOccured = false;
		String msg = "", branchName = "";
		
		long phone;
		String password;
        String name;
        String email;
        
        if(branch == null)
        	return null;
		
		try {
			manager = branch.getManager();
			if(manager != null && manager.getId() != id)
				return null;
			
			// load from DB if not found in cache.
			if(manager == null) {
				conn = Factory.getDataSource().getConnection();
				stmt = conn.prepareStatement("SELECT * FROM manager WHERE id = ? AND branch_id = ?");
				stmt.setLong(1, id);
				stmt.setInt(2, branchId);
				rs = stmt.executeQuery();
				
				if(rs.next()) {
	                name = rs.getString("name");
	                password = rs.getString("password");
	                email = rs.getString("email");
	                phone = rs.getLong("phone");
					
	                // Get from cache
					branchName = Factory.getBranchDAO().get(branchId).name;
					
					manager = new Employee(id, name, password, email, phone, branchId, branchName);
				}
			}
		} catch(SQLException e) {
			System.out.println(e.getMessage());
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
	
	
	public LinkedList<Employee> getAll() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		LinkedList<Employee> managers = new LinkedList<Employee>();
		Employee manager = null;
		boolean exceptionOccured = false;
		String msg = "", branchName = "";
		
		long phone, id;
		String password;
        String name;
        String email;
        int branchId;
		
		try {
			conn = Factory.getDataSource().getConnection();
			stmt = conn.createStatement();	
			rs = stmt.executeQuery("SELECT * FROM manager");
			
			while(rs.next()) {
				id = rs.getLong("id");
                name = rs.getString("name");
                password = rs.getString("password");
                email = rs.getString("email");
                phone = rs.getLong("phone");
                branchId = rs.getInt("branch_id");
				
                // Get from cache
				if(branchName.equals(""))
					branchName = Factory.getBranchDAO().get(branchId).name;
					
				manager = new Employee(id, name, password, email, phone, branchId, branchName);				
				managers.add(manager);
			}
		} catch(SQLException e) {
			System.out.println(e.getMessage());
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
			System.out.println(e.getMessage());
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