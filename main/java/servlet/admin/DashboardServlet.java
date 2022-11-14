package servlet.admin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.Factory;


public class DashboardServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Connection conn = null;
		Statement stmt1 = null, stmt2 = null;
		ResultSet rs1 = null, rs2 = null;
		
		long branchesCnt = -1, integratedBanksCnt = -1;
		LinkedList<Properties> stats = new LinkedList<Properties>();
		Properties prop;
		
		try {
			conn = Factory.getDataSource().getConnection();
			stmt1 = conn.createStatement();
			stmt2 = conn.createStatement();
			
			rs1 = stmt1.executeQuery("SELECT COUNT(*) FROM branch");
			if(rs1.next())
				branchesCnt = rs1.getLong(1);
			
			prop = new Properties();
			prop.put("title", "Branches");
			prop.put("cnt", branchesCnt);
			stats.add(prop);
			
			rs2 = stmt2.executeQuery("SELECT COUNT(*) FROM banks");
			if(rs2.next())
				integratedBanksCnt = rs2.getLong(1);
			
			
			prop = new Properties();
			prop.put("title", "Integrated Banks");
			prop.put("cnt", integratedBanksCnt);			
			stats.add(prop);
			
			req.setAttribute("stats", stats);
			req.getRequestDispatcher("/jsp/admin/dashboard.jsp").forward(req, res);			
		} catch(SQLException e) {
			res.setStatus(500);
			res.getWriter().println("<h1>Internal error</h1>");
		} finally {
            try {
                if(rs1 != null )
                    rs1.close();
                if(rs2 != null )
                    rs2.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
            
            try {
                if(stmt1 != null)
                    stmt1.close();
                if(stmt2 != null)
                    stmt2.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
            
            try {
                if(conn != null)
                    conn.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
		}
		
	}
}