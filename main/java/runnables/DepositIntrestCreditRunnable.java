package runnables;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import cache.AppCache;
import constant.Constants;
import constant.DepositAccountType;
import constant.RegularAccountType;
import constant.TransactionType;
import dao.AccountDAO;
import dao.DepositAccountDAO;
import dao.RegularAccountDAO;
import dao.TransactionDAO;
import model.Bank;
import model.Transaction;
import model.account.DepositAccount;
import model.account.RegularAccount;
import util.Factory;


// Intrest is credited on monthly basis.
public class DepositIntrestCreditRunnable implements Runnable {
	private final int FIRST_BATCH_INTREST_CREDIT_DATE = 21;
	private final int SECOND_BATCH_INTREST_CREDIT_DATE = 11;
	
	private boolean exit = false;
	
	
	protected float calculateIntrestAmount(byte typeId, float intrestRate, int principal, int intrestCreditedMonthCnt) {
		float intrestAmount = 0;
		
		switch(DepositAccountType.getType(typeId)) {
			case FD: intrestAmount = (principal * intrestRate) / 12; break;
			case RD: intrestAmount = (principal * ((intrestCreditedMonthCnt * (intrestCreditedMonthCnt + 1)) / 2) * intrestRate) / 12; break;
		}
	
		return intrestAmount;
	}
	
	
	@Override
	public void run() {
		Connection conn = null;
		PreparedStatement stmt1 = null, stmt2 = null, stmt3 = null;
		ResultSet rs1 = null, rs2 = null;
		
		AccountDAO accountDAO = Factory.getAccountDAO();
		RegularAccountDAO regularAccountDAO = Factory.getRegularAccountDAO();
		DepositAccountDAO depositAccountDAO = Factory.getDepositAccountDAO();
		TransactionDAO transactionDAO = Factory.getTransactionDAO();
		
		Bank bank = AppCache.getBank();
		
		Transaction transaction = null;
		RegularAccount bankAccount = null;
		DepositAccount account = null;
		LocalDate today = null, openingDate = null, maturityDate = null;
		boolean isEligibleForIntrest;
		String description = "";
		float intrestRate, intrestAmount = 0, fromAccountBeforeBalance, toAccountBeforeBalance;
		int tenureMonths = -1;
		byte typeId;
		
		// FD -> variable 'deposit amount' is the amount deposited.
		// RD -> variable 'deposit amount' is the monthly installment.
		int depositAmount = 0, intrestCreditedMonthCnt, branchId;
		long accountNo, transactionId;
		
		long bankAccountNo = bank.getBankAccountNo();
		int bankAccountBranchId = bank.getAccountBranchId();
		
		boolean isProcessingFirstBatch;
		// Range of dates which is applicable for the respective batch.
		// 1 to 15 for first batch and 16 to 28 for second batch.
		int batchRecurringStartDay, batchRecurringEndDay;
		
		try {
			while(!exit) {
				today = LocalDate.now();
				if(today.getDayOfMonth() == FIRST_BATCH_INTREST_CREDIT_DATE || today.getDayOfMonth() == SECOND_BATCH_INTREST_CREDIT_DATE) {
					
					isProcessingFirstBatch = (today.getDayOfMonth() == FIRST_BATCH_INTREST_CREDIT_DATE);
					
					batchRecurringStartDay = (isProcessingFirstBatch) ? 1 : 16;
					batchRecurringEndDay = (isProcessingFirstBatch) ? 15: 28;
					
					System.out.println("\nIntrest credit for deposit account started started: " + LocalDateTime.now());
					
					if(isProcessingFirstBatch)
						System.out.println("Deposit intrest credit: Processing first batch deposits");
					else
						System.out.println("Deposit intrest credit: Processing second batch deposits");
					
					try {
						conn = Factory.getDataSource().getConnection();
						stmt1 = conn.prepareStatement("SELECT da.account_no, da.type_id, da.rate_of_intrest, da.deposit_amount, da.intrest_credited_month_cnt, da.tenure_months, a.opening_date, a.branch_id FROM deposit_account da LEFT JOIN account a ON da.account_no = a.account_no WHERE a.closing_date IS NULL AND (da.intrest_credited_date IS NULL OR da.intrest_credited_date != ?) AND (type_id = ? OR (type_id = ? AND date_part('day', recurring_date) BETWEEN ? AND ?))");
						// Check whether monthly installment paid for RD.
						stmt2 = conn.prepareStatement("SELECT COUNT(*) FROM transaction WHERE to_account_no = ? AND date BETWEEN ? AND ?");
						stmt3 = conn.prepareStatement("UPDATE deposit_account SET intrest_credited_month_cnt = ?, intrest_credited_date = ? WHERE account_no = ?");
						
						// Prevent crediting intrest again when server is restarted on 'DEPOSIT_INTREST_CREDIT_DATE'.
						stmt1.setDate(1, Date.valueOf(today));
						
						// Get all deposits
						stmt1.setInt(2, DepositAccountType.FD.id);
						stmt1.setInt(3, DepositAccountType.RD.id);
						
						// In case of rd, Only retrieve deposits which are applicable for the respective batch (i.e. first or second).
						stmt1.setInt(4, batchRecurringStartDay);
						stmt1.setInt(5, batchRecurringEndDay);
						
						rs1 = stmt1.executeQuery();
						
						// Get all deposit unclosed accounts.
						while(rs1.next()) {
							isEligibleForIntrest = true;
							
							accountNo = rs1.getLong("account_no");
							typeId = rs1.getByte("type_id");
							intrestRate = rs1.getFloat("rate_of_intrest");
							depositAmount = rs1.getInt("deposit_amount");
							intrestCreditedMonthCnt = rs1.getInt("intrest_credited_month_cnt");
							openingDate = rs1.getDate("opening_date").toLocalDate();
							tenureMonths = rs1.getInt("tenure_months");
							branchId = rs1.getInt("branch_id");
							
							System.out.println("\nDeposit credit: Processing A/C: " + accountNo + ", " + DepositAccountType.getType(typeId));
											
							maturityDate = openingDate.plusMonths(tenureMonths);
							// Check whether deposit has reached maturity.
							if(today.isAfter(maturityDate)) {
								System.out.println("Deposit credit: A/C: " + accountNo + " has reached maturity. skipping");
								continue;
							}
							
							if(typeId == DepositAccountType.RD.id) {
								stmt2.setLong(1, accountNo);
								
								// Grace period of 5 days is provided for customer's to make manual RD payment
								// incase of unsuccessfull auto debit.
								if(isProcessingFirstBatch) {
									stmt2.setDate(2, Date.valueOf(today.withDayOfMonth(batchRecurringStartDay)));
									stmt2.setDate(3, Date.valueOf(today.withDayOfMonth(batchRecurringEndDay + 5)));
								} else {
									stmt2.setDate(2, Date.valueOf(today.minusMonths(1).withDayOfMonth(batchRecurringStartDay)));
									stmt2.setDate(3, Date.valueOf(today.minusMonths(1).withDayOfMonth(batchRecurringEndDay).plusDays(5)));
								}
								

								rs2 = stmt2.executeQuery();
								
								/* Installment of this month was not made on RD, account is
									not eligible for intrest */
								if(!rs2.next()) {
									isEligibleForIntrest = (rs2.getInt("count") == 1) ? true : false;
								}
							}
							
							if(isEligibleForIntrest) {
								account = depositAccountDAO.get(accountNo, branchId);
								bankAccount = regularAccountDAO.get(bankAccountNo, bankAccountBranchId);
								
								synchronized(bankAccount) {
									synchronized (account) {
										intrestCreditedMonthCnt++;
										
										// calculate intrest amount for current month.
										intrestAmount = calculateIntrestAmount(typeId, intrestRate, depositAmount, intrestCreditedMonthCnt);
										
										
										// Ensure sufficient balance in bank Account.
										if(bankAccount.getBalance() >= intrestAmount) {
											// credit intrest to bank.
											fromAccountBeforeBalance = accountDAO.updateBalance(conn, bankAccountNo, 0, intrestAmount);	// deduct from bank account
											toAccountBeforeBalance = accountDAO.updateBalance(conn, accountNo, 1, intrestAmount);		// credit to deposit account
											
											// update in cache.
											bankAccount.deductAmount(intrestAmount);
											account.addAmount(intrestAmount);
											
											description = "Intrest credit for deposit A/C: " + accountNo;
											
											transactionId = transactionDAO.create(conn, TransactionType.NEFT.id, description, bankAccountNo, accountNo, intrestAmount, true, true, fromAccountBeforeBalance, toAccountBeforeBalance);
											
											stmt3.setInt(1, intrestCreditedMonthCnt);
											stmt3.setDate(2, Date.valueOf(today));
											stmt3.setLong(3, accountNo);
											stmt3.executeUpdate();
											
											transaction = new Transaction(transactionId, TransactionType.NEFT.id, bankAccountNo, accountNo, intrestAmount, LocalDateTime.now(), description, fromAccountBeforeBalance);
											bankAccount.addTransaction(transaction);
											
											System.out.println("Deposit credit: Intrest of rupees " + intrestAmount + " credited for A/C: " + accountNo);
										} else {
											System.out.println("Deposit credit: Insufficient balance in bank Account !!!");
											break;
										}
									}
								}
							} else {
								System.out.println("Deposit credit: A/C: " + accountNo + " not eligible for intrest credit");
							}
						}						
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
					
					System.out.println("\nIntrest credit for deposit accounts complete: " + LocalDateTime.now());
					
				} else {
					System.out.println("Today is not deposit account intrest credit date. Skipping");
				}
				
				// Sleep thread for one day.
				System.out.println("Depsoit credit Thread put to sleep for " + Constants.ONE_DAY_IN_MILLIS);
				Thread.sleep(Constants.ONE_DAY_IN_MILLIS);
			}
		} catch(InterruptedException e) {
			exit = true;
		}
	}
}