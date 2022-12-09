package servlet.employee;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DebitCardDAO;
import dao.RegularAccountDAO;
import model.card.DebitCard;
import util.Factory;
import util.Util;


public class DeactivateCardServlet extends HttpServlet {
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {		
		Connection conn = null;
		
		RegularAccountDAO accountDAO = Factory.getRegularAccountDAO();
		DebitCardDAO cardDAO = Factory.getDebitCardDAO();
		
		DebitCard card = null;
		
		boolean isError = false, exceptionOccured = false, isCardExists = true;
		String msg = null;
		long cardNo = -1;
		int branchId;
		
		try {
			cardNo = Long.parseLong(req.getParameter("card-no"));
			
			if(!isError && Util.getNoOfDigits(cardNo) != 12 ) {
				isError = true;
				msg = "card no must be a 12 digit number";
			}
			
			if(!isError) {
				card = cardDAO.get(cardNo);
				if(card == null) {
					isCardExists = false;
				} else {
					branchId = (Integer) req.getSession(false).getAttribute("branch-id");					
					
					if(accountDAO.get(card.getLinkedAccountNo(), branchId) == null) {
						isCardExists = false;
					}
				}
			}

			if(!isCardExists) {
				res.sendRedirect("/bank-app/employee/card/?msg=Invalid card details&status=danger");
				return;
			}

			// If update requried, perform update.
			if(!isError) {
				
				synchronized (card) {
					
					if(!isError && card.isDeactivated()) {
						isError = true;
						msg = "Card is aldready deactivated !!!";
					}
					
					if(!isError) {
						conn = Factory.getDataSource().getConnection();
						cardDAO.deactivateCard(conn, cardNo);
						card.deactivateCard();
						msg = "Card Deactivated Successfully";
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
				res.sendRedirect(String.format("/bank-app/employee/card/%d/view?msg=%s&status=danger", cardNo, msg));
			} else {
				res.sendRedirect(String.format("/bank-app/employee/card/%d/view?msg=%s&status=success", cardNo, msg));
			}
		}
	}
}