package servlet.shared;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.Role;
import dao.DepositAccountDAO;
import model.account.DepositAccount;
import util.Factory;
import util.Util;


public class DepositServlet extends HttpServlet {
	
	// Dispatches the request to the respective jsp's.
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		
		DepositAccountDAO accountDAO = Factory.getDepositAccountDAO();
		DepositAccount account = null;
		
		Role role = null;
		boolean isError = false, exceptionOccured = false, isAccountExists = false;
		String path = req.getPathInfo(), action = "", msg = "", queryMsg = null, status = null;
		String [] result;
		long accountNo = -1, customerId = -1;
		int branchId = -1;
				
		queryMsg = req.getParameter("msg");
		status = req.getParameter("status");
		
		// Display notification if exists.
		if(queryMsg != null && status != null)
			out.println(Util.createNotification(queryMsg, status));
		
		if(path == null || path.equals("/")) {
			req.setAttribute("actionType", 0);
			req.getRequestDispatcher("/jsp/components/viewDeposit.jsp").include(req, res);
			return;
		}
		
		try {
			role = (Role) req.getSession(false).getAttribute("role"); 
			path = path.substring(1);
			result = path.split("/");
			
			branchId = (Integer) req.getSession(false).getAttribute("branch-id");
			
			accountNo = Long.parseLong(result[0]);
			action = result[1];
			
			account = accountDAO.get(accountNo);
			
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
        	
        	if(!isAccountExists) {
        		isError = true;
        		msg = "Account not found !!!";
        	}
			
			if(!isError) {
				req.setAttribute("account", account);
				
				switch(action) {
					case "view":
								req.setAttribute("actionType", 1);
								req.getRequestDispatcher("/jsp/components/viewDeposit.jsp").include(req, res); break;
					case "transaction-history":
												req.setAttribute("actionType", 0);
												req.setAttribute("accountCategory", 1);
												req.getRequestDispatcher("/jsp/components/accountTransactionHistory.jsp").include(req, res); break;
					case "close":
								req.setAttribute("actionType", 0);
								req.setAttribute("accountNo", accountNo);
								req.getRequestDispatcher("/jsp/components/closeDeposit.jsp").include(req, res); break;
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
				req.setAttribute("actionType", 0);
				req.getRequestDispatcher("/jsp/components/viewDeposit.jsp").include(req, res);
			}
			
			out.close();
		}
	}
}