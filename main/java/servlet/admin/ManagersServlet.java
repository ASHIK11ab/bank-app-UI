package servlet.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.ManagerDAO;
import model.user.Employee;
import util.Factory;
import util.Util;


public class ManagersServlet extends HttpServlet {
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException , IOException {
		ManagerDAO managerDAO = Factory.getManagerDAO();
		PrintWriter out = res.getWriter();
		
		Collection<Employee> managers;
		String queryMsg, status;
		
		queryMsg = req.getParameter("msg");
		status = req.getParameter("status");
		
		if(queryMsg != null && status != null)
			out.println(Util.createNotification(queryMsg, status));
		
		managers = managerDAO.getAll();
		req.setAttribute("managers", managers);
		req.getRequestDispatcher("/jsp/admin/managers.jsp").include(req, res);
		
		out.close();
	}
}