package servlet.manager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.EmployeeDAO;
import model.EmployeeBean;
import util.Factory;

public class EmployeesServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		EmployeeDAO employeeDAO = Factory.getEmployeeDAO();
		LinkedList<EmployeeBean> employees;
		int branchId;
		
		try {
			branchId = (Integer) req.getSession(false).getAttribute("branch-id"); 
			employees = employeeDAO.getAll(branchId);
			req.setAttribute("employees", employees);
			req.getRequestDispatcher("/jsp/manager/employees.jsp").forward(req, res);
		}  catch(NumberFormatException e) {
			res.setStatus(500);
			res.getWriter().println("<h1>Internal error</h1>");
		} catch(ClassCastException e) {
			res.setStatus(500);
			res.getWriter().println("<h1>Internal error</h1>");
		}  catch(SQLException e) {
			res.setStatus(500);
			res.getWriter().println(e.getMessage());
		}
	}
}