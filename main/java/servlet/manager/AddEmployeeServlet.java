package servlet.manager;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.EmployeeDAO;
import model.user.Employee;
import util.Factory;


public class AddEmployeeServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.getRequestDispatcher("/jsp/manager/addEmployee.jsp").include(req, res);
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		EmployeeDAO employeeDAO = Factory.getEmployeeDAO();
		PrintWriter out = res.getWriter();
		
		boolean exceptionOccured = false;
		String name, email;
		long phone;
		int branchId;
		
		Employee employee = null;
		
		try {
			name = req.getParameter("employee-name");
			phone = Long.parseLong(req.getParameter("employee-phone"));
			email = req.getParameter("employee-email");
			branchId = (Integer) req.getSession(false).getAttribute("branch-id");
			
			employee = employeeDAO.create(branchId, name, email, phone);

			req.setAttribute("employee", employee);
			req.setAttribute("displayPassword", true);
			out.println("<div class='notification success'>" + "employee creation successfull" + "</div>");
			
		} catch(NumberFormatException e) {
			out.println("<div class='notification danger'>" + "invalid input" + "</div>");
			exceptionOccured = true;
		} catch(ClassCastException e) {
			out.println("<div class='notification danger'>" + "internal error" + "</div>");
			exceptionOccured = true;
		} catch(SQLException e) {
			out.println("<div class='notification danger'>" + e.getMessage() + "</div>");
			exceptionOccured = true;
		} finally {
            
			if(exceptionOccured)
				doGet(req, res);
			else
				req.getRequestDispatcher("/jsp/manager/employee.jsp").include(req, res);
			
			out.close();
		}
	}
}