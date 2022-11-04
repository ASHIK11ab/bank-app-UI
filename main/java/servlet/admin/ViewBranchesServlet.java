package servlet.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.BranchDAO;
import model.Branch;
import util.Factory;

public class ViewBranchesServlet extends HttpServlet {
	private BranchDAO branchDAO;
	
	public void init() {
		branchDAO = Factory.getBranchDAO();
	}
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		LinkedList<Branch> branches;
		
		try {
			branches = branchDAO.getAll();
			req.setAttribute("branches", branches);
			req.getRequestDispatcher("/jsp/admin/branches.jsp").forward(req, res);
		} catch(SQLException e) {
			res.setStatus(500);
			res.getWriter().println("<h1>Internal server error</h1>");
		}
	}
}