package servlet.shared;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.AccountCategory;
import constant.Role;
import dao.CustomerDAO;
import dao.DebitCardDAO;
import dao.RegularAccountDAO;
import model.account.RegularAccount;
import model.card.DebitCard;
import model.user.Customer;
import util.Factory;
import util.Util;


public class CardServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		
		CustomerDAO customerDAO = Factory.getCustomerDAO();
		RegularAccountDAO accountDAO = Factory.getRegularAccountDAO();
		DebitCardDAO cardDAO = Factory.getDebitCardDAO();
		
		Customer customer = null;
		DebitCard card = null;
		RegularAccount account = null;
		
		LocalDate today = LocalDate.now();
		Role role = null;
		boolean isError = false, exceptionOccured = false;
		String path = req.getPathInfo(), action = "", msg = "";
		String queryMsg = null, status = null, redirectURI = "", userType;
		String [] result;
		long cardNo = -1, customerId = -1;
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
					req.setAttribute("cards", customer.getCards());
				
				req.getRequestDispatcher("/jsp/components/viewCard.jsp").include(req, res);
				return;
			}
			
			path = path.substring(1);
			result = path.split("/");
			
			cardNo = Long.parseLong(result[0]);
			action = result[1];
			
			if(Util.getNoOfDigits(cardNo) != 12 ) {
				isError = true;
				msg = "card no must be a 12 digit number";
			}
			
			if(!isError) {
				card = cardDAO.get(cardNo);
				if(card == null) {
					isError = true;
					msg = "Invalid card details !!!";
				}				
			}
			
			
			if(!isError) {
	        	// Access for card differs for customer and employee.
				switch(role) {
		        	case EMPLOYEE: 
		    						branchId = (Integer) req.getSession(false).getAttribute("branch-id");
		    						account = accountDAO.get(card.getLinkedAccountNo(), branchId);
	
		    						if(account == null) {
		        						isError = true;
		        						msg = "Account linked with card does not exist in branch !!!";
		        					}
		        					break;
		        	case CUSTOMER: 
		        					// Card number entered does not belong to this customer.
		        					if(customer.getAccountBranchId(AccountCategory.REGULAR, card.getLinkedAccountNo()) == -1
		        							|| card.isDeactivated()) {
		        						isError = true;
		        						msg = "Invalid card details !!!";
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
											req.getRequestDispatcher("/jsp/components/blockUnblockCard.jsp").include(req, res); break;	
										}
										break;
					case "activate":
									if(role != Role.CUSTOMER) {
										isError = true;
										msg = "page not found !!!";
									}
									
									if(!isError && card.isActivated()) {
										isError = true;
										msg = "card is aldready activated !!!";
									}
									
									if(!isError && today.isBefore(card.getValidFromDate())) {
										isError = true;
										msg = "Can only activate card on or after " + card.getValidFromDate().toString();
									}
									
									if(!isError)
										req.getRequestDispatcher("/jsp/customer/activateCard.jsp").forward(req, res);
									break;
									
					case "deactivate":
									if(role != Role.EMPLOYEE) {
										isError = true;
										msg = "page not found !!!";
									}
									
									if(!isError && card.isDeactivated()) {
										isError = true;
										msg = "card is aldready dactivated !!!";
									}
									
									if(!isError) {
										req.getRequestDispatcher("/jsp/employee/deactivateCard.jsp").forward(req, res);
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
				userType = Role.getName(role);
				redirectURI = String.format("/bank-app/%s/card?msg=%s&status=danger", userType, msg);
				res.sendRedirect(redirectURI);
			}
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {		
		Role role = null;
		boolean exceptionOccured = false, isError = false;
		String userType = "", redirectURI = "";
		long cardNo;
		
		try {
			role = (Role) req.getSession(false).getAttribute("role");
			userType = Role.getName(role);
			cardNo = Long.parseLong(req.getParameter("card-no"));
			redirectURI = String.format("/bank-app/%s/card/%d/view", userType, cardNo);
			res.sendRedirect(redirectURI);
		} catch(NumberFormatException e) {
			exceptionOccured = true;
		} finally {
			
			if(isError || exceptionOccured) {
				res.sendRedirect(String.format("/bank-app/%s/card/", userType));
			}
			
		}
	}
}
