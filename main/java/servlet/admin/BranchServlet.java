package servlet.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.BranchDAO;
import model.Branch;
import util.Factory;
import util.Util;


public class BranchServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		BranchDAO branchDAO = Factory.getBranchDAO();
		Branch branch = null;
		
		boolean isError = false, exceptionOccured = false;
		String path = req.getPathInfo(), action = "", msg = "", redirectURI, queryMsg, status;
		String [] result;
		int branchId;
		
		queryMsg = req.getParameter("msg");
		status = req.getParameter("status");
		
		// Display notification if exists.
		if(queryMsg != null && status != null)
			out.println(Util.createNotification(queryMsg, status));
		
		// redirect to all branches page.
		if(path == null || path.equals("/")) {
			res.sendRedirect("/bank-app/admin/branches");
			return;
		}
		
		try {
			path = path.substring(1);
			result = path.split("/");
						
			branchId = Integer.parseInt(result[0]);
			action = result[1];
			
			branch = branchDAO.get(branchId);
			
			if(branch == null) {
				isError = true;
				msg = "Branch does not exist !!!";
			}
			
			if(!isError) {
				req.setAttribute("branch", branch);
				switch(action) {
					case "view": 
								req.getRequestDispatcher("/jsp/admin/branch.jsp").include(req, res);
								break;
					case "assign-manager": 
											req.setAttribute("branchId", branchId);
											req.getRequestDispatcher("/jsp/admin/assignManager.jsp").forward(req, res);
											break;
					case "edit":
								req.setAttribute("branchId", branchId);
								req.setAttribute("branchName", branch.getName());
								req.setAttribute("inputBranchName", branch.getName());
								req.setAttribute("address", branch.getAddress());
								req.getRequestDispatcher("/jsp/admin/editBranch.jsp").forward(req, res);
								break;
					default: isError = true;
							 msg = "page not found !!!";
							 break;
				}
			}
			
		} catch(IndexOutOfBoundsException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "page not found !!!";
		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "Page not found !!!";
		} finally {			
			
			if(isError || exceptionOccured) {
				redirectURI = String.format("/bank-app/admin/branches?msg=%s&status=danger", msg);
				res.sendRedirect(redirectURI);
			}
			
			out.close();
		}
	}
}