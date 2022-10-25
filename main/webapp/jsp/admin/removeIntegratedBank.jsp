<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Remove integrated bank</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/components/navbar.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/form.css">
</head>
<body>
	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<h1>Remove Integrated Bank</h1>
			<form action="/bank-app/admin/integrated-banks/bank/delete" method="post">
				
				<jsp:include page="/jsp/components/genericDropdown.jsp">
					<jsp:param name="labelName" value="Select Bank:" />
					<jsp:param name="name" value="bank-id" />
					<jsp:param name="placeholderOptionText" value="select bank" />
					<jsp:param name="displayId" value="${ false }" />
				</jsp:include>
				
				<button>Remove bank</button>
			</form>
		</div>
	</main>
</body>
</html>