package filter.employee;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.Role;


public class EmployeeAuthenticationFilter extends HttpFilter {
	
	public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
		Role role = null;
		
		try {
			role = (Role) req.getSession(false).getAttribute("role");
		} catch(ClassCastException e) {
			res.getWriter().println("<h1>Internal error</h1>");
			res.getWriter().close();
			return;
		}
		
		/* Ensure that user has employee privileges, redirect to login page
			if user does not have employee privileges */
		if(role != Role.EMPLOYEE) {
			
			switch(role) {
				case ADMIN: res.sendRedirect("/bank-app/login/admin"); break;
				case MANAGER: res.sendRedirect("/bank-app/login/manager"); break;
				case CUSTOMER: res.sendRedirect("/bank-app/login/customer"); break;
				default: break;
			}
			
			return;
		}
		
		chain.doFilter(req, res);
	}
}