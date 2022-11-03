package servlet.employee;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.RegularAccountType;
import dao.CustomerDAO;
import dao.RegularAccountDAO;
import model.Address;
import model.user.Customer;
import model.account.RegularAccount;
import util.Factory;
import util.Util;


public class CreateCustomerServlet extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        PrintWriter out = res.getWriter();
        Connection conn = null;
        
        Customer customer = null;
        Address address = null;
        RegularAccount account = null;
        
        RegularAccountType type;
        
        CustomerDAO customerDAO = Factory.getCustomerDAO();
        RegularAccountDAO accountDAO = Factory.getRegularAccountDAO();
		
		String name = "", email = "", pan = "", martialStatus = "", occupation = "";
        String doorNo = "", street = "", city = "", state = "", msg = "";
        boolean isError = false, exceptionOccured = false;
        long phone = -1, adhaar = -1;
        int pincode = -1, income = -1, branchId, accountType = -1, cardType = -1;
        char gender = ' ';
        byte age = 0;
        
        try {
        	branchId = (Integer) req.getSession(false).getAttribute("branch-id");
        	
        	name = req.getParameter("name");
        	phone = Long.parseLong(req.getParameter("phone"));
        	email = req.getParameter("email");
        	age = Byte.parseByte(req.getParameter("age"));
        	adhaar = Long.parseLong(req.getParameter("adhaar"));
        	pan = req.getParameter("pan");
        	martialStatus = req.getParameter("martial-status");
        	occupation = req.getParameter("occupation");
        	income = Integer.parseInt(req.getParameter("income"));
        	gender = req.getParameter("gender").charAt(0);
        	
        	accountType = Integer.parseInt(req.getParameter("account-type"));
        	cardType = Integer.parseInt(req.getParameter("card-type"));
        	
        	doorNo = req.getParameter("door-no");
        	street = req.getParameter("street");
        	city = req.getParameter("city");
        	state = req.getParameter("state");
        	pincode = Integer.parseInt(req.getParameter("pincode"));
        	
        	// Validate user input
        	if(!isError && Util.getNoOfDigits(phone) != 10) {
				isError = true;
        		msg = "Invalid phone number";
        	}
        	
        	if(!isError && Util.getNoOfDigits(adhaar) != 12) {
				isError = true;
        		msg = "Invalid adhaar number";
        	}
        	
        	if(!isError && pan.length() != 10) {
				isError = true;
        		msg = "Invalid PAN number"; 		
        	}
        	
        	if(!isError && Util.getNoOfDigits(pincode) != 6 ) {
				isError = true;
        		msg = "Invalid pincode";	
        	}
        	
        	if(!isError && !(occupation.length() >= 3 && occupation.length() <= 20)) {
        		isError = true;
        		msg = "Occupation length should be between 3 to 20 characters";
        	}
        	
        	if(!isError && email.length() > 30) {
        		isError = true;
        		msg = "Email should be less than 30 characters";
        	}
        	
        	if(!isError && doorNo.length() > 10) {
        		isError = true;
        		msg = "door no should be less than 10 characters";
        	}
        	
        	if(!isError && street.length() > 30) {
        		isError = true;
        		msg = "street should be less than 30 characters";
        	}
        	
        	if(!isError && city.length() > 15) {
        		isError = true;
        		msg = "city should be less than 15 characters";
        	}
        	
        	if(!isError && state.length() > 15) {
        		isError = true;
        		msg = "state should be less than 15 characters";
        	}
        	
        	// Create new customer.
        	if(!isError) {	
	            address = new Address(doorNo, street, city, state, pincode);
	        	
	        	type = RegularAccountType.getType(accountType);
	        	
	        	conn = Factory.getDataSource().getConnection();
	        	customer = customerDAO.create(conn, name, phone, email, age, gender, 
	        									martialStatus, occupation, income, adhaar, 
	        									pan, address);
	        	account = accountDAO.create(conn, customer.getId(), name, branchId, type, cardType, null);
	        	
	        	req.setAttribute("customer", customer);
	        	req.setAttribute("displayPassword", true);
	        	req.setAttribute("account", account);
	        	req.getRequestDispatcher("/jsp/employee/customerCreationSuccess.jsp").forward(req, res);
        	}
        } catch(ClassCastException e) {
        	System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "internal error !!!";
		} catch(NumberFormatException e) {
        	System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "internal error !!!";
		} catch(SQLException e) {
			exceptionOccured = true;
			msg = e.getMessage();
		} finally {
			
            try {
                if(conn != null)
                    conn.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
			
			if(isError || exceptionOccured) {
				// Dummy customer object to prefill the user input values.
	            address = new Address(doorNo, street, city, state, pincode);
	            customer = new Customer(-1, name, "", phone, email, age,
					                    gender, martialStatus, occupation, income, adhaar, pan, 
					                    "", address);
				out.println(Util.createNotification(msg, "danger"));
				req.setAttribute("customer", customer);
				req.setAttribute("accountType", accountType);
    			req.setAttribute("cardType", cardType);
    			req.getRequestDispatcher("/jsp/employee/createCustomer.jsp").include(req, res);
			}
			
			out.close();
		}
	}
}