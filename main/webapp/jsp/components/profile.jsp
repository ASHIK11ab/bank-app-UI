<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
	import = "constant.Role"    
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Profile" />
	</jsp:include>
</head>
<body>
	<c:set var="userType" value="${ Role.getName(sessionScope.role) }" />
	
	<jsp:include page="/jsp/${ userType }/components/navbar.jsp" />
	
	<main class="container">
		<section class="wrapper">
			<h1>Your profile</h1>
			
			<c:choose>
				<c:when test="${ sessionScope.role == Role.CUSTOMER }">
					<c:set var="customer" value="${ user }" scope="request" />
					<jsp:include page="/jsp/components/customer.jsp" />
					
					<a class="button" href="/bank-app/customer/profile/update">Edit profile</a>
					<a class="button secondary" href="/bank-app/customer/profile/password-reset?type=0">Reset password</a>
					<a class="button" href="/bank-app/customer/profile/password-reset?type=1">Reset transaction password</a>
				</c:when>
				
				<c:when test="${ sessionScope.role != Role.CUSTOMER }">
					<p>Id: ${ user.getId() }</p>
					<p>Name: ${ user.getName() }</p>
					<p>Phone: ${ user.getPhone() }</p>
					<p>Email: ${ user.getEmail() }</p>
					
					<c:if test="${ sessionScope.role == Role.EMPLOYEE || sessionScope.role == Role.MANAGER }">
						<p>Branch: ${ user.getBranchName() }</p>
					</c:if>
					
					<a class="button" href="/bank-app/${ userType }/profile/password-reset?type=0">Reset password</a>
				</c:when>
			</c:choose>
			
		</section>
	</main>
</body>
</html>