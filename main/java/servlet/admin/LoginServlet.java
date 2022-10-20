package servlet.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.InputMismatchException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import dao.AdminDAO;
import model.UserBean;


public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 6415721614440963644L;
	
	private AdminDAO adminDAO;
	
	public void init() {		
		adminDAO = new AdminDAO();
		System.out.println("Admin login servlet initialized");
	}
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.getRequestDispatcher("/jsp/admin/login.jsp").forward(req, res);
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
			req.getRequestDispatcher("/jsp/admin/login.jsp").forward(req, res);
		}
		
		out = res.getWriter();
		admin = adminDAO.get(id);
		
        if((admin != null) && admin.getPassword().equals(password)) {
        	HttpSession session = req.getSession();
        	session.setAttribute("id", admin.getId());
        	session.setAttribute("role", 1);
            res.sendRedirect("/bank-app/admin/dashboard");
        } else {
			req.setAttribute("error", "Invalid id or password");
            req.getRequestDispatcher("/jsp/admin/login.jsp").forward(req, res);
        }
        
        out.close();
	}
	
	
	public void destroy() {
		System.out.println("Admin login servlet destroyed");
	}
}