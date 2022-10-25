package servlet.manager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.Factory;


public class DashboardServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Connection conn = null;
		PreparedStatement stmt1 = null, stmt2 = null, stmt3 = null;
		ResultSet rs1 = null, rs2 = null, rs3 = null;
		
		long employeeCnt = -1, regularAccountCnt = -1, depositAccountCnt = -1;
		int branchId = -1;
		LinkedList<Properties> stats = new LinkedList<Properties>();
		Properties prop;
		
		try {
			branchId =  (Integer) req.getSession(false).getAttribute("branch-id"); 
			conn = Factory.getDataSource().getConnection();
			stmt1 = conn.prepareStatement("SELECT COUNT(*) FROM employee WHERE branch_id = ?");
			stmt2 = conn.prepareStatement("SELECT COUNT(*) FROM regular_account ra LEFT JOIN account a ON ra.account_no = a.account_no WHERE a.branch_id = ?");
			stmt3 = conn.prepareStatement("SELECT COUNT(*) FROM deposit_account da LEFT JOIN account a ON da.account_no = a.account_no WHERE a.branch_id = ?");
			
			stmt1.setInt(1, branchId);
			rs1 = stmt1.executeQuery();
			if(rs1.next())
				employeeCnt = rs1.getLong(1);
			
			prop = new Properties();
			prop.put("title", "Employees");
			prop.put("cnt", employeeCnt);
			stats.add(prop);
			
			stmt2.setInt(1, branchId);
			rs2 = stmt2.executeQuery();
			if(rs2.next())
				regularAccountCnt = rs2.getLong(1);
			
			prop = new Properties();
			prop.put("title", "Regular Accounts");
			prop.put("cnt", regularAccountCnt);			
			stats.add(prop);
			
			stmt3.setInt(1, branchId);
			rs3 = stmt3.executeQuery();
			if(rs3.next())
				depositAccountCnt = rs3.getLong(1);
			
			prop = new Properties();
			prop.put("title", "Deposit Accounts");
			prop.put("cnt", depositAccountCnt);
			stats.add(prop);
			
			req.setAttribute("stats", stats);
			req.getRequestDispatcher("/jsp/manager/dashboard.jsp").forward(req, res);			
		} catch(ClassCastException e) {
			res.setStatus(500);
			res.getWriter().println("<h1>Internal error</h1>");
		} catch(SQLException e) {
			res.setStatus(500);
			res.getWriter().println("<h1>Internal error</h1>");
		} finally {
            try {
                if(rs1 != null )
                    rs1.close();
                if(rs2 != null )
                    rs2.close();
                if(rs3 != null )
                    rs3.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
            
            try {
                if(stmt1 != null)
                    stmt1.close();
                if(stmt2 != null)
                    stmt2.close();
                if(stmt3 != null)
                    stmt3.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
            
            try {
                if(conn != null)
                    conn.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
		}
	}
}