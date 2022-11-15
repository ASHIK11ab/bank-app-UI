<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
	import = "constant.BeneficiaryType"    
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<c:set var="title" value='${ actionType == 0 ? "Add" : "Edit" } Beneficiary' />
	<c:set var="buttonText" value='${ actionType == 0 ? "Add Beneficiary" : "Save" }' />
	
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="${ title }" />
	</jsp:include>
</head>
<body>
	<c:set var="ownBankId" value="${ BeneficiaryType.getId(BeneficiaryType.OWN_BANK) }" />
	<c:set var="otherBankId" value="${ BeneficiaryType.getId(BeneficiaryType.OTHER_BANK) }" />

	<jsp:include page="/jsp/customer/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<h1>${ title }</h1>
			
			<!-- Display some information about the beneficiary to be updated -->
			
			<form action="/bank-app/customer/add-edit-beneficiary" id="beneficiary-select-form" method="post">
				<input name="action-type" value="${ actionType }" class="hidden" required>
				
				<!-- Store beneficiary id incase of update -->
				<c:if test="${ actionType == 1 }">
					<input name="beneficiary-id" value="${ beneficiary.getId() }" class="hidden" required>
					<input name="type" value="${ type }" class="hidden" required>
				</c:if>
				
				<c:if test="${ actionType == 0 }">
					<label>Select Beneficiary type</label>
					<select id="beneficiary-type-dropdown" name="type">
						<option value="-1" hidden disabled selected>select</option>
						<option value="${ ownBankId }" ${ type == ownBankId ? "selected" : "" }>Own Bank</option>
						<option value="${ otherBankId }" ${ type == otherBankId ? "selected": "" }>Other Bank</option>
					</select>
				</c:if>
				
				<label>Name:</label>
				<input type="text" name="name" value="${ beneficiary.getName() }"
					maxlength="20" placeholder="Beneficiary name" required>
					
				<label>Nick Name:</label>
				<input type="text" name="nick-name" value="${ beneficiary.getNickName() }"
					maxlength="15" placeholder="Beneficiary nick name">
				
				<label>Account No:</label>
				<input type='${ actionType == 0 ? "password" : "text" }' name="account-no" placeholder="account no"
					value="${ beneficiary.getAccountNo() }" required>
				
				<!-- Only display during creating a beneficiary -->
				<c:if test="${ actionType == 0 }">
					<label>Confirm Account No:</label>
					<input type="number" placeholder="retype account number" name="confirm-account-no" required>
				</c:if>
				
				<!-- Display the below section only on creation and when editing a other bank beneficiary -->
				<c:if test="${ actionType == 0 || (actionType == 1 && type == otherBankId) }">
					
					<!-- Initially hidden during creation -->
					<div id="other-bank-section" class='${ actionType == 0 ? "hidden" : "" }'>
						<label>IFSC:</label>
						<input type="text" name="ifsc" placeholder="11 digit IFSC code"
							value="${ beneficiary.getIFSC() }" maxlength="11">
										
						<label>Select Bank</label>
						<select name="bank-id">
							<option value="-1" disabled hidden selected>Select</option>
							<c:forEach items="${ banks }" var="bank">
								<option value="${ bank.getId() }" ${ beneficiary.getBankId() == bank.getId() ? "selected" : "" }>
									${ bank.getName() }
								</option>
							</c:forEach>
						</select>
					</div>
				</c:if>
				
				<button>${ buttonText }</button>
				
				<c:if test="${ actionType == 1 }">
					<a class="button secondary" href="/bank-app/customer/beneficiaries/${ beneficiary.getId() }/view?type=${ type }">
						Cancel
					</a>
				</c:if>
			</form>
		</div>
	</main>
	
	<c:if test="${ actionType == 0 }">
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
	</c:if>
</body>
</html>