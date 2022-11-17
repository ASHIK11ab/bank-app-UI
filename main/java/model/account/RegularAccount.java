package model.account;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.TreeSet;

import model.Nominee;
import model.Transaction;
import model.card.DebitCard;

public abstract class RegularAccount extends Account {
    private boolean isActive;
    private TreeSet<DebitCard> cards;
    
    private LinkedList<Transaction> recentTransactions;


    public abstract int initiateTransaction(float amount);
    
    public RegularAccount(long accountNo, long customerId, String customerName, 
    						Nominee nominee, int branchId, float amount, 
    						LocalDate openingDate, LocalDate closingDate, int typeId, boolean activeStatus) {
		super(accountNo, customerId, customerName, nominee, branchId, amount, openingDate, closingDate, typeId);
		this.isActive = activeStatus;
		this.recentTransactions = new LinkedList<Transaction>();
		this.cards = new TreeSet<DebitCard>();
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
    
    
    public void addDebitCard(DebitCard card) {
    	this.cards.add(card);
    }
    
    
    public void removeDebitCard(DebitCard card) {
    	this.cards.remove(card);
    }
    
    
    // Getters
    public boolean getIsActive() {
    	return this.isActive;
    }
    
    
    public TreeSet<DebitCard> getCards() {
    	return this.cards;
    }
    
    
//    public DebitCard getCard(long cardNo) {
//    	DebitCard card = null;
//    	
//    	for(DebitCard debitCard : this.cards)
//    		if(debitCard.getCardNo() == cardNo) {
//    			card = debitCard;
//    			break;
//    		}
//    	
//    	return card;
//    }
    
    
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