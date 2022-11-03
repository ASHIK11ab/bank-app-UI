package servlet.shared;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cache.AppCache;
import constant.Constants;
import constant.DepositAccountType;
import constant.Role;
import dao.AccountDAO;
import dao.DepositAccountDAO;
import dao.TransactionDAO;
import model.account.DepositAccount;
import util.Factory;
import util.Util;


public class CloseDepositServlet extends HttpServlet {
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Connection conn = null;
		
		TransactionDAO transactionDAO = Factory.getTransactionDAO();
		DepositAccountDAO depositAccountDAO = Factory.getDepositAccountDAO();
		AccountDAO accountDAO = Factory.getAccountDAO();
		
		DepositAccount account = null;
		
		Role role = null;
		String msg = "", description = "", userType = "", redirectURI = "";
		float fromAccountBeforeBalance, toAccountBeforeBalance, totalAmount;
		boolean exceptionOccured = false, isError = false, prematureClosing = false, isAccountExists = false;
		long accountNo = -1, bankAccountNo = AppCache.getBank().getBankAccountNo(), customerId = -1;
		int branchId = -1, actionType = 0;
		
		try {
			role = (Role) req.getSession(false).getAttribute("role");
			userType = Role.getName(role);
			branchId = (Integer) req.getSession(false).getAttribute("branch-id");
			actionType = Integer.parseInt(req.getParameter("action-type"));
			accountNo = Long.parseLong(req.getParameter("account-no"));
			
			if(Util.getNoOfDigits(accountNo) != 11 ) {
				isError = true;
				msg = "A/C no must be a 11 digit number";
			}
			
			account = depositAccountDAO.get(accountNo);
			
        	// Access for account differs for customer and employee.
        	switch(role) {
	        	case EMPLOYEE: 
	        					if(account != null && account.getBranchId() == branchId)
	        						isAccountExists = true;
	        					break;
	        	case CUSTOMER: 
	        					customerId = (Long) req.getSession(false).getAttribute("id"); 
	        					if(account != null && account.getCustomerId() == customerId)
	        						isAccountExists = true;
	        					break;
	        	default: isAccountExists = false;
        	}
        	
        	if(!isAccountExists) {
        		isError = true;
        		msg = "Account not found !!!";
        	}
			
			/* Check whether account has reached maturity */
			if(!isError) {
				// Premature closing, request for confirmation.
				if(LocalDate.now().isBefore(account.getMaturityDate()))
					prematureClosing = true;
			}
			

			if(!isError && actionType == 0 && prematureClosing) {
				req.setAttribute("actionType", 1);
				req.setAttribute("accountNo", accountNo);
				req.getRequestDispatcher("/jsp/components/closeDeposit.jsp").forward(req, res);
			}
			
			/* Close deposit only when it has matured or after obtaining
			   confirmation. */
			if(!isError && ((actionType == 0 && !prematureClosing) ||  actionType == 1)) {
				
				synchronized (account) {
					
					totalAmount = account.getBalance();
					
					// Deduct premature closing charges from total amount.
					if(prematureClosing)
						totalAmount = totalAmount - Constants.PREMATURE_CLOSING_CHARGES;
				
					conn = Factory.getDataSource().getConnection();
					
					// Debit charges for premature closing
					if(prematureClosing) {
						// credit 500 to bank account.
						fromAccountBeforeBalance = accountDAO.updateBalance(conn, account.getAccountNo(), 0, Constants.PREMATURE_CLOSING_CHARGES);
						toAccountBeforeBalance = accountDAO.updateBalance(conn, bankAccountNo, 1, Constants.PREMATURE_CLOSING_CHARGES);
						
						description = "Premature closing charges on A/C: " + account.getAccountNo();
						transactionDAO.create(conn, 1, description, account.getAccountNo(), bankAccountNo, Constants.PREMATURE_CLOSING_CHARGES, true, true, fromAccountBeforeBalance, toAccountBeforeBalance);
					}
				
					// credit amount to payout account and close deposit.
					fromAccountBeforeBalance = accountDAO.updateBalance(conn, account.getAccountNo(), 0, totalAmount);
					toAccountBeforeBalance = accountDAO.updateBalance(conn, account.getPayoutAccountNo(), 1, totalAmount);
					description = DepositAccountType.getType(account.getTypeId()).toString() + " closing on A/C: " + account.getAccountNo();
					transactionDAO.create(conn, 1, description, account.getAccountNo(), account.getPayoutAccountNo(), totalAmount, true, true, fromAccountBeforeBalance, toAccountBeforeBalance);
					
					accountDAO.delete(conn, account.getAccountNo());
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
			
            try {
                if(conn != null)
                    conn.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
            
            
			if(isError || exceptionOccured) {
				redirectURI = String.format("/bank-app/%s/deposit/%d/view?msg=%s&status=danger", userType, accountNo, msg);
				res.sendRedirect(redirectURI);
			} 
				// Display success message when the deposit is closed successfully.
				else
					if((actionType == 0 && !prematureClosing) ||  actionType == 1) {
						msg = "Deposit closed and amount credited to payout account successfully";
						redirectURI = String.format("/bank-app/%s/deposit/?msg=%s&status=success", userType, msg);
						res.sendRedirect(redirectURI);					
					}
		}
	}
}