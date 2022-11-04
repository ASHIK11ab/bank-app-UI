package model;

public class Nominee {
    private final long id;
    private String name;
    private String adhaar;
    private String phone;
    private String relationship;
    

    public Nominee(long id, String name, String adhaar, 
                    String phone, String relationship) {
        this.id = id;
        this.name = name;
        this.adhaar = adhaar;
        this.phone = phone;
        this.relationship = relationship;
    }
    
    public long getId() {
    	return id;
    }
    
    public String getName() {
    	return name;
    }
}