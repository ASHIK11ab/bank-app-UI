package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AccountDAO {
	public boolean delete(Connection conn, long accountNo) throws SQLException {
		PreparedStatement stmt = null;
		
		String msg = "";
		boolean exceptionOccured = false;
		int rowsAffected = -1;
		
		try {
			stmt = conn.prepareStatement("DELETE FROM account WHERE account_no = ?");
			stmt.setLong(1, accountNo);
			rowsAffected = stmt.executeUpdate();
			System.out.println(rowsAffected);
		} catch(SQLException e) {
            exceptionOccured = true;
            msg = "internal error";
        } finally {
            try {
                if(stmt != null)
                    stmt.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
        }
		
		if(exceptionOccured)
			throw new SQLException(msg);
		else
			return rowsAffected == 1;
	}
	
	
	public void updateBalance(Connection conn, long accountNo, int updateType, float amount) throws SQLException {
		PreparedStatement stmt = null;
		
		String msg = "";
		boolean exceptionOccured = false;
		
		try {
			// Type 0 - Deduct amount, Type 1 - Add amount
			if(updateType == 0)
				stmt = conn.prepareStatement("UPDATE account SET balance = (balance - ?) WHERE account_no = ?");
			else
				stmt = conn.prepareStatement("UPDATE account SET balance = (balance + ?) WHERE account_no = ?");
			
			stmt.setFloat(1, amount);
			stmt.setLong(2, accountNo);
			stmt.executeUpdate();
		} catch(SQLException e) {
            exceptionOccured = true;
            msg = "internal error";
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