package model.card;

import java.time.LocalDate;

public class DebitCard {	
	private long cardNo;
	private long linkedAccountNo;
	private LocalDate issueDate;
	private LocalDate expiryDate;
	private int typeId;
	private boolean isActive;
	private int pin;
	private int cvv;
	
	
	public DebitCard(long cardNo, long accountNo, LocalDate issueDate, LocalDate expiryDate,
						int typeId, boolean isActive, int pin, int cvv) {
		this.cardNo = cardNo;
		this.linkedAccountNo = accountNo;
		this.issueDate = issueDate;
		this.expiryDate = expiryDate;
		this.typeId = typeId;
		this.isActive = isActive;
		this.pin = pin;
		this.cvv = cvv;
	}
	
	// Getters
	public long getCardNo() {
		return cardNo;
	}
	
	public long getLinkedAccountNo() {
		return linkedAccountNo;
	}
	
	public LocalDate getIssueDate() {
		return issueDate;
	}
	
	public LocalDate getExpiryDate() {
		return expiryDate;
	}
	
	public int getPin() {
		return pin;
	}
	
	public int getCvv() {
		return cvv;
	}
	
	public int getTypeId() {
		return typeId;
	}
	
    public boolean getIsActive() {
    	return this.isActive;
    }	
	
	// setters	
    public void setIsActive(boolean status) {
    	this.isActive = status;
    }
}