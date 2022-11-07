package servlet.manager;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.ManagerDAO;
import model.user.Employee;
import util.Factory;


public class ProfileServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		ManagerDAO managerDAO = Factory.getManagerDAO();
		PrintWriter out = res.getWriter();
		Employee manager = null;
		long managerId = -1;
		int branchId;
		
		try {
			branchId = (Integer) req.getSession(false).getAttribute("branch-id");
			managerId = (Long) req.getSession(false).getAttribute("id");
			
			manager = managerDAO.get(managerId, branchId);
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