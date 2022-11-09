package model;

import java.util.concurrent.ConcurrentHashMap;

import constant.DepositAccountType;
import constant.RegularAccountType;
import model.user.Employee;
import model.account.*;

public class Branch {
    private final int id;
    public String name;
    public Address address;
    private Employee manager;
    private ConcurrentHashMap<Long, Employee> employees;
    private ConcurrentHashMap<Long, SavingsAccount> savingsAccounts;
    private ConcurrentHashMap<Long, CurrentAccount> currentAccounts;
    private ConcurrentHashMap<Long, DepositAccount> depositAccounts;

    
    // Used loading data from DB.
    public Branch(int id, String name, Address address) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.manager = null;
        this.employees = new ConcurrentHashMap<Long, Employee>();
        this.savingsAccounts = new ConcurrentHashMap<Long, SavingsAccount>();
        this.currentAccounts = new ConcurrentHashMap<Long, CurrentAccount>();
        this.depositAccounts = new ConcurrentHashMap<Long, DepositAccount>();
    }


    public void assignManager(Employee manager) {
        this.manager = manager;
    }


    public void addEmployee(Employee employee) {
        this.employees.put(employee.getId(), employee);
    }


    public void removeEmployee(long employeeId) {
        this.employees.remove(employeeId);
    }


    // Adds a account to the branch.
//    public void addAccount(int accountType, Account account, char type) {
//        switch(accountType) {
//            case RegularAccountType.SAVINGS.id: this.savingsAccounts.put(account.getAccountNo(), (SavingsAccount) account);
//                                      			break;
//            case RegularAccountType.CURRENT.id: this.currentAccounts.put(account.getAccountNo(), (CurrentAccount) account);
//                                      			break;
//            case DepositAccountType.FD.id: this.depositAccounts.put(account.getAccountNo(), (DepositAccount) account);
//                                       		break;
//            case DepositAccountType.FD.id: this.depositAccounts.put(account.getAccountNo(), (DepositAccount) account);
//       										break;
//        }
//        
//    }


    // Removes a account to the branch.
//    public void removeAccount(int accountType, long accountNo) {
//        switch(accountType) {
//            case AccountType.SAVINGS: this.savingsAccounts.remove(accountNo);
//                                        break;
//            case AccountType.CURRENT: this.currentAccounts.remove(accountNo);
//                                        break;
//            // case AccountType.DEPOSIT: this.depositAccounts.remove(accountNo);
//            //                             break;
//        }
//    }

    
    // Getters
    // Returns a account to the branch.
    public Account getAccount(long accountNo) {
        Account account = null;

        account = this.savingsAccounts.get(accountNo);
        if(account != null)
            return account;

        account = this.currentAccounts.get(accountNo);
        if(account != null)
            return account;
            
         account = this.depositAccounts.get(accountNo);
         if(account != null)
             return account;
        
        return null;
    }

    public Employee getManager() {
        return this.manager;
    }


    public Employee getEmployee(long employeeId) {
        return this.employees.get(employeeId);
    }
    
    public String getName() {
    	return name;
    }

    public Address getAddress() {
    	return address;
    }
    
    public int getId() {
        return this.id;
    }
    
    // setters
    public void setName(String name) {
    	this.name = name;
    }
    
    public void setAddress(Address address) {
    	this.address = address;
    }


    @Override
    public boolean equals(Object obj) {
    	System.out.println("in branch equals");
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }
        
        Branch target = (Branch) obj;
        
        return (this.name.equals(target.getName()) && this.address.equals(target.getAddress()));
    }
}