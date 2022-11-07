package servlet.employee;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.Role;
import dao.CustomerDAO;
import dao.UserDAO;
import model.user.Customer;
import util.Factory;

public class CustomerPasswordResetServlet extends HttpServlet {
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		Customer customer = null;
		boolean isError = false, exceptionOccured = false;
		long customerId = -1;
		String password, msg = "";
		
		UserDAO userDAO = Factory.getUserDAO();
		CustomerDAO customerDAO = Factory.getCustomerDAO();
		
		try {
			customerId = Long.parseLong(req.getParameter("customer-id"));
			password = req.getParameter("password");
			
			if(!(password.length() >= 8 && password.length() <= 15)) {
				isError = true;
				msg = "Password should be between 8 and 15 characters";
			}
			
			customer = customerDAO.get(customerId);
			
			if(!isError && (customer == null || customer.isRemoved())) {
				isError = true;
				msg = "Invalid customer id !!!";
			}
			
			if(!isError && customer.getPassword().equals(password)) {
				isError = true;
				msg = "New password is same as old password !!!";
			}
			
			if(!isError) {
				synchronized (customer) {					
					userDAO.updatePassword(customerId, password, Role.CUSTOMER, (byte) 0, -1);
				}
			}
			
		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			isError = true;
			msg = "Invalid input !!!";
		} catch(SQLException e) {
			isError = true;
			msg = e.getMessage();
		} finally {
			if(isError || exceptionOccured)
				res.sendRedirect("/bank-app/employee/customer/" + customerId + "/password-reset?msg=" + msg + "&status=danger");
			else
				res.sendRedirect("/bank-app/employee/customer/" + customerId + "/view?msg=customer password reset successfull" + "&status=success");
		}
	}
}