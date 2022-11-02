<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="constant.Role" %>

<h3>General Information:</h3>
<label>Name:</label>
<input type="text" placeholder="customer name" name="name" maxlength="20" 
	value="${ customer.getName() }" required>
	
<label>Email:</label>
<input type="email" placeholder="customer email" name="email" 
	maxlength="30" value="${ customer.getEmail() }" required>
	
<label>Phone:</label>
<input type="number" placeholder="contact number" name="phone" 
	value="${ customer.getPhone() }" required>

<h3>Personal Information:</h3>
<label>Age:</label>
<input type="number" placeholder="customer age" name="age" 
	value="${ customer.getAge() }" required>
	
<label>Gender:</label>
<select name="gender" required>
	<option value="m" <c:out value='${ customer.getGender().toString().equals("m") ? "selected" : ""}'/>>
		Male
	</option>
	
	<option value="f" <c:out value='${ customer.getGender().toString().equals("f") ? "selected" : ""}' />>
		Female
	</option>
</select>

<label>Martial Status:</label>
<select name="martial-status" required>
	
	<option value="married" <c:out value='${ customer.getMartialStatus().equals("married") ? "selected": "" }' />>
		Married
	</option>
	
	<option value="unmarried" <c:out value='${ customer.getMartialStatus().equals("unmarried") ? "selected": "" }' />>
		Un Married
	</option>
	
</select>

<label>Occupation:</label>
<input type="text" placeholder="customer occupation" name="occupation" 
	value="${ customer.getOccupation() }" maxlength="20" required>
	
<label>Income:</label>
<input type="number" placeholder="customer income" 
	name="income" value="${ customer.getIncome() }" required>
	
<c:choose>

	<c:when test="${ sessionScope.role == Role.EMPLOYEE }">
		<label>Adhaar No:</label>
		<input type="number" placeholder="addhar number" name="adhaar" 
			value="${ customer.getAdhaar() }" required>
			
		<label>Pan:</label>
		<input type="text" placeholder="pan number" name="pan" 
			maxlength="10" value="${ customer.getPan() }" required>
	</c:when>
	
	<c:when test="${ sessionScope.role == Role.CUSTOMER }">
		<label>Adhaar No:</label>
		<p>${ customer.getAdhaar() }</p>
		
		<label>Pan:</label>
		<p>${ customer.getPan() }</p>
	</c:when>
	
</c:choose>

<c:set var="address" value="${ customer.getAddress() }" scope="request"/>
<h3>Address:</h3>
<jsp:include page="/jsp/components/addressForm.jsp" />