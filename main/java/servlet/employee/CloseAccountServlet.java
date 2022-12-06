package servlet.employee;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cache.AppCache;
import constant.AccountCategory;
import constant.RegularAccountType;
import constant.TransactionType;
import dao.AccountDAO;
import dao.RegularAccountDAO;
import dao.TransactionDAO;
import model.Branch;
import model.Transaction;
import model.account.RegularAccount;
import util.Factory;
import util.Util;


public class CloseAccountServlet extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Connection conn = null;
		PreparedStatement stmt1 = null, stmt2 = null;
		ResultSet rs1 = null, rs2 = null;
		
		PrintWriter out = res.getWriter();
		
		TransactionDAO transactionDAO = Factory.getTransactionDAO();
		RegularAccountDAO regularAccountDAO = Factory.getRegularAccountDAO();
		AccountDAO accountDAO = Factory.getAccountDAO();
		
		Branch branch = null;
		RegularAccount account = null;
		Transaction transaction = null;
		
		String msg = "";
		boolean exceptionOccured = false, isError = false, requireConfirmation = false;
		long accountNo = -1, customerId = -1, transactionId;
		float beforeBalance = 0;
		int branchId, noOfAccounts = -1;
		
		try {
			branchId = (Integer) req.getSession(false).getAttribute("branch-id");
			accountNo = Long.parseLong(req.getParameter("account-no"));
			
			branch = AppCache.getBranch(branchId);
			
			if(Util.getNoOfDigits(accountNo) != 11 ) {
				isError = true;
				msg = "A/C no must be a 11 digit number";
			}
			
			if(!isError) {
				account = regularAccountDAO.get(accountNo, branchId);
				if(account == null || account.isClosed()) {
					isError = true;
					msg = "Account does not exist !!!";
				}
			}
			
			// Close account.
			if(!isError) {
				synchronized (account) {
					customerId = account.getCustomerId();
					conn = Factory.getDataSource().getConnection();
					
		            // Check whether account is linked with any active deposit account(s).
		            stmt1 = conn.prepareStatement("SELECT da.account_no FROM deposit_account da LEFT JOIN account a ON da.account_no = a.account_no WHERE (da.debit_from_account_no = ? OR da.payout_account_no = ?) AND (a.closing_date IS NULL) limit 1");
		            stmt1.setLong(1, accountNo);
		            stmt1.setLong(2, accountNo);
		            rs1 = stmt1.executeQuery();
                
	                if(rs1.next()) {
	                	isError = true;
	                	msg = "Account is linked with deposit account(s) cannot close account !!!";
	                } else {
	                	// get number of opened accounts for customer
	    				stmt2 = conn.prepareStatement("select count(*) as count from account WHERE customer_id = ? AND closing_date IS NULL");
	                    stmt2.setLong(1, customerId);
	                    rs2 = stmt2.executeQuery();
	                    
	                    if(rs2.next())
	                        noOfAccounts = rs2.getInt("count");
                    
	                    // Customer has only one account in bank. Require confirmation
	                    if(noOfAccounts == 1) {
	                    	requireConfirmation = true;
	                    	String note = "customer has only one account, closing this account will remove customer from bank";
	                    	req.setAttribute("accountNo", accountNo);
	                    	req.setAttribute("note", note);
	                    	req.getRequestDispatcher("/jsp/employee/customerRemovalConfirmationPage.jsp").forward(req, res);
	                    } else {
	                    	// If account has balance, credit balance as cash to user, create a transaction record.
	                    	if(account.getBalance() > 0) {
	                    		beforeBalance = accountDAO.updateBalance(conn, accountNo, 0, account.getBalance());
	                    		transactionId = transactionDAO.create(conn, TransactionType.CASH.id, ("Closing of A/C: " + accountNo), accountNo, null, beforeBalance, true, false, beforeBalance, 0);
	                    		transaction = new Transaction(transactionId, TransactionType.CASH.id, account.getAccountNo(), -1, account.getBalance(), LocalDateTime.now(), ("Closing of A/C: " + account.getAccountNo()), beforeBalance);
	                    		
	                    		account.deductAmount(beforeBalance);
	                    		account.addTransaction(transaction);
	                    		
	                    		// update stats.
			            		synchronized (branch) {
			            			switch(RegularAccountType.getType(account.getTypeId())) {
				            			case SAVINGS: branch.setSavingsAccountCnt(branch.getSavingsAccountCnt() - 1); break;
				            			case CURRENT: branch.setCurrentAccountCnt(branch.getCurrentAccountCnt() - 1); break;
			            			}									
								}
	                    	}
	                    	
	                    	// close account.
	                    	accountDAO.closeAccount(conn, account, AccountCategory.REGULAR);
	                    }
	                }
				}
			}
		} catch(ClassCastException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "internal error !!!";
		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "internal error !!!";
		} catch(SQLException e) {
			exceptionOccured = true;
			msg = e.getMessage();
		} finally {
			
            try {
                if(conn != null)
                    conn.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
			
			if(isError || exceptionOccured) {
				res.sendRedirect("/bank-app/employee/account/" + accountNo + "/view?msg=" + msg + "&status=danger");
			} else {
				if(!requireConfirmation)
					res.sendRedirect("/bank-app/employee/account?msg=account closed successfully&status=success");	
			}

			out.close();
		}
	}
}