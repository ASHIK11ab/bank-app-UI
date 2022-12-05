package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import cache.AppCache;
import model.IntegratedBank;
import util.Factory;

public class IntegratedBankDAO {
	public IntegratedBank createUpdate(String name, String email, String apiURL, long phone, byte type, int id) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		IntegratedBank integratedBank = null;
		String msg = "";
		int bankId = -1;
		boolean exceptionOccured = false;
		
		try {
            conn = Factory.getDataSource().getConnection();
            
            // Creation
            if(type == 0)
            	stmt = conn.prepareStatement("INSERT INTO banks (name, contact_mail, contact_phone, api_url) values (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            else
            	stmt = conn.prepareStatement("UPDATE banks SET name = ?, contact_mail = ?, contact_phone = ?, api_url = ? WHERE id = ?");
            	
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setLong(3, phone);
            stmt.setString(4, apiURL);
            
            // update
            if(type == 1)
            	stmt.setInt(5, id);
            
            stmt.executeUpdate();

            if(type == 0) {
                rs = stmt.getGeneratedKeys();

                if(rs.next())
                    bankId = rs.getInt(1);	
            } else {
            	bankId = id;
            }
            
            // update in cache.
            integratedBank = new IntegratedBank(bankId, name, email, phone, apiURL);
            AppCache.getBank().addIntegratedBank(integratedBank);
        } catch(SQLException e) {
        	System.out.println(e.getMessage());
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
	
	
	public void delete(int id) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		boolean exceptionOccured = false;
		String msg = "";
		
		try {
			conn = Factory.getDataSource().getConnection();
			stmt = conn.prepareStatement("DELETE FROM banks WHERE id = ?");
			stmt.setInt(1, id);
			stmt.executeUpdate();
			// update in cache.
			AppCache.getBank().removeIntegratedBank(id);
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
	
	
	public IntegratedBank get(int id) {
		return AppCache.getBank().getIntegratedBank(id);
	}
	
	
	public Collection<IntegratedBank> getAll() {
		return AppCache.getBank().getIntegratedBanks();
	}
}