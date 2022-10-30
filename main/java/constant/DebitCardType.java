package constant;

public enum DebitCardType {
	CLASSIC_DEBIT_CARD(1),
	PLATINUM_DEBIT_CARD(2);
	
	public final byte id;
	
	DebitCardType(int id) { this.id = (byte) id; }
	
	
	public static DebitCardType getType(byte type) {
		switch(type) {
			case 1: return DebitCardType.CLASSIC_DEBIT_CARD;
			case 2: return DebitCardType.PLATINUM_DEBIT_CARD;
			default: return null;
		}
	}
	
	public static String getName(byte type) {
		switch(type) {
			case 1: return "Classic Debit Card";
			case 2: return "Platinum Debit Card";
			default: return "";
		}
	}
	
	public static byte getId(DebitCardType type) {
		switch(type) {
			case CLASSIC_DEBIT_CARD: return DebitCardType.CLASSIC_DEBIT_CARD.id;
			case PLATINUM_DEBIT_CARD: return DebitCardType.PLATINUM_DEBIT_CARD.id;
			default: return -1;
		}
	}
}