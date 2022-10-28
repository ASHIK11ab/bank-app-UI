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

import model.TransactionBean;
import util.Factory;

public class TransactionDAO {
	public TransactionBean create(Connection conn, int typeId, String description, long fromAccountNo,
									long toAccountNo, float amount, boolean fromAccountOwnBank,
									boolean toAccountOwnBank, float fromAccountBeforeBalance,
									float toAccountBeforeBalance) throws SQLException {
		PreparedStatement stmt1 = null, stmt2 = null;
		ResultSet rs1 = null;
		
		LocalDate today = LocalDate.now();
		LocalTime time = LocalTime.now();
		TransactionBean transaction = null;
		boolean exceptionOccured = false;
		String msg = "";
		long transactionId = -1;
		
		try {
			conn = Factory.getDataSource().getConnection();
            // Create transaction record.
            stmt1 = conn.prepareStatement("INSERT INTO transaction (type_id, description, from_account_no, to_account_no, amount, date, time) VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt2 = conn.prepareStatement("INSERT INTO account_transaction VALUES (?, ?, ?)");
            
            stmt1.setInt(1, typeId);
            stmt1.setString(2, description);
            stmt1.setLong(3, fromAccountNo);
            stmt1.setLong(4, toAccountNo);
            stmt1.setFloat(5, amount);
            stmt1.setDate(6, Date.valueOf(today));
            stmt1.setTime(7, Time.valueOf(time));
            stmt1.executeUpdate();

            rs1 = stmt1.getGeneratedKeys();
            if(rs1.next())
                transactionId = rs1.getLong("id");
            
            if(fromAccountOwnBank) {
                // associate transaction with account.
                stmt2.setLong(1, fromAccountNo);
                stmt2.setLong(2, transactionId);
                stmt2.setFloat(3, fromAccountBeforeBalance);
                stmt2.executeUpdate();	
            }
            
            if(toAccountOwnBank) {
                // associate transaction with account.
                stmt2.setLong(1, toAccountNo);
                stmt2.setLong(2, transactionId);
                stmt2.setFloat(3, toAccountBeforeBalance);
                stmt2.executeUpdate();	
            }
            
            transaction = new TransactionBean();
            transaction.setId(transactionId);
            transaction.setDescription(description);
            transaction.setFromAccountNo(fromAccountNo);
            transaction.setToAccountNo(toAccountNo);
            transaction.setAmount(amount);
            transaction.setTypeId(typeId);
            transaction.setDateTime(LocalDateTime.parse(today.toString() + 'T' + time.toString()));
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
			return transaction;
	}
}