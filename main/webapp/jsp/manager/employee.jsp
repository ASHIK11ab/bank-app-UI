<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>${ requestScope.employee.getName() } employee | ${ requestScope.employee.getBranchName() }</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/components/navbar.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
</head>
<body>
	<jsp:include page="/jsp/manager/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<h1>Employee details:</h1>
			<section>
				<p>Id: ${ employee.getId() }</p>
				
				<c:if test="${ requestScope.displayPassword }">
					<p>Password: ${ employee.getPassword() }
				</c:if>
				
				<p>Name : ${ employee.getName() }</p>
				<p>Email: ${ employee.getEmail() }</p>
				<p>Phone: ${ employee.getPhone() }</p>
			</section>
			<section>
				<h2 class="title">Branch details:</h2>
				<p>Branch Name: ${ employee.getBranchName() }</p>
			</section>
		</div>
	</main>
</body>
</html>