package servlet.customer;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.BeneficiaryType;
import dao.CustomerDAO;
import dao.IntegratedBankDAO;
import model.Beneficiary;
import model.IntegratedBank;
import model.user.Customer;
import util.Factory;
import util.Util;


public class BeneficiaryServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		IntegratedBankDAO integratedBandDAO = Factory.getIntegratedBankDAO();
		CustomerDAO customerDAO = Factory.getCustomerDAO();
		Customer customer = null;
		
		Collection<IntegratedBank> integratedBanks = null;
		SortedSet<Beneficiary> ownBankBeneficiaries = null, otherBankBeneficiaries = null;
		Beneficiary beneficiary = null;
		
		BeneficiaryType beneficiaryType = null;
		boolean isError = false, exceptionOccured = false;
		String path = req.getPathInfo(), action = "", msg = "", queryMsg = "", status = "";
		String [] result;
		long beneficiaryId, customerId;
		int type;
		
		queryMsg = req.getParameter("msg");
		status = req.getParameter("status");
		
		// Display notification if exists.
		if(queryMsg != null && status != null)
			out.println(Util.createNotification(queryMsg, status));
		
		try {
			customerId = (Long) req.getSession(false).getAttribute("id");
			customer = customerDAO.get(customerId); 
			
			// Display all beneficiaries.
			if(path == null || path.equals("/")) {
				ownBankBeneficiaries = customer.getBeneficiaries(BeneficiaryType.OWN_BANK);
				otherBankBeneficiaries = customer.getBeneficiaries(BeneficiaryType.OTHER_BANK);
				req.setAttribute("ownBankBeneficiaries", ownBankBeneficiaries);
				req.setAttribute("otherBankBeneficiaries", otherBankBeneficiaries);
				req.getRequestDispatcher("/jsp/customer/displayBeneficiaries.jsp").include(req, res);
				return;
			}
			
			// add a beneficiary.
			if(path.equals("/add")) {
				integratedBanks = integratedBandDAO.getAll();
				req.setAttribute("banks", integratedBanks);
				// action type = 0 -> Add a beneficiary.
				req.setAttribute("actionType", 0);
				req.getRequestDispatcher("/jsp/customer/createEditBeneficiary.jsp").forward(req, res);
				return;
			}
			
			path = path.substring(1);
			result = path.split("/");
						
			beneficiaryId = Integer.parseInt(result[0]);
			action = result[1];
			
			type = Byte.parseByte(req.getParameter("type"));
			beneficiaryType = BeneficiaryType.getType(type);
						
			if(beneficiaryType == null) {
				isError = true;
				msg = "Invalid beneficiary type !!!";
			}
			
			if(!isError) {
				beneficiary = customer.getBeneficiary(beneficiaryType, beneficiaryId);
				
				if(beneficiary == null) {
					isError = true;
					msg = "Beneficiary does not exist !!!";
				}
			}
			
			if(!isError) {
				req.setAttribute("beneficiary", beneficiary);
				req.setAttribute("type", type);
				switch(action) {
					case "view": 
								req.getRequestDispatcher("/jsp/customer/displayBeneficiary.jsp").include(req, res);
								break;
					case "remove": req.getRequestDispatcher("/jsp/customer/removeBeneficiaryConfirmation.jsp").forward(req, res);
									break;
					case "edit":
								integratedBanks = integratedBandDAO.getAll();
								req.setAttribute("banks", integratedBanks);
								// action type = 1 -> update beneficiary.
								req.setAttribute("actionType", 1);
								req.getRequestDispatcher("/jsp/customer/createEditBeneficiary.jsp").forward(req, res);
								break;
					default: isError = true;
							 msg = "page not found !!!";
							 break;
				}
			}
			
		} catch(NullPointerException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "Error !!!";
		} catch(IndexOutOfBoundsException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "page not found !!!";
		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "Internal error !!!";
		} catch(SQLException e) {
			exceptionOccured = true;
			msg = e.getMessage();
		} finally {			
			
			if(isError || exceptionOccured) {
				res.sendError(404);
			}
			
			out.close();
		}
	}
}
