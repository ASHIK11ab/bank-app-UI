package servlet.employee;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class InitiateAccountCreationServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.getRequestDispatcher("/jsp/employee/initiateAccountCreation.jsp").include(req, res);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		int customerType, accountType, cardType;
		
		try {
        	customerType = Integer.parseInt(req.getParameter("customer-type"));
        	accountType = Integer.parseInt(req.getParameter("account-type"));
        	cardType = Integer.parseInt(req.getParameter("card-type"));
        	
        	// Pass the data to next page.
			req.setAttribute("accountType", accountType);
			req.setAttribute("cardType", cardType);
        	
        	switch(customerType) {
        		case 0: req.getRequestDispatcher("/jsp/employee/createCustomer.jsp").forward(req, res); break;
        		case 1: req.getRequestDispatcher("/jsp/employee/createAccount.jsp").forward(req, res); break;
        		default:
        			out.println("<div class='notification danger'>" + "invalid customer type !!!" + "</div>");
        			doGet(req, res);
        	}
        	
		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			out.println("<div class='notification danger'>" + "internal error !!!" + "</div>");
		} finally {
			out.close();
		}
	}
}