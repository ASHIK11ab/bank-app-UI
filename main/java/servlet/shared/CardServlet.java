package servlet.shared;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.Role;
import dao.DebitCardDAO;
import dao.RegularAccountDAO;
import model.account.RegularAccount;
import model.card.DebitCard;
import util.Factory;
import util.Util;


public class CardServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		
		RegularAccountDAO accountDAO = Factory.getRegularAccountDAO();
		DebitCardDAO cardDAO = Factory.getDebitCardDAO();
		DebitCard card = null;
		RegularAccount account = null;
		
		Role role = null;
		boolean isError = false, exceptionOccured = false;
		String path = req.getPathInfo(), action = "", msg = "", queryMsg = null, status = null;
		String [] result;
		long cardNo = -1, customerId = -1;
		int branchId = -1;
				
		queryMsg = req.getParameter("msg");
		status = req.getParameter("status");
		
		// Display notification if exists.
		if(queryMsg != null && status != null)
			out.println(Util.createNotification(queryMsg, status));
		
		if(path == null || path.equals("/")) {
			req.setAttribute("actionType", 0);
			req.getRequestDispatcher("/jsp/components/viewCard.jsp").include(req, res);
			return;
		}
		
		try {
			role = (Role) req.getSession(false).getAttribute("role"); 
			path = path.substring(1);
			result = path.split("/");
			
			cardNo = Long.parseLong(result[0]);
			action = result[1];
			
			if(Util.getNoOfDigits(cardNo) != 12 ) {
				isError = true;
				msg = "card no must be a 12 digit number";
			}
			
			card = cardDAO.get(cardNo);
			
			if(card != null) {		
				account = accountDAO.get(card.getLinkedAccountNo());
			} else {
				isError = true;
				msg = "Invalid card details !!!";
			}
			
			if(!isError) {
	        	// Access for card differs for customer and employee.
	        	switch(role) {
		        	case EMPLOYEE: 
		    						branchId = (Integer) req.getSession(false).getAttribute("branch-id");
		        					if(account == null || account.getBranchId() != branchId) {
		        						isError = true;
		        						msg = "Account linked with card does not exist in branch !!!";
		        					}
		        					break;
		        	case CUSTOMER: 
		        					customerId = (Long) req.getSession(false).getAttribute("id"); 
		        					if(account == null || account.getCustomerId() != customerId && !card.isDeactivated()) {
		        						isError = true;
		        						msg = "Card not found !!!";
		        					}
		        					break;
		        	default: break;
	        	}
			}
			
			if(!isError) {
				req.setAttribute("cardNo", cardNo);
				req.setAttribute("card", card);
				
				switch(action) {
					case "view":
								req.setAttribute("actionType", 1);
								req.getRequestDispatcher("/jsp/components/viewCard.jsp").include(req, res); break;
					case "block-unblock":
										if(card.isDeactivated()) {
											isError = true;
											msg = "card is deactivated !!! cannot block or unblock !!!";
										}
										
										if(!isError && !card.isActivated()) {
											isError = true;
											msg = "card is not activated !!! cannot block or unblock !!!";
										}
										
										if(!isError) {
											req.setAttribute("actionType", 0);
											req.setAttribute("cardNo", cardNo);
											req.getRequestDispatcher("/jsp/components/blockUnblockCard.jsp").include(req, res); break;	
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
				req.getRequestDispatcher("/jsp/components/viewCard.jsp").include(req, res);
				out.close();
			}
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		
		Role role = null;
		boolean exceptionOccured = false, isError = false;
		String errorMsg = "", userType = "", redirectURI = "";
		long cardNo;
		
		try {
			role = (Role) req.getSession(false).getAttribute("role");
			userType = Role.getName(role);
			cardNo = Long.parseLong(req.getParameter("card-no"));
			redirectURI = String.format("/bank-app/%s/card/%d/view", userType, cardNo);
			res.sendRedirect(redirectURI);
		} catch(NumberFormatException e) {
			exceptionOccured = true;
			errorMsg = "internal error";
		} finally {
			
			if(isError || exceptionOccured) {
				out.println(Util.createNotification(errorMsg, "danger"));
				req.setAttribute("actionType", 0);
				req.getRequestDispatcher("/jsp/components/viewCard.jsp").include(req, res);
			}
			
			out.close();
		}
	}
}
