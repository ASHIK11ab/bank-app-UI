<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Admin dashboard</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/components/navbar.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
</head>
<body>
	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<main class="container">
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