package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private final long id;
    private final int type;
    private final String description;
    private final long fromAccountNo;
    private final long toAccountNo;
    private final float amount;
    private final LocalDateTime dateTime;

    public Transaction(long id, int type, long fromAccountNo, long toAccountNo,
                        float amount, LocalDateTime dateTime, String description) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.fromAccountNo = fromAccountNo;
        this.toAccountNo = toAccountNo;
        this.amount = amount;
        this.dateTime = dateTime;
    }


    // Getters
    public LocalDate getDate() {
        return this.dateTime.toLocalDate();
    }

    public float getAmount() {
        return this.amount;
    }

    public long getToAccountNo() {
        return this.fromAccountNo;
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

    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyy hh:mm a");
        
        String repr = "";
        repr += "\nTransaction id : " + this.id;
        repr += "\nDate & Time    : " + fmt.format(dateTime);
        repr += "\nPayer A/C      : " + this.fromAccountNo;
        repr += "\nPayee A/C      : " + this.toAccountNo;
        repr += "\nDescription    : " + this.description;
        repr += "\nAmount         : " + this.amount;
        return repr;
    }
}