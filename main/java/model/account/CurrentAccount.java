package model.account;

import java.time.LocalDate;

import constant.RegularAccountType;
import model.Nominee;

public class CurrentAccount extends RegularAccount {
    private static int minimumBalance = 10000;
    
    
    public CurrentAccount(long accountNo, long customerId, String customerName, 
							Nominee nominee, int branchId, float amount,
							LocalDate openingDate, LocalDate closingDate, boolean activeStatus) {
		super(accountNo, customerId, customerName, nominee, branchId, 
				amount, openingDate, closingDate, RegularAccountType.CURRENT.id, activeStatus);
	}
    

    @Override
    public int initiateTransaction(float amount) { return 200; }
    
    // Getters
    public static int getMinimumBalance() {
    	return minimumBalance;
    }
    
    // Setters
    public static void setMinimumBalance(int minBalance) {
    	CurrentAccount.minimumBalance = minBalance;
    }
}