package model.card;

import java.time.LocalDate;

public class DebitCard {	
	private long cardNo;
	private long linkedAccountNo;
	private LocalDate validFromDate;
	private LocalDate expiryDate;
	private byte typeId;
	private boolean isActive;
	private int pin;
	private int cvv;
	
	
	public DebitCard(long cardNo, long accountNo, LocalDate validFromDate, LocalDate expiryDate,
						byte typeId, boolean isActive, int pin, int cvv) {
		this.cardNo = cardNo;
		this.linkedAccountNo = accountNo;
		this.validFromDate = validFromDate;
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
	
	public LocalDate getValidFromDate() {
		return validFromDate;
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
	
	public byte getTypeId() {
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