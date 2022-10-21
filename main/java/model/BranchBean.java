package model;

import java.io.Serializable;

public class BranchBean implements Serializable {
	private int id;
    private String name;
    private AddressBean address;
    private UserBean manager;
    
    // getters
    public int getId() {
    	return this.id;
    }
    
    public String getName() {
    	return this.name;
    }
    
    public AddressBean getAddress() {
    	return this.address;
    }
    
    public UserBean getManager() {
    	return this.manager;
    }
    
    // setters
    public void setManager(UserBean manager) {
        this.manager = manager;
    }

    public void setId(int id) {
    	this.id = id;
    }
    
    public void setName(String name) {
    	this.name = name;
    }
    
    public void setAddress(AddressBean address) {
    	this.address = address;
    }
}