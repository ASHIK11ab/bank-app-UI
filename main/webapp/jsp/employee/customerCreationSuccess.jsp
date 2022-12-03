<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Customer Creation Successfull</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/components/navbar.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
</head>
<body>
	<jsp:include page="/jsp/employee/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<h1 style="color: green">Customer Created Successfully</h1>
			<jsp:include page="/jsp/components/customer.jsp" />
			<jsp:include page="/jsp/components/account.jsp" />
			
			<h2>Card Details:</h2>
			<jsp:include page="/jsp/components/card.jsp" />
			
			<!-- Display card credentials on account creation -->
			<h3>Card Credentials:</h3>
			<p>Pin: ${ card.getPin() }</p>
			<p>CVV: ${ card.getCvv() }</p>
		</div>
	</main>
</body>
</html>