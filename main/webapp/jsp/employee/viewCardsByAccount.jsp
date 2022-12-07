<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
	import="constant.DebitCardType"    
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
		<jsp:include page="/jsp/components/htmlHead.jsp">
			<jsp:param name="title" value="View Cards by Account no | Employee" />
		</jsp:include>
</head>
<body>
	<jsp:include page="/jsp/employee/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<h1>View Cards By Account No</h1>
			<form action="/bank-app/employee/cards" method="post">
				<label>Account No:</label>
				<input type="number" placeholder="Account no linked with cards" name="account-no" required>
				<button>submit</button>
			</form>
			
			<c:if test="${ actionType == 1 }">
				<section>
					<h2>Account Information:</h2>
					<p>A/C No: ${ account.getAccountNo() }</p>
					<p>Customer Id: ${ account.getCustomerId() }</p>
					<p>Customer Name: ${ account.getCustomerName() }</p>
				</section>
				<section>
					<c:choose>
						<c:when test="${ cards.size() == 0 }">
							<h2>No Debit card exist for account.</h2>
						</c:when>
						
						<c:when test="${ cards.size() > 0 }">
							<h2>Linked Cards:</h2>
							
							<c:forEach items="${ cards }" var="card">
								<div style="margin-top: 1.2rem; border-top: 3px solid #ddd;">
									<p>Card No: ${ card.getCardNo() }</p>
									<p>Card Type: ${ DebitCardType.getName(card.getTypeId()) }</p>
									<a class="button" href="/bank-app/employee/card/${ card.getCardNo() }/view">
										View card
									</a>
								</div>
							</c:forEach>
							
						</c:when>
					</c:choose>
				</section>
			</c:if>
		</div>
	</main>
</body>
</html>