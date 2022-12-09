<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Remove integrated bank" />
	</jsp:include>
</head>
<body>
	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<h1>${ actionType == 0 ? 'Remove Integrated Bank' : 'Confirm Integrated Bank details' }</h1>
			<form action="/bank-app/admin/integrated-banks/bank/delete" method="post">
				<input name="actionType" value="${ actionType }" class="hidden">
				
				<c:if test="${ actionType == 1 }">
					<c:set var="selectedId" value="${ bank.getId() }" scope="request" />
				</c:if>
				
				<div class="${ actionType == 1 ? 'hidden' : '' }">
					<jsp:include page="/jsp/components/genericDropdown.jsp">
						<jsp:param name="labelName" value="Select Bank:" />
						<jsp:param name="name" value="bank-id" />
						<jsp:param name="placeholderOptionText" value="select bank" />
						<jsp:param name="displayId" value="${ false }" />
					</jsp:include>
				</div>
				
				<c:if test="${ actionType == 1 }">
					<h3>Integrated Bank details</h3>
					<p>Bank Name: ${ bank.getName() }</p>
					<p>Bank Email: ${ bank.getEmail() }</p>
					<p>You are about to remove the integrated bank. This action cannot be reversed. Proceed to remove integrated bank</p>
				</c:if>
				
				<button class="danger" >Remove bank</button>
				
				<c:if test="${ actionType == 1 }">
					<a class="button secondary" href="/bank-app/admin/integrated-banks/bank/delete">Cancel</a>
				</c:if>
			</form>
		</div>
	</main>
</body>
</html>