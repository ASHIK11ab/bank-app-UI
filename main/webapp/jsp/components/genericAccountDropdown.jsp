<%@ page import="constant.AccountCategory" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- Generic Account dropdown component. -->
<label>${ param.labelName }</label>
<select name="${ param.name }" required>
	<option value="-1" selected disabled hidden>select account</option>
	<c:choose>
		<c:when test="${ param.category == AccountCategory.REGULAR }">
			<c:forEach items="${ savingsAccounts }" var="accountNo">
				<option value="${ accountNo }" ${ selectedAccountNo == accountNo ? "selected" : "" }>
					${ accountNo } (Savings)
				</option>
			</c:forEach>
			
			<c:if test="${ currentAccount != null }">
				<option value="${ currentAccount }" ${ selectedAccountNo == currentAccount ? "selected" : "" }>
					${ currentAccount } (Current)
				</option>
			</c:if>
		</c:when>
		
		<c:when test="${ param.category == AccountCategory.DEPOSIT }">
			<c:forEach items="${ accounts }" var="accountNo">
				<option value="${ accountNo }">
					${ accountNo }
				</option>
			</c:forEach>
		</c:when>
	</c:choose>
</select>