package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;

import model.Transaction;
import util.Factory;

public class TransactionDAO {
	public long create(Connection conn, int typeId, String description, Object fromAccountNo,
									Object toAccountNo, float amount, boolean fromAccountOwnBank,
									boolean toAccountOwnBank, float fromAccountBeforeBalance,
									float toAccountBeforeBalance) throws SQLException {
		PreparedStatement stmt1 = null, stmt2 = null;
		ResultSet rs1 = null;
		
		LocalDate today = LocalDate.now();
		LocalTime time = LocalTime.now();
		boolean exceptionOccured = false;
		String msg = "";
		long transactionId = -1;
		
		try {
            // Create transaction record.
            stmt1 = conn.prepareStatement("INSERT INTO transaction (type_id, description, from_account_no, to_account_no, amount, date, time) VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt2 = conn.prepareStatement("INSERT INTO account_transaction VALUES (?, ?, ?)");
            
            stmt1.setInt(1, typeId);
            stmt1.setString(2, description);
            stmt1.setObject(3, fromAccountNo);
            stmt1.setObject(4, toAccountNo);
            stmt1.setFloat(5, amount);
            stmt1.setDate(6, Date.valueOf(today));
            stmt1.setTime(7, Time.valueOf(time));
            stmt1.executeUpdate();

            rs1 = stmt1.getGeneratedKeys();
            if(rs1.next())
                transactionId = rs1.getLong("id");
            
            if(fromAccountOwnBank) {
                // associate transaction with account.
                stmt2.setObject(1, fromAccountNo);
                stmt2.setLong(2, transactionId);
                stmt2.setFloat(3, fromAccountBeforeBalance);
                stmt2.executeUpdate();	
            }
            
            if(toAccountOwnBank) {
                // associate transaction with account.
                stmt2.setObject(1, toAccountNo);
                stmt2.setLong(2, transactionId);
                stmt2.setFloat(3, toAccountBeforeBalance);
                stmt2.executeUpdate();	
            }
		} catch(SQLException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "internal error";
		} finally {
			try {
			    if(rs1 != null)
			        rs1.close();
			} catch(SQLException e) { System.out.println(e.getMessage()); }
			
			try {
			    if(stmt1 != null)
			        stmt1.close();
			    if(stmt2 != null)
			        stmt2.close();
			} catch(SQLException e) { System.out.println(e.getMessage()); }
		}
		
		if(exceptionOccured)
			throw new SQLException(msg);
		else
			return transactionId;
	}
	
	
	// Returns list of transactions on a given date range of an account.
	public LinkedList<Transaction> getAll(long accountNo, LocalDate fromDate, LocalDate toDate) throws SQLException {
		Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        LinkedList<Transaction> transactions = new LinkedList<Transaction>(); 
        Transaction transaction = null;
        boolean exceptionOccured = false;
        long transactionId, fromAccountNo, toAccountNo;
        String description, msg = "";
        float amount, balanceBeforeTransaction;
        int typeId;
        Date date;
        Time time;
        LocalDateTime dateTime;

        try {
            conn = Factory.getDataSource().getConnection();
            stmt = conn.prepareStatement("SELECT * FROM account_transaction at LEFT JOIN transaction t ON at.transaction_id = t.id WHERE at.account_no = ? AND (t.from_account_no = ? OR t.to_account_no = ?) AND (date BETWEEN ? AND ?)");
            stmt.setLong(1, accountNo);
            stmt.setLong(2, accountNo);
            stmt.setLong(3, accountNo);
            stmt.setDate(4, Date.valueOf(fromDate));
            stmt.setDate(5, Date.valueOf(toDate));

            rs = stmt.executeQuery();

            while(rs.next()) {
                transactionId = rs.getLong("id");
                typeId = rs.getInt("type_id");
                description = rs.getString("description");
                fromAccountNo = rs.getLong("from_account_no");
                toAccountNo = rs.getLong("to_account_no");
                amount = rs.getFloat("amount");
                balanceBeforeTransaction = rs.getFloat("before_balance");
                date = rs.getDate("date");
                time = rs.getTime("time");
                dateTime = LocalDateTime.parse(date.toString() + 'T' + time.toString());

                transaction = new Transaction(transactionId, typeId, fromAccountNo, toAccountNo, amount, dateTime, description, balanceBeforeTransaction);
                transactions.add(transaction);
            }
        }  catch(SQLException e) {
        	System.out.println(e.getMessage());
            exceptionOccured = true;
            msg = "internal error";
        } finally {
            try {
                if(stmt != null)
                    stmt.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }

            try {
                if(conn != null)
                    conn.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
        }
        
        if(exceptionOccured)
        	throw new SQLException(msg);
        else
        	return transactions;
	}
}