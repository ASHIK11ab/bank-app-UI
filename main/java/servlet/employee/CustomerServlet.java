package servlet.employee;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.CustomerDAO;
import model.user.Customer;
import util.Factory;
import util.Util;


public class CustomerServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		
		Customer customer = null;
		CustomerDAO customerDAO = Factory.getCustomerDAO();
		
		String path = req.getPathInfo(), msg = "", action = "";
		String queryMsg = "", status = "";
		boolean isError = false, exceptionOccured = false;
		long customerId;
		
		if(path == null || path.equals("/")) {
			req.getRequestDispatcher("/jsp/employee/viewCustomer.jsp").forward(req, res);
			return;
		}
				
		try {
			path = path.substring(1);
			
			customerId = Long.parseLong(path.split("/")[0]);
			action = path.split("/")[1];
			customer = customerDAO.get(customerId);

			if(customer == null) {
				isError = true;
				msg = "Invalid customer id !!!";
			} else {
				req.setAttribute("customer", customer);
				queryMsg = req.getParameter("msg");
				status = req.getParameter("status");
				
				if(queryMsg != null && status != null)
					out.println(Util.createNotification(queryMsg, status));
				
				switch(action) {
					case "view": req.getRequestDispatcher("/jsp/employee/viewCustomer.jsp").include(req, res); break;
					case "update": req.getRequestDispatcher("/jsp/employee/updateCustomer.jsp").include(req, res); break;
					case "password-reset": req.getRequestDispatcher("/jsp/employee/resetCustomerPassword.jsp").include(req, res); break;
					default:
							isError = true;
							msg = "Page not found !!!";
				}
			}
			
		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			isError = true;
			msg = "Invalid input !!!";
		} catch(IndexOutOfBoundsException e) {
			System.out.println(e.getMessage());
			isError = true;
			msg = "Page not found !!!";
		} catch(SQLException e) {
			isError = true;
			msg = e.getMessage();
		} finally {
			
			if(isError || exceptionOccured) {
				out.println(Util.createNotification(msg, "danger"));
				req.setAttribute("customer", null);
				req.getRequestDispatcher("/jsp/employee/viewCustomer.jsp").include(req, res);
				out.close();
			}
		}
	}
	
	
	// Handles the post request and redirects to the view page.
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {		
		PrintWriter out = res.getWriter();
		
		boolean exceptionOccured = false, isError = false;
		String errorMsg = "", redirectURI = "";
		long customerId;
		
		try {
			customerId = Long.parseLong(req.getParameter("customer-id"));
			redirectURI = String.format("/bank-app/employee/customer/%d/view", customerId);
			res.sendRedirect(redirectURI);
		} catch(NumberFormatException e) {
			exceptionOccured = true;
			errorMsg = "internal error";
		} finally {
			
			if(isError || exceptionOccured) {
				out.println(Util.createNotification(errorMsg, "danger"));
				req.setAttribute("actionType", 0);
				req.getRequestDispatcher("/jsp/employee/viewCustomer.jsp").include(req, res);
			}
			
			out.close();
		}
	}
}