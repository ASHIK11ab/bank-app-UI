package servlet.admin;

import java.io.IOException;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.BranchDAO;
import model.BranchBean;
import util.Factory;


public class MergeBranchServlet extends HttpServlet {
	private BranchDAO branchDAO;
	
	public void init() {
		branchDAO = Factory.getBranchDAO();
	}
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		LinkedList<BranchBean> branches = null;
		
		try {
			branches = branchDAO.getAll();
			req.setAttribute("branches", branches);
			req.getRequestDispatcher("/jsp/admin/mergeBranch.jsp").include(req, res);
		} catch(SQLException e) {
			res.setStatus(500);
			res.getWriter().println("<h1>Internal error</h1>");
		}
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Connection conn = null;
		PrintWriter out = res.getWriter();
		int baseBranchId, targetBranchId;
		
		try {
			baseBranchId = Integer.parseInt(req.getParameter("base-branch-id"));
			targetBranchId = Integer.parseInt(req.getParameter("target-branch-id"));
		} catch(NumberFormatException e) {
			out.println("<div class='notification danger'>" + "invalid input" + "</div>");
			doGet(req, res);
			out.close();
			return;
		}
		
		if(baseBranchId == targetBranchId) {
			out.println("<div class='notification danger'>" + "Invalid branches selected" + "</div>");
			doGet(req, res);
			out.close();
			return;
		}
		
		try {
			conn = Factory.getDataSource().getConnection();
			branchDAO.delete(conn, baseBranchId);
			// Update base branch accounts to target branch.
			out.println("<div class='notification success'>" + "branches merged successfully" + "</div>");
		} catch(SQLException e) {
			out.println("<div class='notification danger'>" + e.getMessage() + "</div>");
		} finally {
			
            try {
                if(conn != null)
                    conn.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
            
			doGet(req, res);
			out.close();
		}
	}
}