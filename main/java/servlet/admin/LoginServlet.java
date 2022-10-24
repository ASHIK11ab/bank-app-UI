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
import model.UserBean;
import util.Factory;


public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 6415721614440963644L;
	
	private AdminDAO adminDAO;
	
	public void init() {		
		adminDAO = Factory.getAdminDAO();
	}
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setAttribute("title", "Admin Login");
		req.setAttribute("actionURL", req.getRequestURI());
		req.getRequestDispatcher("/jsp/components/loginForm.jsp").forward(req, res);
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out;
		UserBean admin;
		
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
			admin = adminDAO.get(id);    
		} catch(SQLException e) {
			out = res.getWriter();
			res.setStatus(500);
			out.println("<h1>Internal error</h1>");
			out.close();
			return;
		}
		
        if((admin != null) && admin.getPassword().equals(password)) {
        	HttpSession session = req.getSession();
        	session.setAttribute("id", admin.getId());
        	// 2 days.
        	session.setMaxInactiveInterval(60*60*24*2);
        	session.setAttribute("role", Role.ADMIN);
            res.sendRedirect("/bank-app/admin/dashboard");
        } else {
			req.setAttribute("error", "Invalid id or password");
            doGet(req, res);
        }
	}
}