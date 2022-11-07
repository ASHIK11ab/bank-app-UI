<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="${ branch.getName() } branch" />
	</jsp:include>
</head>
<body>
	
	<jsp:include page="/jsp/admin/components/navbar.jsp" />

	<c:set var="branch" value="${ requestScope.branch }" />
	<c:set var="manager" value="${ branch.getManager() }" />
	
	<main class="container">
		<div class="wrapper">
			<section class="branch-section">
				<h2 class="title">Branch details:</h2>
				<p>Name: ${ branch.getName() }</p>
				<p>Address: ${ branch.getAddress() }</p>
				
				<a class="button" href="/bank-app/admin/branches/branch/${ branch.getId() }/edit">Edit</a>
			</section>
			<section class="manager-section">
				<h2 class="title">Manager details:</h2>
				<p>Id: ${ manager.getId() }</p>
				
				<c:if test="${ requestScope.displayManagerPassword }">
					<p>Password: ${ manager.getPassword() }<p>
				</c:if>
				
				<p>Name: ${ manager.getName() }</p>
				<a class="button" href="/bank-app/admin/managers/manager/${ manager.getId() }/view">View manager</a>
				<a class="button secondary" href="/bank-app/admin/branches/branch/${ branch.getId() }/assign-manager">Assign new manager</a>
			</section>
		</div>
	</main>
</body>
</html>