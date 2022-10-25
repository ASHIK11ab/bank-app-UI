<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>${ requestScope.branch.getName() } branch</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/components/navbar.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/admin/branch.css">
</head>
<body>
	
	<jsp:include page="/jsp/admin/components/navbar.jsp" />

	<c:set var="branch" value="${ requestScope.branch }" />
	<c:set var="manager" value="${ branch.getManager() }" />
	
	<main class="container">
		<div class="wrapper">
			<section class="branch-section">
				<h2 class="title">Branch details:</h2>
				<p>Name: ${ branch.getName() }</p>
				<p>Address: ${ branch.getAddress() }</p>
			</section>
			<section class="manager-section">
				<h2 class="title">Manager details:</h2>
				<p>Id: ${ manager.getId() }</p>
				
				<c:if test="${ requestScope.displayManagerPassword }">
					<p>Password: ${ manager.getPassword() }<p>
				</c:if>
				
				<p>Name: ${ manager.getName() }</p>
				<p>Email: ${ manager.getEmail() }</p>
				<p>Phone: ${ manager.getPhone() }</p>
			</section>
		</div>
	</main>
</body>
</html>