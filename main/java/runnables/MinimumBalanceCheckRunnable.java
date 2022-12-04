package runnables;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import cache.AppCache;
import constant.Constants;
import constant.RegularAccountType;
import constant.TransactionType;
import dao.AccountDAO;
import dao.RegularAccountDAO;
import dao.TransactionDAO;
import model.Transaction;
import model.account.RegularAccount;
import model.account.SavingsAccount;
import util.Factory;

/* Calculates the closing balance of savings accounts on each day and
 * debit's charges when the monthly average balance is lesser than the
 * required minimum balance.
 */
public class MinimumBalanceCheckRunnable implements Runnable {
	public boolean exit = false;
	private static final int MINIMUM_BALANCE_DEBIT_DATE = 4;
	
	@Override
	public void run() {
		Connection conn = null;
		PreparedStatement stmt1 = null, stmt2 = null, stmt3 = null;
		ResultSet rs1 = null, rs2 = null;
		
		TransactionDAO transactionDAO = Factory.getTransactionDAO();
		RegularAccountDAO regularAccountDAO = Factory.getRegularAccountDAO();
		AccountDAO accountDAO = Factory.getAccountDAO();
		
		RegularAccount account = null, bankAccount = null;
		Transaction transaction = null;
		
		LocalDateTime dateTime;
		LocalDate today;
		
		float closing_balance, sum_closing_balance;
		int closing_balance_calculated_days;
		
		long accountNo, fromAccountNo, transactionId;
		int accountBranchId;
		float transactionAmount, beforeBalance, fromAccountBeforeBalance, toAccountBeforeBalance;
		
		String description;
		
		long bankAccountNo = AppCache.getBank().getBankAccountNo();
		int bankAccountBranchId;
		
		try {
			while(!exit) {
				today = LocalDate.now();
				
				System.out.println("Minimum balance check thread started: " + LocalDateTime.now());
				
				try {
					conn = Factory.getDataSource().getConnection();
					
					bankAccountBranchId = accountDAO.getBranchId(conn, bankAccountNo);
					
					stmt1 = conn.prepareStatement("SELECT ra.account_no, ra.sum_closing_balance, ra.closing_balance_calculated_days, a.branch_id FROM regular_account ra LEFT JOIN account a ON ra.account_no = a.account_no WHERE ra.active = true AND ra.type_id = ? AND (ra.closing_balance_calculated_on IS NULL OR ra.closing_balance_calculated_on != ?)");
					
					stmt2 = conn.prepareStatement("SELECT t.from_account_no, t.amount, at.before_balance FROM transaction t JOIN account_transaction at ON t.id = at.transaction_id WHERE at.account_no = ? AND t.date < ? ORDER BY t.id DESC LIMIT 1");
				
					stmt3 = conn.prepareStatement("UPDATE regular_account SET sum_closing_balance = ?, closing_balance_calculated_days = ?, closing_balance_calculated_on = ? WHERE account_no = ?");
					
					stmt1.setInt(1, RegularAccountType.SAVINGS.id);
					// Prevent calculating intrest again in same day. (Occurs when server restarted in same day).
					stmt1.setDate(2, Date.valueOf(today));
					
					rs1 = stmt1.executeQuery();
					
					// Get all active savings accounts along with the previously calculated closing balance details.
					while(rs1.next()) {
						accountNo = rs1.getLong("account_no");
						sum_closing_balance = rs1.getFloat("sum_closing_balance");
						closing_balance_calculated_days = rs1.getInt("closing_balance_calculated_days");
						accountBranchId = rs1.getInt("branch_id");
						
						stmt2.setLong(1, accountNo);
						stmt2.setDate(2, Date.valueOf(today.minusDays(1)));
						rs2 = stmt2.executeQuery();
						
						// Get yesterday's closing balance of the account.
						if(rs2.next()) {
							fromAccountNo = rs2.getLong("from_account_no");
							transactionAmount = rs2.getFloat("amount");
							beforeBalance = rs2.getFloat("before_balance");
							
							// Calculate yesterday's closing balance based on the transaction (Debit / Credit).
							closing_balance = (fromAccountNo == accountNo) ? beforeBalance - transactionAmount : beforeBalance + transactionAmount; 
							
							// Update accounts closing balance details
							sum_closing_balance += closing_balance;
							closing_balance_calculated_days++;
							
							// If today is minimum balance debit date, if applicable,
							// Debit charges.
							if(today.getDayOfMonth() == MINIMUM_BALANCE_DEBIT_DATE) {
								// If monthly average balance deficit, debit charges.
								if( (sum_closing_balance / closing_balance_calculated_days) < SavingsAccount.getMinimumBalance() ) {
									
									account = regularAccountDAO.get(accountNo, accountBranchId);
									bankAccount = regularAccountDAO.get(bankAccountNo, bankAccountBranchId);
									
									synchronized (bankAccount) {
										synchronized (account) {
											dateTime = LocalDateTime.now();
											
											fromAccountBeforeBalance = accountDAO.updateBalance(conn, accountNo, 0, Constants.MINIMUM_BALANCE_DEFECIT_CHARGES); // Debit
											toAccountBeforeBalance = accountDAO.updateBalance(conn, bankAccountNo, 1, Constants.MINIMUM_BALANCE_DEFECIT_CHARGES); // Credit to bank account.
											
											// update in cache.
											account.deductAmount(Constants.MINIMUM_BALANCE_DEFECIT_CHARGES);
											bankAccount.addAmount(Constants.MINIMUM_BALANCE_DEFECIT_CHARGES);
											
											description = "Minimum balance defecit";
											
											transactionId = transactionDAO.create(conn, TransactionType.NEFT.id, description, accountNo, bankAccountNo, Constants.MINIMUM_BALANCE_DEFECIT_CHARGES, true, true, fromAccountBeforeBalance, toAccountBeforeBalance);
											
											transaction = new Transaction(transactionId, TransactionType.NEFT.id, accountNo, bankAccountNo, Constants.MINIMUM_BALANCE_DEFECIT_CHARGES, dateTime, description, fromAccountBeforeBalance);
											account.addTransaction(transaction);
											
											transaction = new Transaction(transactionId, TransactionType.NEFT.id, accountNo, bankAccountNo, Constants.MINIMUM_BALANCE_DEFECIT_CHARGES, dateTime, description, toAccountBeforeBalance);
											bankAccount.addTransaction(transaction);
											
											System.out.println("Minimum balance deficit charges debited for A/C: " + accountNo);
										}
									}
									// End of synchronized block on bank account.
								}
								
								// Reset for next month.
								sum_closing_balance = 0;
								closing_balance_calculated_days = 0;
							}
							
							stmt3.setFloat(1, sum_closing_balance);
							stmt3.setInt(2, closing_balance_calculated_days);
							stmt3.setDate(3, Date.valueOf(today));
							stmt3.setLong(4, accountNo);
							stmt3.executeUpdate();
						}
						// End of 'if' block on result set 2.
					}
					// End of processing all savings account.
				} catch(SQLException e) {
					System.out.println(e.getMessage());
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
					    if(stmt3 != null)
					        stmt3.close();
					} catch(SQLException e) { System.out.println(e.getMessage()); }
					
					try {
					    if(conn != null)
					        conn.close();
					} catch(SQLException e) { System.out.println(e.getMessage()); }
				}
				
				System.out.println("Minimum balance check completed: " + LocalDateTime.now());
				
				// Sleep thread for one day.
				System.out.println("Minimum balance check thread put to sleep for " + Constants.ONE_DAY_IN_MILLIS);
				Thread.sleep(Constants.ONE_DAY_IN_MILLIS);
			}	
		} catch(InterruptedException e) {
			exit = true;
		}
	}
}