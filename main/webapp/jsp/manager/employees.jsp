<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>All employees</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/components/navbar.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
</head>
<body>
	<jsp:include page="/jsp/manager/components/navbar.jsp" />
	
	<main class="container">
		<c:if test="${ requestScope.employees.size() == 0 }">
			<h1>No employees in branch</h1>
		</c:if>
		
		<c:if test="${ requestScope.employees.size() > 0 }">
			<h1>Employees: ${ requestScope.employees.size() }</h1>
			<div class="group">
				
				<c:forEach items="${ requestScope.employees }" var="employee" varStatus="loop">
					<div class="item">
						<a href="/bank-app/manager/employees/employee/${ employee.getId() }">
							${ loop.index + 1 }. ${ employee.getName() }
						</a>				
					</div>
				</c:forEach>
				
			</div>
		</c:if>
	</main>
</body>
</html>