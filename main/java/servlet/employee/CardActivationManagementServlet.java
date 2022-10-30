package servlet.employee;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DebitCardDAO;
import model.card.DebitCard;
import util.Factory;
import util.Util;


public class CardActivationManagementServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setAttribute("actionType", 0);
		req.getRequestDispatcher("/jsp/employee/cardActivationManagement.jsp").include(req, res);
	}


	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		DebitCardDAO cardDAO = Factory.getDebitCardDAO();
		
		DebitCard card = null;
		boolean isError = false, exceptionOccured = false;
		String msg = null;
		long cardNo;
		byte activationType;
		int branchId, actionType;
		
		try {
			branchId = (Integer) req.getSession(false).getAttribute("branch-id"); 
			actionType = Integer.parseInt(req.getParameter("action-type"));
			
			cardNo = Long.parseLong(req.getParameter("card-no"));
			activationType = Byte.parseByte(req.getParameter("activation-type"));
			
			if(Util.getNoOfDigits(cardNo) != 12 ) {
				isError = true;
				msg = "card no must be a 12 digit number";
			}
			
			if(activationType != 0 && activationType != 1) {
				isError = true;
				msg = "invalid activation type value !!!";
			}
			
			// Display card details for confirmation.
			if(!isError && actionType == 0) {
				card = cardDAO.get(cardNo);
				
				if(card != null) {
					req.setAttribute("actionType", 1);
					req.setAttribute("activationType", activationType);
					req.setAttribute("card", card);
					req.getRequestDispatcher("/jsp/employee/cardActivationManagement.jsp").forward(req, res);
				} else {
					isError = true;
					msg = "invalid card number !!!";
				}
			}
			
			// If update requried, perform update.
			if(!isError && actionType == 1) {
				
				card = cardDAO.get(cardNo);
				
				// Type 0 - Deactivate, type 1 - activate.
				if(activationType == 0 && !card.getIsActive()) {
					isError = true;
					msg = "Card is aldready deactivated !!!";
				}
				
				if(activationType == 1 && card.getIsActive()) {
					isError = true;
					msg = "Card is aldready activated !!!";
				}
				
				synchronized (card) {					
					if(!isError) {
						cardDAO.setActivationStatus(cardNo, (activationType == 0) ? false : true);
						out.println(Util.createNotification("Card Activation status updated successfully", "success"));
						doGet(req, res);
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
			
			if(isError || exceptionOccured) {
				out.println(Util.createNotification(msg, "danger"));
				doGet(req, res);
			}
			
			out.close();
		}
	}

}
