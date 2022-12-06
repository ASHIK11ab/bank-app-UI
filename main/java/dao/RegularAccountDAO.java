package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedList;

import cache.AppCache;
import constant.AccountCategory;
import constant.RegularAccountType;
import model.Branch;
import model.Nominee;
import model.Transaction;
import model.account.*;
import model.card.DebitCard;
import model.user.Customer;
import util.Factory;
import util.Util;

public class RegularAccountDAO {
	public RegularAccount create(Connection conn, long customerId,
										String customerName, int branchId, 
										RegularAccountType accountType, int cardType,
										Nominee nominee) throws SQLException {
		PreparedStatement stmt1 = null, stmt2 = null;
		ResultSet rs = null;
		
		DebitCardDAO cardDAO = Factory.getDebitCardDAO();
		
		Customer customer = null;
		DebitCard card = null;
		
		LocalDate today = LocalDate.now();
		RegularAccount account = null;
		boolean exceptionOccured = false;
		String msg = "";
		long generatedAccountNo = -1;
		float balance = 0;
		
		try {
			stmt1 = conn.prepareStatement(AccountDAO.ACCOUNT_CREATION_QUERY, Statement.RETURN_GENERATED_KEYS);
			stmt2 = conn.prepareStatement("INSERT INTO regular_account (account_no, type_id, active) VALUES (?, ?, ?)");
	
	        balance = (accountType == RegularAccountType.SAVINGS) ? SavingsAccount.getMinimumBalance() : CurrentAccount.getMinimumBalance();
	        
	        stmt1.setLong(1, customerId);
	        stmt1.setInt(2, branchId);
	        stmt1.setFloat(3, balance);
	        stmt1.setDate(4, Date.valueOf(today));
	        
	        if(nominee != null)
	        	stmt1.setLong(5, nominee.getId());
	        else
	        	stmt1.setObject(5, null);
	        
	        stmt1.executeUpdate();
	        rs = stmt1.getGeneratedKeys();
	        if(rs.next())
	            generatedAccountNo = rs.getLong(1);
	
	        stmt2.setLong(1, generatedAccountNo);
	        stmt2.setInt(2, accountType.id);
	        stmt2.setBoolean(3, true);
	        stmt2.executeUpdate();
	        
	        card = cardDAO.create(conn, generatedAccountNo, (byte) cardType);
	        
	        switch(accountType) {
	        	case SAVINGS: account = new SavingsAccount(generatedAccountNo, customerId, customerName,
	        												nominee, branchId, balance, today, null, true); break;
	        	case CURRENT: account = new CurrentAccount(generatedAccountNo, customerId, customerName,
															nominee, branchId, balance, today, null, true); break;
	        }
	        
	        account.addDebitCard(card);
	        
			// update in cache.
			customer = Factory.getCustomerDAO().get(customerId);
			customer.addAccountBranchMapping(AccountCategory.REGULAR, accountType.id, generatedAccountNo, branchId);
	        
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
	
	
	synchronized public RegularAccount get(long accountNo, int branchId) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt1 = null, stmt2 = null;
		ResultSet rs1 = null, rs2 = null;
		
		DebitCardDAO cardDAO = Factory.getDebitCardDAO();
		TransactionDAO transactionDAO = Factory.getTransactionDAO();
		LinkedList<Transaction> recentTransactions = null;
		
		Branch branch = AppCache.getBranch(branchId);
		RegularAccount account = null;
		LocalDate openingDate, closingDate;
		boolean exceptionOccured = false, isActive;
		String msg = "", customerName = "";
		float balance;
		long customerId;
		int type_id = -1;
		
		if(branch == null) {
			return null;
		}
				
		try {
			account = (RegularAccount) branch.getAccount(AccountCategory.REGULAR, accountNo);
			// If not found in branch load from db.
			if(account == null) {
				conn = Factory.getDataSource().getConnection();
				stmt1 = conn.prepareStatement("SELECT * FROM regular_account ra LEFT JOIN account a ON ra.account_no = a.account_no WHERE ra.account_no = ? AND a.branch_id = ?");
				stmt2 = conn.prepareStatement("SELECT name FROM customer WHERE id = ?");
				
				stmt1.setLong(1, accountNo);
				stmt1.setInt(2, branchId);
				rs1 = stmt1.executeQuery();
				
				if(rs1.next()) {
					type_id = rs1.getInt("type_id");
					
			        customerId= rs1.getLong("customer_id");
			        balance = rs1.getFloat("balance");
			        openingDate = rs1.getDate("opening_date").toLocalDate();
			        isActive = rs1.getBoolean("active");
			        
			        if(rs1.getDate("closing_date") != null)
			        	closingDate = rs1.getDate("closing_date").toLocalDate();
			        else
			        	closingDate = null;
			        
					stmt2.setLong(1, customerId);
					rs2 = stmt2.executeQuery();
					if(rs2.next())
						customerName = rs2.getString("name");
					
					// update getting nominee
					switch(RegularAccountType.getType(type_id)) {
						case SAVINGS : account = new SavingsAccount(accountNo, customerId, customerName, null, branchId, balance, openingDate, closingDate, isActive); break;
						case CURRENT : account = new CurrentAccount(accountNo, customerId, customerName, null, branchId, balance, openingDate, closingDate, isActive); break;
					}
					
					// Cache recent transactions of account.
					recentTransactions = transactionDAO.getRecentTransactions(conn, accountNo);
					
					for(Transaction transaction : recentTransactions)
						account.addTransaction(transaction);
					
					// Load cards linked with account.
					for(DebitCard card : cardDAO.getAll(conn, accountNo))
						account.addDebitCard(card);
					
					// ADD to cache.
					branch.addAccount(AccountCategory.REGULAR, account.getTypeId(), account);
					System.out.println("fetched from db regular account");
				}
			} else {
				System.out.println("served from cache regular account");
			}
		} catch(ClassCastException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "internal error";		
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