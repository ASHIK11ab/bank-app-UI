package servlet.employee;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.RegularAccountType;
import dao.CustomerDAO;
import model.Address;
import model.user.Customer;
import util.Factory;
import util.Util;


public class UpdateCustomerServlet extends HttpServlet {

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {		
		Address address;
		CustomerDAO customerDAO = Factory.getCustomerDAO();
		
		boolean isError = false, exceptionOccured = false;
		String name, email, pan, martialStatus, occupation;
        String doorNo, street, city, state, msg = "";
        long phone, adhaar, customerId = -1;
        int pincode, income;
        char gender;
        byte age;
        
        try {   
        	customerId = Long.parseLong(req.getParameter("customer-id"));
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
        	        	
        	if(customerDAO.get(customerId) == null) {
        		isError = true;
        		msg = "Invalid customer details !!!";
        	}
        	
        	// Create new customer.
        	if(!isError) {	
	            address = new Address(doorNo, street, city, state, pincode);

	        	customerDAO.update(customerId, name, phone, email, age, gender, 
	        									martialStatus, occupation, income, adhaar, 
	        									pan, address);	        	
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
			
			if(isError || exceptionOccured)
				res.sendRedirect("/bank-app/employee/customer/" + customerId + "/update?msg=" + msg + "&status=danger");	
			else
				res.sendRedirect("/bank-app/employee/customer/" + customerId + "/view?msg=customer details updated successfully&status=success");				
		}
	}

}
