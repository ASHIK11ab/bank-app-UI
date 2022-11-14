package runnables;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import constant.Constants;
import constant.DepositAccountType;
import dao.AccountDAO;
import dao.DepositAccountDAO;
import dao.RegularAccountDAO;
import dao.TransactionDAO;
import model.account.DepositAccount;
import model.account.RegularAccount;
import util.Factory;

public class AutoDebitRDRunnable implements Runnable {
	private boolean exit = false;
	
	
	@Override
	public void run() {
		Connection conn = null;
		PreparedStatement stmt1 = null, stmt2 = null, stmt3 = null;
		ResultSet rs1 = null, rs2 = null;
		
		RegularAccountDAO regularAccountDAO = Factory.getRegularAccountDAO();
		DepositAccountDAO depositAccountDAO = Factory.getDepositAccountDAO();
		AccountDAO accountDAO = Factory.getAccountDAO();
		TransactionDAO transactionDAO = Factory.getTransactionDAO();
		
		RegularAccount debitFromAccount;
		DepositAccount rdAccount;
		LocalDate today;
		boolean monthlyInstallmentPaid = false;
		float fromAccountBeforeBalance, toAccountBeforeBalance;
		int monthlyInstallment, branchId, debitFromAccountBranchId;
		long debitFromAccountNo, rdAccountNo;
		
		try {
			while(!exit) {
				today = LocalDate.now();
				try {
					System.out.println("\nAuto Debit for RD thread started: " + LocalDateTime.now());
					if(today.getDayOfMonth() <= 15) {
						conn = Factory.getDataSource().getConnection();
						
						// Get all RD's whose recurring date is today.
						stmt1 = conn.prepareStatement("SELECT da.account_no, da.deposit_amount, da.debit_from_account_no, a.branch_id FROM deposit_account da LEFT JOIN account a ON da.account_no = a.account_no WHERE da.type_id = ? AND da.recurring_date = ? AND a.closing_date IS NULL");
						stmt2 = conn.prepareStatement("SELECT COUNT(*) FROM transaction WHERE to_account_no = ? AND date BETWEEN ? AND ?");
						stmt3 = conn.prepareStatement("UPDATE deposit_account SET recurring_date = ? WHERE account_no = ?");
						
						stmt1.setInt(1, DepositAccountType.RD.id);
						stmt1.setDate(2, Date.valueOf(today));
						rs1 = stmt1.executeQuery();
						
						while(rs1.next()) {
							monthlyInstallmentPaid = false;
							
							rdAccountNo = rs1.getLong("account_no");
							monthlyInstallment = rs1.getInt("deposit_amount");
							debitFromAccountNo = rs1.getLong("debit_from_account_no");
							branchId = rs1.getInt("branch_id");
							
							rdAccount = depositAccountDAO.get(rdAccountNo, branchId);
							
							synchronized (rdAccount) {
								// Check if monthly installment payment has been made aldready.
								stmt2.setLong(1, rdAccountNo);
								stmt2.setDate(2, Date.valueOf(LocalDate.now().withDayOfMonth(1)));
								stmt2.setDate(3, Date.valueOf(today));
								
								rs2 = stmt2.executeQuery();
								
								if(rs2.next()) {
									monthlyInstallmentPaid = (rs2.getInt("count") > 1) ? true : false;
									
									// Only auto debit for RD if current month installment is not aldready paid.
									if(!monthlyInstallmentPaid) {
											debitFromAccountBranchId = accountDAO.getBranchId(conn, debitFromAccountNo);
										
											debitFromAccount = regularAccountDAO.get(debitFromAccountNo, debitFromAccountBranchId);
											
											synchronized (debitFromAccount) {
												
												// Ensure for sufficient balance in debit from account.
												if(debitFromAccount.getIsActive() && debitFromAccount.getBalance() >= monthlyInstallment) {
													fromAccountBeforeBalance = accountDAO.updateBalance(conn, debitFromAccountNo, 0, monthlyInstallment);
													toAccountBeforeBalance = accountDAO.updateBalance(conn, rdAccountNo, 1, monthlyInstallment);
													
													// update cache.
													debitFromAccount.deductAmount(monthlyInstallment);
													rdAccount.addAmount(monthlyInstallment);
													
													transactionDAO.create(conn, 1, ("RD withdrawal for A/C: " + rdAccountNo), debitFromAccountNo, rdAccountNo, monthlyInstallment, true, true, fromAccountBeforeBalance, toAccountBeforeBalance);
												
													// update RD recurring date to next month.
													stmt3.setDate(1, Date.valueOf(today.plusMonths(1)));
													stmt3.setLong(2, rdAccountNo);
													stmt3.executeUpdate();
													
													System.out.println("Auto debited for RD: " + rdAccountNo + " from A/C: " + debitFromAccountNo);
												} else {
													// Handle auto debit rd insufficient balance.
													System.out.println("Insufficient balance or debit from account not active !!!");
													System.out.println("Cannot auto debit for RD: " + rdAccountNo);
												}
											}
											// End of synchronised block on debit from account.
										} else {
											System.out.println("Montly installment aldready paid rd A/C: " + rdAccountNo);
										}
									}
								}
							// End of synchronised block on rd account
						}
						// End of inner while loop
						System.out.println("Auto Debit for RD thread completed: " + LocalDateTime.now());
					} else {
						System.out.println("Auto debit for RD is not applicable today");
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
				
				System.out.println("Auto Debit for RD thread put to sleep for: " + Constants.ONE_DAY_IN_MILLIS);
				Thread.sleep(Constants.ONE_DAY_IN_MILLIS);
			}
			// End of outer while loop
		} catch(InterruptedException e) {
			exit = true;
		}
	}
}