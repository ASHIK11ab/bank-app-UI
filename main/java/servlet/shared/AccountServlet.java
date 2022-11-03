package servlet.shared;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.Role;
import dao.RegularAccountDAO;
import model.account.RegularAccount;
import util.Factory;
import util.Util;


public class AccountServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		
		RegularAccountDAO accountDAO = Factory.getRegularAccountDAO();
		RegularAccount account = null;
		
		Role role = null;
		boolean isError = false, exceptionOccured = false, pageFound = true;
		String path = req.getPathInfo(), action = "", msg = "", queryMsg = null, status = null;
		String [] result;
		long accountNo = -1;
		int branchId = -1;
		
		queryMsg = req.getParameter("msg");
		status = req.getParameter("status");
		
		// Display notification if exists.
		if(queryMsg != null && status != null)
			out.println(Util.createNotification(queryMsg, status));
		
		if(path == null || path.equals("/")) {
			req.setAttribute("actionType", 0);
			req.getRequestDispatcher("/jsp/components/viewAccount.jsp").include(req, res);
			return;
		}
		
		try {
			path = path.substring(1);
			result = path.split("/");
			
			role = (Role) req.getSession(false).getAttribute("role"); 
			branchId = (Integer) req.getSession(false).getAttribute("branch-id");
			
			accountNo = Long.parseLong(result[0]);
			action = result[1];
			
			account = accountDAO.get(accountNo);
			if(account == null || account.getBranchId() != branchId) {
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
								req.getRequestDispatcher("/jsp/components/viewAccount.jsp").include(req, res); break;
					case "transaction-history":
												req.setAttribute("actionType", 0);
												req.setAttribute("accountCategory", 0);
												req.getRequestDispatcher("/jsp/components/accountTransactionHistory.jsp").include(req, res); break;
					default: pageFound = false;
				}
				
				// role specific switch
				if(role == Role.EMPLOYEE && !pageFound) {
					pageFound = true;
					
					switch(action) {
						case "close":
									req.setAttribute("accountNo", accountNo);
									req.getRequestDispatcher("/jsp/employee/closeAccountConfirmation.jsp").include(req, res); break;
						default: pageFound = false;
					}
				}
			}
			
			if(!pageFound) {
				isError = true;
				msg = "page not found !!!";
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
				System.out.println(msg);
				out.println(Util.createNotification(msg, "danger"));
				req.setAttribute("actionType", 0);
				req.getRequestDispatcher("/jsp/components/viewAccount.jsp").include(req, res);
			}
			
			out.close();
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
}