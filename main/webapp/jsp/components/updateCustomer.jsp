<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
	import = "constant.Role"    
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Update customer details" />
	</jsp:include>
</head>
<body>
	<c:set var="role" value="${ sessionScope.role }" />
	<c:set var="userType" value="${ Role.getName(role) }" />
	<c:set var="actionURL" value='${ role == Role.EMPLOYEE ? "/bank-app/employee/customer/update" : "/bank-app/customer/update" }' />
	
	<c:if test="${ role == Role.EMPLOYEE }">
		<c:set var="cancelURL" value="/bank-app/employee/customer/${ customer.getId() }/view" />
	</c:if>
	<c:if test="${ role == Role.CUSTOMER }">
		<c:set var="cancelURL" value="/bank-app/customer/profile/view" />
	</c:if>

	<jsp:include page="/jsp/${ userType }/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<h1>Update customer details</h1>
			<form action="${ actionURL }" method="post">
			
				<c:if test="${ role == Role.EMPLOYEE }">
					<input name="customer-id" value="${ customer.getId() }" class="hidden">
				</c:if>
				
				<jsp:include page="/jsp/components/customerDetailsForm.jsp" />
				<button>Save</button>
				<a class="button secondary" href="${ cancelURL }">Cancel</a>
			</form>
		</div>
	</main>
</body>
</html>