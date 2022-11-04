package model.account;

import java.time.LocalDate;

import constant.RegularAccountType;
import model.Nominee;

public class SavingsAccount extends RegularAccount {
    public static float intrestRate;
    private static int minimumBalance = 1000;
//    public static int dailyLimit;
    
    
    public SavingsAccount(long accountNo, long customerId, String customerName, 
							Nominee nominee, int branchId, float amount, 
							LocalDate openingDate, LocalDate closingDate, boolean activeStatus) {
		super(accountNo, customerId, customerName, nominee, branchId, 
				amount, openingDate, closingDate, RegularAccountType.SAVINGS.id, activeStatus);
	}
    

    @Override
    public int initiateTransaction(float amount) { return 200; }
    
    // Getters
    public static float getIntrestRate()  {
    	return intrestRate;
    }
    
    public static int getMinimumBalance() {
    	return minimumBalance;
    }
//    
//    public int getDailyLimit() {
//    	return dailyLimit;
//    }
    
    // Setters
    public static void setIntrestRate(float intrestRate) {
    	SavingsAccount.intrestRate = intrestRate;
    }
    
    public static void setMinimumBalance(int minBalance) {
    	SavingsAccount.minimumBalance = minBalance;
    }
}