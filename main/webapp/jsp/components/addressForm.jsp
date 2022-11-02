<label>Door no:</label>
<input type="text" placeholder="door no" name="door-no" 
	value="${ address.getDoorNo() }" maxlength="10" required>
	
<label>Street:</label>
<input type="text" placeholder="street name" name="street" 
	value="${ address.getStreet() }" maxlength="30" required>
	
<label>City:</label>
<input type="text" placeholder="city name" name="city" 
	value="${ address.getCity() }" maxlength="15" required>
	
<label>State:</label>
<input type="text" placeholder="state name" name="state" 
	value="${ address.getState() }" maxlength="15" required>
	
<label>Pincode:</label>
<input type="number" placeholder="pincode" name="pincode" 
	value="${ address.getPincode() }" required>