package model.account;


public abstract class RegularAccountBean extends AccountBean {
	
    private boolean isActive;

    public abstract int initiateTransaction(float amount);
    
    // Getters
    public boolean getIsActive() {
    	return this.isActive;
    }
     
    // Setters
    public void setIsActive(boolean status) {
    	this.isActive = status;
    }
}