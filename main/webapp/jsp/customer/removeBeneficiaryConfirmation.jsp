<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
	import = "constant.BeneficiaryType"
	import = "cache.AppCache"    
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Remove beneficiary" />
	</jsp:include>
</head>
<body>
	<jsp:include page="/jsp/customer/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<h1>Confirm Beneficiary details:</h1>
			<h3 style="color: red">Note:</h3>
			<p>You are about to remove the beneficary:</p>
			
			<p>Name: ${ beneficiary.getName() }</p>
	
			<c:if test="${ beneficiary.getNickName().length() > 0 }">
				<p>Nick Name: ${ beneficiary.getNickName() }</p>
			</c:if>
			
			<c:if test="${ BeneficiaryType.getType(type) == BeneficiaryType.OTHER_BANK }">
				<p>Bank Name: ${ AppCache.getIntegratedBank(beneficiary.getBankId()).getName() }
			</c:if>
			
			<form action="/bank-app/customer/remove-beneficiary" method="post"
				style="border: 0; padding: 0;">
				<input name="beneficiary-id" value="${ beneficiary.getId() }" class="hidden" required>
				<input name="type" value="${ type }" class="hidden" required>
				
				<button class="danger">Remove beneficiary</button>
			</form>
			
			<a class="button secondary" style="margin-top: 1rem;"
				href="/bank-app/customer/beneficiaries/${ beneficiary.getId() }/view?type=${ type }">
				Cancel
			</a>
		</div>
	</main>
</body>
</html>