package model;

import java.io.Serializable;

public class UserBean implements Serializable {
	
	private static final long serialVersionUID = 253983475845L;
	
    private long id;
    private long phone;
    private String name;
    private String password;
    private String email;

    // Getters
    public long getId() {
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

    public String getPassword() {
        return this.password;
    }

    // Setters
    public void setId(long id) {
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
    
    public void setPassword(String password) {
        this.password = password;
    }
}