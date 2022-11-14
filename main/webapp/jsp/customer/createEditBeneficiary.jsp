<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
	import = "constant.BeneficiaryType"    
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<c:set var="title" value='${ actionType == 0 ? "Add" : "Edit" } Beneficiary' />

	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="${ title }" />
	</jsp:include>
</head>
<body>
	<c:set var="ownBankId" value="${ BeneficiaryType.getId(BeneficiaryType.OWN_BANK) }" />
	<c:set var="otherBankId" value="${ BeneficiaryType.getId(BeneficiaryType.OTHER_BANK) }" />
	<c:set var="actionURL" value='/bank-app/customer/${ actionType == 0 ? "add" : "edit" }-beneficiary' />

	<jsp:include page="/jsp/customer/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<h1>${ title }</h1>
			<form action="${ actionURL }" id="beneficiary-select-form" method="post">
				<input name="action-type" value="${ actionType }" class="hidden" required>
				
				<label>Select Beneficiary type</label>
				<select id="beneficiary-type-dropdown" name="type">
					<option value="-1" hidden disabled selected>select</option>
					<option value="${ ownBankId }" ${ type == ownBankId ? "selected" : "" }>Own Bank</option>
					<option value="${ otherBankId }" ${ type == otherBankId ? "selected": "" }>Other Bank</option>
				</select>
				
				<label>Name:</label>
				<input type="text" name="name" value="${ beneficiary.getName() }"
					maxlength="20" placeholder="Beneficiary name" required>
					
				<label>Nick Name:</label>
				<input type="text" name="nick-name" value="${ beneficiary.getNickName() }"
					maxlength="15" placeholder="Beneficiary nick name" required>
				
				<label>Account No:</label>
				<input type="password" name="account-no" placeholder="account no"
					value="${ beneficiary.getAccountNo() }" required>
				
				<!-- Only display during creating a beneficiary -->
				<c:if test="${ actionType == 0 }">
					<label>Confirm Account No:</label>
					<input type="number" placeholder="retype account number" name="confirm-account-no" required>
				</c:if>
				
				<div id="other-bank-section" class='hidden'>
					<label>IFSC:</label>
					<input type="text" name="ifsc" placeholder="11 digit IFSC code"
						value="${ beneficiary.getIFSC() }" maxlength="11">
									
					<label>Select Bank</label>
					<select name="bank-id">
						<option value="-1" disabled hidden selected>Select</option>
						<c:forEach items="${ banks }" var="bank">
							<option value="${ bank.getId() }" ${ beneficiary.getId() == bank.getId() ? "selected" : "" }>
								${ bank.getName() }
							</option>
						</c:forEach>
					</select>
				</div>
				
				<button>Submit</button>
			</form>
		</div>
	</main>
	
	<script>
		const typeSelect = document.getElementById("beneficiary-type-dropdown");
		const otherBankSection = document.getElementById("other-bank-section");
		
		typeSelect.addEventListener('change', function() {
			if(typeSelect.value == ${ otherBankId })
				otherBankSection.classList.remove("hidden");
			else
				otherBankSection.classList.add("hidden");
		});
	</script>
</body>
</html>