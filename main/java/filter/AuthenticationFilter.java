package filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthenticationFilter extends HttpFilter {
	private static final long serialVersionUID = 3840019640106384571L;

	public void doFilter(HttpServletRequest req,HttpServletResponse res, FilterChain chain) throws ServletException, IOException {		
		// Make sure that the session is a valid session.
		// Redirect to respective login page if not authenticated.
		HttpSession session = req.getSession(false);
		if(session == null || session.getAttribute("role") == null) {
			res.getWriter().println("<h1>In app homepage</h1>");
			return;
		}
		
		chain.doFilter(req, res);
	}
}