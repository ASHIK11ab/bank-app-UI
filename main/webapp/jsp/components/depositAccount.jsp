<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="constant.DepositAccountType" %>

<c:set var="account" value="${ requestScope.account }" scope="request"/>

<section>
	<h2>Deposit Account Details:</h2>
	<p>A/C No: ${ account.getAccountNo() }<p>
	<p>Balance: ${ account.getBalance() }</p>
	<p>Deposit Type: ${ DepositAccountType.getName(account.getTypeId()) }
	<p>Intrest Rate (p.a): ${ Math.round(account.getIntrestRate() * 100) }%</p>
	<p>A/C opening date: ${ account.getOpeningDate() }</p>
	<p>Maturity date: ${ account.getOpeningDate().plusMonths(account.getTenureMonths()) }</p>
</section>
<section>
	<h2>Customer details:</h2>
	<p>Customer Id: ${ account.getCustomerId() }
	<p>Customer Name: ${ account.getCustomerName() }
</section>