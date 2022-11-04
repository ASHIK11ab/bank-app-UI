<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Update customer details" />
	</jsp:include>
</head>
<body>
	<jsp:include page="/jsp/employee/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<h1>Update customer details</h1>
			<form action="/bank-app/employee/customer/update" method="post">
				<input name="customer-id" value="${ customer.getId() }" class="hidden">
				
				<jsp:include page="/jsp/components/customerDetailsForm.jsp" />
				<button>Save</button>
				<a class="button secondary" href="/bank-app/employee/customer/${ customer.getId() }/view">Cancel</a>
			</form>
		</div>
	</main>
</body>
</html>