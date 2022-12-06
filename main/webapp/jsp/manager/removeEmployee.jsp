<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Remove Employee" />
	</jsp:include>
</head>
<body>
	<jsp:include page="/jsp/manager/components/navbar.jsp" />
	
	<main class="container">
		<section class="wrapper">
			<h1>Remove Employee</h1>
			<c:choose>
				<c:when test="${ employees.size() > 0 }">
					<form action="/bank-app/manager/employee/remove" method="post">
						
						<c:set var="values" value="${ employees }" scope="request" />
						<jsp:include page="/jsp/components/genericDropdown.jsp">
							<jsp:param name="labelName" value="Select Employee" />
							<jsp:param name="name" value="id" />
							<jsp:param name="placeholderOptionText" value="Select employee" />
							<jsp:param name="displayId" value="${ true }" />
						</jsp:include>
						
						<button>Remove employee</button>
					</form>
				</c:when>
				
				<c:when test="${ employees.size() == 0 }">
					<h2>No employees in branch</h2>
				</c:when>
			</c:choose>
		</section>
	</main>
</body>
</html>