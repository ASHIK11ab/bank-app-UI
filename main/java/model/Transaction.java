package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private final long id;
    private final int type;
    private final String description;
    private final long fromAccountNo;
    private final long toAccountNo;
    private final float amount;
    private final float balanceBeforeTransaction;
    private final LocalDateTime dateTime;

    public Transaction(long id, int type, long fromAccountNo, long toAccountNo,
                        float amount, LocalDateTime dateTime, String description,
                        float balanceBeforeTransaction) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.fromAccountNo = fromAccountNo;
        this.toAccountNo = toAccountNo;
        this.amount = amount;
        this.dateTime = dateTime;
        this.balanceBeforeTransaction = balanceBeforeTransaction;
    }


    // Getters
    public LocalDate getDate() {
        return this.dateTime.toLocalDate();
    }
    
    public LocalTime getTime() {
    	return this.dateTime.toLocalTime();
    }

    public float getAmount() {
        return this.amount;
    }
    
    public float getBalanceBeforeTransaction() {
    	return balanceBeforeTransaction;
    }

    public long getToAccountNo() {
        return this.toAccountNo;
    }

    public long getFromAccountNo() {
        return this.fromAccountNo;
    }

    public String getDescription() {
        return this.description;
    }

    public long getId() {
        return this.id;
    }

    public int getType() {
        return this.type;
    }
}