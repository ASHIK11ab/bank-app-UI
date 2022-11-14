<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="${ bank.getName() } bank | Integrated bank" />
	</jsp:include>
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
			<section>
				<a class="button" href="/bank-app/admin/integrated-banks/${ bank.getId() }/edit">Edit</a>
			</section>
		</div>
	</main>
</body>
</html>