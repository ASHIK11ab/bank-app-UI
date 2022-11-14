package constant;

public enum BeneficiaryType {
    OWN_BANK(0),
    OTHER_BANK(1);
	
	public final int id;
	
	BeneficiaryType(int id) { this.id = id; }
	
	public static BeneficiaryType getType(int id) {
		switch(id) {
			case 0: return OWN_BANK;
			case 1: return OTHER_BANK;
			default: return null;
		}
	}
	
	public static int getId(BeneficiaryType type) {
		switch(type) {
			case OWN_BANK: return 0;
			case OTHER_BANK: return 1;
			default: return -1;
		}
	}
}