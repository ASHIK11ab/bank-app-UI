package model.account;

import java.time.LocalDate;

import cache.AppCache;
import model.Nominee;

public abstract class Account {
    private final long accountNo;
    private long customerId;
    private String customerName;
    private Nominee nominee;
    private int branchId;
    private float balance;
    private int typeId;
    private LocalDate openingDate;
    private LocalDate closingDate;
    
    
    public Account(long accountNo, long customerId, String customerName, Nominee nominee,
		            int branchId, float amount, LocalDate openingDate, LocalDate closingDate, int typeId) {
		this.accountNo = accountNo;
		this.customerId = customerId;
		this.customerName = customerName;
		this.nominee = nominee;
		this.branchId = branchId;
		this.balance = amount;
		this.openingDate = openingDate;
		this.closingDate = closingDate;
		this.typeId = typeId;
	}


    // Getters
    public LocalDate getOpeningDate() {
        return this.openingDate;
    }
    
    public LocalDate getClosingDate() {
    	return this.closingDate;
    }
    
    public boolean isClosed() {
    	return this.closingDate != null;
    }
    
    public int getTypeId() {
    	return this.typeId;
    }
    
    public long getCustomerId() {
        return this.customerId;
    }
    
    public String getCustomerName() {
    	return this.customerName;
    }

    public long getAccountNo() {
        return this.accountNo;
    }

    public int getBranchId() {
        return this.branchId;
    }
    
    public String getBranchName() {
    	return AppCache.getBranch(this.branchId).getName();
    }
    
    public float getBalance() {
        return this.balance;
    }

    public Nominee getNominee() {
    	return this.nominee;
    }
    
    // setters
    public void setClosingDate(LocalDate date) {
    	if(!this.isClosed())
    		this.closingDate = date;
    }
    
    public void setBranchId(int branchId) {
    	this.branchId = branchId;
    }
    
    public void addAmount(float amount) {
        this.balance += amount;
    }

    public void deductAmount(float amount) {
        this.balance -= amount;
    }
}