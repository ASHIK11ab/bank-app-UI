package servlet.employee;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DepositAccountDAO;
import dao.RegularAccountDAO;
import dao.TransactionDAO;
import model.Transaction;
import model.account.Account;
import util.Factory;
import util.Util;


public class AccountTransactionHistoryServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setAttribute("actionType", 0);
		req.getRequestDispatcher("/jsp/employee/accountTransactionHistory.jsp").include(req, res);
	}


	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		
		LocalDate fromDate, toDate;
		long accountNo;
		boolean exceptionOccured = false, isError = false;
		String msg = "";
		int branchId;
		
		// accountCategory - category of account (0 - Regular, 1 - deposit)
		byte accountCategory = -1;
		
		Account account = null;
		LinkedList<Transaction> transactions;
		
		TransactionDAO transactionDAO = Factory.getTransactionDAO();
		RegularAccountDAO regularAccountDAO = Factory.getRegularAccountDAO();
		DepositAccountDAO depositAccountDAO = Factory.getDepositAccountDAO();
		
		try {
			branchId = (Integer) req.getSession(false).getAttribute("branch-id"); 
			accountNo = Long.parseLong(req.getParameter("account-no"));
			fromDate = LocalDate.parse(req.getParameter("from-date"));
			toDate = LocalDate.parse(req.getParameter("to-date"));
			
			if(Util.getNoOfDigits(accountNo) != 11 ) {
				isError = true;
				msg = "A/C no must be a 11 digit number";
			}
			
	        // Invalid date range.
	        if(fromDate.isAfter(toDate)) {
	            isError = true;
	            msg = "Invalid date range";
	        }
			
	        if(!isError) {
	        	account = regularAccountDAO.get(accountNo);
				
				if(account == null) {
					account = depositAccountDAO.get(accountNo);
					
					if(account != null)
						accountCategory = 1;
					
				} else {
					accountCategory = 0;
				}
				
				if(account != null && account.getBranchId() == branchId) {
					transactions = transactionDAO.getAll(accountNo, fromDate, toDate);
					req.setAttribute("actionType", 1);
					req.setAttribute("accountCategory", accountCategory);
					req.setAttribute("account", account);
					req.setAttribute("transactions", transactions);
					req.getRequestDispatcher("/jsp/employee/accountTransactionHistory.jsp").forward(req, res);
				} else {
					isError = true;
					msg = "Account not found in branch !!!";
				}
			}
		} catch(ClassCastException e) {
			exceptionOccured = true;
			msg = "internal error";
		} catch(NumberFormatException e) {
			exceptionOccured = true;
			msg = "internal error";
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
