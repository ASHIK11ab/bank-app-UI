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
	<c:set var="userType" value='${ sessionScope.role == Role.EMPLOYEE ? "employee" : "customer" }' />
	
	<jsp:include page="/jsp/${ userType }/components/navbar.jsp" />
	
	<main class="container">
		<div>
			<h1>Account Transaction History</h1>
			<div>
				<h2>Account Details:</h2>
				<p>A/C No: ${ account.getAccountNo() }<p>
				
				<c:if test="${ requestScope.actionType == 1 }">				
					<p>Balance: ${ account.getBalance() }</p>
				</c:if>
			</div>
					
			<c:choose>
				<c:when test="${ actionType == 0 }">
					
					<form action="/bank-app/${ userType }/account/transaction-history" method="post">
						<input type="number" name="account-no" value="${ account.getAccountNo() }" class="hidden" required>
						
						<label>From Date:</label>
						<input type="date" name="from-date" required>
						<label>To Date:</label>
						<input type="date" name="to-date" required>
						<button>Submit</button>
					</form>
				</c:when>
				
				<c:when test="${ actionType == 1 }">
					<section>
					<!-- Display transactions -->
						<c:choose>
							<c:when test="${ transactions.size() == 0 }">
								<h2>No transactions in given range</h2>
							</c:when>
						
							<c:when test="${ transactions.size() > 0 }">
								<table>
									<tr>
										<th>Transaction Id</th>
										<th>Date</th>
										<th>Description</th>
										<th>Amount</th>
										<th>Type</th>
										<th>Balance</th>
									</tr>
								
									<c:forEach items="${ transactions }" var="transaction">
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
				</c:when>
				
			</c:choose>
		</div>
	</main>
</body>
</html>