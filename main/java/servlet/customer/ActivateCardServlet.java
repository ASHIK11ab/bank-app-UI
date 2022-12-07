package servlet.customer;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.AccountCategory;
import dao.CustomerDAO;
import dao.DebitCardDAO;
import model.card.DebitCard;
import model.user.Customer;
import util.Factory;
import util.Util;


public class ActivateCardServlet extends HttpServlet {
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		
		Connection conn = null;
		
		CustomerDAO customerDAO = Factory.getCustomerDAO();
		DebitCardDAO cardDAO = Factory.getDebitCardDAO();
		
		DebitCard card = null;
		Customer customer = null;
		
		LocalDate today = LocalDate.now();
		boolean isError = false, exceptionOccured = false, isCardExists = true;
		String msg = null;
		long cardNo = -1, customerId = -1;
		int pin = -1, cvv;
		
		try {
			cardNo = Long.parseLong(req.getParameter("card-no"));
			pin = Integer.parseInt(req.getParameter("pin"));
			cvv = Integer.parseInt(req.getParameter("cvv"));
			
			if(Util.getNoOfDigits(cardNo) != 12 ) {
				isError = true;
				msg = "card no must be a 12 digit number";
			}
			
			if(!isError && Util.getNoOfDigits(pin) != 4) {
					isError = true;
					msg = "Pin number should be 4 digits !!!";
			}
			
			if(!isError && Util.getNoOfDigits(cvv) != 3) {
				isError = true;
				msg = "Cvv number should be 3 digits !!!";
			}
			
			if(!isError) {
				card = cardDAO.get(cardNo);
				if(card == null) {
					isCardExists = false;
				} else {
					customerId = (Long) req.getSession(false).getAttribute("id");
					customer = customerDAO.get(customerId);
					
					// Card nunber entered does not belong to this customer.
					if(customer.getAccountBranchId(AccountCategory.REGULAR, card.getLinkedAccountNo()) == -1) {
						isCardExists = false;
					}
				}
			}

			if(!isCardExists) {
				res.sendRedirect("/bank-app/customer/card?msg=Invalid card details&status=danger");
				return;
			}

			// If update requried, perform update.
			if(!isError) {
				
				synchronized (card) {
					
					if(!isError && card.isDeactivated()) {
						isError = true;
						msg = "Card is deactivated !!! cannot update card status !!!";
					}
					
					if(!isError && card.isActivated()) {
						isError = true;
						msg = "Card is aldready activated !!!";
					}
					
					if(!isError && today.isBefore(card.getValidFromDate())) {
						isError = true;
						msg = "Can only activate card on or after " + card.getValidFromDate().toString();
					}
					
					if(!isError && card.getPin() != pin || card.getCvv() != cvv) {
						isError = true;
						msg = "Incorrect pin or cvv !!!";
					}
					
					if(!isError) {
						conn = Factory.getDataSource().getConnection();
						cardDAO.activateCard(conn, cardNo);
						card.activateCard();
						msg = "Card Activated Successfully";
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
				out.println(Util.createNotification(msg, "danger"));
				req.setAttribute("cardNo", cardNo);
				req.getRequestDispatcher("/jsp/customer/activateCard.jsp").include(req, res);
				out.close();
			} else {
				res.sendRedirect(String.format("/bank-app/customer/card/%d/view?msg=%s&status=success", cardNo, msg));
			}
		}
	}
}