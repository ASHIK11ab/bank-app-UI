package constant;

public enum BeneficiaryType {
    OWN_BANK(0),
    OTHER_BANK(1);
	
	public final int id;
	
	BeneficiaryType(int id) { this.id = id; }
	
	public static BeneficiaryType getType(int id) {
		switch(id) {
			case 1: return OWN_BANK;
			case 2: return OTHER_BANK;
			default: return null;
		}
	}
}