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
	<c:set var="userType" value='${ Role.getName(sessionScope.role) }' />
	
	<jsp:include page="/jsp/${ userType }/components/navbar.jsp" />
	
	<main class="container">
		<div>
			<h1>Account Transaction History</h1>
			<div>
				<h2>Account Details:</h2>
				<p>A/C No: ${ account.getAccountNo() }<p>
				
				<c:if test="${ requestScope.actionType == 1 }">				
					<p>Balance: ${ String.format("%.2f", account.getBalance()) }</p>
				</c:if>
			</div>
					
			<c:choose>
				<c:when test="${ actionType == 0 }">
					
					<form action="/bank-app/${ userType }/account/transaction-history" method="post">
						<input type="number" name="account-no" value="${ account.getAccountNo() }" class="hidden" required>
						<input type="number" name="account-category" value="${ accountCategory }" class="hidden" required>
						
						<label>From Date:</label>
						<input type="date" name="from-date" required>
						<label>To Date:</label>
						<input type="date" name="to-date" required>
						<button>Submit</button>
					</form>
				</c:when>
				
				<c:when test="${ actionType == 1 }">
					<jsp:include page="/jsp/components/transactionHistoryTable.jsp">
						<jsp:param name="fallbackText" value="No transactions in given range" />
					</jsp:include>
				</c:when>
				
			</c:choose>
		</div>
	</main>
</body>
</html>