package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import constant.RegularAccountType;
import model.account.*;
import util.Factory;
import util.Util;

public class RegularAccountDAO {
	public RegularAccountBean create(Connection conn, long customerId,
										String customerName, int branchId, 
										RegularAccountType accountType, int cardType,
										NomineeBean nominee) throws SQLException {
		PreparedStatement stmt1 = null, stmt2 = null, stmt3 = null;
		ResultSet rs = null;
		
		RegularAccountBean account = null;
		LocalDate validFromDate, expiryDate;
		boolean exceptionOccured = false;
		String msg = "";
		long generatedAccountNo = -1;
		float balance = 0;
		int pin, cvv;
		
		try {
			if(nominee != null)
				stmt1 = conn.prepareStatement("INSERT INTO account (customer_id, branch_id, balance, opening_date, nominee_id) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);	
			else
				stmt1 = conn.prepareStatement("INSERT INTO account (customer_id, branch_id, balance, opening_date) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);	

			stmt2 = conn.prepareStatement("INSERT INTO regular_account (account_no, type_id, active) VALUES (?, ?, ?)");
	        stmt3 = conn.prepareStatement("INSERT INTO debit_card (account_no, valid_from, expiry_date, type_id, pin, cvv) VALUES (?, ?, ?, ?, ?, ?)");
	
	        balance = (accountType == RegularAccountType.SAVINGS) ? SavingsAccountBean.getMinimumBalance() : CurrentAccountBean.getMinimumBalance();
	        
	        stmt1.setLong(1, customerId);
	        stmt1.setInt(2, branchId);
	        stmt1.setFloat(3, balance);
	        stmt1.setDate(4, Date.valueOf(LocalDate.now()));
	        
	        if(nominee != null)
	        	stmt1.setLong(5, nominee.getId());
	        
	        stmt1.executeUpdate();
	        rs = stmt1.getGeneratedKeys();
	        if(rs.next())
	            generatedAccountNo = rs.getLong(1);
	
	        stmt2.setLong(1, generatedAccountNo);
	        stmt2.setInt(2, accountType.id);
	        stmt2.setBoolean(3, true);
	        stmt2.executeUpdate();
	
	        validFromDate = LocalDate.now().plusDays(10);
	        expiryDate = validFromDate.plusYears(3);
	        pin = Util.genPin(4);
	        cvv = Util.genPin(3);
	        
	        stmt3.setLong(1, generatedAccountNo);
	        stmt3.setDate(2, Date.valueOf(validFromDate));
	        stmt3.setDate(3, Date.valueOf(expiryDate));
	        stmt3.setInt(4, cardType);
	        stmt3.setInt(5, pin);
	        stmt3.setInt(6, cvv);
	        stmt3.executeUpdate();
	        
	        
	        switch(accountType) {
	        	case SAVINGS: account = new SavingsAccountBean(); break;
	        	case CURRENT: account = new CurrentAccountBean(); break;
	        }
	        
	        account.setAccountNo(generatedAccountNo);
	        account.setCustomerId(customerId);
	        account.setCustomerName(customerName);
	        account.setBranchId(branchId);
	        account.setBalance(balance);
	        account.setOpeningDate(LocalDate.now());
	        account.setNominee(nominee);
	        account.setIsActive(true);
	        account.setTypeId(accountType.id);
	        
		} catch(SQLException e) {
			exceptionOccured = true;
			msg = "internal error";
		} finally {
			try {
			    if(rs != null)
			        rs.close();
			} catch(SQLException e) { System.out.println(e.getMessage()); }
			
			try {
			    if(stmt1 != null)
			        stmt1.close();
			    if(stmt2 != null)
			        stmt2.close();
			    if(stmt3 != null)
			        stmt3.close();
			} catch(SQLException e) { System.out.println(e.getMessage()); }
		}
		
		if(exceptionOccured)
			throw new SQLException(msg);
		else
			return account;
	}
	
	
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
	
	
	public RegularAccountBean get(long accountNo) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt1 = null, stmt2 = null;
		ResultSet rs1 = null, rs2 = null;
		
		RegularAccountBean account = null;
		boolean exceptionOccured = false;
		String msg = "";
		int type_id = -1;
		
		try {
			conn = Factory.getDataSource().getConnection();
			stmt1 = conn.prepareStatement("SELECT * FROM regular_account ra LEFT JOIN account a ON ra.account_no = a.account_no WHERE ra.account_no = ?");
			stmt2 = conn.prepareStatement("SELECT name FROM customer WHERE id = ?");
			
			stmt1.setLong(1, accountNo);
			rs1 = stmt1.executeQuery();
			
			if(rs1.next()) {
				type_id = rs1.getInt("type_id");
				
				switch(RegularAccountType.getType(type_id)) {
					case SAVINGS : account = new SavingsAccountBean(); break;
					case CURRENT : account = new CurrentAccountBean(); break;
					default: return null;
				}
				
		        account.setAccountNo(rs1.getLong(1));
		        account.setCustomerId(rs1.getLong("customer_id"));
		        account.setBranchId(rs1.getInt("branch_id"));
		        account.setBalance(rs1.getFloat("balance"));
		        account.setOpeningDate(rs1.getDate("opening_date").toLocalDate());
		        account.setIsActive(rs1.getBoolean("active"));
		        account.setTypeId(type_id);	
		        
				stmt2.setLong(1, account.getCustomerId());
				rs2 = stmt2.executeQuery();
				if(rs2.next())
					account.setCustomerName(rs2.getString("name"));
			}	        
		} catch(SQLException e) {
			exceptionOccured = true;
			msg = "internal error";
		} finally {
			try {
			    if(rs1 != null)
			        rs1.close();
			    if(rs2 != null)
			        rs2.close();
			} catch(SQLException e) { System.out.println(e.getMessage()); }
			
			try {
			    if(stmt1 != null)
			        stmt1.close();
			    if(stmt2 != null)
			        stmt2.close();
			} catch(SQLException e) { System.out.println(e.getMessage()); }
			
			try {
			    if(conn != null)
			        conn.close();
			} catch(SQLException e) { System.out.println(e.getMessage()); }
		}
		
		if(exceptionOccured)
			throw new SQLException(msg);
		else
			return account;
	}
}