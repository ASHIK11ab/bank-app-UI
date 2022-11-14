<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="All managers" />
	</jsp:include>
</head>
<body>	
	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<main class="container">
		<h1>Managers: ${ requestScope.managers.size() }</h1>
		<div class="group">
			
			<c:forEach items="${ requestScope.managers }" var="manager" varStatus="loop">
				<div class="item">
					<a href="/bank-app/admin/managers/${ manager.getId() }/view?branch-id=${ manager.getBranchId() }">
						${ loop.index + 1}. ${ manager.getName() }
					</a>
				</div>
			</c:forEach>
			
		</div>
	</main>
</body>
</html>