package model.account;

import java.time.LocalDate;

import model.Nominee;

public abstract class Account {
    private long accountNo;
    private long customerId;
    private String customerName;
    private Nominee nominee;
    private int branchId;
    private float balance;
    private int typeId;
    private LocalDate openingDate;
    
    
    public Account(long accountNo, long customerId, String customerName, Nominee nominee,
		            int branchId, float amount, LocalDate openingDate, int typeId) {
		this.accountNo = accountNo;
		this.customerId = customerId;
		this.customerName = customerName;
		this.nominee = nominee;
		this.branchId = branchId;
		this.balance = amount;
		this.openingDate = openingDate;
		this.typeId = typeId;
	}


    // Getters
    public LocalDate getOpeningDate() {
        return this.openingDate;
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
    
    public float getBalance() {
        return this.balance;
    }

    public Nominee getNominee() {
    	return this.nominee;
    }
    
    // setters
    
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