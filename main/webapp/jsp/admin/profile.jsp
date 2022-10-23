<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>${ requestScope.user.getName() } profile</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/components/navbar.css">
</head>
<body>
	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<main class="container">
		<section class="wrapper">
			<h1>Your profile</h1>
			<p>Name: ${ requestScope.user.getName() }</p>
			<p>Phone: ${ requestScope.user.getPhone() }</p>
			<p>Email: ${ requestScope.user.getEmail() }</p>
		</section>
	</main>
</body>
</html>