<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Create Customer</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/components/navbar.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/form.css">
</head>
<body>
	<jsp:include page="/jsp/employee/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<form action="/bank-app/employee/account/create/new-customer" method="post">
				<input name="account-type" value="${ requestScope.accountType }" class="hidden">
				<input name="card-type" value="${ requestScope.cardType }" class="hidden">
				
				<h1>Create Customer</h1>
				<section id="customer-details-section">
					<h2>Customer details:</h2>
					
					<h3>General Information:</h3>
					<label>Name:</label>
					<input type="text" placeholder="customer name" name="name" maxlength="20" required>
					<label>Email:</label>
					<input type="email" placeholder="customer email" name="email" maxlength="30" required>
					<label>Phone:</label>
					<input type="number" placeholder="contact number" name="phone" required>
					
					<h3>Personal Information:</h3>
					<label>Age:</label>
					<input type="number" placeholder="customer age" name="age" required>
					<label>Gender:</label>
					<select name="gender" required>
						<option value="-1" selected hidden disabled>select gender</option>
						<option value="m">Male</option>
						<option value="f">Female</option>
					</select>
					<label>Martial Status:</label>
					<select name="martial-status" required>
						<option value="-1" selected hidden disabled>select martial status</option>
						<option value="married">Married</option>
						<option value="unmarried">Un Married</option>
					</select>
					<label>Occupation:</label>
					<input type="text" placeholder="customer occupation" name="occupation" maxlength="20" required>
					<label>Income:</label>
					<input type="number" placeholder="customer income" name="income" required>
					<label>Adhaar No:</label>
					<input type="number" placeholder="addhar number" name="adhaar" required>
					<label>Pan:</label>
					<input type="text" placeholder="pan number" name="pan" maxlength="10" required>
					
					<h3>Address:</h3>
					<jsp:include page="/jsp/components/addressForm.jsp" />
				</section>
				
				<button>Create Customer</button>
			</form>
		</div>
	</main>
</body>
</html>