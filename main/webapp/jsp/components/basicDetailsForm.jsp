<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page 
	import="constant.Role"
	%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="update details | ${ Role.getName(sessionScope.role) }" />
	</jsp:include>
</head>
<body>
	
	<c:set var="role" value="${ sessionScope.role }" />
	<c:set var="userType" value='${ Role.getName(role) }' />
	
	<jsp:include page="/jsp/${ userType }/components/navbar.jsp" />	
	
	<main class="container">
		<div class="wrapper">
			<section>
				<h1>Update details</h1>
				<form action="/bank-app/${ targetUserType }/profile/update" method="post">

					<label>Name:</label>
					<input type="text" placeholder="name" name="name" 
						value="${ user.getName() }" maxlength="20" required>
					<label>Email:</label>
					<input type="email" placeholder="email address" name="email" 
						value="${ user.getEmail() }" maxlength="30" required>
					<label>Phone:</label>
					<input type="number" placeholder="contact number" name="phone" 
						value="${ user.getPhone() }" required>						

					<button>save</button>
					<a class="button secondary" href="/bank-app/${ userType }/dashboard">Cancel</a>
				</form>
			</section>
		</div>
	</main>
</body>
</html>