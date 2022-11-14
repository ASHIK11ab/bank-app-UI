package servlet.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.BranchDAO;
import dao.ManagerDAO;
import model.Branch;
import model.user.Employee;
import util.Factory;
import util.Util;

public class AssignManagerServlet extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Connection conn = null;
		
		PrintWriter out = res.getWriter();
		ManagerDAO managerDAO = Factory.getManagerDAO();
		BranchDAO branchDAO = Factory.getBranchDAO();
		
		boolean isError = false, exceptionOccured = false;
		String name = "", email = "", msg = "";
		long phone = 0;
		int branchId = -1;
		
		Branch branch = null;
		Employee manager = null;
		long oldBranchManagerId = -1;
		
		try {
			name = req.getParameter("manager-name");
			phone = Long.parseLong(req.getParameter("manager-phone"));
			email = req.getParameter("manager-email");
			branchId = Integer.parseInt(req.getParameter("branch-id"));
			
			if(Util.getNoOfDigits(phone) != 10 ) {
				isError = true;
				msg = "Phone number must be 10 digits !!!";
			}
			
			if(!isError && email.length() > 30) {
				isError = true;
				msg = "Email should be less than 30 characters !!!";
			}
			
			if(!isError && name.length() > 20) {
				isError = true;
				msg = "Manager name should be less than 20 characters !!!";
			}
			
			if(!isError) {
				
				branch = branchDAO.get(branchId);
			
				if(branch == null) {
					isError = true;
					msg = "Branch does not exist !!!";
				} else {
					conn = Factory.getDataSource().getConnection();
					
					oldBranchManagerId = branch.getManager().getId();
					// Remove old manager and assign new manager.
					
					managerDAO.delete(conn, oldBranchManagerId);
					manager = managerDAO.create(conn, branch.getId(), name, email, phone);
					branch.assignManager(manager);
					
					out.println(Util.createNotification("manager assigned to branch successfully", "success"));
					
					req.setAttribute("manager", manager);
					req.setAttribute("displayPassword", true);
					req.getRequestDispatcher("/jsp/admin/manager.jsp").include(req, res);
				}
			
			}
		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "Invalid input !!!";
		} catch(SQLException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = e.getMessage();
		} finally {
			
            try {
                if(conn != null)
                    conn.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
            
			if(isError || exceptionOccured) {
				// set the form fields and return same page.
				req.setAttribute("name", name);
				req.setAttribute("email", email);
				req.setAttribute("phone", phone);
				req.setAttribute("branchId", branchId);
				
				out.println(Util.createNotification(msg, "danger"));
				req.getRequestDispatcher("/jsp/admin/assignManager.jsp").include(req, res);
			}
			
			out.close();
		}
	}
}