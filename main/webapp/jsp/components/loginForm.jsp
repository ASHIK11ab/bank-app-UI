<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
	import = "constant.Role"    
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="${ title }" />
	</jsp:include>
</head>
<body>
	<main class="container">
		<div class="wrapper">
			<h1 class="app-name">Your Bank</h1>
			<section class="form">
				<h2 class="form-title">${ requestScope.title }</h2>
				<form action="${ requestScope.actionURL }" method="post">
					
					<c:if test="${ forRole == Role.EMPLOYEE || forRole == Role.MANAGER }">
						<c:set var="values" value="${ branches }" scope="request" />
						<jsp:include page="/jsp/components/genericDropdown.jsp">
							<jsp:param name="placeholderOptionText" value="select branch" />
							<jsp:param name="name" value="branch-id" />
							<jsp:param name="labelName" value="Select Branch:" />
							<jsp:param name="displayId" value="${ false }" />
						</jsp:include>
					</c:if>
				
					<label for="id">id:</label>
					<input type="number" id="id" name="id" required>
					<label for="password">password:</label>
					<input type="password" id="password" name="password" required maxlength="15">
					
					<c:if test="${ requestScope.error != null }">
						<span class='error'>${ requestScope.error }</span>
					</c:if>
					
					<button>Login</button>		
				</form>
			</section>
		</div>
	</main>
</body>
</html>