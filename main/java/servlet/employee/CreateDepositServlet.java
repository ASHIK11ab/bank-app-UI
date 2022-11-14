package servlet.employee;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.DepositAccountType;
import dao.AccountDAO;
import dao.DepositAccountDAO;
import dao.RegularAccountDAO;
import dao.TransactionDAO;
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
		DepositAccountDAO depositAccountDAO = Factory.getDepositAccountDAO();
		RegularAccountDAO regularAccountDAO = Factory.getRegularAccountDAO();
		TransactionDAO transactionDAO = Factory.getTransactionDAO();
		
		RegularAccount debitFromAccount = null, payoutAccount = null;
		DepositAccount account = null;
		
		LocalDate today = LocalDate.now();
		boolean isError = false, exceptionOccured = false, isSufficientBalance = false;
		String msg = "", customerName, description = "";
		long payoutAccountNo, debitFromAccountNo, customerId;
		float balance, beforeBalance;
		int branchId, actionType = -1, depositType, amount = 0, tenureMonths;
		int recurringDate = -1, debitFromAccountBranchId, payoutAccountBranchId;
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
			
			if(!isError) {
				if(depositType == DepositAccountType.RD.id) {
					amount = Integer.parseInt(req.getParameter("rd-amount"));
					recurringDate = Integer.parseInt(req.getParameter("recurring-date"));
					
					if((recurringDate >= 1) && (recurringDate <= 15)) {
						nextRecurringDate = today.withDayOfMonth(recurringDate).plusMonths(1);
					} else {
						isError = true;
						msg = "Monthly installment date should be between 1 to 15";
					}
					
				} else {
					amount = Integer.parseInt(req.getParameter("fd-amount"));
				}
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
			
			if(!isError && (tenureMonths < 2 || tenureMonths > 20)) {
				isError = true;
				msg = "Tenure months should be between 2 to 20 !!!";
			}
			
			// Validate debit from account and payout account details
			if(!isError) {
				conn = Factory.getDataSource().getConnection();
				debitFromAccountBranchId = accountDAO.getBranchId(conn, debitFromAccountNo);
				debitFromAccount = regularAccountDAO.get(debitFromAccountNo, debitFromAccountBranchId);
				
				if(debitFromAccount != null && debitFromAccount.getIsActive() && !debitFromAccount.isClosed()) {
					if(debitFromAccountNo == payoutAccountNo)
						payoutAccount = debitFromAccount;
					else {
						
						payoutAccountBranchId = accountDAO.getBranchId(conn, payoutAccountNo);
						payoutAccount = regularAccountDAO.get(payoutAccountNo, payoutAccountBranchId);
						if(payoutAccount == null || !payoutAccount.getIsActive() || payoutAccount.isClosed()
								|| payoutAccount.getCustomerId() != debitFromAccount.getCustomerId()) {
							isError = true;
							msg = "Invalid payout account details !!!";
						}
						
					}
				} else {
					isError = true;
					msg = "Invalid debit from account details !!!";
				}
			}
			
			/* Inorder to display previously inputed values attributes are set
			even when some error occurs */
			req.setAttribute("depositType", depositType);
			req.setAttribute("debitFromAccountNo", debitFromAccountNo);
			req.setAttribute("payoutAccountNo", payoutAccountNo);
			req.setAttribute("tenureMonths", tenureMonths);
			req.setAttribute("amount", amount);
			req.setAttribute("recurringDate", recurringDate);
			
			// Get deposit account details and ask for confirmation
			if(!isError && actionType == 0) {
				req.setAttribute("actionType", 1);
				req.setAttribute("debitFromAccount", debitFromAccount);
				req.setAttribute("payoutAccount", payoutAccount);					
				req.getRequestDispatcher("/jsp/employee/createDeposit.jsp").forward(req, res);
			}
			
			
			// Create Deposit account
			if(!isError && actionType == 1) {
				customerId = debitFromAccount.getCustomerId();
				customerName = debitFromAccount.getCustomerName();
				
				/* Incase of RD if deposit account create date is after 15 th of the month
					dont debit for RD, set recurring date to next month and create account */
				
				if(depositType == DepositAccountType.RD.id && today.getDayOfMonth() > 15) {
					
					// create deposit account
	            	account = depositAccountDAO.create(conn, customerId, customerName, branchId, depositType, null, 0, tenureMonths, payoutAccountNo, debitFromAccountNo, nextRecurringDate);
					out.println(Util.createNotification("Deposit created successfully, initial deposit amount will be auto debited on next recurring date", "success"));
	            	req.setAttribute("account", account);
	            	req.getRequestDispatcher("/jsp/employee/depositCreationSuccess.jsp").include(req, res);
				
				} else {
					synchronized (debitFromAccount) {
						
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
			            	
			            	// update debit from account amount in cache.
			            	debitFromAccount.deductAmount(amount);
			            	
			            	description = DepositAccountType.getType(depositType).toString() + " withdrawal for A/C: " + account.getAccountNo();
			            	
			            	transactionDAO.create(conn, 1, description, debitFromAccountNo, account.getAccountNo(), amount, true, true, beforeBalance, 0);	            	
			            	
			            	req.setAttribute("account", account);
			            	req.getRequestDispatcher("/jsp/employee/depositCreationSuccess.jsp").forward(req, res);
			            } else {
			            	isError = true;
			            	msg = "Insufficient balance in debit from account !!!";
			            }
					}
					// End of synchronised block
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
