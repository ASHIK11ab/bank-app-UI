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
	
	<c:choose>
		<c:when test="${ DepositAccountType.getType(account.getTypeId()) == DepositAccountType.RD }">
			<p>Monthly installment date: Day ${ account.getRecurringDate().getDayOfMonth() } of every month</p>
			<p>Monthly installment amount: ${ account.getDepositAmount() }</p>
		</c:when>
		<c:when test="${ DepositAccountType.getType(account.getTypeId()) == DepositAccountType.FD }">
			<p>Deposited Amount: ${ account.getDepositAmount() }</p>
		</c:when>
	</c:choose>
	
	<c:if test="${ account.isClosed() }">
		<strong>Deposit closed on: ${ account.getClosingDate() }</strong>
	</c:if>
	
</section>