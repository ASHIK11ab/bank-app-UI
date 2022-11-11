package servlet.shared;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.Role;
import dao.AdminDAO;
import dao.CustomerDAO;
import dao.EmployeeDAO;
import dao.ManagerDAO;
import model.user.Customer;
import model.user.User;
import util.Factory;
import util.Util;

public class ProfileServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		EmployeeDAO employeeDAO = Factory.getEmployeeDAO();
		ManagerDAO managerDAO = Factory.getManagerDAO();
		CustomerDAO customerDAO = Factory.getCustomerDAO();
		AdminDAO adminDAO = Factory.getAdminDAO();
		
		Customer customer = null;
		User user = null;
		
		PrintWriter out = res.getWriter();
		
		Role role = null;
		String path = req.getPathInfo(), action = "", msg = "", queryMsg, status, roleName = "";
		boolean isError = false, exceptionOccured = false;
		long id;
		int branchId = -1;
		
		queryMsg = req.getParameter("msg");
		status = req.getParameter("status");
		
		// Display notification if exists.
		if(queryMsg != null && status != null)
			out.println(Util.createNotification(queryMsg, status));
		
		try {
			role = (Role) req.getSession(false).getAttribute("role"); 
			roleName = Role.getName(role);
			
			id = (Long) req.getSession(false).getAttribute("id");  
			action = path.substring(1);
			
			if(role == Role.EMPLOYEE || role == Role.MANAGER)
				branchId = (Integer) req.getSession(false).getAttribute("branch-id");
			
			switch(role) {
				case ADMIN   : user = adminDAO.get(id); break;
				case EMPLOYEE: user = employeeDAO.get(id, branchId);
							   break;
				case MANAGER : user = managerDAO.get(id, branchId);
							   break;
				case CUSTOMER: customer = customerDAO.get(id);
							   user = (User) customer; 
							   break;
			}
									
			if(!isError) {
				
				if(role == Role.CUSTOMER)
					req.setAttribute("user", customer);
				else
					req.setAttribute("user", user);
				
				switch(action) {
					case "view": 
								req.getRequestDispatcher("/jsp/components/profile.jsp").include(req, res);
								break;
					case "password-reset": 
											req.setAttribute("id", id);
											req.setAttribute("name", user.getName());
											req.setAttribute("forRole", role);
											req.setAttribute("redirectURI", String.format("/bank-app/%s/profile/view", roleName));
											req.getRequestDispatcher("/jsp/components/resetPassword.jsp").include(req, res);
											break;
					default:
							isError = true;
							msg = "Page not found !!!";
				}
			}
			
		} catch(SQLException e) {
			exceptionOccured = true;
			msg = e.getMessage();
		} catch(ClassCastException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "Page not found !!!";
		} finally {			
			
			if(isError || exceptionOccured) {
				res.sendRedirect(String.format("/bank-app/login/%s", roleName));
			}
			
			out.close();
		}
	}
}