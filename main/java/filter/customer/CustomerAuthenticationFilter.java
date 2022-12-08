package filter.customer;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import constant.Role;
import dao.CustomerDAO;
import model.user.Customer;
import util.Factory;


public class CustomerAuthenticationFilter extends HttpFilter {

	public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpSession session = req.getSession(false);
		CustomerDAO customerDAO = Factory.getCustomerDAO();
		Customer customer = null;
		boolean isError = false, exceptionOccured = false;
		long customerId;
		Role role = null;
		
		try {
			role = (Role) session.getAttribute("role");
			
			/* Ensure that user has customer privileges, redirect to login page
			if user does not have customer privileges */
			if(role != Role.CUSTOMER) {
				switch(role) {
					case ADMIN: res.sendRedirect("/bank-app/login/admin"); break;
					case MANAGER: res.sendRedirect("/bank-app/login/manager"); break;
					case EMPLOYEE: res.sendRedirect("/bank-app/login/employee"); break;
					default: break;
				}
				
				return;
			} else {
				customerId = (Long)  session.getAttribute("id");
				
				// Validate customer id in session, if invalid redirect to login page and 
				// invalidate session.
				customer = customerDAO.get(customerId);
				
				// A removed customer has no access to the application, even when the
				// associated record exists.
				if(customer == null || customer.isRemoved() || !customer.isLoggedIn()) {
					isError = true;
					session.invalidate();
				} else {
					chain.doFilter(req, res);
				}
			}
		} catch(ClassCastException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
		} catch(SQLException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
		} finally {
			if(isError || exceptionOccured)
				res.sendRedirect("/bank-app/login/customer");
		}
	}
}
