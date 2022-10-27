package model.account;

import java.time.LocalDate;

import constant.DepositAccountType;

public class DepositAccountBean extends AccountBean {
	private static float fdIntrestRate = 0.03F;
	private static float rdIntrestRate = 0.05F;
	
    private long payoutAccountNo;
    private float intrestRate;
    private int tenureMonths;
    private long debitFromAccountNo;
    
    // Specific to Recurring Deposit.
    private int amountPerMonth;
    private LocalDate recurringDate;
    
    
    // getters.
    public static float getTypeIntrestRate(DepositAccountType type) {
    	switch(type) {
	    	case FD: return DepositAccountBean.fdIntrestRate;
	    	case RD: return DepositAccountBean.rdIntrestRate;
	    	default: return 0;
    	}
    }
    
    public long getPayoutAccountNo() {
    	return payoutAccountNo;
    }
    
    public float getIntrestRate() {
    	return intrestRate;
    }
    
    public int getTenureMonths() {
    	return tenureMonths;
    }
    
    public long getDebitFromAccountNo() {
    	return debitFromAccountNo;
    }
    
    public int getAmountPerMonth() {
    	return amountPerMonth;
    }
    
    public LocalDate getRecurringDate() {
    	return recurringDate;
    }
    
    
    // setters
    public void setIntrestRate(float intrestRate) {
    	this.intrestRate = intrestRate;
    }
    
    public void setTenureMonths(int months) {
    	this.tenureMonths = months;
    }
    
    public void setPayoutAccountNo(long accountNo) {
    	this.payoutAccountNo = accountNo;
    }
    
    public void setDebitFromAccountNo(long accountNo) {
    	this.debitFromAccountNo = accountNo;
    }
    
    public void setAmountPerMonth(int amount) {
    	this.amountPerMonth = amount;
    }
    
    public void setRecurringDate(LocalDate recurringDate) {
    	this.recurringDate = recurringDate;
    }
    
    public static void setTypeIntrestRate(DepositAccountType type, float intrestRate) {
    	switch(type) {
	    	case FD: DepositAccountBean.fdIntrestRate = intrestRate; break;
	    	case RD: DepositAccountBean.rdIntrestRate = intrestRate; break;
	    	default: return;
    	}
    }
}