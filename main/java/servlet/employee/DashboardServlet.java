package servlet.employee;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cache.AppCache;
import model.Branch;


public class DashboardServlet extends HttpServlet {
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {		
		int branchId;
		
		Branch branch = null;
		LinkedList<Properties> stats = new LinkedList<Properties>();
		Properties prop;
				
		boolean exceptionOccured = false;
		
		try {
			branchId =  (Integer) req.getSession(false).getAttribute("branch-id"); 
			branch = AppCache.getBranch(branchId);
			
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
			req.getRequestDispatcher("/jsp/employee/dashboard.jsp").include(req, res);			
		} catch(ClassCastException e) {
			exceptionOccured = true;
			System.out.println(e.getMessage());
		} finally {
            
            if(exceptionOccured)
            	res.sendError(500);
		}
	}
}