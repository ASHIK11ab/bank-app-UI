package servlet.manager;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cache.AppCache;
import model.Branch;
import util.Factory;
import util.Util;


public class DashboardServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		
		int branchId;
		
		Branch branch = null;
		LinkedList<Properties> stats = new LinkedList<Properties>();
		Properties prop;
		
		String queryMsg, status;
		
		boolean exceptionOccured = false;
		
		queryMsg = req.getParameter("msg");
		status = req.getParameter("status");
		
		if(queryMsg != null && status != null)
			out.println(Util.createNotification(queryMsg, status));
		
		try {
			branchId =  (Integer) req.getSession(false).getAttribute("branch-id"); 
			branch = AppCache.getBranch(branchId);
			
			prop = new Properties();
			prop.put("title", "Employees");
			prop.put("cnt", branch.getEmployeeCnt());
			stats.add(prop);
			
			prop = new Properties();
			prop.put("title", "Savings Accounts");
			prop.put("cnt", branch.getSavingsAccountCnt());			
			stats.add(prop);
			
			prop = new Properties();
			prop.put("title", "Current Accounts");
			prop.put("cnt", branch.getCurrentAccountCnt());
			stats.add(prop);
			
			prop = new Properties();
			prop.put("title", "Deposit Accounts");
			prop.put("cnt", branch.getDepositAccountCnt());
			stats.add(prop);
			
			req.setAttribute("stats", stats);
			req.getRequestDispatcher("/jsp/manager/dashboard.jsp").include(req, res);			
		} catch(ClassCastException e) {
			exceptionOccured = true;
			System.out.println(e.getMessage());
		} finally {
            
            if(exceptionOccured)
            	res.sendError(500);
		}
	}
}