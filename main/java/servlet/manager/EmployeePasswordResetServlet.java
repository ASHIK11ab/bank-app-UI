package servlet.manager;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.lang.model.element.Element;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.Role;
import dao.EmployeeDAO;
import dao.UserDAO;
import model.user.Employee;
import util.Factory;
import util.Util;


public class EmployeePasswordResetServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		EmployeeDAO employeeDAO = Factory.getEmployeeDAO();
		LinkedList<Employee> employees;
		int branchId = -1;
		
		try {
			branchId = (Integer) req.getSession(false).getAttribute("branch-id");
			employees = employeeDAO.getAll(branchId);
			req.setAttribute("values", employees);
			req.getRequestDispatcher("/jsp/manager/employeePasswordReset.jsp").include(req, res);
		} catch(ClassCastException e) {
			res.setStatus(500);
			res.getWriter().println("<h1>Internal error</h1>");
		} catch(SQLException e) {
			res.setStatus(500);
			res.getWriter().println(e.getMessage());
		}
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		EmployeeDAO employeeDAO = Factory.getEmployeeDAO();
		UserDAO userDAO = Factory.getUserDAO();
		
		Employee employee = null;
		PrintWriter out = res.getWriter();
		
		boolean isError = false, exceptionOccured = false;
		long employeeId = -1;
		String newPassword = "", msg = "";
		int branchId = -1;
		
		try {
			branchId = (Integer) req.getSession(false).getAttribute("branch-id");
			employeeId = Long.parseLong(req.getParameter("id"));
			newPassword = req.getParameter("password");
			
			if(newPassword.length() < 8 || newPassword.length() > 15) {
				isError = true;
				msg = "Password should be within 8 to 15 characters !!!";
			}
			
			if(!isError) {
				employee = employeeDAO.get(employeeId, branchId);
				
				if(employee != null) {
					synchronized(employee) {
						userDAO.updatePassword(employeeId, newPassword, Role.EMPLOYEE, (byte) 0, branchId);
						out.println(Util.createNotification("password reset successfull", "success"));
					}
				} else {
					isError = true;
					msg = "Employee does not exist !!!";
				}
			}
			
		} catch(ClassCastException e) {
			System.out.print(e.getMessage());
			exceptionOccured = true;
			msg = "internal error !!!";
		} catch(NumberFormatException e) {
			System.out.print(e.getMessage());
			exceptionOccured = true;
			msg = "internal error !!!";
		} catch(SQLException e) {
			exceptionOccured = true;
			msg = e.getMessage();
		} finally {
			
			if(isError || exceptionOccured)
				out.println(Util.createNotification(msg, "danger"));
			
			doGet(req, res);
			out.close();
		}
	}
}