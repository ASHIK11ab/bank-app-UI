<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="constant.Role" %>

<c:choose>
	<c:when test="${ nominee == null }">
		<p>Nominee registered: No</p>
	</c:when>
	
	<c:when test="${ nominee != null }">
		<p>Id: ${ nominee.getId() }</p>
		<p>Name: ${ nominee.getName() }</p>
		<p>Relationship: ${ nominee.getRelationship() }</p>
		
		<c:if test="${ sessionScope.role == Role.EMPLOYEE }">
			<p>Adhaar: ${ nominee.getAdhaar() }</p>
			<p>Phone: ${ nominee.getPhone() }</p>
		</c:if>
	</c:when>
</c:choose>