<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
	import="constant.*"
	import="model.account.DepositAccount"
	import="java.time.LocalDate" 
	import="cache.AppCache"
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="create deposit"/>
	</jsp:include>
</head>
<body>
	<c:set var="fdIntrest" value="${ DepositAccount.getTypeIntrestRate(DepositAccountType.FD) }" />
	<c:set var="rdIntrest" value="${ DepositAccount.getTypeIntrestRate(DepositAccountType.RD) }" />
	<c:set var="userType" value="${ Role.getName(sessionScope.role) }" />

	<jsp:include page="/jsp/${ userType }/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<h1><c:out value="${ actionType == 0 ? 'Create Deposit:' : 'Confirm details:' }" /></h1>
			
			<!-- Display deposit details during creation -->
			<c:if test="${ actionType == 0 }">
				<h3>Intrest Rates:</h3>
				<p><span class="bold">Fixed Deposit</span> ${ Math.round(fdIntrest * 100) } % (p.a)</p>
				<p><span class="bold">Recurring Deposit</span> ${ Math.round(rdIntrest * 100) } % (p.a)</p>
			</c:if>
			
			
			<form action="/bank-app/${ userType }/deposit/create" method="post">
				
				<input name="action-type" value="${ actionType }" class="hidden">
				
				<!-- Hide form details when showing confirmation page -->
				<div class="${ actionType == 1 ? 'hidden': '' }">
					<label>Select Deposit Type</label>
					<select name="deposit-type" required id="deposit-type">
						<option value="-1" selected hidden disabled>select type</option>
						
						<option value="${ DepositAccountType.getId(DepositAccountType.FD) }" 
							${ depositType == DepositAccountType.getId(DepositAccountType.FD) ? 'selected' : '' }>Fixed Deposit (FD)</option>
						
						<option value="${ DepositAccountType.getId(DepositAccountType.RD) }"
							${ depositType == DepositAccountType.getId(DepositAccountType.RD) ? 'selected' : '' }>Recurring Deposit (RD)</option>
					
					</select>
					
					<c:choose>
						<c:when test="${ sessionScope.role == Role.EMPLOYEE }">
							<label>Debit From Account No</label>
							<input type="number" placeholder="A/C no to debit amount from" name="debit-from-account-no" 
								value="${ debitFromAccountNo }" required>
							
							<label>Payout Account No</label>
							<input type="number" placeholder="A/C no to credit amount on closure" name="payout-account-no"
								value="${ payoutAccountNo }" required>
						</c:when>
						
						<c:when test="${ sessionScope.role == Role.CUSTOMER }">
							<c:set var="values" value="${ branches }" scope="request" />
							<c:set var="selectedId" value="${ branchId }" scope="request" />
														
							<jsp:include page="/jsp/components/genericDropdown.jsp">
								<jsp:param name="labelName" value="Select Branch:" />
								<jsp:param name="name" value="branch" />
								<jsp:param name="placeholderOptionText" value="Select" />
								<jsp:param name="displayId" value="${ false }" />
							</jsp:include>
							
							<!-- Variables are accessed by the account dropdown component. -->
							<c:set var="savingsAccounts" value="${ activeAccounts.get(RegularAccountType.SAVINGS) }" scope="request" />
							<c:set var="currentAccount" value="${ activeAccounts.get(RegularAccountType.CURRENT) }" scope="request" />
							
							<!-- Incase of displaying confirmation, set previously selected account no's, so that
								the selected account no value is preserved in the hidden form.
							 -->
							<c:set var="selectedAccountNo" value="${ debitFromAccountNo }" scope="request" />
							
							<jsp:include page="/jsp/components/genericAccountDropdown.jsp">
								<jsp:param name="labelName" value="Select Debit From account:" />
								<jsp:param name="name" value="debit-from-account-no" />
								<jsp:param name="category" value="${ AccountCategory.REGULAR }" />
							</jsp:include>
							
							<c:set var="selectedAccountNo" value="${ payoutAccountNo }" scope="request" />
														 
							<jsp:include page="/jsp/components/genericAccountDropdown.jsp">
								<jsp:param name="labelName" value="Select Payout Account:" />
								<jsp:param name="name" value="payout-account-no" />
								<jsp:param name="category" value="${ AccountCategory.REGULAR }" />
							</jsp:include>
						</c:when>
					</c:choose>
						
					<label>Deposit duration in months (2 months to 20 months):</label>
					<input type="number" placeholder="duration in months (minimum 2 months)" name="tenure-months" 
						value="${ tenureMonths }" required>	
						
					<div id="fd-only" class='hidden'>
						<label>Amount to deposit (Minimum amount 1000):</label>
						<input type="number" placeholder="Amount to deposit (minimum 1000 rupees)" name="fd-amount"
						 value="${ amount }">
					</div>
					
					<div id="rd-only" class='hidden'>
						<label>Monthly installment (Minimum amount 1000):</label>
						<input type="number" placeholder="Monthly installment amount (minimum 1000 rupees)" name="rd-amount"
							 value="${ amount }">
						
						<label>Monthly installment date (Between 1 to 15 of every month):</label>
						<input type="number" name="recurring-date" placeholder="Date to debit amount for RD account"
							value="${ recurringDate }">
					</div>
					<button>Proceed to create deposit</button>
				</div>
				
				<!-- Display details for confirmation, only display customer details when
					employee creates a deposit.
				 -->
				<c:if test="${ actionType == 1 }">
				
					<c:if test="${ role == Role.EMPLOYEE }">
						<h3>Customer Details:</h3>
						<p>Customer Id: ${ debitFromAccount.getCustomerId() }</p>
						<p>Customer Name: ${ debitFromAccount.getCustomerName() }</p>
					</c:if>
					
					<c:if test="${ role == Role.CUSTOMER }">
						<p>This deposit will be created in <strong>${ AppCache.getBranch(branchId).getName() }</strong> branch.
					</c:if>
						
					<h3>Debit from account Details:</h3>
					<p>A/C No: ${ debitFromAccount.getAccountNo() }</p>
					<p>Branch: ${ debitFromAccount.getBranchName() }</p>
					
					<h3>Payout account Details:</h3>
					<p>A/C No: ${ payoutAccount.getAccountNo() }</p>
					<p>Branch Name: ${ payoutAccount.getBranchName() }</p>
					
					<h3>Deposit details:</h3>
					<p>Deposit type: ${ DepositAccountType.getName(depositType) }</p>
					<p>Intrest Rate (% p.a): ${ Math.round(DepositAccount.getTypeIntrestRate(DepositAccountType.getType(depositType)) * 100) } %</p>
					<p>Period (in months): ${ requestScope.tenureMonths }</p>
					
					<c:choose>
						<c:when test="${ DepositAccountType.getType(depositType) == DepositAccountType.FD }">
							<p>Deposit amount: ${ amount }</p>
							<p><strong>Maturity value: ${ String.format("%.2f", (amount * fdIntrest * tenureMonths) + amount) }</strong></p>
							<p>Maturity Date: ${ LocalDate.now().plusMonths(tenureMonths) }</p>
							<p style="color: red; font-weight: 600;">Note:</p>
							<p>The maturity value is only applicable subject to the condition that the deposit is only closed on maturity.</p>
						</c:when>
						
						<c:when test="${ DepositAccountType.getType(depositType) == DepositAccountType.RD }">
							<p>Monthly installment amount: ${ amount }</p>
							<p>Monthly installment debit date: ${ recurringDate } th day of every month</p>
							<p><strong>Maturity value: ${ String.format("%.2f", ((amount * ((tenureMonths * (tenureMonths + 1)) / 2) * rdIntrest) + amount)) }</strong></p>
							<p>Maturity Date: ${ LocalDate.now().plusMonths(tenureMonths) }</p>
							<p style="color: red; font-weight: 600;">Note:</p>
							<p>The maturity value is only applicable subject to the condition that the deposit is only closed on maturity and all the montly installments are paid on date.</p>
						</c:when>
					</c:choose>
					
					<p>Premature closing of deposit will lead to deduction of charges</p>
					
					<button>Agree and create Deposit</button>
					<a class="button secondary" href="/bank-app/employee/deposit/create">Cancel</a>
				</c:if>
			</form>
			
		</div>
	</main>
	
	<!-- Displays the corresponding deposit section when creating deposit. -->
	<c:if test="${ actionType == 0 }">
		<script>
			const depositSelect = document.getElementById("deposit-type");
			const rdSection = document.getElementById("rd-only");
			const fdSection = document.getElementById("fd-only");
			
			depositSelect.onchange = function() {
				if(depositSelect.value == ${ DepositAccountType.getId(DepositAccountType.RD) }) {
					fdSection.classList.add('hidden');
					rdSection.classList.remove('hidden');
				} else {
					rdSection.classList.add('hidden');
					fdSection.classList.remove('hidden');
				}
			}
		</script>
	</c:if>
</body>
</html>