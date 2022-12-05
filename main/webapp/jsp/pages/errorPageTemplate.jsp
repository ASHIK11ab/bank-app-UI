<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import = "constant.Role"
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="${ param.title }" />
	</jsp:include>
</head>
<body>
	<c:choose>
	
	<c:when test="${ sessionScope.role != null }">
		<c:set var="userType" value="${ Role.getName(sessionScope.role) }" />
		<jsp:include page="/jsp/${ userType }/components/navbar.jsp" />
	</c:when>
	
	<c:when test="${ sessionScope.role == null }">
		<main class="container">
			<div class="wrapper">
				<jsp:include page="/jsp/components/generalNavbar.jsp" />
			
				<h1 style="color: red">${ param.text }</h1>
				
				<jsp:include page="/jsp/components/footer.jsp" />
			</div>
		</main>
	</c:when>
	</c:choose>
</body>
</html>