package constant;

public enum AccountCategory {
	REGULAR(0),
	DEPOSIT(1),
	LOAN(2);
	
	public final int id;
	
	private AccountCategory(int id) { this.id = id; }
}