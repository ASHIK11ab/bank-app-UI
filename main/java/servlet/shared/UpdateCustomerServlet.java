package servlet.shared;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.RegularAccountType;
import constant.Role;
import dao.CustomerDAO;
import model.Address;
import model.user.Customer;
import util.Factory;
import util.Util;


public class UpdateCustomerServlet extends HttpServlet {

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {		
		PrintWriter out = res.getWriter();
		Role role = null;
		Address address = null;
		CustomerDAO customerDAO = Factory.getCustomerDAO();
		
		Customer customer = null, tempCustomer = null;
		boolean isError = false, exceptionOccured = false;
		String name = "", email = "", pan = "", martialStatus = "", occupation = "", redirectURI = "";
        String doorNo = "", street = "", city = "", state = "", msg = "";
        long phone = 0, adhaar = 0, customerId = -1;
        int pincode = -1, income = -1;
        char gender = ' ';
        byte age = -1;
        
        try {
        	role = (Role) req.getSession(false).getAttribute("role"); 
        	name = req.getParameter("name");
        	phone = Long.parseLong(req.getParameter("phone"));
        	email = req.getParameter("email");
        	age = Byte.parseByte(req.getParameter("age"));
        	martialStatus = req.getParameter("martial-status");
        	occupation = req.getParameter("occupation");
        	income = Integer.parseInt(req.getParameter("income"));
        	gender = req.getParameter("gender").charAt(0);
        	
        	doorNo = req.getParameter("door-no");
        	street = req.getParameter("street");
        	city = req.getParameter("city");
        	state = req.getParameter("state");
        	pincode = Integer.parseInt(req.getParameter("pincode"));
        	
            address = new Address(doorNo, street, city, state, pincode);
        	
        	// Validate user input
        	
        	// Based on role get the input data
        	if(role == Role.CUSTOMER) {
        		// Incase of self update by customer get id from session,
        		customerId = (Long) req.getSession(false).getAttribute("id");
        	} else {
            	customerId = Long.parseLong(req.getParameter("customer-id"));
        	}
    	
        	customer = customerDAO.get(customerId);
        	
        	if(customer == null || customer.isRemoved()) {
        		isError = true;
        		msg = "Invalid customer details !!!";
        	}
        	
        	if(!isError) {
        		if(role == Role.CUSTOMER) {
        			adhaar = customer.getAdhaar();
        			pan = customer.getPan();
        		} else {
        			// Only employee can update customer ADHAAR and PAN.
                	adhaar = Long.parseLong(req.getParameter("adhaar"));
                	pan = req.getParameter("pan");
                	
	            	if(!isError && Util.getNoOfDigits(adhaar) != 12) {
	    				isError = true;
	            		msg = "Invalid adhaar number";
	            	}
	            	
	            	if(!isError && pan.length() != 10) {
	    				isError = true;
	            		msg = "Invalid PAN number"; 		
	            	}
        		}
        	}
        	
        	if(!isError && Util.getNoOfDigits(phone) != 10) {
				isError = true;
        		msg = "Invalid phone number";
        	}
        	
        	if(!isError && Util.getNoOfDigits(pincode) != 6 ) {
				isError = true;
        		msg = "Invalid pincode";	
        	}
        	
        	// Create new customer.
        	if(!isError) {	
	        	customerDAO.update(customerId, name, phone, email, age, gender, 
	        									martialStatus, occupation, income, adhaar, 
	        									pan, address);	 
	        	msg = "customer details updated successfully";
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
			
			if(isError || exceptionOccured) {
				// Dummy customer object to prefill the user input values.
	            tempCustomer = new Customer(customerId, name, "", phone, email, age,
						                    gender, martialStatus, occupation, income, adhaar, pan, 
						                    "", address, null);
				out.println(Util.createNotification(msg, "danger"));
				req.setAttribute("customer", tempCustomer);
				req.getRequestDispatcher("/jsp/components/updateCustomer.jsp").include(req, res);
			}
			else {
				if(role == Role.EMPLOYEE)
					redirectURI = String.format("/bank-app/employee/customer/%d/view?msg=%s&status=success", customerId, msg);
				else
					redirectURI = String.format("/bank-app/customer/profile/view?msg=%s&status=success", msg);
				
				res.sendRedirect(redirectURI);
			}
		}
	}

}
