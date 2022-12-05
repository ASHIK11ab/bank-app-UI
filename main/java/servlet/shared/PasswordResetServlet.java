package servlet.shared;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constant.Role;
import dao.AdminDAO;
import dao.CustomerDAO;
import dao.EmployeeDAO;
import dao.ManagerDAO;
import dao.UserDAO;
import model.user.Customer;
import model.user.Employee;
import model.user.User;
import util.Factory;

// Generic password reset servlet.
public class PasswordResetServlet extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		UserDAO userDAO = Factory.getUserDAO();
		EmployeeDAO employeeDAO = Factory.getEmployeeDAO();
		ManagerDAO managerDAO = Factory.getManagerDAO();
		CustomerDAO customerDAO = Factory.getCustomerDAO();
		AdminDAO adminDAO = Factory.getAdminDAO();
		
		User user = null;
		Employee employeeObject = null;
		Customer customer = null;
		
		Role role = null, forRole = null;
		boolean isError = false, exceptionOccured = false;
		long id = -1;
		String newPassword = "", msg = "", oldPassword, redirectURI = "", status, previousPassword = "";
		int branchId = -1;
		byte type = 0;
		
		try {			
			role = (Role) req.getSession(false).getAttribute("role"); 
			forRole = Role.getRole(req.getParameter("for-role"));
			
			newPassword = req.getParameter("password");
			redirectURI = req.getParameter("redirectURI");
			
			// validate that recieved for role attribute.
			if(forRole == null) {
				isError = true;
				msg = "Internal error !!!";
			}
			
			// Restrict password reset access when resetting password of other Role user.
			if(!isError && forRole != role) {
				switch(role) {
					case ADMIN   : 
									if(forRole != Role.MANAGER)
										isError = true;
									break;
					case MANAGER: 									
									if(forRole != Role.EMPLOYEE)
										isError = true;
									break;
					case EMPLOYEE :
									if(forRole != Role.CUSTOMER)
										isError = true;
									break;
					case CUSTOMER: 
									if(forRole != Role.CUSTOMER)
										isError = true;
									break;
				}
				
				if(isError)
					msg = "Error !!!";
			}
			
			if(!isError) {
				// self password reset, get data from session.
				if(forRole == role) {
					id = (Long) req.getSession(false).getAttribute("id");
					
					// get branch id's for manager and employee password reset
					if(forRole == Role.EMPLOYEE || forRole == Role.MANAGER)
						branchId = (Integer) req.getSession(false).getAttribute("branch-id");
					
				} else {
					id = Long.parseLong(req.getParameter("id"));
					// get branch id's for manager and employee password reset
					if(forRole == Role.EMPLOYEE || forRole == Role.MANAGER) {
						branchId = Integer.parseInt(req.getParameter("branch-id"));
					}
				}
			}
			
			if(!isError && (newPassword.length() < 8 || newPassword.length() > 15)) {
				isError = true;
				msg = "Password should be within 8 to 15 characters !!!";
			}
			
			if(!isError) {
				switch(forRole) {
					case ADMIN   : user = adminDAO.get(id); break;
					case EMPLOYEE: employeeObject = employeeDAO.get(id, branchId);
								   user = (User) employeeObject; 
								   break;
					case MANAGER : employeeObject = managerDAO.get(id, branchId);
							       user = (User) employeeObject;
								   break;
					case CUSTOMER: 
									// For customer password reset, get type of password (login / transaction)
									type = Byte.parseByte(req.getParameter("type"));
									customer = customerDAO.get(id);
				}
				
				if(forRole == Role.CUSTOMER) {
					if((type != 0 && type != 1) || customer == null) {
						isError = true;
						msg = "Invalid password reset details !!!";
					}
				} else {
					if(user == null) {
						isError = true;
						msg = "Invalid user details !!!";
					}
				}
			}
			
			// Get old password in case of self password reset.
			if(!isError && forRole == role) {
				oldPassword = req.getParameter("old-password");
				
				if(forRole == Role.CUSTOMER)
					previousPassword = (type == 0) ? customer.getPassword() : customer.getTransPassword();
				else
					previousPassword = user.getPassword();
				
				if(!previousPassword.equals(oldPassword)) {
					isError = true;
					msg = "Invalid old password !!!";
				}
			}
			
			if(!isError) {
				
				if(forRole == Role.ADMIN) {
					synchronized (user) {					
						userDAO.updatePassword(id, newPassword, Role.ADMIN, (byte) 0, -1);
						user.setPassword(newPassword);
					}
				} else 
					if(forRole == Role.EMPLOYEE || forRole == Role.MANAGER) {
						synchronized (employeeObject) {					
							userDAO.updatePassword(id, newPassword, forRole, (byte) 0, branchId);
							employeeObject.setPassword(newPassword);
						}
					} else {
						synchronized (customer) {
							userDAO.updatePassword(id, newPassword, Role.CUSTOMER, type, -1);
							if(type == 0)
								customer.setPassword(newPassword);
							else
								customer.setTransPassword(newPassword);
						}
					}
				msg = "password updated successfully";
			}
		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			isError = true;
			msg = "Invalid input !!!";
		} catch(SQLException e) {
			isError = true;
			msg = e.getMessage();
		} catch(ClassCastException e) {
			System.out.println(e.getMessage());
			isError = true;
			msg = e.getMessage();
		} catch(NullPointerException e) {
			System.out.println(e.getMessage());
			isError = true;
			msg = e.getMessage();
		} finally {
			status = (isError || exceptionOccured) ? "danger" : "success";
			
			if(forRole == Role.EMPLOYEE || forRole == Role.MANAGER)
				redirectURI = String.format((redirectURI + "?branch-id=%d&msg=%s&status=%s"), branchId, msg, status);
			else
				redirectURI = String.format((redirectURI + "?msg=%s&status=%s"), msg, status);

			res.sendRedirect(redirectURI);
		}
	}
}