<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="All Employees" />
	</jsp:include>
</head>
<body>
	<jsp:include page="/jsp/manager/components/navbar.jsp" />
	
	<main class="container">
		<c:choose>
			<c:when test="${ employees.size() == 0 }">
				<h1>No employees in branch</h1>
			</c:when>
			
			<c:when test="${ employees.size() > 0 }">
				<h1>Employees: ${ employees.size() }</h1>
				<div class="group">
					
					<c:forEach items="${ employees }" var="employee" varStatus="loop">
						<div class="item">
							<a href="/bank-app/manager/employee/${ employee.getId() }/view">
								${ loop.index + 1 }. ${ employee.getName() }
							</a>				
						</div>
					</c:forEach>
					
				</div>
			</c:when>
		</c:choose>
	</main>
</body>
</html>