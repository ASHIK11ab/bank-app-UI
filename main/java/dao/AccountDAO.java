package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
	
	
	public float updateBalance(Connection conn, long accountNo, int updateType, float amount) throws SQLException {
		PreparedStatement stmt1 = null, stmt2 = null;
		ResultSet rs1 = null;
		
		String msg = "";
		boolean exceptionOccured = false;
		float beforeBalance = 0;
		
		try {
			stmt1 = conn.prepareStatement("SELECT balance FROM account WHERE account_no = ?");
			
			// Type 0 - Deduct amount, Type 1 - Add amount
			if(updateType == 0)
				stmt2 = conn.prepareStatement("UPDATE account SET balance = (balance - ?) WHERE account_no = ?");
			else
				stmt2 = conn.prepareStatement("UPDATE account SET balance = (balance + ?) WHERE account_no = ?");
			
			stmt1.setLong(1, accountNo);
			rs1 = stmt1.executeQuery();
			if(rs1.next())
				beforeBalance = rs1.getFloat("balance");
			
			stmt2.setFloat(1, amount);
			stmt2.setLong(2, accountNo);
			stmt2.executeUpdate();
		} catch(SQLException e) {
			System.out.println(e.getMessage());
            exceptionOccured = true;
            msg = "internal error";
        } finally {
            try {
                if(rs1 != null)
                    rs1.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
            
            try {
                if(stmt1 != null)
                    stmt1.close();
                if(stmt2 != null)
                    stmt2.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
        }
		
		if(exceptionOccured)
			throw new SQLException(msg);
		else
			return beforeBalance;
	}
}