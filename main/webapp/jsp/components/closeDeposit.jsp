<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import = "constant.Constants"
    import = "constant.Role"
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="close deposit" />
	</jsp:include>
</head>
<body>
	<c:set var="userType" value='${ Role.getName(sessionScope.role) }' />
	
	<jsp:include page="/jsp/${ userType }/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<form action="/bank-app/${ userType }/deposit/close" method="post">
				<input name="action-type" value="${ actionType }" class="hidden">
				<input name="account-no" value="${ accountNo }" class="hidden">
				
				<c:choose>
					
					<c:when test="${ actionType == 0 }">
						<!-- Require confirmation -->
						<h1>Confirmation Required</h1>
						<p>You are about to close deposit A/C: ${ accountNo }</p>
						<p>Proceeding to this action will close the account</p>
					</c:when>
					
					<c:when test="${ actionType == 1 }">						
						<h1>Confirm Premature Closing</h1>
						<h3 style="color: red">Note:</h3>
						<p>Please read below before closing deposit:</p>
	                    <p>1) On closing of this account the intrest will be applicable only for the available balance.</p>
	                    <p>2) Rupees ${ Constants.PREMATURE_CLOSING_CHARGES } will be charged for premature closing and will be deducted from your deposit account balance</p>
					</c:when>
					
				</c:choose>
				<button class="danger">Proceed to close deposit</button>
				<a class="button secondary" href="/bank-app/${ userType }/deposit/${ accountNo }/view">Cancel</a>
			</form>			
		</div>
	</main>
</body>
</html>