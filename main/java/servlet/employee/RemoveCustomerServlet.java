package servlet.employee;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.AccountCategory;
import constant.DepositAccountType;
import constant.TransactionType;
import dao.AccountDAO;
import dao.CustomerDAO;
import dao.RegularAccountDAO;
import dao.TransactionDAO;
import model.account.RegularAccount;
import model.user.Customer;
import util.Factory;
import util.Util;


public class RemoveCustomerServlet extends HttpServlet {
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Connection conn = null;
		String msg = "";
		long accountNo = -1;
		int branchId;
		float beforeBalance = 0;
		boolean exceptionOccured = false, isError = false;
		
		TransactionDAO transactionDAO = Factory.getTransactionDAO();
		AccountDAO accountDAO = Factory.getAccountDAO();
		RegularAccountDAO regularAccountDAO = Factory.getRegularAccountDAO();
		CustomerDAO customerDAO = Factory.getCustomerDAO();
		
		Customer customer = null;
		RegularAccount account = null;
		
		try {
			branchId = (Integer) req.getSession(false).getAttribute("branch-id");
			accountNo = Long.parseLong(req.getParameter("account-no"));
			
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
                    		transactionDAO.create(conn, TransactionType.CASH.id, ("Closing of A/C: " + accountNo), accountNo, null, beforeBalance, true, false, beforeBalance, 0);
                    		account.deductAmount(beforeBalance);
                    	}
                    	accountDAO.closeAccount(conn, account, AccountCategory.REGULAR);
						customerDAO.removeCustomer(conn, customer.getId(), accountNo);
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
