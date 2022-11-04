<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page 
	import="constant.Role"
	%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="view deposit" />
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
					<h1>View Deposit</h1>
					<form action="/bank-app/${ userType }/deposit" method="post">
						<label>Account No:</label>
						
						<c:choose>
						
							<c:when test="${ role == Role.EMPLOYEE }">
								<input type="number" placeholder="Enter account no" 
									name="account-no" id="account-no-input" required>
							</c:when>
							
							<c:when test="${ role == Role.CUSTOMER }">
								<select name="account-no">
									<option value="-1" selected disabled hidden>select account</option>
									<c:forEach items="${ requestScope.customerDeposits }" var="account">
										<option value="${ account.getAccountNo() }">
											( ${ account.getAccountNo() } ) ${ RegularAccountType.getName(account.getTypeId()) }
										</option>
									</c:forEach>
								</select>
							</c:when>
							
						</c:choose>
						

						<button>submit</button>
					</form>
				</section>
			</c:if>
			
			<!-- Display deposit -->
			<c:if test="${ actionType == 1 }">				
				<jsp:include page="/jsp/components/deposit.jsp" />
				
				<!-- Only display account holder details when employee views -->
				<c:if test="${ role == Role.EMPLOYEE }">
					<section>
						<h2>Account holder details:</h2>
						<p>Customer Id: ${ account.getCustomerId() }
						<p>Customer Name: ${ account.getCustomerName() }
					</section>				
				</c:if>
				
				<a class="button" href="/bank-app/${ userType }/deposit/${ account.getAccountNo() }/transaction-history">
					Transaction history
				</a>
				
				<c:if test="${ !account.isClosed() }">
					<a class="button danger" href="/bank-app/${ userType }/deposit/${ account.getAccountNo() }/close">
						close deposit
					</a>
				</c:if>
			</c:if>
		</div>
	</main>
</body>
</html>