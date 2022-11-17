<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
	import="constant.Role"    
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Deposit created successfully" />
	</jsp:include>
</head>
<body>
	<c:set var="userType" value="${ Role.getName(sessionScope.role) }" />

	<jsp:include page="/jsp/${ userType }/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<h1 style="color: green">Deposit created successfully</h1>
			
			<jsp:include page="/jsp/components/deposit.jsp" />
			
			<c:if test="${ role == Role.EMPLOYEE }">
				<h3>Customer Details:</h3>
				<p>Customer Id: ${ account.getCustomerId() }</p>
				<p>Customer Name: ${ account.getCustomerName() }</p>
			</c:if>
		</div>
	</main>
</body>
</html>