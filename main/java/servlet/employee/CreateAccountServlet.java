package servlet.employee;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.RegularAccountType;
import dao.CustomerDAO;
import dao.RegularAccountDAO;
import model.user.Customer;
import model.account.RegularAccount;
import util.Factory;

public class CreateAccountServlet extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		PrintWriter out = res.getWriter();
		CustomerDAO customerDAO = Factory.getCustomerDAO();
		RegularAccountDAO accountDAO = Factory.getRegularAccountDAO();
		
		Customer customer = null;
		RegularAccount account = null;
		RegularAccountType type;
		String msg = "", customerName;
		boolean isEligibleForAccount = true;
		long customerId;
		int accountType, cardType, branchId, actionType;
		
		try {
			actionType = Integer.parseInt(req.getParameter("action-type"));
			customerId = Long.parseLong(req.getParameter("customer-id"));
        	accountType = Integer.parseInt(req.getParameter("account-type"));
        	cardType = Integer.parseInt(req.getParameter("card-type"));
        	branchId = (Integer) req.getSession(false).getAttribute("branch-id");
        	
			// Display customer details and ask for confirmation.
			if(actionType == 0) {
				customer = customerDAO.get(customerId);
				
				// Pass data.
				req.setAttribute("accountType", accountType);
				req.setAttribute("cardType", cardType);
				
				if(customer == null) {
					out.println("<div class='notification danger'>" + "invalid customer id !!!" + "</div>");
				} else {
					req.setAttribute("customer", customer);
				}
				
				req.getRequestDispatcher("/jsp/employee/createAccount.jsp").include(req, res);

			} else {
				
				conn = Factory.getDataSource().getConnection();
				
				type = RegularAccountType.getType(accountType);
				
                if(type == RegularAccountType.CURRENT) {
                    stmt = conn.prepareStatement("SELECT ra.account_no FROM regular_account ra LEFT JOIN account a ON ra.account_no = a.account_no WHERE a.customer_id = ? AND ra.type_id = ?");
                    stmt.setLong(1, customerId);
                    stmt.setInt(2, accountType);
                    rs = stmt.executeQuery();

                    // Customer aldready has a current account.
                    if(rs.next()) {
                        isEligibleForAccount = false;
                        msg = "Customer aldready has a current account in bank !!!";
                    }
                } else {
                    stmt = conn.prepareStatement("SELECT ra.account_no FROM regular_account ra LEFT JOIN account a ON ra.account_no = a.account_no WHERE a.customer_id = ? AND a.branch_id = ? AND ra.type_id = ?");
                    stmt.setLong(1, customerId);
                    stmt.setInt(2, branchId);
                    stmt.setInt(3, accountType);
                    rs = stmt.executeQuery();

                    // Customer aldreay has a savings account in this branch.
                    if(rs.next()) {
                        isEligibleForAccount = false;
                        msg = "Customer aldready has a savings account in this branch !!!";
                    }
                }
                
                if(isEligibleForAccount) {
                	customerName = req.getParameter("customer-name");
                	account = accountDAO.create(conn, customerId, customerName,
													branchId, type, cardType, null);
					req.setAttribute("account", account);
					req.getRequestDispatcher("/jsp/employee/accountCreationSuccess.jsp").forward(req, res);
                } else {
    				req.setAttribute("msg", msg);
    				req.getRequestDispatcher("/jsp/employee/accountCreationFailure.jsp").forward(req, res);
                }
			}
			
		} catch(NumberFormatException e) {
			out.println("<div class='notification danger'>" + "internal error !!!" + "</div>");
		} catch(ClassCastException e) {
			out.println("<div class='notification danger'>" + "internal error !!!" + "</div>");
		} catch(SQLException e) {
			out.println("<div class='notification danger'>" + e.getMessage() + "</div>");
		} finally {
			
			try {
				if(rs != null)
					rs.close();
			} catch(SQLException e) { System.out.println(e.getMessage()); }
			
			try {
				if(stmt != null)
					stmt.close();
			} catch(SQLException e) { System.out.println(e.getMessage()); }
			
			try {
				if(conn != null)
					conn.close();
			} catch(SQLException e) { System.out.println(e.getMessage()); }
			
			out.close();
		}
	}	
}