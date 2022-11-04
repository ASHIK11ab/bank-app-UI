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

import constant.Role;
import dao.DepositAccountDAO;
import dao.RegularAccountDAO;
import dao.TransactionDAO;
import model.Transaction;
import model.account.Account;
import util.Factory;
import util.Util;


public class AccountTransactionHistoryServlet extends HttpServlet {
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		
		Role role = null;
		LocalDate fromDate, toDate;
		long accountNo = -1, customerId = -1;
		boolean exceptionOccured = false, isError = false, isAccountExists = false;
		String msg = "", redirectURI = "", userType = "", accountCategoryName = "";
		int branchId, accountCategory = -1;
		
		Account account = null;
		LinkedList<Transaction> transactions;
		
		TransactionDAO transactionDAO = Factory.getTransactionDAO();
		RegularAccountDAO regularAccountDAO = Factory.getRegularAccountDAO();
		DepositAccountDAO depositAccountDAO = Factory.getDepositAccountDAO();
		
		try {
			
			role = (Role) req.getSession(false).getAttribute("role"); 
			
			branchId = (Integer) req.getSession(false).getAttribute("branch-id");
			accountNo = Long.parseLong(req.getParameter("account-no"));
			accountCategory = Integer.parseInt(req.getParameter("account-category"));
			fromDate = LocalDate.parse(req.getParameter("from-date"));
			toDate = LocalDate.parse(req.getParameter("to-date"));
			
			if(Util.getNoOfDigits(accountNo) != 11 ) {
				isError = true;
				msg = "A/C no must be a 11 digit number";
			}
			
	        // Invalid date range.
	        if(fromDate.isAfter(toDate)) {
	            isError = true;
	            msg = "Invalid date range";
	        }
			
	        if(!isError) {
	        	account = regularAccountDAO.get(accountNo);
	        	
	        	if(account == null)
	        		account = depositAccountDAO.get(accountNo);
				
	        	// Access for account differs for customer and employee.
	        	switch(role) {
		        	case EMPLOYEE: 
		        					if(account != null && account.getBranchId() == branchId)
		        						isAccountExists = true;
		        					break;
		        	case CUSTOMER: 
		        					customerId = (Long) req.getSession(false).getAttribute("id"); 
		        					if(account != null && account.getCustomerId() == customerId)
		        						isAccountExists = true;
		        					break;
		        	default: isAccountExists = false;
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
				
				switch(accountCategory) {
					case 0: accountCategoryName = "account"; break;
					case 1: accountCategoryName = "deposit"; break;
					default: accountCategoryName = "";
				}
				
				userType = Role.getName(role);
				
				redirectURI = String.format("/bank-app/%s/%s/%d/transaction-history?msg=%s&status=danger", userType, accountCategoryName, accountNo, msg);
				res.sendRedirect(redirectURI);
				out.close();
			}
			
		}
	}

}
