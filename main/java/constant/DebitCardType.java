package constant;

public enum DebitCardType {
	CLASSIC_DEBIT_CARD(1),
	PLATINUM_DEBIT_CARD(2);
	
	public final int id;
	
	DebitCardType(int id) { this.id = id; }
}