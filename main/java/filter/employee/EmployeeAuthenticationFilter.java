package filter.employee;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import constant.Role;
import dao.EmployeeDAO;
import util.Factory;


public class EmployeeAuthenticationFilter extends HttpFilter {
	
	public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		EmployeeDAO employeeDAO = Factory.getEmployeeDAO();
		boolean isError = false, exceptionOccured = false;
		long employeeId;
		int branchId;
		Role role = null;
		
		try {
			role = (Role) session.getAttribute("role");
			
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
			} else {
				employeeId = (Long)  session.getAttribute("id");
				branchId = (Integer) session.getAttribute("branch-id");
				
				// Invalid employee, branch id's in session, redirect to login page and 
				// invalidate session.
				if(employeeDAO.get(employeeId, branchId) == null) {
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
				res.sendRedirect("/bank-app/login/employee");
		}
	}
}