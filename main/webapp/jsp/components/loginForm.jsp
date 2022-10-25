<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>${ requestScope.title }</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/form.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/authentication-page.css">
</head>
<body>
	<main class="container">
		<h1 class="app-name">Your Bank</h1>
		<section class="form">
			<h2 class="form-title">${ requestScope.title }</h2>
			<form action="${ requestScope.actionURL }" method="post">
			
				<label for="id">id:</label>
				<input type="number" id="id" name="id" required>
				<label for="password">password:</label>
				<input type="password" id="password" name="password" required maxlength="15">
				
				<c:if test="${ requestScope.error != null }">
					<span class='error'>${ requestScope.error }</span>
				</c:if>
				
				<button>Login</button>		
			</form>
		</section>
	</main>
</body>
</html>