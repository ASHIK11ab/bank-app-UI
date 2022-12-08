package filter.manager;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import constant.Role;
import dao.ManagerDAO;
import model.user.Employee;
import util.Factory;


public class ManagerAuthenticationFilter extends HttpFilter {
	public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		ManagerDAO managerDAO = Factory.getManagerDAO();
		
		Employee manager = null;
		boolean isError = false, exceptionOccured = false;
		long managerId;
		int branchId;
		Role role = null;
		
		try {
			role = (Role) session.getAttribute("role");
			
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
			} else {
				managerId = (Long)  session.getAttribute("id");
				branchId = (Integer) session.getAttribute("branch-id");
				manager = managerDAO.get(managerId, branchId);
				
				// Validate manager attributes in session and also validate whether
				// manager is logged in.
				if(manager == null || !manager.isLoggedIn()) {
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
				res.sendRedirect("/bank-app/login/manager");				
		}
	}
}