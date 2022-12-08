<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="cache.AppCache"
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Branches list" />
	</jsp:include>
</head>
<body>
	<jsp:include page="/jsp/components/generalNavbar.jsp" />
	
	<c:set var="branches" value="${ AppCache.getBank().getBranches() }" />
	<main class="container">
		<h1>Branches</h1>
		<p>YOUR BANK has branches across all states in india. Currently we have ${ branches.size() } branches in India.</p>
		<p>A detailed list of all our branches is given below:</p>
		<div>
			<c:forEach items="${ branches }" var="branch" varStatus="loop">
				<div>
					<p>${ loop.index + 1}) Name: ${ branch.getName() }</p>
					<p>Address: ${ branch.getAddress() }</p>
				</div>
			</c:forEach>
		</div>
	</main>
	
	<jsp:include page="/jsp/components/footer.jsp" />
</body>
</html>