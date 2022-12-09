<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Activate card" />
	</jsp:include>
</head>
<body>
	<jsp:include page="/jsp/customer/components/navbar.jsp" />

	<main class="container">
		<div class="wrapper">
			<form action="/bank-app/customer/activate-card" method="post">
				<input name="card-no" value="${ cardNo }" class="hidden" required>
				
				<h1>Activate card</h1>
				<p><span class="bold">Card No: </span>${ cardNo }</p>

				<label>Enter Pin:</label>
				<input type="password" name="pin" placeholder="4 digit pin of your card" required>
				
				<label>Enter cvv:</label>
				<input type="password" name="cvv" placeholder="3 digit number on back of debit card" required>
				
				<button>Activate card</button>
				<a class="button secondary" href="/bank-app/customer/card/${ cardNo }/view">cancel</a>
			</form>
		</div>
	</main>
</body>
</html>