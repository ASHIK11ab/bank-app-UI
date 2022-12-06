package servlet.shared;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.AccountCategory;
import constant.Role;
import dao.CustomerDAO;
import dao.RegularAccountDAO;
import model.Transaction;
import model.account.RegularAccount;
import model.user.Customer;
import util.Factory;
import util.Util;


public class AccountServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		
		CustomerDAO customerDAO = Factory.getCustomerDAO();
		RegularAccountDAO accountDAO = Factory.getRegularAccountDAO();
		
		RegularAccount account = null;
		Customer customer = null;
		
		Role role = null;
		boolean isError = false, exceptionOccured = false, pageFound = true, isAccessGranted = false;
		String path = req.getPathInfo(), action = "", msg = "", queryMsg = null, status = null;
		String [] result;
		long accountNo = -1, customerId = -1;
		int branchId = -1;
		
		queryMsg = req.getParameter("msg");
		status = req.getParameter("status");
		
		// Display notification if exists.
		if(queryMsg != null && status != null)
			out.println(Util.createNotification(queryMsg, status));
		
		try {
			role = (Role) req.getSession(false).getAttribute("role");
			
			// load customer's regular Accounts.
			if(role == Role.CUSTOMER) {
				customerId = (Long) req.getSession(false).getAttribute("id");
				customer = customerDAO.get(customerId);
			}
			
			if(path == null || path.equals("/")) {
				
				// Set customer's account in request for displaying accounts as a dropdown.
				if(role == Role.CUSTOMER)
					_setCustomerAccountsInRequest(req, customer);
				
				req.setAttribute("actionType", 0);
				req.getRequestDispatcher("/jsp/components/viewAccount.jsp").include(req, res);
				return;
			}
			
			path = path.substring(1);
			result = path.split("/");
			
			accountNo = Long.parseLong(result[0]);
			action = result[1];
						
        	// Access for account differs for customer and employee.
        	switch(role) {
	        	case EMPLOYEE: 
	    						branchId = (Integer) req.getSession(false).getAttribute("branch-id");
	    						account = accountDAO.get(accountNo, branchId);

	        					if(account != null)
	        						isAccessGranted = true;
	        					break;
	        	case CUSTOMER: 
	        					// customer cannot access closed account.
	        					branchId = customer.getAccountBranchId(AccountCategory.REGULAR, accountNo);
        						account = accountDAO.get(accountNo, branchId);
	        					if(account != null && !account.isClosed())
	        						isAccessGranted = true;
	        					
	        					break;
	        	default: isAccessGranted = false;
        	}
        	
        	if(!isAccessGranted) {
        		isError = true;
        		msg = "Account not found !!!";
        	}
			
			if(!isError) {
				req.setAttribute("account", account);
				
				pageFound = true;
				// Generic switch
				switch(action) {
					case "view":
								req.setAttribute("actionType", 1);
								req.getRequestDispatcher("/jsp/components/viewAccount.jsp").include(req, res);
								break;
					case "transaction-history":
												req.setAttribute("actionType", 0);
												req.setAttribute("accountCategory", AccountCategory.REGULAR.id);
												req.getRequestDispatcher("/jsp/components/accountTransactionHistory.jsp").include(req, res);
												break;
					case "mini-statement":
											req.getRequestDispatcher("/jsp/components/miniStatement.jsp").forward(req, res);
											break;
											
					default: pageFound = false;
				}
				
				// role specific switch
				if(role == Role.EMPLOYEE && !pageFound) {
					pageFound = true;
					
					switch(action) {
						case "close":
									// Only employee has access to a closed account.
									// prevent closing a closed account.
									if(account.isClosed()) {
										isError = true;
										msg = "Account is aldready closed !!!";
									} else {
										req.setAttribute("actionType", 0);
										req.setAttribute("accountNo", accountNo);
										req.getRequestDispatcher("/jsp/employee/closeAccountConfirmation.jsp").include(req, res);								
									}
									break;
						default: pageFound = false;
					}
				}
			}
			
			if(!pageFound) {
				isError = true;
				msg = "page not found !!!";
			}
		} catch(NullPointerException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "Page not found !!!";
		} catch(ClassCastException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "internal error !!!";
		} catch(IndexOutOfBoundsException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "Page not found !!!";
		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "internal error !!!";
		} catch(SQLException e) {
			exceptionOccured = true;
			msg = e.getMessage();
		} finally {
			
			if(isError || exceptionOccured) {
				out.println(Util.createNotification(msg, "danger"));
				
				if(role == Role.CUSTOMER)
					_setCustomerAccountsInRequest(req, customer);
				
				req.setAttribute("actionType", 0);
				req.getRequestDispatcher("/jsp/components/viewAccount.jsp").include(req, res);
			}
			
			out.close();
		}
	}
	
	
	// Handles the post request and redirects to the view page.
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {		
		PrintWriter out = res.getWriter();
		
		Role role = null;
		boolean exceptionOccured = false, isError = false;
		String errorMsg = "", userType = "", redirectURI = "";
		long accountNo;
		
		try {
			role = (Role) req.getSession(false).getAttribute("role");
			userType = Role.getName(role);
			accountNo = Long.parseLong(req.getParameter("account-no"));
			redirectURI = String.format("/bank-app/%s/account/%d/view", userType, accountNo);
			res.sendRedirect(redirectURI);
		} catch(NumberFormatException e) {
			exceptionOccured = true;
			errorMsg = "internal error";
		} finally {
			
			if(isError || exceptionOccured) {
				out.println(Util.createNotification(errorMsg, "danger"));
				req.setAttribute("actionType", 0);
				req.getRequestDispatcher("/jsp/components/viewAccount.jsp").include(req, res);
			}
			
			out.close();
		}
	}
	
	
	// Internally used method, since customer accounts are set at multiple places 
	// in the servlet.
	private void _setCustomerAccountsInRequest(HttpServletRequest req, Customer customer) {
		Collection<Long> savingsAccounts = customer.getSavingsAccounts();
		long currentAccount = customer.getCurrentAccount();
		
		req.setAttribute("savingsAccounts", savingsAccounts);
		
		if(currentAccount != -1)
			req.setAttribute("currentAccount", currentAccount);
	}
}