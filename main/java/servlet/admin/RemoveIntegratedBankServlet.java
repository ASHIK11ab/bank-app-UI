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
import util.Util;

public class RemoveIntegratedBankServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		IntegratedBankDAO integratedBankDAO = Factory.getIntegratedBankDAO();
		Collection<IntegratedBank> integratedBanks = integratedBankDAO.getAll();
		req.setAttribute("values", integratedBanks);
		req.setAttribute("actionType", 0);
		req.getRequestDispatcher("/jsp/admin/removeIntegratedBank.jsp").include(req, res);
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		IntegratedBankDAO integratedBankDAO = Factory.getIntegratedBankDAO();
		PrintWriter out = res.getWriter();
		
		IntegratedBank bank = null;
		boolean isError = false, exceptionOccured = false;
		String msg = "";
		int integratedBankId, actionType;
		
		try {
			integratedBankId = Integer.parseInt(req.getParameter("bank-id"));
			actionType = Integer.parseInt(req.getParameter("actionType"));
			
			if(actionType != 0 && actionType != 1) {
				isError = true;
				msg = "Invalid action !!!";
			}
			
			if(!isError) {
				bank = integratedBankDAO.get(integratedBankId);
				
				if(bank == null) {
					isError = true;
					msg = "Integrated bank does not exist !!!";
				}
			}
			
			// ask for confirmation.
			if(!isError && actionType == 0) {
				req.setAttribute("actionType", 1);
				req.setAttribute("bank", bank);
				req.setAttribute("values", Factory.getIntegratedBankDAO().getAll());
				req.getRequestDispatcher("/jsp/admin/removeIntegratedBank.jsp").include(req, res);
			}
			
			if(!isError && actionType == 1) {
				integratedBankDAO.delete(integratedBankId);
				msg = "integrated bank deleted successfully";
				res.sendRedirect(String.format("/bank-app/admin/integrated-banks?msg=%s&status=success", msg));
			}
			
		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "Invalid input !!!";
		} catch(SQLException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = e.getMessage();
		} finally {
			
			if(isError || exceptionOccured) {
				out.println(Util.createNotification(msg, "danger"));
				doGet(req, res);
				out.close();
			}
		}
	}
}