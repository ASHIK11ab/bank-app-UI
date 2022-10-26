package servlet.manager;

import java.io.IOException;

import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import constant.Role;
import dao.ManagerDAO;
import model.EmployeeBean;
import util.Factory;


public class LoginServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setAttribute("title", "Manager Login");
		req.setAttribute("actionURL", req.getRequestURI());
		req.getRequestDispatcher("/jsp/components/loginForm.jsp").forward(req, res);
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out;
		ManagerDAO managerDAO = Factory.getManagerDAO();
		
		EmployeeBean manager = null;
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
			manager = managerDAO.get(id);    
		} catch(SQLException e) {
			out = res.getWriter();
			res.setStatus(500);
			out.println("<h1>Internal error</h1>");
			out.close();
			return;
		}
		
        if((manager != null) && manager.getPassword().equals(password)) {
        	HttpSession session = req.getSession();
        	session.setAttribute("id", manager.getId());
        	session.setAttribute("branch-id", manager.getBranchId());
        	// 2 days.
        	session.setMaxInactiveInterval(60*60*24*2);
        	session.setAttribute("role", Role.MANAGER);
            res.sendRedirect("/bank-app/manager/dashboard");
        } else {
			req.setAttribute("error", "Invalid id or password");
            doGet(req, res);
        }
	}
}