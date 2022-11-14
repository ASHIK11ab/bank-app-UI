package servlet.admin;

import java.io.IOException;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cache.AppCache;
import constant.AccountCategory;
import dao.AccountDAO;
import dao.BranchDAO;
import model.Branch;
import model.account.Account;
import model.account.DepositAccount;
import model.account.RegularAccount;
import util.Factory;
import util.Util;


public class MergeBranchServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		BranchDAO branchDAO = Factory.getBranchDAO();
		Collection<Branch> branches = branchDAO.getAll();
		req.setAttribute("branches", branches);
		req.getRequestDispatcher("/jsp/admin/mergeBranch.jsp").include(req, res);
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		Branch baseBranch = null, targetBranch = null;
		BranchDAO branchDAO = Factory.getBranchDAO();
		PrintWriter out = res.getWriter();
		
		boolean isError = false, exceptionOccured = false;
		String msg = "";
		int baseBranchId, targetBranchId;
		
		try {
			baseBranchId = Integer.parseInt(req.getParameter("base-branch-id"));
			targetBranchId = Integer.parseInt(req.getParameter("target-branch-id"));
			
			if(baseBranchId == targetBranchId) {
				isError = true;
				msg = "Base and target branches cannot be same !!!";
			}
			
			baseBranch = branchDAO.get(baseBranchId);
			targetBranch = branchDAO.get(targetBranchId);
			
			if(!isError && (baseBranch == null || targetBranch == null)) {
				isError = true;
				msg = "Invalid branches selected !!!";
			}
			
			if(!isError) {
				synchronized (baseBranch) {
					synchronized (targetBranch) {
						conn = Factory.getDataSource().getConnection();
						stmt = conn.prepareStatement("UPDATE account SET branch_id = ? WHERE branch_id = ?");
						stmt.setInt(1, targetBranchId);
						stmt.setInt(2, baseBranchId);
						stmt.executeUpdate();
						
						// Update cached base branch accounts to target branch.
						for(RegularAccount account : baseBranch.getSavingsAccounts()) {
							synchronized(account) {					
								account.setBranchId(targetBranchId);
								baseBranch.removeAccount(account.getAccountNo());
								targetBranch.addAccount(AccountCategory.REGULAR, account.getTypeId(), account);
							}
						}
						
						for(RegularAccount account : baseBranch.getCurrentAccounts()) {
							synchronized(account) {					
								account.setBranchId(targetBranchId);
								baseBranch.removeAccount(account.getAccountNo());
								targetBranch.addAccount(AccountCategory.REGULAR, account.getTypeId(), account);
							}
						}
						
						for(DepositAccount account : baseBranch.getDepositAccounts()) {
							synchronized(account) {					
								account.setBranchId(targetBranchId);
								baseBranch.removeAccount(account.getAccountNo());
								targetBranch.addAccount(AccountCategory.DEPOSIT, account.getTypeId(), account);
							}
						}
					
						branchDAO.delete(conn, baseBranchId);
						// remove branch from cache.
						AppCache.getBank().removeBranch(baseBranchId);
					}
				}
				
				res.sendRedirect(String.format("/bank-app/admin/branches?msg=branches merged successfully&status=success"));
			}
		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "Invalid input !!!";
		} catch(SQLException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = e.getMessage();
		}finally {
			
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