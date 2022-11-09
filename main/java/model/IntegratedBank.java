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
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }
        
        IntegratedBank target = (IntegratedBank) obj;
        
        return (this.name.equals(target.name) && this.email.equals(target.getEmail()) &&
        			this.phone == target.getPhone() && this.apiURL.equals(target.getApiURL()));
    }
}