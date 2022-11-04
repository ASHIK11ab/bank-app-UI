<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="close account confirmation" />
	</jsp:include>
</head>
<body>
	<jsp:include page="/jsp/employee/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<section>
				<h1>Confirmation Required</h1>
				<form action="/bank-app/employee/account/close" method="post">	
					<input name="account-no" value="${ accountNo }" class="hidden">
				
					<p style="color: red; font-weight: 700; font-size: 25px;">Note:<p>
					<p>You are about to close A/C: ${ accountNo }</p>
					<p>Proceeding to this action will close the account.</p>
					
					<button class="danger">Close account</button>
					<a class="button secondary" href="/bank-app/employee/account/${ accountNo }/view">
						Cancel
					</a>
				</form>
			</section>
		</div>
	</main>
</body>
</html>