package constant;

public enum Role {
	ADMIN,
	MANAGER,
	EMPLOYEE,
	CUSTOMER;
	
	public static String getName(Role role) {
		return role.toString().toLowerCase();
	}
	
	public static Role getRole(String name) {
		name = name.toLowerCase();
		switch(name) {
			case "admin": return Role.ADMIN;
			case "manager": return Role.MANAGER;
			case "employee": return Role.EMPLOYEE;
			case "customer": return Role.CUSTOMER;
			default: return null;
		}
	}
}