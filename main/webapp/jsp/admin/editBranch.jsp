<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Edit branch" />
	</jsp:include>
</head>
<body>
	
	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<main class="container">
		<section class="form wrapper">
			<h1 class="form-title">Edit branch</h1>
			<h3>Original Branch Name: ${ branchName }</h3>
			<form action="/bank-app/admin/branches/branch/edit" method="post">
				<input name="branch-id" value="${ branchId }" class="hidden" required>
				
				<section class="branch">
					<label>Name:</label>
					<input type="text" placeholder="branch name" name="name" maxlength="20" 
						value="${ inputBranchName }" required>
					<p>Address:</p>
					<jsp:include page="/jsp/components/addressForm.jsp" />		
				</section>
				
				<button>Save</button>
				<a class="button secondary" href="/bank-app/admin/branches/branch/${ branchId }/view">Cancel</a>
			</form>
		</section>
	</main>
</body>
</html>