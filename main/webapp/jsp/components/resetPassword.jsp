<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
	import = "constant.Role"    
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
	<c:set var="role" value="${ sessionScope.role }" />
	
	<c:choose>
		<c:when test="${ forRole == role }">
			<c:if test="${ forRole == Role.CUSTOMER }">
				<c:set var="title" value='${ type == 0 ? "Login password reset" : "Transaction password reset" }' />
			</c:if>	
			
			<c:if test="${ forRole != Role.CUSTOMER }">
				<c:set var="title" value="Password Reset" />
			</c:if>
		</c:when>
		
		<c:when test="${ forRole != role }">
			<c:set var="title" value="${ Role.getName(forRole) } password reset" />
		</c:when>
	</c:choose>

	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="${ title }" />
	</jsp:include>
<body>
	
	<c:set var="userType" value="${ Role.getName(role) }" />

	<jsp:include page="/jsp/${ userType }/components/navbar.jsp" />

	<main class="container">
		<section class="wrapper">
			<h1 class="title">${ title }</h1>
			
			<!-- Only display id and name when resetting other user password -->
			<c:if test="${ forRole != role }">
				<p><strong>Id: ${ id }</strong></p>
				<p><strong>Name: ${ name }</strong></p>
			</c:if>
			
			<form action="/bank-app/password-reset" method="post">
			
				<input name="id" value="${ id }" class="hidden" required>
				<input name="for-role" value="${ forRole }" class="hidden" required>
				<input name="redirectURI" value="${ redirectURI }" class="hidden" required>
				
				<!-- Branch id is only required for branch employees -->
				<c:if test="${ forRole == Role.EMPLOYEE || forRole == Role.MANAGER }">
					<input name="branch-id" value="${ branchId }" class="hidden" required>
				</c:if>
				
				<!-- Type 0 -> login password, type 1 -> transaction password -->
				<c:if test="${ forRole == Role.CUSTOMER }">
					<input name="type" value="${ type }" class="hidden" required>
				</c:if>
				
				<!-- Prompt for old password during self password reset -->
				<c:if test="${ forRole == role }">
					<label>Old Password:</label>
					<input type="password" name="old-password" placeholder="old password" maxlength="15" required>
				</c:if>
				
				<label>New Password:</label>
				<input type="text" name="password" placeholder="new password" maxlength="15" required>
				
				<button>Reset password</button>
			</form>
			
		</section>
	</main>
</body>
</html>