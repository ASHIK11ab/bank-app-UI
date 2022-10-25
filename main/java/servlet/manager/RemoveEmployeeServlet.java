package servlet.manager;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.BranchDAO;
import dao.EmployeeDAO;
import dao.ManagerDAO;
import model.BranchBean;
import model.EmployeeBean;
import util.Factory;


public class RemoveEmployeeServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		EmployeeDAO employeeDAO = Factory.getEmployeeDAO();
		LinkedList<EmployeeBean> employees;
		int branchId;
		
		try {
			branchId = (Integer) req.getSession(false).getAttribute("branch-id"); 
			employees = employeeDAO.getAll(branchId);
			req.setAttribute("values", employees);
			req.getRequestDispatcher("/jsp/manager/removeEmployee.jsp").include(req, res);
		}  catch(NumberFormatException e) {
			res.setStatus(500);
			out.println("<h1>Internal error</h1>");
		} catch(ClassCastException e) {
			res.setStatus(500);
			out.println("<h1>Internal error</h1>");
		}  catch(SQLException e) {
			res.setStatus(500);
			out.println(e.getMessage());
		} finally {
			out.close();
		}
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		EmployeeDAO employeeDAO = Factory.getEmployeeDAO();
		PrintWriter out = res.getWriter();
		
		long employeeId = -1;
		int branchId = -1;
		boolean isDeletionSuccessfull = false;
		
		try {
			employeeId = Long.parseLong(req.getParameter("id"));
			branchId = (Integer) req.getSession(false).getAttribute("branch-id");
			
			isDeletionSuccessfull = employeeDAO.delete(employeeId, branchId);
			
			if(isDeletionSuccessfull)
				out.println("<div class='notification success'>" + "employee removed successfully" + "</div>");
			else
				out.println("<div class='notification danger'>" + "error removing employee" + "</div>");
			
		} catch(NumberFormatException e) {
			out.println("<div class='notification danger'>" + "invalid input" + "</div>");
		} catch(ClassCastException e) {
			out.println("<div class='notification danger'>" + "internal error" + "</div>");
		} catch(SQLException e) {
			out.println("<div class='notification danger'>" + e.getMessage() + "</div>");
		} finally {
            doGet(req, res);
			out.close();
		}
	}
}