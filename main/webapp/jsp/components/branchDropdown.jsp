<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<select name="${ param.name }">
	<option value="-1" selected disabled hidden>Select branch</option>
	<c:forEach items="${ requestScope.branches }" var="branch">
		<option value="${ branch.getId() }">${ branch.getName() }</option>
	</c:forEach>
</select>