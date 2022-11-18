package model;

import model.user.*;

import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


public class Bank {
    public String name;
    public String supportEmail;
    public long supportPhone;
    public String websiteURL;
    private User admin;
    private long accountNo;
    private int accountBranchId;
    private ConcurrentHashMap<Integer, Branch> branches;
    private ConcurrentHashMap<Long, Customer> customers;
    private ConcurrentHashMap<Integer, IntegratedBank> integratedBanks;
    
    // Mapping of debit card no to its associated account number, branchId.
    private ConcurrentHashMap<Long, Properties> cardAccountBranchMap = null; 

    public Bank(String name, String supportEmail, long supportPhone, String websiteURL,
                long bankAccountNo, int accountBranchId) {
        this.name = name;
        this.supportEmail = supportEmail;
        this.supportPhone = supportPhone;
        this.websiteURL = websiteURL;
        this.accountNo = bankAccountNo;
        this.accountBranchId = accountBranchId;
        branches = new ConcurrentHashMap<Integer, Branch>();
        customers = new ConcurrentHashMap<Long, Customer>();
        integratedBanks = new ConcurrentHashMap<Integer, IntegratedBank>();
        this.cardAccountBranchMap = new ConcurrentHashMap<Long, Properties>();
    }


    // Adds a customer to bank.
    public void addCustomer(Customer customer) {
        this.customers.put(customer.getId(), customer);
    }


    public void addBranch(Branch branch) {
        this.branches.put(branch.getId(), branch);
    }


    public void addIntegratedBank(IntegratedBank integratedBank) {
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
    }
    
    public void removeIntegratedBank(int bankId) {
        this.integratedBanks.remove(bankId);
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


    // Setters
    public void setAdmin(User admin) {
        this.admin = admin;
    }
}