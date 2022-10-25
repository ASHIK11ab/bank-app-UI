<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>${ requestScope.manager.getName() } profile</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/components/navbar.css">
</head>
<body>
	<jsp:include page="/jsp/manager/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<section>
				<h1>Your profile</h1>
				<p>Name: ${ requestScope.manager.getName() }</p>
				<p>Phone: ${ requestScope.manager.getPhone() }</p>
				<p>Email: ${ requestScope.manager.getEmail() }</p>
			</section>
			<section>
				<h2 class="title">Branch details:</h2>
				<p>Branch Name: ${ requestScope.manager.getBranchName() }</p>
			</section>
		</div>
	</main>
</body>
</html>