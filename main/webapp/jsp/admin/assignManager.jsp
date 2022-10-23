<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Assign new manager</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/component/navbar.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/form.css">
</head>
<body>

	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<h1>Assign Manager</h1>
			<form action="/bank-app/admin/managers/manager/assign" method="post">
				<label>Name:</label>
				<input type="text" placeholder="manager name" name="manager-name" maxlength="20" required>
				<label>Email:</label>
				<input type="email" placeholder="manager email" name="manager-email" maxlength="30" required>
				<label>Phone:</label>
				<input type="number" placeholder="manager contact number" name="manager-phone" required>
				<label>Branch:</label>
				<select name="branch-id">
					<option value="-1" selected disabled hidden>Select branch</option>
					<c:forEach items="${ requestScope.branches }" var="branch">
						<option value="${ branch.getId() }">${ branch.getName() }</option>
					</c:forEach>
				</select>
				<button>Assign manager</button>
			</form>
		</div>
	</main>
</body>
</html>