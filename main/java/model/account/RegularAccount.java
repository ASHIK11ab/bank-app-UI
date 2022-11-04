package model.account;

import java.time.LocalDate;
import model.Nominee;

public abstract class RegularAccount extends Account {
    private boolean isActive;

    public abstract int initiateTransaction(float amount);
    
    public RegularAccount(long accountNo, long customerId, String customerName, 
    						Nominee nominee, int branchId, float amount, 
    						LocalDate openingDate, LocalDate closingDate, int typeId, boolean activeStatus) {
		super(accountNo, customerId, customerName, nominee, branchId, amount, openingDate, closingDate, typeId);
		this.isActive = activeStatus;
	}
    
    // Getters
    public boolean getIsActive() {
    	return this.isActive;
    }
     
    // Setters
    public void setIsActive(boolean status) {
    	this.isActive = status;
    }
}