package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import model.Address;
import model.Branch;
import model.user.*;
import util.Factory;


public class BranchDAO {
	public LinkedList<Branch> getAll() throws SQLException {
		LinkedList<Branch> branches = new LinkedList<Branch>();
		Branch branch = null;
		Employee manager = null;
		Address address = null;
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		boolean exceptionOccured = false;
        String doorNo, street, city, state, msg="";
        String managerName, managerEmail, managerPassword;
        long managerId, managerPhone;
        int pincode;
		
		try {
			conn = Factory.getDataSource().getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT b.id, b.name, door_no, street, city, state, pincode, m.id as manager_id, m.name as manager_name, m.email as manager_email, m.phone as manager_phone, m.password as manager_password FROM branch b JOIN manager m ON b.id = m.branch_id ORDER BY b.name");
			
			while(rs.next()) {
                doorNo = rs.getString("door_no");
                street = rs.getString("street");
                city = rs.getString("city");
                state = rs.getString("state");
                pincode = rs.getInt("pincode");
                
				address = new Address(doorNo, street, city, state, pincode);
				branch = new Branch(rs.getInt("id"), rs.getString("name"), address) ;
                
                managerId = rs.getLong("manager_id");
                managerName = rs.getString("manager_name");
                managerPhone = rs.getLong("manager_phone");
                managerEmail = rs.getString("manager_email");
                managerPassword = rs.getString("manager_password");
                
                manager = new Employee(managerId, managerName, managerPassword, managerEmail, managerPhone, branch.getId(), branch.name);
                branch.assignManager(manager);
                
                branches.add(branch);
			}
		} catch(SQLException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = e.getMessage();
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
			return branches;
	}
	
	
	public Branch get(int id) throws SQLException {
		Branch branch = null;
		Employee manager = null;
		Address address = null;
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		boolean exceptionOccured = false;
        String doorNo, street, city, state, msg="";
        String managerName, managerEmail, managerPassword;
        long managerId, managerPhone;
        int pincode;
		
		try {
			conn = Factory.getDataSource().getConnection();
			stmt = conn.prepareStatement("SELECT b.id, b.name, door_no, street, city, state, pincode, m.id as manager_id, m.name as manager_name, m.email as manager_email, m.phone as manager_phone, m.password as manager_password FROM branch b JOIN manager m ON b.id = m.branch_id WHERE branch_id = ?");
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			
			if(rs.next()) {
                doorNo = rs.getString("door_no");
                street = rs.getString("street");
                city = rs.getString("city");
                state = rs.getString("state");
                pincode = rs.getInt("pincode");
                
				address = new Address(doorNo, street, city, state, pincode);
				branch = new Branch(rs.getInt("id"), rs.getString("name"), address) ;
                
                managerId = rs.getLong("manager_id");
                managerName = rs.getString("manager_name");
                managerPhone = rs.getLong("manager_phone");
                managerEmail = rs.getString("manager_email");
                managerPassword = rs.getString("manager_password");
                
                manager = new Employee(managerId, managerName, managerPassword, managerEmail, managerPhone, branch.getId(), branch.name);
                branch.assignManager(manager);
			}
		} catch(SQLException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = e.getMessage();
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
			return branch;
	}
	
	
	// Adds a new branch to DB.
	public Branch create(Connection conn, String name, Address address) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		Branch branch = null;
		String msg = null;
		boolean exceptionOccured = false;
		int branchId = 0;
				
		try {
            stmt = conn.prepareStatement("INSERT INTO branch (name, door_no, street, city, state, pincode) values (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, name);
            stmt.setString(2, address.getDoorNo());
            stmt.setString(3, address.getStreet());
            stmt.setString(4, address.getCity());
            stmt.setString(5, address.getState());
            stmt.setLong(6, address.getPincode());

            stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();

            if(rs.next()) {
                branchId = rs.getInt(1);
            }

            branch = new Branch(branchId, name, address);
        } catch(SQLException e) {
        	System.out.println(e.getMessage());
            exceptionOccured = true;
            msg = "Error adding branch";
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
			return branch;
	}
	
	
	// deletes a branch from DB.
	public void delete(Connection conn, int id) throws SQLException {
		PreparedStatement stmt = null;
		
		String msg = "";
		boolean exceptionOccured = false;
		
		try {
			stmt = conn.prepareStatement("DELETE FROM branch WHERE id = ?");
			stmt.setInt(1,  id);
			stmt.executeUpdate();
		} catch(SQLException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "Error removing branch";
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