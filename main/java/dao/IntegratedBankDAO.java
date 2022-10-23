package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import model.IntegratedBankBean;
import util.Factory;

public class IntegratedBankDAO {
	public void create(String name, String email, String apiURL, long phone) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		IntegratedBankBean integratedBank = null;
		String msg = "";
		int bankId = -1;
		boolean exceptionOccured = false;
		
		try {
            conn = Factory.getDataSource().getConnection();
            stmt = conn.prepareStatement("INSERT INTO banks (name, contact_mail, contact_phone, api_url) values (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setLong(3, phone);
            stmt.setString(4, apiURL);
            stmt.executeUpdate();

            rs = stmt.getGeneratedKeys();

            if(rs.next())
                bankId = rs.getInt(1);
            
            // update in cache.
            integratedBank = new IntegratedBankBean();
            integratedBank.setId(bankId);
            integratedBank.setName(name);
            integratedBank.setEmail(email);
            integratedBank.setPhone(phone);
            integratedBank.setApiURL(apiURL);
        } catch(SQLException e) {
            exceptionOccured = true;
            msg = "internal error";
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
	}
	
	
	public boolean delete(int id) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		boolean exceptionOccured = false;
		String msg = "";
		int rowsAffected = 0;
		
		try {
			conn = Factory.getDataSource().getConnection();
			stmt = conn.prepareStatement("DELETE FROM banks WHERE id = ?");
			stmt.setInt(1, id);
			rowsAffected = stmt.executeUpdate();
		} catch(SQLException e) {
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
		
		return rowsAffected == 1;
	}
	
	
	public IntegratedBankBean get(int id) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		IntegratedBankBean integratedBank = null;
		String msg = "";
		boolean exceptionOccured = false;
		
		try {
            conn = Factory.getDataSource().getConnection();
            stmt = conn.prepareStatement("SELECT * FROM banks WHERE id = ?");
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if(rs.next()) {
	            integratedBank = new IntegratedBankBean();
	            integratedBank.setId(rs.getInt("id"));
	            integratedBank.setName(rs.getString("name"));
	            integratedBank.setEmail(rs.getString("contact_mail"));
	            integratedBank.setPhone(rs.getLong("contact_phone"));
	            integratedBank.setApiURL(rs.getString("api_url"));
            }
        } catch(SQLException e) {
            exceptionOccured = true;
            msg = "internal error";
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
			return integratedBank;
	}
	
	
	public LinkedList<IntegratedBankBean> getAll() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		LinkedList<IntegratedBankBean> integratedBanks = new LinkedList<IntegratedBankBean>();
		IntegratedBankBean integratedBank = null;
		String msg = "";
		boolean exceptionOccured = false;
		
		try {
            conn = Factory.getDataSource().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM banks");
            
            while(rs.next()) {
	            integratedBank = new IntegratedBankBean();
	            integratedBank.setId(rs.getInt("id"));
	            integratedBank.setName(rs.getString("name"));
	            integratedBank.setEmail(rs.getString("contact_mail"));
	            integratedBank.setPhone(rs.getLong("contact_phone"));
	            integratedBank.setApiURL(rs.getString("api_url"));
	            
	            integratedBanks.add(integratedBank);
            }
        } catch(SQLException e) {
            exceptionOccured = true;
            msg = "internal error";
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
			return integratedBanks;
	}
}