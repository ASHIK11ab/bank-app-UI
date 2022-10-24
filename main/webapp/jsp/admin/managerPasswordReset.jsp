<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Manager password reset</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/components/navbar.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/form.css">
</head>
<body>
	<jsp:include page="/jsp/admin/components/navbar.jsp" />

	<main class="container">
		<section class="wrapper">
			<h1 class="title">Manager Password Reset</h1>
			
			<form action="/bank-app/admin/managers/manager/password-reset" method="post">
				<label>Select Manager:</label>
				<select name="manager-id">
					<option value="-1" selected disabled hidden>Select Manager</option>
					<c:forEach items="${ requestScope.managers }" var="manager">
						<option value="${ manager.getId() }">${ manager.getName() }</option>
					</c:forEach>
				</select>
				
				<label>New Password:</label>
				<input type="text" name="password" placeholder="manager password" maxlength="15" required>
				
				<button>Reset password</button>
			</form>
			
		</section>
	</main>
</body>
</html>