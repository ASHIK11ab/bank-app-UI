package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import model.EmployeeBean;
import util.Factory;
import util.Util;


public class EmployeeDAO {
	public EmployeeBean create(int branchId, String name, String email, long phone) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		long employeeId = -1;
		EmployeeBean employee = null;
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
                employee = new EmployeeBean();
                employee.setId(employeeId);
                employee.setName(name);
                employee.setPassword(password);
                employee.setPhone(phone);
                employee.setEmail(email);
                employee.setBranchId(branchId);

                branchName = Factory.getBranchDAO().get(branchId).getName();
                employee.setBranchName(branchName);
            }
        } catch(SQLException e) {
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
	
	
	public LinkedList<EmployeeBean> getAll(int branchId) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		LinkedList<EmployeeBean> employees = new LinkedList<EmployeeBean>();
		EmployeeBean employee = null;
		
		boolean exceptionOccured = false;
		String branchName = "", msg = "";
		
		try {
			conn = Factory.getDataSource().getConnection();
			stmt = conn.prepareStatement("SELECT * FROM employee WHERE branch_id = ?");
			stmt.setInt(1, branchId);
			rs = stmt.executeQuery();
			
			while(rs.next()) {
                employee = new EmployeeBean();
                employee.setId(rs.getLong("id"));
                employee.setName(rs.getString("name"));
                employee.setPassword(rs.getString("password"));
                employee.setPhone(rs.getLong("phone"));
                employee.setEmail(rs.getString("email"));
                employee.setBranchId(branchId);
                	
                if(branchName.equals(""))
                	branchName = Factory.getBranchDAO().get(branchId).getName();
                
                employee.setBranchName(branchName);
                
                employees.add(employee);
			}
		} catch(SQLException e) {
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
	
	
	public EmployeeBean get(long id) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		EmployeeBean employee = null;
		
		boolean exceptionOccured = false;
		String branchName = "", msg = "";
		
		try {
			conn = Factory.getDataSource().getConnection();
			stmt = conn.prepareStatement("SELECT * FROM employee WHERE id = ?");
			stmt.setLong(1, id);
			rs = stmt.executeQuery();
			
			while(rs.next()) {
                employee = new EmployeeBean();
                employee.setId(rs.getLong("id"));
                employee.setName(rs.getString("name"));
                employee.setPassword(rs.getString("password"));
                employee.setPhone(rs.getLong("phone"));
                employee.setEmail(rs.getString("email"));
                employee.setBranchId(rs.getInt("branch_id"));
                	
                if(branchName.equals(""))
                	branchName = Factory.getBranchDAO().get(employee.getBranchId()).getName();
                
                employee.setBranchName(branchName);
			}
		} catch(SQLException e) {
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
			stmt = conn.prepareStatement("DELETE FROM employee WHERE id = ? AND branch_id = ?");
			stmt.setLong(1, id);
			stmt.setInt(2, branchId);
			rowsAffected = stmt.executeUpdate();
		} catch(SQLException e) {
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