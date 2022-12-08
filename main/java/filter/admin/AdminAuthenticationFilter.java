package filter.admin;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import constant.Role;
import dao.AdminDAO;
import model.user.User;
import util.Factory;


public class AdminAuthenticationFilter extends HttpFilter {
	public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {		
		HttpSession session = req.getSession(false);
		
		boolean exceptionOccured = false, isError = false;
		User admin = null;
		long id;
		Role role = null;
		
		AdminDAO adminDAO = Factory.getAdminDAO();
		
		try {
			role = (Role) session.getAttribute("role");
		
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
			} else {
				id = (Long) session.getAttribute("id");
				admin = adminDAO.get(id);
				
				// Ensure that admin account exists and check whether admin is logged in.
				if(admin == null || !admin.isLoggedIn()) {
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
				res.sendRedirect("/bank-app/login/admin");
		}
	}
}