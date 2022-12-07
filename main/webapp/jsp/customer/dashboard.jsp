<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Dashboard | Customer" />
	</jsp:include>
</head>
<body>
	<jsp:include page="/jsp/customer/components/navbar.jsp" />
	
	<main class="container">
		<h1>Dashboard</h1>
		<div class="cards">
		
			<c:forEach items="${ requestScope.stats }" var="stat">
				<div class ="card">
					<span class="title">${ stat.get("title") }</span>
					<span class="cnt">${ stat.get("cnt") }</span>
				</div>
			</c:forEach>
			
		</div>
	</main>
</body>
</html>