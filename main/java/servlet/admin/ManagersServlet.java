package servlet.admin;

import java.io.IOException;

import java.sql.SQLException;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.ManagerDAO;
import model.user.Employee;
import util.Factory;


public class ManagersServlet extends HttpServlet {
	private ManagerDAO	managerDAO;
	
	public void init() {
		managerDAO = Factory.getManagerDAO();
	}
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException , IOException {
		LinkedList<Employee> managers;
		
		try {
			managers = managerDAO.getAll();
			req.setAttribute("managers", managers);
			req.getRequestDispatcher("/jsp/admin/managers.jsp").forward(req, res);
		} catch(SQLException e) {
			res.setStatus(500);
			res.getWriter().println(e.getMessage());
		}
	}
}