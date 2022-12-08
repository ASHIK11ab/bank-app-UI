<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="cache.AppCache"
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Integrated banks list" />
	</jsp:include>
</head>
<body>
	<jsp:include page="/jsp/components/generalNavbar.jsp" />
	
	<c:set var="banks" value="${ AppCache.getBank().getIntegratedBanks() }" />
	<main class="container">
		<h1>Integrated Banks</h1>
		<p>We have integrations over others banks in India.</p>
		<p>A detailed list of integrated banks is given below:</p>
		<ol>
			<c:forEach items="${ banks }" var="bank">
				<li>
					${ bank.getName() }
				</li>	
			</c:forEach>
		</ol>
	</main>
	
	<jsp:include page="/jsp/components/footer.jsp" />
</body>
</html>