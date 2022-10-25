<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Transfer Managers</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/components/navbar.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/form.css">
</head>
<body>

	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<main class="container">
		<section>
			<h1>Transfer managers</h1>
			<form action="/bank-app/admin/managers/transfer" method="post">
				
				<jsp:include page="/jsp/components/genericDropdown.jsp">
					<jsp:param name="labelName" value="First branch:" />
					<jsp:param name="name" value="first-branch-id" />
					<jsp:param name="placeholderOptionText" value="select branch" />
					<jsp:param name="displayId" value="${ false }" />
				</jsp:include>
				
				<jsp:include page="/jsp/components/genericDropdown.jsp">
					<jsp:param name="labelName" value="Second branch:" />
					<jsp:param name="name" value="second-branch-id" />
					<jsp:param name="placeholderOptionText" value="select branch" />
					<jsp:param name="displayId" value="${ false }" />
				</jsp:include>
				
				<button>transfer managers</button>
			</form>
		</section>
	</main>
</body>
</html>