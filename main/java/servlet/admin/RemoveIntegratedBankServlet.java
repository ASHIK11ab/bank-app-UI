package servlet.admin;

import java.io.IOException;
import java.io.PrintWriter;
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

public class RemoveIntegratedBankServlet extends HttpServlet {
	private static final long serialVersionUID = -4181185294048918446L;
	private IntegratedBankDAO integratedBankDAO;
	
	public void init() {
		integratedBankDAO = Factory.getIntegratedBankDAO();
	}
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		LinkedList<IntegratedBank> integratedBanks;
		
		try {
			integratedBanks = integratedBankDAO.getAll();
			req.setAttribute("values", integratedBanks);
			req.getRequestDispatcher("/jsp/admin/removeIntegratedBank.jsp").include(req, res);
		} catch(SQLException e) {
			res.setStatus(500);
			res.getWriter().println("<h1>Internal server error</h1>");
		}
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		int integratedBankId;
		boolean status = false;
		
		try {
			integratedBankId = Integer.parseInt(req.getParameter("bank-id"));
			status = integratedBankDAO.delete(integratedBankId);
			
			if(status)
				out.println("<div class='notification success'>" + "integrated bank deleted successfully" + "</div>");
			else
				out.println("<div class='notification danger'>" + "invalid bank selected" + "</div>");
		
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