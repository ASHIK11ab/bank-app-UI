package model.account;


public abstract class RegularAccountBean extends AccountBean {
	
    private boolean isActive;
    private int typeId;

    public abstract int initiateTransaction(float amount);
    
    // Getters
    public boolean getIsActive() {
    	return this.isActive;
    }
    
    public int getTypeId() {
    	return this.typeId;
    }
    
    // Setters
    public void setIsActive(boolean status) {
    	this.isActive = status;
    }
    
    public void setTypeId(int typeId) {
    	this.typeId = typeId;
    }
}