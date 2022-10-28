<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Deposit Created successfully</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/components/navbar.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/form.css">
</head>
<body>
	<jsp:include page="/jsp/employee/components/navbar.jsp" />
	<c:set var="account" value="${ requestScope.account }" scope="request"/>
	
	<main class="container">
		<div class="wrapper">
			<h1 style="color: green">Deposit created successfully</h1>
			
			<jsp:include page="/jsp/components/depositAccount.jsp" />
		</div>
	</main>
</body>
</html>