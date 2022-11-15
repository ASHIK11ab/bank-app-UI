<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Beneficiary details" />
	</jsp:include>
</head>
<body>
	<jsp:include page="/jsp/customer/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<h1>Beneficiary details:</h1>
			
			<jsp:include page="/jsp/components/beneficiary.jsp">
				<jsp:param name="type" value="${ type }" />
			</jsp:include>
			
			<a class="button secondary" href="/bank-app/customer/beneficiaries/${ beneficiary.getId() }/edit?type=${ type }">
				Edit
			</a>
			<a class="button danger" href="/bank-app/customer/beneficiaries/${ beneficiary.getId() }/remove?type=${ type }">
				Remove
			</a>
		</div>
	</main>
</body>
</html>