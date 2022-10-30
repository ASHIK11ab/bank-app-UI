package servlet.employee;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import dao.DebitCardDAO;
import dao.RegularAccountDAO;
import model.account.RegularAccount;
import model.card.DebitCard;
import util.Factory;
import util.Util;


public class ViewCardsServlet extends HttpServlet {
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setAttribute("actionType", 0);
		req.getRequestDispatcher("/jsp/employee/viewCards.jsp").include(req, res);
	}
	
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		
		RegularAccountDAO accountDAO = Factory.getRegularAccountDAO();
		DebitCardDAO cardDAO = Factory.getDebitCardDAO();
		
		RegularAccount account = null;
		boolean isError = false, exceptionOccured = false;
		String msg = "";
		long accountNo;
		int branchId;
		LinkedList<DebitCard> cards = null;
		
		try {
			branchId = (Integer) req.getSession(false).getAttribute("branch-id"); 
			accountNo = Long.parseLong(req.getParameter("account-no"));
			
			if(Util.getNoOfDigits(accountNo) != 11 ) {
				isError = true;
				msg = "A/C no must be a 11 digit number";
			}
			
			if(!isError) {
				account = accountDAO.get(accountNo);
				
				if(account != null) {
					cards = cardDAO.getAll(accountNo);
					req.setAttribute("actionType", 1);
					req.setAttribute("account", account);
					req.setAttribute("cards", cards);
					req.getRequestDispatcher("/jsp/employee/viewCards.jsp").forward(req, res);
				} else {
					isError = true;
					msg = "Invalid account details !!!";
				}
			}
			
		} catch(ClassCastException e) {
			System.out.print(e.getMessage());
			exceptionOccured = true;
			msg = "internal error !!!";
		} catch(NumberFormatException e) {
			System.out.print(e.getMessage());
			exceptionOccured = true;
			msg = "internal error !!!";
		} catch(SQLException e) {
			exceptionOccured = true;
			msg = e.getMessage();
		} finally {
			
			if(isError || exceptionOccured) {
				out.println(Util.createNotification(msg, "danger"));
				doGet(req, res);
			}
			
			out.close();
		}
	}
}
