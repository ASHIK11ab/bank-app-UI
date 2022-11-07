package servlet.admin;

import java.io.IOException;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.ManagerDAO;
import model.user.Employee;
import util.Factory;


public class ManagerServlet extends HttpServlet {
	private ManagerDAO	managerDAO;
	
	public void init() {
		managerDAO = Factory.getManagerDAO();
	}
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException , IOException {
		PrintWriter out = res.getWriter();
		long managerId;
		int branchId;
		Employee manager;
		
		try {
			managerId = Long.parseLong(req.getPathInfo().substring(1));
			branchId = Integer.parseInt(req.getParameter("branch-id"));
			
			manager = managerDAO.get(managerId, branchId);
			
			if(manager == null) {
				res.setStatus(404);
				out.println("<h1>Page not found</h1>");
			} else {
				req.setAttribute("manager", manager);
				req.getRequestDispatcher("/jsp/admin/manager.jsp").forward(req, res);				
			}
			
		} catch(NumberFormatException e) {
			res.setStatus(500);
			res.getWriter().println("<h1>Internal error</h1>");
		} catch(SQLException e) {
			res.setStatus(500);
			res.getWriter().println(e.getMessage());
		} finally {
			out.close();
		}
	}
}