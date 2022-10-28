package servlet.employee;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.DepositAccountType;
import dao.AccountDAO;
import dao.DepositAccountDAO;
import dao.TransactionDAO;
import model.account.DepositAccountBean;
import util.Factory;
import util.Util;


public class CloseDepositServlet extends HttpServlet {
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setAttribute("actionType", 0);
		req.getRequestDispatcher("/jsp/employee/closeDeposit.jsp").include(req, res);
	}
	
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Connection conn = null;
		PrintWriter out = res.getWriter();
		
		TransactionDAO transactionDAO = Factory.getTransactionDAO();
		DepositAccountDAO depositAccountDAO = Factory.getDepositAccountDAO();
		AccountDAO accountDAO = Factory.getAccountDAO();
		
		DepositAccountBean account = null;
		
		LocalDate maturityDate = null;
		String msg = "", description = "";
		
		float intrestAmount = 0, beforeBalance, totalAmount;
		boolean exceptionOccured = false, isError = false, prematureClosing = false;
		
		long accountNo, bankAccountNo = 1452317983;
		
		int branchId = -1, actionType = 0, tenureMonths;
		
		try {
			branchId = (Integer) req.getSession(false).getAttribute("branch-id");
			
			actionType = Integer.parseInt(req.getParameter("action-type"));
			accountNo = Long.parseLong(req.getParameter("account-no"));
			
			if(Util.getNoOfDigits(accountNo) != 11 ) {
				isError = true;
				msg = "A/C no must be a 11 digit number";
			}
			
			account = depositAccountDAO.get(accountNo);
			
			// Get a/c no and display for confirmation
			if(!isError && actionType == 0) {
				
				if(account != null && account.getBranchId() == branchId) {
					req.setAttribute("actionType", 1);
					req.setAttribute("accountNo", account.getAccountNo());
					req.setAttribute("account", account);
				} else {
					isError = true;
					msg = "account does not exist in branch !!!";
				}
			}
			
			/* If A/C reached maturity -> close (or) request confirmation for 
				premature closing */
			if(!isError && actionType == 1) {
				
				maturityDate = account.getOpeningDate().plusMonths(account.getTenureMonths());
				
				// Premature closing, request for confirmation.
				if(LocalDate.now().isBefore(maturityDate)) {
					req.setAttribute("actionType", 2);
					req.setAttribute("accountNo", account.getAccountNo());
					prematureClosing = true;
				}				
			}
			
			
			/* Close deposit only when it has matured or after obtaining
			   confirmation. */
			if(!isError && ((actionType == 1 && !prematureClosing) || actionType == 2)) {
				
				if(actionType == 2)
					prematureClosing = true;
				
				// update logic to find intrest.
			
				intrestAmount = 20;
				totalAmount = account.getBalance() + intrestAmount;
				
				// 500 is debited as charges for premature closing.
				if(prematureClosing)
					totalAmount = totalAmount - 500;
				
				
				conn = Factory.getDataSource().getConnection();
				
				
				// Intrest is credited only to fd's on closing, for rd it is credited regularly.
				if(account.getTypeId() == DepositAccountType.FD.id) {
					// credit intrest amount to deposit account.
					beforeBalance = accountDAO.updateBalance(conn, bankAccountNo, 0, intrestAmount);
					
					description = "Intrest credit for deposit A/C: " + account.getAccountNo();
					
					transactionDAO.create(conn, 1, description, bankAccountNo, account.getAccountNo(), intrestAmount, false, true, beforeBalance, account.getBalance());	
					account.setBalance(account.getBalance() + intrestAmount);
				}
				
				// Debit charges for premature closing
				if(prematureClosing) {
					// credit 500 to bank account.
					beforeBalance = accountDAO.updateBalance(conn, bankAccountNo, 1, 500);
					description = "Premature closing charges on A/C: " + account.getAccountNo();
					transactionDAO.create(conn, 1, description, account.getAccountNo(), bankAccountNo, 500, true, false, account.getBalance(), beforeBalance);
				}
				
				// credit amount to payout account and close deposit.
				beforeBalance = accountDAO.updateBalance(conn, account.getPayoutAccountNo(), 1, totalAmount);
				
				description = DepositAccountType.getType(account.getTypeId()).toString() + " closing on A/C: " + account.getAccountNo();
				
				transactionDAO.create(conn, 1, description, account.getAccountNo(), account.getPayoutAccountNo(), totalAmount, true, true, account.getBalance(), beforeBalance);
				
				accountDAO.delete(conn, account.getAccountNo());
				
				out.println(Util.createNotification("Deposit closed and amount credited to payout account successfully", "success"));
				req.setAttribute("actionType", 0);
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
				out.println(Util.createNotification(msg, "danger"));
				doGet(req, res);
			} else {
				req.getRequestDispatcher("/jsp/employee/closeDeposit.jsp").include(req, res);
			}
			
			out.close();
		}
	}
}