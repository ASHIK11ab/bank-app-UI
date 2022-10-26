package util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import dao.*;

// Contains the singleton objects of DAO and Data Source.
public class Factory {
	private static DataSource dataSource;
	
	// DAO
	private static BranchDAO branchDAO;
	private static ManagerDAO managerDAO;
	private static EmployeeDAO employeeDAO;
	private static CustomerDAO customerDAO;
	private static AdminDAO adminDAO;
	private static IntegratedBankDAO integratedBankDAO;
	private static RegularAccountDAO regularAccountDAO;
	
	public static void init() throws Exception {
		try {
			Context ctx = new InitialContext();
			dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/newbankdb");
			
			branchDAO = new BranchDAO();
			managerDAO = new ManagerDAO();
			employeeDAO = new EmployeeDAO();
			customerDAO = new CustomerDAO();
			adminDAO = new AdminDAO();
			integratedBankDAO = new IntegratedBankDAO();
			regularAccountDAO = new RegularAccountDAO();
		} catch(NamingException e) {
			throw new Exception(e.getMessage());
		}
	}
	
	
	// Getters
	public static DataSource getDataSource() {
		return dataSource;
	}
	
	public static BranchDAO getBranchDAO() {
		return branchDAO;
	}
	
	public static ManagerDAO getManagerDAO() {
		return managerDAO;
	}
	
	public static EmployeeDAO getEmployeeDAO() {
		return employeeDAO;
	}
	
	public static AdminDAO getAdminDAO() {
		return adminDAO;
	}
	
	public static CustomerDAO getCustomerDAO() {
		return customerDAO;
	}
	
	public static IntegratedBankDAO getIntegratedBankDAO() {
		return integratedBankDAO;
	}
	
	public static RegularAccountDAO getRegularAccountDAO() {
		return regularAccountDAO;
	}
}