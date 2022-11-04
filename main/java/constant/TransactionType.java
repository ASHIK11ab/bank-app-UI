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
}