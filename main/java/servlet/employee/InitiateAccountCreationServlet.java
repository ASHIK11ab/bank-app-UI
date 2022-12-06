package servlet.employee;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.DebitCardType;
import constant.RegularAccountType;
import model.card.DebitCard;
import util.Util;

public class InitiateAccountCreationServlet extends HttpServlet {
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.getRequestDispatcher("/jsp/employee/initiateAccountCreation.jsp").include(req, res);
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();

		String msg = "";
		boolean isError = false, exceptionOccured = false;
		int customerType, accountType, cardType;
		
		try {
        	customerType = Integer.parseInt(req.getParameter("customer-type"));
        	accountType = Integer.parseInt(req.getParameter("account-type"));
        	cardType = Integer.parseInt(req.getParameter("card-type"));
        	
        	if(RegularAccountType.getType(accountType) == null) {
        		isError = true;
        		msg = "Invalid account selected !!!";
        	}
        	
        	if(!isError && DebitCardType.getType((byte) cardType) == null) {
        		isError = true;
        		msg = "Invalid card selected !!!";
        	}
        	
        	if(!isError) {
	        	// Pass the data to next page.
				req.setAttribute("accountType", accountType);
				req.setAttribute("cardType", cardType);
	        	
	        	switch(customerType) {
	        		case 0: req.getRequestDispatcher("/jsp/employee/createCustomer.jsp").forward(req, res); break;
	        		case 1: req.setAttribute("actionType", 0);
	        				req.getRequestDispatcher("/jsp/employee/createAccount.jsp").forward(req, res); break;
	        		default:
	        			isError = true;
	        			msg = "invalid customer type !!!";
	        	}
        	}
        	
		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "internal error !!!";
		} finally {
			
			if(isError || exceptionOccured) {
				out.println(Util.createNotification(msg, "danger"));
				doGet(req, res);
			}
			
			out.close();
		}
	}
}