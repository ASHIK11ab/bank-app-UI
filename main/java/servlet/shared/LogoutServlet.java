package servlet.shared;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import constant.Role;
import dao.AdminDAO;
import dao.CustomerDAO;
import dao.EmployeeDAO;
import dao.ManagerDAO;
import model.user.User;
import util.Factory;


public class LogoutServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		EmployeeDAO employeeDAO = Factory.getEmployeeDAO();
		ManagerDAO managerDAO = Factory.getManagerDAO();
		CustomerDAO customerDAO = Factory.getCustomerDAO();
		AdminDAO adminDAO = Factory.getAdminDAO();
		
		User user = null;
		Role role = null;
		long id;
		int branchId = -1;
		boolean exceptionOccured = false;
		
		try {
			role = (Role) req.getSession().getAttribute("role"); 
			id = (Long) req.getSession().getAttribute("id"); 
			
			if(role == Role.EMPLOYEE || role == Role.MANAGER)
				branchId = (Integer) req.getSession().getAttribute("branch-id");
			
			switch(role) {
				case ADMIN   : user = adminDAO.get(id);
							   break;
				case EMPLOYEE: user = employeeDAO.get(id, branchId);
							   break;
				case MANAGER : user = managerDAO.get(id, branchId);
							   break;
				case CUSTOMER: user = customerDAO.get(id);
							   break;
			}
			
			if(user != null)
				synchronized (user) {
					user.setLoggedInStatus(false);
					session.invalidate(); 
					res.sendRedirect("/bank-app/");
				}
		} catch(SQLException e) {
			exceptionOccured = true;
		} catch(ClassCastException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
		} finally {
			if(exceptionOccured)
				res.sendError(500);
		}
	}
}