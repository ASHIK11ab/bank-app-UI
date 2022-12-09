<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/jsp/components/htmlHead.jsp">
		<jsp:param name="title" value="Your Bank | Home Page" />
	</jsp:include>
</head>
<body>
	
	<jsp:include page="/jsp/components/generalNavbar.jsp" />
	
	<main class="container">
		<section>
			<h1>Welcome to <span class="bank-name">Your Bank</span></h1>
			<p>YOUR BANK is one of the leading private sector banks. We provide a wide range
			of services to our customers. At YOUR BANK customer satisfaction is our priority and
			we strive to keep it the same way. Being a customer of YOUR BANK you get access to
			the following services.</p>
			<ol>
				<li>24/7 Net Banking facility</li>
				<li>ATM Facility</li>
				<li>Instant Grievance redressal</li>
			</ol>
		</section>
		<section>
			<h2>About <span class="bank-name">Your Bank</span></h2>
			<p> For the past 50 years <span class="bank-name">YOUR BANK</span> has been serving
			the people. <span class="bank-name">YOUR BANK</span> has been a key player in the Digital 
			India initiative. YOUR BANK was started in the year 1972 with an aim to digitally transform
			India.</p>
		</section>
	</main>
	
	
	<jsp:include page="/jsp/components/footer.jsp" />
</body>
</html>