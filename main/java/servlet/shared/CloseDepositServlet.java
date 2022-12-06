package servlet.shared;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cache.AppCache;
import constant.AccountCategory;
import constant.Constants;
import constant.DepositAccountType;
import constant.Role;
import constant.TransactionType;
import dao.AccountDAO;
import dao.CustomerDAO;
import dao.DepositAccountDAO;
import dao.RegularAccountDAO;
import dao.TransactionDAO;
import model.Bank;
import model.Branch;
import model.Transaction;
import model.account.DepositAccount;
import model.account.RegularAccount;
import model.user.Customer;
import util.Factory;
import util.Util;


public class CloseDepositServlet extends HttpServlet {
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Connection conn = null;
		
		CustomerDAO customerDAO = Factory.getCustomerDAO();
		TransactionDAO transactionDAO = Factory.getTransactionDAO();
		RegularAccountDAO regularAccountDAO = Factory.getRegularAccountDAO();
		DepositAccountDAO depositAccountDAO = Factory.getDepositAccountDAO();
		AccountDAO accountDAO = Factory.getAccountDAO();
		
		Customer customer = null;
		RegularAccount payoutAccount = null;
		RegularAccount bankAccount = null;
		DepositAccount account = null;
		Transaction transaction = null;
		LocalDateTime dateTime = null;
		
		Branch branch = null;
		Bank bank = AppCache.getBank();
		
		Role role = null;
		String msg = "", description = "", userType = "", redirectURI = "";
		float fromAccountBeforeBalance, toAccountBeforeBalance, totalAmount;
		
		boolean exceptionOccured = false, isError = false, prematureClosing = false;
		long accountNo = -1, customerId = -1;
		
		long bankAccountNo = bank.getBankAccountNo();
		int bankAccountBranchId = bank.getAccountBranchId();
		
		long transactionId;
		int branchId = -1, actionType = 0, payoutAccountBranchId;
		
		try {
			role = (Role) req.getSession(false).getAttribute("role");
			userType = Role.getName(role);
			actionType = Integer.parseInt(req.getParameter("action-type"));
			accountNo = Long.parseLong(req.getParameter("account-no"));
			
			if(Util.getNoOfDigits(accountNo) != 11 ) {
				isError = true;
				msg = "A/C no must be a 11 digit number";
			}
						
			if(!isError) {
				
	        	// Access for account differs for customer and employee.
	        	switch(role) {
		        	case EMPLOYEE:	branchId = (Integer) req.getSession(false).getAttribute("branch-id");
		        					break;
		        	case CUSTOMER: 
		        					customerId = (Long) req.getSession(false).getAttribute("id"); 
		        					customer = customerDAO.get(customerId);
		        					branchId = customer.getAccountBranchId(AccountCategory.DEPOSIT, accountNo);
		        	default: break;
	        	}
	        	
				account = depositAccountDAO.get(accountNo, branchId);
				if(account == null || account.isClosed()) {
					isError = true;
					msg = "Account does not exist !!!";
				}
				
				if(!isError) {
					branch = AppCache.getBranch(branchId);
					if(branch == null) {
						isError = true;
						msg = "Invalid branch selected !!!";
					}
				}
        	
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
					dateTime = LocalDateTime.now();
					
					totalAmount = account.getBalance();
					
					// Deduct premature closing charges from total amount.
					if(prematureClosing)
						totalAmount = totalAmount - Constants.PREMATURE_CLOSING_CHARGES;
				
					conn = Factory.getDataSource().getConnection();
					
					// Debit charges for premature closing
					if(prematureClosing) {
						
						bankAccount = regularAccountDAO.get(bankAccountNo, bankAccountBranchId);
						
						synchronized (bankAccount) {
							// credit 500 to bank account.
							fromAccountBeforeBalance = accountDAO.updateBalance(conn, account.getAccountNo(), 0, Constants.PREMATURE_CLOSING_CHARGES);
							toAccountBeforeBalance = accountDAO.updateBalance(conn, bankAccountNo, 1, Constants.PREMATURE_CLOSING_CHARGES);
							
							// update in cache.
							account.deductAmount(Constants.PREMATURE_CLOSING_CHARGES);
							bankAccount.addAmount(Constants.PREMATURE_CLOSING_CHARGES);
							
							description = "Premature closing charges on A/C: " + account.getAccountNo();
							transactionId = transactionDAO.create(conn, TransactionType.NEFT.id , description, account.getAccountNo(), bankAccountNo, Constants.PREMATURE_CLOSING_CHARGES, true, true, fromAccountBeforeBalance, toAccountBeforeBalance);
							transaction = new Transaction(transactionId, TransactionType.NEFT.id, account.getAccountNo(), bankAccountNo, Constants.PREMATURE_CLOSING_CHARGES, LocalDateTime.now(), description, toAccountBeforeBalance);
							
							bankAccount.addTransaction(transaction);
						
						}
					}
					
					payoutAccountBranchId = accountDAO.getBranchId(conn, account.getPayoutAccountNo());
					payoutAccount = regularAccountDAO.get(account.getPayoutAccountNo(), payoutAccountBranchId);
					
					synchronized(payoutAccount) {
						// credit amount to payout account and close deposit.
						fromAccountBeforeBalance = accountDAO.updateBalance(conn, account.getAccountNo(), 0, totalAmount);
						toAccountBeforeBalance = accountDAO.updateBalance(conn, account.getPayoutAccountNo(), 1, totalAmount);
						
						// update in cache.
						
						account.deductAmount(totalAmount);
						payoutAccount.addAmount(totalAmount);
						
						description = DepositAccountType.getType(account.getTypeId()).toString() + " closing on A/C: " + account.getAccountNo();
						
						transactionId = transactionDAO.create(conn, TransactionType.NEFT.id, description, account.getAccountNo(), account.getPayoutAccountNo(), totalAmount, true, true, fromAccountBeforeBalance, toAccountBeforeBalance);
						
						// Update transaction record in cache.
						transaction = new Transaction(transactionId, TransactionType.NEFT.id,  account.getAccountNo(), account.getPayoutAccountNo(), totalAmount, dateTime, description, toAccountBeforeBalance);						
						payoutAccount.addTransaction(transaction);
						
						accountDAO.closeAccount(conn, account, AccountCategory.DEPOSIT);
					}
					// End of payout account synchronized block.
					
					synchronized (branch) {
	        			branch.setDepositAccountCnt(branch.getDepositAccountCnt() - 1);									
					}
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