package servlet.employee;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.DepositAccountType;
import dao.AccountDAO;
import dao.CustomerDAO;
import dao.DepositAccountDAO;
import dao.RegularAccountDAO;
import dao.TransactionDAO;
import model.user.Customer;
import model.Transaction;
import model.account.DepositAccount;
import model.account.RegularAccount;
import util.Factory;
import util.Util;


public class CreateDepositServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setAttribute("actionType", 0);
		req.getRequestDispatcher("/jsp/employee/createDeposit.jsp").include(req, res);
	}


	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		PrintWriter out = res.getWriter();
		
		AccountDAO accountDAO = Factory.getAccountDAO();
		CustomerDAO customerDAO = Factory.getCustomerDAO();
		DepositAccountDAO depositAccountDAO = Factory.getDepositAccountDAO();
		RegularAccountDAO regularAccountDAO = Factory.getRegularAccountDAO();
		TransactionDAO transactionDAO = Factory.getTransactionDAO();
		
		RegularAccount debitFromAccount = null, payoutAccount = null;
		DepositAccount account = null;
		
		boolean isError = false, exceptionOccured = false, isSufficientBalance = false;
		String msg = "", customerName, description = "";
		long payoutAccountNo, debitFromAccountNo, customerId;
		float balance, beforeBalance;
		int branchId, actionType = -1, depositType, amount = 0, tenureMonths;
		int recurringDate = -1;
		LocalDate nextRecurringDate = null;
		
		try {
			branchId = (Integer) req.getSession(false).getAttribute("branch-id");
			
			actionType = Integer.parseInt(req.getParameter("action-type"));
			depositType = Integer.parseInt(req.getParameter("deposit-type"));
			debitFromAccountNo = Long.parseLong(req.getParameter("debit-from-account-no"));
			payoutAccountNo = Long.parseLong(req.getParameter("payout-account-no"));
			tenureMonths = Integer.parseInt(req.getParameter("tenure-months"));
			
			if(DepositAccountType.getType(depositType) == null) {
				isError = true;
				msg = "Invalid deposit type";
			}
			
			if(!isError && depositType == DepositAccountType.RD.id) {
				amount = Integer.parseInt(req.getParameter("rd-amount"));
				recurringDate = Integer.parseInt(req.getParameter("recurring-date"));
				
				if(!isError && !((recurringDate >= 1) && (recurringDate <= 30))) {
					isError = true;
					msg = "Invalid recurring date";
				} else {
	            	nextRecurringDate = LocalDate.now().withDayOfMonth(recurringDate).plusMonths(1);
				}
				
			} else {
				amount = Integer.parseInt(req.getParameter("fd-amount"));
			}
			
			// Validate account no.
			if(!isError && (Util.getNoOfDigits(debitFromAccountNo) != 11 || Util.getNoOfDigits(payoutAccountNo) != 11)) {
				isError = true;
				msg = "A/C no must be a 11 digit number";
			}
			
			if(!isError && amount < 1000) {
				isError = true;
				msg = "Mimimum amount is 1000 !!!";
			}
			
			if(!isError && tenureMonths < 2) {
				isError = true;
				msg = "Minimum no of months is 2 !!!";
			}
			
			
			// Get deposit account details and ask for confirmation
			if(!isError && actionType == 0) {
				debitFromAccount = regularAccountDAO.get(debitFromAccountNo);
				
				if(debitFromAccount != null) {
					if(debitFromAccountNo == payoutAccountNo)
						payoutAccount = debitFromAccount;
					else {
						payoutAccount = regularAccountDAO.get(payoutAccountNo);
						if(payoutAccount == null) {
							isError = true;
							msg = "Invalid payout account details !!!";
						}
					}
				} else {
					isError = true;
					msg = "Invalid debit from account details !!!";
				}				
		
				if(!isError) {
					req.setAttribute("actionType", 1);
					req.setAttribute("depositType", depositType);
					req.setAttribute("debitFromAccount", debitFromAccount);
					req.setAttribute("payoutAccount", payoutAccount);
					req.setAttribute("tenureMonths", tenureMonths);
					req.setAttribute("amount", amount);
					req.setAttribute("recurringDate", recurringDate);
					req.getRequestDispatcher("/jsp/employee/createDeposit.jsp").forward(req, res);
				}
			}
			
			
			// Create Deposit account
			if(!isError && actionType == 1) {
				debitFromAccount = regularAccountDAO.get(debitFromAccountNo);
				customerId = debitFromAccount.getCustomerId();
				customerName = debitFromAccount.getCustomerName();
				
				conn = Factory.getDataSource().getConnection();
				stmt = conn.prepareStatement("SELECT balance FROM account WHERE account_no = ?");
	            
				stmt.setLong(1, debitFromAccountNo);
	            rs = stmt.executeQuery();

	            if(rs.next()) {
	                balance = rs.getFloat("balance");
	                if(balance >= amount)
	                    isSufficientBalance = true;
	            }
	            
	            if(isSufficientBalance) {
	            	// Deduct amount from debit from account.
	            	beforeBalance = accountDAO.updateBalance(conn, debitFromAccountNo, 0, amount);
	            	
	            	// create deposit account
	            	account = depositAccountDAO.create(conn, customerId, customerName, branchId, depositType, null, amount, tenureMonths, payoutAccountNo, debitFromAccountNo, nextRecurringDate);
	            	
	            	description = DepositAccountType.getType(depositType).toString() + " withdrawal for A/C: " + account.getAccountNo();
	            	
	            	transactionDAO.create(conn, 1, description, debitFromAccountNo, account.getAccountNo(), amount, true, true, beforeBalance, 0);	            	
	            	
	            	req.setAttribute("account", account);
	            	req.getRequestDispatcher("/jsp/employee/depositCreationSuccess.jsp").forward(req, res);
	            } else {
	            	isError = true;
	            	msg = "Insufficient balance in debit from account !!!";
	            }
			}
		} catch(ClassCastException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "internal error !!!";
		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "invalid input !!!";
		} catch(SQLException e) {
			exceptionOccured = true;
			msg = e.getMessage();
		} finally {
			
            try {
                if(conn != null)
                    conn.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
            
			if(isError || exceptionOccured) {
				out.println(Util.createNotification(msg, "danger"));
				doGet(req, res);
			}
			
			out.close();
		}
	}
}
