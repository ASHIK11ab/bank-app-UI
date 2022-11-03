<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import = "constant.Role"
	import = "constant.DebitCardType"    
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="View Card" />
	</jsp:include>
</head>
<body>
	<c:set var="role" value="${ sessionScope.role }" />
	<c:set var="userType" value='${ Role.getName(sessionScope.role) }' />
	
	<jsp:include page="/jsp/${ userType }/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<form action="/bank-app/${ userType }/card/" method="post">
				<input name="action-type" value="${ actionType }" class="hidden">
				
				<section class="${ actionType == 0 ? '' : 'hidden'}">
					<h1>View Card</h1>
					
					<c:choose>
					
						<c:when test="${ role == Role.EMPLOYEE  }">
							<label>Card No:</label>
							<input type="number" placeholder="12 Digit card no"
								name="card-no" value="${ cardNo }"  required>
						</c:when>
						
						<c:when test="${ role == Role.CUSTOMER }">
							<select name="card-no">
								<option value="-1" selected disabled hidden>select card</option>
								<c:forEach items="${ requestScope.cards }" var="card">
									<option value="${ card.getCardNo() }"
										${ card.getCardNo() == cardNo ? 'selected' : '' }
										>
										
										( ${ card.getCardNo() } ) ${ DebitCardType.getName(card.getTypeId()) }
									
									</option>
								</c:forEach>
							</select>
						</c:when>
						
					</c:choose>
					
					<button>submit</button>
				</section>
				
				<c:if test="${ actionType == 1 }">
					<section>
						<h1>Card Details:</h1>
						<p>Card No: ${ card.getCardNo() }</p>
						<p>Card Type: ${ DebitCardType.getName(card.getTypeId()) }</p>
						<p>Linked with account: ${ card.getLinkedAccountNo() }</p>
						<p>Card Status: <c:out value='${ card.getIsActive() ? "Active" : "Not active" }'/></p>
						<a class="button" href="/bank-app/${ userType }/card/${ cardNo }/block-unblock">Block / Unblock card</a>
					</section>
				</c:if>
			</form>
		</div>
	</main>
</body>
</html>