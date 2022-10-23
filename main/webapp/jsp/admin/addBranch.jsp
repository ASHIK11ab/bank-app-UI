<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Create new branch</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/component/navbar.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/form.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/admin/add-branch.css">
</head>
<body>
	
	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<main class="container">
		<section class="form">
			<h1 class="form-title">add branch</h1>
			<form action="/bank-app/admin/branches/branch/create" method="post">
				<section class="branch">
					<h3>Branch Details:</h3>
					<label>Name:</label>
					<input type="text" placeholder="branch name" name="name" maxlength="20" required>
					<p>Address:</p>
					<label>Door no:</label>
					<input type="text" placeholder="door no" name="door-no" maxlength="10" required>
					<label>Street:</label>
					<input type="text" placeholder="street name" name="street" maxlength="30" required>
					<label>City:</label>
					<input type="text" placeholder="city name" name="city" maxlength="15" required>
					<label>State:</label>
					<input type="text" placeholder="state name" name="state" maxlength="15" required>
					<label>Pincode:</label>
					<input type="number" placeholder="pincode" name="pincode" required>			
				</section>
				
				<section class="manager">
					<h3>Manager Details:</h3>
					<label>Name:</label>
					<input type="text" placeholder="manager name" name="manager-name" maxlength="20" required>
					<label>Email:</label>
					<input type="email" placeholder="email address" name="manager-email" maxlength="30" required>
					<label>Phone:</label>
					<input type="number" placeholder="contact number" name="manager-phone" required>
				</section>
				
				<button>Create branch</button>
			</form>
		</section>
	</main>
</body>
</html>