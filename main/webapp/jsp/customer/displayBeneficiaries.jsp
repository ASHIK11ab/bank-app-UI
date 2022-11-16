<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
	import = "constant.BeneficiaryType"    
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="All Beneficiaries" />
	</jsp:include>
</head>
<body>
	<c:set var="ownBankId" value="${ BeneficiaryType.getId(BeneficiaryType.OWN_BANK) }" />
	<c:set var="otherBankId" value="${ BeneficiaryType.getId(BeneficiaryType.OTHER_BANK) }" />

	<jsp:include page="/jsp/customer/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<h1>View Beneficiaries</h1>
			<form action="#" id="beneficiary-select-form" method="post">
				<label>Select Beneficiary type</label>
				<select id="beneficiary-type-dropdown">
					<option value="-1" hidden disabled selected>select</option>
					<option value="${ ownBankId }">Own Bank</option>
					<option value="${ otherBankId }">Other Bank</option>
				</select>
				<button>Submit</button>
			</form>
			
			<div id="own-bank" class="hidden">
				<c:choose>
					<c:when test="${ ownBankBeneficiaries.size() > 0 }">
						<h2>Own Bank Beneficiaries (${ ownBankBeneficiaries.size() })</h2>
						
						<c:forEach items="${ ownBankBeneficiaries }" var="beneficiary">
							<div style="border-top: 1.5px solid #ddd">
								<p>Name: ${ beneficiary.getName() }</p>
								<p>A/C No: ${ beneficiary.getAccountNo() }</p>
								<a class="button secondary" href="/bank-app/customer/beneficiaries/${ beneficiary.getId() }/view?type=${ ownBankId }">
									view
								</a>
							</div>
							
						</c:forEach>
						
					</c:when>
					
					<c:when test="${ ownBankBeneficiaries.size() == 0 }">
						<h2>No Own Bank beneficiaries</h2>
						<a class="button secondary" href="/bank-app/customer/beneficiaries/add">Add Beneficiary</a>
					</c:when>
					
				</c:choose>
			</div>
			
			<div id="other-bank" class="hidden">
				<c:choose>
					<c:when test="${ otherBankBeneficiaries.size() > 0 }">
						<h2>Other Bank Beneficiaries (${ otherBankBeneficiaries.size() })</h2>
						
						<c:forEach items="${ otherBankBeneficiaries }" var="beneficiary">
							<div style="border-top: 2px solid #ddd; margin-top: 10px">
								<p>Name: ${ beneficiary.getName() }</p>
								<p>A/C No: ${ beneficiary.getAccountNo() }</p>
								<a class="button secondary" href="/bank-app/customer/beneficiaries/${ beneficiary.getId() }/view?type=${ otherBankId }">
									view
								</a>
							</div>
							
						</c:forEach>
						
					</c:when>
					
					<c:when test="${ otherBankBeneficiaries.size() == 0 }">
						<h2>No Other Bank beneficiaries</h2>
						<a class="button secondary" href="/bank-app/customer/beneficiaries/add">Add Beneficiary</a>
					</c:when>
					
				</c:choose>
			</div>
		
		</div>
	</main>
	
	<script>
		const typeSelect = document.getElementById("beneficiary-type-dropdown");
		const ownBankBeneficiaries = document.getElementById("own-bank");
		const otherBankBeneficiaries = document.getElementById("other-bank");
	
		document.getElementById("beneficiary-select-form").addEventListener('submit', function(e) {
			if(typeSelect.value == ${ ownBankId }) {
				ownBankBeneficiaries.classList.remove('hidden');
				otherBankBeneficiaries.classList.add('hidden');
			} else {
				ownBankBeneficiaries.classList.add('hidden');
				otherBankBeneficiaries.classList.remove('hidden');
			}
			
			e.preventDefault();
		});
	</script>
</body>
</html>