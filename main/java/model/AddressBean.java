package model;

import java.io.Serializable;

public class AddressBean implements Serializable {
	private static final long serialVersionUID = -4698064057144376402L;
	
	private String doorNo;
    private String street;
    private String city;
    private String state;
    private int pincode;

    public String getDoorNo() {
        return this.doorNo;
    }

    public String getStreet() {
        return this.street;
    }

    public String getCity() {
        return this.city;
    }

    public String getState() {
        return this.state;
    }

    public int getPincode() {
        return this.pincode;
    }
    
    // setters
    public void setDoorNo(String doorNo) {
    	this.doorNo = doorNo;
    }
    
    public void setStreet(String street) {
    	this.street = street;
    }
    
    public void setCity(String city) {
    	this.city = city;
    }
    
    public void setState(String state) {
    	this.state = state;
    }
    
    public void setPincode(int pincode) {
    	this.pincode = pincode;
    }
    
    public String toString() {
        String repr = "";
        repr += "\nNo: " + this.doorNo + ", " + this.street;
        repr += "\n" + this.city + " - " + this.pincode + "\n" + this.state;
        return repr;
    }
}