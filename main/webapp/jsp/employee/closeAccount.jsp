<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Close Account</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/components/navbar.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/form.css">
</head>
<body>
	<jsp:include page="/jsp/employee/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<section>
				<h1>Close Account</h1>
				<form action="/bank-app/employee/account/close" method="post">
					<input name="action-type" value="${ requestScope.confirmationType }" class="hidden">
					
					<c:if test="${ requestScope.confirmationType == 0 }">
						<!-- Allow to input account no -->
						<label>Account No:</label>
						<input type="number" placeholder="Enter account no" name="account-no" required>
						<button>submit</button>
					</c:if>
					
					<c:if test="${ requestScope.confirmationType == 1 }">
						<!-- Require confirmation -->
						<input name="account-no" value="${ requestScope.account.getAccountNo() }" class="hidden">
						<input name="customer-id" value="${ requestScope.account.getCustomerId() }" class="hidden">
					
						<jsp:include page="/jsp/components/account.jsp" />
						
						<section>
							<h2>Account holder details:</h2>
							<p>Customer Id: ${ account.getCustomerId() }
							<p>Customer Name: ${ account.getCustomerName() }
						</section>
						
						<button>Proceed to close account</button>
					</c:if>
					
					<c:if test="${ requestScope.confirmationType == 2 }">
						<!-- Require confirmation (since proceeding to closing account
							will remove customer from bank -->
						<input name="account-no" value="${ requestScope.accountNo }" class="hidden">
						<input name="customer-id" value="${ requestScope.customerId }" class="hidden">
					
						<p style="color: red; font-weight: 700; font-size: 25px;">Note:<p>
						<p>${ requestScope.note }</p>
						
						<button>Remove customer</button>
					</c:if>
				</form>
			</section>
		</div>
	</main>
</body>
</html>