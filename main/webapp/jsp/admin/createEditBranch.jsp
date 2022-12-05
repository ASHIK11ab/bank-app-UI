<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<c:set var="title" value='${ type == 0 ? "Create Branch" : "Edit Branch" }' />

	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="${ title }" />
	</jsp:include>
</head>
<body>
	<c:set var="action" value='${ type == 0 ? "create" : "edit" }' />
	<c:set var="buttonText" value='${ type == 0 ? "create branch" : "save" }' />
	
	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<main class="container">
		<section class="form wrapper">
			<h1 class="form-title">${ title }</h1>
			<form action="/bank-app/admin/branches/branch/${ action }" method="post">
				<input name="type" value="${ type }" class="hidden" required>
			
				<section class="branch">
					<h3>Branch Details:</h3>
					<label>Name:</label>
					<input type="text" placeholder="branch name" name="name" maxlength="20" 
						value="${ branch.getName() }" required>
						
					<c:set var="address" value="${ branch.getAddress() }" scope="request" />
						
					<p>Address:</p>
					<jsp:include page="/jsp/components/addressForm.jsp" />		
				</section>
				
				<!-- Only display manager form on branch creation -->
				<c:if test="${ type == 0 }">
					<section class="manager">
						<h3>Manager Details:</h3>
						
						<jsp:include page="/jsp/components/employeeRegistrationForm.jsp">
							<jsp:param name="employeeType" value="manager" />
						</jsp:include>			
					
					</section>
				</c:if>
				
				<button>${ buttonText }</button>
				<!-- On edit store branch id as hidden input field -->
				<c:if test="${ type == 1 }">
					<input name="branch-id" value="${ branchId }" class="hidden" required>
					
					<a class="button secondary" href="/bank-app/admin/branches/${ branchId }/view">
						Cancel
					</a>
				</c:if>
			</form>
		</section>
	</main>
</body>
</html>