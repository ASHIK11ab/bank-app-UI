<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<label>${ param.labelName }</label>
<select name="${ param.name }" required>
	<option value="-1" selected disabled hidden>${ param.placeholderOptionText }</option>
	<c:forEach items="${ requestScope.values }" var="value">
	
		<c:if test='${ param.displayId }'>
			<option value="${ value.getId() }" ${ selectedId == value.getId() ? "selected" : ""}>(${ value.getId()}) ${ value.getName() }</option>
		</c:if>
		
		<c:if test='${ !param.displayId }'>		
			<option value="${ value.getId() }" ${ selectedId == value.getId() ? "selected" : ""}>${ value.getName() }</option>
		</c:if>
	</c:forEach>
</select>