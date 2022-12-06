package servlet.manager;

import java.io.IOException;

import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.Role;
import dao.EmployeeDAO;
import model.user.Employee;
import util.Factory;
import util.Util;


public class EmployeeServlet extends HttpServlet {
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		
		Employee employee = null;
		EmployeeDAO employeeDAO = Factory.getEmployeeDAO();
		
		String path = req.getPathInfo(), msg = "", action = "";
		String queryMsg = "", status = "";
		String[] result;
		boolean isError = false, exceptionOccured = false;
		int branchId;
		long employeeId;
		
		queryMsg = req.getParameter("msg");
		status = req.getParameter("status");
		
		if(queryMsg != null && status != null)
			out.println(Util.createNotification(queryMsg, status));
		
		try {
			path = path.substring(1);
			result = path.split("/");
			
			branchId = (Integer) req.getSession().getAttribute("branch-id");
			
			employeeId = Long.parseLong(result[0]);
			action = result[1];
			
			employee = employeeDAO.get(employeeId, branchId);

			if(employee == null) {
				isError = true;
				msg = "Employee does not exist !!!";
			} else {
				req.setAttribute("employee", employee);
				
				switch(action) {
					case "view": req.getRequestDispatcher("/jsp/manager/employee.jsp").include(req, res); break;
					
					case "password-reset": 
											req.setAttribute("id", employeeId);
											req.setAttribute("name", employee.getName());
											req.setAttribute("forRole", Role.EMPLOYEE);
											req.setAttribute("branchId", branchId);
											req.setAttribute("redirectURI", String.format("/bank-app/manager/employee/%d/view", employeeId));
											req.getRequestDispatcher("/jsp/components/resetPassword.jsp").include(req, res); break;
					default:
							isError = true;
							msg = "Page not found !!!";
				}
			}
			
		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "Invalid input !!!";
		} catch(IndexOutOfBoundsException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "Page not found !!!";
		} catch(SQLException e) {
			exceptionOccured = true;
			msg = e.getMessage();
		} finally {
			
			if(isError || exceptionOccured)
				res.sendRedirect(String.format("/bank-app/manager/dashboard?msg=%s&status=danger", msg));
			out.close();
		}
	}
}