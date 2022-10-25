package servlet.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.AdminDAO;
import model.UserBean;
import util.Factory;

public class ProfileServlet extends HttpServlet {
	private static final long serialVersionUID = 8455697239257669372L;
	private AdminDAO adminDAO;
	
	public void init() {		
		adminDAO = Factory.getAdminDAO();
	}
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		
		try {
			long adminId = (Long) req.getSession(false).getAttribute("id");
			UserBean admin = adminDAO.get(adminId);
			req.setAttribute("user", admin);
			req.getRequestDispatcher("/jsp/admin/profile.jsp").forward(req, res);
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