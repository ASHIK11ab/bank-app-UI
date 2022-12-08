package model.user;


public class User {
    private final long id;
    private String name;
    private String password;
    private String email;
    private long phone;
    private boolean isLoggedIn;

    public User(long id, String name, String password, String email, long phone) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.email = email;
        this.isLoggedIn = false;
    }

    // Getters
    public long getId() {
        return this.id;
    }
    
    public boolean isLoggedIn() {
    	return this.isLoggedIn;
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
    
    public void setLoggedInStatus(boolean status) {
    	this.isLoggedIn = status;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }
        
        User target = (User) obj;
        return (this.name.equals(target.getName()) && this.email.equals(target.getEmail()) 
        		&& this.phone == target.getPhone());
    }
}