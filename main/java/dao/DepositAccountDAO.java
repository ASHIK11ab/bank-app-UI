package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import constant.DepositAccountType;
import model.account.DepositAccount;
import model.Nominee;
import util.Factory;


public class DepositAccountDAO {
	public DepositAccount create(Connection conn, long customerId, String customerName,
										int branchId, int depositType, Nominee nominee,
										int amount, int tenureMonths, long payoutAccountNo,
										long debitFromAccountNo, LocalDate recurringDate) throws SQLException {
		PreparedStatement stmt1 = null, stmt2 = null;
		ResultSet rs1 = null;
		
		DepositAccountType type = DepositAccountType.getType(depositType);
		DepositAccount account = null;
		
		LocalDate today = LocalDate.now();
		boolean exceptionOccured = false;
		String msg = "";
		float intrestRate;
		long generatedAccountNo = -1;
		
		try {
			stmt1 = conn.prepareStatement(AccountDAO.ACCOUNT_CREATION_QUERY, Statement.RETURN_GENERATED_KEYS);
			stmt2 = conn.prepareStatement("INSERT INTO deposit_account (account_no, type_id, payout_account_no, rate_of_intrest, tenure_months, debit_from_account_no, deposit_amount, recurring_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

	        stmt1.setLong(1, customerId);
	        stmt1.setInt(2, branchId);
	        stmt1.setFloat(3, amount);
	        stmt1.setDate(4, Date.valueOf(today));
	        
	        if(nominee != null)
	        	stmt1.setLong(5, nominee.getId());
	        else
	        	stmt1.setObject(5, null);
	        
	        stmt1.executeUpdate();
	        rs1 = stmt1.getGeneratedKeys();
	        if(rs1.next())
	            generatedAccountNo = rs1.getLong(1);
	        
	        intrestRate = DepositAccount.getTypeIntrestRate(type);
	        
            // create deposit account which maps to account
            stmt2.setLong(1, generatedAccountNo);
            stmt2.setInt(2, depositType);
            stmt2.setLong(3, payoutAccountNo);
            stmt2.setFloat(4, intrestRate);
            stmt2.setInt(5, tenureMonths);
            stmt2.setLong(6, debitFromAccountNo);
            stmt2.setFloat(7, amount);
            
            if(type == DepositAccountType.RD)
            	stmt2.setDate(8, Date.valueOf(recurringDate));            	
            else
            	stmt2.setObject(8, null);
            
            stmt2.executeUpdate();
            
            switch(type) {
	            case RD: account = new DepositAccount(generatedAccountNo, customerId, customerName, nominee, 
	            															branchId, amount, payoutAccountNo, debitFromAccountNo,
	            															tenureMonths, intrestRate, today, null, amount, recurringDate);
	            								break;
	            case FD: account = new DepositAccount(generatedAccountNo, customerId, customerName, nominee, 
																			branchId, amount, payoutAccountNo, debitFromAccountNo,
																			tenureMonths, intrestRate, today, null, amount);
	            								break;
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
	
	
	public DepositAccount get(long accountNo) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt1 = null, stmt2 = null;
		ResultSet rs1 = null, rs2 = null;
		
		boolean exceptionOccured = false;
		String msg = "", customerName = "";
		
		Nominee nominee = null;
		DepositAccount account = null;
		DepositAccountType type;
		
		LocalDate openingDate, recurringDate = null, closingDate;
		long customerId, payoutAccountNo, debitFromAccountNo;
		float balance, rateOfIntrest;
		int branchId, typeId, tenureMonths, monthlyInstallment = 0, amountDeposited = 0;
		
		try {
			conn = Factory.getDataSource().getConnection();
            stmt1 = conn.prepareStatement("SELECT * FROM deposit_account da LEFT JOIN account a ON a.account_no = da.account_no WHERE a.account_no = ?");
            stmt2 = conn.prepareStatement("SELECT name FROM customer WHERE id = ?");
            
            stmt1.setLong(1, accountNo);
            rs1 = stmt1.executeQuery();
            
            if(rs1.next()) {
            	customerId = rs1.getLong("customer_id");
                typeId = rs1.getInt("type_id");
                tenureMonths = rs1.getInt("tenure_months");
                openingDate = rs1.getDate("opening_date").toLocalDate();
                balance = rs1.getFloat("balance");
                rateOfIntrest = rs1.getFloat("rate_of_intrest");
                branchId = rs1.getInt("branch_id");
                payoutAccountNo = rs1.getLong("payout_account_no");
                debitFromAccountNo = rs1.getLong("debit_from_account_no");
                
                type = DepositAccountType.getType(typeId);
                
                if(type == DepositAccountType.RD) {
                    monthlyInstallment = rs1.getInt("deposit_amount");
                    recurringDate = rs1.getDate("recurring_date").toLocalDate();	
                } else {
                	amountDeposited = rs1.getInt("deposit_amount");
                }
                
		        if(rs1.getDate("closing_date") != null)
		        	closingDate = rs1.getDate("closing_date").toLocalDate();
		        else
		        	closingDate = null;
		        
                stmt2.setLong(1, customerId);
                rs2 = stmt2.executeQuery();
                
                if(rs2.next())
                	customerName = rs2.getString("name");
                
                
                switch(type) {
		            case RD: account = new DepositAccount(accountNo, customerId, customerName, nominee, 
		            															branchId, balance, payoutAccountNo, debitFromAccountNo,
		            															tenureMonths, rateOfIntrest, openingDate, closingDate, monthlyInstallment, recurringDate);
		            								break;
		            case FD: account = new DepositAccount(accountNo, customerId, customerName, nominee, 
																				branchId, balance, payoutAccountNo, debitFromAccountNo,
																				tenureMonths, rateOfIntrest, openingDate, closingDate, amountDeposited);
		            								break;
	            } 
            }
		} catch(SQLException e) {
			System.out.println(e.getMessage());
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