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
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		
		LocalDate fromDate, toDate;
		long accountNo = -1;
		boolean exceptionOccured = false, isError = false;
		String msg = "";
		int branchId;
		
		Account account = null;
		LinkedList<Transaction> transactions;
		
		TransactionDAO transactionDAO = Factory.getTransactionDAO();
		RegularAccountDAO regularAccountDAO = Factory.getRegularAccountDAO();
		
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
				
				if(account != null && account.getBranchId() == branchId) {
					transactions = transactionDAO.getAll(accountNo, fromDate, toDate);
					req.setAttribute("actionType", 1);
					req.setAttribute("account", account);
					req.setAttribute("transactions", transactions);
					req.getRequestDispatcher("/jsp/components/accountTransactionHistory.jsp").forward(req, res);
				} else {
					isError = true;
					msg = "Account not found !!!";
				}
			}
		} catch(ClassCastException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "internal error";
		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "internal error";
		} catch(SQLException e) {
			exceptionOccured = true;
			msg = e.getMessage();
		} finally {
			
			if(isError || exceptionOccured) {
				res.sendRedirect("/bank-app/employee/account/" + accountNo + "/transaction-history?msg=" + msg + "&status=danger");
				out.close();
			}
			
		}
	}

}
