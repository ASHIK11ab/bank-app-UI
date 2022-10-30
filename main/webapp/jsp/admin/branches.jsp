<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import ="model.Branch" %>

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>All branches</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/components/navbar.css">
</head>
<body>
	
	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<main class="container" style="margin-top: 5rem">
		<h1>Branches: <c:out value="${ requestScope.branches.size() }" /></h1>
		<div class="group">

			<c:forEach items="${ requestScope.branches }" var="branch" varStatus="loop">			
				<div class="item">
					<a href="/bank-app/admin/branches/branch/${ branch.getId() }">
						${ loop.index + 1 }. ${ branch.getName() } Branch
					</a>
				</div>
			</c:forEach>
			
		</div>
	</main>
</body>
</html>