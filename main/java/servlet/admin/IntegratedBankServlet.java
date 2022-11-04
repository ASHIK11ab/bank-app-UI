package servlet.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.IntegratedBankDAO;
import model.IntegratedBank;
import util.Factory;

public class IntegratedBankServlet extends HttpServlet {
	private static final long serialVersionUID = -4834928724658535143L;
	private IntegratedBankDAO integratedBankDAO;
	
	public void init() {
		integratedBankDAO = Factory.getIntegratedBankDAO();
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		IntegratedBank integratedBank = null;
		int id;
		
		try {
			id = Integer.parseInt(req.getPathInfo().substring(1));
			integratedBank = integratedBankDAO.get(id);
			
			if(integratedBank == null) {
				res.setStatus(404);
				res.getWriter().println("<h1>Page not found</h1>");
			} else {
				req.setAttribute("integratedBank", integratedBank);
				req.getRequestDispatcher("/jsp/admin/integratedBank.jsp").forward(req, res);				
			}
			
		} catch(SQLException e) {
			res.setStatus(500);
			res.getWriter().println("<h1>Internal server error</h1>");
		}
	}
}