<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
	import="constant.Role"
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Mini Statement" />
	</jsp:include>
</head>
<body>
	<c:set var="userType" value='${ Role.getName(sessionScope.role) }' />
	
	<jsp:include page="/jsp/${ userType }/components/navbar.jsp" />
	
	<main class="container">
		<div>
			<h1>Mini Statement</h1>
			<div>
				<h2>Account Details:</h2>
				<p>A/C No: ${ account.getAccountNo() }<p>
				<p>Balance: ${ String.format("%.2f", account.getBalance()) }</p>
			</div>
			<c:set var="transactions" value="${ account.getMiniStatement() }" scope="request" />
			
			<jsp:include page="/jsp/components/transactionHistoryTable.jsp">
				<jsp:param name="fallbackText" value="No recent transactions" />
			</jsp:include>
		</div>
	</main>
</body>
</html>