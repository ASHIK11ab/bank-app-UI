package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import model.UserBean;

public class AdminDAO {
	private DataSource dataSource;
	
	public AdminDAO() {
		try {			
			Context context = new InitialContext();
			dataSource = (DataSource) context.lookup("java:comp/env/jdbc/newbankdb");
		} catch(NamingException e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	public UserBean get(long id) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		UserBean admin = null;
		long phone;
		String password;
        String name;
        String email;
        
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM admin WHERE id = ?");
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            // Load admin to cache if exists with the given id.
            if(rs.next()) {
                name = rs.getString("name");
                password = rs.getString("password");
                email = rs.getString("email");
                phone = rs.getLong("phone");
                
                admin = new UserBean();
                admin.setId(id);
                admin.setName(name);
                admin.setPhone(phone);
                admin.setEmail(email);
                admin.setPassword(password);
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
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
        
        return admin;
	}
}