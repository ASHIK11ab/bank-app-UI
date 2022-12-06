package constant;

public enum RegularAccountType {
	SAVINGS(1),
	CURRENT(2);
	
	public final int id;
	
	RegularAccountType(int id) { this.id = id; }
	
	public static RegularAccountType getType(int id) {
		switch(id) {
			case 1: return SAVINGS;
			case 2: return CURRENT;
			default: return null;
		}
	}
	
	public static int getId(RegularAccountType type) {
		switch(type) {
			case SAVINGS: return SAVINGS.id;
			case CURRENT: return CURRENT.id;
			default: return -1;
		}
	}
	
	public static String getName(int id) {
		switch(id) {
			case 1: return SAVINGS.toString();
			case 2: return CURRENT.toString();
			default: return "";
		}
	}
}