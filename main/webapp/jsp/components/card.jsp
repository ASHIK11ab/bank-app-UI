<%@ page import="constant.DebitCardType" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<p>Card No: ${ card.getCardNo() }</p>
<p>Card Type: ${ DebitCardType.getName(card.getTypeId()) }</p>
<p>Valid From: ${ card.getValidFromDate() }</p>
<p>Expiry Date: ${ card.getExpiryDate() }</p>
<p>Active: <c:out value='${ card.getIsActive() ? "Yes" : "No" }'/></p>