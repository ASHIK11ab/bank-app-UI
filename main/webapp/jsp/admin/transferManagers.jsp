<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Transfer managers" />
	</jsp:include>
</head>
<body>

	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<main class="container">
		<section>
			<h1>${ actionType == 0 ? 'Transfer Managers' : 'Confirm Managers for transfer' }</h1>
			<form action="/bank-app/admin/managers/transfer" method="post">
				<input name="actionType" value="${ actionType }" class="hidden">
				
				<c:choose>
					<c:when test="${ actionType == 0 }">
						<jsp:include page="/jsp/components/genericDropdown.jsp">
							<jsp:param name="labelName" value="First branch:" />
							<jsp:param name="name" value="first-branch-id" />
							<jsp:param name="placeholderOptionText" value="select branch" />
							<jsp:param name="displayId" value="${ false }" />
						</jsp:include>
						
						<jsp:include page="/jsp/components/genericDropdown.jsp">
							<jsp:param name="labelName" value="Second branch:" />
							<jsp:param name="name" value="second-branch-id" />
							<jsp:param name="placeholderOptionText" value="select branch" />
							<jsp:param name="displayId" value="${ false }" />
						</jsp:include>
					</c:when>
					
					<c:when test="${ actionType == 1 }">
						<input name="first-branch-id" value="${ baseBranchManager.getBranchId() }" class="hidden">
						<input name="second-branch-id" value="${ targetBranchManager.getBranchId() }" class="hidden">
					
						<div>
							<h2>Base branch Manager:</h2>
							<p>Manager id: ${ baseBranchManager.getId() }</p>
							<p>Manager name: ${ baseBranchManager.getName() }</p>
							<p>Branch Name: ${ baseBranchManager.getBranchName() }</p>
						</div>
						
						<div>
							<h2>Target branch Manager:</h2>
							<p>Manager id: ${ targetBranchManager.getId() }</p>
							<p>Manager name: ${ targetBranchManager.getName() }</p>
							<p>Branch Name: ${ targetBranchManager.getBranchName() }</p>
						</div>
						
						<h3 style="color: red">Note:</h3>
						<p>Proceeding to this action will the transfer managers. Proceed to transfer managers.</p>
					</c:when>
					
				</c:choose>
				
				<button class="${ actionType == 1 ? 'danger' : '' }" >Transfer managers</button>
				
				<c:if test="${ actionType == 1 }">
					<a class="button secondary" href="/bank-app/admin/managers/transfer">Cancel</a>
				</c:if>
			</form>
		</section>
	</main>
</body>
</html>