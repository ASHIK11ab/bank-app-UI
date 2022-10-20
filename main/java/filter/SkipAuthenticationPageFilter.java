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


public class SkipAuthenticationPageFilter extends HttpFilter {
	private static final long serialVersionUID = -6425401947379949726L;

	public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		
		/* Skip login page and redirect to respective dashboard when
			user is aldready logged in. */
		if(session != null && session.getAttribute("role") != null) {
			switch((Integer) session.getAttribute("role")) {
				case 1: res.sendRedirect("/bank-app/admin/dashboard");
			}
			
			return;
		} else {
			chain.doFilter(req, res);
		}
	}
}