<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page 
	import="constant.RegularAccountType"
	import="constant.Role"
	%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="view account" />
	</jsp:include>
</head>
<body>
	
	<c:set var="role" value="${ sessionScope.role }" />
	<c:set var="userType" value='${ role == Role.EMPLOYEE ? "employee" : "customer" }' />
	
	<jsp:include page="/jsp/${ userType }/components/navbar.jsp" />	
	
	<main class="container">
		<div class="wrapper">
			<c:if test="${ actionType == 0 }">
				<section>
					<h1>View Account</h1>
					<form action="/bank-app/${ userType }/account/" method="POST">
						<label>Account No:</label>
						
						<c:choose>
						
							<c:when test="${ role == Role.EMPLOYEE }">
								<input type="number" placeholder="Enter account no" 
									name="account-no" id="account-no-input" required>
							</c:when>
							
							<c:when test="${ role == Role.CUSTOMER }">
								<select name="account-no" id="account-no-input">
									<option value="-1" selected disabled hidden>select account</option>
									<c:forEach items="${ savingsAccounts }" var="accountNo">
										<option value="${ accountNo }">
											${ accountNo } (Savings)
										</option>
									</c:forEach>
									
									<c:if test="${ currentAccount != null }">
										<option value="${ currentAccount }">
											${ currentAccount } (Current)
										</option>
									</c:if>
									
								</select>
							</c:when>
							
						</c:choose>
						

						<button>submit</button>
					</form>
				</section>
			</c:if>
			
			<c:if test="${ actionType == 1 }">				
				<jsp:include page="/jsp/components/account.jsp" />
				
				<!-- Only display account holder details when employee views -->
				<c:choose>
					<c:when test="${ role == Role.EMPLOYEE }">
						<section>
							<h2>Account holder details:</h2>
							<p>Customer Id: ${ account.getCustomerId() }
							<p>Customer Name: ${ account.getCustomerName() }
						</section>				
					</c:when>
					
					<c:when test="${ role == Role.CUSTOMER }">
						<h2>Branch Details:</h2>
						<p>Branch Name: ${ account.getBranchName() }</p>
					</c:when>
				
				</c:choose>
				
				<a class="button" href="/bank-app/${ userType }/account/${ account.getAccountNo() }/transaction-history">
					Transaction history
				</a>
				
				<a class="button secondary" href="/bank-app/${ userType }/account/${ account.getAccountNo() }/mini-statement">
					Mini Statement
				</a>
				
				<c:if test="${ role == Role.EMPLOYEE && !account.isClosed() }">
					<a class="button danger" href="/bank-app/employee/account/${ account.getAccountNo() }/close">
						close Account
					</a>
				</c:if>
			</c:if>
		</div>
	</main>
</body>
</html>