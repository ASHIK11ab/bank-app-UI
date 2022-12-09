<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Merge branches" />
	</jsp:include>
</head>
<body>

	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<c:set var="title" value='${ actionType == 0 ? "Merge Branches" : "Confirm Branch Details for Merge" }' />
	
	<main class="container">
		<section>
			<h1>${ title }</h1>
			
			<c:choose>
				<c:when test="${ branches.size() == 1 }">
					<h3 style="color: red">Only 1 branch exists in bank !!!</h3>
				</c:when>
				
				<c:when test="${ branches.size() > 1 }">
					<c:set var="values" value="${ branches }" scope="request" />
					
					<form action="/bank-app/admin/branches/merge" method="post">
						<input name="action-type" value="${ actionType }" class="hidden">
						
						<div class="${ actionType == 1 ? 'hidden' : '' }">
							<c:set var="selectedId" value="${ baseBranch.getId() }" scope="request" />
							
							<jsp:include page="/jsp/components/genericDropdown.jsp">
								<jsp:param name="labelName" value="Base branch:" />
								<jsp:param name="name" value="base-branch-id" />
								<jsp:param name="placeholderOptionText" value="select branch" />
								<jsp:param name="displayId" value="${ false }" />
							</jsp:include>
							
							<c:set var="selectedId" value="${ targetBranch.getId() }" scope="request" />
							
							<jsp:include page="/jsp/components/genericDropdown.jsp">
								<jsp:param name="labelName" value="Target branch:" />
								<jsp:param name="name" value="target-branch-id" />
								<jsp:param name="placeholderOptionText" value="select branch" />
								<jsp:param name="displayId" value="${ false }" />
							</jsp:include>
						</div>
						
						<c:if test="${ actionType == 1 }">
							<h3>Base branch details:</h3>
							<p>Branch: ${ baseBranch.getName() }</p>
							<p>Address: ${ baseBranch.getAddress() }</p>
							
							<h3>Target branch details:</h3>
							<p>Branch: ${ targetBranch.getName() }</p>
							<p>Address: ${ targetBranch.getAddress() }</p>
						</c:if>
						
						<button>merge branches</button>
						
						<c:if test="${ actionType == 1 }">
							<a class="button secondary" href="/bank-app/admin/branches/merge">Cancel</a>
						</c:if>
					</form>
				</c:when>
			</c:choose>
		</section>
	</main>
</body>
</html>