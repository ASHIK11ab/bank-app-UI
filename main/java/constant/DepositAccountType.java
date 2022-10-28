package constant;

public enum DepositAccountType {
	FD(3),
	RD(4);
	
	public final int id;
	
	private DepositAccountType(int id) { this.id = id;}
	
	public static int getId(DepositAccountType type) {
		switch(type) {
			case FD: return 3;
			case RD: return 4;
			default: return -1;
		}
	}
	
	public static DepositAccountType getType(int id) {
		switch(id) {
			case 3: return FD;
			case 4: return RD;
			default: return null;
		}
	}
	
	public static String getName(int id) {
		switch(id) {
			case 3: return "Fixed Deposit";
			case 4: return "Recurring Deposit";
			default: return "";
		}
	}
}