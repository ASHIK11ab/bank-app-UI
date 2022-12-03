package servlet.customer;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cache.AppCache;
import constant.BeneficiaryType;
import dao.BeneficiaryDAO;
import dao.CustomerDAO;
import dao.IntegratedBankDAO;
import model.Beneficiary;
import model.IntegratedBank;
import model.user.Customer;
import util.Factory;
import util.Util;


public class AddEditBeneficiaryServlet extends HttpServlet {
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();
		
		IntegratedBankDAO integratedBankDAO = Factory.getIntegratedBankDAO();
		BeneficiaryDAO beneficiaryDAO = Factory.getBeneficiaryDAO();
		CustomerDAO customerDAO = Factory.getCustomerDAO();
				
		BeneficiaryType type = null;
		Beneficiary beneficiary = null, tempBeneficiary = null;
		Customer customer = null;
		String name = "", ifsc = "", nickName = "", msg = "", bankName = "";
		boolean isError = false, exceptionOccured = false;
		long accountNo = 0, customerId, confirmAccountNo, beneficiaryId = -1;
		int bankId = -1, beneficiaryType = -1, actionType = -1;
		
		try {
			customerId = (Long) req.getSession(false).getAttribute("id");
			
			beneficiaryType = Integer.parseInt(req.getParameter("type"));
			actionType = Integer.parseInt(req.getParameter("action-type"));
			
			accountNo = Long.parseLong(req.getParameter("account-no"));
			
			confirmAccountNo = (actionType == 0) ? Long.parseLong(req.getParameter("confirm-account-no")) : -1;
			
			name = req.getParameter("name");
			nickName = req.getParameter("nick-name").strip();
			
			type = BeneficiaryType.getType(beneficiaryType);
			
			if(type == null) {
				isError = true;
				msg = "Invalid beneficiary type !!!";
			}
			
			if(!isError && (actionType != 0 && actionType != 1)) {
				isError = true;
				msg = "Invalid action !!!";
			}
			
			if(!isError) {
				// get the beneficiary id to be updated.
				beneficiaryId = actionType == 1 ? Long.parseLong(req.getParameter("beneficiary-id")) : -1;
				
				if(type == BeneficiaryType.OTHER_BANK) {
					bankId = Integer.parseInt(req.getParameter("bank-id"));
					ifsc = req.getParameter("ifsc");
				} else {
					if(Util.getNoOfDigits(accountNo) != 11 ) {
						isError = true;
						msg = "A/C number must be 11 digits !!!";
					}
				}
			}
			
			if(!isError && actionType == 0 && accountNo != confirmAccountNo) {
				isError = true;
				msg = "Account no's does not match !!!";
			}
			
			if(!isError && type == BeneficiaryType.OTHER_BANK) {
				if(integratedBankDAO.get(bankId) == null) {
					isError = true;
					msg = "Invalid bank selected !!!";
				}
				
				if(ifsc.length() != 11) {
					isError = true;
					msg = "Invalid IFSC !!!";
				}
			}
			
			if(!isError && name.length() == 0) {
				isError = true;
				msg = "Name cannot be empty !!!";
			}
			
			if(!isError && name.length() > 20) {
				isError = true;
				msg = "Name should be less than 20 characters";
			}
			
			if(!isError && nickName.length() > 15) {
				isError = true;
				msg = "Nick name should be less than 15 characters";
			}
						
			// Dummy object to hold user input values.			
            if(type == BeneficiaryType.OWN_BANK)
            	tempBeneficiary = new Beneficiary(beneficiaryId, accountNo, name, nickName);
            else {
            	bankName = AppCache.getIntegratedBank(bankId).getName();
            	tempBeneficiary = new Beneficiary(beneficiaryId, accountNo, name, nickName, bankId, bankName, ifsc);
            }
			
			if(!isError) {
				customer = customerDAO.get(customerId);
				synchronized(customer) {

					// new beneficiary.
					if(actionType == 0) {
						
						for(Beneficiary existingBeneficiary : customer.getBeneficiaries(type)) {
							if(existingBeneficiary.equals(tempBeneficiary)) {
								isError = true;
								msg = "Beneficiary with given details aldready exists !!!";
							}
						}
						
						if(!isError) {
							beneficiary = beneficiaryDAO.create(type, customerId, accountNo, name, nickName, bankId, ifsc);
							
							customer.addBeneficiary(type, beneficiary);
							msg = "Beneficiary added successfully";
						}
					} else {
						// update.
						
						beneficiary = customer.getBeneficiary(type, beneficiaryId);
						
						if(beneficiary == null) {
							isError = true;
							msg = "Beneficiary does not exist !!!";
						} else {
							// update beneficiary in DB only when changed.
							if(!beneficiary.equals(tempBeneficiary)) {
								synchronized(beneficiary) {
									
									beneficiaryDAO.update(type, beneficiaryId, accountNo, name, nickName, bankId, ifsc);
									
									// update in cache.
									beneficiary.setAccountNo(accountNo);
									beneficiary.setName(name);
									beneficiary.setNickName(nickName);
									
									if(type == BeneficiaryType.OTHER_BANK) {
										beneficiary.setBankId(bankId);
										beneficiary.setIFSC(ifsc);
									}
								}
							}
							
							msg = "Beneficiary details updated successfully";
						}
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
			
			if(isError || exceptionOccured) {
				out.println(Util.createNotification(msg, "danger"));
				req.setAttribute("actionType", actionType);
				req.setAttribute("type", beneficiaryType);
				req.setAttribute("beneficiary", tempBeneficiary);
				req.setAttribute("banks", integratedBankDAO.getAll());
				req.getRequestDispatcher("/jsp/customer/createEditBeneficiary.jsp").include(req, res);
			} else {
				res.sendRedirect(String.format("/bank-app/customer/beneficiaries/%d/view?type=%d&msg=%s&status=success", beneficiary.getId(), beneficiaryType, msg));
			}
			
			out.close();
		}
	}
}