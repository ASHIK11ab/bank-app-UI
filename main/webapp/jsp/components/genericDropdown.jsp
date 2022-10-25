<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<label>${ param.labelName }</label>
<select name="${ param.name }">
	<option value="-1" selected disabled hidden>${ param.placeholderOptionText }</option>
	<c:forEach items="${ requestScope.values }" var="value">
		<option value="${ value.getId() }">${ value.getName() }</option>
	</c:forEach>
</select>