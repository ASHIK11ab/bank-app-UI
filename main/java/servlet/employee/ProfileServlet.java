package servlet.employee;

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


public class ProfileServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		EmployeeDAO employeeDAO = Factory.getEmployeeDAO();
		PrintWriter out = res.getWriter();
		Employee employee = null;
		long employeeId = -1;
		int branchId;
		
		try {
			branchId = (Integer) req.getSession(false).getAttribute("branch-id");
			employeeId = (Long) req.getSession(false).getAttribute("id");
			
			employee = employeeDAO.get(employeeId, branchId);
			req.setAttribute("employee", employee);
			req.getRequestDispatcher("/jsp/employee/profile.jsp").forward(req, res);
			
		} catch(ClassCastException e) {
			res.setStatus(500);
			out.println("<h1>Internal error</h1>");
		} catch(SQLException e) {
			res.setStatus(500);
			out.println("<h1>Internal error</h1>");
		} finally {
			out.close();
		}
	}
}
