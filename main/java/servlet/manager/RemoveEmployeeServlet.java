package servlet.manager;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cache.AppCache;
import dao.BranchDAO;
import dao.EmployeeDAO;
import dao.ManagerDAO;
import model.Branch;
import model.user.Employee;
import util.Factory;
import util.Util;


public class RemoveEmployeeServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		EmployeeDAO employeeDAO = Factory.getEmployeeDAO();
		Collection<Employee> employees;
		int branchId;
		boolean exceptionOccured = false;
		
		try {
			branchId = (Integer) req.getSession(false).getAttribute("branch-id"); 
			employees = employeeDAO.getAll(branchId);
			req.setAttribute("employees", employees);
			req.getRequestDispatcher("/jsp/manager/removeEmployee.jsp").include(req, res);
		}  catch(ClassCastException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
		} catch(SQLException e) {
			exceptionOccured = true;
		} finally {
			if(exceptionOccured)
				res.sendError(500);
		}
	}
	
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		EmployeeDAO employeeDAO = Factory.getEmployeeDAO();
		PrintWriter out = res.getWriter();
		
		Branch branch = null;
		Employee employee = null;
		String msg = "";
		long employeeId = -1;
		int branchId = -1;
		boolean exceptionOccured = false;
		
		try {
			employeeId = Long.parseLong(req.getParameter("id"));
			branchId = (Integer) req.getSession(false).getAttribute("branch-id");
			branch = AppCache.getBranch(branchId);
			
			employee = employeeDAO.get(employeeId, branchId);
			
			if(employee != null) {
				synchronized (employee) {
					employeeDAO.delete(employeeId, branchId);
					synchronized (branch) {
						branch.setEmployeeCnt(branch.getEmployeeCnt() - 1);
					}
					out.println(Util.createNotification("Employee removed successfully", "success"));
				}
			} else {
				out.println(Util.createNotification("employee does not exist !!!", "danger"));
			}
			
		} catch(ClassCastException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "internal error !!!";
		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "Invalid input !!!";
		} catch(SQLException e) {
			exceptionOccured = true;
			msg = e.getMessage();
		} finally {
			if(exceptionOccured)
				out.println(Util.createNotification(msg, "danger"));
			
			doGet(req, res);
		}
	}
}