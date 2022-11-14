package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.LinkedList;

import org.postgresql.sspi.NTDSAPIWrapper;

import cache.AppCache;
import model.Branch;
import model.user.Employee;
import util.Factory;
import util.Util;


public class EmployeeDAO {
	public Employee create(int branchId, String name, String email, long phone) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		long employeeId = -1;
		Employee employee = null;
		boolean exceptionOccured = false;
		String password = Util.genPassword(), branchName = "", msg = "";

        try {
            conn = Factory.getDataSource().getConnection();
            stmt = conn.prepareStatement("INSERT INTO employee (name, password, phone, email, branch_id) values (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, name);
            stmt.setString(2, password);
            stmt.setLong(3, phone);
            stmt.setString(4, email);
            stmt.setInt(5, branchId);

            stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();

            if(rs.next()) {
                employeeId = rs.getLong(1);
             
                branchName = Factory.getBranchDAO().get(branchId).name;
                
                employee = new Employee(employeeId, name, password, email, phone, branchId, branchName);
            }
        } catch(SQLException e) {
        	System.out.println(e.getMessage());
            exceptionOccured = true;
            msg = "Internal error";
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
        	return employee;
	}
	
	
	public LinkedList<Employee> getAll(int branchId) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		LinkedList<Employee> employees = new LinkedList<Employee>();
		Employee employee = null;
		
		boolean exceptionOccured = false;
		String branchName = "", msg = "";
		
		long phone, id;
		String password;
        String name;
        String email;
		
		try {
			conn = Factory.getDataSource().getConnection();
			stmt = conn.prepareStatement("SELECT * FROM employee WHERE branch_id = ?");
			stmt.setInt(1, branchId);
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				id = rs.getLong("id");
                name = rs.getString("name");
                password = rs.getString("password");
                email = rs.getString("email");
                phone = rs.getLong("phone");
				
                // Get from cache
				if(branchName.equals(""))
					branchName = Factory.getBranchDAO().get(branchId).name;
					
				employee = new Employee(id, name, password, email, phone, branchId, branchName);	
                
                employees.add(employee);
			}
		} catch(SQLException e) {
			System.out.println(e.getMessage());
            exceptionOccured = true;
            msg = "Internal error";
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
        	return employees;
	}
	
	
	public Employee get(long id, int branchId) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		Employee employee = null;
		Branch branch = Factory.getBranchDAO().get(branchId);
		
		boolean exceptionOccured = false;
		String branchName = "", msg = "";
		
		long phone;
		String password;
        String name;
        String email;
        
        if(branch == null)
        	return null;
		
		try {
			employee = branch.getEmployee(id);
			
			if(employee == null) {
				conn = Factory.getDataSource().getConnection();
				stmt = conn.prepareStatement("SELECT * FROM employee WHERE id = ? AND branch_id = ?");
				stmt.setLong(1, id);
				stmt.setInt(2, branchId);
				rs = stmt.executeQuery();
				
				if(rs.next()) {
	                name = rs.getString("name");
	                password = rs.getString("password");
	                email = rs.getString("email");
	                phone = rs.getLong("phone");
	                branchName = branch.name;
	                
	                employee = new Employee(id, name, password, email, phone, branchId, branchName);
	                // add to cache.
	                branch.addEmployee(employee);
				}
			}
		} catch(SQLException e) {
			System.out.println(e.getMessage());
            exceptionOccured = true;
            msg = "Internal error";
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
        	return employee;
	}
	
	
	public boolean delete(long id, int branchId) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
				
		boolean exceptionOccured = false;
		String msg = "";
		int rowsAffected = 0;
		
		try {
			conn = Factory.getDataSource().getConnection();
			stmt = conn.prepareStatement("DELETE FROM employee WHERE id = ?");
			stmt.setLong(1, id);
			rowsAffected = stmt.executeUpdate();
			// update in cache if exists.
			AppCache.getBank().getBranch(branchId).removeEmployee(id);
		} catch(SQLException e) {
			System.out.println(e.getMessage());
            exceptionOccured = true;
            msg = "Internal error";
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
        else
        	return rowsAffected == 1;
	}
}