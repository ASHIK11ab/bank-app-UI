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

import cache.AppCache;
import util.Factory;


public class DashboardServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		long branchesCnt = -1, integratedBanksCnt = -1;
		LinkedList<Properties> stats = new LinkedList<Properties>();
		Properties prop;
			
		branchesCnt = AppCache.getBank().getBranches().size();
		integratedBanksCnt = AppCache.getBank().getIntegratedBanks().size();
		
		prop = new Properties();
		prop.put("title", "Branches");
		prop.put("cnt", branchesCnt);
		stats.add(prop);
		
		prop = new Properties();
		prop.put("title", "Integrated Banks");
		prop.put("cnt", integratedBanksCnt);			
		stats.add(prop);
		
		req.setAttribute("stats", stats);
		req.getRequestDispatcher("/jsp/admin/dashboard.jsp").forward(req, res);			
	}
}