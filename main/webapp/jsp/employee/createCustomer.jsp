<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="create customer" />
	</jsp:include>
</head>
<body>
	<jsp:include page="/jsp/employee/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<form action="/bank-app/employee/account/create/new-customer" method="post">
				<input name="account-type" value="${ accountType }" class="hidden">
				<input name="card-type" value="${ cardType }" class="hidden">
				
				<h1>Create Customer</h1>
				<section id="customer-details-section">
					<h2>Customer details:</h2>	
					<jsp:include page="/jsp/components/customerDetailsForm.jsp" />
				</section>
				
				<button>Create Customer</button>
			</form>
		</div>
	</main>
</body>
</html>