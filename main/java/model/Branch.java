package model;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import constant.AccountCategory;
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
    
    private int employeeCnt;
    private int savingsAccountCnt;
    private int currentAccountCnt;
    private int depositAccountCnt;

    
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
        
        this.employeeCnt = 0;
        this.savingsAccountCnt = 0;
        this.currentAccountCnt = 0;
        this.depositAccountCnt = 0;
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
    public void addAccount(AccountCategory category, int accountType, Account account) {
    	RegularAccountType type;
    	
    	switch(category) {
	    	case REGULAR: 
	    					type = RegularAccountType.getType(accountType);
				            switch(type) {
					            case SAVINGS: this.savingsAccounts.put(account.getAccountNo(), (SavingsAccount) account);
					            			  break;
					            case CURRENT: this.currentAccounts.put(account.getAccountNo(), (CurrentAccount) account);
					            			  break;
				            }
				            break;
	    	case DEPOSIT: this.depositAccounts.put(account.getAccountNo(), (DepositAccount) account);
	    				  break;
	    	default: break;
    	}
        
    }


    // Removes a account to the branch.
    public void removeAccount(long accountNo) {    	
    	if(this.savingsAccounts.containsKey(accountNo)) {
    		this.savingsAccounts.remove(accountNo);
    		return;
    	}
    	
    	if(this.currentAccounts.containsKey(accountNo)) {
    		this.currentAccounts.remove(accountNo);
    		return;
    	}
    	
    	if(this.depositAccounts.containsKey(accountNo)) {
    		this.depositAccounts.remove(accountNo);
    		return;
    	} 
    }

    
    // Getters
    
    public Collection<SavingsAccount> getSavingsAccounts() {
        return this.savingsAccounts.values();
    }


    public Collection<CurrentAccount> getCurrentAccounts() {
        return this.currentAccounts.values();
    }

     public Collection<DepositAccount> getDepositAccounts() {
         return this.depositAccounts.values();
     }
    
    // Returns a account to the branch.
    public Account getAccount(AccountCategory category, long accountNo) {
        Account account = null;
        
        switch(category) {
	        case REGULAR:   account = this.savingsAccounts.get(accountNo);
					        if(account != null)
					            return account;
					
					        account = this.currentAccounts.get(accountNo);
					        if(account != null)
					            return account;
					        break;
	        case DEPOSIT: account = this.depositAccounts.get(accountNo);
				          if(account != null)
				             return account;
	        default: break;
        }
        
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
    
    public int getEmployeeCnt() {
    	return this.employeeCnt;
    }
    
    public int getSavingsAccountCnt() {
    	return this.savingsAccountCnt;
    }
    
    public int getCurrentAccountCnt() {
    	return this.currentAccountCnt;
    }
    
    public int getDepositAccountCnt() {
    	return this.depositAccountCnt;
    }
    
    // setters
    public void setName(String name) {
    	this.name = name;
    }
    
    public void setAddress(Address address) {
    	this.address = address;
    }
    
    public void setEmployeeCnt(int cnt) {
    	this.employeeCnt = cnt;
    }
    
    public void setSavingsAccountCnt(int cnt) {
    	this.savingsAccountCnt = cnt;
    }
    
    public void setCurrentAccountCnt(int cnt) {
    	this.currentAccountCnt = cnt;
    }
    
    public void setDepositAccountCnt(int cnt) {
    	this.depositAccountCnt = cnt;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }
        
        Branch target = (Branch) obj;
        return (this.id == target.getId() && this.name.equals(target.getName()) &&
        		this.address.equals(target.getAddress()));
    }
}