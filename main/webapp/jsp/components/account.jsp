<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="constant.RegularAccountType" %>

<c:set var="account" value="${ requestScope.account }" />

<section>
	<h2>Account Details:</h2>
	<p>A/C No: ${ account.getAccountNo() }<p>
	<p>A/C Type: ${ RegularAccountType.getName(account.getTypeId()) }
	<p>Balance: ${ account.getBalance() }</p>
	<p>Status: <c:out value='${ account.getIsActive() ? "Active" : "Not Active" }' /></p>
	<p>A/C opened on: ${ account.getOpeningDate() }</p>
</section>