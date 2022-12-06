<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="constant.RegularAccountType" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Account created successfully" />
	</jsp:include>
</head>
<body>
	<jsp:include page="/jsp/employee/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<h1 style="color: green">Account Creation Successfull</h1>
			
			<!-- Renders using account object in request -->
			<jsp:include page="/jsp/components/account.jsp" />
			
			<h2>Card Details:</h2>
			<jsp:include page="/jsp/components/card.jsp" />
			
			<!-- Display card credentials on account creation -->
			<h3>Card Credentials:</h3>
			<p>Pin: ${ card.getPin() }</p>
			<p>CVV: ${ card.getCvv() }</p>
			
			<section>
				<h2>Account holder details:</h2>
				<p>Customer Id: ${ account.getCustomerId() }
				<p>Customer Name: ${ account.getCustomerName() }
			</section>
		</div>
	</main>
</body>
</html>