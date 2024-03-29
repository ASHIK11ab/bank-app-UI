package model;

import model.user.*;
import util.Util;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;


public class Bank {
    public String name;
    public String supportEmail;
    public long supportPhone;
    public String websiteURL;
    private User admin;
    private long accountNo;
    private int accountBranchId;
    
    // Internal hashmap '_branches' used by branch comparator for sorting branches
    // based on values.
    private HashMap<Integer, Branch> _branches;
    private SortedMap<Integer, Branch> branches; 
    
    private ConcurrentHashMap<Long, Customer> customers;
    private int customerCnt;
    
    // Internal hashmap '_integratedBanks' used by Integrated banks comparator for 
    // sorting branches based on values.
    private HashMap<Integer, IntegratedBank> _integratedBanks;
    private SortedMap<Integer, IntegratedBank> integratedBanks;
    
    private Comparator<Integer> branchComparator; 
    private Comparator<Integer> integratedBankComparator;
    
    // Mapping of debit card no to its associated (account number, branchId).
    private ConcurrentHashMap<Long, Properties> cardAccountBranchMap = null; 

    public Bank(String name, String supportEmail, long supportPhone, String websiteURL,
                long bankAccountNo, int accountBranchId) {
        this.name = name;
        this.supportEmail = supportEmail;
        this.supportPhone = supportPhone;
        this.websiteURL = websiteURL;
        this.accountNo = bankAccountNo;
        this.accountBranchId = accountBranchId;
        
        // Comparators (anonymous implementation).
        branchComparator = new Comparator<Integer>() {
        	
        	// Branches are compared based on their names (dictionary format).
            @Override
            public int compare(Integer key1, Integer key2) {            	
            	Branch base = _branches.get(key1);
            	Branch target = _branches.get(key2);
            	
            	if(base == null || target == null)
            		return -1;
            	            	
            	// When using get method of map.
            	if(key1 == key2) {
            		return 0;
            	}
            	
            	return Util.compareByName(base.getName(), target.getName());
            }
        };
        
        integratedBankComparator = new Comparator<Integer>() {
        	
        	// Integrated banks are compared based on their names (dictionary format).
            @Override
            public int compare(Integer key1, Integer key2) {            	
            	IntegratedBank base = _integratedBanks.get(key1);
            	IntegratedBank target = _integratedBanks.get(key2);
            	
            	if(base == null || target == null)
            		return -1;
            	
            	// When using get method of map.
            	if(key1 == key2)
            		return 0;
            	
            	return Util.compareByName(base.getName(), target.getName());
            }
        };
        
        this._branches = new HashMap<Integer, Branch>();
        this.branches = Collections.synchronizedSortedMap(new TreeMap<Integer, Branch>(branchComparator)); 
        
        this._integratedBanks = new HashMap<Integer, IntegratedBank>();
        this.integratedBanks = Collections.synchronizedSortedMap(new TreeMap<Integer, IntegratedBank>(integratedBankComparator));
        
        this.customers = new ConcurrentHashMap<Long, Customer>();
        this.customerCnt = 0;
        this.cardAccountBranchMap = new ConcurrentHashMap<Long, Properties>();
    }


    // Adds a customer to bank.
    public void addCustomer(Customer customer) {
        this.customers.put(customer.getId(), customer);
    }


    public void addBranch(Branch branch) {
        this._branches.put(branch.getId(), branch);
        
        // Branches are stored in sorted order by their name.
        this.branches.put(branch.getId(), branch);
    }


    public void addIntegratedBank(IntegratedBank integratedBank) {
    	this._integratedBanks.put(integratedBank.getId(), integratedBank);
        this.integratedBanks.put(integratedBank.getId(), integratedBank);
    }
    
    
    public void addCardAccountBranchMapping(long cardNo, Properties props) {
    	this.cardAccountBranchMap.put(cardNo, props);
    }


    // removals
    public void removeCustomer(long customerId) {
        this.customers.remove(customerId);
    }


    public void removeBranch(int branchId) {  	
    	this.branches.remove(branchId);
    	this._branches.remove(branchId);
    }
    
    public void removeIntegratedBank(int bankId) {
    	this.integratedBanks.remove(bankId);
    	this._integratedBanks.remove(bankId);
    }


    // Getters
    public Properties getCardAccountBranch(long cardNo) {
    	return this.cardAccountBranchMap.get(cardNo);
    }
    
    public long getBankAccountNo() {
        return this.accountNo;
    }
    
    public int getAccountBranchId() {
        return this.accountBranchId;
    }

    public User getAdmin() {
        return this.admin;
    }
    
    public int getCustomerCnt() {
    	return this.customerCnt;
    }


    public Customer getCustomer(long customerId) {
        return this.customers.get(customerId);
    }


    public Collection<Branch> getBranches() {
        return this.branches.values();
    }


    public Collection<IntegratedBank> getIntegratedBanks() {
        return this.integratedBanks.values();
    }

    
    public IntegratedBank getIntegratedBank(int bankId) {
        return this.integratedBanks.get(bankId);
    }


    public Branch getBranch(int branchId) {
        return this.branches.get(branchId);
    }
    
    public String getSupportEmail() {
    	return this.supportEmail;
    }

    public long getSupportPhone() {
    	return this.supportPhone;
    }

    // Setters
    public void setAdmin(User admin) {
        this.admin = admin;
    }
    
    public void setCustomerCnt(int cnt) {
    	this.customerCnt = cnt;
    }
}