<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Deposits | Services" />
	</jsp:include>
</head>
<body>
	<jsp:include page="/jsp/components/generalNavbar.jsp" />
	
	<main class="container">
		<h1>Deposit Services</h1>
		<p><span class="bank-name">Your Bank</span> offers the following deposit schemes:</p>
		<ul>
			<li>
				<a href="#fd-section">Fixed Deposit (FD)</a>
			</li>
			<li>
				<a href="#rd-section">Recurring Deposit (RD)</a>
			</li>
		</ul>
				
		<section id="fd-section">
			<h2>Fixed Deposit (FD)</h2>
			<p>A fixed deposit is a type of deposit in which a user deposits a fixed amount,
			for a fixed period. On maturity the principal amount plus the intrest amount is
			paid back to the user.</p>
			<h3>Why FD ?</h3>
			<ol>
				<li>
					Opportunity of long term investment.
				</li>
				<li>
					Get intrest for your deposited amount.
				</li>
			</ol>
		</section>
		
		<section id="rd-section">
			<h2>Recurring Deposit</h2>
			<p>In a Recurring Deposit (RD) a fixed amount (installment) is paid every month
			during the period of the deposit on or before a the monthly installment date. Intrest is
			calculated on monthly installments and paid on deposit closure. </p>
			
			<h3>Why RD ?</h3>
			<ol>
				<li>
					Higher intrest rates as compared to Fixed Deposits (FD).
				</li>
				<li>
					Long term RD's have higher benefits.
				</li>
			</ol>
		</section>
		
		<h3>FD vs RD comparison</h3>
		<table>
			<tr>
				<th>Name</th>
				<th>Intrest Rate (p.a)</th>
			</tr>
			<tr>
				<td>Fixed Deposit (FD)</td>
				<td>3 %</td>
			</tr>
			<tr>
				<td>Recurring Deposit (RD)</td>
				<td>5 %</td>
			</tr>
		</table>
	</main>
	
	<jsp:include page="/jsp/components/footer.jsp" />
</body>
</html>