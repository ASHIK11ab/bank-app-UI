package listener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import cache.AppCache;
import dao.AccountDAO;
import model.Address;
import model.Bank;
import model.Branch;
import model.IntegratedBank;
import model.user.Employee;
import runnables.AutoDebitRDRunnable;
import runnables.DepositIntrestCreditRunnable;
import runnables.MinimumBalanceCheckRunnable;
import util.Factory;


public class AppListener implements ServletContextListener {
	private Thread depositIntrestCreditThread;
	private Thread autoDebitRDThread;
	private Thread minimumBalanceCheckThread;
	
	public void contextInitialized(ServletContextEvent sce)  { 
    	try {    		    		
    		Factory.init();
    		System.out.println("factory initialized");
    		
    		AppCache.init();
    		loadCache();
    		
    		depositIntrestCreditThread = new Thread(new DepositIntrestCreditRunnable());
    		depositIntrestCreditThread.start();
    		System.out.println("\nDeposit intrest credit thread started in context");
    		
    		autoDebitRDThread = new Thread(new AutoDebitRDRunnable());
    		autoDebitRDThread.start();
    		System.out.println("\nAuto Debit RD thread started in context");
    		
    		minimumBalanceCheckThread = new Thread(new MinimumBalanceCheckRunnable());
    		minimumBalanceCheckThread.start();
    		System.out.println("\nMinimum balance check thread started in context");
    		
    	} catch(Exception e) {
    		System.out.println(e.getMessage());
    	}
    }
	
	
	public void contextDestroyed(ServletContextEvent sce) {
		depositIntrestCreditThread.interrupt();
		autoDebitRDThread.interrupt();
		minimumBalanceCheckThread.interrupt();
		System.out.println("context destroyed");
	}
	
	
	private void loadCache() {
        Connection conn = null;
        PreparedStatement stmt1 = null, stmt2 = null, stmt3 = null;
        ResultSet rs1 = null, rs2 = null, rs3 = null;
        
        AccountDAO accountDAO = Factory.getAccountDAO();

        Bank bank;
        Branch branch;
        IntegratedBank integratedBank;

        String bankName;
        String bankContactEmail;
        long bankContactPhone;
        String bankWebsiteURL;

        long integratedBankPhone;
        int integratedBankId;
        String integratedBankName;
        String integratedBankEmail;
        String integratedBankApiURL;

        long bankAccountNo = -1;
        int bankAccountBranchId = -1;
        	
        int branchId, pincode;
        String branchName;
        String doorNo;
        String street;
        String city;
        String state;
        
        Employee manager = null;
        long managerId, managerPhone;
        String managerName, managerEmail, managerPassword;
        
        try {
            conn = Factory.getDataSource().getConnection();
            stmt1 = conn.prepareStatement("SELECT * FROM bank");
            stmt2 = conn.prepareStatement("SELECT b.id, b.name, b.door_no, b.street, b.city, b.state, b.pincode, m.id as manager_id, m.name as manager_name, m.password as manager_password, m.phone as manager_phone, m.email as manager_email FROM branch b JOIN manager m ON b.id = m.branch_id");
            stmt3 = conn.prepareStatement("SELECT * FROM banks");

            rs1 = stmt1.executeQuery();
            if(rs1.next()) {
                bankName = rs1.getString("name");
                bankContactEmail = rs1.getString("support_email");
                bankContactPhone = rs1.getLong("support_phone");
                bankWebsiteURL = rs1.getString("website_url");
                bankAccountNo = rs1.getLong("account_no");
                
                bankAccountBranchId = accountDAO.getBranchId(conn, bankAccountNo);
                
                // Cache bank
                bank = new Bank(bankName, bankContactEmail, bankContactPhone, bankWebsiteURL, bankAccountNo, bankAccountBranchId);

	            rs2 = stmt2.executeQuery();
	            // Cache branches.
	            while(rs2.next()) {
	                branchId = rs2.getInt("id");
	                branchName = rs2.getString("name");
	                doorNo = rs2.getString("door_no");
	                street = rs2.getString("street");
	                city = rs2.getString("city");
	                state = rs2.getString("state");
	                pincode = rs2.getInt("pincode");
	                
	                managerId = rs2.getLong("manager_id");
	                managerName = rs2.getString("manager_name");
	                managerEmail = rs2.getString("manager_email");
	                managerPhone = rs2.getLong("manager_phone");
	                managerPassword = rs2.getString("manager_password");
	                
	                branch = new Branch(branchId, branchName, new Address(doorNo, street, city, state, pincode));
	                manager = new Employee(managerId, managerName, managerPassword, managerEmail, managerPhone, branchId, branchName);
	                branch.assignManager(manager);
	                
	                bank.addBranch(branch);
	            }

	            rs3 = stmt3.executeQuery();
	            while(rs3.next()) {
	                integratedBankId = rs3.getInt("id");
	                integratedBankName = rs3.getString("name");
	                integratedBankPhone = rs3.getLong("contact_phone");
	                integratedBankEmail = rs3.getString("contact_mail");
	                integratedBankApiURL = rs3.getString("api_url");
	
	                integratedBank = new IntegratedBank(integratedBankId, integratedBankName, integratedBankEmail,
	                                                        integratedBankPhone, integratedBankApiURL);
	                bank.addIntegratedBank(integratedBank);
	            }
	            
                AppCache.cacheBank(bank);
            }
        } catch(SQLException e) {
            System.out.println(e);
        } finally {
            try {
                if(rs1 != null)
                    rs1.close();
                if(rs2 != null)
                    rs2.close();
                if(rs3 != null)
                    rs3.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }

            try {
                if(stmt1 != null)
                    stmt1.close();
                if(stmt2 != null)
                    stmt2.close();
                if(stmt3 != null)
                    stmt3.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }

            try {
                if(conn != null)
                    conn.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
        }
    }
	
}
