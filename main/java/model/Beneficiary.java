package model;

import cache.AppCache;

public class Beneficiary implements Comparable<Beneficiary> {
    private final long id;
    private int bankId;
    private String bankName;
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
        // Bank name is not necessary incase of own bank beneficiary.
        this.bankName = "";
        this.IFSC = "";
    }


    // Other bank beneficiary
    public Beneficiary(long id, long accountNo, String name, String nickName,
    					int bankId, String bankName, String IFSC) {
        this.id = id;
        this.bankId = bankId;
        this.bankName = bankName;
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
    
    public String getBankName() {
    	return this.bankName;
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
    
    
    // setters
    // Updates bank id and bank name.
    public void setBankId(int id) {
    	this.bankId = id;
    	this.bankName = AppCache.getIntegratedBank(this.bankId).getName();
    }
    
    public void setName(String name) {
    	this.name = name;
    }
    
    public void setNickName(String nickName) {
    	this.nickName = nickName;
    }
    
    public void setAccountNo(long accountNo) {
    	this.accountNo = accountNo;
    }
    
    public void setIFSC(String IFSC) {
    	this.IFSC = IFSC;
    }
        
    // Beneficiaries are compared based on their names (dictionary format).
    @Override
    public int compareTo(Beneficiary target) {
    	if(target == null)
    		return 1;
    	
    	char baseCharacter, targetCharacter;
    	    	
    	int minLength = (this.name.length() < target.getName().length()) ? this.name.length() : target.getName().length();
    	
    	for(int index = 0; index < minLength; ++index) {
    		baseCharacter = Character.toLowerCase(this.name.charAt(index));
    		targetCharacter = Character.toLowerCase(target.getName().charAt(index));
    		
    		if(baseCharacter != targetCharacter) {
    			return baseCharacter - targetCharacter;
    		}
    	}

    	return 0;
    }
    
    
    @Override
    public boolean equals(Object obj) {
    	if(obj == null)
    		return false;
    	
    	if(this.getClass() != obj.getClass()) 
    		return false;
    	
    	Beneficiary target = (Beneficiary) obj;
    	
    	return (this.getName().equals(target.getName()) && this.getNickName().equals(target.getNickName())
    			&& this.getAccountNo() == target.getAccountNo() && this.getIFSC().equals(target.getIFSC())
    			&& this.getBankId() == target.getBankId());
    }
}