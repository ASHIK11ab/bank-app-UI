<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="All branches" />
	</jsp:include>
</head>
<body>
	
	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<main class="container" style="margin-top: 5rem">
		<h1>Branches: <c:out value="${ branches.size() }" /></h1>
		<div class="group">

			<c:forEach items="${ branches }" var="branch" varStatus="loop">			
				<div class="item">
					<a href="/bank-app/admin/branches/${ branch.getId() }/view">
						${ loop.index + 1 }. ${ branch.getName() } Branch
					</a>
				</div>
			</c:forEach>
			
		</div>
	</main>
</body>
</html>