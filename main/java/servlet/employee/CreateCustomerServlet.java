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
import model.AddressBean;
import model.CustomerBean;
import model.account.RegularAccountBean;
import util.Factory;
import util.Util;


public class CreateCustomerServlet extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        PrintWriter out = res.getWriter();
        Connection conn = null;
        
        CustomerBean customer = null;
        AddressBean address = null;
        RegularAccountBean account = null;
        
        RegularAccountType type;
        
        CustomerDAO customerDAO = Factory.getCustomerDAO();
        RegularAccountDAO accountDAO = Factory.getRegularAccountDAO();
		
		String name, email, pan, martialStatus, occupation;
        String doorNo, street, city, state, msg = "";
        boolean isError = false, exceptionOccured = false;
        long phone, adhaar;
        int pincode, income, branchId, accountType = -1, cardType = -1;
        char gender;
        byte age;
        
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
        	
        	// Create new customer.
        	if(!isError) {	
	            address = new AddressBean();
	            address.setDoorNo(doorNo);
	            address.setStreet(street);
	            address.setCity(city);
	            address.setState(state);
	            address.setPincode(pincode);
	        	
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
			exceptionOccured = true;
			msg = "internal error !!!";
		} catch(NumberFormatException e) {
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
				out.println(Util.createNotification(msg, "danger"));
				req.setAttribute("account-type", accountType);
    			req.setAttribute("card-type", cardType);
    			req.getRequestDispatcher("/jsp/employee/createCustomer.jsp").include(req, res);
			}
			
			out.close();
		}
	}
}