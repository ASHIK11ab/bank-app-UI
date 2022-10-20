<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Admin Login</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/form.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/authentication-page.css">
</head>
<body>
	<main class="container">
		<h1 class="app-name">your bank</h1>
		<section class="form">
			<h2 class="form-title">admin login</h2>
			<form action="/bank-app/login/admin" method="post">
				<label for="id">user id:</label>
				<input type="number" id="id" name="id" required>
				<label for="password">password:</label>
				<input type="password" id="password" name="password" required maxlength="20">
				
				<%
					if(request.getAttribute("error") != null)
						out.println("<span class='error'>" + request.getAttribute("error") + "</span>");
				%>
				
				<button>Login</button>		
			</form>
		</section>
	</main>
</body>
</html>