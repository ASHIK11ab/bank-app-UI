<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Integrated bank: ${ requestScope.integratedBank.getName() }</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/components/navbar.css">
	<style>
		.wrapper {
			width: 600px;
			border: 1.5px solid #ddd;
			margin: auto;
			padding: 25px 30px;
			font-family: Arial, sans-serif;
			box-sizing: border-box;
		}
	
		section .title {
			font-size: 18px;
			text-transform: capitalize;
		}
	</style>
</head>
<body>
	<c:set var="bank" value="${ requestScope.integratedBank }" />
	
	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<section>
				<h2 class="title">Integrated bank details:</h2>
				<p>Name: ${ bank.getName() }</p>
				<p>Email: ${ bank.getEmail() }</p>
				<p>Phone: ${ bank.getPhone() }</p>
				<p>Api URL: ${ bank.getApiURL() }
			</section>
		</div>
	</main>
</body>
</html>