<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import = "constant.Role"
	import = "constant.DebitCardType"    
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Block / Unblock Management" />
	</jsp:include>
</head>
<body>
	<c:set var="role" value="${ sessionScope.role }" />
	<c:set var="userType" value='${ Role.getName(sessionScope.role) }' />
	
	<jsp:include page="/jsp/${ userType }/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<form action="/bank-app/${ userType }/card/block-unblock" method="post">
				<input name="card-no" value="${ cardNo }" class="hidden" required>
				
				<h1>Block / Unblock card</h1>
				<p><span class="bold">Card No: </span>${ cardNo }</p>
				
				<label>Select action:</label>
				<select name="activation-type" required>
					<option value="-1" selected hidden>select type</option>
					<option value="1">
						Unblock
					</option>
					<option value="0">
						Block
					</option>
				</select>
				
				<c:if test="${ role == Role.CUSTOMER }">
					<label>Enter Pin:</label>
					<input type="password" name="pin" placeholder="4 digit pin of your card" required>
				</c:if>
				<button class="danger">submit</button>
			</form>
		</div>
	</main>
</body>
</html>