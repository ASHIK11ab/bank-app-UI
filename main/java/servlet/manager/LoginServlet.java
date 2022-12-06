package servlet.manager;

import java.io.IOException;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cache.AppCache;
import constant.Role;
import dao.ManagerDAO;
import model.Branch;
import model.user.Employee;
import util.Factory;
import util.Util;


public class LoginServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Collection<Branch> branches = Factory.getBranchDAO().getAll();
		req.setAttribute("branches", branches);
		req.setAttribute("forRole", Role.MANAGER);
		req.setAttribute("title", "Manager Login");
		req.setAttribute("actionURL", req.getRequestURI());
		req.getRequestDispatcher("/jsp/components/loginForm.jsp").include(req, res);
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		ManagerDAO managerDAO = Factory.getManagerDAO();
		
		Employee manager = null;
		long id = 0;
		int branchId;
		String password = "", msg = "";
		
		boolean isError = false, exceptionOccured = false;
				
		try {
			branchId = Integer.parseInt(req.getParameter("branch-id"));
			id = Long.parseLong(req.getParameter("id"));
			password = req.getParameter("password");
			
			manager = managerDAO.get(id, branchId); 
			
	        if((manager != null) && manager.getPassword().equals(password)) {
	        	HttpSession session = req.getSession();
	        	session.setAttribute("id", manager.getId());
	        	session.setAttribute("branch-id", manager.getBranchId());
	        	// 1 hour.
	        	session.setMaxInactiveInterval(60*60);
	        	session.setAttribute("role", Role.MANAGER);
	            res.sendRedirect("/bank-app/manager/dashboard");
	        } else {
	        	isError = true;
	        	msg = "Invalid id or password !!!";
	        }
		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "invalid input !!!";
		} catch(SQLException e) {
			exceptionOccured = true;
			msg = e.getMessage();
		} finally {
			if(isError || exceptionOccured) {
				out.println(Util.createNotification(msg, "danger"));
				doGet(req, res);
			}
		}
	}
}