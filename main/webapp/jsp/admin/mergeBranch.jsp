<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Merge Branches</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/component/navbar.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/form.css">
</head>
<body>

	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<main class="container">
		<section>
			<h1>Merge Branches</h1>
			<form action="/bank-app/admin/branches/branch/merge" method="post">
				<label>Base branch:</label>
				<select name="base-branch-id" id="base-branch-id">
					<option value="-1" selected disabled hidden>Select branch</option>
					<c:forEach items="${ requestScope.branches }" var="branch">
						<option value="${ branch.getId() }">${ branch.getName() }</option>
					</c:forEach>
				</select>
				<label>Target branch:</label>
				<select name="target-branch-id" id="target-branch-id">
					<option value="-1" selected disabled hidden>Select branch</option>
					<c:forEach items="${ requestScope.branches }" var="branch">
						<option value="${ branch.getId() }">${ branch.getName() }</option>
					</c:forEach>
				</select>
				
				<button>merge branches</button>
			</form>
		</section>
	</main>
</body>
</html>