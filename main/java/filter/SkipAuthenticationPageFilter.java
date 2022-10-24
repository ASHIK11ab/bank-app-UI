package filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import constant.Role;


public class SkipAuthenticationPageFilter extends HttpFilter {
	private static final long serialVersionUID = -6425401947379949726L;

	public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		
		/* Skip login page and redirect to respective dashboard when
			user is aldready authenticated */
		if(session != null && session.getAttribute("role") != null) {
			
			switch((Role) session.getAttribute("role")) {
				case ADMIN: res.sendRedirect("/bank-app/admin/dashboard"); break;
				case MANAGER: res.sendRedirect("/bank-app/manager/dashboard"); break;
				case EMPLOYEE: res.sendRedirect("/bank-app/employee/dashboard"); break;
				case CUSTOMER: res.sendRedirect("/bank-app/customer/dashboard"); break;
				default: break;
			}
			
			return;
		} else {
			chain.doFilter(req, res);
		}
	}
}