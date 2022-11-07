<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Assign new manager" />
	</jsp:include>
</head>
<body>

	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<h1>Assign New Manager</h1>
			<h3>Branch Name: ${ branch.getName() }</h3>
			<form action="/bank-app/admin/managers/manager/assign" method="post">
				<input name="branch-id" value="${ branchId }" class="hidden" required>
				
				<jsp:include page="/jsp/components/employeeRegistrationForm.jsp">
					<jsp:param name="employeeType" value="manager" />
				</jsp:include>
				
				<button>Assign manager</button>
			</form>
		</div>
	</main>
</body>
</html>