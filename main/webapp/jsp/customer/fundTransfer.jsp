<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import = "cache.AppCache"
	import = "constant.*"    
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Fund Transfer" />
	</jsp:include>
</head>
<body>
	<!-- Globally used variables -->
	<c:set var="neftId" value="${ TransactionType.getId(TransactionType.NEFT) }" />
	<c:set var="impsId" value="${ TransactionType.getId(TransactionType.IMPS) }" />
	<c:set var="rtgsId" value="${ TransactionType.getId(TransactionType.RTGS) }" />
	<c:set var="ownBankId" value="${ BeneficiaryType.getId(BeneficiaryType.OWN_BANK) }" />
	<c:set var="otherBankId" value="${ BeneficiaryType.getId(BeneficiaryType.OTHER_BANK) }" />

	<c:choose>
		<c:when test="${ actionType == 0 }">
			<c:set var="title" value="Fund Transfer" />
			<c:set var="buttonText" value="Initiate fund transfer" />
		</c:when>
		<c:when test="${ actionType == 1 }">
			<c:set var="title" value="Fund Transfer (Step 1)" />
			<c:set var="buttonText" value="Proceed" />
		</c:when>
		<c:when test="${ actionType == 2 }">
			<c:set var="title" value="Fund Transfer (Step 2)" />
			<c:set var="buttonText" value="Proceed" />
		</c:when>
		<c:when test="${ actionType == 3 }">
			<c:set var="title" value="Confirm Transaction details" />
			<c:set var="buttonText" value="Submit" />
		</c:when>
	</c:choose>

	<jsp:include page="/jsp/customer/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<h1>${ title }</h1>
			
			<form action="/bank-app/customer/fund-transfer" method="post" id="form">
				<input name="action-type" value="${ actionType }" class="hidden" required>
				
				<div class="${ actionType != 0 ? 'hidden' : '' }">
					<label>Transaction password:</label>
					<input type="password" name="transaction-password"
						value="${ transactionPassword }" placeholder="transaction password" required>
				</div>
				
				<div class="${ actionType != 1 ? 'hidden' : '' }">
					<label>Select transaction type:</label>
					<select name="transaction-type" required>
						<option value="-1" selected disabled hidden>select type</option>
						<option value='${ neftId }' ${ transactionTypeId == neftId ? "selected" : "" }>
							NEFT
						</option>
						<option value='${ impsId }' ${ transactionTypeId == impsId ? "selected" : "" }>
							IMPS
						</option>
						<option value='${ rtgsId }' ${ transactionTypeId == rtgsId ? "selected" : "" }>
							RTGS
						</option>
					</select>
					
					<label>Transfer type</label>
					<select name="beneficiary-type" required>
						<option value="-1" hidden disabled selected>select type</option>
						<option value='${ ownBankId }' ${ beneficiaryTypeId == ownBankId ? "selected" : "" }>Own Bank</option>
						<option value='${ otherBankId }' ${ beneficiaryTypeId == otherBankId ? "selected" : "" }>Other Bank</option>
					</select>
				</div>
				
				<div class="${ actionType != 2 ? 'hidden' : '' }">
					<label>Select beneficiary</label>
					
					<div class="beneficiary-container">
						<c:forEach items="${ beneficiaries }" var="beneficiary">
							
							<div class="beneficiary-group">
								<div>
									<input type="radio" name="selected-beneficiary"
										value="${ beneficiary.getId() }" ${ selectedBeneficiary.getId() == beneficiary.getId() ? "checked=true" : "" }>
								</div>
								<div class="details">
									<p>Name: ${ beneficiary.getName() }</p>
									
									<c:if test="${ beneficiary.getNickName().length() > 0 }">
										<p>Nick Name: ${ beneficiary.getNickName() }</p>
									</c:if>
									
									<p><strong>A/C No: ${ beneficiary.getAccountNo() }</strong></p>	
								</div>
							</div>
						</c:forEach>
					</div>
					
					<label>Select Account:</label>
					<select name="selected-account">
						<option value="-1" selected disabled hidden>select account</option>
						
						<c:if test="${ activeAccounts.get(RegularAccountType.SAVINGS) != null }">
							<c:forEach items="${ activeAccounts.get(RegularAccountType.SAVINGS) }" var="accountNo">
								<option value="${ accountNo }" ${ selectedAccount.getAccountNo() == accountNo ? "selected" : "" } >
									${ accountNo } (Savings)
								</option>
							</c:forEach>
						</c:if>
						
						<c:if test="${ activeAccounts.get(RegularAccountType.CURRENT) != null }">
							<c:set var="currentAccountNo" value="${ activeAccounts.get(RegularAccountType.CURRENT) }" />
							
							<option value="${ currentAccountNo }" ${ selectedAccount.getAccountNo() == currentAccountNo ? "selected" : "" }>
								${ currentAccountNo } (Current)
							</option>
						</c:if>
						
					</select>
					
					<label>Enter amount:</label>
					<input type="number" name="amount" value="${ amount }"
						placeholder="amount to transfer">
					
					<label>Description (optional):</label>
					<input type="text" name="description" 
						placeholder="Remarks" value="${ description }">
				</div>
				
				<c:if test="${ actionType == 3 }">
					<section>
						<div>
							<h3>Transaction Details:</h3>
							<p>Transaction Type: <strong>${ TransactionType.getType(transactionTypeId).toString() }</strong></p>
							<p>Transfer Type: <strong>${ beneficiaryTypeId == ownBankId ? "Own" : "Other" } Bank Transfer</strong></p>
						</div>
						<div>
							<h3>From:</h3>
							<p>A/C No: ${ selectedAccount.getAccountNo() }</p>
							<p>A/C Type: ${ RegularAccountType.getType(selectedAccount.getTypeId()) }</p>
						</div>
						<div>
							<h3>To:</h3>
							<p>A/C No: ${ selectedBeneficiary.getAccountNo() }</p>
							<p>Beneficiary Name: ${ selectedBeneficiary.getName() }
														
							<c:if test="${ beneficiaryTypeId == otherBankId }">
								<p>Bank Name: ${ AppCache.getIntegratedBank(selectedBeneficiary.getBankId()).getName() }</p>
							</c:if>							
						</div>
						<div>
							<h3>Fund transfer details:</h3>
							<p>Amount: ${ String.format("%.2f", amount) }</p>
							<p>Description: ${ description.length() > 0 ? description : "Nil" }</p>
						</div>
					</section>
				</c:if>
				
				<button>${ buttonText }</button>
				<a class="button secondary" href="/bank-app/customer/dashboard?msg=transaction-cancelled&status=warning">
					Cancel
				</a>
				
			</form>
			
		</div>
	</main>
</body>
</html>