package servlet.employee;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import constant.Role;
import dao.EmployeeDAO;
import model.user.Employee;
import util.Factory;


public class LoginServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setAttribute("title", "Employee Login");
		req.setAttribute("actionURL", req.getRequestURI());
		req.getRequestDispatcher("/jsp/components/loginForm.jsp").forward(req, res);
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out;
		EmployeeDAO employeeDAO = Factory.getEmployeeDAO();
		
		Employee employee = null;
		long id = 0;
		String password = "";
				
		try {
			id = Long.parseLong(req.getParameter("id"));
			password = req.getParameter("password");
		} catch(NumberFormatException e) {
			req.setAttribute("error", "Invliad input for id");
			doGet(req, res);
			return;
		}
		
		try {
			employee = employeeDAO.get(id);    
		} catch(SQLException e) {
			out = res.getWriter();
			res.setStatus(500);
			out.println("<h1>Internal error</h1>");
			out.close();
			return;
		}
		
        if((employee != null) && employee.getPassword().equals(password)) {
        	HttpSession session = req.getSession();
        	session.setAttribute("id", employee.getId());
        	session.setAttribute("branch-id", employee.getBranchId());
        	// 2 days.
        	session.setMaxInactiveInterval(60*60*24*2);
        	session.setAttribute("role", Role.EMPLOYEE);
            res.sendRedirect("/bank-app/employee/dashboard");
        } else {
			req.setAttribute("error", "Invalid id or password");
            doGet(req, res);
        }
	}
}