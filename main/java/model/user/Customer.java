package model.user;

import java.time.LocalDate;

import model.Address;

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
    
    /* private ArrayList<Beneficiary> ownBankBeneficiaries;
    private ArrayList<Beneficiary> otherBankBeneficiaries;
    private ArrayList<Nominee> nominees;
    // Mapping of A/C(s) no's to corresponding branch Id.
    private ConcurrentHashMap<Long, Integer> savingsAccounts; */

    // A customer can have only one current account.
    // A/C no, branch Id
//    private long currentAccountNo;
//    private int currentAccountBranchId;

    // private HashMap<Long, Integer> depositAccounts;


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
        
//        this.ownBankBeneficiaries = new ArrayList<Beneficiary>();
//        this.otherBankBeneficiaries = new ArrayList<Beneficiary>();
//        this.nominees = new ArrayList<Nominee>();
//        this.savingsAccounts = new ConcurrentHashMap<Long, Integer>();
//
//        this.currentAccountNo = -1;
//        this.currentAccountBranchId = -1;
        
        // this.depositAccounts = new HashMap<Long, Integer>();
    }


    // Add a mapping of account no and branch (if exists).
    /* public void addAccountBranchMapping(int type, long accountNo, int branchId) {        
        switch(type) {
            case AccountType.SAVINGS: this.savingsAccounts.put(accountNo, branchId);
                                        break;
            case AccountType.CURRENT: this.currentAccountNo = accountNo;
                                      this.currentAccountBranchId = branchId;
                                        break;
            // case AccountType.DEPOSIT: this.depositAccounts.put(accountNo, branchId);
            //                             break;
        }
    }
    

    // remove cached mapping of account no and branch (if exists).
    public void removeAccountBranchMapping(int type, long accountNo) {     
        switch(type) {
            case AccountType.SAVINGS: this.savingsAccounts.remove(accountNo);
                                        break;
            case AccountType.CURRENT: this.currentAccountNo = -1;
                                      this.currentAccountBranchId = -1;
                                        break;
            // case AccountType.DEPOSIT: this.depositAccounts.remove(accountNo);
            //                             break;
        }
    }


    public void addBeneficiary(BeneficiaryType type, Beneficiary beneficiary) {
        ArrayList<Beneficiary> beneficiaries;
        beneficiaries =  (type == BeneficiaryType.OWN_BANK) ? this.ownBankBeneficiaries : this.otherBankBeneficiaries;
        beneficiaries.add(beneficiary);
    }


    // Removes a beneficiary from cache.
    private void removeBeneficiary(BeneficiaryType type, long beneficiaryId) {
        int index = 0;
        ArrayList<Beneficiary> beneficiaries;
        beneficiaries = (type == BeneficiaryType.OWN_BANK) ? this.ownBankBeneficiaries : this.otherBankBeneficiaries;

        for(Beneficiary beneficiary : beneficiaries)
            if(beneficiaryId == beneficiary.getId())
                break;
            else
                ++index;

        beneficiaries.remove(index);
    } */
    
    // Getters
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
    
    public String toString() {
        String repr = "Role:     Customer";
        return super.toString() + repr;
    }
}