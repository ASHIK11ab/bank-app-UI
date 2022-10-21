package servlet.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.BranchDAO;
import model.AddressBean;

public class AddBranchServlet extends HttpServlet {
	private BranchDAO branchDAO;
	
	public void init() {
		branchDAO = new BranchDAO();
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.getRequestDispatcher("/jsp/admin/addBranch.jsp").include(req, res);
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		AddressBean address;
		
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
			
			address = new AddressBean();
            address.setDoorNo(doorNo);
            address.setStreet(street);
            address.setCity(city);
            address.setState(state);
            address.setPincode(pincode);
		} catch(NumberFormatException e) {
			out.println("<div class='notification danger'>" + "invalid input" + "</div>");
			doGet(req, res);
			out.close();
			return;
		}
		
		try {
			branchDAO.create(name, address, managerName, managerEmail, managerPhone);
			out.println("<div class='notification success'>" + "branch created successfully" + "</div>");
		} catch(SQLException e) {
			out.println("<div class='notification danger'>" + "error creating branch" + "</div>");
		} finally {
			doGet(req, res);
			out.close();
		}
	}
}