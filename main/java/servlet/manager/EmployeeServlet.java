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

public class EmployeeServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		EmployeeDAO employeeDAO = Factory.getEmployeeDAO();
		PrintWriter out = res.getWriter();
		Employee employee = null;
		long employeeId = -1;
		int branchId = -1;
		
		try {
			branchId = (Integer) req.getSession(false).getAttribute("branch-id");
			employeeId = Long.parseLong(req.getPathInfo().substring(1));
			employee = employeeDAO.get(employeeId, branchId);
			
			if(employee != null) {
				req.setAttribute("employee", employee);
				req.getRequestDispatcher("/jsp/manager/employee.jsp").forward(req, res);
			} else {
				res.setStatus(404);
				out.println("<h1>Page not found</h1>");
			}
			
		} catch(NumberFormatException e) {
			res.setStatus(500);
			out.println("<h1>Internal error</h1>");
		} catch(ClassCastException e) {
			res.setStatus(500);
			res.getWriter().println("<h1>Internal error</h1>");
		} catch(SQLException e) {
			res.setStatus(500);
			out.println(e.getMessage());			
		} finally {
			out.close();
		}
	}
}