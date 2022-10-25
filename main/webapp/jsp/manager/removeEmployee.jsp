<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
	<title>Remove employee</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/components/navbar.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/form.css">
</head>
<body>
	<jsp:include page="/jsp/manager/components/navbar.jsp" />
	
	<main class="container">
		<section class="wrapper">
			<h1>Remove Employee</h1>
			
			<c:if test="${ requestScope.values.size() > 0 }">
				<form action="/bank-app/manager/employees/employee/remove" method="post">
				
					<jsp:include page="/jsp/components/genericDropdown.jsp">
						<jsp:param name="labelName" value="Select Employee" />
						<jsp:param name="name" value="id" />
						<jsp:param name="placeholderOptionText" value="Select employee" />
						<jsp:param name="displayId" value="${ true }" />
					</jsp:include>
					
					<button>Remove employee</button>
				</form>
			</c:if>
			
			<c:if test="${ requestScope.values.size() == 0 }">
				<h2>No employees in branch</h2>
			</c:if>
		</section>
	</main>
</body>
</html>