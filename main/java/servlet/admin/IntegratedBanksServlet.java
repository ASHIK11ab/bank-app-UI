package servlet.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.IntegratedBankDAO;
import model.IntegratedBank;
import util.Factory;

public class IntegratedBanksServlet extends HttpServlet {
	private static final long serialVersionUID = 4635541717523887476L;
	
	private IntegratedBankDAO integratedBankDAO;
	
	public void init() {
		integratedBankDAO = Factory.getIntegratedBankDAO();
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		LinkedList<IntegratedBank> integratedBanks;
		
		try {
			integratedBanks = integratedBankDAO.getAll();
			req.setAttribute("integratedBanks", integratedBanks);
			req.getRequestDispatcher("/jsp/admin/integratedBanks.jsp").forward(req, res);
		} catch(SQLException e) {
			res.setStatus(500);
			res.getWriter().println("<h1>Internal server error</h1>");
		}
	}
}