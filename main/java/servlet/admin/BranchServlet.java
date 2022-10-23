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
import model.BranchBean;
import util.Factory;


public class BranchServlet extends HttpServlet {
	private BranchDAO branchDAO;
	
	public void init() {
		branchDAO = Factory.getBranchDAO();
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		int branchId;
		BranchBean branch = null;
		
		try {
			branchId = Integer.parseInt(req.getPathInfo().substring(1));
			branch = branchDAO.get(branchId);
			
			if(branch == null) {
				res.setStatus(404);
				out.println("<h1>Page not found</h1>");
			}  else {
				req.setAttribute("branch", branch);
				req.getRequestDispatcher("/jsp/admin/branch.jsp").forward(req, res);
			}
			
		} catch(NumberFormatException e) {
			res.setStatus(500);
			out.println("<h1>Internal error</h1>");
		} catch(SQLException e) {
			res.setStatus(500);
			out.println("<h1>Internal error</h1>");		
		} finally {			
			out.close();
		}
	}
}