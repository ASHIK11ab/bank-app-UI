package servlet.shared;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.AccountCategory;
import constant.Role;
import dao.CustomerDAO;
import dao.DepositAccountDAO;
import model.account.DepositAccount;
import model.user.Customer;
import util.Factory;
import util.Util;


public class DepositServlet extends HttpServlet {
	
	// Dispatches the request to the respective jsp's.
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		
		CustomerDAO customerDAO = Factory.getCustomerDAO();
		DepositAccountDAO accountDAO = Factory.getDepositAccountDAO();
		
		Customer customer = null;
		DepositAccount account = null;
		
		Role role = null;
		boolean isError = false, exceptionOccured = false;
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
			
			if(role == Role.CUSTOMER) {
				customerId = (Long) req.getSession(false).getAttribute("id");
				customer = customerDAO.get(customerId);
			}
			
			if(path == null || path.equals("/")) {
				req.setAttribute("actionType", 0);
				
				if(role == Role.CUSTOMER)
					req.setAttribute("customerDeposits", customer.getDepositAccounts());
				
				req.getRequestDispatcher("/jsp/components/viewDeposit.jsp").include(req, res);
				return;
			}
			
			path = path.substring(1);
			result = path.split("/");
						
			accountNo = Long.parseLong(result[0]);
			action = result[1];
			
			if(Util.getNoOfDigits(accountNo) != 11 ) {
				isError = true;
				msg = "Invalid account no !!!";
			}
			
			if(!isError) {
				switch(role) {
		        	case EMPLOYEE: 
		    						branchId = (Integer) req.getSession(false).getAttribute("branch-id");
		    						account = accountDAO.get(accountNo, branchId);
	
		    						if(account == null) {
		        						isError = true;
		        						msg = "Account does not exist !!!";
		        					}
		        					break;
		        	case CUSTOMER: 
		        					branchId = customer.getAccountBranchId(AccountCategory.DEPOSIT, accountNo);
		    						account = accountDAO.get(accountNo, branchId);
		    						// Customer cannot access a closed account.
		    						if(account == null || account.isClosed()) {
		        						isError = true;
		        						msg = "Account does not exist !!!";
		        					}
		        					break;
		        	default: break;
	        	}
			}
						
			if(!isError) {
				req.setAttribute("account", account);
				
				switch(action) {
					case "view":
								req.setAttribute("actionType", 1);
								req.getRequestDispatcher("/jsp/components/viewDeposit.jsp").include(req, res); break;
					case "transaction-history":
												req.setAttribute("actionType", 0);
												req.setAttribute("accountCategory", AccountCategory.DEPOSIT.id);
												req.getRequestDispatcher("/jsp/components/accountTransactionHistory.jsp").include(req, res); break;
					case "close":
								// Only employee has access to a closed account.
								// prevent closing a closed account.
								if(account.isClosed()) {
									isError = true;
									msg = "Account is aldready closed !!!";
								} else {
									req.setAttribute("actionType", 0);
									req.setAttribute("accountNo", accountNo);
									req.getRequestDispatcher("/jsp/components/closeDeposit.jsp").include(req, res); break;									
								}
								break;
					default: 
							isError = true;
							msg = "page not found !!!";
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
			
			if(isError || exceptionOccured) {
				out.println(Util.createNotification(msg, "danger"));
				req.setAttribute("actionType", 0);
				
				if(role == Role.CUSTOMER)
					req.setAttribute("customerDeposits", customer.getDepositAccounts());
				
				req.getRequestDispatcher("/jsp/components/viewDeposit.jsp").include(req, res);
				out.close();
			}
		}
	}
	
	
	// Handles the post request and redirects to the view page.
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {		
		PrintWriter out = res.getWriter();
		
		Role role = null;
		boolean exceptionOccured = false, isError = false;
		String errorMsg = "", userType = "", redirectURI = "";
		long accountNo;
		
		try {
			role = (Role) req.getSession(false).getAttribute("role");
			userType = Role.getName(role);
			accountNo = Long.parseLong(req.getParameter("account-no"));
			redirectURI = String.format("/bank-app/%s/deposit/%d/view", userType, accountNo);
			res.sendRedirect(redirectURI);
		} catch(NumberFormatException e) {
			exceptionOccured = true;
			errorMsg = "internal error";
		} finally {
			
			if(isError || exceptionOccured) {
				out.println(Util.createNotification(errorMsg, "danger"));
				doGet(req, res);
			}
			
			out.close();
		}
	}
}