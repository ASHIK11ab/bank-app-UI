package model;

import java.io.Serializable;

public class EmployeeBean extends UserBean implements Serializable {
	private int branchId;
	private String branchName;
	
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