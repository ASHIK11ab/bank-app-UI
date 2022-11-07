package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import cache.AppCache;
import model.user.User;
import util.Factory;

public class AdminDAO {
	public User get(long id) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		User admin = null;
		long phone;
		String password;
        String name;
        String email;
        
        boolean exceptionOccured = false;
        String msg = "";
        
        try {
        	admin = AppCache.getAdmin();
        	
        	if(admin != null && admin.getId() != id) {
        		return null;
        	}
        	
        	// Try loading from DB
        	if(admin == null) {
	            conn = Factory.getDataSource().getConnection();
	            stmt = conn.prepareStatement("SELECT * FROM admin WHERE id = ?");
	            stmt.setLong(1, id);
	            rs = stmt.executeQuery();
	
	            // Load admin to cache if exists with the given id.
	            if(rs.next()) {
	                name = rs.getString("name");
	                password = rs.getString("password");
	                email = rs.getString("email");
	                phone = rs.getLong("phone");
	                
	                admin = new User(id, name, password, email, phone, null);
	                // update in cache.
	                AppCache.cacheAdmin(admin);
	            }
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
        	return admin;
	}
}