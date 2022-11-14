package servlet.admin;

import java.io.IOException;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.Role;
import dao.CustomerDAO;
import dao.ManagerDAO;
import model.user.Customer;
import model.user.Employee;
import util.Factory;
import util.Util;


public class AdminManagerServlet extends HttpServlet {
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		
		Employee manager = null;
		ManagerDAO managerDAO = Factory.getManagerDAO();
		
		String path = req.getPathInfo(), msg = "", action = "";
		String queryMsg = "", status = "";
		String[] result;
		boolean isError = false, exceptionOccured = false;
		int branchId;
		long managerId;
		
		queryMsg = req.getParameter("msg");
		status = req.getParameter("status");
		
		if(queryMsg != null && status != null)
			out.println(Util.createNotification(queryMsg, status));
		
		try {
			path = path.substring(1);
			result = path.split("/");
			branchId = Integer.parseInt(req.getParameter("branch-id"));
			
			managerId = Long.parseLong(result[0]);
			action = result[1];
			
			manager = managerDAO.get(managerId, branchId);

			if(manager == null) {
				isError = true;
				msg = "Manager does not exist !!!";
			} else {
				req.setAttribute("manager", manager);
				
				switch(action) {
					case "view": req.getRequestDispatcher("/jsp/admin/manager.jsp").include(req, res); break;
					
					case "password-reset": 
											req.setAttribute("id", managerId);
											req.setAttribute("name", manager.getName());
											req.setAttribute("forRole", Role.MANAGER);
											req.setAttribute("branchId", branchId);
											req.setAttribute("redirectURI", String.format("/bank-app/admin/managers/%d/view", managerId));
											req.getRequestDispatcher("/jsp/components/resetPassword.jsp").include(req, res); break;
					default:
							isError = true;
							msg = "Page not found !!!";
				}
			}
			
		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			isError = true;
			msg = "Invalid input !!!";
		} catch(IndexOutOfBoundsException e) {
			System.out.println(e.getMessage());
			isError = true;
			msg = "Page not found !!!";
		} catch(SQLException e) {
			isError = true;
			msg = e.getMessage();
		} finally {
			
			if(isError || exceptionOccured)
				res.sendRedirect(String.format("/bank-app/admin/managers?msg=%s&status=danger", msg));
			out.close();
		}
	}
}