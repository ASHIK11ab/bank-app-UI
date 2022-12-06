package servlet.employee;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cache.AppCache;
import constant.DebitCardType;
import constant.RegularAccountType;
import constant.TransactionType;
import dao.CustomerDAO;
import dao.RegularAccountDAO;
import dao.TransactionDAO;
import model.user.Customer;
import model.Branch;
import model.account.RegularAccount;
import util.Factory;
import util.Util;

public class CreateAccountServlet extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		Branch branch;
		PrintWriter out = res.getWriter();
		CustomerDAO customerDAO = Factory.getCustomerDAO();
		TransactionDAO transactionDAO = Factory.getTransactionDAO();
		RegularAccountDAO accountDAO = Factory.getRegularAccountDAO();
		
		Customer customer = null;
		RegularAccount account = null;
		RegularAccountType accountType;
		String msg = "";
		boolean isEligibleForAccount = true, isError = false, exceptionOccured = false;
		long customerId;
		int accountTypeId, cardTypeId, branchId, actionType;
		
		try {
			actionType = Integer.parseInt(req.getParameter("action-type"));
			customerId = Long.parseLong(req.getParameter("customer-id"));
        	accountTypeId = Integer.parseInt(req.getParameter("account-type"));
        	cardTypeId = Integer.parseInt(req.getParameter("card-type"));
        	branchId = (Integer) req.getSession(false).getAttribute("branch-id");
        	
        	branch = AppCache.getBranch(branchId);
        	
	       	accountType = RegularAccountType.getType(accountTypeId);
        	
			customer = customerDAO.get(customerId);
			if(customer == null || customer.isRemoved()) {
				isError = true;
				msg = "invalid customer id !!!";
			}
			
			if(!isError && (actionType != 0 && actionType != 1)) {
				isError = true;
				msg = "Error !!!";
			}
			
        	if(!isError && RegularAccountType.getType(accountTypeId) == null) {
        		isError = true;
        		msg = "Invalid account selected !!!";
        	}
        	
        	if(!isError && DebitCardType.getType((byte) cardTypeId) == null) {
        		isError = true;
        		msg = "Invalid card selected !!!";
        	}
        	
			// Pass data.
			req.setAttribute("accountType", accountTypeId);
			req.setAttribute("cardType", cardTypeId);
			
			if(!isError) {
				// Display customer details and ask for confirmation.
				if(actionType == 0) {
					req.setAttribute("actionType", 1);
					req.setAttribute("customer", customer);
					req.getRequestDispatcher("/jsp/employee/createAccount.jsp").include(req, res);					
				} else {
	        		conn = Factory.getDataSource().getConnection();
										
	                if(accountType == RegularAccountType.CURRENT) {
	                    stmt = conn.prepareStatement("SELECT ra.account_no FROM regular_account ra LEFT JOIN account a ON ra.account_no = a.account_no WHERE a.customer_id = ? AND ra.type_id = ?");
	                    stmt.setLong(1, customerId);
	                    stmt.setInt(2, RegularAccountType.CURRENT.id);
	                    rs = stmt.executeQuery();
	
	                    // Customer aldready has a current account.
	                    if(rs.next()) {
	                        isEligibleForAccount = false;
	                        isError = true;
	                        msg = "Customer aldready has a current account in bank <br> Cannot create account !!!";
	                    } 
	                } 
                	else {
	                    stmt = conn.prepareStatement("SELECT ra.account_no FROM regular_account ra LEFT JOIN account a ON ra.account_no = a.account_no WHERE a.customer_id = ? AND a.branch_id = ? AND ra.type_id = ?");
	                    stmt.setLong(1, customerId);
	                    stmt.setInt(2, branchId);
	                    stmt.setInt(3, RegularAccountType.SAVINGS.id);
	                    rs = stmt.executeQuery();
	
	                    // Customer aldreay has a savings account in this branch.
	                    if(rs.next()) {
	                        isEligibleForAccount = false;
	                        isError = true;
	                        msg = "Customer aldready has a savings account in this branch !!!";
	                    }
                    }
                
	                if(isEligibleForAccount) {
	                	synchronized (customer) {
		                	account = accountDAO.create(conn, customer.getId(), customer.getName(),
															branchId, accountType, cardTypeId, null);
		            		transactionDAO.create(conn, TransactionType.CASH.id, ("Deposit to A/C: " + account.getAccountNo()), null, account.getAccountNo(), account.getBalance(), false, true, -1, 0);
							
		            		synchronized (branch) {
		            			switch(accountType) {
			            			case SAVINGS: branch.setSavingsAccountCnt(branch.getSavingsAccountCnt() + 1); break;
			            			case CURRENT: branch.setCurrentAccountCnt(branch.getCurrentAccountCnt() + 1); break;
		            			}									
							}
		            		
		            		req.setAttribute("account", account);
							req.setAttribute("card", account.getCards().first());
							req.getRequestDispatcher("/jsp/employee/accountCreationSuccess.jsp").forward(req, res);
	                	}
	                }
				}
			}
		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "Internal error";
		} catch(ClassCastException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "Internal error";
		} catch(SQLException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = e.getMessage();
		} finally {
			
			try {
				if(rs != null)
					rs.close();
			} catch(SQLException e) { System.out.println(e.getMessage()); }
			
			try {
				if(stmt != null)
					stmt.close();
			} catch(SQLException e) { System.out.println(e.getMessage()); }
			
			try {
				if(conn != null)
					conn.close();
			} catch(SQLException e) { System.out.println(e.getMessage()); }
			
			if(isError || exceptionOccured) {
				out.println(Util.createNotification(msg, "danger"));
				req.getRequestDispatcher("/jsp/employee/initiateAccountCreation.jsp").include(req, res);
			}
			
			out.close();
		}
	}	
}