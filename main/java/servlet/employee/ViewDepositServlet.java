package servlet.employee;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DepositAccountDAO;
import model.account.DepositAccountBean;
import util.Factory;
import util.Util;


public class ViewDepositServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.getRequestDispatcher("/jsp/employee/viewDeposit.jsp").include(req, res);
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		DepositAccountDAO accountDAO = Factory.getDepositAccountDAO();
		DepositAccountBean account = null;
		
		boolean exceptionOccured = false, isError = false;
		String errorMsg = "";
		
		long accountNo = -1;
		int branchId = -1;
		
		try {
			branchId = (Integer) req.getSession(false).getAttribute("branch-id");
			accountNo = Long.parseLong(req.getParameter("account-no"));
			
			if(Util.getNoOfDigits(accountNo) != 11 ) {
				out.println(Util.createNotification("A/C no must be a 11 digit number", "danger"));
				doGet(req, res);
				out.close();
			}
			
			account = accountDAO.get(accountNo);
			if(account != null && account.getBranchId() == branchId) {
				req.setAttribute("account", account);
			} else {
				isError = true;
				errorMsg = "No account found !!!";
			}
		} catch(ClassCastException e) {
			exceptionOccured = true;
			errorMsg = "internal error";
		} catch(NumberFormatException e) {
			exceptionOccured = true;
			errorMsg = "internal error";
		} catch(SQLException e) {
			exceptionOccured = true;
			errorMsg = e.getMessage();
		} finally {
			
			if(isError || exceptionOccured)
				out.println(Util.createNotification(errorMsg, "danger"));
			
			doGet(req, res);
			out.close();
		}
	}
}