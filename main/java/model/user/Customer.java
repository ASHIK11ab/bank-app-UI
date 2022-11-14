package model.user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import constant.AccountCategory;
import constant.BeneficiaryType;
import constant.RegularAccountType;
import model.Address;
import model.Beneficiary;


public class Customer extends User {
    private byte age;
    private char gender;
    private String martialStatus;
    private String occupation;
    private int income;
    private long adhaar;
    private String pan;
    private String transPassword;
    private Address address;
    private LocalDate removedDate;
    
    private ArrayList<Beneficiary> ownBankBeneficiaries;
    private ArrayList<Beneficiary> otherBankBeneficiaries;
    
    // Mapping of A/C(s) no's to corresponding branch Id.
    private ConcurrentHashMap<Long, Integer> savingsAccounts;

    // A customer can have only one current account.
    // A/C no, branch Id
    private long currentAccountNo;
    private int currentAccountBranchId;

    // A/C no to branch id mapping.
    private ConcurrentHashMap<Long, Integer> depositAccounts;


    public Customer(long id, String name, String password, long phone,
                        String email, byte age, char gender, String martialStatus,
                        String occupation, int income, long adhaar, String pan,
                        String transPassword, Address address, LocalDate removedDate) {

        super(id, name, password, email, phone);
        this.age = age;
        this.gender = gender;
        this.martialStatus = martialStatus;
        this.occupation = occupation;
        this.income = income;
        this.adhaar = adhaar;
        this.pan = pan;
        this.transPassword = transPassword;
        this.address = address;
        this.removedDate = removedDate;
        
        this.ownBankBeneficiaries = new ArrayList<Beneficiary>();
        this.otherBankBeneficiaries = new ArrayList<Beneficiary>();
        this.savingsAccounts = new ConcurrentHashMap<Long, Integer>();

        this.currentAccountNo = -1;
        this.currentAccountBranchId = -1;
        
        this.depositAccounts = new ConcurrentHashMap<Long, Integer>();
    }


    // Add a mapping of account no and branch (if exists).
    public void addAccountBranchMapping(AccountCategory category, int accountType, long accountNo, int branchId) {        
    	RegularAccountType type;
    	
    	switch(category) {
	    	case REGULAR: 
	    					type = RegularAccountType.getType(accountType);
				            switch(type) {
					            case SAVINGS: this.savingsAccounts.put(accountNo, branchId);
					                          break;
					            case CURRENT: this.currentAccountNo = accountNo;
					            			  this.currentAccountBranchId = branchId;
					            			  break;
				            }
				            break;
	    	case DEPOSIT: this.depositAccounts.put(accountNo, branchId);
	    	              break;
	    	default: break;
    	}
    }
    

    // remove cached mapping of account no and branch (if exists).
    public void removeAccountBranchMapping(AccountCategory category, long accountNo) {
    	switch(category) {
	    	case REGULAR: 
				        	if(this.savingsAccounts.containsKey(accountNo)) {
				        		this.savingsAccounts.remove(accountNo);
				        	} else {
					        	if(this.currentAccountNo == accountNo) {
					        		this.currentAccountNo = -1;
					        		this.currentAccountBranchId = -1;
					        		return;
					        	}	
				        	}
				            break;
				            
	    	case DEPOSIT:   if(this.depositAccounts.containsKey(accountNo)) {
					    		this.depositAccounts.remove(accountNo);
					    	}
	    				    break;
	    	default: break;
		}
    }


    public void addBeneficiary(BeneficiaryType type, Beneficiary beneficiary) {
        ArrayList<Beneficiary> beneficiaries;
        beneficiaries =  (type == BeneficiaryType.OWN_BANK) ? this.ownBankBeneficiaries : this.otherBankBeneficiaries;
        beneficiaries.add(beneficiary);
    }


