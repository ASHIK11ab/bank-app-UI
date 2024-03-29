package model.account;

import java.time.LocalDate;

import constant.DepositAccountType;
import model.Nominee;

public class DepositAccount extends Account {
	private static float fdIntrestRate = 0.03F;
	private static float rdIntrestRate = 0.05F;
	
    private long payoutAccountNo;
    private float intrestRate;
    private int tenureMonths;
    private long debitFromAccountNo;
    
    // FD - initial amount deposited, RD - monthly installment.
    private int depositAmount;
    private LocalDate recurringDate;
    
    
    // RD
    public DepositAccount(long accountNo, long customerId, String customerName,
                            Nominee nominee, int branchId, float amount,
                            long payoutAccountNo, long debitFromAccountNo, int tenureMonths,
                            float intrestRate, LocalDate openingDate, LocalDate closingDate, int depositAmount,
                            LocalDate recurringDate) {
		super(accountNo, customerId, customerName, nominee, branchId, 
				amount, openingDate, closingDate, DepositAccountType.RD.id);
        this.payoutAccountNo = payoutAccountNo;
        this.intrestRate = intrestRate;
        this.depositAmount = depositAmount;
        this.recurringDate = recurringDate;
        this.tenureMonths = tenureMonths;
        this.debitFromAccountNo = debitFromAccountNo;
    }


    // FD
    public DepositAccount(long accountNo, long customerId, String customerName,
				            Nominee nominee, int branchId, float amount,
				            long payoutAccountNo, long debitFromAccountNo, int tenureMonths,
				            float intrestRate, LocalDate openingDate, LocalDate closingDate, int amountDeposited) {
		super(accountNo, customerId, customerName, nominee, branchId, 
				amount, openingDate, closingDate, DepositAccountType.FD.id);
        this.payoutAccountNo = payoutAccountNo;
        this.intrestRate = intrestRate;
        this.tenureMonths = tenureMonths;
        this.debitFromAccountNo = debitFromAccountNo;
        this.depositAmount = amountDeposited;
        
        this.recurringDate = null;
    }

    public LocalDate getMaturityDate() {
        return this.getOpeningDate().plusMonths(this.tenureMonths);
    }
    
    
    // getters.
    public static float getTypeIntrestRate(DepositAccountType type) {
    	switch(type) {
	    	case FD: return DepositAccount.fdIntrestRate;
	    	case RD: return DepositAccount.rdIntrestRate;
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
    
    public int getDepositAmount() {
    	return depositAmount;
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
    
    public void setRecurringDate(LocalDate recurringDate) {
    	this.recurringDate = recurringDate;
    }
    
    public static void setTypeIntrestRate(DepositAccountType type, float intrestRate) {
    	switch(type) {
	    	case FD: DepositAccount.fdIntrestRate = intrestRate; break;
	    	case RD: DepositAccount.rdIntrestRate = intrestRate; break;
	    	default: return;
    	}
    }
}