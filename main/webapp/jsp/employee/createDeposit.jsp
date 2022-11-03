<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="constant.DepositAccountType" %>
<%@ page import="model.account.DepositAccount" %>
<%@ page import="java.time.LocalDate" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="create deposit"/>
	</jsp:include>
</head>
<body>
	<jsp:include page="/jsp/employee/components/navbar.jsp" />
		
	<main class="container">
		<div class="wrapper">
			<h1><c:out value="${ actionType == 0 ? 'Create Deposit:' : 'Confirm details:' }" /></h1>
			
			<form action="/bank-app/employee/deposit/create" method="post">
				
				<input name="action-type" value="${ actionType }" class="hidden">
				
				<!-- Hide form details when showing confirmation page -->
				<div class="${ actionType == 1 ? 'hidden': '' }">
					<label>Select Deposit Type</label>
					<select name="deposit-type" required id="deposit-type">
						<option value="-1" selected hidden disabled>select type</option>
						
						<option value="${ DepositAccountType.getId(DepositAccountType.FD) }" 
							<c:out value="${ depositType == DepositAccountType.getId(DepositAccountType.FD) ? 'selected' : '' }" /> >Fixed Deposit (FD)</option>
						
						<option value="${ DepositAccountType.getId(DepositAccountType.RD) }"
							<c:out value="${ depositType == DepositAccountType.getId(DepositAccountType.RD) ? 'selected' : '' }" />>Recurring Deposit (RD)</option>
					
					</select>
					
					<label>Debit From Account No</label>
					<input type="number" placeholder="A/C no to debit amount from" name="debit-from-account-no" 
						value="${ debitFromAccountNo }" required>
					
					<label>Payout Account No</label>
					<input type="number" placeholder="A/C no to credit amount on closure" name="payout-account-no"
						value="${ payoutAccountNo }" required>
						
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
				
				<!-- Display details for confirmation -->
				<c:if test="${ requestScope.actionType == 1 }">
											
					<h3>Debit from account Details:</h3>
					<p>A/C No: ${ requestScope.debitFromAccount.getAccountNo() }</p>
					<p>Customer Id: ${ requestScope.debitFromAccount.getCustomerId() }</p>
					<p>Customer Name: ${ requestScope.debitFromAccount.getCustomerName() }</p>
					
					<h3>Payout account Details:</h3>
					<p>A/C No: ${ requestScope.payoutAccount.getAccountNo() }</p>
					<p>Customer Id: ${ requestScope.payoutAccount.getCustomerId() }</p>
					<p>Customer Name: ${ requestScope.payoutAccount.getCustomerName() }</p>
					
					<h3>Deposit details:</h3>
					<p>Deposit type: ${ DepositAccountType.getName(requestScope.depositType) }</p>
					<p>Intrest Rate (% p.a): ${ Math.round(DepositAccount.getTypeIntrestRate(DepositAccountType.getType(depositType)) * 100) } %</p>
					<p>Period (in months): ${ requestScope.tenureMonths }</p>
					
					<c:if test="${ DepositAccountType.getType(depositType) == DepositAccountType.FD }">
						<p>Deposit amount: ${ requestScope.amount }</p>
					</c:if>
					
					<c:if test="${ DepositAccountType.getType(depositType) == DepositAccountType.RD }">
						<p>Monthly installment amount: ${ requestScope.amount }</p>
						<p>Monthly installment debit date: ${ requestScope.recurringDate } th day of every month</p>
					</c:if>
					
					<p>Maturity Date: ${ LocalDate.now().plusMonths(requestScope.tenureMonths) }</p>
				
					<button>Create Deposit</button>
					<a class="button secondary" href="/bank-app/employee/deposit/create">Cancel</a>
				</c:if>
			</form>
			
		</div>
	</main>
	
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
</body>
</html>