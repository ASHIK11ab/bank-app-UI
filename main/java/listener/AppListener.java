package listener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import cache.AppCache;
import model.Address;
import model.Bank;
import model.Branch;
import model.IntegratedBank;
import runnables.AutoDebitRDRunnable;
import runnables.DepositIntrestCreditRunnable;
import util.Factory;


public class AppListener implements ServletContextListener {
	private Thread depositIntrestCreditThread;
	private Thread autoDebitRDThread;
	
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
    		
    	} catch(Exception e) {
    		System.out.println(e.getMessage());
    	}
    }
	
	
	public void contextDestroyed(ServletContextEvent sce) {
		depositIntrestCreditThread.interrupt();
		autoDebitRDThread.interrupt();
		System.out.println("context destroyed");
	}
	
	
	private void loadCache() {
        Connection conn = null;
        PreparedStatement stmt1 = null, stmt2 = null, stmt3 = null;
        ResultSet rs1 = null, rs2 = null, rs3 = null;

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

        int branchId, pincode;
        String branchName;
        String doorNo;
        String street;
        String city;
        String state;
        
        try {
            conn = Factory.getDataSource().getConnection();
            stmt1 = conn.prepareStatement("SELECT * FROM bank");
            stmt2 = conn.prepareStatement("SELECT * FROM branch");
            stmt3 = conn.prepareStatement("SELECT * FROM banks");

            rs1 = stmt1.executeQuery();
            if(rs1.next() == true) {
                bankName = rs1.getString("name");
                bankContactEmail = rs1.getString("support_email");
                bankContactPhone = rs1.getLong("support_phone");
                bankWebsiteURL = rs1.getString("website_url");
                bankAccountNo = rs1.getLong("account_no");
                
                // Cache bank
                bank = new Bank(bankName, bankContactEmail, bankContactPhone, bankWebsiteURL, bankAccountNo);
                AppCache.cacheBank(bank);
            }

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

                branch = new Branch(branchId, branchName, new Address(doorNo, street, city, state, pincode));
                AppCache.cacheBranch(branch);
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
                AppCache.cacheIntegratedBank(integratedBank);
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
