package filter.admin;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import constant.Role;


public class AdminAuthenticationFilter extends HttpFilter {
	private static final long serialVersionUID = 5677563436449958423L;

	public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {		
		HttpSession session = req.getSession(false);
		Role role = (Role) session.getAttribute("role");
		
		/* Redirect user to respective login page when the user
		 does not have admin Privileges */
		if(role != Role.ADMIN) {
			
			switch(role) {
				case MANAGER: res.sendRedirect("/bank-app/login/manager"); break;
				case EMPLOYEE: res.sendRedirect("/bank-app/login/employee"); break;
				case CUSTOMER: res.sendRedirect("/bank-app/login/customer"); break;
				default: break;
			}
			
			return;
		}
		
		chain.doFilter(req, res);
	}
}