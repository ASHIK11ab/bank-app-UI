package dao;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import constant.BeneficiaryType;
import model.Beneficiary;
import util.Factory;

public class BeneficiaryDAO {
	public Beneficiary create(BeneficiaryType type, long customerId, long accountNo, String name, String nickName,
									int bankId, String IFSC) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		Beneficiary beneficiary = null;
		boolean exceptionOccured = false;
		String msg = "";
		long beneficiaryId = -1;
		
		try {
			conn = Factory.getDataSource().getConnection();
			
			if(type == BeneficiaryType.OWN_BANK)
                stmt = conn.prepareStatement("INSERT INTO own_bank_beneficiary (customer_id, account_no, name, nick_name) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			else
                stmt = conn.prepareStatement("INSERT INTO other_bank_beneficiary (customer_id, account_no, name, nick_name, bank_id, ifsc) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

				stmt.setLong(1, customerId);
                stmt.setLong(2, accountNo);
                stmt.setString(3, name);
                stmt.setString(4, nickName);
                
                if(type == BeneficiaryType.OTHER_BANK) {
                    stmt.setInt(5, bankId);
                    stmt.setString(6, IFSC);
                }

                stmt.executeUpdate();

                rs = stmt.getGeneratedKeys();
                if(rs.next()) {
                    beneficiaryId = rs.getLong(1);
                }
                
                if(type == BeneficiaryType.OWN_BANK)
                	beneficiary = new Beneficiary(beneficiaryId, accountNo, name, nickName);
                else
                	beneficiary = new Beneficiary(beneficiaryId, accountNo, name, nickName, bankId, IFSC);
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
			return beneficiary;
	}
	
	
	public void delete(BeneficiaryType type, long id) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		boolean exceptionOccured = false;
		String msg = "";
		
		try {
			conn = Factory.getDataSource().getConnection();
			
            if(type == BeneficiaryType.OWN_BANK)
                stmt = conn.prepareStatement("DELETE FROM own_bank_beneficiary WHERE id = ?");
            else
                stmt = conn.prepareStatement("DELETE FROM other_bank_beneficiary WHERE id = ?");

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