<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
	import = "constant.*"    
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Account Transaction History" />
	</jsp:include>
</head>
<body>
	<jsp:include page="/jsp/employee/components/navbar.jsp" />
	
	<main class="container">
		<div>
			<h1>Account Transaction History</h1>
			<form action="/bank-app/employee/account/transaction-history" method="post">
				<label>Account No:</label>
				<input type="number" placeholder="11 digit account no" name="account-no" required>
				<label>From Date:</label>
				<input type="date" name="from-date" required>
				<label>To Date:</label>
				<input type="date" name="to-date" required>
				<button>Submit</button>
			</form>
			
			<c:if test="${ requestScope.actionType == 1 }">
				<c:set var="account" value="${ requestScope.account }" />
				<section>
					<div>
						<h2>Account Details:</h2>
						<p>A/C No: ${ account.getAccountNo() }<p>
						
						<c:choose>
							<c:when test="${ requestScope.accountCategory == 0 }">						
								<p>A/C Type: ${ RegularAccountType.getName(account.getTypeId()) }
							</c:when>
							<c:when test="${ requestScope.accountCategory == 1 }">						
								<p>A/C Type: ${ DepositAccountType.getName(account.getTypeId()) }
							</c:when>
						</c:choose>
						
						<p>Balance: ${ account.getBalance() }</p>
					</div>
											
					<c:choose>
						<c:when test="${ requestScope.transactions.size() == 0 }">
							<h2>No transactions in given range</h2>
						</c:when>
						
						<c:when test="${ requestScope.transactions.size() > 0 }">
							<table>
								<tr>
									<th>Transaction Id</th>
									<th>Date</th>
									<th>Description</th>
									<th>Amount</th>
									<th>Type</th>
									<th>Balance</th>
								</tr>
								
								<c:forEach items="${ requestScope.transactions }" var="transaction">
									<tr>
										<td>${ transaction.getId() }</td>
										<td>${ transaction.getDate() }</td>
										<td>${ transaction.getDescription() }</td>
										<td>${ transaction.getAmount() }</td>
										
										<c:choose>
											<c:when test="${ transaction.getFromAccountNo() == account.getAccountNo() }">
												<td>Debit</td>
												<td>${ transaction.getBalanceBeforeTransaction() - transaction.getAmount() }
											</c:when>
											<c:when test="${ transaction.getToAccountNo() == account.getAccountNo() }">
												<td>Credit</td>
												<td>${ transaction.getBalanceBeforeTransaction() + transaction.getAmount() }
											</c:when>
										</c:choose>
									</tr>
								</c:forEach>
								
							</table>
						</c:when>
						
					</c:choose>
						
				</section>
			</c:if>
		</div>
	</main>
</body>
</html>