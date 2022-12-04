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

/* Calculates the intrest by closing balance of savings accounts on each day and
 * credits intrest on quaterly basis.
 */
public class SavingsIntrestCreditRunnable implements Runnable {
	public boolean exit = false;
	private static final int INTREST_CREDIT_DATE = 5;
	
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
		
		float closing_balance, intrestAmount, intrest;
		boolean insufficientBalance = false;
		
		long accountNo, fromAccountNo, transactionId;
		int accountBranchId;
		float transactionAmount, beforeBalance, fromAccountBeforeBalance, toAccountBeforeBalance;
		
		String description;
		
		long bankAccountNo = AppCache.getBank().getBankAccountNo();
		int bankAccountBranchId;
		
		try {
			while(!exit) {
				today = LocalDate.now();
				
				System.out.println("Savings Account Intrest credit thread started: " + LocalDateTime.now());
				
				try {
					conn = Factory.getDataSource().getConnection();
					
					bankAccountBranchId = accountDAO.getBranchId(conn, bankAccountNo);
					
					stmt1 = conn.prepareStatement("SELECT ra.account_no, ra.intrest_amount, a.branch_id FROM regular_account ra LEFT JOIN account a ON ra.account_no = a.account_no WHERE ra.active = true AND ra.type_id = ? AND (ra.intrest_calculated_on IS NULL OR ra.intrest_calculated_on != ?)");
					
					stmt2 = conn.prepareStatement("SELECT t.from_account_no, t.amount, at.before_balance FROM transaction t JOIN account_transaction at ON t.id = at.transaction_id WHERE at.account_no = ? AND t.date < ? ORDER BY t.id DESC LIMIT 1");
				
					stmt3 = conn.prepareStatement("UPDATE regular_account SET intrest_amount = ?, intrest_calculated_on = ? WHERE account_no = ?");
					
					stmt1.setInt(1, RegularAccountType.SAVINGS.id);
					// Prevent calculating intrest again in same day. (Occurs when server restarted in same day).
					stmt1.setDate(2, Date.valueOf(today));
					
					rs1 = stmt1.executeQuery();
					
					// Get all active savings accounts.
					while(rs1.next()) {
						insufficientBalance = false;
						
						accountNo = rs1.getLong("account_no");
						intrestAmount = rs1.getFloat("intrest_amount");
						accountBranchId = rs1.getInt("branch_id");
						
						// Get yesterday's closing balance of the account.
						stmt2.setLong(1, accountNo);
						stmt2.setDate(2, Date.valueOf(today.minusDays(1)));
						rs2 = stmt2.executeQuery();
						
						if(rs2.next()) {
							fromAccountNo = rs2.getLong("from_account_no");
							transactionAmount = rs2.getFloat("amount");
							beforeBalance = rs2.getFloat("before_balance");
							
							// Calculate intrest amount for yesterday's closing balance.
							closing_balance = (fromAccountNo == accountNo) ? beforeBalance - transactionAmount : beforeBalance + transactionAmount; 
							
							// Update total intrest to be credited.
							intrest = closing_balance * (SavingsAccount.getIntrestRate() / 365);
														
							intrestAmount += intrest;
							
							// Credit intrest on quaterly basis.
							if(today.getMonthValue() % 3 == 0 && today.getDayOfMonth() == INTREST_CREDIT_DATE) {
									
								account = regularAccountDAO.get(accountNo, accountBranchId);
								bankAccount = regularAccountDAO.get(bankAccountNo, bankAccountBranchId);
								
								synchronized (bankAccount) {
									synchronized (account) {
										
										if(bankAccount.getBalance() > intrestAmount) {										
											dateTime = LocalDateTime.now();
											
											fromAccountBeforeBalance = accountDAO.updateBalance(conn, bankAccountNo, 0, intrestAmount); // Debit
											toAccountBeforeBalance = accountDAO.updateBalance(conn, accountNo, 1, intrestAmount); // Credit to bank account.
											
											// update in cache.
											bankAccount.deductAmount(intrestAmount);
											account.addAmount(intrestAmount);
											
											description = "Savings Intrest";
											
											transactionId = transactionDAO.create(conn, TransactionType.NEFT.id, description, bankAccountNo, accountNo, intrestAmount, true, true, fromAccountBeforeBalance, toAccountBeforeBalance);
											
											transaction = new Transaction(transactionId, TransactionType.NEFT.id, bankAccountNo, accountNo, intrestAmount, dateTime, description, toAccountBeforeBalance);
											account.addTransaction(transaction);
											
											transaction = new Transaction(transactionId, TransactionType.NEFT.id, bankAccountNo, accountNo, intrestAmount, dateTime, description, fromAccountBeforeBalance);
											bankAccount.addTransaction(transaction);
											
											System.out.println("Savings intrest credited for A/C: " + accountNo);
											
											// Reset intrest for next quarter
											intrestAmount = 0;
										} else {
											insufficientBalance = true;
											System.out.println("Insufficient balance in bank Account !!!");
											exit = true;
										}
									}
								}
								// End of synchronized block on bank account.
							}
							
							if(!insufficientBalance) {
								stmt3.setFloat(1, intrestAmount);
								stmt3.setDate(2, Date.valueOf(today));
								stmt3.setLong(3, accountNo);
								stmt3.executeUpdate();
							}
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
				
				System.out.println("Savings intrest credit thread completed: " + LocalDateTime.now());
				
				// Sleep thread for one day.
				System.out.println("Savings intrest credit thread put to sleep for " + Constants.ONE_DAY_IN_MILLIS);
				Thread.sleep(Constants.ONE_DAY_IN_MILLIS);
			}	
		} catch(InterruptedException e) {
			exit = true;
		}
	}
}