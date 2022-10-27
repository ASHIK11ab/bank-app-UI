package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.AddressBean;
import model.CustomerBean;
import util.Factory;
import util.Util;

public class CustomerDAO {
	public CustomerBean create(Connection conn, String name, long phone, String email, 
								byte age, char gender, String martialStatus,
					            String occupation, int income, long adhaar, String pan,
					            AddressBean address) throws SQLException {
		PreparedStatement stmt1 = null, stmt2 = null;
		ResultSet rs = null;
		
		CustomerBean customer = null;
		
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
                        
            customer = new CustomerBean();
            customer.setId(customerId);
            customer.setName(name);
            customer.setEmail(email);
            customer.setPhone(phone);
            customer.setPassword(password);
            customer.setTransPassword(transactionPassword);
            customer.setAge(age);
            customer.setGender(gender);
            customer.setOccupation(occupation);
            customer.setMartialStatus(martialStatus);
            customer.setIncome(income);
            customer.setAdhaar(adhaar);
            customer.setPan(pan);
            customer.setAddress(address);
		} catch(SQLException e) {
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
		Connection conn = Factory.getDataSource().getConnection();
		PreparedStatement stmt = null;
		
		String msg = "";
		boolean exceptionOccured = false;
		int rowsAffected = -1;
		
		try {
			stmt = conn.prepareStatement("DELETE FROM customer WHERE id = ?");
			stmt.setLong(1, customerId);
			rowsAffected = stmt.executeUpdate();
			System.out.println(rowsAffected);
		} catch(SQLException e) {
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
	
	
	public CustomerBean get(long customerId) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		CustomerBean customer = null;
		AddressBean address = null;
		boolean exceptionOccured = false;
		String doorNo, street, city, state, msg = "";
		int pincode;
		
		try {
			conn = Factory.getDataSource().getConnection();
            stmt = conn.prepareStatement("SELECT * FROM customer c JOIN customer_info c_info ON c.id = c_info.customer_id WHERE c.id = ?");
            stmt.setLong(1, customerId);
            
            rs = stmt.executeQuery();
            
            if(rs.next()) {
                customer = new CustomerBean();
                customer.setId(customerId);
                customer.setName(rs.getString("name"));
                customer.setEmail(rs.getString("email"));
                customer.setPhone(rs.getLong("phone"));
                customer.setPassword(rs.getString("password"));
                customer.setTransPassword(rs.getString("transaction_password"));
                customer.setAge(rs.getByte("age"));
                customer.setGender(rs.getString("gender").charAt(0));
                customer.setOccupation(rs.getString("occupation"));
                customer.setIncome(rs.getInt("income"));
                customer.setAdhaar(rs.getLong("adhaar"));
                customer.setPan(rs.getString("pan"));
                
                address = new AddressBean();
                address.setDoorNo(rs.getString("door_no"));
                address.setStreet(rs.getString("street"));
                address.setCity(rs.getString("city"));
                address.setState(rs.getString("state"));
                address.setPincode(rs.getInt("pincode"));
               
                customer.setAddress(address);	
            }
		} catch(SQLException e) {
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
}