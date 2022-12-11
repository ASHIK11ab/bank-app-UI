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
			<h1>${ actionType == 0 ? 'Remove Employee' : 'Confirm Employee Removal' }</h1>
			<form action="/bank-app/manager/employee/remove" method="post">
				<input name="actionType" value="${ actionType }" class="hidden">
		
				<c:choose>
					<c:when test="${ actionType == 0 }">
						<c:choose>
							<c:when test="${ employees.size() > 0 }">
									
									<c:set var="values" value="${ employees }" scope="request" />
									<jsp:include page="/jsp/components/genericDropdown.jsp">
										<jsp:param name="labelName" value="Select Employee" />
										<jsp:param name="name" value="id" />
										<jsp:param name="placeholderOptionText" value="Select employee" />
										<jsp:param name="displayId" value="${ true }" />
									</jsp:include>
									
							</c:when>
							
							<c:when test="${ employees.size() == 0 }">
								<h2>No employees in branch</h2>
							</c:when>
						</c:choose>
					</c:when>
					
					<c:when test="${ actionType == 1 }">
						<input name="id" value="${ employee.getId() }" class="hidden">
					
						<h2>Employee details:</h2>
						<p>Id: ${ employee.getId() }
						<p>Name: ${ employee.getName() }</p>
						<h3 style="color: red">Note:</h3>
						<p>You are about to remove this employee. Proceeding to this action will remove the employee
						account. Proceed to remove employee.</p>
					</c:when>
				</c:choose>
				
				<c:if test="${ employees.size() > 0 || actionType == 1 }">
					<button class="${ actionType == 1 ? 'danger' : '' }">Remove employee</button>
					
					<c:if test="${ actionType == 1 }">
						<a class="button secondary" href="/bank-app/manager/employee/remove">Cancel</a>
					</c:if>
				</c:if>
			</form>
		</section>
	</main>
</body>
</html>