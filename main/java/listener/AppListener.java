package listener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import cache.AppCache;
import constant.RegularAccountType;
import dao.AccountDAO;
import model.Address;
import model.Bank;
import model.Branch;
import model.IntegratedBank;
import model.account.CurrentAccount;
import model.account.SavingsAccount;
import model.user.Employee;
import runnables.AutoDebitRDRunnable;
import runnables.DepositIntrestCreditRunnable;
import runnables.MinimumBalanceCheckRunnable;
import runnables.SavingsIntrestCreditRunnable;
import util.Factory;


public class AppListener implements ServletContextListener {
	private Thread depositIntrestCreditThread;
	private Thread autoDebitRDThread;
	private Thread minimumBalanceCheckThread;
	private Thread savingsIntrestCreditThread;
	
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
    		
    		savingsIntrestCreditThread = new Thread(new SavingsIntrestCreditRunnable());
    		savingsIntrestCreditThread.start();
    		System.out.println("\nSavings intrest credit thread started in context");
    		
    	} catch(Exception e) {
    		System.out.println(e.getMessage());
    	}
    }
	
	
	public void contextDestroyed(ServletContextEvent sce) {
		depositIntrestCreditThread.interrupt();
		autoDebitRDThread.interrupt();
		minimumBalanceCheckThread.interrupt();
		savingsIntrestCreditThread.interrupt();
		System.out.println("context destroyed");
	}
	
	
	private void loadCache() {
        Connection conn = null;
        PreparedStatement stmt1 = null, stmt2 = null, stmt3 = null, stmt4 = null, stmt5 = null;
        PreparedStatement stmt6 = null, stmt7 = null, stmt8 = null;
        ResultSet rs1 = null, rs2 = null, rs3 = null, rs4 = null, rs5 = null, rs6 = null, rs7 = null, rs8 = null;
        
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
        
        int accountTypeId, minimumBalance, dailyLimit;
        float intrestRate;
        
        Employee manager = null;
        long managerId, managerPhone;
        String managerName, managerEmail, managerPassword;
        
        try {
            conn = Factory.getDataSource().getConnection();
            stmt1 = conn.prepareStatement("SELECT * FROM bank");
            stmt2 = conn.prepareStatement("SELECT b.id, b.name, b.door_no, b.street, b.city, b.state, b.pincode, m.id as manager_id, m.name as manager_name, m.password as manager_password, m.phone as manager_phone, m.email as manager_email FROM branch b JOIN manager m ON b.id = m.branch_id");
            stmt3 = conn.prepareStatement("SELECT * FROM banks");
            stmt4 = conn.prepareStatement("SELECT * FROM regular_account_type");
            
            stmt5 = conn.prepareStatement("SELECT COUNT(*) FROM employee WHERE branch_id = ?");
			stmt6 = conn.prepareStatement(AccountDAO.BRANCH_REGULAR_ACCOUNT_COUNT_QUERY);
			stmt7 = conn.prepareStatement(AccountDAO.BRANCH_DEPOSIT_ACCOUNT_COUNT_QUERY);
			stmt8 = conn.prepareStatement("SELECT COUNT(*) FROM customer WHERE removed_date IS NULL");
			
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
	                
	                // Load branch stats
	    			stmt5.setInt(1, branchId);
	    			rs5 = stmt5.executeQuery();
	    			if(rs5.next())
	    				branch.setEmployeeCnt(rs5.getInt("count"));
	    			
	    			stmt6.setInt(1, branchId);
	    			rs6 = stmt6.executeQuery();
	    			while(rs6.next()) {
	    				switch(RegularAccountType.getType(rs6.getInt("type_id"))) {
		    				case SAVINGS: branch.setSavingsAccountCnt(rs6.getInt("count")); break;
		    				case CURRENT: branch.setCurrentAccountCnt(rs6.getInt("count")); break;
	    				}
	    			}
	    			
	    			stmt7.setInt(1, branchId);
	    			rs7 = stmt7.executeQuery();
	    			if(rs7.next())
	    				branch.setDepositAccountCnt(rs7.getInt("count"));	
	                
	                bank.addBranch(branch);
	            }
	            
	            // load integrated banks
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
                
                // load regular account configuration.
                rs4 = stmt4.executeQuery();
                while(rs4.next()) {
                	accountTypeId = rs4.getInt("id");
                	minimumBalance = rs4.getInt("minimum_balance");
                	
                	if(accountTypeId == RegularAccountType.SAVINGS.id) {
                		intrestRate = rs4.getFloat("rate_of_intrest");
                		dailyLimit = rs4.getInt("daily_limit");
                		
                		SavingsAccount.setDailyLimit(dailyLimit);
                		SavingsAccount.setIntrestRate(intrestRate);
                		SavingsAccount.setMinimumBalance(minimumBalance);
                	} else {
                		CurrentAccount.setMinimumBalance(minimumBalance);
                	}
                }
                
                
                rs8 = stmt8.executeQuery();
                if(rs8.next())
                	bank.setCustomerCnt(rs8.getInt("count"));
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
                if(rs4 != null)
                    rs4.close();
                if(rs5 != null)
                    rs5.close();
                if(rs6 != null)
                    rs6.close();
                if(rs7 != null)
                    rs7.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }

            try {
                if(stmt1 != null)
                    stmt1.close();
                if(stmt2 != null)
                    stmt2.close();
                if(stmt3 != null)
                    stmt3.close();
                if(stmt4 != null)
                    stmt4.close();
                if(stmt5 != null)
                    stmt5.close();
                if(stmt6 != null)
                    stmt6.close();
                if(stmt7 != null)
                    stmt7.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }

            try {
                if(conn != null)
                    conn.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
        }
    }
	
}
