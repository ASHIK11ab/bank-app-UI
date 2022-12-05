package constant;

public enum TransactionType {
	NEFT(1, 1, 500000),
	IMPS(2, 1, 200000),
	RTGS(3, 200000, 1000000),
	UPI(4, 1, 50000),
	ATM(5, 100, 80000),
	CASH(6, 100, 1000000);
	
	public final int id;
	public final int minAmount;
	public final int maxAmount;
	
	private TransactionType(int id, int minAmount, int maxAmount) {
		this.id = id;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
	}	
	
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