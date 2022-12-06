<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="constant.RegularAccountType" %>

<section>
	<c:if test="${ account.isClosed() }">
		<h2 style="color: red">Account closed</h2>
	</c:if>
	
	<h2>Account Details:</h2>
	<p>A/C No: ${ account.getAccountNo() }<p>
	<p>A/C Type: ${ RegularAccountType.getName(account.getTypeId()) }
	<p>Balance: ${ account.getBalance() }</p>
	<p>Status: <c:out value='${ account.getIsActive() ? "Active" : "Not Active" }' /></p>
	<p>A/C opened on: ${ account.getOpeningDate() }</p>
	
	<c:if test="${ account.isClosed() }">
		<strong>Account closed on: ${ account.getClosingDate() }</strong>
	</c:if>
</section>