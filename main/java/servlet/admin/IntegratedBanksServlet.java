package servlet.admin;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
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
		Collection<IntegratedBank> integratedBanks;
		
		integratedBanks = integratedBankDAO.getAll();
		req.setAttribute("integratedBanks", integratedBanks);
		req.getRequestDispatcher("/jsp/admin/integratedBanks.jsp").forward(req, res);
	}
}