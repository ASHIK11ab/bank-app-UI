<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="constant.RegularAccountType" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>View account | Employee</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/components/navbar.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/form.css">
</head>
<body>
	<jsp:include page="/jsp/employee/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<section>
				<h1>View Account</h1>
				<form action="/bank-app/employee/account/view" method="post">
					<label>Account No:</label>
					<input type="number" placeholder="Enter account no" name="account-no" required>
					<button>submit</button>
				</form>
			</section>
			
			<c:if test="${ requestScope.account != null }">
				<c:set var="account" value="${ requestScope.account }" />
				
				<section>
					<h2>Account Details:</h2>
					<p>A/C No: ${ account.getAccountNo() }<p>
					<p>A/C Type: ${ RegularAccountType.getName(account.getTypeId()) }
					<p>Balance: ${ account.getBalance() }</p>
					<p>Status: <c:out value='${ account.getIsActive() ? "Active" : "Not Active" }' /></p>
					<p>A/C opened on: ${ account.getOpeningDate() }</p>
				</section>
				<section>
					<h2>Account holder details:</h2>
					<p>Customer Id: ${ account.getCustomerId() }
					<p>Customer Name: ${ account.getCustomerName() }
				</section>
			</c:if>
		</div>
	</main>
</body>
</html>