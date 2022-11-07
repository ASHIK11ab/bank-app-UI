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
	
	<main class="container">
		<section>
			<h1>Merge Branches</h1>
			
			<c:choose>
				<c:when test="${ branches.size() == 1 }">
					<h3 style="color: red">Only 1 branch exists in bank !!!</h3>
				</c:when>
				
				<c:when test="${ branches.size() > 1 }">
					<c:set var="values" value="${ branches }" scope="request" />
					<form action="/bank-app/admin/branches/merge" method="post">
						
						<jsp:include page="/jsp/components/genericDropdown.jsp">
							<jsp:param name="labelName" value="Base branch:" />
							<jsp:param name="name" value="base-branch-id" />
							<jsp:param name="placeholderOptionText" value="select branch" />
							<jsp:param name="displayId" value="${ false }" />
						</jsp:include>
						
						<jsp:include page="/jsp/components/genericDropdown.jsp">
							<jsp:param name="labelName" value="Target branch:" />
							<jsp:param name="name" value="target-branch-id" />
							<jsp:param name="placeholderOptionText" value="select branch" />
							<jsp:param name="displayId" value="${ false }" />
						</jsp:include>
						
						<button>merge branches</button>
					</form>
				</c:when>
			</c:choose>
		</section>
	</main>
</body>
</html>