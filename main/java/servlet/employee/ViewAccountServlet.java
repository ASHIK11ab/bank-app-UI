package servlet.employee;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.RegularAccountDAO;
import model.account.RegularAccount;
import util.Factory;


public class ViewAccountServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.getRequestDispatcher("/jsp/employee/viewAccount.jsp").include(req, res);
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		RegularAccountDAO accountDAO = Factory.getRegularAccountDAO();
		RegularAccount account = null;
		long accountNo = -1;
		int branchId = -1;
		
		try {
			branchId = (Integer) req.getSession(false).getAttribute("branch-id");
			accountNo = Long.parseLong(req.getParameter("account-no"));
			account = accountDAO.get(accountNo);
			
			if(account != null && account.getBranchId() == branchId) {
				req.setAttribute("account", account);
			} else {
				out.println("<div class='notification danger'>" + "no account found !!!" + "</div>");
			}
		} catch(ClassCastException e) {
			out.println("<div class='notification danger'>" + "internal error !!!" + "</div>");
		} catch(NumberFormatException e) {
			out.println("<div class='notification danger'>" + "internal error !!!" + "</div>");
		} catch(SQLException e) {
			out.println("<div class='notification danger'>" + e.getMessage() + "</div>");
		} finally {
			doGet(req, res);
			out.close();
		}
	}
}