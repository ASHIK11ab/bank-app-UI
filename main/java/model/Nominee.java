package model;

public class Nominee {
    private final long id;
    private String name;
    private long adhaar;
    private long phone;
    private String relationship;
    

    public Nominee(long id, String name, long adhaar, 
                    long phone, String relationship) {
        this.id = id;
        this.name = name;
        this.adhaar = adhaar;
        this.phone = phone;
        this.relationship = relationship;
    }
    
    public long getId() {
    	return this.id;
    }
    
    public String getName() {
    	return this.name;
    }
    
    public long getPhone() {
    	return this.phone;
    }
    
    public long getAdhaar() {
    	return this.adhaar;
    }
    
    public String getRelationship() {
    	return this.relationship;
    }
}