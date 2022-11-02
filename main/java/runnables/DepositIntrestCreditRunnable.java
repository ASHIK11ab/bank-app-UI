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
import dao.AccountDAO;
import dao.TransactionDAO;
import util.Factory;


public class DepositIntrestCreditRunnable implements Runnable {
	private final int DEPOSIT_INTREST_CREDIT_DATE = 21;
	
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
		Statement stmt1 = null;
		PreparedStatement stmt2 = null, stmt3 = null;
		ResultSet rs1 = null, rs2 = null;
		
		AccountDAO accountDAO = Factory.getAccountDAO();
		TransactionDAO transactionDAO = Factory.getTransactionDAO();
		
		LocalDate today = null;
		boolean isEligibleForIntrest;
		float intrestRate, intrestAmount = 0, fromAccountBeforeBalance, toAccountBeforeBalance;
		byte typeId;
		
		// FD -> deposit amount is the amount deposited.
		// RD -> deposit amount is the monthly installment.
		int depositAmount = 0, intrestCreditedMonthCnt;
		long accountNo, bankAccountNo = AppCache.getBank().getBankAccountNo();

		try {
			while(!exit) {
				today = LocalDate.now();
				if(today.getDayOfMonth() == DEPOSIT_INTREST_CREDIT_DATE) {
					
					System.out.println("\nIntrest credit for deposit account started started: " + LocalDateTime.now());
					
					try {
						conn = Factory.getDataSource().getConnection();
						stmt1 = conn.createStatement();
						
						stmt2 = conn.prepareStatement("SELECT to_account_no FROM transaction WHERE to_account_no = ? AND date BETWEEN ? AND ?");
						stmt3 = conn.prepareStatement("UPDATE deposit_account SET intrest_credited_month_cnt = ? WHERE account_no = ?");
						
						rs1 = stmt1.executeQuery("SELECT da.account_no, da.type_id, da.rate_of_intrest, da.amount_per_month, da.intrest_credited_month_cnt FROM deposit_account da LEFT JOIN account a ON da.account_no = a.account_no");
						
						// Get all deposit accounts.
						while(rs1.next()) {
							isEligibleForIntrest = true;
							
							accountNo = rs1.getLong("account_no");
							typeId = rs1.getByte("type_id");
							intrestRate = rs1.getFloat("rate_of_intrest");
							depositAmount = rs1.getInt("amount_per_month");
							intrestCreditedMonthCnt = rs1.getInt("intrest_credited_month_cnt");
														
							if(typeId == DepositAccountType.RD.id) {
								System.out.println("RD: " + accountNo);
								stmt2.setLong(1, accountNo);
								stmt2.setDate(2, Date.valueOf(today.withDayOfMonth(1)));
								stmt2.setDate(3, Date.valueOf(today.withDayOfMonth(20)));
								rs2 = stmt2.executeQuery();
								
								/* Installment of this month was not made on RD, account is
									not eligible for intrest */
								if(!rs2.next()) {
									isEligibleForIntrest = false;
								}
							}
							
							if(isEligibleForIntrest) {
								intrestCreditedMonthCnt++;
								
								// calculate intrest amount for current month.
								intrestAmount = calculateIntrestAmount(typeId, intrestRate, depositAmount, intrestCreditedMonthCnt);
								
								// credit intrest to bank.
								fromAccountBeforeBalance = accountDAO.updateBalance(conn, bankAccountNo, 0, intrestAmount);	// deduct from bank account
								toAccountBeforeBalance = accountDAO.updateBalance(conn, accountNo, 1, intrestAmount);		// credit to deposit account
								transactionDAO.create(conn, 1, ("Intrest credit for deposit A/C: " + accountNo), bankAccountNo, accountNo, intrestAmount, true, true, fromAccountBeforeBalance, toAccountBeforeBalance);
								
								stmt3.setInt(1, intrestCreditedMonthCnt);
								stmt3.setLong(2, accountNo);
								stmt3.executeUpdate();
								System.out.println("Intrest credited for A/C: " + accountNo);
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