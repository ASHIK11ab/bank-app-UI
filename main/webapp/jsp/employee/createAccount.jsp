<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Create Account</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/components/navbar.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/form.css">
</head>
<body>
	<jsp:include page="/jsp/employee/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<h1>Create new account</h1>
			<form action="/bank-app/employee/account/create/existing-customer" method="post">
				<!-- Store data recieved from previous page -->
				<input class="hidden" name="account-type" value="${ requestScope.accountType }">
				<input class="hidden" name="card-type" value="${ requestScope.cardType }">
				
				<section>
					<!-- Display customer details if in confirmation action. -->
					<c:if test="${ requestScope.customer != null }">
						<h2>Confirm Customer Details:</h2>
						<p>Customer Id: ${ requestScope.customer.getId() }<p>
						<p>Customer Name: ${ requestScope.customer.getName() }</p>
						<p>Customer Phone: ${ requestScope.customer.getPhone() }</p>
						
						<input name="customer-id" value="${ customer.getId() }" class="hidden">
						<input name="customer-name" value="${ customer.getName() }" class="hidden">
						<input name="action-type" value="1" class="hidden">
						
						<button>Create Account</button>
						<a type="button" href="/bank-app/employee/account/create">
							Go back
						</a>
					</c:if>
					
					<c:if test="${ requestScope.customer == null }">
						<label>Customer Id:</label>
						<input type="number" placeholder="customer id" name="customer-id" required>
						<input name="action-type" value="0" class="hidden">	
						<button>Proceed to create account</button>
					</c:if>
						
				</section>
				
			</form>
		</div>
	</main>
</body>
</html>