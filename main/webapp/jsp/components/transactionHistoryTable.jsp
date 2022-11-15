<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<section>
	<!-- Display transactions -->
	<c:choose>
		<c:when test="${ transactions.size() == 0 }">
			<h2>${ fallbackText }</h2>
		</c:when>
	
		<c:when test="${ transactions.size() > 0 }">
			<table>
			<tr>
				<th>Transaction Id</th>
				<th>Date</th>
				<th>Description</th>
				<th>Amount</th>
				<th>Type</th>
				<th>Balance</th>
			</tr>
		
			<c:forEach items="${ transactions }" var="transaction">
				<tr>
					<td>${ transaction.getId() }</td>
					<td>${ transaction.getDate() }</td>
					<td>${ transaction.getDescription() }</td>
					<td>${ String.format("%.2f", transaction.getAmount()) }</td>
					
					<c:choose>
						<c:when test="${ transaction.getFromAccountNo() == account.getAccountNo() }">
							<td>Debit</td>
							<td>${ String.format("%.2f", transaction.getBalanceBeforeTransaction() - transaction.getAmount()) }
						</c:when>
						<c:when test="${ transaction.getToAccountNo() == account.getAccountNo() }">
							<td>Credit</td>
							<td>${ String.format("%.2f", transaction.getBalanceBeforeTransaction() + transaction.getAmount()) }
							</c:when>
						</c:choose>
					</tr>
				</c:forEach>
			
			</table>
		</c:when>
		
	</c:choose>
</section>