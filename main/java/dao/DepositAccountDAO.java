package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import constant.DepositAccountType;
import model.account.DepositAccountBean;
import model.account.NomineeBean;
import util.Factory;


public class DepositAccountDAO {
	public DepositAccountBean create(Connection conn, long customerId, String customerName,
										int branchId, int depositType, NomineeBean nominee,
										int amount, int tenureMonths, long payoutAccountNo,
										long debitFromAccountNo, LocalDate recurringDate) throws SQLException {
		PreparedStatement stmt1 = null, stmt2 = null;
		ResultSet rs1 = null;
		
		DepositAccountBean account = null;
		
		boolean exceptionOccured = false;
		String msg = "";
		float intrestRate;
		long generatedAccountNo = -1;
		
		try {
			if(nominee != null)
				stmt1 = conn.prepareStatement("INSERT INTO account (customer_id, branch_id, balance, opening_date, nominee_id) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);	
			else
				stmt1 = conn.prepareStatement("INSERT INTO account (customer_id, branch_id, balance, opening_date) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		
			if(depositType == DepositAccountType.FD.id)
				stmt2 = conn.prepareStatement("INSERT INTO deposit_account (account_no, type_id, payout_account_no, rate_of_intrest, tenure_months, debit_from_account_no) VALUES (?, ?, ?, ?, ?, ?)");
			else
				stmt2 = conn.prepareStatement("INSERT INTO deposit_account (account_no, type_id, payout_account_no, rate_of_intrest, tenure_months, debit_from_account_no, amount_per_month, recurring_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

	        stmt1.setLong(1, customerId);
	        stmt1.setInt(2, branchId);
	        stmt1.setFloat(3, amount);
	        stmt1.setDate(4, Date.valueOf(LocalDate.now()));
	        
	        if(nominee != null)
	        	stmt1.setLong(5, nominee.getId());
	        
	        stmt1.executeUpdate();
	        rs1 = stmt1.getGeneratedKeys();
	        if(rs1.next())
	            generatedAccountNo = rs1.getLong(1);
	        
	        intrestRate = DepositAccountBean.getTypeIntrestRate(DepositAccountType.getType(depositType));
	        
            // create deposit account which maps to account
            stmt2.setLong(1, generatedAccountNo);
            stmt2.setInt(2, depositType);
            stmt2.setLong(3, payoutAccountNo);
            stmt2.setFloat(4, intrestRate);
            stmt2.setInt(5, tenureMonths);
            stmt2.setLong(6, debitFromAccountNo);
            
            if(depositType == DepositAccountType.RD.id) {
            	stmt2.setFloat(7, amount);
            	stmt2.setDate(8, Date.valueOf(recurringDate));            	
            }
            
            stmt2.executeUpdate();
            
            account = new DepositAccountBean();
            account.setAccountNo(generatedAccountNo);
            account.setCustomerId(customerId);
            account.setCustomerName(customerName);
            account.setBranchId(branchId);
            account.setBalance(amount);
            account.setOpeningDate(LocalDate.now());
            account.setNominee(nominee);
            account.setTypeId(depositType);
            account.setIntrestRate(intrestRate);
            account.setTenureMonths(tenureMonths);
            account.setPayoutAccountNo(payoutAccountNo);
            account.setDebitFromAccountNo(debitFromAccountNo);
            
            if(depositType == DepositAccountType.RD.id) {
            	account.setAmountPerMonth(amount);
            	account.setRecurringDate(recurringDate);
            }
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
			return account;
	}
	
	
	public DepositAccountBean get(long accountNo) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt1 = null, stmt2 = null;
		ResultSet rs1 = null, rs2 = null;
		
		boolean exceptionOccured = false;
		String msg = "";
		
		NomineeBean nominee = null;
		DepositAccountBean account = null;
		
		try {
			conn = Factory.getDataSource().getConnection();
            stmt1 = conn.prepareStatement("SELECT * FROM deposit_account da LEFT JOIN account a ON a.account_no = da.account_no WHERE a.account_no = ?");
            stmt2 = conn.prepareStatement("SELECT name FROM customer WHERE id = ?");
            
            stmt1.setLong(1, accountNo);
            rs1 = stmt1.executeQuery();
            
            if(rs1.next()) {            	
                account = new DepositAccountBean();
                account.setAccountNo(rs1.getLong(1));
                account.setBranchId(rs1.getInt("branch_id"));
                account.setBalance(rs1.getFloat("balance"));
                account.setCustomerId(rs1.getLong("customer_id"));
                account.setOpeningDate(rs1.getDate("opening_date").toLocalDate());
                account.setTypeId(rs1.getInt("type_id"));
                account.setIntrestRate(rs1.getFloat("rate_of_intrest"));
                account.setTenureMonths(rs1.getInt("tenure_months"));
                account.setPayoutAccountNo(rs1.getLong("payout_account_no"));
                account.setDebitFromAccountNo(rs1.getLong("debit_from_account_no"));
                
                if(account.getTypeId() == DepositAccountType.RD.id) {
                	account.setAmountPerMonth(rs1.getInt("amount_per_month"));
                	account.setRecurringDate(rs1.getDate("recurring_date").toLocalDate());
                }
                
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