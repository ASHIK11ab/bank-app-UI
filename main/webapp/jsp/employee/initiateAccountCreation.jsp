<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
	import="constant.RegularAccountType"
	import="constant.DebitCardType"    
%>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Initiate Account Creation" />
	</jsp:include>
</head>
<body>
	<jsp:include page="/jsp/employee/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<h1>Account creation</h1>
			<form action="/bank-app/employee/account/create" method="post">
				<label>Customer Type</label>
				<select name="customer-type" required>
					<option value="-1" selected hidden disabled>select type</option>
					<option value="0">New Customer</option>
					<option value="1">Existing Customer</option>
				</select>
				<section>
					<h2>Account details:</h2>
					<label>Account type:</label>
					<select name="account-type" required>
						<option value="-1" selected hidden disabled>select type</option>
						<option value="${ RegularAccountType.getId(RegularAccountType.SAVINGS) }">Savings</option>
						<option value="${ RegularAccountType.getId(RegularAccountType.CURRENT) }">Current</option>
					</select>
					<label>Debit Card Type:</label>
					<select name="card-type">
						<option value="-1" selected hidden disabled>select type</option>
						<option value="${ DebitCardType.getId(DebitCardType.CLASSIC_DEBIT_CARD) }">CLASSIC DEBIT CARD</option>
						<option value="${ DebitCardType.getId(DebitCardType.PLATINUM_DEBIT_CARD) }">PLATINUM DEBIT CARD</option>
					</select>
				</section>
				<button>Initiate Account Creation</button>
			</form>
		</div>
	</main>
</body>
</html>