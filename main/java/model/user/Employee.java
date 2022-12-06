package model.user;

public class Employee extends User implements Comparable<Employee> {
	private int branchId;
	private String branchName;
	
    public Employee(long id, String name, String password, String email,
            			long phone, int branchId, String branchName) {
		super(id, name, password, email, phone);
		this.branchId = branchId;
		this.branchName = branchName;
	}
	
	// getters
	public int getBranchId() {
		return this.branchId;
	}
	
	public String getBranchName() {
		return this.branchName;
	}
	
	// setters
	public void setBranchId(int id) {
		this.branchId = id;
	}
	
	public void setBranchName(String name) {
		this.branchName = name;
	}
	
	@Override
	public int compareTo(Employee target) {
		if(this.equals(target))
			return 0;
		
		char baseCharacter, targetCharacter;
    	
    	int minLength = (this.getName().length() < target.getName().length()) ? this.getName().length() : target.getName().length();
    	
    	for(int index = 0; index < minLength; ++index) {
    		baseCharacter = Character.toLowerCase(this.getName().charAt(index));
    		targetCharacter = Character.toLowerCase(target.getName().charAt(index));
    		
    		if(baseCharacter != targetCharacter) {
    			return baseCharacter - targetCharacter;
    		}
    	}
    	
    	// When first 'minLength' characters are same, string with lesser length
    	// is stored first.
    	if(this.getName().length() >= target.getName().length())
    		return 1;
    	else
    		return -1;
	}
}