package servlet.admin;

import java.io.IOException;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.BranchDAO;
import model.Branch;
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
			
			if(!isError && (branchDAO.get(baseBranchId) == null || branchDAO.get(targetBranchId) == null)) {
				isError = true;
				msg = "Invalid branches selected !!!";
			}
			
			if(!isError) {
				conn = Factory.getDataSource().getConnection();
				branchDAO.delete(conn, baseBranchId);
				// Update base branch accounts to target branch.
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