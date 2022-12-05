package servlet.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jdt.internal.compiler.lookup.TypeIds;

import dao.IntegratedBankDAO;
import model.IntegratedBank;
import util.Factory;
import util.Util;


public class AddEditIntegratedBankServlet extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		IntegratedBankDAO integratedBankDAO = Factory.getIntegratedBankDAO();
		PrintWriter out = res.getWriter();
		
		IntegratedBank temp, bank = null;
		boolean isError = false, exceptionOccured = false;
		String name = "", email = "", apiURL = "", msg = "";
		long phone = 0;
		int bankId = -1;
		byte type = 0;
		
		try {
			type = Byte.parseByte(req.getParameter("type-id"));
			name = req.getParameter("name");
			email = req.getParameter("email");
			phone = Long.parseLong(req.getParameter("phone"));
			apiURL = req.getParameter("api-url");
			
			if(name.length() > 30) {
				isError = true;
				msg = "Name should be less than 30 characters !!!";
			}
			
        	if(!isError && email.length() > 30) {
        		isError = true;
        		msg = "Email should be less than 30 characters";
        	}
        	
        	if(!isError && Util.getNoOfDigits(phone) != 10) {
        		isError = true;
        		msg = "Phone number should be 10 digits !!!";
        	}
        	
        	if(!isError && apiURL.length() > 70) {
        		isError = true;
        		msg = "API url should be less than 30 characters";
        	}
        	
        	// edit
        	if(type == 1) {
        		bankId = Integer.parseInt(req.getParameter("bank-id"));
        		bank = integratedBankDAO.get(bankId);
        		if(bank == null) {
        			isError = true;
        			msg = "Invalid bank selected !!!";
        		}
        	}
			
        	if(!isError) {
        		switch(type) {
	        		case 0: bank = integratedBankDAO.createUpdate(name, email, apiURL, phone, (byte) 0, -1); 
	        				msg = "bank created successfully";
	        				break;
	        		case 1: 
	        				temp = new IntegratedBank(bankId, name, email, phone, apiURL);
	        				
	        				// only update in DB when data is changed.
        					synchronized (bank) {									
        						if(!bank.equals(temp)) {
        							integratedBankDAO.createUpdate(name, email, apiURL, phone, (byte) 1, bankId);
        						}
	        				}
	        				
	        				msg = "bank details updated successfully";
	        				break;
	        		default:
	        				isError = true;
	        				msg = "Page not found !!!";
        		}
        	}
		}catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "Invalid input !!!";
		} catch(SQLException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = e.getMessage();
		} finally {			
			
			if(isError || exceptionOccured) {
				out.println(Util.createNotification(msg, "danger"));
				// create a temporary object to store user input values to prefill
				// the form with user input.
				temp = new IntegratedBank(bankId, name, email, phone, apiURL);
				req.setAttribute("bank", temp);
				req.setAttribute("type", type);
				req.getRequestDispatcher("/jsp/admin/addEditIntegratedBank.jsp").include(req, res);
			} else {
				res.sendRedirect(String.format("/bank-app/admin/integrated-banks/%d/view?msg=%s&status=success", bank.getId(), msg));
			}
			
			out.close();
		}
	}
}