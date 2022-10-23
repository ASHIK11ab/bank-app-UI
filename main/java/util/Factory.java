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
	private static AdminDAO adminDAO;
	
	public static void init() throws Exception {
		try {
			Context ctx = new InitialContext();
			dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/newbankdb");
			
			branchDAO = new BranchDAO();
			managerDAO = new ManagerDAO();
			adminDAO = new AdminDAO();
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
	
	public static AdminDAO getAdminDAO() {
		return adminDAO;
	}
}