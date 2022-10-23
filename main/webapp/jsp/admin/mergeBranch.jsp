<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Merge Branches</title>
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/global.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/components/navbar.css">
	<link rel="stylesheet" href="http://localhost:8080/bank-app/css/form.css">
</head>
<body>

	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<main class="container">
		<section>
			<h1>Merge Branches</h1>
			<form action="/bank-app/admin/branches/branch/merge" method="post">
				
				<label>Base branch:</label>
				<jsp:include page="/jsp/components/branchDropdown.jsp">
					<jsp:param name="name" value="base-branch-id" />
				</jsp:include>
				
				<label>Target branch:</label>
				<jsp:include page="/jsp/components/branchDropdown.jsp">
					<jsp:param name="name" value="target-branch-id" />
				</jsp:include>
				
				<button>merge branches</button>
			</form>
		</section>
	</main>
</body>
</html>