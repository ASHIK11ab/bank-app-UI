package model.account;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import model.Nominee;
import model.Transaction;

public abstract class RegularAccount extends Account {
    private boolean isActive;
    private LinkedList<Transaction> recentTransactions;


    public abstract int initiateTransaction(float amount);
    
    public RegularAccount(long accountNo, long customerId, String customerName, 
    						Nominee nominee, int branchId, float amount, 
    						LocalDate openingDate, LocalDate closingDate, int typeId, boolean activeStatus) {
		super(accountNo, customerId, customerName, nominee, branchId, amount, openingDate, closingDate, typeId);
		this.isActive = activeStatus;
		this.recentTransactions = new LinkedList<Transaction>();
	}
    
    
    public void addTransaction(Transaction newTransaction) {
    	LocalDate today = LocalDate.now();
    	ListIterator<Transaction> it = null;
    	Transaction transaction = null;
    	    	
    	this.recentTransactions.addFirst(newTransaction);
    	
    	// Only keep recent 10 transactions or transactions that were performed
    	// today, if limit exceedes, remove unwanted transactions from the end.
    	if(this.recentTransactions.size() > 10) {
    		it = this.recentTransactions.listIterator(this.recentTransactions.size());
    		
    		while(it.hasPrevious() && this.recentTransactions.size() > 10) {
    			transaction = it.previous();
    			if(transaction.getDate().isBefore(today))
    				it.remove();
    		}
    	}
    	
    }
    
    
    // Getters
    public boolean getIsActive() {
    	return this.isActive;
    }
    
    
    protected LinkedList<Transaction> getRecentTransactions() {
    	return this.recentTransactions;
    }
    
    
    // Returns the recent 10 transactions
    public LinkedList<Transaction> getMiniStatement() {
    	byte cnt = 0;
    	LinkedList<Transaction> transactions = new LinkedList<Transaction>();
    	
    	Iterator<Transaction> it = this.recentTransactions.iterator();
    	
    	while(it.hasNext()) {
    		if(cnt == 10)
    			break;
    		
    		transactions.add(it.next());
    	}
    	
    	return transactions;
    }
    
     
    // Setters
    @Override
    public void setClosingDate(LocalDate date) { 
    	super.setClosingDate(date);
    	this.setIsActive(false);
    }
    
    public void setIsActive(boolean status) {
    	this.isActive = status;
    }
}