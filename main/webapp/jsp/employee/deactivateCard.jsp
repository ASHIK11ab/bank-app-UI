<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Deactivate card" />
	</jsp:include>
</head>
<body>
	<jsp:include page="/jsp/employee/components/navbar.jsp" />

	<main class="container">
		<div class="wrapper">
			<h1>Confirm card deactivation</h1>
			<form action="/bank-app/employee/deactivate-card" method="post">			
				<input name="card-no" value="${ card.getCardNo() }" class="hidden" required>
				
				<h3 style="color: red">Note:</h3>
				<p>You are about to deactive the following card. This action cannot be reversed. Proceed to continue</p>
				
				<p><span class="bold">Card No: </span>${ card.getCardNo() }</p>
				<p><span class="bold">Linked A/C: </span>${ card.getLinkedAccountNo() }</p>	
				
				<button class="danger" >Deactivate card</button>
				<a class="button secondary" href="/bank-app/employee/card/${ card.getCardNo() }/view">cancel</a>
			</form>
		</div>
	</main>
</body>
</html>