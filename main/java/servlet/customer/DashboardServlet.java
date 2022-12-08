package servlet.customer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.BeneficiaryType;
import constant.DepositAccountType;
import model.user.Customer;
import util.Factory;


public class DashboardServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		long id;
		Customer customer = null;
		LinkedList<Properties> stats = new LinkedList<Properties>();
		Properties prop;
				
		boolean exceptionOccured = false;
		
		try {
			id = (Long) req.getSession().getAttribute("id");
			customer = Factory.getCustomerDAO().get(id);
			prop = new Properties();
			prop.put("title", "Savings Accounts");
			prop.put("cnt", customer.getSavingsAccounts().size());			
			stats.add(prop);
			
			prop = new Properties();
			prop.put("title", "Current Account");
			prop.put("cnt", (customer.getCurrentAccount() != -1 ? "Yes" : "No"));
			stats.add(prop);
			
			prop = new Properties();
			prop.put("title", "Fixed Deposits (FD)");
			prop.put("cnt", customer.getDepositAccounts(DepositAccountType.FD).size());
			stats.add(prop);
			
			prop = new Properties();
			prop.put("title", "Recurring Deposits (RD)");
			prop.put("cnt", customer.getDepositAccounts(DepositAccountType.RD).size());
			stats.add(prop);
			
			prop = new Properties();
			prop.put("title", "Debit Cards");
			prop.put("cnt", customer.getCards().size());
			stats.add(prop);
			
			prop = new Properties();
			prop.put("title", "Own Bank Beneficiaries");
			prop.put("cnt", customer.getBeneficiaries(BeneficiaryType.OWN_BANK).size());
			stats.add(prop);
			
			prop = new Properties();
			prop.put("title", "Other Bank Beneficiaries");
			prop.put("cnt", customer.getBeneficiaries(BeneficiaryType.OTHER_BANK).size());
			stats.add(prop);
		} catch(SQLException e) {
			exceptionOccured = true;
		} finally {
			if(exceptionOccured)
				res.sendError(500);
			else {
				req.setAttribute("stats", stats);
				req.getRequestDispatcher("/jsp/customer/dashboard.jsp").include(req, res);
			}
				
		}
	}
}
