package constant;

public enum Role {
	ADMIN,
	MANAGER,
	EMPLOYEE,
	CUSTOMER;
	
	public static String getName(Role role) {
		return role.toString().toLowerCase();
	}
}