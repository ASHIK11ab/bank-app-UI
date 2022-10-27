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

import dao.AccountDAO;
import dao.CustomerDAO;
import dao.RegularAccountDAO;
import model.account.RegularAccountBean;
import util.Factory;
import util.Util;

public class CloseAccountServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setAttribute("confirmationType", 0);
		req.getRequestDispatcher("/jsp/employee/closeAccount.jsp").include(req, res);
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Connection conn = null;
		PreparedStatement stmt1 = null, stmt2 = null;
		ResultSet rs1 = null, rs2 = null;
		
		PrintWriter out = res.getWriter();
		
		CustomerDAO customerDAO = Factory.getCustomerDAO();
		RegularAccountDAO regularAccountDAO = Factory.getRegularAccountDAO();
		AccountDAO accountDAO = Factory.getAccountDAO();
		
		RegularAccountBean account = null;
		
		String msg = "";
		boolean exceptionOccured = false, isError = false, isDeletionSuccessfull = false;
		long accountNo = -1, customerId = -1;
		int actionType = -1, branchId, noOfAccounts = -1;
		
		try {
			branchId = (Integer) req.getSession(false).getAttribute("branch-id");
			accountNo = Long.parseLong(req.getParameter("account-no"));
			actionType = Integer.parseInt(req.getParameter("action-type"));
			
			if(Util.getNoOfDigits(accountNo) != 11 ) {
				out.println(Util.createNotification("A/C no must be a 11 digit number", "danger"));
				doGet(req, res);
				out.close();
			}
			
			// Display account and ask for confirmation.
			if(actionType == 0) {
				account = regularAccountDAO.get(accountNo);
				
				if(account != null && account.getBranchId() == branchId) {
					req.setAttribute("confirmationType", 1);
					req.setAttribute("account", account);
					req.getRequestDispatcher("/jsp/employee/closeAccount.jsp").forward(req, res);
				} else {
					isError = true;
					msg = "Invalid account no !!!";
				}
			}
			
			// Delete account if eligible or request further confirmation.
			if(actionType == 1) {
				customerId = Long.parseLong(req.getParameter("customer-id"));
				conn = Factory.getDataSource().getConnection();
				
                // Check whether account is linked with any deposit account(s).
                stmt1 = conn.prepareStatement("SELECT account_no from deposit_account WHERE debit_from_account = ? OR payout_account_no = ? limit 1");
                stmt1.setLong(1, accountNo);
                stmt1.setLong(2, accountNo);
                rs1 = stmt1.executeQuery();
                
                if(rs1.next()) {
                	isError = true;
                	msg = "Account is linked with deposit account(s) <br> cannot close account !!!";
                } else {
    				stmt2 = conn.prepareStatement("select count(*) as count from account WHERE customer_id = ?");
                    stmt2.setLong(1, customerId);
                    rs2 = stmt2.executeQuery();
                    
                    if(rs2.next())
                        noOfAccounts = rs2.getInt("count");
                    
                    // Customer has only one account in bank. Require confirmation
                    if(noOfAccounts == 1) {
                    	String note = "customer has only one account, closing this account will remove customer from bank";
                    	req.setAttribute("confirmationType", 2);
                    	req.setAttribute("accountNo", accountNo);
                    	req.setAttribute("customerId", customerId);
                    	req.setAttribute("note", note);
                    	req.getRequestDispatcher("/jsp/employee/closeAccount.jsp").forward(req, res);
                    } else {
                    	// Delete account.
                    	isDeletionSuccessfull = accountDAO.delete(conn, accountNo);
                    	
                    	if(!isDeletionSuccessfull) {
                    		isError = true;
                    		msg = "cannot close account internal error";
                    	} else {
            				out.println(Util.createNotification("account closed successfully", "success"));
                    	}
                    }
                }
			}
			
			// Remove customer.
			if(actionType == 2) {
				customerId = Long.parseLong(req.getParameter("customer-id"));
            	isDeletionSuccessfull = customerDAO.delete(customerId);
            	
            	if(!isDeletionSuccessfull) {
            		isError = true;
            		msg = "cannot close account internal error";
            	} else {
    				out.println(Util.createNotification("Account closed and customer removed from bank successfully", "success"));
            	}
			}
			
		} catch(ClassCastException e) {
			exceptionOccured = true;
			msg = "internal error !!!";
		} catch(NumberFormatException e) {
			exceptionOccured = true;
			msg = "internal error !!!";
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
			}
			
			doGet(req, res);
			out.close();
		}
	}
}