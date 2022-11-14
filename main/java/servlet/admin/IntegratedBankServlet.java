package servlet.admin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.IntegratedBankDAO;
import model.IntegratedBank;
import util.Factory;
import util.Util;

public class IntegratedBankServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		IntegratedBankDAO integratedBankDAO = Factory.getIntegratedBankDAO();
		IntegratedBank integratedBank = null;
		PrintWriter out = res.getWriter();
		
		boolean isError = false, exceptionOccured = false;
		String path = req.getPathInfo(), action = "", msg = "", redirectURI, queryMsg, status;
		String [] result;
		int bankId;
		
		queryMsg = req.getParameter("msg");
		status = req.getParameter("status");
		// Display notification if exists.
		if(queryMsg != null && status != null)
			out.println(Util.createNotification(queryMsg, status));
				
		// redirect to all branches page.
		if(path == null || path.equals("/")) {
			res.sendRedirect("/bank-app/admin/integrated-banks");
			return;
		}
		
		// dispatch to add page.
		if(path.equals("/add")) {
			req.setAttribute("type", 0);
			req.getRequestDispatcher("/jsp/admin/addEditIntegratedBank.jsp").include(req, res);
			return;
		}
		
		try {
			path = path.substring(1);
			result = path.split("/");
						
			bankId = Integer.parseInt(result[0]);
			action = result[1];
			
			integratedBank = integratedBankDAO.get(bankId);
			
			if(integratedBank == null) {
				isError = true;
				msg = "Integrated bank does not exist !!!";
			}
			
			if(!isError) {
				req.setAttribute("bank", integratedBank);
				switch(action) {
					case "view": 
								req.getRequestDispatcher("/jsp/admin/integratedBank.jsp").include(req, res);
								break;
					case "edit":
								req.setAttribute("type", 1);
								req.getRequestDispatcher("/jsp/admin/addEditIntegratedBank.jsp").forward(req, res);
								break;
					default: isError = true;
							 msg = "page not found !!!";
							 break;
				}
			}
			
		} catch(IndexOutOfBoundsException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "page not found !!!";
		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "Page not found !!!";
		} finally {			
			
			if(isError || exceptionOccured) {
				redirectURI = String.format("/bank-app/admin/integrated-banks?msg=%s&status=danger", msg);
				res.sendRedirect(redirectURI);
			}
			
			out.close();
		}
	}
}