<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
	import="cache.AppCache"    
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Contact Us | Your Bank" />
	</jsp:include>
</head>
<body>
	<jsp:include page="/jsp/components/generalNavbar.jsp" />
	<c:set var="bank" value="${ AppCache.getBank() }" />
	<main class="container">
		<div class="wrapper">
			<h1>Contact Page</h1>
			<p>For feedback and queries, use the below information to send an email
			or call to the tollfree number.</p>
			<p><strong>Email:</strong> ${ bank.getSupportEmail() }</p>
			<p><strong>Phone:</strong> ${ bank.getSupportPhone() }</p>
		</div>
	</main>
	
	<jsp:include page="/jsp/components/footer.jsp" />
</body>
</html>