<%@ page import="constant.DebitCardType" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<p>Card No: ${ card.getCardNo() }</p>
<p>Linked A/C: ${ card.getLinkedAccountNo() }</p>
<p>Card Type: ${ DebitCardType.getName(card.getTypeId()) }</p>
<p>Valid From: ${ card.getValidFromDate() }</p>
<p>Expiry Date: ${ card.getExpiryDate() }</p>

<c:if test="${ card.isActivated() }">
	<p>Active: <c:out value='${ card.getIsActive() ? "Yes" : "No" }'/></p>
	<p>Card Activated on: ${ card.getActivatedDate() }</p>
</c:if>
<c:if test="${ !card.isActivated() && !card.isDeactivated() }">
	<p><strong>Card yet to be activated</strong></p>
</c:if>
<c:if test="${ card.isDeactivated() }">
	<p><strong>Card deactivated on: ${ card.getDeactivatedDate() }</strong></p>
</c:if>