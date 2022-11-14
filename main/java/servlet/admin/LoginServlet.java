package servlet.admin;

import java.io.IOException;

import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import constant.Role;
import dao.AdminDAO;
import model.user.User;
import util.Factory;
import util.Util;


public class LoginServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setAttribute("title", "Admin Login");
		req.setAttribute("actionURL", req.getRequestURI());
		req.getRequestDispatcher("/jsp/components/loginForm.jsp").include(req, res);
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		AdminDAO adminDAO = Factory.getAdminDAO();
		PrintWriter out = res.getWriter();
		User admin;
		
		boolean isError = false, exceptionOccured = false;
		long id = 0;
		String password = "", msg = "";
				
		try {
			id = Long.parseLong(req.getParameter("id"));
			password = req.getParameter("password");
			
			admin = adminDAO.get(id);    
			
	        if((admin != null) && admin.getPassword().equals(password)) {
	        	HttpSession session = req.getSession();
	        	session.setAttribute("id", admin.getId());
	        	// 2 days.
	        	session.setMaxInactiveInterval(60*60*24*2);
	        	session.setAttribute("role", Role.ADMIN);
	            res.sendRedirect("/bank-app/admin/dashboard");
	        } else {
				isError = true;
				msg = "Invalid id or password";
	        }

		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			isError = true;
			msg = "Invalid input !!!";
		} catch(SQLException e) {
			System.out.println(e.getMessage());
			isError = true;
			msg = e.getMessage();
		} finally {
			if(isError || exceptionOccured) {
				out.println(Util.createNotification(msg, "danger"));
				doGet(req, res);
			}
		}
	}
}