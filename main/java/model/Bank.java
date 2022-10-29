package model;

import model.user.*;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;


public class Bank {
    public String name;
    public String supportEmail;
    public long supportPhone;
    public String websiteURL;
    private User admin;
    private long bankAccountNo;
    private ConcurrentHashMap<Integer, Branch> branches;
    private ConcurrentHashMap<Long, Customer> customers;
    private ConcurrentHashMap<Integer, IntegratedBank> integratedBanks;

    public Bank(String name, String supportEmail, long supportPhone, String websiteURL,
                long bankAccountNo) {
        this.name = name;
        this.supportEmail = supportEmail;
        this.supportPhone = supportPhone;
        this.websiteURL = websiteURL;
        this.bankAccountNo = bankAccountNo;
        branches = new ConcurrentHashMap<Integer, Branch>();
        customers = new ConcurrentHashMap<Long, Customer>();
        integratedBanks = new ConcurrentHashMap<Integer, IntegratedBank>();
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


    // removals
    public void removeCustomer(long customerId) {
        this.customers.remove(customerId);
    }


    public void removeBranch(int branchId) {
        this.branches.remove(branchId);
    }
    
    public void addIntegratedBank(int bankId) {
        this.integratedBanks.remove(bankId);
    }


    // Getters
    public long getBankAccountNo() {
        return this.bankAccountNo;
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


    public ConcurrentHashMap<Integer, IntegratedBank> getIntegratedBanks() {
        return this.integratedBanks;
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


    public String toString() {
        String repr = "";
        repr += "\nBank Name         : " + name;
        repr += "\nSupport Email     : " + supportEmail;
        repr += "\nSupport Phone     : " + supportPhone;
        repr += "\nWebsite URL       : " + websiteURL;
        return repr;
    }
}