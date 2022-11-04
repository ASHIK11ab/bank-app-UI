<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Reset customer password" />
	</jsp:include>
</head>
<body>
	<jsp:include page="/jsp/employee/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<h1>Reset customer password</h1>
			<h3>Customer Details:</h3>
			<p>Customer Id: ${ customer.getId() }</p>
			<p>Customer Name: ${ customer.getName() }</p>
			<form action="/bank-app/employee/customer/password-reset" method="post">
				<input name="customer-id" value="${ customer.getId() }" class="hidden" required>
				
				<label>New Password (8 to 15 characters):</label>
				<input type="text" name="password" placeholder="customer's new password" required>
				
				<button>Reset password</button>
				<a class="button secondary" href="/bank-app/employee/customer/${ customer.getId() }/view">Cancel</a>
			</form>
		</div>
	</main>
</body>
</html>