package servlet.customer;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import constant.Role;
import dao.CustomerDAO;
import model.user.Customer;
import util.Factory;
import util.Util;


public class LoginServlet extends HttpServlet {
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setAttribute("title", "Customer Login");
		req.setAttribute("actionURL", "/bank-app/login/customer");
		req.getRequestDispatcher("/jsp/components/loginForm.jsp").include(req, res);
	}


	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		
		HttpSession session = null;
		CustomerDAO customerDAO = Factory.getCustomerDAO();
		Customer customer = null;
		long customerId;
		String password, msg = "";
		
		boolean isError = false, exceptionOccured = false;
				
		try {
			customerId = Long.parseLong(req.getParameter("id"));
			password = req.getParameter("password");
			
			customer = customerDAO.get(customerId);    
			
	        if((customer != null) && !customer.isRemoved() && customer.getPassword().equals(password)) {
	        	customer.setLoggedInStatus(true);
	        	// create session.
	        	session = req.getSession();
	        	session.setAttribute("id", customer.getId());
	        	// 1 hour.
	        	session.setMaxInactiveInterval(60*60);
	        	session.setAttribute("role", Role.CUSTOMER);
	            res.sendRedirect("/bank-app/customer/dashboard");
	        } else {
				isError = true;
				msg = "Invalid id or password";
	        }

		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			isError = true;
			msg = "Invalid input !!!";
		} catch(SQLException e) {
			System.out.println(e.getMessage());
			isError = true;
			msg = e.getMessage();
		} finally {
			if(isError || exceptionOccured) {
				out.println(Util.createNotification(msg, "danger"));
				doGet(req, res);
			}
		}
	}
}
