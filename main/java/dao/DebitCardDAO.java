package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.LinkedList;

import model.card.DebitCard;
import util.Factory;
import util.Util;

public class DebitCardDAO {	
	public DebitCard create(Connection conn, long linkedAccountNo, byte typeId) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		DebitCard card = null;
		LocalDate today = LocalDate.now(), validFromDate, expiryDate;
		long cardNo = -1;
		int pin, cvv;
		boolean exceptionOccured = false;
		String msg = "";
		
		try {
            stmt = conn.prepareStatement("INSERT INTO debit_card (account_no, valid_from, expiry_date, type_id, pin, cvv) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            pin = (byte) Util.genPin(4);
            cvv = (byte) Util.genPin(3);
            
            validFromDate = today.plusDays(10);
            expiryDate = validFromDate.plusYears(3);
            
            stmt.setLong(1, linkedAccountNo);
            stmt.setDate(2, Date.valueOf(validFromDate));
            stmt.setDate(3, Date.valueOf(expiryDate));
            stmt.setByte(4, typeId);
            stmt.setInt(5, pin);
            stmt.setInt(6, cvv);
            stmt.executeUpdate();
            
            rs = stmt.getGeneratedKeys();
            if(rs.next())
            	cardNo = rs.getLong("card_no");
            
            // On creation, card is not activated and active status is false.
            card = new DebitCard(cardNo, linkedAccountNo, validFromDate, expiryDate, typeId, false, pin, cvv, null, null);
		} catch(SQLException e) {
			System.out.println(e.getMessage());
            exceptionOccured = true;
            msg = "internal error";
        } finally {
            try {
                if(rs != null)
                    rs.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
            
            try {
                if(stmt != null)
                    stmt.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
        }
		
		if(exceptionOccured)
			throw new SQLException(msg);
		else
			return card;
	}
	
	
	// Returns a debit card.
	public DebitCard get(long cardNo) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		DebitCard card = null;
		LocalDate validFromDate, expiryDate, activatedDate, deactivatedDate;
		long linkedAccountNo;
		byte typeId;
		int pin, cvv;
		boolean exceptionOccured = false, isActive;
		String msg = "";
		
		try {
			conn = Factory.getDataSource().getConnection();
            stmt = conn.prepareStatement("SELECT * FROM debit_card WHERE card_no = ?");
            stmt.setLong(1, cardNo);
            
            rs = stmt.executeQuery();
            if(rs.next()) {
                linkedAccountNo = rs.getLong("account_no");
                validFromDate = rs.getDate("valid_from").toLocalDate();
                expiryDate = rs.getDate("expiry_date").toLocalDate();
                typeId = rs.getByte("type_id");
                pin = rs.getInt("pin");
                cvv = rs.getInt("cvv");
                isActive = rs.getBoolean("is_active");
                activatedDate = (rs.getDate("activated_date") != null) ? rs.getDate("activated_date").toLocalDate() : null;
                deactivatedDate = (rs.getDate("deactivated_date") != null) ? rs.getDate("deactivated_date").toLocalDate() : null;
                
                card = new DebitCard(cardNo, linkedAccountNo, validFromDate, expiryDate, typeId, isActive, pin, cvv, activatedDate, deactivatedDate);
            }
            
		} catch(SQLException e) {
			System.out.println(e.getMessage());
            exceptionOccured = true;
            msg = "internal error";
        } finally {
            try {
                if(rs != null)
                    rs.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
            
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
			return card;
	}
	
	
	// Returns all debit cards linked with a account.
	public LinkedList<DebitCard> getAll(Connection conn, long accountNo) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		LinkedList<DebitCard> cards = new LinkedList<DebitCard>(); 
		DebitCard card = null;
		LocalDate validFromDate, expiryDate, activatedDate, deactivatedDate;
		long cardNo;
		byte typeId;
		int pin, cvv;
		boolean exceptionOccured = false, isActive;
		String msg = "";
		
		try {
			conn = Factory.getDataSource().getConnection();
            stmt = conn.prepareStatement("SELECT * FROM debit_card WHERE account_no = ?");
            stmt.setLong(1, accountNo);
            
            rs = stmt.executeQuery();
            while(rs.next()) {
                cardNo = rs.getLong("card_no");
                validFromDate = rs.getDate("valid_from").toLocalDate();
                expiryDate = rs.getDate("expiry_date").toLocalDate();
                typeId = rs.getByte("type_id");
                pin = rs.getInt("pin");
                cvv = rs.getInt("cvv");
                isActive = rs.getBoolean("is_active");
                activatedDate = (rs.getDate("activated_date") != null) ? rs.getDate("activated_date").toLocalDate() : null;
                deactivatedDate = (rs.getDate("deactivated_date") != null) ? rs.getDate("deactivated_date").toLocalDate() : null;
                
                card = new DebitCard(cardNo, accountNo, validFromDate, expiryDate, typeId, isActive, pin, cvv, activatedDate, deactivatedDate);
                cards.add(card);
            }
            
		} catch(SQLException e) {
			System.out.println(e.getMessage());
            exceptionOccured = true;
            msg = "internal error";
        } finally {
            try {
                if(rs != null)
                    rs.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
            
            try {
                if(stmt != null)
                    stmt.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
        }
		
		if(exceptionOccured)
			throw new SQLException(msg);
		else
			return cards;
	}
	
	
	public void setCardActiveStatus(Connection conn, long cardNo, boolean activeStatus) throws SQLException {
		PreparedStatement stmt = null;
		
		boolean exceptionOccured = false;
		String msg = "";
		
		try {
			conn = Factory.getDataSource().getConnection();
            stmt = conn.prepareStatement("UPDATE debit_card SET is_active = ? WHERE card_no = ?");
            stmt.setBoolean(1, activeStatus);
            stmt.setLong(2, cardNo);
            stmt.executeUpdate();
            
		} catch(SQLException e) {
			System.out.println(e.getMessage());
            exceptionOccured = true;
            msg = "internal error";
        } finally {            
            try {
                if(stmt != null)
                    stmt.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
        }
		
		if(exceptionOccured)
			throw new SQLException(msg);
	}
	
	
	// public interface
	public void activateCard(Connection conn, long cardNo) throws SQLException {
		_setCardActivationStatus(conn, cardNo, true);
	}
	
	
	public void deactivateCard(Connection conn, long cardNo) throws SQLException {
		_setCardActivationStatus(conn, cardNo, false);
	}
	
	
	// internal method used when activating / deactivating a card.
	private void _setCardActivationStatus(Connection conn, long cardNo, boolean activationStatus) throws SQLException {
		PreparedStatement stmt = null;
		
		boolean exceptionOccured = false;
		String msg = "";
		
		try {			
			if(activationStatus == true)
				stmt = conn.prepareStatement("UPDATE debit_card SET activated_date = ? WHERE card_no = ?");
			else
				stmt = conn.prepareStatement("UPDATE debit_card SET deactivated_date = ? WHERE card_no = ?");
			
			
            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            stmt.setLong(2, cardNo);
            stmt.executeUpdate();
            
            // Once activated or deactivated update card status respectively.
            setCardActiveStatus(conn, cardNo, activationStatus);
		} catch(SQLException e) {
			System.out.println(e.getMessage());
            exceptionOccured = true;
            msg = "internal error";
        } finally {            
            try {
                if(stmt != null)
                    stmt.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
        }
		
		if(exceptionOccured)
			throw new SQLException(msg);
	}
}