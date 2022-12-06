<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="create account" />
	</jsp:include>
</head>
<body>
	<jsp:include page="/jsp/employee/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<h1>Create new account</h1>
			<form action="/bank-app/employee/account/create/existing-customer" method="post">
				<!-- Store data recieved from previous page -->
				<input class="hidden" name="account-type" value="${ accountType }">
				<input class="hidden" name="card-type" value="${ cardType }">
				<input class="hidden" name="action-type" value="${ actionType }">
				
				<section>
					<c:choose>			
						<c:when test="${ actionType == 0 }">
							<label>Customer Id:</label>
							<input type="number" placeholder="customer id" name="customer-id" required>
							<button>Proceed to create account</button>
						</c:when>
							
						<c:when test="${ actionType == 1 }">
							<h2>Confirm Customer Details:</h2>
							<p>Customer Id: ${ customer.getId() }<p>
							<p>Customer Name: ${ customer.getName() }</p>
							<p>Customer Phone: ${ requestScope.customer.getPhone() }</p>
							
							<input name="customer-id" value="${ customer.getId() }" class="hidden">
														
							<button>Create Account</button>
							<a class="button secondary" href="/bank-app/employee/account/create">
								Go back
							</a>
						</c:when>
					</c:choose>
				</section>
			</form>
		</div>
	</main>
</body>
</html>