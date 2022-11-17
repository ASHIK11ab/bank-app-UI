package model.card;

import java.time.LocalDate;

public class DebitCard implements Comparable<DebitCard> {	
	private final long cardNo;
	private final long linkedAccountNo;
	private final LocalDate validFromDate;
	private final LocalDate expiryDate;
	private LocalDate activatedDate;
	private LocalDate deactivatedDate;
	private final byte typeId;
	private boolean isActive;
	private int pin;
	private final int cvv;
	
	
	public DebitCard(long cardNo, long accountNo, LocalDate validFromDate, LocalDate expiryDate,
						byte typeId, boolean isActive, int pin, int cvv,
						LocalDate activatedDate, LocalDate deactivatedDate) {
		this.cardNo = cardNo;
		this.linkedAccountNo = accountNo;
		this.validFromDate = validFromDate;
		this.expiryDate = expiryDate;
		this.typeId = typeId;
		this.isActive = isActive;
		this.pin = pin;
		this.cvv = cvv;
		this.activatedDate = activatedDate;
		this.deactivatedDate = deactivatedDate;
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
	
	public LocalDate getActivatedDate() {
		return activatedDate;
	}
	
	public LocalDate getDeactivatedDate() {
		return deactivatedDate;
	}
	
	public boolean isActivated() {
		return activatedDate != null;
	}
	
	public boolean isDeactivated() {
		return deactivatedDate != null;
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
    
    
    @Override
    public int compareTo(DebitCard card) {    	
    	if(this.cardNo == card.getCardNo())
    		return 0;
    	else
    		return (this.cardNo < card.getCardNo()) ? -1 : 1;
    }
}