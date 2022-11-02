package servlet.employee;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.CustomerDAO;
import dao.RegularAccountDAO;
import model.account.RegularAccount;
import model.user.Customer;
import util.Factory;
import util.Util;


public class RemoveCustomerServlet extends HttpServlet {
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		String msg = "";
		long accountNo = -1;
		int branchId;
		boolean exceptionOccured = false, isError = false;
		
		RegularAccountDAO regularAccountDAO = Factory.getRegularAccountDAO();
		CustomerDAO customerDAO = Factory.getCustomerDAO();
		
		Customer customer = null;
		RegularAccount account = null;
		
		try {
			branchId = (Integer) req.getSession(false).getAttribute("branch-id");
			accountNo = Long.parseLong(req.getParameter("account-no"));
			
			if(Util.getNoOfDigits(accountNo) != 11 ) {
				isError = true;
				msg = "A/C no must be a 11 digit number";
			}
			
			account = regularAccountDAO.get(accountNo);
			
			if(account == null || account.getBranchId() != branchId) {
				isError = true;
				msg = "Account does not exist !!!";
			}
			
			/* Remove customer (executed only when a customer has only one account in bank
				and is about to be closed */
			if(!isError) {
				customer = customerDAO.get(account.getCustomerId());
				
				synchronized (customer) {
					synchronized(account) {						
						customerDAO.delete(customer.getId());
					}
				}
			}
		} catch(ClassCastException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "internal error !!!";
		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "internal error !!!";
		} catch(SQLException e) {
			exceptionOccured = true;
			msg = e.getMessage();
		} finally {
			if(isError || exceptionOccured)
				res.sendRedirect("/bank-app/employee/account/" + accountNo + "/close?msg=" + msg + "&status=danger");
			else
				res.sendRedirect("/bank-app/employee/account?msg=account closed and customer removed successfully&status=success");
			
			out.close();
		}
	}

}
