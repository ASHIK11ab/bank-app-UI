package servlet.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cache.AppCache;
import dao.BranchDAO;
import dao.ManagerDAO;
import model.Address;
import model.Bank;
import model.Branch;
import model.user.Employee;
import util.Factory;
import util.Util;

public class CreateEditBranchServlet extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		BranchDAO branchDAO = Factory.getBranchDAO();
		ManagerDAO managerDAO = Factory.getManagerDAO();
		
		Connection conn = null;
		PrintWriter out = res.getWriter();
		
		Bank bank = AppCache.getBank();
		Branch branch = null, temp = null;
		Employee manager = null;
		Address address = null;
		
		String name = "", doorNo = "", street = "", city = "", state = "";
		int pincode, branchId = -1;
		byte type = -1;
		String managerName = "", managerEmail = "";
		long managerPhone = 0;
		
		boolean isError = false, exceptionOccured = false;
		String msg = "";
		
		try {
			type = Byte.parseByte(req.getParameter("type"));
			name = req.getParameter("name").toLowerCase();
			doorNo = req.getParameter("door-no");
			street = req.getParameter("street");
			city = req.getParameter("city");
			state = req.getParameter("state");
			pincode = Integer.parseInt(req.getParameter("pincode"));
			
			address = new Address(doorNo, street, city, state, pincode);
			
        	if(name.length() > 20) {
        		isError = true;
        		msg = "branch name should be less than 20 characters";
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
			
			if(!isError && Util.getNoOfDigits(pincode) != 6 ) {
				isError = true;
				msg = "Invalid pincode !!!";
			}
			
			// Validate manager input only on branch creation, since manager details is
			// not editable from this view.
			if(type == 0) {
				
				managerName = req.getParameter("manager-name");
				managerEmail = req.getParameter("manager-email");
				managerPhone = Long.parseLong(req.getParameter("manager-phone"));
				
				if(!isError && Util.getNoOfDigits(managerPhone) != 10 ) {
					isError = true;
					msg = "Phone number should be 10 digits !!!";
				}
				
				if(!isError && managerEmail.length() > 30) {
					isError = true;
					msg = "Email should be less than 30 characters !!!";
				}
				
				if(!isError && managerName.length() > 20) {
					isError = true;
					msg = "Manager name should be less than 20 characters !!!";
				}
			}
			
			// edit
			if(type == 1) {
				branchId = Integer.parseInt(req.getParameter("branch-id"));
				branch = branchDAO.get(branchId);
				if(branch == null) {
					isError = true;
					msg = "Branch does not exist !!!";
				}
			}
			
			if(!isError) {
				/* connection is created and passed to DAO's to prevent the usage
				of multiple connections for doing one operation */
				conn = Factory.getDataSource().getConnection();
				
				switch(type) {
					// create
					case 0: 
							branch = branchDAO.createUpdate(conn, name, address, (byte) 0, -1);
							manager = managerDAO.create(conn, branch.getId(), managerName, managerEmail, managerPhone);
							branch.assignManager(manager);
							
							out.println(Util.createNotification("branch created successfully", "success"));
							req.setAttribute("branch", branch);
							req.setAttribute("displayManagerPassword", true);
							req.getRequestDispatcher("/jsp/admin/branch.jsp").include(req, res);
							break;
					// edit
					case 1: 
							temp = new Branch(branchId, name, address);
							
							// update in DB only when data has changed.
								synchronized (branch) {							
									if(!branch.equals(temp)) {
										branchDAO.createUpdate(conn, name, address, (byte) 1, branch.getId());
										manager = branch.getManager();
										
										if(manager != null)
											synchronized (manager) {
												manager.setBranchName(name);
											}
									}
						            	
								}
							res.sendRedirect(String.format("/bank-app/admin/branches/%d/view?msg=branch details updated successfully&status=success", branchId));
							break;
					default: 
							isError = true;
							msg = "Page not found !!!";
				}
			}
		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "Invalid input";
		} catch(SQLException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = e.getMessage();
		} finally {
			
            try {
                if(conn != null)
                    conn.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
            
            if(isError || exceptionOccured) {
            	System.out.println("in error");
            	out.println(Util.createNotification(msg, "danger"));
            	
            	// create dummy object to store user input values
            	temp = new Branch(branchId, name, address);

            	req.setAttribute("type", type);
            	req.setAttribute("branchId", branchId);
            	req.setAttribute("branch", temp);
				req.setAttribute("name", managerName);
				req.setAttribute("email", managerEmail);
				req.setAttribute("phone", managerPhone);
            	req.getRequestDispatcher("/jsp/admin/createEditBranch.jsp").include(req, res);
            }
            
			out.close();
		}
	}
}