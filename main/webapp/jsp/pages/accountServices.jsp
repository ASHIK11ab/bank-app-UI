<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
	import="model.account.*"    
%>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Accounts | Services" />
	</jsp:include>
</head>
<body>
	<jsp:include page="/jsp/components/generalNavbar.jsp" />
	
	<main class="container">
		<h1>Account Services</h1>
		<p>At <span class="bank-name">Your Bank</span> you can avail the services of the following accounts:</p>
		<ul>
			<li>
				<a href="#savings-acount-section">Savings Account</a>
			</li>
			<li>
				<a href="#current-account-section">Current Account</a>
			</li>
		</ul>
				
		<section id="savings-account-section">
			<h2>Savings Account</h2>
			<p>Why Savings Account ?</p>
			<ol>
				<li>
					Savings account is best for people who plan to invest their money.
				</li>
				<li>
					You get intrest for your money.
				</li>
				<li>
					Get access to our Card & ATM & Net Banking services
				</li>
			</ol>
		</section>
		
		<section id="current-account-section">
			<h2>Current Account</h2>
			<p>Why Current Account ?</p>
			<ol>
				<li>
					Business people have a current account.
				</li>
				<li>
					No daily limit on transaction.
				</li>
				<li>
					Get access to our Card & ATM & Net Banking services
				</li>
			</ol>
		</section>
		
		<h3>Savings vs Current account comparison</h3>
		<table>
			<tr>
				<th>Name</th>
				<th>Intrest Rate (p.a)</th>
				<th>Minimum Balance</th>
				<th>Daily Net Banking transaction limit</th>
			</tr>
			<tr>
				<td>Savings Account</td>
				<td>${ String.format("%.2f", SavingsAccount.getIntrestRate()*100) } %</td>
				<td>${ SavingsAccount.getMinimumBalance() }</td>
				<td>${ SavingsAccount.getDailyLimit() }</td>
			</tr>
			<tr>
				<td>Current Account</td>
				<td>Nil</td>
				<td>${ CurrentAccount.getMinimumBalance() }</td>
				<td>No limit on transaction amount</td>
			</tr>
		</table>
	</main>
	
	<jsp:include page="/jsp/components/footer.jsp" />
</body>
</html>