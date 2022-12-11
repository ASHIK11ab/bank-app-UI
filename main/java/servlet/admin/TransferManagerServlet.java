package servlet.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.BranchDAO;
import model.Branch;
import model.user.Employee;
import util.Factory;
import util.Util;


public class TransferManagerServlet extends HttpServlet {
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setAttribute("values", Factory.getBranchDAO().getAll());
		req.setAttribute("actionType", 0);
		req.getRequestDispatcher("/jsp/admin/transferManagers.jsp").include(req, res);
	}
	
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		BranchDAO branchDAO = Factory.getBranchDAO();
		PrintWriter out = res.getWriter();
		
		Employee firstBranchManager = null, secondBranchManager = null;
		Branch firstBranch = null, secondBranch = null;

		int firstBranchId = -1, secondBranchId = -1, actionType;
		boolean isError = false, exceptionOccured = false;
		String msg = "";
		
		try {
			firstBranchId = Integer.parseInt(req.getParameter("first-branch-id"));
			secondBranchId = Integer.parseInt(req.getParameter("second-branch-id"));
			actionType = Integer.parseInt(req.getParameter("actionType"));
			
			if(actionType != 0 && actionType != 1) {
				isError = true;
				msg = "Invalid action !!!";
			}
			
			// Cannot transfer to same branch.
			if(!isError && firstBranchId == secondBranchId) {
				isError = true;
				msg = "cannot transfer to same branch";
			}
			
			if(!isError) {
	            firstBranch = branchDAO.get(firstBranchId);
	            secondBranch = branchDAO.get(secondBranchId);
	            
	            // Handle invalid branch id's
	            if(firstBranch == null || secondBranch == null) {
	            	isError = true;
					msg = "invalid branches !!!";
	            }
			}
			
			if(!isError && actionType == 0) {
				req.setAttribute("baseBranchManager", firstBranch.getManager());
				req.setAttribute("targetBranchManager", secondBranch.getManager());
				req.setAttribute("actionType", 1);
				req.getRequestDispatcher("/jsp/admin/transferManagers.jsp").include(req, res);
			}
			
			// Perform transfer
			if(!isError && actionType == 1) {
				conn = Factory.getDataSource().getConnection();
	            stmt = conn.prepareStatement("UPDATE manager SET branch_id = ? WHERE id = ?");
	            
	            synchronized (firstBranch) {
					synchronized (secondBranch) {
		            	firstBranchManager = firstBranch.getManager();
		            	secondBranchManager = secondBranch.getManager();
		            	
		            	// update manager's branch id's
		                stmt.setInt(1, secondBranchId);
		                stmt.setLong(2, firstBranchManager.getId());
		                stmt.executeUpdate();
		
		                stmt.setInt(1, firstBranchId);
		                stmt.setLong(2, secondBranchManager.getId());
		                stmt.executeUpdate();
		                
		    			// remove managers from cache.
		                firstBranchManager.setBranchId(secondBranchId);
		                firstBranchManager.setBranchName(secondBranch.getName());
		                
		                secondBranchManager.setBranchId(firstBranchId);
		                secondBranchManager.setBranchName(firstBranch.getName());
		                
		                firstBranch.assignManager(secondBranchManager);
		                secondBranch.assignManager(firstBranchManager);		                
					}
	            }
	            
	            out.println(Util.createNotification("Managers transfer successfull", "success"));
	            doGet(req, res);
			}
		}  catch(NumberFormatException e) {			
			System.out.println(e.getMessage());
			isError = true;
			msg = "invalid input";
		} catch(SQLException e) {
			exceptionOccured = true;
			msg = e.getMessage();
		} finally {

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
                doGet(req, res);
            }

            out.close();
        }
	}
}