package filter.manager;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.Role;


public class ManagerAuthenticationFilter extends HttpFilter {
	private static final long serialVersionUID = -8531833699294525517L;

	public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
		Role role = (Role) req.getSession(false).getAttribute("role");
		
		/* Ensure that user has manager privileges, redirect to login page
			if user does not have manager privileges */
		if(role != Role.MANAGER) {
			
			switch(role) {
				case ADMIN: res.sendRedirect("/bank-app/login/admin"); break;
				case EMPLOYEE: res.sendRedirect("/bank-app/login/employee"); break;
				case CUSTOMER: res.sendRedirect("/bank-app/login/customer"); break;
				default: break;
			}
			
			return;
		}
		
		chain.doFilter(req, res);
	}
}