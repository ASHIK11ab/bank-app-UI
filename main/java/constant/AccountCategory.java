package constant;

public enum AccountCategory {
	REGULAR(0),
	DEPOSIT(1),
	LOAN(2);
	
	public final int id;
	
	private AccountCategory(int id) { this.id = id; }
	
	public static String getName(AccountCategory category) {
		switch(category) {
			case REGULAR: return "account";
			case DEPOSIT: return "deposit";
			case LOAN: return "loan";
			default: return "";
		}
	}
	
	
	public static AccountCategory getCategory(int type) {
		switch(type) {
			case 0: return AccountCategory.REGULAR;
			case 1: return AccountCategory.DEPOSIT;
			case 2: return AccountCategory.LOAN;
			default: return null;
		}
	}
}