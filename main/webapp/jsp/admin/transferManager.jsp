<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Transfer Managers</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/components/navbar.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/form.css">
</head>
<body>

	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<main class="container">
		<section>
			<h1>Transfer managers</h1>
			<form action="/bank-app/admin/managers/transfer" method="post">
				<label>First Branch:</label>
				<select name="first-branch-id">
					<option value="-1" selected disabled hidden>Select branch</option>
					<c:forEach items="${ requestScope.branches }" var="branch">
						<option value="${ branch.getId() }">${ branch.getName() }</option>
					</c:forEach>
				</select>
				<label>Second Branch:</label>
				<select name="second-branch-id">
					<option value="-1" selected disabled hidden>Select branch</option>
					<c:forEach items="${ requestScope.branches }" var="branch">
						<option value="${ branch.getId() }">${ branch.getName() }</option>
					</c:forEach>
				</select>
				
				<button>transfer managers</button>
			</form>
		</section>
	</main>
</body>
</html>