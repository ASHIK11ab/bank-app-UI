package model.account;

import java.time.LocalDate;
import java.util.Iterator;

import constant.RegularAccountType;
import model.Nominee;
import model.Transaction;

public class SavingsAccount extends RegularAccount {
    private static float intrestRate;
    private static int minimumBalance = 1000;
    public static int dailyLimit = 5000;
    
    
    public SavingsAccount(long accountNo, long customerId, String customerName, 
							Nominee nominee, int branchId, float amount, 
							LocalDate openingDate, LocalDate closingDate, boolean activeStatus) {
		super(accountNo, customerId, customerName, nominee, branchId, 
				amount, openingDate, closingDate, RegularAccountType.SAVINGS.id, activeStatus);
	}
    

    @Override
    public int initiateTransaction(float amount) {
    	Transaction transaction = null;
    	LocalDate today = LocalDate.now();
    	Iterator<Transaction> it = null;
        float amountDebitedToday = 0;

        // Insufficient balance.
        if(super.getBalance() < amount)
            return 401;
        
        // Check for transaction exceeding daily limit.
        it = this.getRecentTransactions().iterator();
        while(it.hasNext()) {
        	transaction = it.next();
        	
        	// All of the remaining transaction where performed before today, so skip.
        	if(!transaction.getDate().equals(today))
        		break;
        	else {
        		// Calculate total amount sent today (Debit transactions).
        		if(transaction.getFromAccountNo() == this.getAccountNo())
        			amountDebitedToday += transaction.getAmount();
        	}
        }
        
        // Performing the transaction will exceed daily limit.
        if(amountDebitedToday + amount > SavingsAccount.getDailyLimit())
        	return 402;
        
        return 200;
    }
    
    // Getters
    public static float getIntrestRate()  {
    	return intrestRate;
    }
    
    public static int getMinimumBalance() {
    	return minimumBalance;
    }
    
    public static int getDailyLimit() {
    	return dailyLimit;
    }
    
    // Setters
    public static void setIntrestRate(float intrestRate) {
    	SavingsAccount.intrestRate = intrestRate;
    }
    
    public static void setMinimumBalance(int minBalance) {
    	SavingsAccount.minimumBalance = minBalance;
    }
    
    public static void setDailyLimit(int dailyLimit) {
    	SavingsAccount.dailyLimit = dailyLimit;
    }
}