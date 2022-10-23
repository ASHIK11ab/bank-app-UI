<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Remove integrated bank</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/components/navbar.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/form.css">
</head>
<body>
	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<h1>Remove Integrated Bank</h1>
			<form action="/bank-app/admin/integrated-banks/bank/delete" method="post">
				<label>Select Bank:</label>
				<select name="bank-id">
					<option value="-1" selected disabled hidden>Select bank</option>
					<c:forEach items="${ requestScope.integratedBanks }" var="bank">
						<option value="${ bank.getId() }">${ bank.getName() }</option>
					</c:forEach>
				</select>
				
				<button>Remove bank</button>
			</form>
		</div>
	</main>
</body>
</html>