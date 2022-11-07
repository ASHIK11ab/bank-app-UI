package servlet.employee;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.Role;
import dao.EmployeeDAO;
import model.user.Employee;
import util.Factory;
import util.Util;


public class PasswordResetServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setAttribute("userType", "employee");
		req.getRequestDispatcher("/jsp/pages/selfPasswordReset.jsp").include(req, res);
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		PrintWriter out = res.getWriter();
		EmployeeDAO employeeDAO = Factory.getEmployeeDAO();
		
		Employee employee = null;
		boolean exceptionOccured = false, isError = false;
		long employeeId = -1;
		int branchId;
		String oldPassword = "", newPassword = "", msg = "";
		
		try {
			branchId = (Integer) req.getSession(false).getAttribute("branch-id");
			employeeId = (Long) req.getSession(false).getAttribute("id");
			oldPassword = req.getParameter("old-password");
			newPassword = req.getParameter("new-password");
			
			
			if(newPassword.length() < 8 || newPassword.length() > 15) {
				isError = true;
				msg = "Password should be within 8 to 15 characters !!!";
			}
			
			if(!isError) {
				
				employee = employeeDAO.get(employeeId, branchId);
				
				if(employee == null) {
					isError = true;
					msg = "Invalid employee details !!!";
				}
			}
			
			if(!isError && !employee.getPassword().equals(oldPassword)) {
				isError = true;
				msg = "Incorrect old password";
			}
			
			if(!isError) {
				
				synchronized (employee) {					
					Factory.getUserDAO().updatePassword(employeeId, newPassword, Role.EMPLOYEE, (byte) 0);
				}
				
				out.println(Util.createNotification("Password reset successfull", "success"));
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