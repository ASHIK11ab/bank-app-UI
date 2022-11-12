package servlet.shared;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.AccountCategory;
import constant.Role;
import dao.CustomerDAO;
import dao.DepositAccountDAO;
import dao.RegularAccountDAO;
import dao.TransactionDAO;
import model.Transaction;
import model.account.Account;
import model.user.Customer;
import util.Factory;
import util.Util;


public class AccountTransactionHistoryServlet extends HttpServlet {
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		
		Role role = null;
		AccountCategory category = null;
		LocalDate fromDate, toDate;
		long accountNo = -1, customerId = -1;
		boolean exceptionOccured = false, isError = false, isAccountExists = false;
		String msg = "", redirectURI = "", userType = "", accountCategoryName = "";
		int branchId = -1, accountCategory = -1;
		
		Customer customer = null;
		Account account = null;
		LinkedList<Transaction> transactions;
		
		CustomerDAO customerDAO = Factory.getCustomerDAO();
		TransactionDAO transactionDAO = Factory.getTransactionDAO();
		RegularAccountDAO regularAccountDAO = Factory.getRegularAccountDAO();
		DepositAccountDAO depositAccountDAO = Factory.getDepositAccountDAO();
		
		try {
			
			role = (Role) req.getSession(false).getAttribute("role"); 
			accountCategory = Integer.parseInt(req.getParameter("account-category"));
			
			category = AccountCategory.getCategory(accountCategory);
			
			accountNo = Long.parseLong(req.getParameter("account-no"));
			fromDate = LocalDate.parse(req.getParameter("from-date"));
			toDate = LocalDate.parse(req.getParameter("to-date"));
			
			if(Util.getNoOfDigits(accountNo) != 11 ) {
				isError = true;
				msg = "A/C no must be a 11 digit number";
			}
			
	        // Invalid date range.
	        if(!isError && fromDate.isAfter(toDate)) {
	            isError = true;
	            msg = "Invalid date range";
	        }
	        
	        if(!isError && category == null) {
	        	isError = true;
	        	msg = "Internal error !!!";
	        }
			
	        if(!isError) {
				
	        	// Get branch id associated with the account.
	        	switch(role) {
		        	case EMPLOYEE: 	branchId = (Integer) req.getSession(false).getAttribute("branch-id");
		        					break;
		        	case CUSTOMER: 
		        					customerId = (Long) req.getSession(false).getAttribute("id");
		        					customer = customerDAO.get(customerId);
		        					branchId = customer.getAccountBranchId(category, accountNo);
		        	default: break;
	        	}
	        	
	        	account = regularAccountDAO.get(accountNo, branchId);
	        	
	        	// Try getting account from deposit account dao.
	        	if(account == null)
	        		account = depositAccountDAO.get(accountNo, branchId);
	        
	        	if(account != null)
	        		isAccountExists = true;
	        }
	        	
			if(isAccountExists) {
				transactions = transactionDAO.getAll(accountNo, fromDate, toDate);
				req.setAttribute("actionType", 1);
				req.setAttribute("account", account);
				req.setAttribute("accountCategory", accountCategory);
				req.setAttribute("transactions", transactions);
				req.getRequestDispatcher("/jsp/components/accountTransactionHistory.jsp").forward(req, res);
			} else {
				isError = true;
				msg = "Account not found !!!";
			}
		} catch(ClassCastException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "internal error";
		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "internal error";
		} catch(SQLException e) {
			exceptionOccured = true;
			msg = e.getMessage();
		} finally {
			
			// Redirect to the respective URL from where request arrived.
			if(isError || exceptionOccured) {
				accountCategoryName = AccountCategory.getName(category);
				
				userType = Role.getName(role);
				
				redirectURI = String.format("/bank-app/%s/%s/%d/transaction-history?msg=%s&status=danger", userType, accountCategoryName, accountNo, msg);
				res.sendRedirect(redirectURI);
				out.close();
			}
			
		}
	}

}
