package servlet.admin;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import cache.AppCache;
import dao.BranchDAO;
import dao.IntegratedBankDAO;
import model.Branch;
import util.Factory;


public class DashboardServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		int branchesCnt = -1, integratedBanksCnt = -1, customerCnt;
		long savingsAccountCnt = 0, currentAccountCnt = 0, depositAccountCnt = 0, employeeCnt = 0;
		
		BranchDAO branchDAO = Factory.getBranchDAO();
		IntegratedBankDAO integratedBankDAO = Factory.getIntegratedBankDAO();
		
		LinkedList<Properties> stats = new LinkedList<Properties>();
		Properties prop;
			
		branchesCnt = branchDAO.getAll().size();
		integratedBanksCnt = integratedBankDAO.getAll().size();
		
		prop = new Properties();
		prop.put("title", "Branches");
		prop.put("cnt", branchesCnt);
		stats.add(prop);
		
		prop = new Properties();
		prop.put("title", "Integrated Banks");
		prop.put("cnt", integratedBanksCnt);			
		stats.add(prop);
		
		prop = new Properties();
		prop.put("title", "Customers");
		prop.put("cnt", AppCache.getBank().getCustomerCnt());			
		stats.add(prop);
		
		for(Branch branch: branchDAO.getAll()) {
			employeeCnt += branch.getEmployeeCnt();
			savingsAccountCnt += branch.getSavingsAccountCnt();
			currentAccountCnt += branch.getCurrentAccountCnt();
			depositAccountCnt += branch.getDepositAccountCnt();
		}
		
		prop = new Properties();
		prop.put("title", "Regular Accounts");
		prop.put("cnt", savingsAccountCnt + currentAccountCnt);			
		stats.add(prop);
		
		prop = new Properties();
		prop.put("title", "Savings Accounts");
		prop.put("cnt", savingsAccountCnt);			
		stats.add(prop);
		
		prop = new Properties();
		prop.put("title", "Current Accounts");
		prop.put("cnt", currentAccountCnt);
		stats.add(prop);
		
		prop = new Properties();
		prop.put("title", "Deposit Accounts");
		prop.put("cnt", depositAccountCnt);
		stats.add(prop);
		
		req.setAttribute("stats", stats);
		req.getRequestDispatcher("/jsp/admin/dashboard.jsp").forward(req, res);			
	}
}