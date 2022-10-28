<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Close Deposit</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/components/navbar.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/form.css">
</head>
<body>
	<jsp:include page="/jsp/employee/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<form action="/bank-app/employee/deposit/close" method="post">
				<input name="action-type" value="${ requestScope.actionType }" class="hidden">
				
				<div class="${ requestScope.actionType != 0 ? 'hidden' : '' }">
					<!-- Allow to input account no -->
					<!-- Hide this section when showing confirmation details -->
					<h1>Close Deposit</h1>
					<label>Account No:</label>
					<input type="number" placeholder="Enter account no" 
						name="account-no" value="${ requestScope.accountNo }" required>
					<button>submit</button>
				</div>
				
				<c:choose>
					
					<c:when test="${ requestScope.actionType == 1 }">
						<!-- Require confirmation -->
						<h1>Confirm Details</h1>
						<jsp:include page="/jsp/components/depositAccount.jsp" />
						<button>Proceed to close deposit</button>
						<a href="/bank-app/employee/deposit/close">Go back</a>
					</c:when>
					
					<c:when test="${ requestScope.actionType == 2 }">						
						<h1>Confirm Premature Closing</h1>
						<h3 style="color: red">Note:</h3>
						<p>Please read below before closing deposit:</p>
	                    <p>1) On closing of this account the intrest will be applicable only for the available balance.</p>
	                    <p>2) Rupees 500 will be charged for premature closing and will be deducted from your deposit account balance</p>
						<button>Close Deposit</button>
						<a href="/bank-app/employee/deposit/close">Cancel</a>
					</c:when>
					
				</c:choose>
			</form>			
		</div>
	</main>
</body>
</html>