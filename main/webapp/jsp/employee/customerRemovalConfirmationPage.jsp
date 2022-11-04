<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Remove customer confirmation page" />
	</jsp:include>
</head>
<body>
	<jsp:include page="/jsp/employee/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<section>
				<h1>Confirmation Required</h1>
				<form action="/bank-app/employee/customer/close" method="post">	
					<!-- Require confirmation (since proceeding to closing account
						will remove customer from bank -->
					<input name="account-no" value="${ accountNo }" class="hidden">
				
					<p style="color: red; font-weight: 700; font-size: 25px;">Note:<p>
					<p>Please read the below carefully</p>
					<p>${ note }</p>
					
					<button class="danger">Remove customer</button>
					<a class="button secondary" href="/bank-app/employee/account/${ accountNo }/view">
						Cancel
					</a>
				</form>
			</section>
		</div>
	</main>
</body>
</html>