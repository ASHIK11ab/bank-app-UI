<%@ page import="constant.DepositAccountType" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<section>
	<c:if test="${ account.isClosed() }">
		<h2 style="color: red">Deposit closed</h2>
	</c:if>
	
	<h2>Deposit Account Details:</h2>
	<p>A/C No: ${ account.getAccountNo() }<p>
	<p>Balance: ${ account.getBalance() }</p>
	<p>Deposit Type: ${ DepositAccountType.getName(account.getTypeId()) }
	<p>Intrest Rate (p.a): ${ Math.round(account.getIntrestRate() * 100) }%</p>
	<p>A/C opening date: ${ account.getOpeningDate() }</p>
	<p>Maturity date: ${ account.getOpeningDate().plusMonths(account.getTenureMonths()) }</p>
	
	<c:if test="${ account.isClosed() }">
		<strong>Deposit closed on: ${ account.getClosingDate() }</strong>
	</c:if>
	
</section>