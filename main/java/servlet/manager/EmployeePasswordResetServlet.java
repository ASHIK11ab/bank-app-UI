package servlet.manager;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.EmployeeDAO;
import model.user.Employee;
import util.Factory;


public class EmployeePasswordResetServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		EmployeeDAO employeeDAO = Factory.getEmployeeDAO();
		LinkedList<Employee> employees;
		int branchId = -1;
		
		try {
			branchId = (Integer) req.getSession(false).getAttribute("branch-id");
			employees = employeeDAO.getAll(branchId);
			req.setAttribute("values", employees);
			req.getRequestDispatcher("/jsp/manager/employeePasswordReset.jsp").include(req, res);
		} catch(ClassCastException e) {
			res.setStatus(500);
			res.getWriter().println("<h1>Internal error</h1>");
		} catch(SQLException e) {
			res.setStatus(500);
			res.getWriter().println(e.getMessage());
		}
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		PrintWriter out = res.getWriter();
		long employeeId = -1;
		String newPassword = "";
		int rowsAffectedCnt = 0, branchId = -1;
		
		try {
			branchId = (Integer) req.getSession(false).getAttribute("branch-id");
			employeeId = Long.parseLong(req.getParameter("id"));
			newPassword = req.getParameter("password");
			
			if(newPassword.length() < 8 || newPassword.length() > 15) {
				out.println("<div class='notification danger'>" + "Password should be within 8 to 15 characters !!!" + "</div>");
				doGet(req, res);
				out.close();
				return;
			}
			
			conn = Factory.getDataSource().getConnection();
			stmt = conn.prepareStatement("UPDATE employee SET password = ? WHERE id = ? AND branch_id = ?");
			
			stmt.setString(1, newPassword);
			stmt.setLong(2, employeeId);
			stmt.setInt(3, branchId);
			rowsAffectedCnt = stmt.executeUpdate();
			
			if(rowsAffectedCnt == 0)
				out.println("<div class='notification danger'>" + "invalid employee selected !!!" + "</div>");
			else
				out.println("<div class='notification success'>" + "password reset successfull" + "</div>");
			
		} catch(ClassCastException e) {
			out.println("<div class='notification danger'>" + "internal error !!!" + "</div>");
		} catch(NumberFormatException e) {
			out.println("<div class='notification danger'>" + "invalid input !!!" + "</div>");
		} catch(SQLException e) {
			out.println("<div class='notification danger'>" + e.getMessage() + "</div>");
		} finally {
            try {
                if(stmt != null)
                    stmt.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
            
            try {
                if(conn != null)
                    conn.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
            
            doGet(req, res);
            out.close();
		}
	}
}