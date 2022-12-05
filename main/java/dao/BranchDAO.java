package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import cache.AppCache;
import model.Address;
import model.Branch;
import util.Factory;


public class BranchDAO {
	public Collection<Branch> getAll() {
		return AppCache.getBank().getBranches();
	}
	
	
	public Branch get(int id) {
		return AppCache.getBank().getBranch(id);
	}
	
	
	// Adds a new branch to DB.
	public Branch createUpdate(Connection conn, String name, Address address, byte type, int id) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		Branch branch = null;
		String msg = null;
		boolean exceptionOccured = false;
		int branchId = 0;
				
		try {
			if(type == 0)
				stmt = conn.prepareStatement("INSERT INTO branch (name, door_no, street, city, state, pincode) values (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			else
	            stmt = conn.prepareStatement("UPDATE branch SET name = ?, door_no = ?, street = ?, city = ?, state = ?, pincode = ? WHERE id = ?");

			stmt.setString(1, name);
            stmt.setString(2, address.getDoorNo());
            stmt.setString(3, address.getStreet());
            stmt.setString(4, address.getCity());
            stmt.setString(5, address.getState());
            stmt.setLong(6, address.getPincode());
            
            if(type == 1)
            	stmt.setInt(7, id);
            
            stmt.executeUpdate();
            
            // Branch creation.
            if(type == 0) {
	            rs = stmt.getGeneratedKeys();
	
	            if(rs.next()) {
	                branchId = rs.getInt(1);
	            }
	            branch = new Branch(branchId, name, address);
	            AppCache.getBank().addBranch(branch);
            } else {
            	branch = AppCache.getBranch(id);
            	branch.setName(name);
            	branch.setAddress(address);
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
			
			// remove from cache.
			AppCache.getBank().removeBranch(id);
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