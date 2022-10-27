package model;

public class CustomerBean extends UserBean {
    private byte age;
    private char gender;
    private String martialStatus;
    private String occupation;
    private int income;
    private long adhaar;
    private String pan;
    private String transPassword;
    private AddressBean address;
    
    // Getters
    public byte getAge() {
    	return this.age;
    }
    
    public char getGender() {
    	return this.gender;
    }
    
    public String getMartialStatus() {
    	return this.martialStatus;
    }
    
    public String getOccupation() {
    	return this.occupation;
    }
    
    public int getIncome() {
    	return this.income;
    }
    
    public long getAdhaar() {
    	return this.adhaar;
    }
    
    public String getPan() {
    	return this.pan;
    }
    
    public String getTransPassword() {
    	return this.transPassword;
    }
    
    public AddressBean getAddress() {
    	return this.address;
    }
    
    // Setters
    public void setAge(byte age) {
    	this.age = age;
    }
    
    public void setGender(char gender) {
    	this.gender = gender;
    }
    
    public void setMartialStatus(String martialStatus) {
    	this.martialStatus = martialStatus;
    }
    
    public void setIncome(int income) {
    	this.income = income;
    }
    
    public void setOccupation(String occupation) {
    	this.occupation = occupation;
    }
    
    public void setAdhaar(long adhaar) {
    	this.adhaar = adhaar;
    }
    
    public void setPan(String pan) {
    	this.pan = pan;
    }
    
    public void setTransPassword(String password) {
    	this.transPassword = password;
    }
    
    public void setAddress(AddressBean address) {
    	this.address = address;
    }
}