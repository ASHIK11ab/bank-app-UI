<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
	import = "constant.DebitCardType"    
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Card Activation Management" />
	</jsp:include>
</head>
<body>
	<jsp:include page="/jsp/employee/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<form action="/bank-app/employee/cards/card/activation-management" method="post">
				<input name="action-type" value="${ requestScope.actionType }" class="hidden">
				
				<section class='${ requestScope.actionType == 1 ? "hidden" : "" }'>
					<h1>Card Activation Management</h1>
					
					<label>Card No:</label>
					<input type="number" placeholder="12 Digit card no"
						name="card-no" value="${ requestScope.card.getCardNo() }"  required>
					
					<label>Activation Type</label>
					<select name="activation-type" required>
						<option value="-1" selected hidden>select type</option>
						<option value="1" ${ requestScope.activationType == 1 ? "selected" : "" }>
							Activate
						</option>
						<option value="0" ${ requestScope.activationType == 0 ? "selected" : "" }>
							Deactivate
						</option>
					</select>
					<button>submit</button>
				</section>
				
				<c:if test="${ requestScope.actionType == 1 }">
					<section>
						<h1>Confirm Card Details:</h1>
						<p>Card No: ${ card.getCardNo() }</p>
						<p>Card Type: ${ DebitCardType.getName(card.getTypeId()) }</p>
						<p>Linked with account: ${ card.getLinkedAccountNo() }</p>
						<p>Card Status: <c:out value='${ card.getIsActive() ? "Active" : "Not active" }'/></p>
						<button>Confirm and Proceed</button>
						<a href="/bank-app/employee/cards/card/activation-management">Cancel</a>
					</section>
				</c:if>
				
			</form>
		</div>
	</main>
</body>
</html>