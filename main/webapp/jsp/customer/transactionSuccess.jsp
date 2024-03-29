<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
	import="constant.TransactionType"    
	import = "util.Util"  
%>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Transaction successfull" />
	</jsp:include>
</head>
<body>
	<jsp:include page="/jsp/customer/components/navbar.jsp" />
	
	<main class="container">
		<div class="wrapper">
			<h1 style="color: green;">Transaction Successfull</h1>
			<div>
				<h3>Transaction Details:</h3>
				<p>Id: ${ transaction.getId() }</p>
				<p>Type: ${ TransactionType.getType(transaction.getType()) }</p>
				<p>From A/C: ${ transaction.getFromAccountNo() }</p>
				<p>To A/C: ${ transaction.getToAccountNo() }</p>
				<p>Amount: ${ transaction.getAmount() }</p>
				<p>Date: ${ transaction.getDate() }</p>
				<p>Time: ${ Util.getFormattedTime(transaction.getTime()) }</p>
				<p>Description: ${ transaction.getDescription() }</p>				
			</div>
		</div>
	</main>
</body>
</html>