<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<c:set var="title" value='${ type == 0 ? "Add Integrated Bank" : "Edit Integrated Bank" }' />
	
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="${ title }" />
	</jsp:include>
</head>
<body>

	<c:set var="action" value='${ type == 0 ? "add" : "edit" }' />
	<c:set var="buttonText" value='${ type == 0 ? "Add Integrated Bank" : "Save" }' />

	<jsp:include page="/jsp/admin/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<h1>${ title }</h1>
			<form action="/bank-app/admin/integrated-banks/bank/${ action }" method="post">
				<input name="type-id" value="${ type }" class="hidden" />
				
				<h2>Bank details:</h2>
				<jsp:include page="/jsp/components/integratedBankForm.jsp" />
				
				<button>${ buttonText }</button>
				
				<!-- Type 1 -> Edit (Store the bank id as hidden field, display cancel button) -->
				<c:if test="${ type == 1 }">
					<input name="bank-id" value="${ bank.getId() }" class="hidden" />
					<a class="button secondary" href="/bank-app/admin/integrated-banks/${ bank.getId() }/view">
						Cancel
					</a>
				</c:if>
			</form>
		</div>
	</main>
</body>
</html>