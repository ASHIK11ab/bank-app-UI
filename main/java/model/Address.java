package model;


public class Address {
	
	private String doorNo;
    private String street;
    private String city;
    private String state;
    private int pincode;
    
    public Address(String doorNo, String street, String city, String state, int pincode) {
        this.doorNo = doorNo;
        this.street = street;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
    }

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
    
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }
        
        Address target = (Address) obj;
        
        return (this.doorNo == target.getDoorNo() && this.street.equals(target.getStreet()) && this.city.equals(target.getCity())
        		&& this.state.equals(target.getState()) && this.pincode == target.getPincode());
    }
    
    
    public String toString() {
        String repr = "";
        repr += "\nNo: " + this.doorNo + ", " + this.street;
        repr += "\n" + this.city + " - " + this.pincode + "\n" + this.state;
        return repr;
    }
}