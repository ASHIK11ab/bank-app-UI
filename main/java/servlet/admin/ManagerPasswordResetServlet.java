package servlet.admin;

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

import dao.ManagerDAO;
import model.EmployeeBean;
import util.Factory;

public class ManagerPasswordResetServlet extends HttpServlet {
	private static final long serialVersionUID = 223615731928695544L;
	private ManagerDAO managerDAO;
	
	public void init() {
		managerDAO = new ManagerDAO();
	}
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		LinkedList<EmployeeBean> managers;
		
		try {
			managers = managerDAO.getAll();
			req.setAttribute("managers", managers);
			req.getRequestDispatcher("/jsp/admin/managerPasswordReset.jsp").include(req, res);
		} catch(SQLException e) {
			res.setStatus(500);
			res.getWriter().println(e.getMessage());
		}
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		PrintWriter out = res.getWriter();
		long managerId = -1;
		String newPassword = "";
		int rowsAffectedCnt = 0;
		
		try {			
			managerId = Long.parseLong(req.getParameter("manager-id"));
			newPassword = req.getParameter("password");
			
			if(newPassword.length() < 8 || newPassword.length() > 15) {
				out.println("<div class='notification danger'>" + "Password should be within 8 to 15 characters !!!" + "</div>");
				doGet(req, res);
				out.close();
				return;
			}
			
			conn = Factory.getDataSource().getConnection();
			stmt = conn.prepareStatement("UPDATE manager SET password = ? WHERE id = ?");
			
			stmt.setString(1, newPassword);
			stmt.setLong(2, managerId);
			rowsAffectedCnt = stmt.executeUpdate();
			
			if(rowsAffectedCnt == 0) {
				out.println("<div class='notification danger'>" + "invalid manager id !!!" + "</div>");
			} else {
				out.println("<div class='notification success'>" + "password reset successfull" + "</div>");
			}
		} catch(NumberFormatException e) {
			out.println("<div class='notification danger'>" + "invalid input !!!" + "</div>");
		} catch(SQLException e) {
			out.println("<div class='notification danger'>" + "internal error" + "</div>");
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