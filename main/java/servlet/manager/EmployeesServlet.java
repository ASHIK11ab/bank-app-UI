package servlet.manager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.EmployeeDAO;
import model.user.Employee;
import util.Factory;

public class EmployeesServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {		
		EmployeeDAO employeeDAO = Factory.getEmployeeDAO();
		Collection<Employee> employees;
		
		int branchId;
		boolean exceptionOccured = false;
		
		try {
			branchId = (Integer) req.getSession(false).getAttribute("branch-id"); 
			employees = employeeDAO.getAll(branchId);
			req.setAttribute("employees", employees);
			req.getRequestDispatcher("/jsp/manager/employees.jsp").forward(req, res);
		} catch(ClassCastException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
		} catch(SQLException e) {
			exceptionOccured = true;
		} finally {
			if(exceptionOccured)
				res.sendError(500);
		}
	}
}