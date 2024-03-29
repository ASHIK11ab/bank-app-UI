<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="View Customer" />
	</jsp:include>
</head>
<body>
	<jsp:include page="/jsp/employee/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
		
			<c:choose>
				<c:when test="${ actionType == 0 }">
					<h1>View Customer:</h1>
					<form action="/bank-app/employee/customer/" method="post">
						<label>Customer Id:</label>
						<input type="number" name="customer-id" placeholder="customer id" required>
						<button>submit</button>
					</form>
				</c:when>
	
				<c:when test="${ actionType == 1 }">
					<c:if test="${ customer.isRemoved() }">
						<h1 style="color: red">Customer removed</h1>
					</c:if>
					
					<h2>Customer Details:</h2>
					<jsp:include page="/jsp/components/customer.jsp" />
					
					<c:if test="${ !customer.isRemoved() }">
						<a class="button" href="/bank-app/employee/customer/${ customer.getId() }/update">
							update customer details
						</a>
						<a class="button secondary" href="/bank-app/employee/customer/${ customer.getId() }/password-reset">
							reset customer password
						</a>
					</c:if>
				</c:when>
			</c:choose>

		</div>
	</main>
</body>
</html>