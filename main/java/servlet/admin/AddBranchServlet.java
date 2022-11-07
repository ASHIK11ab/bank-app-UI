package servlet.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.BranchDAO;
import dao.ManagerDAO;
import model.Address;
import model.Branch;
import model.user.Employee;
import util.Factory;
import util.Util;

public class AddBranchServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.getRequestDispatcher("/jsp/admin/addBranch.jsp").include(req, res);
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		BranchDAO branchDAO = Factory.getBranchDAO();
		ManagerDAO managerDAO = Factory.getManagerDAO();
		
		Connection conn = null;
		PrintWriter out = res.getWriter();
		
		Branch branch = null;
		Employee manager = null;
		Address address;
		
		String name, doorNo, street, city, state;
		int pincode;
		String managerName, managerEmail;
		long managerPhone;
		
		boolean isError = false, exceptionOccured = false;
		String msg = "";
		
		try {
			name = req.getParameter("name");
			doorNo = req.getParameter("door-no");
			street = req.getParameter("street");
			city = req.getParameter("city");
			state = req.getParameter("state");
			pincode = Integer.parseInt(req.getParameter("pincode"));
			
			managerName = req.getParameter("manager-name");
			managerEmail = req.getParameter("manager-email");
			managerPhone = Long.parseLong(req.getParameter("manager-phone"));
			
			address = new Address(doorNo, street, city, state, pincode);
			
			/* connection is created and passed to DAO's to prevent the usage
			of multiple connections for doing one operation */
			conn = Factory.getDataSource().getConnection();
			branch = branchDAO.create(conn, name, address);
			manager = managerDAO.create(conn, branch.getId(), managerName, managerEmail, managerPhone);
			branch.assignManager(manager);
			
			out.println(Util.createNotification("branch created successfully", "success"));
			req.setAttribute("branch", branch);
			req.setAttribute("displayManagerPassword", true);
			req.getRequestDispatcher("/jsp/admin/branch.jsp").include(req, res);
		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "Invalid input";
		} catch(SQLException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = e.getMessage();
		} finally {
			
            try {
                if(conn != null)
                    conn.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
            
            if(isError || exceptionOccured)
            	out.println(Util.createNotification(msg, "danger"));
            
			out.close();
		}
	}
}