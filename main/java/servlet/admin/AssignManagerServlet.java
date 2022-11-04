package servlet.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.BranchDAO;
import dao.ManagerDAO;
import model.Branch;
import model.user.Employee;
import util.Factory;

public class AssignManagerServlet extends HttpServlet {
	private static final long serialVersionUID = 1533366166730604989L;
	private ManagerDAO managerDAO;
	private BranchDAO branchDAO;
	
	public void init() {
		managerDAO = Factory.getManagerDAO();
		branchDAO = Factory.getBranchDAO();
	}
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		LinkedList<Branch> branches;
		
		try {
			branches = branchDAO.getAll();
			req.setAttribute("values", branches);
			req.getRequestDispatcher("/jsp/admin/assignManager.jsp").include(req, res);
		} catch(SQLException e) {
			res.setStatus(500);
			res.getWriter().println("<h1>Internal error</h1>");
		}
		
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Connection conn = null;
		PrintWriter out = res.getWriter();
		String name, email;
		long phone;
		int branchId;
		
		Branch branch = null;
		Employee manager = null;
		long oldBranchManagerId = -1;
		
		try {
			conn = Factory.getDataSource().getConnection();
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
				
				managerDAO.delete(conn, oldBranchManagerId);
				manager = managerDAO.create(conn, branch.getId(), name, email, phone);
				out.println("<div class='notification success'>" + "manager assigned to branch" + "</div>");
				
				req.setAttribute("manager", manager);
				req.setAttribute("displayPassword", true);
				req.getRequestDispatcher("/jsp/admin/manager.jsp").include(req, res);
			}
			
		} catch(NumberFormatException e) {
			out.println("<div class='notification danger'>" + "invalid input" + "</div>");
			doGet(req, res);
		} catch(SQLException e) {
			out.println("<div class='notification danger'>" + e.getMessage() + "</div>");
			doGet(req, res);
		} finally {
			
            try {
                if(conn != null)
                    conn.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
            
			out.close();
		}
	}
}