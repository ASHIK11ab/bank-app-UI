package model.user;

public class Employee extends User {
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
}