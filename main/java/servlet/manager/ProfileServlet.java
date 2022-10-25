package servlet.manager;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.ManagerDAO;
import model.EmployeeBean;
import util.Factory;


public class ProfileServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		ManagerDAO managerDAO = Factory.getManagerDAO();
		PrintWriter out = res.getWriter();
		EmployeeBean manager = null;
		long managerId = -1;
		
		try {
			managerId = (Long) req.getSession(false).getAttribute("id");
			
			manager = managerDAO.get(managerId);
			req.setAttribute("manager", manager);
			req.getRequestDispatcher("/jsp/manager/profile.jsp").forward(req, res);
			
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