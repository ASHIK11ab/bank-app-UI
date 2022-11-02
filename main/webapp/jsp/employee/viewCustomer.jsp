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
				<c:when test="${ requestScope.customer == null }">
					<h1>View Customer:</h1>
					<form id="customer-view-form" method="get">
						<label>Customer Id:</label>
						<input type="number" placeholder="customer id" id="customer-id-input" required>
						<button>submit</button>
					</form>
				</c:when>
	
				<c:when test="${ requestScope.customer != null }">
					<jsp:include page="/jsp/components/customer.jsp" />
					<a class="button" href="/bank-app/employee/customer/${ requestScope.customer.getId() }/update">
						update customer details
					</a>
					<a class="button secondary" href="/bank-app/employee/customer/${ requestScope.customer.getId() }/password-reset">
						reset customer password
					</a>
				</c:when>
			</c:choose>

		</div>
	</main>
	
	<c:if test="${ requestScope.customer == null }">
		<script>
			const customerIdInput = document.getElementById("customer-id-input");
			
			document.getElementById("customer-view-form").onsubmit = function() {
				this.setAttribute("action", "/bank-app/employee/customer/" + customerIdInput.value + "/view");
			}
		</script>
	</c:if>
</body>
</html>