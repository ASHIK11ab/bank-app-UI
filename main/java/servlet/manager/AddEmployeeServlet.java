package servlet.manager;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cache.AppCache;
import dao.EmployeeDAO;
import model.Branch;
import model.user.Employee;
import util.Factory;
import util.Util;


public class AddEmployeeServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.getRequestDispatcher("/jsp/manager/addEmployee.jsp").include(req, res);
	}
	
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		EmployeeDAO employeeDAO = Factory.getEmployeeDAO();
		PrintWriter out = res.getWriter();
		
		boolean exceptionOccured = false, isError = false;
		String name = "", email = "", msg = "";
		long phone = 0;
		int branchId;
		
		Branch branch = null;
		Employee employee = null;
		
		try {
			name = req.getParameter("employee-name");
			phone = Long.parseLong(req.getParameter("employee-phone"));
			email = req.getParameter("employee-email");
			branchId = (Integer) req.getSession(false).getAttribute("branch-id");
			branch = AppCache.getBranch(branchId);
			
			if(Util.getNoOfDigits(phone) != 10 ) {
				isError = true;
				msg = "Phone number should be 10 digits !!!";
			}
			
			if(!isError && email.length() > 30) {
				isError = true;
				msg = "Email should be less than 30 characters !!!";
			}
			
			if(!isError && name.length() > 20) {
				isError = true;
				msg = "Name should be less than 20 characters !!!";
			}
			
			if(!isError) {
				employee = employeeDAO.create(branchId, name, email, phone);
				
				synchronized (branch) {
					branch.setEmployeeCnt(branch.getEmployeeCnt() + 1);
				}
	
				req.setAttribute("employee", employee);
				req.setAttribute("displayPassword", true);
				out.println(Util.createNotification("Employee creation successfull", "success"));
				req.getRequestDispatcher("/jsp/manager/employee.jsp").include(req, res);
			}
			
		} catch(NumberFormatException e) {
			exceptionOccured = true;
			System.out.println(e.getMessage());
			msg = "Invalid input !!!";
		} catch(ClassCastException e) {
			exceptionOccured = true;
			System.out.println(e.getMessage());
			msg = "Internal error !!!";
		} catch(SQLException e) {
			exceptionOccured = true;
			System.out.println(e.getMessage());
			msg = e.getMessage();
		} finally {
            
			if(isError || exceptionOccured) {
				out.println(Util.createNotification(msg, "danger"));
				req.setAttribute("name", name);
				req.setAttribute("email", email);
				req.setAttribute("phone", phone);
				doGet(req, res);
			}
			out.close();
		}
	}
}