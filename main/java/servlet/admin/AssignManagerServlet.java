package servlet.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.BranchDAO;
import dao.ManagerDAO;
import model.BranchBean;
import util.Factory;

public class AssignManagerServlet extends HttpServlet {
	private ManagerDAO managerDAO;
	private BranchDAO branchDAO;
	
	public void init() {
		managerDAO = Factory.getManagerDAO();
		branchDAO = Factory.getBranchDAO();
	}
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		LinkedList<BranchBean> branches;
		
		try {
			branches = branchDAO.getAll();
			req.setAttribute("branches", branches);
			req.getRequestDispatcher("/jsp/admin/assignManager.jsp").include(req, res);
		} catch(SQLException e) {
			res.setStatus(500);
			res.getWriter().println("<h1>Internal error</h1>");
		}
		
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		String name, email;
		long phone;
		int branchId;
		
		BranchBean branch = null;
		long oldBranchManagerId = -1;
		
		try {
			name = req.getParameter("manager-name");
			phone = Long.parseLong(req.getParameter("manager-phone"));
			email = req.getParameter("manager-email");
			branchId = Integer.parseInt(req.getParameter("branch-id"));
			
			branch = branchDAO.get(branchId);
			
			if(branch == null) {
				out.println("<div class='notification danger'>" + "invalid branch !!!" + "</div>");
			} else {
				oldBranchManagerId = branch.getManager().getId();
				// Remove old manager and assign new manager.
				
				managerDAO.delete(oldBranchManagerId);
				managerDAO.create(branchId, name, email, phone);
			}
			out.println("<div class='notification success'>" + "manager assigned to branch" + "</div>");
			
		} catch(NumberFormatException e) {
			out.println("<div class='notification danger'>" + "invalid input" + "</div>");
		} catch(SQLException e) {
			out.println("<div class='notification danger'>" + e.getMessage() + "</div>");
		} finally {
			doGet(req, res);
			out.close();
		}
	}
}