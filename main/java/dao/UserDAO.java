package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import constant.Role;
import util.Factory;


public class UserDAO {
	public void updatePassword(long id, String password, Role role, byte type) throws SQLException {
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
            stmt.setLong(1, id);
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
}