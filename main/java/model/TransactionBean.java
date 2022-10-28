package model;

import java.time.LocalDateTime;

public class TransactionBean {
    private long id;
    private int typeId;
    private String description;
    private long fromAccountNo;
    private long toAccountNo;
    private float amount;
    private LocalDateTime dateTime;
    
    // getters
    public long getId() {
    	return id;
    }
    
    public int getTypeId() {
    	return typeId;
    }
    
    public String getDescription() {
    	return description;
    }
    
    public long getFromAccountNo() {
    	return fromAccountNo;
    }
    
    public long toFromAccountNo() {
    	return toAccountNo;
    }
    
    public float getAmount() {
    	return amount;
    }
    
    public LocalDateTime getLocalDateTime() {
    	return dateTime;
    }
    
    // setters
    public void setId(long id) {
    	this.id = id;
    }
    
    public void setTypeId(int typeId) {
    	this.typeId = typeId;
    }
    
    public void setAmount(float amount) {
    	this.amount = amount;
    }
    
    public void setDescription(String description) {
    	this.description = description;
    }
    
    public void setFromAccountNo(long fromAccountNo) {
    	this.fromAccountNo = fromAccountNo;
    }
    
    public void setToAccountNo(long toAccountNo) {
    	this.toAccountNo = toAccountNo;
    }
    
    public void setDateTime(LocalDateTime dateTime) {
    	this.dateTime = dateTime;
    }
}