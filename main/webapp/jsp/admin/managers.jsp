<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Managers</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/component/navbar.css">
</head>
<body>	
	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<main class="container">
		<h1>Managers: ${ requestScope.managers.size() }</h1>
		<div class="group">
			
			<c:forEach items="${ requestScope.managers }" var="manager" varStatus="loop">
				<div class="item">
					<a href="/bank-app/admin/managers/manager/${ manager.getId() }">
						${ loop.index + 1} ${ manager.getName() }
					</a>
				</div>
			</c:forEach>
			
		</div>
	</main>
</body>
</html>