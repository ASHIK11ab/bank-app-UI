package model;

public class Beneficiary {
    private final long id;
    private int bankId;
    private long accountNo;
    private String IFSC;
    private String name;
    private String nickName;


    // Own bank beneficiary
    public Beneficiary(long id, long accountNo, String name, String nickName) {
        this.id = id;
        this.accountNo = accountNo;
        this.name = name;
        this.nickName = nickName;

        this.bankId = -1;
        this.IFSC = "";
    }


    // Other bank beneficiary
    public Beneficiary(long id, int bankId, long accountNo, String IFSC, String name,
                        String nickName) {
        this.id = id;
        this.bankId = bankId;
        this.accountNo = accountNo;
        this.IFSC = IFSC;
        this.name = name;
        this.nickName = nickName;
    }

    // Getters
    public long getAccountNo() {
        return this.accountNo;
    }

    public int getBankId() {
        return this.bankId;
    }

    public long getId() {
        return this.id;
    }
    
    public String getName() {
    	return name;
    }
    
    public String getNickName() {
    	return nickName;
    }
}