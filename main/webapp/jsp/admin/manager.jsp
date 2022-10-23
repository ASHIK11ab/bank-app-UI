<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Manager</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/component/navbar.css">
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
	<c:set var="manager" value="${ requestScope.manager }" />
	
	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<section>
				<h2 class="title">Manager details:</h2>
				<p>Name: ${ manager.getName() }</p>
				<p>Email: ${ manager.getEmail() }</p>
				<p>Phone: ${ manager.getPhone() }</p>
			</section>
			<section>
				<h1 class="title">Branch details:</h1>
				<p>Branch Name: ${ manager.getBranchName() }</p>
			</section>
		</div>
	</main>
</body>
</html>