package servlet.shared;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

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


public class BlockUnblockCardServlet extends HttpServlet {
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		Connection conn = null;
		CustomerDAO customerDAO = Factory.getCustomerDAO();
		RegularAccountDAO accountDAO = Factory.getRegularAccountDAO();
		DebitCardDAO cardDAO = Factory.getDebitCardDAO();
		
		DebitCard card = null;
		Customer customer = null;
		RegularAccount account = null;
		
		Role role = null;
		boolean isError = false, exceptionOccured = false;
		String msg = null, userType = "", redirectURI = "", status = "", redirectPage = "";
		long cardNo = -1, customerId = -1;
		byte activationType;
		int branchId, pin = -1;
		
		try {
			role = (Role) req.getSession(false).getAttribute("role");
			userType = Role.getName(role);
			cardNo = Long.parseLong(req.getParameter("card-no"));
			activationType = Byte.parseByte(req.getParameter("activation-type"));
			
			if(Util.getNoOfDigits(cardNo) != 12 ) {
				isError = true;
				msg = "card no must be a 12 digit number";
			}
			
			if(!isError && activationType != 0 && activationType != 1) {
				isError = true;
				msg = "invalid activation type value !!!";
			}
			
			if(!isError && role == Role.CUSTOMER) {
				pin = Integer.parseInt(req.getParameter("pin"));
				if(Util.getNoOfDigits(pin) != 4) {
					isError = true;
					msg = "Pin number should be 4 digits !!!";
				}
			}
			
			if(!isError) {
				card = cardDAO.get(cardNo);
				if(card == null) {
					isError = true;
					msg = "Invalid card details !!!";
				}
			}
			
			if(!isError) {
				
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
		        					customerId = (Long) req.getSession(false).getAttribute("id");
		        					customer = customerDAO.get(customerId);
		        					// Card nunber entered does not belong to this customer.
		        					if(customer.getAccountBranchId(AccountCategory.REGULAR, card.getLinkedAccountNo()) == -1) {
		        						isError = true;
		        						msg = "Card not exist !!!";
		        					}
		        					break;
		        	default: break;
	        	}
			}
			
			// If update requried, perform update.
			if(!isError) {
				
				synchronized (card) {
					
					if(role == Role.CUSTOMER && card.getPin() != pin) {
						isError = true;
						msg = "Incorrect pin !!!";
					}
					
					if(!isError && card.isDeactivated()) {
						isError = true;
						msg = "Card is deactivated !!! cannot update card status !!!";
					}
					
					if(!isError && !card.isActivated()) {
						isError = true;
						msg = "Card is not activated !!! ask customer to activate card !!!";
					}
					
					// Type 0 - Block, type 1 - Unblock.
					if(activationType == 0 && !card.getIsActive()) {
						isError = true;
						msg = "Card is aldready blocked !!!";
					}
					
					if(activationType == 1 && card.getIsActive()) {
						isError = true;
						msg = "Card is not blocked, cannot unblock !!!";
					}
					
					if(!isError) {
						conn = Factory.getDataSource().getConnection();
						cardDAO.setCardActiveStatus(conn, cardNo, (activationType == 0) ? false : true);
					}
				}
			}
			
		} catch(ClassCastException e) {
			System.out.print(e.getMessage());
			exceptionOccured = true;
			msg = "internal error !!!";
		} catch(NumberFormatException e) {
			System.out.print(e.getMessage());
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
			
			if(isError || exceptionOccured) {
				status = "danger";
				redirectPage = "block-unblock";
			} else {
				msg = "card status updated successfully";
				status = "success";
				redirectPage = "view";
			}
			
			redirectURI = String.format("/bank-app/%s/card/%d/%s?msg=%s&status=%s", userType, cardNo, redirectPage, msg, status);
			res.sendRedirect(redirectURI);
		}
	}
}