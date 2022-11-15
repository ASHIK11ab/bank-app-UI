package servlet.customer;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.BeneficiaryType;
import dao.BeneficiaryDAO;
import dao.CustomerDAO;
import model.Beneficiary;
import model.user.Customer;
import util.Factory;


public class RemoveBeneficiaryServlet extends HttpServlet {
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {		
		CustomerDAO customerDAO = Factory.getCustomerDAO();
		BeneficiaryDAO beneficiaryDAO = Factory.getBeneficiaryDAO();
		
		Customer customer = null;
		Beneficiary beneficiary = null;
		
		BeneficiaryType type = null;
		boolean isError = false, exceptionOccured = false;
		String msg = "", redirectURI = "";
		long beneficiaryId = -1, customerId;
		int beneficiaryType = -1;
		
		try {
			customerId = (Long) req.getSession(false).getAttribute("id");
			
			beneficiaryId = Long.parseLong(req.getParameter("beneficiary-id"));
			beneficiaryType = Integer.parseInt(req.getParameter("type"));
			
			type = BeneficiaryType.getType(beneficiaryType);
			
			if(type == null) {
				isError = true;
				msg = "Invalid beneficiary type !!!";
			}
			
			// Validate beneficiary id.
			if(!isError) {
				customer = customerDAO.get(customerId);
				beneficiary = customer.getBeneficiary(type, beneficiaryId);
				
				if(beneficiary == null) {
					isError = true;
					msg = "Beneficiary does not exist !!!";
				} else {
					synchronized (customer) {
						beneficiaryDAO.delete(type, beneficiary.getId());
						
						// update in cache.
						customer.removeBeneficiary(type, beneficiary.getId());
						
						msg = "Beneficiary removed successfully";
					}
				}
			}
		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "Internal error !!!";
		} catch(SQLException e) {
			exceptionOccured = true;
			msg = e.getMessage();
		} finally {			
			
			if(isError || exceptionOccured)
				redirectURI = String.format("/bank-app/customer/beneficiaries/%d/view?type=%d&msg=%s&status=danger", beneficiaryId, beneficiaryType, msg);
			else
				redirectURI = String.format("/bank-app/customer/beneficiaries?msg=%s&status=success", msg);

			res.sendRedirect(redirectURI);
		}
	}
}
