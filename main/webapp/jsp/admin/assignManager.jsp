<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Assign new manager</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/components/navbar.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/form.css">
</head>
<body>

	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<h1>Assign Manager</h1>
			<form action="/bank-app/admin/managers/manager/assign" method="post">
				
				<jsp:include page="/jsp/components/managerForm.jsp" />
				
				<label>Branch:</label>
				<jsp:include page="/jsp/components/branchDropdown.jsp">
					<jsp:param name="name" value="branch-id" />
				</jsp:include>
				
				<button>Assign manager</button>
			</form>
		</div>
	</main>
</body>
</html>