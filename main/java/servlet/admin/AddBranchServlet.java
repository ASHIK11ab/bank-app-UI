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

public class AddBranchServlet extends HttpServlet {
	private static final long serialVersionUID = -4891823564773458976L;
	private BranchDAO branchDAO;
	private ManagerDAO managerDAO;
	
	public void init() {
		branchDAO = Factory.getBranchDAO();
		managerDAO = Factory.getManagerDAO();
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.getRequestDispatcher("/jsp/admin/addBranch.jsp").include(req, res);
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		Connection conn = null;
		PrintWriter out = res.getWriter();
		
		Branch branch = null;
		Employee manager = null;
		Address address;
		
		String name, doorNo, street, city, state;
		int pincode;
		
		String managerName, managerEmail;
		long managerPhone;
		
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
		} catch(NumberFormatException e) {
			out.println("<div class='notification danger'>" + "invalid input" + "</div>");
			doGet(req, res);
			out.close();
			return;
		}
		
		/* connection is created and passed to DAO's to prevent the usage
			of multiple connections for doing one operation */
		try {
			conn = Factory.getDataSource().getConnection();
			branch = branchDAO.create(conn, name, address);
			manager = managerDAO.create(conn, branch.getId(), managerName, managerEmail, managerPhone);
			branch.assignManager(manager);
			
			out.println("<div class='notification success'>" + "branch created successfully" + "</div>");
			req.setAttribute("branch", branch);
			req.setAttribute("displayManagerPassword", true);
			req.getRequestDispatcher("/jsp/admin/branch.jsp").include(req, res);
			
		} catch(SQLException e) {
			out.println("<div class='notification danger'>" + "error creating branch" + "</div>");
			doGet(req, res);
		} finally {
			
            try {
                if(conn != null)
                    conn.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
            
			out.close();
		}
	}
}