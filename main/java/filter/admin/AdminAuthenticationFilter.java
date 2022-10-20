package filter.admin;

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


public class AdminAuthenticationFilter extends HttpFilter {
	private static final long serialVersionUID = 5677563436449958423L;

	public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {		
		HttpSession session = req.getSession(false);
		// Ensure that logged in user has admin privileges.
		if((Integer)session.getAttribute("role") != 1) {
			res.sendRedirect("/bank-app/login/admin");
			return;
		}
		
		chain.doFilter(req, res);
	}
}