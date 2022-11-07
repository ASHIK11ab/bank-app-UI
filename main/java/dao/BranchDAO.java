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
            // add to cache.
            AppCache.getBank().addBranch(branch);;
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
	
	
	public Branch update(int branchId, String name, Address address) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		Branch branch = null;
		String msg = null;
		boolean exceptionOccured = false;
				
		try {
			conn = Factory.getDataSource().getConnection();
            stmt = conn.prepareStatement("UPDATE branch SET name = ?, door_no = ?, street = ?, city = ?, state = ?, pincode = ? WHERE id = ?");
            stmt.setString(1, name);
            stmt.setString(2, address.getDoorNo());
            stmt.setString(3, address.getStreet());
            stmt.setString(4, address.getCity());
            stmt.setString(5, address.getState());
            stmt.setLong(6, address.getPincode());
            stmt.setInt(7, branchId);

            stmt.executeUpdate();

            branch = this.get(branchId);
            branch.setName(name);
            branch.setAddress(address);
        } catch(SQLException e) {
        	System.out.println(e.getMessage());
            exceptionOccured = true;
            msg = "Error updating branch details";
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
			return branch;
	}
}