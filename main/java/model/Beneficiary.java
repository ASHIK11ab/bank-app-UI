package model;

import cache.AppCache;

public class Beneficiary implements Comparable {
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
    public Beneficiary(long id, long accountNo, String name, String nickName,
    					int bankId, String IFSC) {
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
    
    public String getIFSC() {
    	return IFSC;
    }
        
    // Beneficiaries are compared and sorted based on the names.
    @Override
    public int compareTo(Object obj) {
    	if(obj == null)
    		return 1;
    	
    	if(this.getClass() != obj.getClass())
    		return 1;
    	
    	Beneficiary target = (Beneficiary) obj;
    	
    	// check for equality.
    	if(this.getName().compareTo(target.getName()) == 0 && this.getNickName().compareTo(target.getNickName()) == 0
    			&& this.getAccountNo() == target.getAccountNo() && this.getIFSC().compareTo(target.getIFSC()) == 0
    			&& this.getBankId() == target.getBankId()) {
    		return 0;
    	}
    	
    	// If not equal just compare by name.
    	return this.getName().compareTo(target.getName());
    }
}