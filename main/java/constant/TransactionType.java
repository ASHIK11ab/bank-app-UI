package constant;

public enum TransactionType {
	NEFT(1),
	IMPS(2),
	RTGS(3),
	UPI(4),
	ATM(5),
	CASH(6);
	
	public final int id;
	
	private TransactionType(int id) { this.id = id; }	
	
	public static TransactionType getType(int id) {
		switch(id) {
			case 1: return NEFT;
			case 2: return IMPS;
			case 3: return RTGS;
			case 4: return UPI;
			case 5: return ATM;
			case 6: return CASH;
			default: return null;
		}
	}
	
	public static int getId(TransactionType type) {
		switch(type) {
			case NEFT: return 1;
			case IMPS: return 2;
			case RTGS: return 3;
			case UPI: return 4;
			case ATM: return 5;
			case CASH: return 6;
			default: return -1;
		}
	}
}