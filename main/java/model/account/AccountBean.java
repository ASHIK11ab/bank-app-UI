package model.account;

import java.io.Serializable;
import java.time.LocalDate;

public abstract class AccountBean implements Serializable {
    private long accountNo;
    private long customerId;
    private String customerName;
    private NomineeBean nominee;
    private int branchId;
    private float balance;
    private int typeId;
    private LocalDate openingDate;


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

    public NomineeBean getNominee() {
    	return this.nominee;
    }
    
    // setters
    public void setAccountNo(long accountNo) {
    	this.accountNo = accountNo;
    }
    
    public void setCustomerId(long customerId) {
    	this.customerId = customerId;
    }
    
    public void setTypeId(int typeId) {
    	this.typeId = typeId;
    }
    
    public void setCustomerName(String name) {
    	this.customerName = name;
    }
    
    public void setBranchId(int branchId) {
    	this.branchId = branchId;
    }
    
    public void setBalance(float balance) {
    	this.balance = balance;
    }
    
    public void setNominee(NomineeBean nominee) {
    	this.nominee = nominee;
    }
    
    public void setOpeningDate(LocalDate openingDate) {
    	this.openingDate = openingDate;
    }
}