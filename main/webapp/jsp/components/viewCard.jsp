<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import = "constant.Role"
	import = "constant.DebitCardType"  
	import = "java.time.LocalDate"  
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
			<c:choose>
				<c:when test="${ actionType == 0 }">
					<form action="/bank-app/${ userType }/card/" method="post">
						<input name="action-type" value="${ actionType }" class="hidden">
						
						<section>
							<h1>View Card</h1>
							
							<c:choose>
							
								<c:when test="${ role == Role.EMPLOYEE  }">
									<label>Card No:</label>
									<input type="number" placeholder="12 Digit card no"
										name="card-no" required>
								</c:when>
							
								<c:when test="${ role == Role.CUSTOMER }">
									<c:choose>
										<c:when test="${ cards.size() > 0 }">
											<select name="card-no" required>
												<option value="-1" selected disabled hidden>select card</option>
												<c:forEach items="${ cards }" var="card">
													<option value="${ card.getCardNo() }">
														( ${ card.getCardNo() } ) ${ DebitCardType.getName(card.getTypeId()) }
													</option>
												</c:forEach>
											</select>
										</c:when>
										
										<c:when test="${ cards.size() == 0 }">
											<h1>No cards found</h1>
										</c:when>
									</c:choose>
								</c:when>
							
							</c:choose>
						
							<button>submit</button>
						</section>
					</form>
				</c:when>
					
				<c:when test="${ actionType == 1 }">
					<section>
						<c:if test="${ card.isDeactivated() }">
							<h2 style="color: red">Card Deactivated</h2>
						</c:if>
						
						<h1>Card Details:</h1>
						<jsp:include page="/jsp/components/card.jsp" />
						
						<c:if test="${ role == Role.CUSTOMER && !card.isActivated() && !LocalDate.now().isBefore(card.getValidFromDate()) }">
							<a class="button" href="/bank-app/${ userType }/card/${ cardNo }/activate">Activate card</a>
						</c:if>
						
						<c:if test="${ card.isActivated() && !card.isDeactivated() }">
							<a class="button secondary" href="/bank-app/${ userType }/card/${ cardNo }/block-unblock">Block / Unblock card</a>
						</c:if>
					</section>
				</c:when>
			</c:choose>
		</div>
	</main>
</body>
</html>