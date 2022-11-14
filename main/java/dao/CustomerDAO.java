package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import cache.AppCache;
import constant.AccountCategory;
import constant.BeneficiaryType;
import model.Address;
import model.Bank;
import model.Beneficiary;
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
			// remove from cache if exists
			AppCache.getBank().removeCustomer(customerId);
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
		PreparedStatement stmt = null, stmt2 = null, stmt3 = null, stmt4 = null, stmt5 = null;
		ResultSet rs = null, rs2 = null, rs3 = null, rs4 = null, rs5 = null;
		
		Bank bank = AppCache.getBank();
		Customer customer = null;
		Address address = null;
		boolean exceptionOccured = false;
		
        Beneficiary beneficiary;
        long beneficiaryAccountNo, beneficiaryId = -1;
        String beneficiaryName, beneficiaryNickName, beneficiaryIfsc = "";
        int bankId = -1;
        
        long accountNo;
        int accountBranchId, type_id;
		
		LocalDate removedDate = null;
        String name, email, pan, martialStatus, occupation;
        String transPassword, customerPassword;
        String doorNo, street, city, state, msg="";
        long phone, adhaar;
        int pincode, income;
        char gender;
        byte age;
		
		try {
			// try getting from cache.
			customer = bank.getCustomer(customerId);
			
			// load customer from DB if not found in cache.
			if(customer == null) {
				conn = Factory.getDataSource().getConnection();
	            stmt = conn.prepareStatement("SELECT * FROM customer c JOIN customer_info c_info ON c.id = c_info.customer_id WHERE c.id = ?");
                stmt2 = conn.prepareStatement("SELECT * from own_bank_beneficiary WHERE customer_id = ?");
                stmt3 = conn.prepareStatement("SELECT * from other_bank_beneficiary WHERE customer_id = ?");
                stmt4 = conn.prepareStatement("SELECT a.account_no, a.branch_id, ra.type_id FROM regular_account ra LEFT JOIN account a ON a.account_no = ra.account_no where a.customer_id = ?");
                stmt5 = conn.prepareStatement("SELECT a.account_no, a.branch_id, da.type_id FROM deposit_account da LEFT JOIN account a ON a.account_no = da.account_no where a.customer_id = ?");
	            
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
                    
	                // Cache customer beneficiaries
                    // Own bank beneficiaries
                    stmt2.setLong(1, customer.getId());
                    rs2 = stmt2.executeQuery();

                    while(rs2.next()) {
                        beneficiaryId = rs2.getLong("id");
                        beneficiaryAccountNo = rs2.getLong("account_no");
                        beneficiaryName = rs2.getString("name");
                        beneficiaryNickName = rs2.getString("nick_name");

                        beneficiary = new Beneficiary(beneficiaryId, beneficiaryAccountNo, beneficiaryName, beneficiaryNickName);
                        customer.addBeneficiary(BeneficiaryType.OWN_BANK, beneficiary);
                    }

                    // Other bank beneficiaries
                    stmt3.setLong(1, customer.getId());
                    rs3 = stmt3.executeQuery();

                    while(rs3.next()) {
                        beneficiaryId = rs3.getLong("id");
                        bankId = rs3.getInt("bank_id");
                        beneficiaryAccountNo = rs3.getLong("account_no");
                        beneficiaryIfsc = rs3.getString("ifsc");
                        beneficiaryName = rs3.getString("name");
                        beneficiaryNickName = rs3.getString("nick_name");

                        beneficiary = new Beneficiary(beneficiaryId, beneficiaryAccountNo, beneficiaryName, beneficiaryNickName, bankId, beneficiaryIfsc);
                        customer.addBeneficiary(BeneficiaryType.OTHER_BANK, beneficiary);
                    }

                    // Load customer's regular accounts mapping
                    stmt4.setLong(1, customer.getId());
                    rs4 = stmt4.executeQuery();

                    while(rs4.next()) {
                        accountNo = rs4.getLong("account_no");
                        accountBranchId = rs4.getInt("branch_id");
                        type_id = rs4.getInt("type_id");

                        customer.addAccountBranchMapping(AccountCategory.REGULAR, type_id, accountNo, accountBranchId);
                    }
                    
                    // Load customer deposit account mapping
                    stmt5.setLong(1, customer.getId());
                    rs5 = stmt5.executeQuery();

                    while(rs5.next()) {
                        accountNo = rs5.getLong("account_no");
                        accountBranchId = rs5.getInt("branch_id");
                        type_id = rs5.getInt("type_id");

                        customer.addAccountBranchMapping(AccountCategory.DEPOSIT, type_id, accountNo, accountBranchId);
                    }
	                
	                // add to cache
	                bank.addCustomer(customer);
	            }
			}
		} catch(SQLException e) {
			System.out.println(e.getMessage());
            exceptionOccured = true;
            msg = "internal error";
        } finally {
            try {
                if(rs != null)
                    rs.close();
				if(rs2 != null)
					rs2.close();
				if(rs3 != null)
					rs3.close();
				if(rs4 != null)
					rs4.close();
				if(rs5 != null)
					rs5.close();
            } catch(SQLException e) { System.out.println(e.getMessage()); }

            try {
                if(stmt != null)
                    stmt.close();
				if(stmt2 != null)
					stmt2.close();
				if(stmt3 != null)
					stmt3.close();
				if(stmt4 != null)
					stmt4.close();
				if(stmt5 != null)
					stmt5.close();
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
		
		Bank bank = AppCache.getBank();
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
			
			// update in cache if customer object exists.
			customer = bank.getCustomer(customerId);
			
			if(customer != null) {
				customer.setName(name);
				customer.setPhone(phone);
				customer.setEmail(email);
				customer.setAge(age);
				customer.setGender(gender);
				customer.setMartialStatus(martialStatus);
				customer.setOccupation(occupation);
				customer.setIncome(income);
				customer.setAdhaar(adhaar);
				customer.setPan(pan);
				customer.setAddress(address);
			}
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
		
		Customer customer = null;
		LocalDate today = LocalDate.now();
		String msg = "";
		boolean exceptionOccured = false;
		
		try {
			conn = Factory.getDataSource().getConnection();
			stmt = conn.prepareStatement("UPDATE customer SET removed_date = ? WHERE id = ?");
				
			// set customer status as removed.
			stmt.setDate(1, Date.valueOf(today));
			stmt.setLong(2, customerId);
			stmt.executeUpdate();
			
			// update in cache if exists.
			customer = AppCache.getBank().getCustomer(customerId);
			if(customer != null) 
				customer.setRemovedDate(today);
			
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