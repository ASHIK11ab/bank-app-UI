<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Manager: ${ requestScope.manager.getName() }</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/components/navbar.css">
</head>
<body>
	<c:set var="manager" value="${ requestScope.manager }" />
	
	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<section>
				<h2 class="title">Manager details:</h2>
				<p>Id   : ${ manager.getId() }</p>
				
				<c:if test="${ requestScope.displayPassword }">
					<p>Password: ${ manager.getPassword() }</p>
				</c:if>
				
				<p>Name : ${ manager.getName() }</p>
				<p>Email: ${ manager.getEmail() }</p>
				<p>Phone: ${ manager.getPhone() }</p>
			</section>
			<section>
				<h2 class="title">Branch details:</h2>
				<p>Branch Name: ${ manager.getBranchName() }</p>
			</section>
		</div>
	</main>
</body>
</html>