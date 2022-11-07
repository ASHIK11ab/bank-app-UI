package servlet.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.BranchDAO;
import model.Address;
import model.Branch;
import model.user.Employee;
import util.Factory;
import util.Util;


public class EditBranchServlet extends HttpServlet {

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		BranchDAO branchDAO = Factory.getBranchDAO();
		
		Connection conn = null;
		PrintWriter out = res.getWriter();
		
		Branch branch = null, editedBranch = null;
		Address address = null;
		
		String name = "", doorNo = "", street = "", city = "", state = "";
		int pincode, branchId = -1;
		
		boolean isError = false, exceptionOccured = false;
		String msg = "";
		
		try {
			branchId = Integer.parseInt(req.getParameter("branch-id"));
			name = req.getParameter("name");
			doorNo = req.getParameter("door-no");
			street = req.getParameter("street");
			city = req.getParameter("city");
			state = req.getParameter("state");
			pincode = Integer.parseInt(req.getParameter("pincode"));
			
			address = new Address(doorNo, street, city, state, pincode);
			
			branch = branchDAO.get(branchId);
			if(branch == null) {
				isError = true;
				msg = "Branch not found !!!";
			}

			if(!isError && name.length() > 20) {
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
			
			if(!isError) {
				
				editedBranch = new Branch(-1, name, address);
				
				// only update in DB when branch details is changed.
				if(!branch.equals(editedBranch)) {
					conn = Factory.getDataSource().getConnection();
					branch = branchDAO.update(branchId, name, address);
				} else {
					System.out.println("ignoring since same data");
				}
				
				out.println(Util.createNotification("branch details updated successfully", "success"));
				res.sendRedirect(String.format("/bank-app/admin/branches/branch/%d/view?msg=branch details udpated successfully&status=success", branchId));
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
            	
            	if(branch == null) {
    				res.sendRedirect(String.format("/bank-app/admin/branches/?msg=%s&status=danger", msg));
            	} else {
                	out.println(Util.createNotification(msg, "danger"));
            		req.setAttribute("branchId", branch.getId());
	            	req.setAttribute("branchName", branch.getName());
	            	req.setAttribute("inputBranchName", name);
	            	req.setAttribute("address", address);
	            	req.getRequestDispatcher("/jsp/admin/editBranch.jsp").include(req, res);
            	}
            }
            
			out.close();
		}
	}

}
