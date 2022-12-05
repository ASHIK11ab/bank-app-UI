package servlet.customer;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jasper.tagplugins.jstl.core.If;

import constant.AccountCategory;
import constant.BeneficiaryType;
import constant.TransactionType;
import dao.AccountDAO;
import dao.CustomerDAO;
import dao.RegularAccountDAO;
import dao.TransactionDAO;
import model.Beneficiary;
import model.Transaction;
import model.account.RegularAccount;
import model.user.Customer;
import util.Factory;
import util.Util;


public class FundTransferServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setAttribute("actionType", 0);
		req.getRequestDispatcher("/jsp/customer/fundTransfer.jsp").include(req, res);
	}


	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();

		Connection conn = null;
		
		CustomerDAO customerDAO = Factory.getCustomerDAO();
		AccountDAO accountDAO = Factory.getAccountDAO();
		RegularAccountDAO regularAccountDAO = Factory.getRegularAccountDAO();
		TransactionDAO transactionDAO = Factory.getTransactionDAO();
		
		Properties activeAccounts = null;
		Customer customer = null;
		Beneficiary beneficiary = null;
		Transaction transaction = null;
		RegularAccount account = null, beneficiaryAccount = null;
		SortedSet<Beneficiary> beneficiaries = null;
		LocalDateTime dateTime = null;
		
		boolean isError = false, exceptionOccured = false;
		String msg = "", transactionPassword, description = "";
		long customerId, selectedBeneficiaryId, selectedAccountNo, transactionId;
		float amount = 0, fromAccountBeforeBalance = -1, toAccountBeforeBalance = -1;
		int actionType = -1, transactionTypeId = -1, beneficiaryTypeId = -1;
		int beneficiaryBranchId, branchId, statusCode;
		TransactionType transactionType = null;
		BeneficiaryType beneficiaryType = null;
		
		try {
			customerId = (Long) req.getSession(false).getAttribute("id");
			actionType = Integer.parseInt(req.getParameter("action-type"));
			transactionPassword = req.getParameter("transaction-password");
			
			// Validate input data
			if(actionType != 0 && actionType != 1 && actionType != 2 && actionType != 3) {
				isError = true;
				msg = "Invalid action !!!";
				out.println(Util.createNotification(msg, "danger"));
				doGet(req, res);
				return;
			}
			
			customer = customerDAO.get(customerId);
			
			synchronized (customer) {
				// Validate transaction password
				if(!customer.getTransPassword().equals(transactionPassword)) {
						isError = true;
						msg = "Invalid transaction password !!!";
				}
				
				if(!isError) {
					activeAccounts = customer.getActiveAccounts(AccountCategory.REGULAR);
					
					if(activeAccounts.size() == 0) {
						isError = true;
						msg = "No active accounts <br> Cannot transfer funds !!!";
					}
				}
				
				// Validate step 1 input.
				if(!isError && actionType >= 1) {
					transactionTypeId = Integer.parseInt(req.getParameter("transaction-type"));
					beneficiaryTypeId = Integer.parseInt(req.getParameter("beneficiary-type"));
					
					transactionType = TransactionType.getType(transactionTypeId);
					beneficiaryType = BeneficiaryType.getType(beneficiaryTypeId);
					
					if(transactionType != TransactionType.NEFT && transactionType != TransactionType.IMPS
							&& transactionType != TransactionType.RTGS) {
						isError = true;
						msg = "Invalid transaction type !!!";
					}
						
					if(!isError && beneficiaryType == null) {
						isError = true;
						msg = "Invalid beneficiary type !!!";
					}
					
					if(!isError) {
						beneficiaries = customer.getBeneficiaries(beneficiaryType);
						
						if(beneficiaries.size() == 0) {
							isError = true;
							msg = "No beneficiaries exist <br> Add beneficiaries to fund transfer !!!";
						}
					}
				}
				// End of validating step 1 input.
				
				// Validate step 2 input.
				if(!isError && actionType >= 2) {
					amount = Float.parseFloat(req.getParameter("amount"));
					selectedBeneficiaryId = Long.parseLong(req.getParameter("selected-beneficiary"));
					selectedAccountNo = Long.parseLong(req.getParameter("selected-account"));
					description = req.getParameter("description").strip();
					
					description = (description.length() > 30) ? description.substring(0, 29) : description;
					
					// validate selected beneficiary.
					if(!isError) {
						beneficiary = customer.getBeneficiary(beneficiaryType, selectedBeneficiaryId);
						
						if(beneficiary == null) {
							isError = true;
							msg = "Invalid beneficiary selected !!!";
						}
					}
					
					// validate selected account.
					// Ensure selected account belongs to customer and is active.
					if(!isError) {
						branchId = customer.getAccountBranchId(AccountCategory.REGULAR, selectedAccountNo);
						account = regularAccountDAO.get(selectedAccountNo, branchId);
						
						if(account == null || !account.getIsActive()) {
							isError = true;
							msg = "Invalid account details !!!";
						}
					}
					
					// Validate amount based on the selected transction.
					if(!isError && !(amount >= transactionType.minAmount && amount <= transactionType.maxAmount)) {
						isError = true;
						msg = "Invalid amount for selected transaction !!!";
					}
				}
				// End of validating step 2 input
				
				if(!isError) {
					// Action type - 0 => Validate password, Display for selecting transaction, beneficiary type page.
					// Action Type - 1 => Ask customer to select account, beneficiary and transaction details.
					// Action type - 2 => Display summary and ask for confirmation.
					
					req.setAttribute("actionType", actionType + 1);
					req.setAttribute("transactionPassword", transactionPassword);
					
					if(actionType == 1 || actionType == 2) {
						req.setAttribute("transactionTypeId", transactionTypeId);
						req.setAttribute("beneficiaryTypeId", beneficiaryTypeId);
						req.setAttribute("beneficiaries", beneficiaries);
						req.setAttribute("activeAccounts", activeAccounts);
					}
					
					// Attributes requied to display transaction summary for confirmation.
					if(actionType == 2) {
						req.setAttribute("selectedBeneficiary", beneficiary);
						req.setAttribute("selectedAccount", account);
						req.setAttribute("amount", amount);
						req.setAttribute("description", description);
					}
					
					if(actionType == 0 || actionType == 1 || actionType == 2) {
						req.getRequestDispatcher("/jsp/customer/fundTransfer.jsp").forward(req, res);
						return;
					}
					
					// perform fund transfer
						
					synchronized (account) {
						conn = Factory.getDataSource().getConnection();
						
						statusCode = account.initiateTransaction(amount);
						
						// Proceed with transaction
						if(statusCode == 200) {
						
							if(beneficiaryType == BeneficiaryType.OWN_BANK) {
								// Ensure beneficiary account exists and is active.
								beneficiaryBranchId = accountDAO.getBranchId(conn, beneficiary.getAccountNo());
								
								// Validate whether added beneficiary account exists in DB.
								if(beneficiaryBranchId == -1) {
									isError = true;
									msg = "Beneficiary account does not exist !!!";
								}
							
								if(!isError) {
									beneficiaryAccount = regularAccountDAO.get(beneficiary.getAccountNo(), beneficiaryBranchId);
									
									// Added beneficiary account is not a regular account.
									if(beneficiaryAccount == null) {
										isError = true;
										msg = "Invalid beneficiary account !!!";
									}
								}
									
								if(!isError) { 
									synchronized (beneficiaryAccount) {
										if(!beneficiaryAccount.getIsActive()) {
											isError = true;
											msg = "Beneficiary cannot accept payments now !!!";
										} 
										
										if(account.getAccountNo() == beneficiaryAccount.getAccountNo()) {
											isError = true;
											msg = "Cannot transfer to same account !!!";
										}
										
										if(!isError) {
											fromAccountBeforeBalance = accountDAO.updateBalance(conn, account.getAccountNo(), 0, amount);	// Debit amount
											toAccountBeforeBalance = accountDAO.updateBalance(conn, beneficiary.getAccountNo(), 1, amount);	// Credit amount
											
											// update balance in cache.
											account.deductAmount(amount);
											beneficiaryAccount.addAmount(amount);
											
										}
									}
									// End of beneficiary synchronized block
								}
							}
							// End of own bank beneficiary check block
							else {
								// Other bank transaction.
								fromAccountBeforeBalance = accountDAO.updateBalance(conn, account.getAccountNo(), 0, amount); // Debit amount
								
								// update balance in cache.
								account.deductAmount(amount);
							}
							
							// Transaction successfull
							if(!isError) {
								// create transaction record.
								dateTime = LocalDateTime.now();
								
								transactionId = transactionDAO.create(conn, transactionTypeId, description, account.getAccountNo(), beneficiary.getAccountNo(), amount, true, (beneficiaryType == BeneficiaryType.OWN_BANK), fromAccountBeforeBalance, toAccountBeforeBalance);
								transaction = new Transaction(transactionId, transactionTypeId, account.getAccountNo(), beneficiary.getAccountNo(), amount, dateTime, description, fromAccountBeforeBalance);
								
								account.addTransaction(transaction);
								if(beneficiaryType == BeneficiaryType.OWN_BANK) {
									synchronized (beneficiaryAccount) {
										transaction = new Transaction(transactionId, transactionTypeId, account.getAccountNo(), beneficiary.getAccountNo(), amount, dateTime, description, toAccountBeforeBalance);
										beneficiaryAccount.addTransaction(transaction);											
									}
								}
								
								req.setAttribute("transaction", transaction);
								req.getRequestDispatcher("/jsp/customer/transactionSuccess.jsp").forward(req, res);
							}
						} // End of status 200 check block
						else {
							isError = true;
							switch(statusCode) {
								case 401: msg = "Insufficient balance !!!";
										  break;
								case 402: msg = "Maximum transaction limit exceeded <br> Try with lesser amount !!!";
								          break;
								default: msg = "Error !!!";
							}
						}
					}
					// End of synchronized block on account.
				}
			}
			// End of synchronized customer block
		} catch(NullPointerException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "Internal error !!!";
		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "Internal error !!!";
		} catch(SQLException e) {
			exceptionOccured = true;
			msg = e.getMessage();
		} finally {			
			
			if(isError || exceptionOccured) {
				out.println(Util.createNotification(msg, "danger"));
				req.removeAttribute("transactionPassword");
				doGet(req, res);
			}
			
			out.close();
		}
	}
}