    // Removes a beneficiary from cache.
    public void removeBeneficiary(BeneficiaryType type, long beneficiaryId) {
        int index = 0;
        ArrayList<Beneficiary> beneficiaries;
        beneficiaries = (type == BeneficiaryType.OWN_BANK) ? this.ownBankBeneficiaries : this.otherBankBeneficiaries;

        for(Beneficiary beneficiary : beneficiaries)
            if(beneficiaryId == beneficiary.getId())
                break;
            else
                ++index;

        beneficiaries.remove(index);
    }
    
    
    // Getters
    public int getAccountBranchId(AccountCategory category, long accountNo) {
    	int branchId = -1;
    	
    	switch(category) {
	    	case REGULAR: 
				        	if(this.savingsAccounts.containsKey(accountNo)) {
				        		branchId = this.savingsAccounts.get(accountNo);
				        	} else {
					        	if(this.currentAccountNo == accountNo) {
					        		branchId = this.currentAccountBranchId;
					        	}
				        	}
				        	
				            break;
				            
	    	case DEPOSIT:   if(this.depositAccounts.containsKey(accountNo)) {
					    		branchId = this.depositAccounts.get(accountNo);
					    	}
	    				    break;
	    	default: break;
		}
    	
    	return branchId;
    }
    
    public Collection<Long> getSavingsAccounts() {
    	return this.savingsAccounts.keySet();
    }
    
    public Long getCurrentAccount() {
    	return this.currentAccountNo;
    }
    
    public Collection<Long> getDepositAccounts() {
    	return this.depositAccounts.keySet();
    }
    
    public ArrayList<Beneficiary> getBeneficiaries(BeneficiaryType type) {
    	return (type == BeneficiaryType.OWN_BANK) ? this.ownBankBeneficiaries : this.otherBankBeneficiaries;
    }
    
    // Returns a beneficiary with the given id.
    public Beneficiary getBeneficiary(BeneficiaryType type, int id) {
    	ArrayList<Beneficiary> beneficiaries;    	
    	beneficiaries = (type == BeneficiaryType.OWN_BANK) ? this.ownBankBeneficiaries : this.otherBankBeneficiaries;
    
    	for(Beneficiary beneficiary : beneficiaries)
    		if(beneficiary.getId() == id)
    			return beneficiary;
    	
    	return null;
    }
    
    public byte getAge() {
    	return this.age;
    }
    
    public char getGender() {
    	return this.gender;
    }
    
    public String getMartialStatus() {
    	return this.martialStatus;
    }
    
    public String getOccupation() {
    	return this.occupation;
    }
    
    public int getIncome() {
    	return this.income;
    }
    
    public long getAdhaar() {
    	return this.adhaar;
    }
    
    public String getPan() {
    	return this.pan;
    }
    
    public String getTransPassword() {
    	return this.transPassword;
    }
    
    public Address getAddress() {
    	return this.address;
    }
    
    public LocalDate getRemovedDate() {
    	return removedDate;
    }
    
    public boolean isRemoved() {
    	return removedDate != null;
    }
    
    // Setters
    public void setRemovedDate(LocalDate removedDate) {
    	this.removedDate = removedDate;
    }
    
    public void setAge(byte age) {
    	this.age = age;
    }
    
    public void setGender(char gender) {
    	this.gender = gender;
    }
    
    public void setMartialStatus(String martialStatus) {
    	this.martialStatus = martialStatus;
    }
    
    public void setIncome(int income) {
    	this.income = income;
    }
    
    public void setOccupation(String occupation) {
    	this.occupation = occupation;
    }
    
    public void setAdhaar(long adhaar) {
    	this.adhaar = adhaar;
    }
    
    public void setPan(String pan) {
    	this.pan = pan;
    }
    
    public void setTransPassword(String password) {
    	this.transPassword = password;
    }
    
    public void setAddress(Address address) {
    	this.address = address;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }
        
        Customer target = (Customer) obj;
        
        return (this.getName().equals(target.getName()) && this.getEmail().equals(target.getEmail()) 
        		&& this.getPhone() == target.getPhone() && this.getAge() == target.getAge() &&
        		this.getGender() == target.getGender() && this.getOccupation().equals(target.getOccupation())
        		&& this.getIncome() == target.getIncome() && this.getAdhaar() == target.getAdhaar() &&
        		this.getPan().equals(target.getPan()) && this.getAddress().equals(target.getAddress()));
    }
}