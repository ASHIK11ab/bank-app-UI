package model.account;

import java.io.Serializable;

public class NomineeBean implements Serializable {
    private long id;
    private String name;
    private long adhaar;
    private long phone;
    private String relationship;
    
    // Getters
    public long getId() {
    	return this.id;
    }
    
    public String getName() {
    	return this.name;
    }
    
    public long getAdhaar() {
    	return this.adhaar;
    }
    
    public long getPhone() {
    	return this.phone;
    }
    
    public String getRelationship() {
    	return this.relationship;
    }
    
    
    // setters
    public void setId(long id) {
    	this.id = id;
    }
    
    public void setName(String name) {
    	this.name = name;
    }
    
    public void setAdhaar(long adhaar) {
    	this.adhaar = adhaar;
    }
    
    public void setPhone(long phone) {
    	this.phone = phone;
    }
    
    public void setRelationship(String relationship) {
    	this.relationship = relationship;
    }
}