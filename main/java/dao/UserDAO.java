package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import constant.Role;
import model.user.Customer;
import model.user.Employee;
import model.user.User;
import util.Factory;
import util.Util;


public class UserDAO {
	public void updatePassword(long id, String password, Role role, byte type, int branchId) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		boolean exceptionOccured = false;
		String msg = "";
		
		try {
			conn = Factory.getDataSource().getConnection();
			
			switch(role) {
				case ADMIN   : stmt = conn.prepareStatement("UPDATE admin SET password = ? WHERE id = ?"); break;
				case EMPLOYEE: stmt = conn.prepareStatement("UPDATE employee SET password = ? WHERE id = ?"); break;
				case MANAGER : stmt = conn.prepareStatement("UPDATE manager SET password = ? WHERE id = ?"); break;
				
				case CUSTOMER: 
								// Type 0 - Updates login password, type 1 - updates transaction password.
								if(type == 0)
									stmt = conn.prepareStatement("UPDATE customer SET password = ? WHERE id = ?");
								else
									stmt = conn.prepareStatement("UPDATE customer SET transaction_password = ? WHERE id = ?");
								break;
			}

            stmt.setString(1, password);
            stmt.setLong(2, id);
            stmt.executeUpdate();
		} catch(SQLException e) {
			System.out.println(e.getMessage());
            exceptionOccured = true;
            msg = "internal error";
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
	
	
	public void update(Role role, Long id, String name, String email, long phone) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;

		boolean exceptionOccured = false;
		String msg = "";

        try {
            conn = Factory.getDataSource().getConnection();
            
			switch(role) {
				case ADMIN   : stmt = conn.prepareStatement("UPDATE admin SET name = ?, email = ?, phone = ?  WHERE id = ?"); break;
				case EMPLOYEE: stmt = conn.prepareStatement("UPDATE employee SET name = ?, email = ?, phone = ?  WHERE id = ?"); break;
				case MANAGER : stmt = conn.prepareStatement("UPDATE manager SET name = ?, email = ?, phone = ?  WHERE id = ?"); break;
				default: break;
            }
            
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setLong(3, phone);
            stmt.setLong(4, id);
            
            stmt.executeUpdate();
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
	}
}