package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import constant.AccountCategory;
import model.Address;
import model.user.Customer;
import util.Factory;
import util.Util;

public class CustomerDAO {
	public Customer create(Connection conn, String name, long phone, String email, 
								byte age, char gender, String martialStatus,
					            String occupation, int income, long adhaar, String pan,
					            Address address) throws SQLException {
		PreparedStatement stmt1 = null, stmt2 = null;
		ResultSet rs = null;
		
		Customer customer = null;
		
		boolean exceptionOccured = false;
		String msg = "", password = Util.genPassword(), transactionPassword = Util.genPassword();
		long customerId = -1;
		
		try {
			conn = Factory.getDataSource().getConnection();
			stmt1 = conn.prepareStatement("INSERT INTO customer (name, phone, email, password, transaction_password) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt2 = conn.prepareStatement("INSERT INTO customer_info (customer_id, age, gender, martial_status, occupation, income, adhaar, pan, door_no, street, city, state, pincode) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            
            stmt1.setString(1, name);
            stmt1.setLong(2, phone);
            stmt1.setString(3, email);
            stmt1.setString(4, password);
            stmt1.setString(5, transactionPassword);
            
            stmt1.executeUpdate();
            rs = stmt1.getGeneratedKeys();
            if(rs.next())
                customerId = rs.getLong(1);

            stmt2.setLong(1, customerId);
            stmt2.setInt(2, age);
            stmt2.setString(3, "" + gender);
            stmt2.setString(4, martialStatus);
            stmt2.setString(5, occupation);
            stmt2.setInt(6, income);
            stmt2.setLong(7, adhaar);
            stmt2.setString(8, pan);
            stmt2.setString(9, address.getDoorNo());
            stmt2.setString(10, address.getStreet());
            stmt2.setString(11, address.getCity());
            stmt2.setString(12, address.getState());
            stmt2.setInt(13, address.getPincode());
            stmt2.executeUpdate();
            
            customer = new Customer(customerId, name, password, phone, email, age,
				                    gender, martialStatus, occupation, income, adhaar, pan, 
				                    transactionPassword, address, null);
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
                if(stmt1 != null)
                    stmt1.close();
                if(stmt2 != null)
                    stmt2.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }
        }
		
		if(exceptionOccured)
			throw new SQLException(msg);
		else
			return customer;
	}
	
	
	public boolean delete(long customerId) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		String msg = "";
		boolean exceptionOccured = false;
		int rowsAffected = -1;
		
		try {
			conn = Factory.getDataSource().getConnection();
			stmt = conn.prepareStatement("DELETE FROM customer WHERE id = ?");
			stmt.setLong(1, customerId);
			rowsAffected = stmt.executeUpdate();
		} catch(SQLException e) {
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
			return rowsAffected == 1;
	}
	
	
	public Customer get(long customerId) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		Customer customer = null;
		Address address = null;
		boolean exceptionOccured = false;
		
		LocalDate removedDate = null;
        String name, email, pan, martialStatus, occupation;
        String transPassword, customerPassword;
        String doorNo, street, city, state, msg="";
        long phone, adhaar;
        int pincode, income;
        char gender;
        byte age;
		
		try {
			conn = Factory.getDataSource().getConnection();
            stmt = conn.prepareStatement("SELECT * FROM customer c JOIN customer_info c_info ON c.id = c_info.customer_id WHERE c.id = ?");
            stmt.setLong(1, customerId);
            
            rs = stmt.executeQuery();
            
            if(rs.next()) {
                name = rs.getString("name");
                customerPassword = rs.getString("password");
                email = rs.getString("email");
                phone = rs.getLong("phone");
                transPassword = rs.getString("transaction_password");
                age = rs.getByte("age");
                gender = rs.getString("gender").charAt(0);
                occupation = rs.getString("occupation");
                income = rs.getInt("income");
                martialStatus = rs.getString("martial_status");
                adhaar = rs.getLong("adhaar");
                pan = rs.getString("pan");
                doorNo = rs.getString("door_no");
                street = rs.getString("street");
                city = rs.getString("city");
                state = rs.getString("state");
                pincode = rs.getInt("pincode");
                removedDate = (rs.getDate("removed_date") != null) ? rs.getDate("removed_date").toLocalDate() : null;
                
                address = new Address(doorNo, street, city, state, pincode);
                
                customer = new Customer(customerId, name, customerPassword, phone, email, age,
                                        gender, martialStatus, occupation, income, adhaar, pan, 
                                        transPassword, address, removedDate);	
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
			return customer;
 	}
	
	
	public Customer update(long customerId, String name, long phone, String email, 
							byte age, char gender, String martialStatus,
				            String occupation, int income, long adhaar, String pan,
				            Address address) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt1 = null, stmt2 = null;
		
		Customer customer = null;
		boolean exceptionOccured = false;
		String msg = "";
				
		try {
			conn = Factory.getDataSource().getConnection();
			stmt1 = conn.prepareStatement("UPDATE customer SET name = ?, phone = ?, email = ? WHERE id = ?");
			stmt2 = conn.prepareStatement("UPDATE customer_info SET age = ?, gender = ?, martial_status = ?, occupation = ?, income = ?, adhaar = ?, pan = ?, door_no = ?, street = ?, city = ?, state = ?, pincode = ? WHERE customer_id = ?");
			
			stmt1.setString(1, name);
			stmt1.setLong(2, phone);
			stmt1.setString(3, email);
			stmt1.setLong(4, customerId);
			
			stmt1.executeUpdate();
			
			stmt2.setInt(1, age);
			stmt2.setString(2, "" + gender);
			stmt2.setString(3, martialStatus);
			stmt2.setString(4, occupation);
			stmt2.setInt(5, income);
			stmt2.setLong(6, adhaar);
			stmt2.setString(7, pan);
			stmt2.setString(8, address.getDoorNo());
			stmt2.setString(9, address.getStreet());
			stmt2.setString(10, address.getCity());
			stmt2.setString(11, address.getState());
			stmt2.setInt(12, address.getPincode());
			stmt2.setLong(13, customerId);
			stmt2.executeUpdate();
			
			// update in cache.
		} catch(SQLException e) {
			System.out.println(e.getMessage());
			exceptionOccured = true;
			msg = "internal error";
		} finally {
		
			try {
				if(stmt1 != null)
					stmt1.close();
				if(stmt2 != null)
					stmt2.close();
			} catch(SQLException e) { System.out.println(e.getMessage()); }
			
			try {
				if(conn != null)
					conn.close();
			} catch(SQLException e) { System.out.println(e.getMessage()); }
		}
		
		if(exceptionOccured)
			throw new SQLException(msg);
		else
			return customer;
	}
	
	
	// Customer is removed only when his last account is closed.
	// sets the remove date of customer to today and prevents from further usage of the customer account.
	public void removeCustomer(Connection conn, long customerId, long accountNo) throws SQLException {
		PreparedStatement stmt = null;
		
		AccountDAO accountDAO = Factory.getAccountDAO();
		LocalDate today = LocalDate.now();
		String msg = "";
		boolean exceptionOccured = false;
		
		try {
			conn = Factory.getDataSource().getConnection();
			stmt = conn.prepareStatement("UPDATE customer SET removed_date = ? WHERE id = ?");
			
			// closes last account associated with the customer.
			accountDAO.closeAccount(conn, accountNo, AccountCategory.REGULAR);
				
			// set customer status as removed.
			stmt.setDate(1, Date.valueOf(today));
			stmt.setLong(2, customerId);
			stmt.executeUpdate();
			
			// update in cache.
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