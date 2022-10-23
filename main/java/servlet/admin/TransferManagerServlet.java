package servlet.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import dao.BranchDAO;
import model.BranchBean;
import util.Factory;


public class TransferManagerServlet extends HttpServlet {
	private BranchDAO branchDAO;
	
	
	public void init() {
		branchDAO = Factory.getBranchDAO();
	}
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		LinkedList<BranchBean> branches;
		
		try {
			branches = branchDAO.getAll();
			req.setAttribute("branches", branches);
			req.getRequestDispatcher("/jsp/admin/transferManager.jsp").include(req, res);
		} catch(SQLException e) {
			res.setStatus(500);
			res.getWriter().println("<h1>Internal error</h1>");
		}	
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		PrintWriter out = res.getWriter();
		BranchBean firstBranch = null, secondBranch = null;

		int firstBranchId = -1, secondBranchId = -1;
		long firstBranchManagerId = -1, secondBranchManagerId = -1;
		boolean isError = false;
		
		try {
			firstBranchId = Integer.parseInt(req.getParameter("first-branch-id"));
			secondBranchId = Integer.parseInt(req.getParameter("second-branch-id"));
			
			// Cannot transfer to same branch.
			if(firstBranchId == secondBranchId) {
				out.println("<div class='notification danger'>" + "cannot transfer to same branch" + "</div>");
				isError = true;
			}
			
		} catch(NumberFormatException e) {			
			out.println("<div class='notification danger'>" + "invalid input" + "</div>");
			isError = true;
		}
		
		// Return and render page.
		if(isError) {
			doGet(req, res);
			out.close();
			return;
		}
		
		try {
			conn = Factory.getDataSource().getConnection();
            stmt = conn.prepareStatement("UPDATE manager SET branch_id = ? WHERE id = ?");
            
            firstBranch = branchDAO.get(firstBranchId);
            secondBranch = branchDAO.get(secondBranchId);
            
            // Handle invalid branch id's
            if(firstBranch == null || secondBranch == null) {
				out.println("<div class='notification danger'>" + "invalid branches !!!" + "</div>");
            } else {
            	firstBranchManagerId = firstBranch.getManager().getId();
            	secondBranchManagerId = secondBranch.getManager().getId();
            	
            	// update manager's branch id's
                stmt.setInt(1, secondBranchId);
                stmt.setLong(2, firstBranchManagerId);
                stmt.executeUpdate();

                stmt.setInt(1, firstBranchId);
                stmt.setLong(2, secondBranchManagerId);
                stmt.executeUpdate();
                
    			out.println("<div class='notification success'>" + "managers transfer successfull" + "</div>");
            }
		} catch(SQLException e) {
			out.println("<div class='notification danger'>" + "managers transfer failed" + "</div>");
		} finally {

            try {
                if(stmt != null)
                    stmt.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }

            try {
                if(conn != null)
                    conn.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
            
            doGet(req, res);
            out.close();
        }
	}
}