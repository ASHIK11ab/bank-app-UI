package model;


public class IntegratedBank {	
	private int id;
    public String name;
    private String email;
    private long phone;
    private String apiURL;
    
    public IntegratedBank(int id, String name, String contactEmail,
            				long contactPhone, String apiURL) {
		this.id = id;
		this.name = name;
		this.email = contactEmail;
		this.phone = contactPhone;
		this.apiURL = apiURL;
	}
    
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
}