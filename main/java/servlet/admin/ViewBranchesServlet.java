package servlet.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.BranchDAO;
import model.Branch;
import util.Factory;
import util.Util;

public class ViewBranchesServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		String queryMsg, status;
		
		queryMsg = req.getParameter("msg");
		status = req.getParameter("status");
		
		// Display notification if exists.
		if(queryMsg != null && status != null)
			out.println(Util.createNotification(queryMsg, status));
		
		BranchDAO branchDAO = Factory.getBranchDAO();
		Collection<Branch> branches = branchDAO.getAll();
		
		req.setAttribute("branches", branches);
		req.getRequestDispatcher("/jsp/admin/branches.jsp").include(req, res);
	}
}