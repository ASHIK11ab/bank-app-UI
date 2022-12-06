package servlet.shared;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cache.AppCache;
import constant.AccountCategory;
import constant.DepositAccountType;
import constant.Role;
import constant.TransactionType;
import dao.AccountDAO;
import dao.BranchDAO;
import dao.CustomerDAO;
import dao.DepositAccountDAO;
import dao.RegularAccountDAO;
import dao.TransactionDAO;
import model.Branch;
import model.Transaction;
import model.account.DepositAccount;
import model.account.RegularAccount;
import model.user.Customer;
import util.Factory;
import util.Util;


public class CreateDepositServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		CustomerDAO customerDAO = Factory.getCustomerDAO();
		BranchDAO branchDAO = Factory.getBranchDAO();
		
		Role role = null;
		Customer customer = null;
		
		long customerId;
		
		try {
			role = (Role) req.getSession(false).getAttribute("role");
			
			if(role == Role.CUSTOMER) {				
				customerId = (Long) req.getSession(false).getAttribute("id");
				customer = customerDAO.get(customerId);
				req.setAttribute("activeAccounts", customer.getActiveAccounts(AccountCategory.REGULAR));
				req.setAttribute("branches", branchDAO.getAll());
			}
				
				req.setAttribute("actionType", 0);
				req.getRequestDispatcher("/jsp/components/createDeposit.jsp").include(req, res);
		} catch(ClassCastException e) {
			System.out.println(e.getMessage());
			res.sendError(500);
		} catch(SQLException e) {
			res.sendError(500);
		}
	}


	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		PrintWriter out = res.getWriter();
		
		BranchDAO branchDAO = Factory.getBranchDAO();
		AccountDAO accountDAO = Factory.getAccountDAO();
		CustomerDAO customerDAO = Factory.getCustomerDAO();
		DepositAccountDAO depositAccountDAO = Factory.getDepositAccountDAO();
		RegularAccountDAO regularAccountDAO = Factory.getRegularAccountDAO();
		TransactionDAO transactionDAO = Factory.getTransactionDAO();
		
		Role role = null;
		Customer customer = null;
		Transaction transaction = null;
		RegularAccount debitFromAccount = null, payoutAccount = null;
		DepositAccount account = null;
		Branch branch = null;
		
		LocalDate today = LocalDate.now();
		boolean isError = false, exceptionOccured = false, isSufficientBalance = false;
		String msg = "", customerName, description = "";
		long payoutAccountNo, debitFromAccountNo, customerId = -1, transactionId;
		float balance, beforeBalance;
		int branchId = -1, actionType = -1, depositType, amount = 0, tenureMonths;
		int recurringDate = -1, debitFromAccountBranchId, payoutAccountBranchId;
		LocalDate nextRecurringDate = null;
		
		try {
			role = (Role) req.getSession(false).getAttribute("role");
			
			if(role == Role.EMPLOYEE)
				branchId = (Integer) req.getSession(false).getAttribute("branch-id");
			else {
				// customer
				customerId = (Long) req.getSession(false).getAttribute("id");
				customer = customerDAO.get(customerId);
				branchId = Integer.parseInt(req.getParameter("branch"));
			}
			
			branch = AppCache.getBranch(branchId);
			
			actionType = Integer.parseInt(req.getParameter("action-type"));
			depositType = Integer.parseInt(req.getParameter("deposit-type"));
			debitFromAccountNo = Long.parseLong(req.getParameter("debit-from-account-no"));
			payoutAccountNo = Long.parseLong(req.getParameter("payout-account-no"));
			tenureMonths = Integer.parseInt(req.getParameter("tenure-months"));
			
			if(DepositAccountType.getType(depositType) == null) {
				isError = true;
				msg = "Invalid deposit type";
			}
			
			if(!isError && branch == null) {
				isError = true;
				msg = "Invalid branch id !!!";
			}
			
			if(actionType != 0 && actionType != 1) {
				isError = true;
				msg = "Internal error";
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
			
			if(!isError && !(tenureMonths >= 2 && tenureMonths <= 20)) {
				isError = true;
				msg = "Tenure months should be between 2 to 20 !!!";
			}
			
			// Validate debit from account and payout account details
			if(!isError) {
				conn = Factory.getDataSource().getConnection();
				debitFromAccountBranchId = accountDAO.getBranchId(conn, debitFromAccountNo);
				debitFromAccount = regularAccountDAO.get(debitFromAccountNo, debitFromAccountBranchId);
				
				if(debitFromAccount != null && debitFromAccount.getIsActive()) {
					if(debitFromAccountNo == payoutAccountNo)
						payoutAccount = debitFromAccount;
					else {
						
						payoutAccountBranchId = accountDAO.getBranchId(conn, payoutAccountNo);
						payoutAccount = regularAccountDAO.get(payoutAccountNo, payoutAccountBranchId);
						
						if(payoutAccount == null || !payoutAccount.getIsActive() || payoutAccount.isClosed()) {
							isError = true;
							msg = "Invalid payout account details !!!";
						}
					}
				} else {
					isError = true;
					msg = "Invalid debit from account details !!!";
				}
				
				// When customer creates deposit ensure that debit from account and payout belongs to the customer.
				if(!isError && role == Role.CUSTOMER && 
						(debitFromAccount.getCustomerId() != customerId || payoutAccount.getCustomerId() != customerId)) {
					isError = true;
					msg = "Account(s) does not exist !!";
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
			
			if(role == Role.CUSTOMER) {
				req.setAttribute("activeAccounts", customer.getActiveAccounts(AccountCategory.REGULAR));
				req.setAttribute("branches", branchDAO.getAll());
				req.setAttribute("branchId", branchId);
			}

			
			// Get deposit account details and ask for confirmation
			if(!isError && actionType == 0) {
				req.setAttribute("actionType", 1);
				req.setAttribute("debitFromAccount", debitFromAccount);
				req.setAttribute("payoutAccount", payoutAccount);					
				req.getRequestDispatcher("/jsp/components/createDeposit.jsp").forward(req, res);
			}
			
			
			// Create Deposit account
			if(!isError && actionType == 1) {
				customerId = debitFromAccount.getCustomerId();
				customerName = debitFromAccount.getCustomerName();
				
				/* Incase of RD if deposit account create date is after 15 th of the month
					dont debit for RD, set recurring date to next month and create account */
				
				if(depositType == DepositAccountType.RD.id && today.getDayOfMonth() > 15) {
					
					// create deposit account
	            	account = depositAccountDAO.create(conn, customerId, customerName, branchId, depositType, null, 0, tenureMonths, payoutAccountNo, debitFromAccountNo, nextRecurringDate, amount);
					out.println(Util.createNotification("Deposit created successfully, initial deposit amount will be auto debited on next recurring date", "success"));
	            	req.setAttribute("account", account);
	            	req.getRequestDispatcher("/jsp/components/depositCreationSuccess.jsp").include(req, res);
				
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
			            	account = depositAccountDAO.create(conn, customerId, customerName, branchId, depositType, null, amount, tenureMonths, payoutAccountNo, debitFromAccountNo, nextRecurringDate, amount);
			            	
			            	// update debit from account amount in cache.
			            	debitFromAccount.deductAmount(amount);
			            	
			            	description = DepositAccountType.getType(depositType).toString() + " withdrawal for A/C: " + account.getAccountNo();
			            	
			            	transactionId = transactionDAO.create(conn, TransactionType.NEFT.id, description, debitFromAccountNo, account.getAccountNo(), amount, true, true, beforeBalance, 0);	            	
			            	
			            	// Add transaction record to cached debit from account.
			            	transaction = new Transaction(transactionId, TransactionType.NEFT.id, debitFromAccountNo, account.getAccountNo(), amount, LocalDateTime.now(), description, beforeBalance);
			            	debitFromAccount.addTransaction(transaction);
			            	
			            	req.setAttribute("account", account);
			            	req.getRequestDispatcher("/jsp/components/depositCreationSuccess.jsp").forward(req, res);
			            } else {
			            	isError = true;
			            	msg = "Insufficient balance in debit from account !!!";
			            }
					}
					// End of synchronised block
				}
				
				if(!isError)
	        		synchronized (branch) {
	        			branch.setDepositAccountCnt(branch.getDepositAccountCnt() + 1);									
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
