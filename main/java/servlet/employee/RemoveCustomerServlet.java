package servlet.employee;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cache.AppCache;
import constant.AccountCategory;
import constant.DepositAccountType;
import constant.RegularAccountType;
import constant.TransactionType;
import dao.AccountDAO;
import dao.CustomerDAO;
import dao.RegularAccountDAO;
import dao.TransactionDAO;
import model.Bank;
import model.Branch;
import model.Transaction;
import model.account.RegularAccount;
import model.user.Customer;
import util.Factory;
import util.Util;


public class RemoveCustomerServlet extends HttpServlet {
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Connection conn = null;
		String msg = "";
		long accountNo = -1, transactionId;
		int branchId;
		float beforeBalance = 0;
		boolean exceptionOccured = false, isError = false;
		
		TransactionDAO transactionDAO = Factory.getTransactionDAO();
		AccountDAO accountDAO = Factory.getAccountDAO();
		RegularAccountDAO regularAccountDAO = Factory.getRegularAccountDAO();
		CustomerDAO customerDAO = Factory.getCustomerDAO();
		
		Bank bank = AppCache.getBank();
		Transaction transaction = null;
		Branch branch = null;
		Customer customer = null;
		RegularAccount account = null;
		
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
				if(account == null) {
					isError = true;
					msg = "Account does not exist !!!";
				}
				
			}
			
			/* Remove customer (executed only when a customer has only one account in bank
				and is about to be closed */
			if(!isError) {
				customer = customerDAO.get(account.getCustomerId());
				
				synchronized (customer) {
					synchronized(account) {
						conn = Factory.getDataSource().getConnection();
                    	// If account has balance, credit balance as cash to user, create a transaction record.
                    	if(account.getBalance() > 0) {
                    		beforeBalance = accountDAO.updateBalance(conn, accountNo, 0, account.getBalance());
                    		transactionId = transactionDAO.create(conn, TransactionType.CASH.id, ("Closing of A/C: " + accountNo), accountNo, null, beforeBalance, true, false, beforeBalance, 0);
                    		transaction = new Transaction(transactionId, TransactionType.CASH.id, account.getAccountNo(), -1, account.getBalance(), LocalDateTime.now(), ("Closing of A/C: " + account.getAccountNo()), beforeBalance);

                    		account.deductAmount(beforeBalance);
                    		account.addTransaction(transaction);
                    	}
                    	accountDAO.closeAccount(conn, account, AccountCategory.REGULAR);
						customerDAO.removeCustomer(conn, customer.getId(), accountNo);
						
                		// update stats.
	            		synchronized (branch) {
	            			switch(RegularAccountType.getType(account.getTypeId())) {
		            			case SAVINGS: branch.setSavingsAccountCnt(branch.getSavingsAccountCnt() - 1); break;
		            			case CURRENT: branch.setCurrentAccountCnt(branch.getCurrentAccountCnt() - 1); break;
	            			}									
						}
	            		
	            		synchronized (bank) {
	    					bank.setCustomerCnt(bank.getCustomerCnt() - 1);
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
            
			if(isError || exceptionOccured)
				res.sendRedirect("/bank-app/employee/account/" + accountNo + "/close?msg=" + msg + "&status=danger");
			else
				res.sendRedirect("/bank-app/employee/account?msg=account closed and customer removed successfully&status=success");			
		}
	}

}
