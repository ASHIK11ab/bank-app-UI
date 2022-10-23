package model;

import java.io.Serializable;

public class IntegratedBankBean implements Serializable {
	private static final long serialVersionUID = -3311798730393725909L;
	
	private int id;
    private String name;
    private String email;
    private long phone;
    private String apiURL;
    
    // Getters
    public int getId() {
        return this.id;
    }
    
    public String getName() {
    	return this.name;
    }
    
    public String getEmail() {
    	return this.email;
    }
    
    public long getPhone() {
    	return this.phone;
    }

    public String getApiURL() {
        return this.apiURL;
    }
    
    // Setters
    public void setId(int id) {
    	this.id = id;
    }
    
    public void setPhone(long phone) {
    	this.phone = phone;
    }
    
    public void setName(String name) {
    	this.name = name;
    }
    
    public void setEmail(String email) {
    	this.email = email;
    }
    
    public void setApiURL(String url) {
    	this.apiURL = url;
    }
}