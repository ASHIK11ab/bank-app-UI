package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedList;

import cache.AppCache;
import constant.AccountCategory;
import model.account.Account;
import model.account.RegularAccount;
import model.card.DebitCard;
import model.user.Customer;
import util.Factory;

public class AccountDAO {
	static final String ACCOUNT_CREATION_QUERY = "INSERT INTO account (customer_id, branch_id, balance, opening_date, nominee_id) VALUES (?, ?, ?, ?, ?)";
	
	public boolean delete(Connection conn, long accountNo) throws SQLException {
		PreparedStatement stmt = null;
		
		String msg = "";
		boolean exceptionOccured = false;
		int rowsAffected = -1;
		
		try {
			stmt = conn.prepareStatement("DELETE FROM account WHERE account_no = ?");
			stmt.setLong(1, accountNo);
			rowsAffected = stmt.executeUpdate();
		} catch(SQLException e) {
			System.out.println(e.getMessage());
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
	
	
	// Returns branch id associated with an account.
	public int getBranchId(Connection conn, long accountNo) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int branchId = -1;
		
		String msg = "";
		boolean exceptionOccured = false;
		
		try {
			stmt = conn.prepareStatement("SELECT branch_id FROM account WHERE account_no = ?");
			stmt.setLong(1, accountNo);
			
			rs = stmt.executeQuery();
			
			if(rs.next())
				branchId = rs.getInt("branch_id");
			
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
        }
				
		if(exceptionOccured)
			throw new SQLException(msg);
		else
			return branchId;
	}
	
	
	public void closeAccount(Connection conn, Account account, AccountCategory category) throws SQLException {
		PreparedStatement stmt1 = null, stmt2 = null;
		
		DebitCardDAO cardDAO = Factory.getDebitCardDAO();
		RegularAccount regularAccount = null;
		
		Customer customer = null;
		Collection<DebitCard> linkedCards = null;
		LocalDate today = LocalDate.now();
		String msg = "";
		boolean exceptionOccured = false;
		
		try {
			stmt1 = conn.prepareStatement("UPDATE account SET closing_date = ? WHERE account_no = ?");
			
			stmt1.setDate(1, Date.valueOf(today));
			stmt1.setLong(2, account.getAccountNo());
			stmt1.executeUpdate();
			
			// category 0 - regular account, update active status to false.
			if(category == AccountCategory.REGULAR) {
				
				regularAccount = (RegularAccount) account;
				
				stmt2 = conn.prepareStatement("UPDATE regular_account SET active = false WHERE account_no = ?");
				stmt2.setLong(1, account.getAccountNo());
				stmt2.executeUpdate();
				
				// deactivate all linked cards
				linkedCards = regularAccount.getCards();
				for(DebitCard card : linkedCards)
					cardDAO.deactivateCard(conn, card.getCardNo());
			}
			
			// update in cache.
			account.setClosingDate(today);
			
			customer = AppCache.getBank().getCustomer(account.getCustomerId());
			
			if(customer != null) {
				synchronized (customer) {
					customer.removeAccountBranchMapping(category, account.getAccountNo());
				}
			}
		} catch(SQLException e) {
			System.out.println(e.getMessage());
            exceptionOccured = true;
            msg = "internal error";
        } catch(ClassCastException e) {
			System.out.println(e.getMessage());
            exceptionOccured = true;
            msg = "internal error";
        } finally {
            try {
                if(stmt1 != null)
                    stmt1.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
            
            try {
                if(stmt2 != null)
                    stmt2.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
        }
				
		if(exceptionOccured)
			throw new SQLException(msg);
	}
}