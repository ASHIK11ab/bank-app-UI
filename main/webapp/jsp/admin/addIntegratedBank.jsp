<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Add Integrated bank</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/components/navbar.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/form.css">
</head>
<body>

	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<h1>Add Integrated Bank</h1>
			<form action="/bank-app/admin/integrated-banks/bank/create" method="post">
				<h2>Bank details:</h2>
				<label>Name:</label>
				<input type="text" placeholder="name" name="name" maxlength="30" required>
				<label>Email:</label>
				<input type="email" placeholder="email address" name="email" maxlength="30" required>
				<label>Phone:</label>
				<input type="number" placeholder="contact number" name="phone" required>
				<label>Api URL:</label>
				<input type="text" placeholder="bank api url" name="api-url" maxlength="70" required>
				<button>Integrate bank</button>
			</form>
		</div>
	</main>
</body>
</html>