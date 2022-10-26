<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Initiate Account Creation</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/components/navbar.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/form.css">
</head>
<body>
	<jsp:include page="/jsp/employee/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<h1>Account creation</h1>
			<form action="/bank-app/employee/account/create" method="post">
				<label>Customer Type</label>
				<select name="customer-type" required>
					<option value="-1" selected hidden disabled>select type</option>
					<option value="0">New Customer</option>
					<option value="1">Existing Customer</option>
				</select>
				<section>
					<h2>Account details:</h2>
					<label>Account type:</label>
					<select name="account-type" required>
						<option value="-1" selected hidden disabled>select type</option>
						<option value="${ 1 }">Savings</option>
						<option value="${ 2 }">Current</option>
					</select>
					<label>Debit Card Type:</label>
					<select name="card-type">
						<option value="-1" selected hidden disabled>select type</option>
						<option value="${ 1 }">CLASSIC DEBIT CARD</option>
						<option value="${ 2 }">PLATINUM DEBIT CARD</option>
					</select>
				</section>
				<button>Initiate Account Creation</button>
			</form>
		</div>
	</main>
</body>
</html>