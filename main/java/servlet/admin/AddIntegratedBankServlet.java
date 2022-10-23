package servlet.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.IntegratedBankDAO;
import util.Factory;


public class AddIntegratedBankServlet extends HttpServlet {
	private static final long serialVersionUID = 7436263962384418424L;
	
	private IntegratedBankDAO integratedBankDAO;
	
	public void init() {
		integratedBankDAO = Factory.getIntegratedBankDAO();
	}
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.getRequestDispatcher("/jsp/admin/addIntegratedBank.jsp").include(req, res);
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		
		String name, email, apiURL;
		long phone;
		
		try {
			
			name = req.getParameter("name");
			email = req.getParameter("email");
			phone = Long.parseLong(req.getParameter("phone"));
			apiURL = req.getParameter("api-url");
			
			integratedBankDAO.create(name, email, apiURL, phone);
			out.println("<div class='notification success'>" + "integrated bank created successfully" + "</div>");
		
		} catch(NumberFormatException e) {
			out.println("<div class='notification danger'>" + "invalid input" + "</div>");
		} catch(SQLException e) {
			out.println("<div class='notification danger'>" + e.getMessage() + "</div>");
		} finally {
			doGet(req, res);
			out.close();
		}
	}
}