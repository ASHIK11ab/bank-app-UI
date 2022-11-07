package servlet.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.IntegratedBankDAO;
import model.IntegratedBank;
import util.Factory;

public class RemoveIntegratedBankServlet extends HttpServlet {
	private static final long serialVersionUID = -4181185294048918446L;
	private IntegratedBankDAO integratedBankDAO;
	
	public void init() {
		integratedBankDAO = Factory.getIntegratedBankDAO();
	}
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Collection<IntegratedBank> integratedBanks = integratedBankDAO.getAll();
		req.setAttribute("values", integratedBanks);
		req.getRequestDispatcher("/jsp/admin/removeIntegratedBank.jsp").include(req, res);
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		int integratedBankId;		
		
		try {
			integratedBankId = Integer.parseInt(req.getParameter("bank-id"));
			integratedBankDAO.delete(integratedBankId);
			out.println("<div class='notification success'>" + "integrated bank deleted successfully" + "</div>");
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