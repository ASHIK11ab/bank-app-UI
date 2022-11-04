<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="customer" value="${ requestScope.customer }" />

<section>	
	<h2>Customer Details:</h2>
	<p>Id: ${ customer.getId() }<p>
	
	<c:if test="${ requestScope.displayPassword }">
		<p>Login password: ${ customer.getPassword() }<p>
	</c:if>
	
	<h3>General Details:</h3>
	<p>Name: ${ customer.getName() }</p>
	<p>Phone: ${ customer.getPhone() }<p>
	<p>Email: ${ customer.getEmail() }<p>
	
	<h3>Personal Details:</h3>
	<p>Gender: ${ customer.getGender().toString().equals("m") ? "male": "female" }</p>
	<p>Age: ${ customer.getAge() }<p>
	<p>Occupation: ${ customer.getOccupation() }</p>
	<p>Income: ${ customer.getIncome() }<p>
	<p>Martial Status: ${ customer.getMartialStatus() }<p>
	<p>ADHAAR no: ${ customer.getAdhaar() }</p>
	<p>PAN no: ${ customer.getPan() }
	
	<h3>Address:</h3>
	<p>${ customer.getAddress() }</p>
</section>