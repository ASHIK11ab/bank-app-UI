<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="password reset" />
	</jsp:include>
</head>
<body>
	
	<jsp:include page="/jsp/${ requestScope.userType }/components/navbar.jsp" />

	<main class="container">
		<div class="wrapper">
			<h1>Password Reset</h1>
			<form action="/bank-app/${ requestScope.userType }/password-reset" method="post">
				<label>Old Password:</label>
				<input type="text" name="old-password" maxlength="15" required>
				<label>New Password:</label>
				<input type="text" name="new-password" maxlength="15" required>
				<button>Reset Password</button>
			</form>
		</div>
	</main>
</body>
</html>