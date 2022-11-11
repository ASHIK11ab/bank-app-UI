<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Manager details" />
	</jsp:include>
</head>
<body>	
	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<section>
				<h2 class="title">Manager details:</h2>
				<p>Id   : ${ manager.getId() }</p>
				
				<c:if test="${ requestScope.displayPassword }">
					<p>Password: ${ manager.getPassword() }</p>
				</c:if>
				
				<p>Name : ${ manager.getName() }</p>
				<p>Email: ${ manager.getEmail() }</p>
				<p>Phone: ${ manager.getPhone() }</p>
			</section>
			<section>
				<h2 class="title">Branch details:</h2>
				<p>Branch Name: ${ manager.getBranchName() }</p>
			</section>
			<section>
				<a class="button secondary" href="/bank-app/admin/managers/${ manager.getId() }/password-reset?branch-id=${ manager.getBranchId() }">Reset password</a>
			</section>
		</div>
	</main>
</body>
</html>