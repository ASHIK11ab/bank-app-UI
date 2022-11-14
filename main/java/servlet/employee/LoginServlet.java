package servlet.employee;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import constant.Role;
import dao.BranchDAO;
import dao.EmployeeDAO;
import model.Branch;
import model.user.Employee;
import util.Factory;
import util.Util;


public class LoginServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Collection<Branch> branches = Factory.getBranchDAO().getAll(); 
		req.setAttribute("forRole", Role.EMPLOYEE);
		req.setAttribute("branches", branches);
		req.setAttribute("title", "Employee Login");
		req.setAttribute("actionURL", req.getRequestURI());
		req.getRequestDispatcher("/jsp/components/loginForm.jsp").include(req, res);
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		BranchDAO branchDAO = Factory.getBranchDAO();
		EmployeeDAO employeeDAO = Factory.getEmployeeDAO();
		
		String msg = "";
		boolean isError = false, exceptionOccured = false;
		Employee employee = null;
		long id = 0;
		int branchId;
		String password = "";
				
		try {
			branchId = Integer.parseInt(req.getParameter("branch-id"));
			id = Long.parseLong(req.getParameter("id"));
			password = req.getParameter("password");
			
			if(branchDAO.get(branchId) == null) {
				isError = true;
				msg = "Invalid branch details !!!";
			}
			
			if(!isError) {
				employee = employeeDAO.get(id, branchId);    
				
		        if((employee != null) && employee.getPassword().equals(password)) {
		        	HttpSession session = req.getSession();
		        	session.setAttribute("id", employee.getId());
		        	session.setAttribute("branch-id", employee.getBranchId());
		        	// 2 days.
		        	session.setMaxInactiveInterval(60*60*24*2);
		        	session.setAttribute("role", Role.EMPLOYEE);
		            res.sendRedirect("/bank-app/employee/dashboard");
		        } else {
					isError = true;
					msg = "Invalid id or password !!!";
		        }
			}
			
		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "Invalid input !!!";
		} catch(SQLException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = e.getMessage();
		} finally {
			if(isError || exceptionOccured) {
				out.println(Util.createNotification(msg, "danger"));
				doGet(req, res);
			}
			
			out.close();
		}
	}
}