package servlet.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.IntegratedBankDAO;
import model.IntegratedBank;
import util.Factory;
import util.Util;

public class IntegratedBanksServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		IntegratedBankDAO integratedBankDAO = Factory.getIntegratedBankDAO();
		String queryMsg = "", status = "";
		
		queryMsg = req.getParameter("msg");
		status = req.getParameter("status");
		
		// Display notification if exists.
		if(queryMsg != null && status != null)
			out.println(Util.createNotification(queryMsg, status));
		
		Collection<IntegratedBank> integratedBanks;
		integratedBanks = integratedBankDAO.getAll();
		req.setAttribute("integratedBanks", integratedBanks);
		req.getRequestDispatcher("/jsp/admin/integratedBanks.jsp").include(req, res);
	}
}