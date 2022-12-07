package model.user;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import constant.AccountCategory;
import constant.BeneficiaryType;
import constant.RegularAccountType;
import dao.RegularAccountDAO;
import model.Address;
import model.Beneficiary;
import model.account.RegularAccount;
import model.card.DebitCard;
import util.Factory;


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
    
    private SortedSet<Beneficiary> ownBankBeneficiaries;
    private SortedSet<Beneficiary> otherBankBeneficiaries;
    
    // Mapping of A/C(s) no's to corresponding branch Id.
    private ConcurrentHashMap<Long, Integer> savingsAccounts;

    // A customer can have only one current account.
    // A/C no, branch Id
    private long currentAccountNo;
    private int currentAccountBranchId;

    // A/C no to branch id mapping.
    private ConcurrentHashMap<Long, Integer> depositAccounts;
    
    private Comparator<Beneficiary> beneficiaryComparator; 
   

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
        
        // Anonymous implementation.
        this.beneficiaryComparator = new Comparator<Beneficiary>() {
        	
        	// Beneficiaries are compared based on their names (dictionary format).
            @Override
            public int compare(Beneficiary base, Beneficiary target) {
            	if(base == null || target == null)
            		return -1;
            	
            	if(base.equals(target) && base.getId() == target.getId())
            		return 0;
            	
            	char baseCharacter, targetCharacter;
            	    	
            	int minLength = (base.getName().length() < target.getName().length()) ? base.getName().length() : target.getName().length();
            	
            	for(int index = 0; index < minLength; ++index) {
            		baseCharacter = Character.toLowerCase(base.getName().charAt(index));
            		targetCharacter = Character.toLowerCase(target.getName().charAt(index));
            		
            		if(baseCharacter != targetCharacter) {
            			return baseCharacter - targetCharacter;
            		}
            	}
            	
            	// When first 'minLength' characters are same, string with lesser length
            	// is stored first.
            	if(base.getName().length() <= target.getName().length())
            		return -1;
            	else
            		return 1;
            }
        };

        this.ownBankBeneficiaries = Collections.synchronizedSortedSet(new TreeSet<Beneficiary>(beneficiaryComparator));
        this.otherBankBeneficiaries = Collections.synchronizedSortedSet(new TreeSet<Beneficiary>(beneficiaryComparator));
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
        SortedSet<Beneficiary> beneficiaries;
        beneficiaries =  (type == BeneficiaryType.OWN_BANK) ? this.ownBankBeneficiaries : this.otherBankBeneficiaries;
        beneficiaries.add(beneficiary);
    }


    // Removes a beneficiary from cache.
    public void removeBeneficiary(BeneficiaryType type, long beneficiaryId) {
    	SortedSet<Beneficiary> beneficiaries = (type == BeneficiaryType.OWN_BANK) ? this.ownBankBeneficiaries : this.otherBankBeneficiaries;
        
    	Beneficiary beneficiary = this.getBeneficiary(type, beneficiaryId);
        
    	beneficiaries.remove(beneficiary);
    }
    
    
    public SortedSet<Beneficiary> getBeneficiaries(BeneficiaryType type) {
    	return (type == BeneficiaryType.OWN_BANK) ? this.ownBankBeneficiaries : this.otherBankBeneficiaries;
    }
    
    
    // Returns a beneficiary with the given id.
    public Beneficiary getBeneficiary(BeneficiaryType type, long id) {
    	SortedSet<Beneficiary> beneficiaries;    	
    	beneficiaries = (type == BeneficiaryType.OWN_BANK) ? this.ownBankBeneficiaries : this.otherBankBeneficiaries;
    
    	for(Beneficiary beneficiary : beneficiaries)
    		if(beneficiary.getId() == id)
    			return beneficiary;
    	
    	return null;
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
    
    // Returns a list of active accoutn no's of the customer.
    public Properties getActiveAccounts(AccountCategory category) throws SQLException {
    	Properties activeAccounts = new Properties();
    	RegularAccountDAO accountDAO = Factory.getRegularAccountDAO();
    	RegularAccount account = null;
    	LinkedList<Long> activeSavingsAccounts = new LinkedList<Long>();
    	
    	if(category == AccountCategory.REGULAR) {
    		
	    	// Add all active savings account to list.
			for(Entry<Long, Integer> set  : this.savingsAccounts.entrySet()) {
	    		account = accountDAO.get(set.getKey(), set.getValue());
	    		synchronized (account) {
					if(account.getIsActive())
						activeSavingsAccounts.add(account.getAccountNo());
				}
			}
			
			if(activeSavingsAccounts.size() > 0)
				activeAccounts.put(RegularAccountType.SAVINGS, activeSavingsAccounts);
			
			// Check if current account exists and active.
			// If so, add current account to active accounts.
			if(this.currentAccountNo != -1) {
				account = accountDAO.get(this.currentAccountNo, this.currentAccountBranchId);
				synchronized (account) {
					if(account.getIsActive())
						activeAccounts.put(RegularAccountType.CURRENT, this.currentAccountNo);
				}
			}
    	}
		
		return activeAccounts;
    }
    
    // Returns all of the debit cards associated with this customer.
    public TreeSet<DebitCard> getCards() throws SQLException {
    	RegularAccountDAO accountDAO = Factory.getRegularAccountDAO();
    	
    	RegularAccount account = null;
    	TreeSet<DebitCard> cards = new TreeSet<DebitCard>();
    	
    	int branchId;
    	
    	for(Long accountNo : this.getSavingsAccounts()) {
    		branchId = this.getAccountBranchId(AccountCategory.REGULAR, accountNo);
    		account = accountDAO.get(accountNo, branchId);
    		
    		for(DebitCard card : account.getCards())
    			if(!card.isDeactivated())
    				cards.add(card);
    	}
    	
    	if(this.currentAccountNo != -1) {
    		branchId = this.getAccountBranchId(AccountCategory.REGULAR, this.currentAccountNo);
    		account = accountDAO.get(this.currentAccountNo, branchId);
    		
    		for(DebitCard card : account.getCards())
    			if(!card.isDeactivated())
    				cards.add(card);
    	}
    	
    	return cards;
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
        
        return (this.getId() == target.getId() && this.getName().equals(target.getName()) && this.getEmail().equals(target.getEmail()) 
        		&& this.getPhone() == target.getPhone() && this.getAge() == target.getAge() &&
        		this.getGender() == target.getGender() && this.getOccupation().equals(target.getOccupation())
        		&& this.getIncome() == target.getIncome() && this.getAdhaar() == target.getAdhaar() &&
        		this.getPan().equals(target.getPan()) && this.getAddress().equals(target.getAddress()));
    }
}