package model;

import cache.AppCache;

public class Beneficiary implements Comparable<Beneficiary> {
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
    
    
    // setters
    public void setBankId(int id) {
    	this.bankId = id;
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
        
    // Beneficiaries are compared and sorted based on the names.
    @Override
    public int compareTo(Beneficiary target) {
    	if(target == null)
    		return 1;
    	
    	// If not equal just compare by name.
    	return this.getName().compareTo(target.getName());
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