<%@ page import="util.Util" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<label>Nominee name:</label>
<input type="text" name="nominee-name" maxlength="15" placeholder="Name of this account's nominee"
	value="${ nominee.getName() }" required>
	
<label>Nominee relationship:</label>
<select name="nominee-relationship" id="nominee-relationship" required>
	<option value="" selected hidden>Select relationship</option>
	<option value="father">Father</option>
	<option value="mother">Mother</option>
	<option value="son">Son</option>
	<option value="daughter">Daughter</option>
	<option value="sibling">Sibling</option>
	<option value="gaurdian">Gaurdian</option>
	<option value="other">Other</option>
</select>

<label>Nominee Adhaar No:</label>
<input type="number" placeholder="adhaar number" name="nominee-adhaar" 
	value="${ nominee.getAdhaar() }" required>
	
<label>Nominee Phone:</label>
<input type="number" placeholder="phone number of the nominee" name="nominee-phone" 
	value="${ nominee.getPhone() }" required>