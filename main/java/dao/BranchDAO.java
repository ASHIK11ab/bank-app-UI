package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import model.AddressBean;
import model.BranchBean;
import model.EmployeeBean;
import model.UserBean;
import util.Factory;
import util.Util;

public class BranchDAO {
	public LinkedList<BranchBean> getAll() throws SQLException {
		LinkedList<BranchBean> branches = new LinkedList<BranchBean>();
		BranchBean branch = null;
		EmployeeBean manager = null;
		AddressBean address = null;
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		boolean exceptionOccured = false;
		String msg = "";
		
		try {
			conn = Factory.getDataSource().getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT b.id, b.name, door_no, street, city, state, pincode, m.id as manager_id, m.name as manager_name, m.email as manager_email, m.phone as manager_phone, m.password as manager_password FROM branch b JOIN manager m ON b.id = m.branch_id");
			
			while(rs.next()) {
				branch = new BranchBean();
				address = new AddressBean();
				manager = new EmployeeBean();
				
				branch.setId(rs.getInt("id"));
				branch.setName(rs.getString("name"));
				
                address.setDoorNo(rs.getString("door_no"));
                address.setStreet(rs.getString("street"));
                address.setCity(rs.getString("city"));
                address.setState(rs.getString("state"));
                address.setPincode(rs.getInt("pincode"));
                
                manager.setId(rs.getLong("manager_id"));
                manager.setName(rs.getString("manager_name"));
                manager.setPhone(rs.getLong("manager_phone"));
                manager.setEmail(rs.getString("manager_email"));
                manager.setPassword(rs.getString("manager_password"));
                manager.setBranchId(branch.getId());
                manager.setBranchName(branch.getName());
                
                branch.setAddress(address);
                branch.setManager(manager);
                branches.add(branch);
			}
		} catch(SQLException e) {
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
	
	
	public BranchBean get(int id) throws SQLException {
		BranchBean branch = null;
		EmployeeBean manager = null;
		AddressBean address = null;
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		boolean exceptionOccured = false;
		String msg = "";
		
		try {
			conn = Factory.getDataSource().getConnection();
			stmt = conn.prepareStatement("SELECT b.id, b.name, door_no, street, city, state, pincode, m.id as manager_id, m.name as manager_name, m.email as manager_email, m.phone as manager_phone, m.password as manager_password FROM branch b JOIN manager m ON b.id = m.branch_id WHERE branch_id = ?");
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				branch = new BranchBean();
				address = new AddressBean();
				manager = new EmployeeBean();
				
				branch.setId(rs.getInt("id"));
				branch.setName(rs.getString("name"));
				
                address.setDoorNo(rs.getString("door_no"));
                address.setStreet(rs.getString("street"));
                address.setCity(rs.getString("city"));
                address.setState(rs.getString("state"));
                address.setPincode(rs.getInt("pincode"));
                
                manager.setId(rs.getLong("manager_id"));
                manager.setName(rs.getString("manager_name"));
                manager.setPhone(rs.getLong("manager_phone"));
                manager.setEmail(rs.getString("manager_email"));
                manager.setPassword(rs.getString("manager_password"));
                manager.setBranchId(branch.getId());
                manager.setBranchName(branch.getName());
                
                branch.setAddress(address);
                branch.setManager(manager);
			}
		} catch(SQLException e) {
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
	public BranchBean create(Connection conn, String name, AddressBean address) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		BranchBean branch = null;
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

            branch = new BranchBean();
            branch.setId(branchId);
            branch.setName(name);
            branch.setAddress(address);
        } catch(SQLException e) {
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